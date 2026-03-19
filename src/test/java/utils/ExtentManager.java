package utils;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
<<<<<<< HEAD

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
=======
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
import java.util.logging.Level;
import java.util.logging.Logger;

/**
<<<<<<< HEAD
 * Singleton factory for ExtentReports used in test reporting. - Thread-safe -
 * Timestamp-based reports - Adds system/environment info
 */
public class ExtentManager {

	private static final Logger LOGGER = Logger.getLogger(ExtentManager.class.getName());

	private static ExtentReports extent;

	public static synchronized ExtentReports getInstance() {

		if (extent == null) {

			try {
				// 📁 Create reports directory
				String reportDir = "reports";
				Files.createDirectories(Path.of(reportDir));

				// 🕒 Timestamp for unique report
				String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

				String reportPath = reportDir + "/ExtentReport_" + timestamp + ".html";

				// 📊 Reporter setup
				ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);

				reporter.config().setReportName("SonarPlay Automation Report");
				reporter.config().setDocumentTitle("Automation Test Results");
				reporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.STANDARD);

				// 📈 Extent setup
				extent = new ExtentReports();
				extent.attachReporter(reporter);

				// 🌍 Add system info (VERY IMPORTANT)
				extent.setSystemInfo("Project", "SonarPlay Automation");
				extent.setSystemInfo("Tester", System.getProperty("user.name"));
				extent.setSystemInfo("OS", System.getProperty("os.name"));
				extent.setSystemInfo("Java Version", System.getProperty("java.version"));
				extent.setSystemInfo("Browser", ConfigReader.getProperty("browser"));

				LOGGER.log(Level.INFO, "Extent report initialized: {0}", reportPath);

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to initialize ExtentReports: {0}", e.getMessage());
			}
		}

		return extent;
	}
=======
 * Singleton factory for ExtentReports used in test reporting.
 * Preserves the existing API but adds structured logging and
 * a couple of default reporter configurations.
 */
public class ExtentManager {

    private static final Logger LOGGER = Logger.getLogger(ExtentManager.class.getName());

    private static ExtentReports extent;

    public static ExtentReports getInstance() {

        if (extent == null) {

            ExtentSparkReporter reporter = new ExtentSparkReporter("test-output/ExtentReport.html");

            reporter.config().setReportName("Sonarplay Automation Report");
            reporter.config().setDocumentTitle("Sonarplay Test Results");
            reporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(reporter);

            LOGGER.log(Level.FINE, "ExtentReports instance created and reporter attached");
        }

        return extent;
    }
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
}