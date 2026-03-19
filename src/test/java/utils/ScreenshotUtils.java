package utils;

import org.openqa.selenium.*;
import org.apache.commons.io.FileUtils;
<<<<<<< HEAD

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
=======
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
import java.util.logging.Level;
import java.util.logging.Logger;

/**
<<<<<<< HEAD
 * Utility for capturing screenshots. - Supports both captureScreenshot() and
 * capture() - Adds timestamp to avoid overwrite - Ensures directory creation -
 * Provides structured logging
 */
public class ScreenshotUtils {

	private static final Logger LOGGER = Logger.getLogger(ScreenshotUtils.class.getName());

	private static final String SCREENSHOT_DIR = "reports/screenshots";

	/**
	 * Primary method (kept for backward compatibility)
	 */
	public static String captureScreenshot(WebDriver driver, String testName) {
		return takeScreenshot(driver, testName);
	}

	/**
	 * New method (used in framework)
	 */
	public static String capture(WebDriver driver, String testName) {
		return takeScreenshot(driver, testName);
	}

	/**
	 * Internal method handling screenshot logic
	 */
	private static String takeScreenshot(WebDriver driver, String testName) {

		if (driver == null) {
			LOGGER.warning("Driver is null. Screenshot skipped.");
			return "";
		}

		try {
			// Create directory if not exists
			Files.createDirectories(Path.of(SCREENSHOT_DIR));

			// Add timestamp to avoid overwrite
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

			String fileName = testName + "_" + timestamp + ".png";
			String fullPath = SCREENSHOT_DIR + "/" + fileName;

			// Capture screenshot
			TakesScreenshot ts = (TakesScreenshot) driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			File destination = new File(fullPath);

			FileUtils.copyFile(source, destination);

			LOGGER.log(Level.INFO, "Screenshot saved: {0}", fullPath);

			return fullPath;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Screenshot capture failed: {0}", e.getMessage());
			return "";
		}
	}
=======
 * Utility for capturing screenshots. Returns the path to the saved image.
 * Keeps the original API signature and behavior but adds defensive checks
 * and structured logging.
 */
public class ScreenshotUtils {

    private static final Logger LOGGER = Logger.getLogger(ScreenshotUtils.class.getName());

    public static String captureScreenshot(WebDriver driver, String testName) {

        if (driver == null)
            return "";

        String dir = "screenshots";
        String path = dir + "/" + testName + ".png";

        try {
            // ensure directory exists
            Files.createDirectories(Path.of(dir));

            TakesScreenshot ts = (TakesScreenshot) driver;

            File source = ts.getScreenshotAs(OutputType.FILE);

            File destination = new File(path);

            FileUtils.copyFile(source, destination);

            LOGGER.log(Level.FINE, "Screenshot saved to {0}", path);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to capture screenshot: {0}", e.getMessage());
            return "";
        }

        return path;
    }
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
}