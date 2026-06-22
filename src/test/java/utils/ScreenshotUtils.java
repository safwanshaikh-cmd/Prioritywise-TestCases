package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Utility for capturing screenshots.
 *
 * <p>Two flavours of capture are supported:
 * <ul>
 *   <li>{@link #captureScreenshot(WebDriver, String)} / {@link #capture(WebDriver, String)}
 *       — generic capture used by existing tests; produces
 *       {@code reports/screenshots/<name>_<yyyyMMdd_HHmmss>.png}.</li>
 *   <li>{@link #captureFailureScreenshot(String, WebDriver)} /
 *       {@link #captureSuccessScreenshot(String, WebDriver)} — used by
 *       {@code ExtentReportListener} on pass/fail; the file name is suffixed
 *       with {@code _FAILED} / {@code _PASSED} so it is easy to spot in the
 *       screenshots folder.</li>
 * </ul>
 *
 * <p>The screenshot directory is centralised in
 * {@link ExecutionFolderManager#getScreenshotsDirectory()} so callers don't
 * hard-code paths.
 */
public class ScreenshotUtils {

	private static final Logger LOGGER = Logger.getLogger(ScreenshotUtils.class.getName());
	private static final String SCREENSHOT_DIR = ExecutionFolderManager.getScreenshotsDirectory();

	public static String captureScreenshot(WebDriver driver, String testName) {
		return takeScreenshot(driver, testName);
	}

	public static String capture(WebDriver driver, String testName) {
		return takeScreenshot(driver, testName);
	}

	/**
	 * Capture a screenshot annotated as a failure. The file name is suffixed
	 * with {@code _FAILED} so it sorts visually distinct from generic captures.
	 */
	public static String captureFailureScreenshot(String testName, WebDriver driver) {
		return takeScreenshot(driver, testName + "_FAILED");
	}

	/**
	 * Capture a screenshot annotated as a success. The file name is suffixed
	 * with {@code _PASSED}.
	 */
	public static String captureSuccessScreenshot(String testName, WebDriver driver) {
		return takeScreenshot(driver, testName + "_PASSED");
	}

	private static String takeScreenshot(WebDriver driver, String testName) {

		if (driver == null) {
			LOGGER.warning("Driver is null. Screenshot skipped.");
			return "";
		}

		try {
			dismissUnexpectedAlerts(driver);
			Files.createDirectories(Path.of(SCREENSHOT_DIR));

			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String fileName = testName + "_" + timestamp + ".png";
			String fullPath = SCREENSHOT_DIR + File.separator + fileName;

			TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
			File source = screenshotDriver.getScreenshotAs(OutputType.FILE);
			File destination = new File(fullPath);

			FileUtils.copyFile(source, destination);
			LOGGER.log(Level.INFO, "Screenshot saved: {0}", fullPath);
			return fullPath;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Screenshot capture failed: {0}", e.getMessage());
			return "";
		}
	}

	private static void dismissUnexpectedAlerts(WebDriver driver) {
		try {
			driver.getCurrentUrl(); // Check session is valid
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
		} catch (org.openqa.selenium.WebDriverException e) {
			// No alert present, session invalid, or other WebDriver issue
		} catch (Exception e) {
			// No alert present
		}
	}
}