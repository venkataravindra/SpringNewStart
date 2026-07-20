package com.dbs.edoc.notification.services.notification.ews;

import com.dbs.edoc.crypto.Crypter;
import com.dbs.edoc.crypto.CryptoUtilException;
import com.dbs.edoc.crypto.impl.DefaultCrypter;
import com.dbs.edoc.notification.error.ExchangeMessageException;
import com.dbs.edoc.notification.services.notification.Mail;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Abstract base service for sending/saving emails via Microsoft Graph API.
 *
 * <p>Replaces the former EWS-based implementation (ews-java-api 2.0).
 * The Microsoft Graph dependency required:
 * <pre>
 *   &lt;dependency&gt;
 *     &lt;groupId&gt;com.microsoft.graph&lt;/groupId&gt;
 *     &lt;artifactId&gt;microsoft-graph&lt;/artifactId&gt;
 *     &lt;version&gt;5.74.0&lt;/version&gt;
 *   &lt;/dependency&gt;
 * </pre>
 *
 * <p>Concrete subclasses must implement:
 * <ul>
 *   <li>{@link #initializeGraphClient(String, String, String)} — build and store
 *       a {@link GraphServiceClient} authenticated for the given user.</li>
 *   <li>{@link #getGraphClient()} — return the stored {@link GraphServiceClient}.</li>
 * </ul>
 */
public abstract class MailExchangeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailExchangeService.class);
    private final Crypter crypter = DefaultCrypter.getAesInstance();

    /**
     * Initializes the Microsoft Graph client authenticated as the given user.
     *
     * <p>Replaces {@code initializeExchangeService(username, password, domain)}.
     * Implementations typically use {@code UsernamePasswordCredential} (ROPC flow)
     * from {@code azure-identity} to obtain an OAuth2 token for Graph.
     *
     * @param username the sender's UPN / email address
     * @param password the plain-text (decrypted) password
     * @param domain   the Azure AD tenant ID or domain (e.g. {@code contoso.onmicrosoft.com})
     * @throws ExchangeMessageException if authentication or client initialisation fails
     */
    abstract void initializeGraphClient(String username, String password, String domain)
            throws ExchangeMessageException;

    /**
     * Returns the authenticated {@link GraphServiceClient} created by
     * {@link #initializeGraphClient}.
     *
     * <p>Replaces {@code getExchangeService()}.
     *
     * @return the live Graph client
     */
    abstract GraphServiceClient getGraphClient();

    /**
     * Saves an email to the Sent Items folder of the sender's mailbox using
     * the Microsoft Graph API.
     *
     * <p>Preserves the exact same external signature as the EWS implementation so
     * that all existing callers continue to work without modification.
     *
     * <p>EWS {@code ExtendedPropertyDefinition(3591, MapiPropertyType.Integer)} was used
     * to set PR_MESSAGE_FLAGS (mark the message as read). The Graph API exposes this
     * natively via {@code message.setIsRead(true)}, which is set here directly.
     *
     * @param senderEmail  the From address / mailbox owner UPN
     * @param emailPwdPair list where index 0 = encrypted password, index 1 = domain/tenant
     * @param mail         the {@link Mail} DTO carrying subject, body, recipients
     * @param attachment   optional raw attachment bytes (may be {@code null})
     * @param fileName     optional attachment file name (may be blank/null)
     * @throws ExchangeMessageException if any Graph API or initialisation call fails
     * @throws CryptoUtilException      if password decryption fails
     */
    public void saveMailToOutbox(String senderEmail, List<String> emailPwdPair,
                                 Mail mail, byte[] attachment, String fileName)
            throws ExchangeMessageException, CryptoUtilException {

        if (emailPwdPair == null) {
            LOGGER.warn("No Credentials information found for [{}]. Can not save the email in to Sent Items",
                    senderEmail);
            return;
        }

        LOGGER.info("Domain and Password Pair found for [{}]", senderEmail);
        String safePwdKey = getPwdForEmail(emailPwdPair.get(0).trim());

        try {
            initializeGraphClient(senderEmail.trim(), safePwdKey, emailPwdPair.get(1).trim());
        } catch (Exception em) {
            LOGGER.error("Exception while initializing graph client for [{}]", senderEmail);
            throw new ExchangeMessageException("Exception while initializing exchange service", em);
        }

        try {
            Message message = new Message();

            // ── Recipients ────────────────────────────────────────────────────
            message.setToRecipients(toRecipientList(mail.getTo()));
            message.setCcRecipients(toRecipientList(mail.getCc()));
            message.setBccRecipients(toRecipientList(mail.getBcc()));

            // ── Subject ───────────────────────────────────────────────────────
            message.setSubject(mail.getSubject());

            // ── Body (plain text) ─────────────────────────────────────────────
            ItemBody body = new ItemBody();
            body.setContentType(BodyType.Text);
            body.setContent(mail.getBody());
            message.setBody(body);

            // ── From address ──────────────────────────────────────────────────
            Recipient from = new Recipient();
            EmailAddress fromAddress = new EmailAddress();
            fromAddress.setAddress(senderEmail);
            from.setEmailAddress(fromAddress);
            message.setFrom(from);

            // ── Mark as read (replaces EWS ExtendedPropertyDefinition PR_MESSAGE_FLAGS = 1) ──
            message.setIsRead(true);

            // ── Attachment ────────────────────────────────────────────────────
            if (attachment != null && !StringUtils.isBlank(fileName)) {
                FileAttachment fileAttachment = new FileAttachment();
                fileAttachment.setName(fileName);
                fileAttachment.setContentBytes(Base64.getEncoder().encode(attachment));
                List<Attachment> attachments = new ArrayList<>();
                attachments.add(fileAttachment);
                message.setAttachments(attachments);
            }

            // ── Save directly to Sent Items (no actual send) ──────────────────
            LOGGER.info("Attempting to save Email [{}] to the email box of [{}]",
                    mail.getSubject(), senderEmail);

            getGraphClient()
                    .users(senderEmail.trim())
                    .mailFolders("sentitems")
                    .messages()
                    .post(message);

            LOGGER.info("Mail [{}] Saved in the Sent items Folder of [{}]",
                    mail.getSubject(), senderEmail);

        } catch (Exception e) {
            LOGGER.info("Error while saving to Sent items for [{}]", mail.getSubject());
            LOGGER.error(" Details of failure : ", e);
            throw new ExchangeMessageException(
                    "Error occurred while saving the message to Sent Items of ["
                            + mail.getSubject() + "] ", e);
        }
    }

    /**
     * Decrypts the stored encrypted password using AES.
     *
     * <p>Unchanged from the EWS implementation — the crypto layer is independent
     * of the mail transport.
     *
     * @param password the encrypted password string
     * @return the plain-text password
     * @throws CryptoUtilException if decryption fails
     */
    private String getPwdForEmail(String password) throws CryptoUtilException {
        return crypter.decrypt(password);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Converts a list of plain email address strings into a
     * {@code List<Recipient>} as required by the Graph API models.
     *
     * @param addresses list of address strings (may be {@code null})
     * @return non-null list of {@link Recipient} objects
     */
    private List<Recipient> toRecipientList(List<String> addresses) {
        if (addresses == null) {
            return new ArrayList<>();
        }
        List<Recipient> recipients = new ArrayList<>();
        for (String addr : addresses) {
            Recipient recipient = new Recipient();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.setAddress(addr);
            recipient.setEmailAddress(emailAddress);
            recipients.add(recipient);
        }
        return recipients;
    }
}
