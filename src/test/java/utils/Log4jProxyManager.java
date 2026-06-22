package utils;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Installs the SLF4J → Log4j2 bridge for {@link java.util.logging} (j.u.l) on
 * class load. This routes any {@code java.util.logging.Logger} call sites
 * (including the ones in framework classes that pre-date the migration to
 * {@code LoggerUtils}) through Log4j2's console pattern, so the live console
 * output looks like:
 *
 * <pre>
 * 14:44:20 [main] INFO  BaseTest - === Test Suite Started ===
 * 14:44:20 [main] INFO  BaseTest - Environment: qa
 * </pre>
 *
 * <p>instead of the j.u.l {@code SimpleFormatter} output:
 *
 * <pre>
 * Jun 22, 2026 2:44:20 PM base.BaseTest setup
 * INFO: === Test Suite Started ===
 * </pre>
 *
 * <p>The class is referenced by the framework bootstrap (currently the
 * {@code ExtentReportListener#onStart} hook) so the bridge is active before
 * any test logs a line.
 */
public final class Log4jProxyManager {

	static {
		// Idempotent: SLF4JBridgeHandler.install() is a no-op on repeat calls
		// once installed is true, but we still guard with removeHandlersForRootLogger
		// so j.u.l's default ConsoleHandler doesn't double-print our messages.
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	private Log4jProxyManager() {
	}

	/**
	 * Static entry point: forces the class to load (and therefore the bridge
	 * to install). Callers should invoke this once during framework bootstrap.
	 */
	public static void install() {
		// The class-loading side-effect already ran via the static initializer.
		// This method exists so callers can be explicit.
	}
}