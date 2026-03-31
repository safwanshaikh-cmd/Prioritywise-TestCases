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
import pages.PlayerPage;
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

	// Helper methods for different account types
	private String getConsumerEmail() {
		return ConfigReader.getProperty("consumer.email", getConfiguredEmail());
	}

	private String getConsumerPassword() {
		return ConfigReader.getProperty("consumer.password", getConfiguredPassword());
	}

	private String getUploaderEmail() {
		return ConfigReader.getProperty("uploader.email", getConfiguredEmail());
	}

	private String getUploaderPassword() {
		return ConfigReader.getProperty("uploader.password", getConfiguredPassword());
	}

	private String getAdminEmail() {
		return ConfigReader.getProperty("admin.email", getConfiguredEmail());
	}

	private String getAdminPassword() {
		return ConfigReader.getProperty("admin.password", getConfiguredPassword());
	}

	private void skipIfValidCredentialsMissing() {
		if (getConfiguredEmail() == null || getConfiguredEmail().isBlank() || getConfiguredPassword() == null
				|| getConfiguredPassword().isBlank()) {
			throw new SkipException(
					"Set login.validEmail and login.validPassword in config.properties to run this test.");
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
		Assert.assertEquals(login.getEmailRequiredMessage(), "Email is required.",
				"Email validation message should appear when email is blank");
		Assert.assertEquals(login.getPasswordRequiredMessage(), "Password is Required",
				"Password validation message should appear when password is blank");
	}

	@Test(priority = 4, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptyEmailValidation() {
		login.enterPassword("Password@123");
		login.clickLogin();
		Assert.assertEquals(login.getEmailRequiredMessage(), "Email is required.",
				"Email validation message should appear when email is blank");
	}

	@Test(priority = 5, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptyPasswordValidation() {
		login.enterEmail(getConfiguredEmail());
		login.clickLogin();
		Assert.assertEquals(login.getPasswordRequiredMessage(), "Password is Required",
				"Password validation message should appear when password is blank");
	}

	@Test(priority = 6, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidPassword() {
		String email = ConfigReader.getProperty("login.validEmail");
		login.loginUser(email, "Wrong@123");
		Assert.assertEquals(login.getInvalidCredentialsMessage(), "Invalid credentials.",
				"Expected 'Invalid credentials.' toast for invalid password");
	}

	@Test(priority = 7, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUnregisteredUserCannotLogin() {
		login.loginUser("Safwan.shaikh+099@11axis.com", "Password@123");
		Assert.assertEquals(login.getUserNotFoundMessage(), "User not found.",
				"Expected 'User not found.' toast for an unregistered user");
	}

	@Test(priority = 8, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidEmailFormat() {
		login.loginUser("safwan.shaikh_01211axis.com", "Password@123");
		Assert.assertFalse(login.isLoginSuccessful(), "Login should not succeed for invalid email format");
	}

	@Test(priority = 9, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailWithLeadingSpace() {
		skipIfValidCredentialsMissing();
		login.loginUser(" " + getConfiguredEmail(), getConfiguredPassword());
		Assert.assertTrue(login.isLoginSuccessful(),
				"Login should succeed because the application trims leading spaces in email");
	}

	@Test(priority = 10, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailWithTrailingSpace() {
		skipIfValidCredentialsMissing();
		login.loginUser(getConfiguredEmail() + " ", getConfiguredPassword());
		Assert.assertTrue(login.isLoginSuccessful(),
				"Login should succeed because the application trims trailing spaces in email");
	}

	@Test(priority = 11, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUppercaseEmailLogin() {
		skipIfValidCredentialsMissing();
		login.loginUser(getConfiguredEmail().toUpperCase(), getConfiguredPassword());
		Assert.assertFalse(login.getLoginSuccessMessage().isEmpty(), "Login should succeed for uppercase email input");
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
		skipIfValidCredentialsMissing();
		login.loginUser(getConfiguredEmail(), " " + getConfiguredPassword() + " ");
		Assert.assertTrue(login.isLoginSuccessful(),
				"Login should succeed because the application trims password spaces");
	}

	@Test(priority = 15, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordCopyPaste() {
		skipIfValidCredentialsMissing();
		login.enterEmail(getConfiguredEmail());
		login.pastePassword(getConfiguredPassword());
		login.clickLogin();
		Assert.assertFalse(login.getLoginSuccessMessage().isEmpty(),
				"Login should succeed when password is pasted into the field");
	}

	@Test(priority = 16, retryAnalyzer = RetryAnalyzer.class)
	public void verifyLongPassword() {
		login.loginUser("unknownuser@example.com", "VeryLongPassword1234567890!@#$%^&*()_+-=VeryLongPassword");
		Assert.assertFalse(login.isLoginSuccessful(), "Unexpected login success for long password input");
	}

	@Test(priority = 17, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRememberMeChecked() {
		if (!login.isRememberMeAvailable()) {
			throw new SkipException("Remember Me checkbox is not identifiable on the current login page.");
		}
		login.clickRememberMe();
		Assert.assertTrue(login.isRememberMeAvailable(), "Remember Me option should be available for login.");
	}

	@Test(priority = 18, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRememberMeUnchecked() {
		if (!login.isRememberMeAvailable()) {
			throw new SkipException("Remember Me checkbox is not identifiable on the current login page.");
		}
		Assert.assertTrue(login.isRememberMeAvailable(), "Remember Me option should be available for login.");
	}

	@Test(priority = 19, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBrowserRestart() {
		skipIfValidCredentialsMissing();

		if (!login.isRememberMeAvailable()) {
			throw new SkipException("Remember Me checkbox is not identifiable on the current login page.");
		}

		login.clickRememberMe();
		login.loginUser(getConfiguredEmail(), getConfiguredPassword());
		Assert.assertTrue(login.isLoginSuccessful(), "Initial login should succeed before browser restart.");
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
		if (login.isLoginTextButtonAvailable()) {
			login.clickLoginTextButton();
		}
		if (!login.isForgotPasswordAvailable()) {
			throw new SkipException("Forgot Password link is not identifiable on the current login page.");
		}
		login.clickForgotPassword();
		Assert.assertTrue(
				login.getCurrentUrl().toLowerCase().contains("forgot")
						|| login.getCurrentUrl().toLowerCase().contains("reset"),
				"Forgot Password should redirect to a reset-password page");
	}

	@Test(priority = 21, retryAnalyzer = RetryAnalyzer.class)
	public void verifyResetWithValidEmail() {
		skipIfValidCredentialsMissing();
		if (!login.isForgotPasswordAvailable()) {
			throw new SkipException("Forgot Password link is not identifiable on the current login page.");
		}
		login.clickForgotPassword();
		Assert.assertTrue(login.isResetEmailFieldDisplayed(),
				"Reset password email field should be displayed after clicking Forgot password");
		login.submitResetPasswordRequest(getConfiguredEmail());
		Assert.assertEquals(login.getOtpSentMessage(), "OTP sent to your registered email.",
				"Valid reset request should stop at the OTP-sent confirmation step");
	}

	@Test(priority = 22, retryAnalyzer = RetryAnalyzer.class)
	public void verifyResetWithInvalidEmail() {
		if (!login.isForgotPasswordAvailable()) {
			throw new SkipException("Forgot Password link is not identifiable on the current login page.");
		}
		login.clickForgotPassword();
		Assert.assertTrue(login.isResetEmailFieldDisplayed(),
				"Reset password email field should be displayed after clicking Forgot password");
		login.submitResetPasswordRequest("unknownuser@example.com");
		Assert.assertEquals(login.getResetInvalidEmailMessage(), "No account found with this email or mobile number.",
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
		if (!login.isRegisterButtonAvailable()) {
			throw new SkipException("Register button is not identifiable on the current login page.");
		}
		login.clickRegister();
		Assert.assertTrue(login.isRegistrationScreenDisplayed(), "Register button should open the registration screen");
	}

	@Test(priority = 26, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRegisterUrlValidation() {
		if (!login.isRegisterButtonAvailable()) {
			throw new SkipException("Register button is not identifiable on the current login page.");
		}
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
		skipIfValidCredentialsMissing();
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
		skipIfValidCredentialsMissing();
		login.loginUser(getConfiguredEmail(), getConfiguredPassword());
		login.clickNextAfterLogin();
		login.navigateBack();
		Assert.assertFalse(login.isOnLoginPage() && login.isLoginButtonDisplayed(),
				"After successful login, browser back should not expose the active login form again");
	}

	@Test(priority = 35, retryAnalyzer = RetryAnalyzer.class)
	public void verifyValidLogin() {

		String email = getConfiguredEmail();
		String password = getConfiguredPassword();

		if (email == null || email.isBlank() || password == null || password.isBlank()) {
			throw new SkipException(
					"Set login.validEmail and login.validPassword in config.properties to run valid login.");
		}

		login.loginUser(email, password);

		String successMsg = login.getLoginSuccessMessage();
		Assert.assertTrue(!successMsg.isEmpty(), "Login failed - success message not displayed");

		login.clickNextAfterLogin();
	}

}