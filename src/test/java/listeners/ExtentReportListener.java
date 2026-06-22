package listeners;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import utils.ConsoleLogSaver;
import utils.ExecutionFolderManager;
import utils.ExtentManager;
import utils.Log4jProxyManager;
import utils.LoggerUtils;
import utils.ScreenshotUtils;

/**
 * TestNG listener that drives the full test-report lifecycle in the same way
 * as the MTNCGModule reference project:
 * <ul>
 *   <li>Per-class, timestamped Extent report file.</li>
 *   <li>Per-test log file at {@code reports/logs/<test>_<timestamp>.log}.</li>
 *   <li>Screenshot capture + attachment on failure.</li>
 *   <li>Suite-level summary at {@code onFinish} covering total / passed /
 *       failed / skipped / pass rate / start time / end time / environment /
 *       browser / report path.</li>
 * </ul>
 *
 * <p>Page-level {@code TestListener.logInfo(...)} calls also flow through
 * here via {@link #getCurrentTest()}.
 */
public class ExtentReportListener implements ITestListener {

	private static final Logger LOGGER = Logger.getLogger(ExtentReportListener.class.getName());
	private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static final String QA_NAME = System.getProperty("user.name", "QA");

	// Captured at onStart, read at onFinish. Plain ThreadLocal<Date> avoids the
	// "unsafe null type conversion" Checker Framework warning that
	// ThreadLocal.withInitial(Date::new) → ThreadLocal<Date> produces (the
	// method-reference supplier infers a @NonNull element type).
	private static final ThreadLocal<Date> suiteStartTime = new ThreadLocal<>();

	@Override
	public void onStart(ITestContext context) {
		suiteStartTime.set(new Date());

		// Install the jul-to-slf4j bridge so java.util.logging.Logger calls
		// in framework classes flow through Log4j2's console pattern.
		Log4jProxyManager.install();

		// Ensure the logs directory exists. No suite-level log file is created —
		// every per-test log file is created in onTestStart via rotateTo().
		ConsoleLogSaver.start();

		LOGGER.info("QA: " + QA_NAME + " | " + getFormattedDate() + " " + getFormattedTime());

		// Get test class name from first test method, e.g. "tests.ConsumerCategoriesTests".
		String className = null;
		ITestNGMethod[] methods = context.getAllTestMethods();
		if (methods != null && methods.length > 0) {
			String fullName = methods[0].getQualifiedName();
			int lastDot = fullName.lastIndexOf('.');
			if (lastDot > 0) {
				int secondLastDot = fullName.lastIndexOf('.', lastDot - 1);
				if (secondLastDot >= 0) {
					className = fullName.substring(secondLastDot + 1, lastDot);
				} else {
					className = fullName.substring(0, lastDot);
				}
			}
		}

		ExtentManager.getInstance(className);

		LoggerUtils.logInfo("========== TEST SUITE STARTED: " + context.getSuite().getName() + " ==========");
		LoggerUtils.logInfo("Reports directory: " + ExecutionFolderManager.getReportsDirectory());
		LoggerUtils.logInfo("Logs directory:    " + ExecutionFolderManager.getLogsDirectory());
		LoggerUtils.logInfo("Screenshots:       " + ExecutionFolderManager.getScreenshotsDirectory());
	}

	@Override
	public void onFinish(ITestContext context) {
		Date startTime = suiteStartTime.get();
		Date endTime = new Date();
		// Defensive fallback: TestNG guarantees onStart runs first, but if a
		// caller invokes onFinish directly the ThreadLocal may be unset.
		if (startTime == null) {
			startTime = endTime;
		}

		int total = context.getAllTestMethods().length;
		int passed = context.getPassedTests().size();
		int failed = context.getFailedTests().size();
		int skipped = context.getSkippedTests().size();
		double passRate = total > 0 ? (passed * 100.0) / total : 0.0;

		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long durationMs = endTime.getTime() - startTime.getTime();
		long durationSeconds = durationMs / 1000;

		LoggerUtils.logInfo("========== TEST SUITE COMPLETED: " + context.getSuite().getName() + " ==========");
		LoggerUtils.logInfo("========== EXECUTION SUMMARY ==========");
		LoggerUtils.logInfo("Total Test Cases    : " + total);
		LoggerUtils.logInfo("Passed Test Cases   : " + passed);
		LoggerUtils.logInfo("Failed Test Cases   : " + failed);
		LoggerUtils.logInfo("Skipped Test Cases  : " + skipped);
		LoggerUtils.logInfo("Pass Percentage     : " + String.format("%.2f", passRate) + "%");
		LoggerUtils.logInfo("Execution Time      : " + durationSeconds + " seconds");
		LoggerUtils.logInfo("Start Time          : " + fmt.format(startTime));
		LoggerUtils.logInfo("End Time            : " + fmt.format(endTime));
		LoggerUtils.logInfo("Environment         : " + utils.ConfigReader.getProperty("env", "QA"));
		LoggerUtils.logInfo("Browser             : " + utils.ConfigReader.getProperty("browser", "chrome"));
		LoggerUtils.logInfo("Report Path         : " + ExtentManager.getReportPath());
		LoggerUtils.logInfo("=======================================");

		LOGGER.info("Results: total=" + total + " pass=" + passed + " fail=" + failed + " skip=" + skipped);
		LOGGER.info("Report: " + ExtentManager.getReportPath());

		ExtentManager.flush();

		// Close any per-test log file that is still open.
		ConsoleLogSaver.end();
		test.remove();
	}

	@Override
	public void onTestStart(ITestResult result) {
		String testName = buildTestName(result);
		String description = getTestDescription(result);

		// Rotate the per-test log file BEFORE anything else so every line
		// produced by this test (including the ExtentReports entry below)
		// lands in its own log file under reports/logs/.
		String methodName = result.getMethod() == null ? testName : result.getMethod().getMethodName();
		String classSimple = result.getTestClass() == null ? "" : result.getTestClass().getRealClass().getSimpleName();
		String rotateName = classSimple.isEmpty() ? methodName : classSimple + "." + methodName;
		ConsoleLogSaver.rotateTo(rotateName);
		String logFileMsg = "Log file: " + ConsoleLogSaver.getLogFilePath();
		LOGGER.info(logFileMsg);
		ConsoleLogSaver.log(logFileMsg);

		ExtentTest extentTest = ExtentManager.getInstance().createTest(testName, description);
		test.set(extentTest);

		String startMsg = "[START] " + testName;
		String bannerStart = "========== TEST STARTED: " + methodName + " ==========";
		LOGGER.info(startMsg);
		LOGGER.info(bannerStart);
		ConsoleLogSaver.log(startMsg);
		ConsoleLogSaver.log(bannerStart);

		// Assign author and category.
		extentTest.assignAuthor(QA_NAME);
		extentTest.assignCategory(getTestCategory(result));

		// Log test details — matches MTNCGModule's "Test Priority" / "Test Groups" entries.
		extentTest.log(Status.INFO, "Test Priority: " + getTestPriority(result));
		extentTest.log(Status.INFO, "Test Groups: " + getTestGroups(result));
		extentTest.log(Status.INFO, "Description: " + description);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = buildTestName(result);
		String methodName = methodNameForBanner(result);
		String passMsg = "[PASS] " + testName;
		String bannerEnd = "========== TEST PASSED: " + methodName + " ==========";
		LOGGER.info(passMsg);
		LOGGER.info(bannerEnd);
		ConsoleLogSaver.log(passMsg);
		ConsoleLogSaver.log(bannerEnd);

		ExtentTest current = test.get();
		if (current != null) {
			current.pass("Test executed successfully");
			current.log(Status.PASS, "Test Passed");
		}

		// Close the per-test log file and reset for the next test.
		ConsoleLogSaver.endPerTest();
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String testName = buildTestName(result);
		String methodName = methodNameForBanner(result);
		String failMsg = "[FAIL] " + testName;
		String errMsg = "Error: " + getErrorMessage(result);
		String bannerEnd = "========== TEST FAILED: " + methodName + " ==========";
		LOGGER.severe(failMsg);
		LOGGER.severe(errMsg);
		LOGGER.severe(bannerEnd);
		ConsoleLogSaver.log(failMsg);
		ConsoleLogSaver.log(errMsg);
		ConsoleLogSaver.log(bannerEnd);

		ExtentTest current = test.get();
		if (current != null) {
			current.log(Status.FAIL, "Test Failed");
			current.fail(getErrorMessage(result));

			if (result.getThrowable() != null) {
				current.fail(result.getThrowable());
			}

			// Capture screenshot.
			captureAndAttachScreenshot(result);
		}

		// Close the per-test log so failure log is flushed to disk.
		ConsoleLogSaver.endPerTest();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = buildTestName(result);
		String methodName = methodNameForBanner(result);
		String skipMsg = "[SKIP] " + testName;
		String bannerEnd = "========== TEST SKIPPED: " + methodName + " ==========";
		LOGGER.warning(skipMsg);
		LOGGER.warning(bannerEnd);
		ConsoleLogSaver.log(skipMsg);
		ConsoleLogSaver.log(bannerEnd);

		ExtentTest current = test.get();
		if (current != null) {
			current.log(Status.SKIP, "Test Skipped");

			if (result.getThrowable() != null) {
				current.skip(result.getThrowable());
				current.log(Status.INFO, "Skip Reason: " + result.getThrowable().getMessage());
			}
		}

		// Close the per-test log.
		ConsoleLogSaver.endPerTest();
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		LOGGER.warning("[PARTIAL] " + buildTestName(result));

		ExtentTest current = test.get();
		if (current != null) {
			current.log(Status.WARNING, "Test Partially Passed");
		}
	}

	// ==================== Helper Methods ====================

	private void captureAndAttachScreenshot(ITestResult result) {
		try {
			WebDriver driver = getDriverFromResult(result);
			if (driver != null) {
				dismissUnexpectedAlerts(driver);
				String screenshotPath = ScreenshotUtils.captureFailureScreenshot(
						result.getMethod().getMethodName(), driver);
				if (screenshotPath != null && !screenshotPath.isEmpty()) {
					ExtentTest current = test.get();
					if (current != null) {
						current.addScreenCaptureFromPath(screenshotPath);
						current.log(Status.INFO, "Screenshot captured");
					}
					String screenshotMsg = "Screenshot: " + screenshotPath;
					LOGGER.info(screenshotMsg);
					ConsoleLogSaver.log(screenshotMsg);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Screenshot capture failed: {0}", e.getMessage());
		}
	}

	private WebDriver getDriverFromResult(ITestResult result) {
		if (result.getTestContext() != null) {
			Object driver = result.getTestContext().getAttribute("driver");
			if (driver instanceof WebDriver) {
				return (WebDriver) driver;
			}
		}

		try {
			Object testInstance = result.getInstance();
			if (testInstance != null) {
				Class<?> searchClass = testInstance.getClass();
				while (searchClass != null) {
					try {
						java.lang.reflect.Field driverField = searchClass.getDeclaredField("driver");
						driverField.setAccessible(true);
						Object fieldValue = driverField.get(testInstance);
						if (fieldValue instanceof WebDriver) {
							return (WebDriver) fieldValue;
						}
					} catch (NoSuchFieldException ignored) {
						// Try parent
					}
					searchClass = searchClass.getSuperclass();
				}
			}
		} catch (Exception e) {
			// Ignore
		}

		return null;
	}

	private void dismissUnexpectedAlerts(WebDriver driver) {
		try {
			driver.getCurrentUrl(); // Check session is valid
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
			LOGGER.info("Dismissed unexpected alert");
		} catch (org.openqa.selenium.WebDriverException e) {
			// No alert present, session invalid, or other WebDriver issue
		} catch (Exception e) {
			// No alert present
		}
	}

	private String buildTestName(ITestResult result) {
		String className = result.getTestClass() == null ? "" : result.getTestClass().getRealClass().getSimpleName();
		String methodName = result.getMethod() == null ? "unknown" : result.getMethod().getMethodName();
		return className.isBlank() ? methodName : className + " :: " + methodName;
	}

	/**
	 * The bare method name used inside the "========== TEST PASSED: … ==========" /
	 * "========== TEST STARTED: … ==========" banners, mirroring MTNCGModule's
	 * narrow banner that names just the test (e.g. {@code TC-063} or
	 * {@code verifyMoreRelatedShowsSectionVisible}), not the
	 * {@code ClassName :: methodName} form used by {@link #buildTestName}.
	 */
	private String methodNameForBanner(ITestResult result) {
		if (result.getMethod() == null) {
			return "unknown";
		}
		String methodName = result.getMethod().getMethodName();
		String description = result.getMethod().getDescription();
		if (description != null && !description.isBlank()) {
			return methodName + ": " + description;
		}
		return methodName;
	}

	private String getTestDescription(ITestResult result) {
		if (result.getMethod() != null && result.getMethod().getDescription() != null) {
			return result.getMethod().getDescription();
		}
		return "No description available";
	}

	private String getTestCategory(ITestResult result) {
		if (result.getTestClass() != null) {
			return result.getTestClass().getRealClass().getSimpleName();
		}
		return "General";
	}

	private String getTestPriority(ITestResult result) {
		if (result.getMethod() != null) {
			int priority = result.getMethod().getPriority();
			if (priority < 50) return "HIGH";
			if (priority < 100) return "MEDIUM";
			return "LOW";
		}
		return "N/A";
	}

	private String getTestGroups(ITestResult result) {
		if (result.getMethod() != null) {
			String[] groups = result.getMethod().getGroups();
			if (groups != null && groups.length > 0) {
				return String.join(", ", groups);
			}
		}
		return "No groups assigned";
	}

	private String getErrorMessage(ITestResult result) {
		if (result.getThrowable() != null) {
			return result.getThrowable().getMessage();
		}
		return "Unknown error";
	}

	private String getFormattedDate() {
		return new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new Date());
	}

	private String getFormattedTime() {
		return new SimpleDateFormat("hh:mm:ss a").format(new Date());
	}

	// ==================== Public accessors ====================

	/**
	 * @return the {@link ExtentTest} bound to the current test method's
	 *         thread, or {@code null} if no test is currently running.
	 *         Page-level {@code TestListener.logInfo(...)} calls forward to
	 *         this so they appear in the HTML report without TestListener
	 *         owning the test lifecycle.
	 */
	public static ExtentTest getCurrentTest() {
		return test.get();
	}
}