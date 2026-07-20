package com.dbs.edoc.notification.services.notification.ews;

import com.dbs.edoc.config.DynamicBooleanProperty;
import com.dbs.edoc.config.DynamicStringProperty;
import com.dbs.edoc.crypto.utils.ssl.IgnoreSSLCertificates;
import com.dbs.edoc.crypto.utils.ssl.InstallSSLCertificates;
import com.dbs.edoc.notification.error.ExchangeMessageException;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Concrete implementation of {@link MailExchangeService} for on-premise / internal
 * DBS Exchange mailboxes.
 *
 * <p>Migrated from EWS ({@code ews-java-api 2.0}) to the Microsoft Graph SDK
 * ({@code microsoft-graph 5.74.0}).
 *
 * <p>Migration summary per point:
 * <ol>
 *   <li><b>Field:</b> {@code ExchangeService exchangeService} → {@code GraphServiceClient graphClient}.
 *       Not eagerly initialized — built inside {@link #initializeGraphClient}.</li>
 *   <li><b>Properties:</b> All three {@code DynamicProperty} fields preserved without deviation.
 *       {@code EXCHANGE_SERVICE_URL} no longer configures a SOAP endpoint but still drives
 *       the SSL certificate install logic (https prefix check) — unchanged behaviour.</li>
 *   <li><b>Constructor:</b> {@link #installMailExchangeCerts()} call preserved. EWS-only lines
 *       ({@code new ExchangeService(Exchange2010_SP2)}, {@code setUrl}) removed.</li>
 *   <li><b>Methods:</b> {@code initializeExchangeService} → {@link #initializeGraphClient};
 *       {@code getExchangeService} → {@link #getGraphClient};
 *       {@code installMailExchangeCerts} — 100% preserved.</li>
 * </ol>
 */
@Service
public class InternalMailExchangeService extends MailExchangeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalMailExchangeService.class);

    // ── Dynamic configuration properties (ALL preserved without deviation) ───

    /**
     * When {@code true}, skips SSL certificate installation and instructs the JVM
     * to ignore SSL errors entirely via {@link IgnoreSSLCertificates}.
     * Preserved unchanged — still applied to the JVM-level SSL context which
     * OkHttp (Graph SDK's HTTP engine) inherits.
     */
    private static final DynamicBooleanProperty MAIL_EXCHANGE_SSL_IGNORE =
            new DynamicBooleanProperty("internal.exchange.mail.ssl.ignore", false);

    /**
     * Originally the EWS SOAP endpoint URL. Preserved for:
     * <ul>
     *   <li>Config-key backward compatibility — no existing config entries need changing.</li>
     *   <li>The {@code https} prefix check in {@link #installMailExchangeCerts()} which
     *       decides whether to install the SSL certificate — logic unchanged.</li>
     * </ul>
     */
    private static final DynamicStringProperty EXCHANGE_SERVICE_URL =
            new DynamicStringProperty("internal.exchange.service.url",
                    "https://webmail.uat1bank.dbs.com/EWS/Exchange.asmx");

    /**
     * Host and port used to fetch and install the on-premise Exchange SSL certificate
     * into the JVM trust store. Preserved exactly.
     */
    private static final DynamicStringProperty EXCHANGE_SERVICE_HOST_PORT =
            new DynamicStringProperty("internal.exchange.service.host.port",
                    "webmail.uat1bank.dbs.com:443");

    // ── Instance fields ───────────────────────────────────────────────────────

    /**
     * Replaces {@code ExchangeService exchangeService}.
     *
     * <p>Not initialized eagerly (unlike EWS where {@code new ExchangeService(Exchange2010_SP2)}
     * was called in the constructor) because {@link GraphServiceClient} requires credentials
     * at construction time. It is created per-call in {@link #initializeGraphClient}.
     */
    private GraphServiceClient graphClient;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Spring-injected constructor — initializes SSL certificates for the on-premise
     * Exchange host before any mail operations are performed.
     *
     * <p>Migration notes vs the EWS constructor:
     * <ul>
     *   <li>{@link #installMailExchangeCerts()} — <b>preserved exactly</b>. It configures
     *       the JVM-wide SSL trust store ({@code jssecacerts}) and system properties that
     *       OkHttp (used internally by the Graph SDK) picks up automatically.</li>
     *   <li>{@code new ExchangeService(Exchange2010_SP2)} — <b>removed</b>. No EWS client
     *       is needed; the {@link GraphServiceClient} is created per authentication call.</li>
     *   <li>{@code exchangeService.setUrl(new URI(...))} — <b>removed</b>. Graph communicates
     *       via {@code https://graph.microsoft.com} REST endpoints, not a SOAP URL.</li>
     *   <li>Log messages — <b>preserved exactly</b>.</li>
     * </ul>
     */
    @Autowired
    public InternalMailExchangeService() {

        try {
            installMailExchangeCerts();
            LOGGER.info("Mail Exchange Service initialized to [{}]", EXCHANGE_SERVICE_URL.getValue());
        } catch (Exception e) {
            LOGGER.error("Exchange Server could not be initialized to [{}]. Will not be saving outgoing messages for on-premise email accounts",
                    EXCHANGE_SERVICE_URL.getValue());
        }
    }

    // ── Abstract method implementations ──────────────────────────────────────

    /**
     * Initializes a {@link GraphServiceClient} authenticated for the given on-premise
     * user using {@link UsernamePasswordCredential} (ROPC / Resource Owner Password
     * Credentials flow).
     *
     * <p>Replaces {@code initializeExchangeService(username, password, domain)}.
     *
     * <p>Migration mapping:
     * <pre>
     *   EWS                                          Graph
     *   ─────────────────────────────────────────────────────────────────────
     *   String user = username.split("@")[0]    →   full UPN (username) used directly
     *                                               in UsernamePasswordCredentialBuilder;
     *                                               short name extracted and logged for
     *                                               traceability (preserved)
     *   new WebCredentials(user, password, domain) → UsernamePasswordCredentialBuilder
     *                                                 .clientId(...)
     *                                                 .tenantId(domain)   ← Windows domain
     *                                                   or Azure tenant ID passed as-is
     *                                                 .username(username)
     *                                                 .password(password)
     *                                                 .build()
     *   exchangeService.setCredentials(credentials) → new GraphServiceClient(credential, scope)
     * </pre>
     *
     * <p><b>Domain parameter note:</b> For on-premise Exchange accessed via Microsoft Graph,
     * {@code domain} is the Azure AD tenant ID configured for the hybrid environment.
     * The parameter name and contract are unchanged from the parent abstract method —
     * callers pass {@code emailPwdPair.get(1)} exactly as before.
     *
     * @param username the sender's full UPN (e.g. {@code user@dbsbank.com})
     * @param password the plain-text (already decrypted) password
     * @param domain   the Windows domain or Azure AD tenant ID
     * @throws ExchangeMessageException if credential or client build fails
     */
    @Override
    public void initializeGraphClient(String username, String password, String domain)
            throws ExchangeMessageException {

        LOGGER.info("Initializing Graph Client with User name [{}]", username);
        String user = username.split("@")[0];
        LOGGER.info("First name extracted [{}] as Web credentials", user);

        try {
            // Replaces: new WebCredentials(user, password, domain)
            //           exchangeService.setCredentials(credentials)
            // Graph SDK's UsernamePasswordCredential uses the full UPN as username
            // and the domain (Windows domain / Azure tenant) as tenantId.
            UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
                    .clientId(getGraphClientId())
                    .tenantId(domain)
                    .username(username)
                    .password(password)
                    .build();

            this.graphClient = new GraphServiceClient(credential,
                    "https://graph.microsoft.com/.default");

            LOGGER.info("GraphServiceClient initialized successfully for [{}]", username);

        } catch (Exception e) {
            LOGGER.error("Error initializing GraphServiceClient for [{}]", username, e);
            throw new ExchangeMessageException(
                    "Error initializing GraphServiceClient for [" + username + "]", e);
        }
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

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Installs or ignores the SSL certificate for the on-premise Exchange host.
     *
     * <p><b>Preserved 100% without any deviation.</b> This method operates entirely
     * at the JVM SSL layer ({@code SSLContext}, {@code trustStore} system properties),
     * which is independent of EWS vs Graph transport. OkHttp — the HTTP client used
     * internally by the Microsoft Graph SDK — inherits these JVM-level SSL settings,
     * so the certificate installation continues to work correctly after migration.
     *
     * <p>Behaviour:
     * <ul>
     *   <li>If {@code MAIL_EXCHANGE_SSL_IGNORE} is {@code true}: calls
     *       {@link IgnoreSSLCertificates#ignoreSslCertificates()} to disable all SSL
     *       validation globally and returns immediately.</li>
     *   <li>If {@code EXCHANGE_SERVICE_URL} starts with {@code https}: extracts host and
     *       port from {@code EXCHANGE_SERVICE_HOST_PORT}, installs the server certificate
     *       into the JVM default SSL context, and sets {@code javax.net.ssl.trustStore}
     *       system properties.</li>
     * </ul>
     */
    private void installMailExchangeCerts() {
        if (MAIL_EXCHANGE_SSL_IGNORE.get()) {
            LOGGER.warn("Ignoring ssl certificate for: {}", EXCHANGE_SERVICE_URL.get());
            IgnoreSSLCertificates.ignoreSslCertificates();
            return;
        }

        if (EXCHANGE_SERVICE_URL.get().toLowerCase(Locale.ENGLISH).startsWith("https")) {
            LOGGER.info("Installing Certificate from [{}]", EXCHANGE_SERVICE_URL.get());
            final String[] hostPort = EXCHANGE_SERVICE_HOST_PORT.get().split(":");
            InstallSSLCertificates.installSslCertificateOnDefaultContext(
                    hostPort[0], Integer.parseInt(hostPort[1]));

            System.setProperty("javax.net.ssl.trustStore", "jssecacerts");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        }
    }

    /**
     * Returns the Azure AD / Microsoft Identity Platform client ID for this
     * on-premise service principal.
     *
     * <p>This value should be injected via a {@code DynamicStringProperty} or
     * {@code @Value} in production. A dedicated config key
     * {@code internal.exchange.client.id} is recommended, consistent with the
     * {@code cloud.exchange.client.id} key used in {@link CloudMailExchangeService}.
     *
     * <p>Override or externalise this method to avoid hard-coding client IDs.
     *
     * @return the Azure AD application (client) ID
     */
    protected String getGraphClientId() {
        // TODO: replace with DynamicStringProperty("internal.exchange.client.id", "")
        // analogous to CloudMailExchangeService.CLIENT_ID
        return System.getProperty("internal.exchange.client.id", "");
    }
}
