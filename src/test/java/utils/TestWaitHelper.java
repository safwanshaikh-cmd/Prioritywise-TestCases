package utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @deprecated Use WaitUtils instead. This class exists only for backward compatibility.
 *
 * Utility class for test wait operations.
 * Provides safe wait methods for test synchronization.
 *
 * This class is deprecated. Use WaitUtils for explicit waits or ElementUtils for element interactions.
 */
@Deprecated
public class TestWaitHelper {

    private static final Logger LOGGER = Logger.getLogger(TestWaitHelper.class.getName());

    /**
     * @deprecated Use WaitUtils.waitForPageLoad() instead
     */
    @Deprecated
    public static void waitForMilliseconds(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Sleep interrupted: {0}", e.getMessage());
        }
    }

    /**
     * @deprecated Use WaitUtils.waitForPageLoad() instead
     */
    @Deprecated
    public static void waitForSeconds(int seconds) {
        waitForMilliseconds(seconds * 1000L);
    }

    /**
     * @deprecated Use explicit waits - hardcoded waits are anti-pattern
     */
    @Deprecated
    public static void shortWait() {
        waitForMilliseconds(500);
    }

    /**
     * @deprecated Use explicit waits - hardcoded waits are anti-pattern
     */
    @Deprecated
    public static void mediumWait() {
        waitForMilliseconds(2000);
    }

    /**
     * @deprecated Use explicit waits - hardcoded waits are anti-pattern
     */
    @Deprecated
    public static void longWait() {
        waitForMilliseconds(5000);
    }
}