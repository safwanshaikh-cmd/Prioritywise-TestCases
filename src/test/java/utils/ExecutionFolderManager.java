package utils;

import java.io.File;

/**
 * Manages the folder layout used during a single test execution:
 * <pre>
 *   user.dir
 *     └── reports/
 *         ├── screenshots/
 *         └── logs/
 * </pre>
 *
 * <p>Mirrors the MTNCGModule {@code ExecutionFolderManager}. A static initializer
 * creates the three directories the first time the class is loaded, so any
 * downstream utility (report, screenshot, log) can rely on them existing.
 *
 * <p>Paths are read from {@code ConfigReader} so they can be overridden via
 * {@code -Dreport.path=...}, environment variables, or
 * {@code src/test/resources/config.properties}. Hard-coded paths are avoided.
 */
public final class ExecutionFolderManager {

	private static final String RUN_ROOT = absolute(resolve("report.path", "reports"));
	private static final String REPORTS_DIR = RUN_ROOT;
	private static final String SCREENSHOTS_DIR = absolute(
			resolve("screenshot.path", REPORTS_DIR + File.separator + "screenshots"));
	private static final String LOGS_DIR = REPORTS_DIR + File.separator + "logs";

	static {
		createDirectory(RUN_ROOT);
		createDirectory(REPORTS_DIR);
		createDirectory(SCREENSHOTS_DIR);
		createDirectory(LOGS_DIR);
	}

	private ExecutionFolderManager() {
	}

	public static String getRunRoot() {
		return RUN_ROOT;
	}

	public static String getReportsDirectory() {
		return REPORTS_DIR;
	}

	public static String getScreenshotsDirectory() {
		return SCREENSHOTS_DIR;
	}

	public static String getLogsDirectory() {
		return LOGS_DIR;
	}

	private static String resolve(String key, String defaultValue) {
		String value = ConfigReader.getProperty(key);
		return (value == null || value.isBlank()) ? defaultValue : value;
	}

	/**
	 * Resolve a path to an absolute one if it isn't already. Relative paths
	 * are resolved against {@code user.dir} so the same layout is produced
	 * regardless of where the JVM was launched from.
	 */
	private static String absolute(String path) {
		File file = new File(path);
		return file.isAbsolute() ? file.getPath() : file.getAbsolutePath();
	}

	private static void createDirectory(String path) {
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}
}