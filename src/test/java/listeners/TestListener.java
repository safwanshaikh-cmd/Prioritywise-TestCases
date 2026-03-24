package listeners;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import factory.DriverFactory;
import utils.ExtentManager;
import utils.ScreenshotUtils;

/**
 * Test listener for reporting and screenshots.
 */
public class TestListener implements ITestListener {

	private static final Logger LOGGER = Logger.getLogger(TestListener.class.getName());
	private static final ExtentReports extent = ExtentManager.getInstance();
	private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	public static void logInfo(String message) {
		ExtentTest currentTest = test.get();
		if (currentTest != null && message != null && !message.isBlank()) {
			currentTest.info(message);
		}
	}

	public static void logMessages(String title, List<String> messages) {
		ExtentTest currentTest = test.get();
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
	public void onTestStart(ITestResult result) {
		ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
		test.set(extentTest);
		LOGGER.info("STARTED: " + result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.get().pass("Test Passed");
		LOGGER.info("PASSED: " + result.getMethod().getMethodName());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		LOGGER.severe("FAILED: " + result.getMethod().getMethodName());
		test.get().fail(result.getThrowable());

		try {
			String path = ScreenshotUtils.capture(DriverFactory.getDriver(), result.getMethod().getMethodName());
			if (!path.isEmpty()) {
				test.get().addScreenCaptureFromPath(path);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Screenshot attach failed: {0}", e.getMessage());
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		test.get().skip("Test Skipped");
		LOGGER.warning("SKIPPED: " + result.getMethod().getMethodName());
	}

	@Override
	public void onStart(ITestContext context) {
		LOGGER.info("===== Test Execution Started =====");
	}

	@Override
	public void onFinish(ITestContext context) {
		extent.flush();
		LOGGER.info("===== Test Execution Completed =====");
	}
}
