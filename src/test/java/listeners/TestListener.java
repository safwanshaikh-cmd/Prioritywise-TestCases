package listeners;

<<<<<<< HEAD
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
 * Test Listener for: - Reporting (Extent) - Logging - Screenshot on failure
 */
public class TestListener implements ITestListener {

	private static final Logger LOGGER = Logger.getLogger(TestListener.class.getName());

	private static ExtentReports extent = ExtentManager.getInstance();

	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
=======
import org.openqa.selenium.WebDriver;
import org.testng.*;
import utils.ExtentManager;
import utils.DriverFactory;
import utils.ScreenshotUtils;

import com.aventstack.extentreports.*;

public class TestListener implements ITestListener {

	static ExtentReports extent = ExtentManager.getInstance();
	static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab

	@Override
	public void onTestStart(ITestResult result) {

		ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
<<<<<<< HEAD
		test.set(extentTest);

		LOGGER.info("STARTED: " + result.getMethod().getMethodName());
=======

		test.set(extentTest);
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		test.get().pass("Test Passed");
<<<<<<< HEAD

		LOGGER.info("PASSED: " + result.getMethod().getMethodName());
=======
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
	}

	@Override
	public void onTestFailure(ITestResult result) {

<<<<<<< HEAD
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
=======
		WebDriver driver = DriverFactory.getDriver();

		String path = ScreenshotUtils.captureScreenshot(driver, result.getMethod().getMethodName());

		test.get().fail(result.getThrowable());

		if (!path.isEmpty())
			test.get().addScreenCaptureFromPath(path);
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
	}

	@Override
	public void onFinish(ITestContext context) {

		extent.flush();
<<<<<<< HEAD

		LOGGER.info("===== Test Execution Completed =====");
=======
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
	}
}