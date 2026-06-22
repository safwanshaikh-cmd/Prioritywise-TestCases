package listeners;

import java.util.List;
import java.util.logging.Logger;

import org.testng.ITestContext;
import org.testng.ITestListener;

import com.aventstack.extentreports.ExtentTest;

/**
 * Lightweight {@link ITestListener} that handles the bits {@link ExtentReportListener}
 * does not: it forwards page-level log calls (e.g. {@code RegistrationPage}'s
 * use of {@link #logInfo(String)} / {@link #logMessages(String, List)}) to the
 * current ExtentTest so they appear in the HTML report.
 *
 * <p>The ExtentTest lifecycle itself (create / pass / fail / skip) is owned by
 * {@link ExtentReportListener} — this class deliberately does not create or
 * mutate the ExtentTest, otherwise the same test would appear twice in the
 * generated report.
 */
public class TestListener implements ITestListener {

	private static final Logger LOGGER = Logger.getLogger(TestListener.class.getName());

	/**
	 * Log a single line to the current test's HTML report entry. Safe to call
	 * outside a test context — in that case the call is a no-op.
	 */
	public static void logInfo(String message) {
		ExtentTest currentTest = ExtentReportListener.getCurrentTest();
		if (currentTest != null && message != null && !message.isBlank()) {
			currentTest.info(message);
		}
	}

	/**
	 * Log a header + list of messages to the current test's HTML report entry.
	 * If the list is null or empty, a single "{header}: none" line is logged
	 * instead. Safe to call outside a test context.
	 */
	public static void logMessages(String title, List<String> messages) {
		ExtentTest currentTest = ExtentReportListener.getCurrentTest();
		if (currentTest == null) {
			return;
		}

		String header = title == null || title.isBlank() ? "Messages" : title;
		if (messages == null || messages.isEmpty()) {
			currentTest.info(header + ": none");
			return;
		}

		currentTest.info(header);
		for (String message : messages) {
			if (message != null && !message.isBlank()) {
				currentTest.info(message);
			}
		}
	}

	@Override
	public void onStart(ITestContext context) {
		LOGGER.info("===== Test Execution Started =====");
	}

	@Override
	public void onFinish(ITestContext context) {
		LOGGER.info("===== Test Execution Completed =====");
	}
}