package constants;

/**
 * Centralized message constants for validation and logging.
 */
public final class ValidationMessages {

    private ValidationMessages() {
        // Prevent instantiation
    }

    // ==================== Login Page Messages ====================
    public static final String EMAIL_REQUIRED = "Email is required.";
    public static final String PASSWORD_REQUIRED = "Password is Required";
    public static final String INVALID_CREDENTIALS = "Invalid credentials.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String OTP_SENT = "OTP sent to your registered email.";
    public static final String ACCOUNT_NOT_FOUND = "No account found with this email or mobile number.";
    public static final String PASSWORD_SPECIAL_CHAR = "Password must include exactly one special character";

    // ==================== Registration Page Messages ====================
    public static final String NAME_REQUIRED = "Name is required.";
    public static final String EMAIL_INVALID = "Please enter a valid email address.";
    public static final String PASSWORD_MISMATCH = "Passwords do not match.";
    public static final String TERMS_REQUIRED = "You must agree to the terms and conditions.";
    public static final String EMAIL_ALREADY_EXISTS = "An account with this email already exists.";

    // ==================== General Error Messages ====================
    public static final String ELEMENT_NOT_FOUND = "Element not found";
    public static final String ELEMENT_NOT_VISIBLE = "Element is not visible";
    public static final String ELEMENT_NOT_CLICKABLE = "Element is not clickable";
    public static final String PAGE_LOAD_FAILED = "Page failed to load within expected timeout";
    public static final String NAVIGATION_FAILED = "Navigation failed";
    public static final String TIMEOUT_ERROR = "Operation timed out";
    public static final String STALE_ELEMENT_ERROR = "Element reference is stale";

    // ==================== Assertion Messages ====================
    public static final String EXPECTED_BUT_WAS = "Expected: %s, but was: %s";
    public static final String SHOULD_NOT_BE_NULL = "Value should not be null";
    public static final String SHOULD_NOT_BE_EMPTY = "Value should not be empty";
    public static final String SHOULD_CONTAIN = "'%s' should contain '%s'";
    public static final String SHOULD_NOT_CONTAIN = "'%s' should not contain '%s'";
    public static final String SHOULD_EQUAL = "Values should be equal";
    public static final String SHOULD_NOT_EQUAL = "Values should not be equal";
    public static final String URL_SHOULD_CONTAIN = "URL should contain '%s'";
    public static final String TITLE_SHOULD_CONTAIN = "Page title should contain '%s'";

    // ==================== Test Execution Messages ====================
    public static final String TEST_SKIPPED = "Test skipped: %s";
    public static final String TEST_FAILED = "Test failed: %s";
    public static final String TEST_PASSED = "Test passed";
    public static final String CREDENTIALS_MISSING = "Required credentials are not configured in config.properties";

    // ==================== Page Object Messages ====================
    public static final String LOGIN_PAGE_LOAD_FAILED = "Failed to navigate to Login page";
    public static final String REGISTRATION_PAGE_LOAD_FAILED = "Failed to navigate to Registration page";
    public static final String DASHBOARD_PAGE_LOAD_FAILED = "Failed to navigate to Dashboard";
    public static final String ELEMENT_ACTION_FAILED = "Failed to perform action on element: %s";
    public static final String ASSERTION_FAILED = "Assertion failed: %s";
}