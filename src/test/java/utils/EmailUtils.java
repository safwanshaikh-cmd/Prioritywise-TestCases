package utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;

/**
 * Utility to fetch OTP from an IMAP inbox.
 */
public final class EmailUtils {

	private static final Logger LOGGER = Logger.getLogger(EmailUtils.class.getName());
	private static final String IMAP_HOST = "imap.gmail.com";

	private EmailUtils() {
	}

	public static String getOTPFromEmail(String email, String password) {

		int maxAttempts = 5;
		int waitTime = 5000;

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

	private static String extractText(Message message) {
		try {
			Object content = message.getContent();

			if (content instanceof String) {
				return (String) content;
			}

			if (content instanceof MimeMultipart) {
				MimeMultipart multipart = (MimeMultipart) content;
				StringBuilder builder = new StringBuilder();

				for (int i = 0; i < multipart.getCount(); i++) {
					BodyPart bodyPart = multipart.getBodyPart(i);
					Object part = bodyPart.getContent();

					if (part instanceof String) {
						builder.append(part);
					}
				}

				return builder.toString();
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Content extraction failed: {0}", e.getMessage());
		}

		return "";
	}

	private static String extractOTP(String text) {
		if (text == null) {
			return "";
		}

		Pattern pattern = Pattern.compile("\\b\\d{4,8}\\b");
		Matcher matcher = pattern.matcher(text);
		return matcher.find() ? matcher.group() : "";
	}
}
