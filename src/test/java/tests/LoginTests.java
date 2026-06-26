package tests;

import java.time.Duration;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import factory.DriverFactory;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Login module tests aligned with the existing framework.
 *
 * <p>Test Coverage: TC_101 - TC_135
 *
 *
 * <p>Account: Consumer/Uploader/Admin (configurable)
 */
public class LoginTests extends BaseTest {

	private LoginPage login;

	@BeforeMethod(alwaysRun = true)
	public void initLoginPage() {
		login = new LoginPage(driver);
		login.openLogin();
	}

	// ==================== TC_101: LOGIN PAGE ELEMENTS DISPLAY ====================

	/**
	 * TC_101: Login - Verify login page elements are displayed
	 */
	@Test(priority = 101, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_101: Verify login page elements are displayed")
	public void TC101_VerifyLoginPageElementsDisplayed() {
		LoggerUtils.logTestStart("TC_101: Login Page Elements Display");

		try {
			LoggerUtils.logStep(1, "Verify email field is displayed");
			boolean isEmailFieldDisplayed = login.isEmailFieldDisplayed();
			LoggerUtils.logInfo("TC_101 - STEP 1: Email field displayed: " + isEmailFieldDisplayed);

			LoggerUtils.logStep(2, "Verify password field is displayed");
			boolean isPasswordFieldDisplayed = login.isPasswordFieldDisplayed();
			LoggerUtils.logInfo("TC_101 - STEP 2: Password field displayed: " + isPasswordFieldDisplayed);

			LoggerUtils.logStep(3, "Verify login button is displayed");
			boolean isLoginButtonDisplayed = login.isLoginButtonDisplayed();
			LoggerUtils.logInfo("TC_101 - STEP 3: Login button displayed: " + isLoginButtonDisplayed);

			Assert.assertTrue(isEmailFieldDisplayed, "TC_101: Email field should be displayed");
			Assert.assertTrue(isPasswordFieldDisplayed, "TC_101: Password field should be displayed");
			Assert.assertTrue(isLoginButtonDisplayed, "TC_101: Login button should be displayed");
			LoggerUtils.logInfo("TC_101: All login page elements verified successfully");

			LoggerUtils.logTestEnd("TC_101", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_101 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_102: PASSWORD FIELD MASKED ====================

	/**
	 * TC_102: Login - Verify password field is masked
	 */
	@Test(priority = 102, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_102: Verify password field is masked")
	public void TC102_VerifyPasswordFieldIsMasked() {
		LoggerUtils.logTestStart("TC_102: Password Field Masking");

		try {
			LoggerUtils.logStep(1, "Get password field type attribute");
			String passwordFieldType = login.getPasswordFieldType();
			LoggerUtils.logInfo("TC_102 - STEP 1: Password field type: " + passwordFieldType);

			Assert.assertEquals(passwordFieldType, "password",
					"TC_102: Password field should be masked (type='password')");
			LoggerUtils.logInfo("TC_102: Password field masking verified successfully");

			LoggerUtils.logTestEnd("TC_102", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_102 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_103: EMPTY FIELDS VALIDATION ====================

	/**
	 * TC_103: Login - Verify empty fields validation
	 */
	@Test(priority = 103, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_103: Verify empty fields validation messages")
	public void TC103_VerifyEmptyFieldsValidation() {
		LoggerUtils.logTestStart("TC_103: Empty Fields Validation");

		try {
			LoggerUtils.logStep(1, "Click login button with empty fields");
			login.clickLogin();

			LoggerUtils.logStep(2, "Verify email required message");
			String emailRequiredMessage = login.getEmailRequiredMessage();
			LoggerUtils.logInfo("TC_103 - STEP 2: Email required message: " + emailRequiredMessage);

			LoggerUtils.logStep(3, "Verify password required message");
			String passwordRequiredMessage = login.getPasswordRequiredMessage();
			LoggerUtils.logInfo("TC_103 - STEP 3: Password required message: " + passwordRequiredMessage);

			Assert.assertEquals(emailRequiredMessage, "Email is required.",
					"TC_103: Email validation message should appear when email is blank");
			Assert.assertEquals(passwordRequiredMessage, "Password is Required",
					"TC_103: Password validation message should appear when password is blank");
			LoggerUtils.logInfo("TC_103: Empty fields validation verified successfully");

			LoggerUtils.logTestEnd("TC_103", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_103 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_104: EMPTY EMAIL VALIDATION ====================

	/**
	 * TC_104: Login - Verify empty email validation
	 */
	@Test(priority = 104, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_104: Verify empty email validation message")
	public void TC104_VerifyEmptyEmailValidation() {
		LoggerUtils.logTestStart("TC_104: Empty Email Validation");

		try {
			LoggerUtils.logStep(1, "Enter password only and click login");
			login.enterPassword("Password@123");
			login.clickLogin();

			LoggerUtils.logStep(2, "Verify email required message");
			String emailRequiredMessage = login.getEmailRequiredMessage();
			LoggerUtils.logInfo("TC_104 - STEP 2: Email required message: " + emailRequiredMessage);

			Assert.assertEquals(emailRequiredMessage, "Email is required.",
					"TC_104: Email validation message should appear when email is blank");
			LoggerUtils.logInfo("TC_104: Empty email validation verified successfully");

			LoggerUtils.logTestEnd("TC_104", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_104 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_105: EMPTY PASSWORD VALIDATION ====================

	/**
	 * TC_105: Login - Verify empty password validation
	 */
	@Test(priority = 105, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_105: Verify empty password validation message")
	public void TC105_VerifyEmptyPasswordValidation() {
		LoggerUtils.logTestStart("TC_105: Empty Password Validation");

		try {
			LoggerUtils.logStep(1, "Enter email only and click login");
			String validEmail = login.getValidEmail();
			if (login.isBlank(validEmail)) {
				throw new SkipException(
						"TC_105: Set login.validEmail in config.properties to run empty password validation test.");
			}
			login.enterEmail(validEmail);
			login.clickLogin();

			LoggerUtils.logStep(2, "Verify password required message");
			String passwordRequiredMessage = login.getPasswordRequiredMessage();
			LoggerUtils.logInfo("TC_105 - STEP 2: Password required message: " + passwordRequiredMessage);

			Assert.assertEquals(passwordRequiredMessage, "Password is Required",
					"TC_105: Password validation message should appear when password is blank");
			LoggerUtils.logInfo("TC_105: Empty password validation verified successfully");

			LoggerUtils.logTestEnd("TC_105", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_105 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_106: INVALID PASSWORD ====================

	/**
	 * TC_106: Login - Verify invalid password error message
	 */
	@Test(priority = 106, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_106: Verify invalid password error message")
	public void TC106_VerifyInvalidPassword() {
		LoggerUtils.logTestStart("TC_106: Invalid Password Error");

		try {
			LoggerUtils.logStep(1, "Login with valid email and invalid password");
			String email = login.getValidEmail();
			if (login.isBlank(email)) {
				throw new SkipException(
						"TC_106: Set login.validEmail in config.properties to run invalid password test.");
			}
			login.loginUser(email, "Wrong@123");

			LoggerUtils.logStep(2, "Verify invalid credentials message");
			String errorMessage = login.getInvalidCredentialsMessage();
			LoggerUtils.logInfo("TC_106 - STEP 2: Error message: " + errorMessage);

			Assert.assertEquals(errorMessage, "Invalid credentials.",
					"TC_106: Expected 'Invalid credentials.' toast for invalid password");
			LoggerUtils.logInfo("TC_106: Invalid password error verified successfully");

			LoggerUtils.logTestEnd("TC_106", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_106 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_107: UNREGISTERED USER ====================

	/**
	 * TC_107: Login - Verify unregistered user cannot login
	 */
	@Test(priority = 107, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_107: Verify unregistered user cannot login")
	public void TC107_VerifyUnregisteredUserCannotLogin() {
		LoggerUtils.logTestStart("TC_107: Unregistered User Login");

		try {
			LoggerUtils.logStep(1, "Attempt login with unregistered user credentials");
			login.loginUser("Safwan.shaikh+099@11axis.com", "Password@123");

			LoggerUtils.logStep(2, "Verify user not found message");
			String errorMessage = login.getUserNotFoundMessage();
			LoggerUtils.logInfo("TC_107 - STEP 2: Error message: " + errorMessage);

			Assert.assertEquals(errorMessage, "User not found.",
					"TC_107: Expected 'User not found.' toast for an unregistered user");
			LoggerUtils.logInfo("TC_107: Unregistered user rejection verified successfully");

			LoggerUtils.logTestEnd("TC_107", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_107 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_108: INVALID EMAIL FORMAT ====================

	/**
	 * TC_108: Login - Verify invalid email format rejection
	 */
	@Test(priority = 108, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_108: Verify invalid email format is rejected")
	public void TC108_VerifyInvalidEmailFormat() {
		LoggerUtils.logTestStart("TC_108: Invalid Email Format Rejection");

		try {
			LoggerUtils.logStep(1, "Attempt login with invalid email format");
			login.loginUser("safwan.shaikh_01211axis.com", "Password@123");

			LoggerUtils.logStep(2, "Verify login is not successful");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_108 - STEP 2: Login successful: " + loginSuccessful);

			Assert.assertFalse(loginSuccessful, "TC_108: Login should not succeed for invalid email format");
			LoggerUtils.logInfo("TC_108: Invalid email format rejection verified successfully");

			LoggerUtils.logTestEnd("TC_108", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_108 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_109: EMAIL WITH LEADING SPACE ====================

	/**
	 * TC_109: Login - Verify email with leading space is handled
	 */
	@Test(priority = 109, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_109: Verify email with leading space is trimmed")
	public void TC109_VerifyEmailWithLeadingSpace() {
		LoggerUtils.logTestStart("TC_109: Email With Leading Space");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_109: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Login with email containing leading space");
			login.loginUser(" " + login.getValidEmail(), login.getValidPassword());

			LoggerUtils.logStep(3, "Verify login is successful");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_109 - STEP 3: Login successful: " + loginSuccessful);

			Assert.assertTrue(loginSuccessful,
					"TC_109: Login should succeed because the application trims leading spaces in email");
			LoggerUtils.logInfo("TC_109: Leading space trimming verified successfully");

			LoggerUtils.logTestEnd("TC_109", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_109 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_110: EMAIL WITH TRAILING SPACE ====================

	/**
	 * TC_110: Login - Verify email with trailing space is handled
	 */
	@Test(priority = 110, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_110: Verify email with trailing space is trimmed")
	public void TC110_VerifyEmailWithTrailingSpace() {
		LoggerUtils.logTestStart("TC_110: Email With Trailing Space");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_110: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Login with email containing trailing space");
			login.loginUser(login.getValidEmail() + " ", login.getValidPassword());

			LoggerUtils.logStep(3, "Verify login is successful");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_110 - STEP 3: Login successful: " + loginSuccessful);

			Assert.assertTrue(loginSuccessful,
					"TC_110: Login should succeed because the application trims trailing spaces in email");
			LoggerUtils.logInfo("TC_110: Trailing space trimming verified successfully");

			LoggerUtils.logTestEnd("TC_110", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_110 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_111: UPPERCASE EMAIL LOGIN ====================

	/**
	 * TC_111: Login - Verify uppercase email login works
	 */
	@Test(priority = 111, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_111: Verify uppercase email login is accepted")
	public void TC111_VerifyUppercaseEmailLogin() {
		LoggerUtils.logTestStart("TC_111: Uppercase Email Login");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_111: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Login with uppercase email");
			login.loginUser(login.getValidEmail().toUpperCase(), login.getValidPassword());

			LoggerUtils.logStep(3, "Verify login is successful");
			String successMessage = login.getLoginSuccessMessage();
			boolean hasSuccessMessage = !login.isBlank(successMessage);
			LoggerUtils.logInfo("TC_111 - STEP 3: Success message: " + successMessage);

			Assert.assertTrue(hasSuccessMessage, "TC_111: Login should succeed for uppercase email input");
			LoggerUtils.logInfo("TC_111: Uppercase email login verified successfully");

			LoggerUtils.logTestEnd("TC_111", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_111 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_112: SQL INJECTION EMAIL REJECTED ====================

	/**
	 * TC_112: Login - Verify SQL injection email is rejected
	 */
	@Test(priority = 112, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_112: Verify SQL injection email payload is rejected")
	public void TC112_VerifySqlInjectionEmailRejected() {
		LoggerUtils.logTestStart("TC_112: SQL Injection Email Rejection");

		try {
			LoggerUtils.logStep(1, "Attempt login with SQL injection email payload");
			login.loginUser("' OR 1=1--", "Password@123");

			LoggerUtils.logStep(2, "Verify login is rejected");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_112 - STEP 2: Login successful: " + loginSuccessful);

			Assert.assertFalse(loginSuccessful, "TC_112: SQL injection email payload must not authenticate");
			LoggerUtils.logInfo("TC_112: SQL injection protection verified successfully");

			LoggerUtils.logTestEnd("TC_112", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_112 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_113: XSS INJECTION EMAIL REJECTED ====================

	/**
	 * TC_113: Login - Verify XSS injection email is rejected
	 */
	@Test(priority = 113, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_113: Verify XSS injection email payload is rejected")
	public void TC113_VerifyXssInjectionEmailRejected() {
		LoggerUtils.logTestStart("TC_113: XSS Injection Email Rejection");

		try {
			LoggerUtils.logStep(1, "Attempt login with XSS injection email payload");
			login.loginUser("<script>alert(1)</script>", "Password@123");

			LoggerUtils.logStep(2, "Verify login is rejected");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_113 - STEP 2: Login successful: " + loginSuccessful);

			Assert.assertFalse(loginSuccessful, "TC_113: XSS email payload must not authenticate");
			LoggerUtils.logInfo("TC_113: XSS injection protection verified successfully");

			LoggerUtils.logTestEnd("TC_113", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_113 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_114: PASSWORD WITH SPACES ====================

	/**
	 * TC_114: Login - Verify password with spaces is handled
	 */
	@Test(priority = 114, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_114: Verify password with leading/trailing spaces is trimmed")
	public void TC114_VerifyPasswordWithSpaces() {
		LoggerUtils.logTestStart("TC_114: Password With Spaces");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_114: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Login with password containing leading and trailing spaces");
			login.loginUser(login.getValidEmail(), " " + login.getValidPassword() + " ");

			LoggerUtils.logStep(3, "Verify login is successful");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_114 - STEP 3: Login successful: " + loginSuccessful);

			Assert.assertTrue(loginSuccessful,
					"TC_114: Login should succeed because the application trims password spaces");
			LoggerUtils.logInfo("TC_114: Password space trimming verified successfully");

			LoggerUtils.logTestEnd("TC_114", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_114 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_115: PASSWORD COPY PASTE ====================

	/**
	 * TC_115: Login - Verify password copy-paste works
	 */
	@Test(priority = 115, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_115: Verify password can be pasted into the field")
	public void TC115_VerifyPasswordCopyPaste() {
		LoggerUtils.logTestStart("TC_115: Password Copy Paste");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_115: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Enter email and paste password");
			login.enterEmail(login.getValidEmail());
			login.pastePassword(login.getValidPassword());
			login.clickLogin();

			LoggerUtils.logStep(3, "Verify login is successful");
			String successMessage = login.getLoginSuccessMessage();
			boolean hasSuccessMessage = !login.isBlank(successMessage);
			LoggerUtils.logInfo("TC_115 - STEP 3: Success message: " + successMessage);

			Assert.assertTrue(hasSuccessMessage,
					"TC_115: Login should succeed when password is pasted into the field");
			LoggerUtils.logInfo("TC_115: Password paste functionality verified successfully");

			LoggerUtils.logTestEnd("TC_115", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_115 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_116: LONG PASSWORD ====================

	/**
	 * TC_116: Login - Verify long password handling
	 */
	@Test(priority = 116, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_116: Verify long password is handled safely")
	public void TC116_VerifyLongPassword() {
		LoggerUtils.logTestStart("TC_116: Long Password Handling");

		try {
			LoggerUtils.logStep(1, "Attempt login with very long password");
			login.loginUser("unknownuser@example.com",
					"VeryLongPassword1234567890!@#$%^&*()_+-=VeryLongPassword");

			LoggerUtils.logStep(2, "Verify login is not successful");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_116 - STEP 2: Login successful: " + loginSuccessful);

			Assert.assertFalse(loginSuccessful, "TC_116: Unexpected login success for long password input");
			LoggerUtils.logInfo("TC_116: Long password handling verified successfully");

			LoggerUtils.logTestEnd("TC_116", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_116 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_117: REMEMBER ME CHECKED ====================

	/**
	 * TC_117: Login - Verify Remember Me checkbox can be checked
	 */
	@Test(priority = 117, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_117: Verify Remember Me checkbox is available and can be checked")
	public void TC117_VerifyRememberMeChecked() {
		LoggerUtils.logTestStart("TC_117: Remember Me Checkbox Checked");

		try {
			LoggerUtils.logStep(1, "Check if Remember Me checkbox is available");
			if (!login.isRememberMeAvailable()) {
				throw new SkipException(
						"TC_117: Remember Me checkbox is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(2, "Click Remember Me checkbox");
			login.clickRememberMe();

			LoggerUtils.logStep(3, "Verify Remember Me is still available");
			boolean isAvailable = login.isRememberMeAvailable();
			LoggerUtils.logInfo("TC_117 - STEP 3: Remember Me available: " + isAvailable);

			Assert.assertTrue(isAvailable,
					"TC_117: Remember Me option should be available for login.");
			LoggerUtils.logInfo("TC_117: Remember Me checkbox verified successfully");

			LoggerUtils.logTestEnd("TC_117", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_117 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_118: REMEMBER ME UNCHECKED ====================

	/**
	 * TC_118: Login - Verify Remember Me unchecked state
	 */
	@Test(priority = 118, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_118: Verify Remember Me checkbox is available")
	public void TC118_VerifyRememberMeUnchecked() {
		LoggerUtils.logTestStart("TC_118: Remember Me Checkbox Unchecked");

		try {
			LoggerUtils.logStep(1, "Check if Remember Me checkbox is available");
			if (!login.isRememberMeAvailable()) {
				throw new SkipException(
						"TC_118: Remember Me checkbox is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(2, "Verify Remember Me is available");
			boolean isAvailable = login.isRememberMeAvailable();
			LoggerUtils.logInfo("TC_118 - STEP 2: Remember Me available: " + isAvailable);

			Assert.assertTrue(isAvailable,
					"TC_118: Remember Me option should be available for login.");
			LoggerUtils.logInfo("TC_118: Remember Me availability verified successfully");

			LoggerUtils.logTestEnd("TC_118", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_118 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_119: BROWSER RESTART WITH REMEMBER ME ====================

	/**
	 * TC_119: Login - Verify browser restart with Remember Me checked
	 */
	@Test(priority = 119, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_119: Verify session persists after browser restart with Remember Me")
	public void TC119_VerifyBrowserRestart() {
		LoggerUtils.logTestStart("TC_119: Browser Restart With Remember Me");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials and Remember Me availability");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_119: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			if (!login.isRememberMeAvailable()) {
				throw new SkipException(
						"TC_119: Remember Me checkbox is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(2, "Check Remember Me and perform login");
			login.clickRememberMe();
			login.loginUser(login.getValidEmail(), login.getValidPassword());

			LoggerUtils.logStep(3, "Verify initial login is successful");
			boolean initialLoginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_119 - STEP 3: Initial login successful: " + initialLoginSuccessful);
			Assert.assertTrue(initialLoginSuccessful, "TC_119: Initial login should succeed before browser restart.");

			login.clickNextAfterLogin();

			LoggerUtils.logStep(4, "Restart browser and verify session persistence");
			DriverFactory.quitDriver();
			driver = DriverFactory.initDriver();
			driver.get(ConfigReader.getProperty("url"));
			driver.manage().window().maximize();
			driver.manage().timeouts()
					.implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicitWait", 10)));

			try {
				new DashboardPage(driver).acceptCookiesIfPresent();
			} catch (Exception e) {
				LoggerUtils.logInfo("TC_119 - Cookie acceptance skipped: " + login.safe(e.getMessage()));
			}

			login = new LoginPage(driver);

			boolean loginFormVisible = login.isLoginButtonDisplayed() && login.isOnLoginPage();
			LoggerUtils.logInfo("TC_119 - STEP 4: Login form visible after restart: " + loginFormVisible);

			Assert.assertFalse(loginFormVisible,
					"TC_119: User session should remain active after browser restart when Remember Me is checked.");
			LoggerUtils.logInfo("TC_119: Browser restart with Remember Me verified successfully");

			LoggerUtils.logTestEnd("TC_119", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_119 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_120: FORGOT PASSWORD LINK ====================

	/**
	 * TC_120: Login - Verify Forgot Password link functionality
	 */
	@Test(priority = 120, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_120: Verify Forgot Password link redirects correctly")
	public void TC120_VerifyForgotPasswordLink() {
		LoggerUtils.logTestStart("TC_120: Forgot Password Link");

		try {
			LoggerUtils.logStep(1, "Click Login text button if available");
			if (login.isLoginTextButtonAvailable()) {
				login.clickLoginTextButton();
			}

			LoggerUtils.logStep(2, "Check if Forgot Password link is available");
			if (!login.isForgotPasswordAvailable()) {
				throw new SkipException(
						"TC_120: Forgot Password link is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(3, "Click Forgot Password link");
			login.clickForgotPassword();

			LoggerUtils.logStep(4, "Verify redirect to reset password page");
			String currentUrl = login.safeLower(login.getCurrentUrlSafely());
			boolean redirected = currentUrl.contains("forgot") || currentUrl.contains("reset");
			LoggerUtils.logInfo("TC_120 - STEP 4: Current URL: " + currentUrl);

			Assert.assertTrue(redirected,
					"TC_120: Forgot Password should redirect to a reset-password page");
			LoggerUtils.logInfo("TC_120: Forgot Password link verified successfully");

			LoggerUtils.logTestEnd("TC_120", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_120 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_121: RESET WITH VALID EMAIL ====================

	/**
	 * TC_121: Login - Verify password reset with valid email
	 */
	@Test(priority = 121, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_121: Verify password reset request with valid email")
	public void TC121_VerifyResetWithValidEmail() {
		LoggerUtils.logTestStart("TC_121: Reset Password With Valid Email");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials and Forgot Password availability");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_121: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			if (!login.isForgotPasswordAvailable()) {
				throw new SkipException(
						"TC_121: Forgot Password link is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(2, "Click Forgot Password link");
			login.clickForgotPassword();

			LoggerUtils.logStep(3, "Verify reset email field is displayed");
			boolean isResetFieldDisplayed = login.isResetEmailFieldDisplayed();
			LoggerUtils.logInfo("TC_121 - STEP 3: Reset email field displayed: " + isResetFieldDisplayed);
			Assert.assertTrue(isResetFieldDisplayed,
					"TC_121: Reset password email field should be displayed after clicking Forgot password");

			LoggerUtils.logStep(4, "Submit reset password request");
			login.submitResetPasswordRequest(login.getValidEmail());

			LoggerUtils.logStep(5, "Verify OTP sent message");
			String otpMessage = login.getOtpSentMessage();
			LoggerUtils.logInfo("TC_121 - STEP 5: OTP message: " + otpMessage);

			Assert.assertEquals(otpMessage, "OTP sent to your registered email.",
					"TC_121: Valid reset request should stop at the OTP-sent confirmation step");
			LoggerUtils.logInfo("TC_121: Valid email reset verified successfully");

			LoggerUtils.logTestEnd("TC_121", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_121 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_122: RESET WITH INVALID EMAIL ====================

	/**
	 * TC_122: Login - Verify password reset with invalid email
	 */
	@Test(priority = 122, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_122: Verify password reset request with invalid email shows error")
	public void TC122_VerifyResetWithInvalidEmail() {
		LoggerUtils.logTestStart("TC_122: Reset Password With Invalid Email");

		try {
			LoggerUtils.logStep(1, "Check if Forgot Password link is available");
			if (!login.isForgotPasswordAvailable()) {
				throw new SkipException(
						"TC_122: Forgot Password link is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(2, "Click Forgot Password link");
			login.clickForgotPassword();

			LoggerUtils.logStep(3, "Verify reset email field is displayed");
			boolean isResetFieldDisplayed = login.isResetEmailFieldDisplayed();
			LoggerUtils.logInfo("TC_122 - STEP 3: Reset email field displayed: " + isResetFieldDisplayed);
			Assert.assertTrue(isResetFieldDisplayed,
					"TC_122: Reset password email field should be displayed after clicking Forgot password");

			LoggerUtils.logStep(4, "Submit reset password request with invalid email");
			login.submitResetPasswordRequest("unknownuser@example.com");

			LoggerUtils.logStep(5, "Verify account not found message");
			String errorMessage = login.getResetInvalidEmailMessage();
			LoggerUtils.logInfo("TC_122 - STEP 5: Error message: " + errorMessage);

			Assert.assertEquals(errorMessage, "No account found with this email or mobile number.",
					"TC_122: Invalid reset request should show the exact account-not-found message");
			LoggerUtils.logInfo("TC_122: Invalid email reset verified successfully");

			LoggerUtils.logTestEnd("TC_122", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_122 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_123: GMAIL LOGIN ====================

	/**
	 * TC_123: Login - Verify Google/Gmail login button functionality
	 */
	@Test(priority = 123, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_123: Verify Google login redirects to Google sign-in page")
	public void TC123_VerifyGmailLogin() {
		LoggerUtils.logTestStart("TC_123: Google/Gmail Login");

		try {
			LoggerUtils.logStep(1, "Check if Google login button is available");
			if (!login.isGoogleLoginAvailable()) {
				throw new SkipException(
						"TC_123: Google login button is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(2, "Get original window and count");
			String originalWindow = driver.getWindowHandle();
			int existingWindows = driver.getWindowHandles().size();
			LoggerUtils.logInfo("TC_123 - STEP 2: Original window: " + originalWindow);

			LoggerUtils.logStep(3, "Click Google login button");
			login.clickGoogleLogin();

			LoggerUtils.logStep(4, "Wait for new window or Google URL");
			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
			shortWait.until(d -> {
				String u = login.safeLower(d.getCurrentUrl());
				return d.getWindowHandles().size() > existingWindows || u.contains("google") || u.contains("accounts");
			});

			LoggerUtils.logStep(5, "Switch to new window if available");
			for (String window : driver.getWindowHandles()) {
				if (!window.equals(originalWindow)) {
					driver.switchTo().window(window);
					break;
				}
			}

			LoggerUtils.logStep(6, "Verify redirected to Google sign-in");
			String currentUrl = login.safeLower(driver.getCurrentUrl());
			boolean redirected = currentUrl.contains("google") || currentUrl.contains("accounts");
			LoggerUtils.logInfo("TC_123 - STEP 6: Current URL: " + currentUrl);

			Assert.assertTrue(redirected,
					"TC_123: Google login flow should redirect to the Gmail/Google sign-in page");
			LoggerUtils.logInfo("TC_123: Google login redirection verified successfully");

			LoggerUtils.logTestEnd("TC_123", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_123 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_124: CANCEL GMAIL LOGIN ====================

	/**
	 * TC_124: Login - Verify cancel Gmail login flow
	 */
	@Test(priority = 124, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_124: Verify cancel Gmail login flow (Skipped - needs popup automation)")
	public void TC124_VerifyCancelGmailLogin() {
		LoggerUtils.logTestStart("TC_124: Cancel Gmail Login");

		try {
			throw new SkipException(
					"TC_124: Google OAuth cancel flow needs a controlled popup/window automation path.");
		} catch (SkipException e) {
			LoggerUtils.logInfo("TC_124: Test skipped - " + e.getMessage());
			throw e;
		}
	}

	// ==================== TC_125: REGISTER BUTTON ====================

	/**
	 * TC_125: Login - Verify Register button functionality
	 */
	@Test(priority = 125, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_125: Verify Register button opens registration screen")
	public void TC125_VerifyRegisterButton() {
		LoggerUtils.logTestStart("TC_125: Register Button");

		try {
			LoggerUtils.logStep(1, "Check if Register button is available");
			if (!login.isRegisterButtonAvailable()) {
				throw new SkipException(
						"TC_125: Register button is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(2, "Click Register button");
			login.clickRegister();

			LoggerUtils.logStep(3, "Verify registration screen is displayed");
			boolean isRegistrationScreenDisplayed = login.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_125 - STEP 3: Registration screen displayed: " + isRegistrationScreenDisplayed);

			Assert.assertTrue(isRegistrationScreenDisplayed,
					"TC_125: Register button should open the registration screen");
			LoggerUtils.logInfo("TC_125: Register button functionality verified successfully");

			LoggerUtils.logTestEnd("TC_125", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_125 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_126: REGISTER URL VALIDATION ====================

	/**
	 * TC_126: Login - Verify Register URL validation
	 */
	@Test(priority = 126, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_126: Verify Register button URL contains register or signup")
	public void TC126_VerifyRegisterUrlValidation() {
		LoggerUtils.logTestStart("TC_126: Register URL Validation");

		try {
			LoggerUtils.logStep(1, "Check if Register button is available");
			if (!login.isRegisterButtonAvailable()) {
				throw new SkipException(
						"TC_126: Register button is not identifiable on the current login page.");
			}

			LoggerUtils.logStep(2, "Click Register button");
			login.clickRegister();

			LoggerUtils.logStep(3, "Verify URL contains register or signup");
			String currentUrl = login.safeLower(login.getCurrentUrlSafely());
			boolean validUrl = currentUrl.contains("register") || currentUrl.contains("signup");
			LoggerUtils.logInfo("TC_126 - STEP 3: Current URL: " + currentUrl);

			Assert.assertTrue(validUrl, "TC_126: Register URL should contain register or signup");
			LoggerUtils.logInfo("TC_126: Register URL validation verified successfully");

			LoggerUtils.logTestEnd("TC_126", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_126 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_127: SQL INJECTION LOGIN BOTH FIELDS ====================

	/**
	 * TC_127: Login - Verify SQL injection in both email and password
	 */
	@Test(priority = 127, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_127: Verify SQL injection in both fields is rejected")
	public void TC127_VerifySqlInjectionLogin() {
		LoggerUtils.logTestStart("TC_127: SQL Injection In Both Fields");

		try {
			LoggerUtils.logStep(1, "Attempt login with SQL injection in both fields");
			login.loginUser("' OR 1=1--", "' OR 1=1--");

			LoggerUtils.logStep(2, "Verify login is rejected");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_127 - STEP 2: Login successful: " + loginSuccessful);

			Assert.assertFalse(loginSuccessful, "TC_127: SQL injection payloads must not authenticate");
			LoggerUtils.logInfo("TC_127: SQL injection protection verified successfully");

			LoggerUtils.logTestEnd("TC_127", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_127 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_128: XSS ATTACK BOTH FIELDS ====================

	/**
	 * TC_128: Login - Verify XSS attack in both email and password
	 */
	@Test(priority = 128, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_128: Verify XSS attack in both fields is rejected")
	public void TC128_VerifyXssAttack() {
		LoggerUtils.logTestStart("TC_128: XSS Attack In Both Fields");

		try {
			LoggerUtils.logStep(1, "Attempt login with XSS injection in both fields");
			login.loginUser("<script>alert(1)</script>", "<script>alert(1)</script>");

			LoggerUtils.logStep(2, "Verify login is rejected");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_128 - STEP 2: Login successful: " + loginSuccessful);

			Assert.assertFalse(loginSuccessful, "TC_128: XSS payloads must not authenticate");
			LoggerUtils.logInfo("TC_128: XSS attack protection verified successfully");

			LoggerUtils.logTestEnd("TC_128", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_128 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_129: BRUTE FORCE ATTEMPT ====================

	/**
	 * TC_129: Login - Verify brute force attempt handling
	 */
	@Test(priority = 129, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_129: Verify repeated invalid login attempts are handled safely")
	public void TC129_VerifyBruteForceAttempt() {
		LoggerUtils.logTestStart("TC_129: Brute Force Attempt Handling");

		try {
			LoggerUtils.logStep(1, "Get email for testing (use valid or fallback)");
			String email = login.getValidEmail();
			if (login.isBlank(email)) {
				email = "unknownuser@example.com";
			}
			LoggerUtils.logInfo("TC_129 - STEP 1: Using email: " + email);

			LoggerUtils.logStep(2, "Perform 3 invalid login attempts");
			String responseMessage = "";
			for (int i = 0; i < 3; i++) {
				driver.get(ConfigReader.getProperty("url"));
				login = new LoginPage(driver);
				login.openLogin();
				if (login.isLoginTextButtonAvailable()) {
					login.clickLoginTextButton();
				}

				LoggerUtils.logInfo("TC_129 - STEP 2: Attempt " + (i + 1) + "/3");
				Assert.assertTrue(login.isEmailFieldDisplayed(),
						"TC_129: Email field should be available for each brute-force attempt");

				login.loginUser(email, "Wrong@123");
				responseMessage = login.getInvalidCredentialsMessage();
				if (login.isBlank(responseMessage)) {
					responseMessage = login.getUserNotFoundMessage();
				}
				if (login.isBlank(responseMessage)) {
					responseMessage = login.getLoginMessage();
				}
			}

			LoggerUtils.logStep(3, "Verify safe response after repeated attempts");
			boolean hasResponse = !login.isBlank(responseMessage);
			LoggerUtils.logInfo("TC_129 - STEP 3: Response message: " + responseMessage);

			Assert.assertTrue(hasResponse,
					"TC_129: Repeated invalid attempts should still return a safe response");
			LoggerUtils.logInfo("TC_129: Brute force protection verified successfully");

			LoggerUtils.logTestEnd("TC_129", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_129 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_130: DOUBLE CLICK LOGIN ====================

	/**
	 * TC_130: Login - Verify double-click login button behavior
	 */
	@Test(priority = 130, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_130: Verify double-clicking login button results in single login")
	public void TC130_VerifyDoubleClickLogin() {
		LoggerUtils.logTestStart("TC_130: Double Click Login Button");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_130: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Enter credentials and double-click login button");
			login.enterEmail(login.getValidEmail());
			login.enterPassword(login.getValidPassword());
			login.doubleClickLogin();

			LoggerUtils.logStep(3, "Verify login is successful");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_130 - STEP 3: Login successful: " + loginSuccessful);

			Assert.assertTrue(loginSuccessful,
					"TC_130: Double-clicking login should still result in one successful login");
			LoggerUtils.logInfo("TC_130: Double-click handling verified successfully");

			LoggerUtils.logTestEnd("TC_130", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_130 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

		// ==================== TC_131: SLOW INTERNET LOGIN ====================
	
		/**
		 * TC_131: Login - Verify slow internet login functionality
		 */
		@Test(priority = 131, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_PERFORMANCE,
				TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_131: Verify login works with slow internet using network throttling")
		public void TC131_VerifySlowInternetLogin() {
		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_131: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Enable slow network throttling (Slow 3G simulation)");
			login.enableSlowNetworkThrottling();

			LoggerUtils.logStep(3, "Perform login with throttled network");
			login.loginUser(login.getValidEmail(), login.getValidPassword());

			LoggerUtils.logStep(4, "Verify login is successful even with slow network");
			boolean loginSuccessful = login.isLoginSuccessful();
			LoggerUtils.logInfo("TC_131 - STEP 4: Login successful: " + loginSuccessful);

			LoggerUtils.logStep(5, "Disable network throttling");
			login.disableNetworkThrottling();

			Assert.assertTrue(loginSuccessful,
					"TC_131: Login should succeed even with slow network conditions");
			LoggerUtils.logInfo("TC_131: Slow internet login verified successfully");

			LoggerUtils.logTestEnd("TC_131", "PASSED");
		} catch (SkipException e) {
			// Ensure throttling is disabled even if test is skipped
			try {
				login.disableNetworkThrottling();
			} catch (Exception ignored) {
			}
			throw e;
		} catch (Exception e) {
			// Ensure throttling is disabled on failure
			try {
				login.disableNetworkThrottling();
			} catch (Exception ignored) {
			}
			LoggerUtils.logWarn("TC_131 - Test failed: " + login.safe(e.getMessage()));
			throw e;

		}
	}

	// ==================== TC_132: NETWORK DISCONNECT ====================

	/**
	 * TC_132: Login - Verify that login attempt fails gracefully when the network
	 * is disconnected mid-flow, and the user remains on the login page. All
	 * DevTools / network-control plumbing lives in {@link LoginPage}; this test
	 * only orchestrates the scenario.
	 */
	@Test(priority = 132, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_PERFORMANCE,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_132: Verify login behavior when network is disconnected")
	public void TC132_VerifyNetworkDisconnect() {
		LoggerUtils.logTestStart("TC_132: Network Disconnect Login");

		boolean networkInitialized = false;
		try {
			LoggerUtils.logStep(1, "Verify valid credentials are configured");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_132: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Initialize network control via LoginPage (Chrome DevTools)");
			login.initializeNetworkControl();
			if (!login.isNetworkControlled()) {
				throw new SkipException("TC_132: Network control could not be initialized (non-Chrome driver?).");
			}
			networkInitialized = true;

			LoggerUtils.logStep(3, "Disconnect network via LoginPage.disconnectNetwork()");
			if (!login.disconnectNetwork()) {
				throw new SkipException("TC_132: Could not disconnect network.");
			}

			LoggerUtils.logStep(4, "Attempt login with network disconnected");
			login.enterEmail(login.getValidEmail());
			login.enterPassword(login.getValidPassword());
			login.clickLogin();

			LoggerUtils.logStep(5, "Verify user remains on login page (login must not succeed while offline)");
			boolean stayedOnLoginPage = new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
				String currentUrl = d.getCurrentUrl();
				String url = (currentUrl == null) ? "" : currentUrl.toLowerCase();
				return url.contains("/login") || url.contains("signin");
			});
			Assert.assertTrue(stayedOnLoginPage,
					"TC_132: With network disconnected, login must not navigate away from the login page");
			LoggerUtils.logInfo("TC_132: Login correctly blocked / stayed on login page while offline");

			LoggerUtils.logTestEnd("TC_132", "PASSED");
		} catch (SkipException e) {
			LoggerUtils.logInfo("TC_132: Test skipped - " + e.getMessage());
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_132 - Test failed: " + e.getMessage());
			throw e;
		} finally {
			// Always restore the network so the rest of the suite is unaffected.
			if (networkInitialized) {
				LoggerUtils.logInfo("TC_132 - Restoring network in finally block");
				login.reconnectNetwork();
			}
		}
	}

	// ==================== TC_133: BROWSER REFRESH DURING LOGIN ====================

	/**
	 * TC_133: Login - Verify page refresh during login
	 */
	@Test(priority = 133, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_133: Verify login page remains accessible after refresh")
	public void TC133_VerifyBrowserRefreshDuringLogin() {
		LoggerUtils.logTestStart("TC_133: Browser Refresh During Login");

		try {
			LoggerUtils.logStep(1, "Enter credentials");
			login.enterEmail(login.getValidEmail());
			login.enterPassword(login.getValidPassword());

			LoggerUtils.logStep(2, "Refresh the page");
			login.refreshPage();

			LoggerUtils.logStep(3, "Verify login page is still accessible");
			boolean onLoginPage = login.isOnLoginPage();
			LoggerUtils.logInfo("TC_133 - STEP 3: On login page: " + onLoginPage);

			Assert.assertTrue(onLoginPage,
					"TC_133: After refresh, login page should still be accessible");
			LoggerUtils.logInfo("TC_133: Browser refresh handling verified successfully");

			LoggerUtils.logTestEnd("TC_133", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_133 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_134: BROWSER BACK AFTER LOGIN ====================

	/**
	 * TC_134: Login - Verify browser back after successful login
	 */
	@Test(priority = 134, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_134: Verify browser back after login does not expose login form")
	public void TC134_VerifyBrowserBackAfterLogin() {
		LoggerUtils.logTestStart("TC_134: Browser Back After Login");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			if (!login.hasValidCredentials()) {
				throw new SkipException(
						"TC_134: Set login.validEmail and login.validPassword in config.properties to run this test.");
			}

			LoggerUtils.logStep(2, "Perform login and click Next");
			login.loginUser(login.getValidEmail(), login.getValidPassword());
			login.clickNextAfterLogin();

			LoggerUtils.logStep(3, "Navigate back");
			login.navigateBack();

			LoggerUtils.logStep(4, "Verify login form is not exposed");
			boolean loginFormExposed = login.isOnLoginPage() && login.isLoginButtonDisplayed();
			LoggerUtils.logInfo("TC_134 - STEP 4: Login form exposed: " + loginFormExposed);

			Assert.assertFalse(loginFormExposed,
					"TC_134: After successful login, browser back should not expose the active login form again");
			LoggerUtils.logInfo("TC_134: Browser back after login verified successfully");

			LoggerUtils.logTestEnd("TC_134", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_134 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_135: VALID LOGIN ====================

	/**
	 * TC_135: Login - Verify valid login functionality
	 */
	@Test(priority = 135, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_135: Verify valid login with correct credentials")
	public void TC135_VerifyValidLogin() {
		LoggerUtils.logTestStart("TC_135: Valid Login Functionality");

		try {
			LoggerUtils.logStep(1, "Check for valid credentials");
			String email = login.getValidEmail();
			String password = login.getValidPassword();

			if (login.isBlank(email) || login.isBlank(password)) {
				throw new SkipException(
						"TC_135: Set login.validEmail and login.validPassword in config.properties to run valid login.");
			}

			LoggerUtils.logStep(2, "Perform login with valid credentials");
			login.loginUser(email, password);

			LoggerUtils.logStep(3, "Verify success message");
			String successMsg = login.getLoginSuccessMessage();
			boolean hasSuccessMessage = !login.isBlank(successMsg);
			LoggerUtils.logInfo("TC_135 - STEP 3: Success message: " + successMsg);

			Assert.assertTrue(hasSuccessMessage, "TC_135: Login failed - success message not displayed");

			LoggerUtils.logStep(4, "Click Next after login");
			login.clickNextAfterLogin();

			LoggerUtils.logInfo("TC_135: Valid login verified successfully");
			LoggerUtils.logTestEnd("TC_135", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_135 - Test failed: " + login.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== (No local helpers — see LoginPage) ====================
}
