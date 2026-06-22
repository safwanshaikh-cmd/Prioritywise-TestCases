package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Centralised logging facade used across the test framework. Every method
 * writes to the Log4j2 console appender (so a developer can tail the run in
 * the terminal) and to {@link ConsoleLogSaver} (so the per-test log file
 * captures the same lines).
 *
 * <p>The Log4j2 logger is resolved against the calling class — this is what
 * gives each page / test its own named logger in the console output. The
 * caller is detected by walking the stack and skipping any frame in this
 * class, so the logger name matches the test/page that originated the log
 * line, not {@code utils.LoggerUtils} itself.
 *
 * <p>Mirrors the surface area of the MTNCGModule {@code LoggerUtils} so tests
 * can use {@code logTestStart} / {@code logStep} / {@code logTestEnd} exactly
 * as in the reference project.
 */
public class LoggerUtils {

	private LoggerUtils() {
	}

	public static Logger getLogger() {
		return LogManager.getLogger(resolveCallerClassName());
	}

	public static void logInfo(String message) {
		getLogger().info(message);
		ConsoleLogSaver.log("INFO  " + message);
	}

	public static void logError(String message) {
		getLogger().error(message);
		ConsoleLogSaver.log("ERROR " + message);
	}

	public static void logError(String message, Throwable throwable) {
		getLogger().error(message, throwable);
		ConsoleLogSaver.log("ERROR " + message + (throwable == null ? "" : " | " + throwable));
	}

	public static void logDebug(String message) {
		getLogger().debug(message);
		ConsoleLogSaver.log("DEBUG " + message);
	}

	public static void logWarn(String message) {
		getLogger().warn(message);
		ConsoleLogSaver.log("WARN  " + message);
	}

	public static void logTestStart(String testName) {
		getLogger().info("========== TEST STARTED: " + testName + " ==========");
		ConsoleLogSaver.log("========== TEST STARTED: " + testName + " ==========");
	}

	public static void logTestEnd(String testName, String status) {
		getLogger().info("========== TEST " + status + ": " + testName + " ==========");
		ConsoleLogSaver.log("========== TEST " + status + ": " + testName + " ==========");
	}

	public static void logStep(String stepDescription) {
		getLogger().info("STEP: " + stepDescription);
		ConsoleLogSaver.log("STEP: " + stepDescription);
	}

	public static void logStep(int stepNumber, String stepDescription) {
		getLogger().info("STEP " + stepNumber + ": " + stepDescription);
		ConsoleLogSaver.log("STEP " + stepNumber + ": " + stepDescription);
	}

	public static void removeLogger() {
		// No-op: LoggerUtils previously kept a ThreadLocal<Logger>, but the
		// logger is now resolved per-call from the calling class's name. There
		// is no per-thread state to clean up anymore.
	}

	/**
	 * Walk the current thread's stack and return the simple class name of the
	 * first frame outside {@code utils.LoggerUtils}. This makes
	 * {@code LogManager.getLogger(...)} bind to the actual caller's class, so
	 * the console output looks like
	 * <pre>14:44:54 [main] INFO  ConsumerCategoriesTests - ...</pre>
	 * instead of
	 * <pre>14:44:54 [main] INFO  LoggerUtils - ...</pre>
	 */
	private static String resolveCallerClassName() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stackTrace.length; i++) {
			String cn = stackTrace[i].getClassName();
			if (!LoggerUtils.class.getName().equals(cn) && !cn.startsWith("java.") && !cn.startsWith("sun.")
					&& !cn.startsWith("org.apache.logging.log4j.")) {
				return cn;
			}
		}
		return LoggerUtils.class.getName();
	}
}