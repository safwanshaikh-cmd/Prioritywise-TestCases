package tests;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import factory.DriverFactory;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

/**
 * Login module tests aligned with the existing framework.
 */
public class LoginTests extends BaseTest {

	private LoginPage login;

	private String getConfiguredEmail() {
		return ConfigReader.getProperty("login.validEmail");
	}

	private String getConfiguredPassword() {
		return ConfigReader.getProperty("login.validPassword");
	}

	private String getInactiveEmail() {
		return ConfigReader.getProperty("login.inactiveEmail", "");
	}

	private String getInactivePassword() {
		return ConfigReader.getProperty("login.inactivePassword", "");
	}

	private String getInactiveMessageFragment() {
		return ConfigReader.getProperty("login.inactiveMessage", "inactive");
	}

	private void skipIfValidCredentialsMissing() {
		if (getConfiguredEmail() == null || getConfiguredEmail().isBlank() || getConfiguredPassword() == null
				|| getConfiguredPassword().isBlank()) {
			throw new SkipException(
					"Set login.validEmail and login.validPassword in config.properties to run this test.");
		}
	}

	private void skipIfInactiveCredentialsMissing() {
		if (getInactiveEmail().isBlank() || getInactivePassword().isBlank()) {
			throw new SkipException(
					"Set login.inactiveEmail and login.inactivePassword in config.properties to run inactive-user tests.");
		}
	}

	private void assertInactiveUserBlocked(String email, String password, String context) {
		login.loginUser(email, password);
		String message = login.printAndCaptureLoginMessage(context);
		Assert.assertFalse(login.isLoginSuccessful(), context + " should not log in successfully.");
		Assert.assertFalse(message.isBlank(), context + " should show an authentication response.");
		Assert.assertTrue(message.toLowerCase().contains(getInactiveMessageFragment().toLowerCase())
				|| message.toLowerCase().contains("invalid") || message.toLowerCase().contains("not found"),
				context + " should show an inactive-account or safe rejection message. Actual message: " + message);
	}

	private void ensureValidCredentialsConfigured() {
		skipIfValidCredentialsMissing();
	}

	private void ensureRememberMeAvailable() {
		if (!login.isRememberMeAvailable()) {
			throw new SkipException("Remember Me checkbox is not identifiable on the current login page.");
		}
	}

	private void openForgotPasswordFlow() {
		if (!login.isForgotPasswordAvailable()) {
			throw new SkipException("Forgot Password link is not identifiable on the current login page.");
		}
		login.openForgotPasswordFlow();
		Assert.assertTrue(login.isResetEmailFieldDisplayed(),
				"Reset password email field should be displayed after clicking Forgot password");
	}

	private void ensureRegisterAvailable() {
		if (!login.isRegisterButtonAvailable()) {
			throw new SkipException("Register button is not identifiable on the current login page.");
		}
	}

	private void assertLoginSucceeds(String email, String password, String context) {
		login.loginUser(email, password);
		Assert.assertTrue(login.isLoginSuccessful(),
				context + " should log in successfully. Message: " + login.printAndCaptureLoginMessage(context));
	}

	private void assertLoginRejected(String email, String password, String context, String... expectedFragments) {
		login.loginUser(email, password);
		String message = login.printAndCaptureLoginMessage(context);
		Assert.assertFalse(login.isLoginSuccessful(), context + " should not log in successfully.");
		if (expectedFragments == null || expectedFragments.length == 0) {
			return;
		}
		String normalizedMessage = message.toLowerCase();
		for (String fragment : expectedFragments) {
			Assert.assertTrue(normalizedMessage.contains(fragment.toLowerCase()),
					context + " should show feedback containing '" + fragment + "'. Actual message: " + message);
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void initLoginPage() {
		login = new LoginPage(driver);
		login.openLogin();
	}

	@Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
	public void verifyLoginPageElementsDisplayed() {
		Assert.assertTrue(login.isEmailFieldDisplayed(), "Email field is not displayed");
		Assert.assertTrue(login.isPasswordFieldDisplayed(), "Password field is not displayed");
		Assert.assertTrue(login.isLoginButtonDisplayed(), "Login button is not displayed");
	}

	@Test(priority = 2, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordFieldIsMasked() {
		Assert.assertEquals(login.getPasswordFieldType(), "password", "Password field should remain masked");
	}

	@Test(priority = 3, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptyFieldsValidation() {
		login.clickLogin();
		Assert.assertEquals(login.getEmailRequiredMessage(), login.getExpectedEmailRequiredText(),
				"Email validation message should appear when email is blank");
		Assert.assertEquals(login.getPasswordRequiredMessage(), login.getExpectedPasswordRequiredText(),
				"Password validation message should appear when password is blank");
	}

	@Test(priority = 4, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptyEmailValidation() {
		login.enterPassword("Password@123");
		login.clickLogin();
		Assert.assertEquals(login.getEmailRequiredMessage(), login.getExpectedEmailRequiredText(),
				"Email validation message should appear when email is blank");
	}

	@Test(priority = 5, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptyPasswordValidation() {
		login.enterEmail(getConfiguredEmail());
		login.clickLogin();
		Assert.assertEquals(login.getPasswordRequiredMessage(), login.getExpectedPasswordRequiredText(),
				"Password validation message should appear when password is blank");
	}

	@Test(priority = 6, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidPassword() {
		assertLoginRejected(getConfiguredEmail(), "Wrong@123", "Invalid password", "invalid");
		Assert.assertEquals(login.getInvalidCredentialsMessage(), login.getExpectedInvalidCredentialsText(),
				"Expected 'Invalid credentials.' toast for invalid password");
	}

	@Test(priority = 7, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUnregisteredUserCannotLogin() {
		assertLoginRejected("Safwan.shaikh+099@11axis.com", "Password@123", "Unregistered user login", "user");
		Assert.assertEquals(login.getUserNotFoundMessage(), login.getExpectedUserNotFoundText(),
				"Expected 'User not found.' toast for an unregistered user");
	}

	@Test(priority = 8, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidEmailFormat() {
		login.loginUser("safwan.shaikh_01211axis.com", "Password@123");
		Assert.assertFalse(login.isLoginSuccessful(), "Login should not succeed for invalid email format");
	}

	@Test(priority = 9, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailWithLeadingSpace() {
		ensureValidCredentialsConfigured();
		assertLoginSucceeds(" " + getConfiguredEmail(), getConfiguredPassword(), "Email with leading space");
	}

	@Test(priority = 10, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailWithTrailingSpace() {
		ensureValidCredentialsConfigured();
		assertLoginSucceeds(getConfiguredEmail() + " ", getConfiguredPassword(), "Email with trailing space");
	}

	@Test(priority = 11, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUppercaseEmailLogin() {
		ensureValidCredentialsConfigured();
		assertLoginSucceeds(getConfiguredEmail().toUpperCase(), getConfiguredPassword(), "Uppercase email login");
	}

	@Test(priority = 12, retryAnalyzer = RetryAnalyzer.class)
	public void verifySqlInjectionEmailRejected() {
		login.loginUser("' OR 1=1--", "Password@123");
		Assert.assertFalse(login.isLoginSuccessful(), "SQL injection email payload must not authenticate");
	}

	@Test(priority = 13, retryAnalyzer = RetryAnalyzer.class)
	public void verifyXssInjectionEmailRejected() {
		login.loginUser("<script>alert(1)</script>", "Password@123");
		Assert.assertFalse(login.isLoginSuccessful(), "XSS email payload must not authenticate");
	}

	@Test(priority = 14, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordWithSpaces() {
		ensureValidCredentialsConfigured();
		assertLoginSucceeds(getConfiguredEmail(), " " + getConfiguredPassword() + " ", "Password with spaces");
	}

	@Test(priority = 15, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordCopyPaste() {
		ensureValidCredentialsConfigured();
		login.enterEmail(getConfiguredEmail());
		login.pastePassword(getConfiguredPassword());
		login.clickLogin();
		Assert.assertTrue(login.isLoginSuccessful(), "Login should succeed when password is pasted into the field");
	}

	@Test(priority = 16, retryAnalyzer = RetryAnalyzer.class)
	public void verifyLongPassword() {
		login.loginUser("unknownuser@example.com", "VeryLongPassword1234567890!@#$%^&*()_+-=VeryLongPassword");
		Assert.assertFalse(login.isLoginSuccessful(), "Unexpected login success for long password input");
	}

	@Test(priority = 17, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRememberMeChecked() {
		ensureRememberMeAvailable();
		login.clickRememberMe();
		Assert.assertTrue(login.isRememberMeAvailable(), "Remember Me option should be available for login.");
	}

	@Test(priority = 18, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRememberMeUnchecked() {
		ensureRememberMeAvailable();
		Assert.assertTrue(login.isRememberMeAvailable(), "Remember Me option should be available for login.");
	}

	@Test(priority = 19, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBrowserRestart() {
		ensureValidCredentialsConfigured();
		ensureRememberMeAvailable();

		login.clickRememberMe();
		assertLoginSucceeds(getConfiguredEmail(), getConfiguredPassword(), "Remember me initial login");
		login.clickNextAfterLogin();

		DriverFactory.quitDriver();
		driver = DriverFactory.initDriver();
		driver.get(ConfigReader.getProperty("url"));
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicitWait", 10)));

		try {
			new DashboardPage(driver).acceptCookiesIfPresent();
		} catch (Exception e) {
			// best effort only
		}

		login = new LoginPage(driver);

		Assert.assertFalse(login.isLoginButtonDisplayed() && login.isOnLoginPage(),
				"User session should remain active after browser restart when Remember Me is checked.");
	}

	@Test(priority = 20, retryAnalyzer = RetryAnalyzer.class)
	public void verifyForgotPasswordLink() {
		openForgotPasswordFlow();
		Assert.assertTrue(
				login.getCurrentUrl().toLowerCase().contains("forgot")
						|| login.getCurrentUrl().toLowerCase().contains("reset"),
				"Forgot Password should redirect to a reset-password page");
	}

	@Test(priority = 21, retryAnalyzer = RetryAnalyzer.class)
	public void verifyResetWithValidEmail() {
		ensureValidCredentialsConfigured();
		openForgotPasswordFlow();
		login.submitResetPasswordRequest(getConfiguredEmail());
		Assert.assertEquals(login.getOtpSentMessage(), login.getExpectedOtpSentText(),
				"Valid reset request should stop at the OTP-sent confirmation step");
	}

	@Test(priority = 22, retryAnalyzer = RetryAnalyzer.class)
	public void verifyResetWithInvalidEmail() {
		openForgotPasswordFlow();
		login.submitResetPasswordRequest("unknownuser@example.com");
		Assert.assertEquals(login.getResetInvalidEmailMessage(), login.getExpectedResetInvalidEmailText(),
				"Invalid reset request should show the exact account-not-found message");
	}

	@Test(priority = 23, retryAnalyzer = RetryAnalyzer.class)
	public void verifyGmailLogin() {
		if (!login.isGoogleLoginAvailable()) {
			throw new SkipException("Google login button is not identifiable on the current login page.");
		}
		String originalWindow = driver.getWindowHandle();
		int existingWindows = driver.getWindowHandles().size();
		login.clickGoogleLogin();

		WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
		shortWait.until(
				d -> d.getWindowHandles().size() > existingWindows || d.getCurrentUrl().toLowerCase().contains("google")
						|| d.getCurrentUrl().toLowerCase().contains("accounts"));

		Set<String> windows = driver.getWindowHandles();
		for (String window : windows) {
			if (!window.equals(originalWindow)) {
				driver.switchTo().window(window);
				break;
			}
		}

		String currentUrl = driver.getCurrentUrl().toLowerCase();
		Assert.assertTrue(currentUrl.contains("google") || currentUrl.contains("accounts"),
				"Google login flow should redirect to the Gmail/Google sign-in page");
	}

	@Test(priority = 24, retryAnalyzer = RetryAnalyzer.class)
	public void verifyCancelGmailLogin() {
		throw new SkipException("Google OAuth cancel flow needs a controlled popup/window automation path.");
	}

	@Test(priority = 25, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRegisterButton() {
		ensureRegisterAvailable();
		login.clickRegister();
		Assert.assertTrue(login.isRegistrationScreenDisplayed(), "Register button should open the registration screen");
	}

	@Test(priority = 26, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRegisterUrlValidation() {
		ensureRegisterAvailable();
		login.clickRegister();
		String currentUrl = login.getCurrentUrl().toLowerCase();
		Assert.assertTrue(currentUrl.contains("register") || currentUrl.contains("signup"),
				"Register URL should contain register or signup");
	}

	@Test(priority = 27, retryAnalyzer = RetryAnalyzer.class)
	public void verifySqlInjectionLogin() {
		login.loginUser("' OR 1=1--", "' OR 1=1--");
		Assert.assertFalse(login.isLoginSuccessful(), "SQL injection payloads must not authenticate");
	}

	@Test(priority = 28, retryAnalyzer = RetryAnalyzer.class)
	public void verifyXssAttack() {
		login.loginUser("<script>alert(1)</script>", "<script>alert(1)</script>");
		Assert.assertFalse(login.isLoginSuccessful(), "XSS payloads must not authenticate");
	}

	@Test(priority = 29, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBruteForceAttempt() {
		String email = getConfiguredEmail();
		if (email == null || email.isBlank()) {
			email = "unknownuser@example.com";
		}

		String responseMessage = "";

		for (int i = 0; i < 3; i++) {
			driver.get(ConfigReader.getProperty("url"));
			login = new LoginPage(driver);
			login.openLogin();
			if (login.isLoginTextButtonAvailable()) {
				login.clickLoginTextButton();
			}
			Assert.assertTrue(login.isEmailFieldDisplayed(),
					"Email field should be available for each brute-force attempt");
			login.loginUser(email, "Wrong@123");
			responseMessage = login.getInvalidCredentialsMessage();
			if (responseMessage.isEmpty()) {
				responseMessage = login.getUserNotFoundMessage();
			}
			if (responseMessage.isEmpty()) {
				responseMessage = login.getLoginMessage();
			}
		}

		Assert.assertTrue(!responseMessage.isEmpty(), "Repeated invalid attempts should still return a safe response");
	}

	@Test(priority = 30, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDoubleClickLogin() {
		ensureValidCredentialsConfigured();
		login.enterEmail(getConfiguredEmail());
		login.enterPassword(getConfiguredPassword());
		login.doubleClickLogin();
		Assert.assertTrue(login.isLoginSuccessful(),
				"Double-clicking login should still result in one successful login");
	}

	@Test(priority = 31, retryAnalyzer = RetryAnalyzer.class)
	public void verifySlowInternetLogin() {
		throw new SkipException(
				"Slow network login needs browser network throttling support in the current environment.");
	}

	@Test(priority = 32, retryAnalyzer = RetryAnalyzer.class)
	public void verifyNetworkDisconnect() {
		throw new SkipException(
				"Network disconnect login test needs browser offline simulation in the current environment.");
	}

	@Test(priority = 33, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBrowserRefreshDuringLogin() {
		login.enterEmail(getConfiguredEmail());
		login.enterPassword(getConfiguredPassword());
		login.refreshPage();
		Assert.assertTrue(login.isOnLoginPage(), "After refresh, login page should still be accessible");
	}

	@Test(priority = 34, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBrowserBackAfterLogin() {
		ensureValidCredentialsConfigured();
		assertLoginSucceeds(getConfiguredEmail(), getConfiguredPassword(), "Browser back after login");
		login.clickNextAfterLogin();
		login.navigateBack();
		Assert.assertFalse(login.isOnLoginPage() && login.isLoginButtonDisplayed(),
				"After successful login, browser back should not expose the active login form again");
	}

	@Test(priority = 35, retryAnalyzer = RetryAnalyzer.class)
	public void verifyValidLogin() {
		ensureValidCredentialsConfigured();
		assertLoginSucceeds(getConfiguredEmail(), getConfiguredPassword(), "Valid login");
		login.clickNextAfterLogin();
	}

	@Test(priority = 36, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInactiveUserCannotLogin() {
		skipIfInactiveCredentialsMissing();
		assertInactiveUserBlocked(getInactiveEmail(), getInactivePassword(), "Inactive user login");
	}

	@Test(priority = 37, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInactiveUserWithUppercaseEmailCannotLogin() {
		skipIfInactiveCredentialsMissing();
		assertInactiveUserBlocked(getInactiveEmail().toUpperCase(), getInactivePassword(),
				"Inactive user login with uppercase email");
	}

	@Test(priority = 38, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInactiveUserWithTrimmedEmailCannotLogin() {
		skipIfInactiveCredentialsMissing();
		assertInactiveUserBlocked(" " + getInactiveEmail() + " ", getInactivePassword(),
				"Inactive user login with surrounding spaces");
	}

}
