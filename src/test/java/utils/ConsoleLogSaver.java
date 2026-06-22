package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Saves test output to a per-test log file
 * (e.g. {@code TC141_VerifySuperAdminLoginAndDashboardLoad_2026_06_18_11_40_25.log}).
 *
 * <p>One file per test method. No suite-level log file is produced. Files are
 * written to {@code reports/logs/} (flat layout; see {@link ExecutionFolderManager}).
 *
 * <p>Lifecycle:
 * <ol>
 *   <li>{@link #start()} — ensures the logs directory exists. Does not open
 *       any writer (kept for backward compatibility with
 *       {@code ExtentReportListener.onStart}).</li>
 *   <li>{@link #rotateTo(String)} — opens a new per-test log file named after
 *       the test method. Subsequent {@link #log(String)} calls land in this
 *       file.</li>
 *   <li>{@link #endPerTest()} — closes the per-test writer. The next test's
 *       {@link #rotateTo(String)} opens a fresh file.</li>
 *   <li>{@link #end()} — closes whatever writer is currently active. Used at
 *       suite finish.</li>
 * </ol>
 */
public class ConsoleLogSaver {

	private static PrintWriter writer;
	private static String logFilePath;
	private static String currentTestName;

	/**
	 * Ensure the logs directory exists. Kept as a no-op-for-files entry point
	 * for backward compatibility with {@code ExtentReportListener.onStart}.
	 * No suite-level log file is created.
	 */
	public static synchronized void start() {
		File logDir = new File(ExecutionFolderManager.getLogsDirectory());
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
	}

	/**
	 * Open a new per-test log file named after the given test
	 * (e.g. {@code ConsumerCategoriesTests.verifyMoreRelatedShowsSectionVisible_2026_06_18_11_40_25.log}).
	 * The {@code testName} is sanitized so it is safe to use as a file name.
	 *
	 * @param testName the TestNG test method name (e.g. "TC217_AdminCanAccessAutoRenewalModule")
	 */
	public static synchronized void rotateTo(String testName) {
		// Close the previous per-test writer if one is open.
		closeActiveWriter();
		try {
			String safeName = sanitizeFileName(testName);
			String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
			File logDir = new File(ExecutionFolderManager.getLogsDirectory());
			if (!logDir.exists()) {
				logDir.mkdirs();
			}
			logFilePath = logDir.getPath() + File.separator + safeName + "_" + timestamp + ".log";
			currentTestName = safeName;
			writer = new PrintWriter(new FileWriter(logFilePath, true));
			writer.println("=".repeat(60));
			writer.println("Test Log: " + testName);
			writer.println("Started: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			writer.println("Log file: " + logFilePath);
			writer.println("=".repeat(60));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close the per-test writer (if active). Call this at the end of each
	 * test so the per-test file is flushed and the next test's
	 * {@link #rotateTo(String)} starts with a clean writer.
	 */
	public static synchronized void endPerTest() {
		closeActiveWriter();
	}

	public static void log(String message) {
		if (writer != null) {
			writer.println(message);
			writer.flush();
		}
	}

	/**
	 * Close whatever writer is currently active. Used at suite finish.
	 */
	public static synchronized void end() {
		closeActiveWriter();
	}

	public static String getLogFilePath() {
		return logFilePath;
	}

	public static String getCurrentTestName() {
		return currentTestName;
	}

	public static boolean isPerTestFile() {
		return writer != null;
	}

	private static void closeActiveWriter() {
		if (writer != null) {
			try {
				writer.println("=".repeat(60));
				writer.println("Ended: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				writer.flush();
				writer.close();
			} catch (Exception ignored) {
			} finally {
				writer = null;
				logFilePath = null;
				currentTestName = null;
			}
		}
	}

	/**
	 * Sanitize a string so it is safe to use as a file name: replace any
	 * non-alphanumeric / dash / underscore characters with underscores.
	 */
	private static String sanitizeFileName(String input) {
		if (input == null || input.isEmpty()) {
			return "test";
		}
		return input.replaceAll("[^A-Za-z0-9_\\-]", "_");
	}
}