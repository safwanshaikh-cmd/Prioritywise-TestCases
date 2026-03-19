package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Utility for capturing screenshots.
 */
public class ScreenshotUtils {

	private static final Logger LOGGER = Logger.getLogger(ScreenshotUtils.class.getName());
	private static final String SCREENSHOT_DIR = "reports/screenshots";

	public static String captureScreenshot(WebDriver driver, String testName) {
		return takeScreenshot(driver, testName);
	}

	public static String capture(WebDriver driver, String testName) {
		return takeScreenshot(driver, testName);
	}

	private static String takeScreenshot(WebDriver driver, String testName) {

		if (driver == null) {
			LOGGER.warning("Driver is null. Screenshot skipped.");
			return "";
		}

		try {
			Files.createDirectories(Path.of(SCREENSHOT_DIR));

			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String fileName = testName + "_" + timestamp + ".png";
			String fullPath = SCREENSHOT_DIR + "/" + fileName;

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
}
