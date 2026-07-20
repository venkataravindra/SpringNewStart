package com.dbs.edoc.notification.services.notification.ews;

import com.dbs.edoc.config.DynamicIntProperty;
import com.dbs.edoc.config.DynamicLongProperty;
import com.dbs.edoc.config.DynamicStringProperty;
import com.dbs.edoc.notification.error.ExchangeMessageException;
import com.dbs.edoc.notification.util.ProxyRegistry;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Concrete implementation of {@link MailExchangeService} for Microsoft 365 cloud mailboxes.
 *
 * <p>Migrated from EWS ({@code ews-java-api 2.0}) + ADAL4J to the
 * Microsoft Graph SDK ({@code microsoft-graph 5.74.0}) + MSAL4J.
 *
 * <p>Migration summary:
 * <ul>
 *   <li>{@code ExchangeService}       → {@code GraphServiceClient}</li>
 *   <li>{@code adal4j AuthenticationContext} → {@code msal4j PublicClientApplication} (ROPC flow)</li>
 *   <li>EWS SOAP URL / WebProxy / WebCredentials → removed (Graph uses REST + OAuth2)</li>
 *   <li>Token cache, proxy registration, and all {@code DynamicProperty} fields are preserved.</li>
 * </ul>
 */
@Service
public class CloudMailExchangeService extends MailExchangeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudMailExchangeService.class);

    // ── Instance fields ───────────────────────────────────────────────────────

    /**
     * Replaces {@code ExchangeService}.
     * Holds the per-request authenticated Graph client. Re-created each time
     * {@link #initializeGraphClient} is called (once per send operation).
     */
    private GraphServiceClient graphClient;

    /**
     * Preserved unchanged — still required by MSAL4J's
     * {@link PublicClientApplication} to execute the async token future.
     */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // ── Dynamic configuration properties (ALL preserved without deviation) ───

    private static final DynamicStringProperty CLIENT_ID        = new DynamicStringProperty("cloud.exchange.client.id", "");
    private static final DynamicStringProperty TENANT_ID        = new DynamicStringProperty("cloud.exchange.tenant.id", "");
    private static final DynamicStringProperty AUTHORITY        = new DynamicStringProperty("cloud.exchange.authority.url", "https://login.microsoftonline.com/");
    private static final DynamicStringProperty RESOURCE_URL     = new DynamicStringProperty("cloud.exchange.resource.url", "https://outlook.office365.com");

    /**
     * {@code EXCHANGE_SERVICE_URL} is preserved for config-key backward compatibility
     * even though the Graph SDK does not use an EWS SOAP URL.
     * It can be repurposed or retired in a follow-up cleanup.
     */
    private static final DynamicStringProperty EXCHANGE_SERVICE_URL   = new DynamicStringProperty("cloud.exchange.service.url", "https://outlook.office365.com/EWS/Exchange.asmx");

    private static final DynamicLongProperty   TOKEN_EXPIRY_MINS      = new DynamicLongProperty("cloud.exchange.token.expiry.mins", 200);

    /**
     * {@code EXCHANGE_SERVICE_TIMEOUT} is preserved for config-key backward compatibility.
     * The Graph SDK (OkHttp) uses JVM proxy/system properties for timeout configuration.
     */
    private static final DynamicIntProperty    EXCHANGE_SERVICE_TIMEOUT = new DynamicIntProperty("cloud.exchange.service.timeout", 30000);

    private static final DynamicLongProperty   OUTLOOK_TIMEOUT        = new DynamicLongProperty("cloud.exchange.outlook.timeout.seconds", 30);

    /** Token cache keyed by sender email — preserved exactly from EWS implementation. */
    private final Cache<String, String> tokens;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Initializes the token cache and proxy settings.
     *
     * <p>EWS-specific setup ({@code WebProxy}, {@code exchangeService.setTimeout},
     * {@code exchangeService.setUrl}) has been removed because the Graph SDK handles
     * HTTP transport internally via OkHttp.
     *
     * <p>Proxy authentication ({@link ProxyRegistry#authenticateProxy()}) is preserved
     * because it registers JVM-level proxy system properties which OkHttp will pick up.
     */
    public CloudMailExchangeService() {

        tokens = CacheBuilder.newBuilder()
                .expireAfterWrite(TOKEN_EXPIRY_MINS.getValue(), TimeUnit.MINUTES)
                .build();

        try {
            ProxyRegistry.authenticateProxy();
            LOGGER.info("Initialized Cloud Mail Exchange Service");
        } catch (Exception ex) {
            LOGGER.error("Error occurred while initializing Cloud Mail Exchange Service", ex);
        }
    }

    // ── Abstract method implementations ──────────────────────────────────────

    /**
     * Initializes a {@link GraphServiceClient} authenticated for {@code email} using
     * the OAuth2 Resource Owner Password Credentials (ROPC) flow via MSAL4J.
     *
     * <p>Replaces {@code initializeExchangeService(email, password, domain)}.
     *
     * <p>Migration notes:
     * <ul>
     *   <li>The {@code firstName} extraction and {@code WebCredentials} constructor
     *       are removed — Graph OAuth2 authenticates with the full UPN ({@code email}).</li>
     *   <li>The token cache check and {@code acquireToken} call are preserved identically.</li>
     *   <li>Instead of injecting the Bearer token into EWS HTTP headers, the token is
     *       supplied to {@link UsernamePasswordCredential} which the Graph SDK uses to
     *       attach the {@code Authorization} header on every request automatically.</li>
     * </ul>
     *
     * @param email    the sender's full UPN (e.g. {@code user@contoso.com})
     * @param password the plain-text (already decrypted) password
     * @param domain   the Azure AD tenant ID (passed from {@code emailPwdPair.get(1)})
     * @throws ExchangeMessageException if token acquisition or client build fails
     */
    @Override
    public void initializeGraphClient(String email, String password, String domain)
            throws ExchangeMessageException {

        ProxyRegistry.registerProxySettings();
        LOGGER.info("Initializing Graph Client with User name [{}]", email);

        final String authKey = tokens.getIfPresent(email);
        if (authKey == null) {
            String accessToken = acquireToken(email, password);
            if (accessToken != null) {
                tokens.put(email, accessToken);
                LOGGER.info("New Access Token acquired and cached for [{}]", email);
            } else {
                LOGGER.error("Could not acquire access Token for Email [{}]", email);
                throw new ExchangeMessageException(
                        "Could not acquire access token for email [" + email + "]", null);
            }
        } else {
            LOGGER.info("Existing Access Token found for [{}]", email);
        }

        // Build the GraphServiceClient using UsernamePasswordCredential (ROPC flow).
        // The credential handles attaching "Authorization: Bearer <token>" on every
        // Graph API call — replacing the manual header injection done in EWS.
        UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
                .clientId(CLIENT_ID.getValue())
                .tenantId(TENANT_ID.getValue())
                .username(email)
                .password(password)
                .build();

        this.graphClient = new GraphServiceClient(credential,
                "https://graph.microsoft.com/.default");

        LOGGER.info("GraphServiceClient initialized successfully for [{}]", email);
    }

    /**
     * Acquires an OAuth2 access token using the ROPC flow via MSAL4J.
     *
     * <p>Replaces the ADAL4J {@code AuthenticationContext.acquireToken()} call.
     *
     * <p>Migration mapping:
     * <pre>
     *   ADAL4J                                  → MSAL4J
     *   ──────────────────────────────────────────────────
     *   new AuthenticationContext(authority,..) → PublicClientApplication.builder(..)
     *   context.setLogPii(true)                 → .logPii(true) on builder
     *   context.acquireToken(resource,clientId,
     *     email, password, null)               → app.acquireToken(
     *                                               UserNamePasswordParameters.builder(
     *                                                 scopes, email, pwd.toCharArray()))
     *   authResult.getAccessToken()             → authResult.accessToken()
     *   authResult.getExpiresOnDate()           → authResult.expiresOnDate()
     * </pre>
     *
     * @param email    the sender's full UPN
     * @param password the plain-text password
     * @return the access token string, or {@code null} if unavailable
     * @throws ExchangeMessageException if MSAL4J throws during token acquisition
     */
    private String acquireToken(String email, String password) throws ExchangeMessageException {
        try {
            String authorityUrl = AUTHORITY.getValue() + TENANT_ID.getValue();
            LOGGER.info("Authority URL [{}]", authorityUrl);

            // MSAL4J uses scopes instead of ADAL4J's resource URL.
            // "https://graph.microsoft.com/.default" requests all app-registered Graph permissions.
            Set<String> scopes = Collections.singleton("https://graph.microsoft.com/.default");

            PublicClientApplication app = PublicClientApplication.builder(CLIENT_ID.getValue())
                    .authority(authorityUrl)
                    .logPii(true)
                    .executorService(executorService)
                    .build();

            Future<IAuthenticationResult> authResultFuture = app.acquireToken(
                    UserNamePasswordParameters.builder(scopes, email, password.toCharArray())
                            .build());

            final IAuthenticationResult authenticationResult =
                    authResultFuture.get(OUTLOOK_TIMEOUT.getValue(), TimeUnit.SECONDS);

            LOGGER.info("AuthenticationResult received : [{}]", authenticationResult);

            if (authenticationResult == null) {
                LOGGER.error(" Unable to fetch AuthenticationResult !! ");
            } else {
                final Date expiresOnDate = authenticationResult.expiresOnDate();
                String accessToken = authenticationResult.accessToken();
                LOGGER.info("Access Token found [{}] for Email [{}]", accessToken, email);
                LOGGER.info("Expiry Date {}", expiresOnDate);
                if (accessToken != null && !accessToken.isEmpty()) {
                    return accessToken;
                } else {
                    LOGGER.error(" Unable to fetch accessToken! ");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred acquiring Token", e);
            throw new ExchangeMessageException("Error occurred acquiring Token", e);
        }
        return null;
    }

    /**
     * Returns the {@link GraphServiceClient} initialized by {@link #initializeGraphClient}.
     *
     * <p>Replaces {@code getExchangeService()}.
     *
     * @return the live authenticated Graph client
     */
    @Override
    public GraphServiceClient getGraphClient() {
        return this.graphClient;
    }
}
