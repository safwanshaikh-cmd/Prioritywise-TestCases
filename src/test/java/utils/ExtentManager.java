package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Singleton factory for ExtentReports.
 */
public class ExtentManager {

	private static final Logger LOGGER = Logger.getLogger(ExtentManager.class.getName());
	private static ExtentReports extent;

	public static synchronized ExtentReports getInstance() {

		if (extent == null) {
			try {
				String reportDir = "reports";
				Files.createDirectories(Path.of(reportDir));

				String reportPath = reportDir + "/AutomationReport.html";

				ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
				reporter.config().setReportName("SonarPlay Automation Report");
				reporter.config().setDocumentTitle("Automation Test Results");
				reporter.config().setTheme(Theme.STANDARD);

				extent = new ExtentReports();
				extent.attachReporter(reporter);
				extent.setSystemInfo("Project", "SonarPlay Automation");
				extent.setSystemInfo("Tester", System.getProperty("user.name"));
				extent.setSystemInfo("OS", System.getProperty("os.name"));
				extent.setSystemInfo("Java Version", System.getProperty("java.version"));
				extent.setSystemInfo("Browser", ConfigReader.getProperty("browser", "chrome"));

				LOGGER.log(Level.INFO, "Extent report initialized: {0}", reportPath);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to initialize ExtentReports: {0}", e.getMessage());
			}
		}

		return extent;
	}
}
