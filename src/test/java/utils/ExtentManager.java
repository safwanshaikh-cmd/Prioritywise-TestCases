package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Singleton factory for {@link ExtentReports}.
 * <p>
 * When {@link #getInstance(String)} is called with a test-class name, the
 * report file is named {@code <ClassSimpleName>_<yyyyMMdd_HHmmss>.html} and
 * stored under {@code reports/}. This mirrors the MTNCGModule behaviour where
 * each test class produces its own timestamped HTML report (e.g.
 * {@code ConsumerCategoriesTests_20260622_143005.html}).
 * <p>
 * When {@link #getInstance()} is called with no argument, the report is named
 * {@code AutomationReport.html} — preserved for any callers that still use the
 * flat single-file layout.
 */
public class ExtentManager {

	private static final Logger LOGGER = Logger.getLogger(ExtentManager.class.getName());
	private static ExtentReports extent;

	// Editable project metadata — kept as constants so they appear in every
	// generated report and can be tweaked in one place.
	private static final String PROJECT_NAME = "SonarPlay Automation";
	private static final String APPLICATION_NAME = "Sonarplay-TestCases";
	private static final String ENVIRONMENT = "QA";
	private static final String DEFAULT_BROWSER = "chrome";
	private static final String DEFAULT_REPORT_FILE = "AutomationReport.html";

	private static String reportFileName = DEFAULT_REPORT_FILE;

	private ExtentManager() {
	}

	public static synchronized ExtentReports getInstance() {
		return getInstance(null);
	}

	/**
	 * Build (or return the cached) {@link ExtentReports}. When {@code className}
	 * is non-null and non-empty, the report file and titles are scoped to that
	 * test class.
	 *
	 * @param className fully qualified test class name (e.g. {@code "tests.ConsumerCategoriesTests"}),
	 *                  or {@code null} to use the default flat report.
	 */
	public static synchronized ExtentReports getInstance(String className) {
		if (extent == null) {
			try {
				if (className != null && !className.isEmpty()) {
					String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
					String simpleClassName = className.replaceAll("^tests\\.", "");
					reportFileName = simpleClassName + "_" + timestamp + ".html";
				} else {
					reportFileName = DEFAULT_REPORT_FILE;
				}

				String reportPath = ExecutionFolderManager.getReportsDirectory() + java.io.File.separator + reportFileName;

				ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);

				String reportTitle = (className != null && !className.isEmpty())
						? className.replaceAll("^tests\\.", "") + " - Test Report"
						: PROJECT_NAME;
				reporter.config().setReportName(reportTitle);
				reporter.config().setDocumentTitle(reportTitle);
				reporter.config().setTheme(Theme.STANDARD);
				reporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

				extent = new ExtentReports();
				extent.attachReporter(reporter);

				// System Information — same set MTN populates, plus our project constants.
				extent.setSystemInfo("Project Name", PROJECT_NAME);
				extent.setSystemInfo("QA Engineer", System.getProperty("user.name", "QA"));
				extent.setSystemInfo("Environment", ConfigReader.getProperty("env", ENVIRONMENT));
				extent.setSystemInfo("Application", APPLICATION_NAME);
				extent.setSystemInfo("Browser", ConfigReader.getProperty("browser", DEFAULT_BROWSER));
				extent.setSystemInfo("Test Class", className != null ? className : "All Tests");
				extent.setSystemInfo("Test Run Date", getCurrentDate());
				extent.setSystemInfo("Test Run Time", getCurrentTime());
				extent.setSystemInfo("Java Version", System.getProperty("java.version"));
				extent.setSystemInfo("Operating System", System.getProperty("os.name"));

				LOGGER.log(Level.INFO, "Extent report initialized: {0}", reportPath);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to initialize ExtentReports: {0}", e.getMessage());
			}
		}
		return extent;
	}

	/**
	 * Flush any pending report entries to disk and drop the cached instance so
	 * the next call to {@link #getInstance(String)} starts a fresh report.
	 */
	public static void flush() {
		if (extent != null) {
			extent.flush();
			LOGGER.info("Extent report flushed successfully");
			extent = null;
		}
	}

	/**
	 * @return the file name of the currently configured report.
	 */
	public static String getReportFileName() {
		return reportFileName;
	}

	/**
	 * @return the absolute path of the currently configured report file.
	 */
	public static String getReportPath() {
		return ExecutionFolderManager.getReportsDirectory() + java.io.File.separator + reportFileName;
	}

	private static String getCurrentDate() {
		return new SimpleDateFormat("MMMM dd, yyyy").format(new Date());
	}

	private static String getCurrentTime() {
		return new SimpleDateFormat("hh:mm:ss a").format(new Date());
	}
}