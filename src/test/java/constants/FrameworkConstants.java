package constants;

/**
 * Centralized constants for the test automation framework.
 */
public final class FrameworkConstants {

    private FrameworkConstants() {
        // Prevent instantiation
    }

    // ==================== Timeouts (in seconds) ====================
    public static final int SHORT_TIMEOUT = 3;
    public static final int DEFAULT_TIMEOUT = 10;
    public static final int MEDIUM_TIMEOUT = 15;
    public static final int LONG_TIMEOUT = 30;
    public static final int MAX_TIMEOUT = 60;

    // ==================== Wait Intervals (in milliseconds) ====================
    public static final int POLLING_INTERVAL = 500;
    public static final int RETRY_INTERVAL = 1000;
    public static final int STABLE_WAIT = 2000;

    // ==================== Retry Configuration ====================
    public static final int MAX_RETRY_ATTEMPTS = 2;
    public static final int CLICK_RETRY_ATTEMPTS = 3;
    public static final int ELEMENT_WAIT_RETRY_ATTEMPTS = 3;

    // ==================== Report Paths ====================
    public static final String REPORT_PATH = "reports/";
    public static final String SCREENSHOT_PATH = "reports/screenshots/";
    public static final String EXTENT_REPORT_NAME = "AutomationReport.html";
    public static final String SCREENSHOT_FORMAT = ".png";

    // ==================== Locator Strategy Names ====================
    public static final String LOCATOR_TYPE_ID = "id";
    public static final String LOCATOR_TYPE_NAME = "name";
    public static final String LOCATOR_TYPE_CLASS = "class";
    public static final String LOCATOR_TYPE_XPATH = "xpath";
    public static final String LOCATOR_TYPE_CSS = "css";
    public static final String LOCATOR_TYPE_LINKTEXT = "linkText";
    public static final String LOCATOR_TYPE_PARTIALLINKTEXT = "partialLinkText";
    public static final String LOCATOR_TYPE_TAGNAME = "tagName";

    // ==================== Browser Names ====================
    public static final String BROWSER_CHROME = "chrome";
    public static final String BROWSER_EDGE = "edge";
    public static final String BROWSER_FIREFOX = "firefox";
    public static final String BROWSER_SAFARI = "safari";

    // ==================== Common Test Data ====================
    public static final String TEST_EMAIL_DOMAIN = "@11axis.com";
    public static final String DEFAULT_PASSWORD = "Password@123";

    // ==================== Wait Condition Constants ====================
    public static final String PAGE_LOAD_STATE_COMPLETE = "complete";
    public static final String AJAX_COMPLETE_SCRIPT = "return (typeof jQuery != 'undefined' && jQuery.active == 0) || true;";
}