package utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for test wait operations.
 * Provides safe wait methods for test synchronization.
 */
public class TestWaitHelper {

	private static final Logger LOGGER = Logger.getLogger(TestWaitHelper.class.getName());

	/**
	 * Pause execution for specified milliseconds.
	 * This method handles InterruptedException gracefully.
	 *
	 * @param millis Time to wait in milliseconds
	 */
	public static void waitForMilliseconds(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.log(Level.WARNING, "Sleep interrupted: {0}", e.getMessage());
		}
	}

	/**
	 * Pause execution for specified seconds.
	 * This method handles InterruptedException gracefully.
	 *
	 * @param seconds Time to wait in seconds
	 */
	public static void waitForSeconds(int seconds) {
		waitForMilliseconds(seconds * 1000L);
	}

	/**
	 * Pause execution for a short moment (500ms).
	 * Useful for brief UI state transitions.
	 */
	public static void shortWait() {
		waitForMilliseconds(500);
	}

	/**
	 * Pause execution for a medium moment (2000ms).
	 * Useful for page element loading.
	 */
	public static void mediumWait() {
		waitForMilliseconds(2000);
	}

	/**
	 * Pause execution for a long moment (5000ms).
	 * Useful for page navigation and full page loads.
	 */
	public static void longWait() {
		waitForMilliseconds(5000);
	}
}
