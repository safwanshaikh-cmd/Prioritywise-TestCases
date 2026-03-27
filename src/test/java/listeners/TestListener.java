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
		String testName = buildTestName(result);
		ExtentTest extentTest = extent.createTest(testName);
		test.set(extentTest);
		LOGGER.info("STARTED: " + testName);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.get().pass("Test Passed");
		LOGGER.info("PASSED: " + buildTestName(result));
	}

	@Override
	public void onTestFailure(ITestResult result) {
		LOGGER.severe("FAILED: " + buildTestName(result));
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
		if (result.getThrowable() != null) {
			test.get().skip(result.getThrowable());
			LOGGER.log(Level.WARNING, "SKIPPED: {0} | Reason: {1}",
					new Object[] { buildTestName(result), result.getThrowable().getMessage() });
		} else {
			LOGGER.warning("SKIPPED: " + buildTestName(result));
		}
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

	private String buildTestName(ITestResult result) {
		String className = result.getTestClass() == null ? "" : result.getTestClass().getRealClass().getSimpleName();
		String methodName = result.getMethod() == null ? "unknown" : result.getMethod().getMethodName();
		String baseName = className.isBlank() ? methodName : className + "." + methodName;

		Object[] parameters = result.getParameters();
		if ("MasterTest".equals(className) && parameters != null && parameters.length >= 3) {
			String tcId = valueOf(parameters[0]);
			String scenario = valueOf(parameters[2]);
			if (!tcId.isBlank() && !scenario.isBlank()) {
				return baseName + " [" + tcId + " - " + scenario + "]";
			}
			if (!tcId.isBlank()) {
				return baseName + " [" + tcId + "]";
			}
		}

		return baseName;
	}

	private String valueOf(Object value) {
		return value == null ? "" : String.valueOf(value).trim();
	}
}
