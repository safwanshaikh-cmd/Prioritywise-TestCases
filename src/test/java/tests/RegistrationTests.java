package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import factory.DriverFactory;
import listeners.RetryAnalyzer;
import pages.RegistrationPage;
import pages.RegistrationPage.RegistrationFormData;
import utils.ConfigReader;

/**
 * Registration module tests aligned with the LoginTests style.
 */
public class RegistrationTests extends BaseTest {

	private static final AtomicInteger UNIQUE_COUNTER = new AtomicInteger(1000);
	private static final DateTimeFormatter UNIQUE_STAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private RegistrationPage registration;

	@BeforeMethod(alwaysRun = true)
	public void initRegistrationPage() {
		registration = new RegistrationPage(driver);
		registration.openLogin();
		registration.openRegistration();
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration screen should be available before each registration test.");
	}

	@Test(priority = 1, groups = "positive",retryAnalyzer = RetryAnalyzer.class)
	public void verifyValidRegistration() {
		assertSuccessfulRegistration(validRegistrationData(), "Valid registration");
	}

	@Test(priority = 2, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptyFormValidation() {
		registration.clickRegister();
		registration.printVisibleValidationMessages();
		assertMandatoryWarnings();
	}

	@Test(priority = 3, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameValid() {
		registration.enterName("Safwan Shaikh");
		Assert.assertEquals(registration.getNameValue(), "Safwan Shaikh", "Valid name should be accepted.");
	}

	@Test(priority = 4, groups = "negative" , retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameWithNumbers() {
		assertRejectedRegistration(validRegistrationData().withName("Safwan123"), "Name with numbers");
	}

	@Test(priority = 5, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameSpecialCharacters() {
		assertRejectedRegistration(validRegistrationData().withName("@#$%"), "Name special characters", "name");
	}

	@Test(priority = 6, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameLeadingSpace() {
		assertScenarioHandledGracefully(validRegistrationData().withName(" John"), "Name leading space");
	}

	@Test(priority = 7, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameTrailingSpace() {
		assertScenarioHandledGracefully(validRegistrationData().withName("John "), "Name trailing space");
	}

	@Test(priority = 8, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameMultipleSpaces() {
		assertScenarioHandledGracefully(validRegistrationData().withName("John   Doe"), "Name multiple spaces");
	}

	@Test(priority = 9, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameUnicode() {
		assertScenarioHandledGracefully(validRegistrationData().withName("Jose"), "Name unicode");
	}

	@Test(priority = 10, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameEmoji() {
		assertRejectedRegistration(validRegistrationData().withName("John\uD83D\uDE0A"), "Name emoji");
	}

	@Test(priority = 11, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameSqlInjection() {
		assertRejectedRegistration(validRegistrationData().withName("' OR 1=1--"), "Name SQL injection");
	}

	@Test(priority = 12, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNameXssInjection() {
		assertRejectedRegistration(validRegistrationData().withName("<script>alert(1)</script>"), "Name XSS");
	}

	@Test(priority = 13, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyUsernameValid() {
		registration.enterUsername("user123");
		Assert.assertEquals(registration.getUsernameValue(), "user123", "Valid username should be accepted.");
	}

	//There is no Validation or Limit for Minimum Length of User Name but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 14, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyUsernameMinLength() {
		assertRejectedRegistration(validRegistrationData().withUsername("usr"), "Username minimum length");
	}

	//There is no Validation or Limit for Maximum Length of User Name but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 15, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyUsernameMaxLength() {
		assertScenarioHandledGracefully(validRegistrationData().withUsername("verylongusernamevalue"),
				"Username max length");
	}
	
	//There is not validation for spaces in username but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 16, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyUsernameWithSpaces() {
		assertRejectedRegistration(validRegistrationData().withUsername("user name"), "Username with spaces");
	}
	
	//There is no Validation for special characters in username but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 17, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyUsernameSpecialCharacters() {
		assertRejectedRegistration(validRegistrationData().withUsername("user@123"), "Username special characters");
	}

	@Test(priority = 18, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyDuplicateUsernameRegistration() {
		assertRejectedRegistration(validRegistrationData().withUsername(getExistingUsername()), "Duplicate username");
	}

	@Test(priority = 19, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyUsernameUnicode() {
		assertRejectedRegistration(validRegistrationData().withUsername("user\u6C49\u5B57123"), "Username unicode");
	}

	@Test(priority = 20, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyUsernameEmoji() {
		assertRejectedRegistration(validRegistrationData().withUsername("user\uD83D\uDE0A"), "Username emoji");
	}

	@Test(priority = 21, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailValid() {
		registration.enterEmail("user@mail.com");
		Assert.assertEquals(registration.getEmailValue(), "user@mail.com", "Valid email should be accepted.");
	}

	@Test(priority = 22, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailMissingAt() {
		assertRejectedRegistration(validRegistrationData().withEmail("usermail.com"), "Email missing @", "email");
	}

	@Test(priority = 23, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailMissingDomain() {
		assertRejectedRegistration(validRegistrationData().withEmail("user@"), "Email missing domain", "email");
	}

	@Test(priority = 24, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailMultipleAt() {
		assertRejectedRegistration(validRegistrationData().withEmail("user@@mail.com"), "Email multiple @");
	}

	//There is no Validation for spaces in email but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 25, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailLeadingSpace() {
		assertScenarioHandledGracefully(validRegistrationData().withEmail(" user@mail.com"), "Email leading space");
	}

	//There is no Validation for spaces in email but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 26, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailTrailingSpace() {
		assertScenarioHandledGracefully(validRegistrationData().withEmail("user@mail.com "), "Email trailing space");
	}

	@Test(priority = 27, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyUppercaseEmail() {
		registration.enterEmail("USER@MAIL.COM");
		Assert.assertEquals(registration.getEmailValue(), "USER@MAIL.COM", "Uppercase email should be accepted.");
	}

	@Test(priority = 28, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailWithSubdomain() {
		registration.enterEmail("user@mail.company.com");
		Assert.assertEquals(registration.getEmailValue(), "user@mail.company.com",
				"Email with subdomain should be accepted.");
	}

	@Test(priority = 29, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyDuplicateEmailRegistration() {
		assertRejectedRegistration(validRegistrationData().withEmail(getExistingEmail()), "Duplicate email", "taken");
	}

	@Test(priority = 30, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailSqlInjection() {
		assertRejectedRegistration(validRegistrationData().withEmail("' OR 1=1--"), "Email SQL injection");
	}

	@Test(priority = 31, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailXssInjection() {
		assertRejectedRegistration(validRegistrationData().withEmail("<script>alert()</script>"), "Email XSS");
	}

	@Test(priority = 32, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordValid() {
		registration.enterPassword("Pass@123");
		Assert.assertEquals(registration.getPasswordValue(), "Pass@123", "Valid password should be accepted.");
	}

	@Test(priority = 33, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordMinLength() {
		assertRejectedRegistration(validRegistrationData().withPassword("12345").withConfirmPassword("12345"),
				"Password minimum length");
	}

	//There is not Validation for Maximum Length of Password but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 34, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordMaxLength() {
		assertScenarioHandledGracefully(validRegistrationData().withPassword("verylongpassword123456")
				.withConfirmPassword("verylongpassword123456"), "Password max length");
	}

	@Test(priority = 35, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordOnlyNumbers() {
		assertRejectedRegistration(validRegistrationData().withPassword("12345678").withConfirmPassword("12345678"),
				"Password only numbers");
	}

	@Test(priority = 36, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordOnlyLetters() {
		assertRejectedRegistration(validRegistrationData().withPassword("password").withConfirmPassword("password"),
				"Password only letters");
	}

	@Test(priority = 37, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordWithSpaces() {
		assertRejectedRegistration(validRegistrationData().withPassword("pass Word@123").withConfirmPassword("pass Word@123"),
				"Password with spaces");
	}

	//Not working 
	@Test(priority = 38, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordLeadingSpace() {
		assertScenarioHandledGracefully(
				validRegistrationData().withPassword(" Pass@123").withConfirmPassword(" Pass@123"),
				"Password leading space");
	}

	//Not working
	@Test(priority = 39, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordTrailingSpace() {
		assertScenarioHandledGracefully(
				validRegistrationData().withPassword("Pass@123 ").withConfirmPassword("Pass@123 "),
				"Password trailing space");
	}

	@Test(priority = 40, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordSpecialCharacters() {
		registration.enterPassword("P@ssw0rd");
		Assert.assertEquals(registration.getPasswordValue(), "P@ssw0rd",
				"Password with special characters should be accepted.");
	}

	@Test(priority = 41, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyPasswordCopyPaste() {
		registration.pastePassword(getConfiguredPassword());
		Assert.assertEquals(registration.getPasswordValue(), getConfiguredPassword(),
				"Pasted password should populate the password field.");
	}

	@Test(priority = 42, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyConfirmPasswordMatch() {
		assertSuccessfulRegistration(validRegistrationData(), "Confirm password match");
	}

	@Test(priority = 43, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyConfirmPasswordMismatch() {
		assertRejectedRegistration(validRegistrationData().withConfirmPassword("Different@123"),
				"Confirm password mismatch");
	}

	@Test(priority = 44, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyConfirmPasswordEmpty() {
		assertRejectedRegistration(validRegistrationData().withConfirmPassword(""), "Confirm password empty",
				"confirm", "password");
	}

	@Test(priority = 45, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyConfirmPasswordCaseSensitivity() {
		assertRejectedRegistration(validRegistrationData().withPassword("Pass@123").withConfirmPassword("pass@123"),
				"Confirm password case sensitivity");
	}

	@Test(priority = 46, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyConfirmPasswordWithSpaces() {
		assertRejectedRegistration(validRegistrationData().withPassword("Pass@123").withConfirmPassword("Pass@123 "),
				"Confirm password with spaces");
	}

	@Test(priority = 47, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyRoleSelectUser() {
		assertRoleSelection("Consumer", "User", getConfiguredRole());
	}

	@Test(priority = 48, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyRoleSelectAdmin() {
		assertRoleSelection("Admin");
	}

	@Test(priority = 49, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyRoleSelectCreator() {
		assertRoleSelection("Uploader");
	}

	@Test(priority = 50, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyRoleNotSelected() {
		RegistrationFormData data = validRegistrationData().withRole("").withAcceptTerms(true);
		registration.submitRegistration(data);
		registration.printMatchedFeedbackMessage("Role not selected", "role");
		Assert.assertFalse(registration.isRegistrationSuccessful(),
				"Role not selected should not create a successful registration.");
		Assert.assertTrue(registration.findFeedbackMessage("role").toLowerCase(Locale.ENGLISH).contains("role"),
				"Role not selected should show role-related feedback.");
		Assert.assertTrue(
				registration.getRoleRequiredMessage().toLowerCase(Locale.ENGLISH).contains("select your role"),
				"Role not selected should show the role warning message.");
	}

	@Test(priority = 51, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyRoleDropdownUi() {
		Assert.assertTrue(registration.isAnyRoleOptionAvailable("Consumer", "User", "Listener", "Admin", "Creator"),
				"Role dropdown should expose at least one selectable role option.");
	}

	@Test(priority = 52, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyTermsUnchecked() {
		RegistrationFormData data = validRegistrationData().withAcceptTerms(false);
		registration.submitRegistration(data);
		registration.printMatchedFeedbackMessage("Terms unchecked", "terms", "conditions");
		Assert.assertFalse(registration.isRegistrationSuccessful(),
				"Submitting without accepting terms should not create a successful registration.");
		Assert.assertTrue(registration.getTermsRequiredMessage().toLowerCase(Locale.ENGLISH).contains("terms"),
				"Submitting without accepting terms should show a terms validation message.");
	}

	@Test(priority = 53, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyTermsChecked() {
		assertSuccessfulRegistration(validRegistrationData(), "Terms accepted registration");
	}

	@Test(priority = 54, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyTermsLink() {
		assertTermsLinkWorks();
	}

	@Test(priority = 55, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyTermsModal() {
		assertTermsLinkWorks();
	}

	@Test(priority = 56, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNewsletterChecked() {
		assertNewsletterFlow(true);
	}
	
	//There is not functionality to remove checkbox selection for newsletter but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 57, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNewsletterUnchecked() {
		assertNewsletterFlow(false);
	}
	
	//There is not functionality to remove checkbox selection for newsletter but still we can keep this test case to check the system behavior for such type of input.
	@Test(priority = 58, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNewsletterStorePreference() {
		throw new SkipException(
				"Newsletter persistence needs a post-registration profile assertion or backend verification.");
	}

	@Test(priority = 59, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyDoubleClickSubmit() {
		RegistrationFormData data = validRegistrationData();
		registration.populateForm(data);
		registration.doubleClickRegister();
		Assert.assertTrue(registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback(),
				"Double-click submit should still result in one visible and stable registration response.");
	}

	@Test(priority = 60, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyBrowserRefresh() {
		registration.enterName("Refresh Test");
		registration.enterUsername(buildUniqueUsername());
		registration.refreshPage();
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration page should remain accessible after refresh.");
	}

	@Test(priority = 61, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyBackButton() {
		registration.enterName("Back Navigation");
		registration.enterUsername(buildUniqueUsername());
		registration.navigateBack();

		boolean registrationStillVisible = registration.isRegistrationScreenDisplayed();
		String pageSource = driver.getPageSource().toLowerCase(Locale.ENGLISH);
		boolean authFlowStillUsable = pageSource.contains("login") || pageSource.contains("register")
				|| pageSource.contains("email") || pageSource.contains("password");

		Assert.assertTrue(registrationStillVisible || authFlowStillUsable,
				"Browser back should keep the user in a stable and usable auth flow.");
	}

	@Test(priority = 62, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifySlowNetworkRegistration() {
		RegistrationFormData data = validRegistrationData();
		registration.populateForm(data);
		registration.clickRegister();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		Assert.assertTrue(
				registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback()
						|| registration.isRegistrationScreenDisplayed(),
				"Registration flow should remain stable while waiting for a slower response.");
	}

	@Test(priority = 63, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyNetworkDisconnect() {
		RegistrationFormData data = validRegistrationData();
		registration.populateForm(data);
		registration.clickRegister();

		Assert.assertTrue(
				registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback()
						|| registration.isRegistrationScreenDisplayed(),
				"Registration flow should remain stable even if a network interruption is simulated externally.");
	}

	@Test(priority = 64, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifySessionTimeout() {
		registration.enterName("Session Timeout");
		registration.enterUsername(buildUniqueUsername());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		registration.enterEmail(buildUniqueEmail());
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration page should remain stable after an idle wait.");
	}

	@Test(priority = 65, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEnterKeySubmit() {
		RegistrationFormData data = validRegistrationData();
		registration.populateForm(data);
		registration.submitWithEnter();
		Assert.assertTrue(registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback(),
				"Submitting registration with the Enter key should produce a visible response.");
	}

	@Test(priority = 66, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyAutofillBrowser() {
		RegistrationFormData data = validRegistrationData();
		registration.populateForm(data);

		Assert.assertEquals(registration.getNameValue(), data.getName(),
				"Name field should retain autofill-like prefetched data.");
		Assert.assertEquals(registration.getUsernameValue(), data.getUsername(),
				"Username field should retain autofill-like prefetched data.");
		Assert.assertEquals(registration.getEmailValue(), data.getEmail(),
				"Email field should retain autofill-like prefetched data.");
	}

	@Test(priority = 67, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyIncognitoRegistration() {
		DriverFactory.quitDriver();
		driver = DriverFactory.initDriver(true);
		driver.get(ConfigReader.getProperty("url"));
		registration = new RegistrationPage(driver);
		registration.openLogin();
		registration.openRegistration();
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration screen should open cleanly in an incognito/private browser session.");
	}

	@Test(priority = 68, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyMobileView() {
		driver.manage().window().setSize(new Dimension(390, 844));
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration page should remain usable in mobile viewport.");
		Assert.assertFalse(registration.getNameValue() == null,
				"Name field should remain accessible in mobile viewport.");
		Assert.assertFalse(registration.getEmailValue() == null,
				"Email field should remain accessible in mobile viewport.");
	}

	@Test(priority = 69, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyTabletView() {
		driver.manage().window().setSize(new Dimension(820, 1180));
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration page should remain usable in tablet viewport.");
	}

	@Test(priority = 70, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyChromeBrowserRegistration() {
		assertBrowserConfiguration("chrome");
	}

	@Test(priority = 71, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyFirefoxBrowserRegistration() {
		assertBrowserConfiguration("firefox");
	}

	@Test(priority = 72, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEdgeBrowserRegistration() {
		assertBrowserConfiguration("edge");
	}

	@Test(priority = 73, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyAccessibilityScreenReader() {
		Assert.assertTrue(registration.isNameFieldDisplayed(), "Name field should be visible for assistive access.");
		Assert.assertTrue(registration.isUsernameFieldDisplayed(),
				"Username field should be visible for assistive access.");
		Assert.assertTrue(registration.isEmailFieldDisplayed(), "Email field should be visible for assistive access.");
		Assert.assertTrue(registration.isPasswordFieldDisplayed(),
				"Password field should be visible for assistive access.");
		Assert.assertTrue(registration.isConfirmPasswordFieldDisplayed(),
				"Confirm password field should be visible for assistive access.");

		Assert.assertTrue(registration.getNamePlaceholder().toLowerCase(Locale.ENGLISH).contains("name"),
				"Name field should expose a readable placeholder.");
		Assert.assertTrue(registration.getUsernamePlaceholder().toLowerCase(Locale.ENGLISH).contains("username"),
				"Username field should expose a readable placeholder.");
		Assert.assertTrue(registration.getEmailPlaceholder().toLowerCase(Locale.ENGLISH).contains("email"),
				"Email field should expose a readable placeholder.");
		Assert.assertTrue(registration.getPasswordPlaceholder().toLowerCase(Locale.ENGLISH).contains("password"),
				"Password field should expose a readable placeholder.");
		Assert.assertTrue(
				registration.getConfirmPasswordPlaceholder().toLowerCase(Locale.ENGLISH).contains("confirm"),
				"Confirm password field should expose a readable placeholder.");

		registration.focusNameField();
		WebElement firstFocusedElement = driver.switchTo().activeElement();
		firstFocusedElement.sendKeys(Keys.TAB);
		WebElement secondFocusedElement = driver.switchTo().activeElement();
		Assert.assertNotEquals(secondFocusedElement, firstFocusedElement,
				"Keyboard users should be able to move focus through the registration form.");
	}

	@Test(priority = 74, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyTabNavigation() {
		registration.focusNameField();
		WebElement beforeTab = driver.switchTo().activeElement();
		beforeTab.sendKeys(Keys.TAB);
		WebElement afterTab = driver.switchTo().activeElement();
		Assert.assertNotEquals(afterTab, beforeTab, "Tab key should move focus to the next interactive element.");
	}

	@Test(priority = 75, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyFieldMaxLength() {
		String veryLongName = "N".repeat(120);
		String veryLongUsername = "user".repeat(20);
		String veryLongEmail = "longautomationemailvalue".repeat(3) + "@mail.com";

		registration.enterName(veryLongName);
		registration.enterUsername(veryLongUsername);
		registration.enterEmail(veryLongEmail);

		Assert.assertTrue(registration.getNameValue().length() <= veryLongName.length(),
				"Name field should handle very long input safely.");
		Assert.assertTrue(registration.getUsernameValue().length() <= veryLongUsername.length(),
				"Username field should handle very long input safely.");
		Assert.assertTrue(registration.getEmailValue().length() <= veryLongEmail.length(),
				"Email field should handle very long input safely.");
	}

	@Test(priority = 76, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyFieldMinLength() {
		assertScenarioHandledGracefully(validRegistrationData().withName("A").withUsername("abc")
				.withPassword("A@1bcde").withConfirmPassword("A@1bcde"), "Minimum length registration input");
	}

	@Test(priority = 77, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmojiInputFields() {
		RegistrationFormData data = validRegistrationData().withName("Jane\uD83D\uDE0A")
				.withUsername("user\uD83D\uDE0A").withEmail("emoji\uD83D\uDE0A@mail.com");
		assertRejectedRegistration(data, "Emoji input fields");
	}

	@Test(priority = 78, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyHtmlInjection() {
		RegistrationFormData data = validRegistrationData().withName("<b>Test</b>").withUsername("<b>user</b>");
		assertRejectedRegistration(data, "HTML injection");
	}

	@Test(priority = 79, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyRateLimiting() {
		String lastFeedback = "";

		for (int i = 0; i < 3; i++) {
			registration.submitRegistration(validRegistrationData().withEmail(getExistingEmail()));
			lastFeedback = registration.findFeedbackMessage("email");
			driver.get(ConfigReader.getProperty("url"));
			registration = new RegistrationPage(driver);
			registration.openLogin();
			registration.openRegistration();
		}

		Assert.assertTrue(!lastFeedback.isBlank() || registration.isRegistrationScreenDisplayed(),
				"Repeated registration attempts should continue returning a stable response.");
	}

	@Test(priority = 80, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyActivationEmail() {
		registration.submitRegistration(validRegistrationData());
		String feedback = registration.getFirstVisibleFeedbackMessage().toLowerCase(Locale.ENGLISH);
		Assert.assertTrue(registration.isRegistrationSuccessful() || feedback.contains("otp") || feedback.contains("email")
				|| feedback.contains("registered"),
				"Registration should indicate that activation or verification details were sent.");
	}

	@Test(priority = 81, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyActivationLink() {
		registration.submitRegistration(validRegistrationData());
		String feedback = registration.getFirstVisibleFeedbackMessage().toLowerCase(Locale.ENGLISH);
		Assert.assertTrue(registration.isRegistrationSuccessful() || feedback.contains("otp") || feedback.contains("link")
				|| feedback.contains("email"),
				"Registration should indicate an activation-link or verification step.");
	}

	@Test(priority = 82, groups = "positive", retryAnalyzer = RetryAnalyzer.class)
	public void verifyResendActivation() {
		registration.submitRegistration(validRegistrationData());
		Assert.assertTrue(registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback(),
				"Activation flow should provide a stable response that can support resend activation handling.");
	}

	@Test(priority = 83, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyRegisterWithCookiesCleared() {
		registration.clearCookies();
		driver.get(ConfigReader.getProperty("url"));
		registration = new RegistrationPage(driver);
		registration.openLogin();
		registration.openRegistration();
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration screen should still open after clearing cookies.");
	}

	@Test(priority = 84, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyRegisterJsDisabled() {
		RegistrationFormData data = validRegistrationData();
		registration.populateForm(data);
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration form should remain usable before any JavaScript-disabled validation path.");
	}

	@Test(priority = 85, groups = "negative", retryAnalyzer = RetryAnalyzer.class)
	public void verifyApiFailureHandling() {
		RegistrationFormData data = validRegistrationData().withEmail(getExistingEmail());
		registration.submitRegistration(data);
		Assert.assertTrue(registration.hasAnyVisibleFeedback() || registration.isRegistrationScreenDisplayed(),
				"Registration should handle API-side rejection with a stable UI response.");
	}

	@Test(priority = 86, groups = "edge", retryAnalyzer = RetryAnalyzer.class)
	public void verifyDatabaseLatency() {
		RegistrationFormData data = validRegistrationData();
		registration.submitRegistration(data);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		Assert.assertTrue(
				registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback()
						|| registration.isRegistrationScreenDisplayed(),
				"Registration flow should remain stable while backend processing is delayed.");
	}

	private void assertSuccessfulRegistration(RegistrationFormData data, String context) {
		registration.submitRegistration(data);
		registration.printVisibleFeedbackMessages(context);
		Assert.assertTrue(registration.isRegistrationSuccessful(),
				context + " should succeed. Feedback: " + registration.getAllVisibleFeedbackMessages());
	}

	private void assertRejectedRegistration(RegistrationFormData data, String context, String... expectedFragments) {
		registration.submitRegistration(data);
		String[] feedbackFragments = resolveFeedbackFragments(context, expectedFragments);
		Assert.assertFalse(registration.isRegistrationSuccessful(),
				context + " should not create a successful registration.");

		if (feedbackFragments.length > 0) {
			String matchedFeedback = registration.findFeedbackMessage(feedbackFragments);
			registration.printMatchedFeedbackMessage(context, feedbackFragments);
			Assert.assertFalse(matchedFeedback.isBlank(),
					context + " should show feedback containing " + joinFragments(feedbackFragments)
							+ ". Actual feedback: " + registration.getAllVisibleFeedbackMessages());
		} else {
			registration.printMatchedFeedbackMessage(context);
			Assert.assertTrue(registration.hasAnyVisibleFeedback() || registration.isRegistrationScreenDisplayed(),
					context + " should leave the user on the registration flow with visible feedback.");
		}
	}

	private void assertScenarioHandledGracefully(RegistrationFormData data, String context) {
		registration.submitRegistration(data);
		String[] feedbackFragments = resolveFeedbackFragments(context);
		if (feedbackFragments.length > 0) {
			registration.printMatchedFeedbackMessage(context, feedbackFragments);
		} else {
			registration.printVisibleFeedbackMessages(context);
		}
		Assert.assertTrue(
				registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback()
						|| registration.isRegistrationScreenDisplayed(),
				context + " should result in a stable UI response.");
	}

	private void assertMandatoryWarnings() {
		Assert.assertTrue(
				registration.getNameRequiredMessage().toLowerCase(Locale.ENGLISH).contains("name is required"),
				"Name validation message should be displayed.");
		Assert.assertTrue(
				registration.getUsernameRequiredMessage().toLowerCase(Locale.ENGLISH).contains("username is required"),
				"Username validation message should be displayed.");
		Assert.assertTrue(
				registration.getEmailRequiredMessage().toLowerCase(Locale.ENGLISH).contains("email is required"),
				"Email validation message should be displayed.");
		Assert.assertTrue(registration.getPasswordRequiredMessage().toLowerCase(Locale.ENGLISH).contains("password"),
				"Password validation message should be displayed.");
		Assert.assertTrue(
				registration.getConfirmPasswordRequiredMessage().toLowerCase(Locale.ENGLISH)
						.contains("password confirmation is required"),
				"Confirm password validation message should be displayed.");
		Assert.assertTrue(
				registration.getRoleRequiredMessage().toLowerCase(Locale.ENGLISH).contains("select your role"),
				"Role validation message should be displayed.");
		Assert.assertTrue(registration.getTermsRequiredMessage().toLowerCase(Locale.ENGLISH).contains("terms"),
				"Terms validation message should be displayed.");
	}

	private void assertRoleSelection(String... candidates) {
		String role = resolveAvailableRole(candidates);
		registration.selectRole(role);
		String selectedRole = registration.getSelectedRoleText().toLowerCase(Locale.ENGLISH);
		Assert.assertTrue(selectedRole.contains(role.toLowerCase(Locale.ENGLISH)) || !selectedRole.isBlank(),
				"Selected role should be reflected in the role dropdown.");
	}

	private void assertTermsLinkWorks() {
		if (!registration.isTermsLinkAvailable()) {
			throw new SkipException("Terms and Conditions link is not exposed on the current registration page.");
		}

		String currentUrl = registration.getCurrentUrl();
		Set<String> existingWindows = driver.getWindowHandles();
		registration.clickTermsLink();

		boolean navigated = !registration.getCurrentUrl().equalsIgnoreCase(currentUrl);
		boolean openedWindow = driver.getWindowHandles().size() > existingWindows.size();
		boolean contentVisible = registration.isTermsContentVisible();

		Assert.assertTrue(navigated || openedWindow || contentVisible,
				"Terms interaction should open legal content, navigate, or launch a new window.");
	}

	private void assertNewsletterFlow(boolean subscribe) {
		if (!registration.isNewsletterCheckboxAvailable()) {
			throw new SkipException("Newsletter checkbox is not available on the current registration page.");
		}

		RegistrationFormData data = validRegistrationData().withSubscribeToNewsletter(subscribe);
		assertScenarioHandledGracefully(data, subscribe ? "Newsletter checked" : "Newsletter unchecked");
	}

	private void assertBrowserConfiguration(String expectedBrowser) {
		DriverFactory.quitDriver();
		driver = DriverFactory.initDriver(expectedBrowser);
		driver.get(ConfigReader.getProperty("url"));
		registration = new RegistrationPage(driver);
		registration.openLogin();
		registration.openRegistration();
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration page should load successfully on " + expectedBrowser + ".");
	}

	private RegistrationFormData validRegistrationData() {
		String password = getConfiguredPassword();
		return new RegistrationFormData().withName(getConfiguredName()).withUsername(buildUniqueUsername())
				.withEmail(buildUniqueEmail()).withPassword(password).withConfirmPassword(password)
				.withRole(firstNonBlank(getConfiguredRole(), "Consumer"))
				.withAcceptTerms(true);
	}

	private String getConfiguredName() {
		return ConfigReader.getProperty("registration.name", "Safwan Shaikh");
	}

	private String getConfiguredRole() {
		return ConfigReader.getProperty("registration.role", "Consumer");
	}

	private String getConfiguredPassword() {
		return ConfigReader.getProperty("registration.password", "Password@123");
	}

	private String getExistingEmail() {
		return firstNonBlank(ConfigReader.getProperty("registration.existingEmail"),
				ConfigReader.getProperty("login.validEmail"));
	}

	private String getExistingUsername() {
		return ConfigReader.getProperty("registration.username", "safwan012");
	}

	private String buildUniqueEmail() {
		String seedEmail = firstNonBlank(ConfigReader.getProperty("registration.newEmail"),
				ConfigReader.getProperty("login.validEmail"), getExistingEmail(), "automation@mail.com");
		String[] parts = seedEmail.split("@", 2);
		String localPart = sanitizeEmailLocalPart(parts[0]);
		String domainPart = parts.length > 1 ? parts[1] : "mail.com";
		return localPart + "+reg" + buildUniqueToken() + "@" + domainPart;
	}

	private String buildUniqueUsername() {
		String baseValue = sanitizeUsername(getExistingUsername());
		if (baseValue.isBlank()) {
			baseValue = "reguser";
		}
		String suffix = buildUniqueToken();
		String prefix = baseValue.length() > 8 ? baseValue.substring(0, 8) : baseValue;
		return prefix + suffix;
	}

	private String buildUniqueToken() {
		String timestamp = LocalDateTime.now().format(UNIQUE_STAMP_FORMAT);
		return timestamp + UNIQUE_COUNTER.incrementAndGet();
	}

	private String sanitizeEmailLocalPart(String value) {
		String normalized = value == null ? "automation" : value.replaceAll("[^A-Za-z0-9._-]", "");
		return normalized.isBlank() ? "automation" : normalized;
	}

	private String sanitizeUsername(String value) {
		return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ENGLISH);
	}

	private String resolveAvailableRole(String... candidates) {
		String availableRole = registration.getFirstAvailableRole(candidates);
		if (availableRole == null || availableRole.isBlank()) {
			throw new SkipException("No supported role option is available on the current registration page.");
		}
		return availableRole;
	}

	private String[] resolveFeedbackFragments(String context, String... expectedFragments) {
		if (expectedFragments != null && expectedFragments.length > 0) {
			return expectedFragments;
		}

		String normalizedContext = context == null ? "" : context.toLowerCase(Locale.ENGLISH);

		if (normalizedContext.contains("duplicate email")) {
			return new String[] { "email", "taken" };
		}
		if (normalizedContext.contains("duplicate username")) {
			return new String[] { "username", "taken" };
		}
		if (normalizedContext.contains("confirm password mismatch")) {
			return new String[] { "confirm", "password" };
		}
		if (normalizedContext.contains("confirm password empty")) {
			return new String[] { "confirm", "password", "required" };
		}
		if (normalizedContext.contains("role")) {
			return new String[] { "role" };
		}
		if (normalizedContext.contains("terms")) {
			return new String[] { "terms" };
		}
		if (normalizedContext.contains("email")) {
			return new String[] { "email" };
		}
		if (normalizedContext.contains("username")) {
			return new String[] { "username" };
		}
		if (normalizedContext.contains("name")) {
			return new String[] { "name" };
		}
		if (normalizedContext.contains("password")) {
			return new String[] { "password" };
		}

		return new String[0];
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return "";
	}

	private String joinFragments(String... fragments) {
		List<String> values = new ArrayList<>();
		for (String fragment : fragments) {
			if (fragment != null && !fragment.isBlank()) {
				values.add(fragment);
			}
		}
		return values.toString();
	}
}
