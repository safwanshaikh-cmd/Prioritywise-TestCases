package utils;

<<<<<<< HEAD
=======
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
import jakarta.mail.*;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.internet.MimeMultipart;

<<<<<<< HEAD
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to fetch OTP from email inbox (IMAP). Enhanced with retry, regex
 * extraction, and config support.
 */
public final class EmailUtils {

	private static final Logger LOGGER = Logger.getLogger(EmailUtils.class.getName());
	private static final String IMAP_HOST = "imap.gmail.com";

	private EmailUtils() {
	}

	public static String getOTPFromEmail(String email, String password) {

		int maxAttempts = 5;
		int waitTime = 5000; // 5 sec

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {

			try {
				LOGGER.info("Checking email for OTP (Attempt " + attempt + ")");

				Properties props = new Properties();
				props.put("mail.store.protocol", "imaps");

				Session session = Session.getInstance(props);
				Store store = session.getStore("imaps");

				store.connect(IMAP_HOST, email, password);

				Folder inbox = store.getFolder("INBOX");
				inbox.open(Folder.READ_WRITE);

				Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

				for (int i = messages.length - 1; i >= 0; i--) {

					Message msg = messages[i];
					String subject = msg.getSubject();

					if (subject != null && subject.toLowerCase().contains("otp")) {

						String content = extractText(msg);

						String otp = extractOTP(content);

						if (!otp.isEmpty()) {
							LOGGER.info("OTP Found: " + otp);

							msg.setFlag(Flags.Flag.SEEN, true);

							inbox.close(false);
							store.close();

							return otp;
						}
					}
				}

				inbox.close(false);
				store.close();

				Thread.sleep(waitTime);

			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "OTP fetch failed: {0}", e.getMessage());
			}
		}

		LOGGER.severe("OTP not found after retries");
		return "";
	}

	// 🔥 Extract text safely
	private static String extractText(Message message) {
		try {
			Object content = message.getContent();

			if (content instanceof String) {
				return (String) content;
			}

			if (content instanceof MimeMultipart) {
				MimeMultipart mp = (MimeMultipart) content;
				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < mp.getCount(); i++) {
					BodyPart bp = mp.getBodyPart(i);
					Object part = bp.getContent();

					if (part instanceof String) {
						sb.append(part);
					}
				}
				return sb.toString();
			}

		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Content extraction failed: {0}", e.getMessage());
		}

		return "";
	}

	// 🔥 Extract OTP using regex (safer)
	private static String extractOTP(String text) {

		if (text == null)
			return "";

		Pattern pattern = Pattern.compile("\\b\\d{4,8}\\b");
		Matcher matcher = pattern.matcher(text);

		return matcher.find() ? matcher.group() : "";
	}
=======
/**
 * Lightweight helper to read OTP values from an IMAP mailbox.
 *
 * Notes:
 * - Uses IMAPS against gmail's server (imap.gmail.com).
 * - This utility reads unseen messages and searches for a subject containing "OTP".
 * - It preserves the original behavior but adds more robust message content handling
 *   (supports plain text and multipart messages) and structured logging.
 */
public final class EmailUtils {

    private static final Logger LOGGER = Logger.getLogger(EmailUtils.class.getName());
    private static final String IMAP_HOST = "imap.gmail.com"; // preserved

    private EmailUtils() {
        // utility class
    }

    public static String getOTPFromEmail(String email, String password) {

        String otp = "";
        Store store = null;
        Folder inbox = null;

        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);

            store = session.getStore("imaps");

            store.connect(IMAP_HOST, email, password);

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            for (int i = messages.length - 1; i >= 0; i--) {
                Message msg = messages[i];
                String subject = msg.getSubject();

                if (subject != null && subject.contains("OTP")) {
                    String content = extractTextFromMessage(msg);
                    LOGGER.log(Level.FINE, "Email Content: {0}", content);

                    if (content != null) {
                        otp = content.replaceAll("[^0-9]", "");
                    }
                    break;
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to read OTP from email: {0}", e.getMessage());
        } finally {
            try {
                if (inbox != null && inbox.isOpen()) {
                    inbox.close(false);
                }
            } catch (MessagingException me) {
                LOGGER.log(Level.FINE, "Error closing inbox: {0}", me.getMessage());
            }
            try {
                if (store != null && store.isConnected()) {
                    store.close();
                }
            } catch (MessagingException me) {
                LOGGER.log(Level.FINE, "Error closing store: {0}", me.getMessage());
            }
        }

        LOGGER.log(Level.FINE, "Extracted OTP: {0}", otp);
        return otp;
    }

    // Helper to extract text content from a Message (handles multipart)
    private static String extractTextFromMessage(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String) {
                return (String) content;
            }
            if (content instanceof MimeMultipart) {
                MimeMultipart mp = (MimeMultipart) content;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart bp = mp.getBodyPart(i);
                    Object partContent = bp.getContent();
                    if (partContent instanceof String) {
                        sb.append((String) partContent);
                    }
                }
                return sb.toString();
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Unable to extract message content: {0}", e.getMessage());
        }
        return null;
    }
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
}