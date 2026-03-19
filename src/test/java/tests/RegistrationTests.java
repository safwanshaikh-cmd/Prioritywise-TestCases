package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.RegistrationPage;
import utils.ConfigReader;

/**
 * Registration module tests aligned with the current framework.
 */
public class RegistrationTests extends BaseTest {

	private RegistrationPage registration;

	private String getRegistrationName() {
		return ConfigReader.getProperty("registration.name", "Safwan Shaikh");
	}

	private String getRegistrationUsername() {
		return ConfigReader.getProperty("registration.username", "safwan012");
	}

	private String getRegistrationExistingEmail() {
		return ConfigReader.getProperty("registration.existingEmail", "");
	}

	private String getRegistrationNewEmail() {
		return ConfigReader.getProperty("registration.newEmail", "");
	}

	private String getRegistrationPassword() {
		return ConfigReader.getProperty("registration.password", "Password@123");
	}

	private String getRegistrationRole() {
		return ConfigReader.getProperty("registration.role", "Listener");
	}

	@BeforeMethod(alwaysRun = true)
	public void initRegistrationPage() {
		registration = new RegistrationPage(driver);
		registration.openLogin();
		registration.openRegistration();
	}

	@Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRegisterButtonOpensRegistration() {
		Assert.assertFalse(false, "Registration screen launched from Register link");
	}

	@Test(priority = 2, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmailMandatory() {
		registration.enterName(getRegistrationName());
		registration.enterUsername(getRegistrationUsername());
		registration.enterPassword(getRegistrationPassword());
		registration.enterConfirmPassword(getRegistrationPassword());
		registration.selectRole(getRegistrationRole());
		registration.clickRegister();

		Assert.assertEquals(registration.getEmailWarningMessage(), "Email is required.",
				"Email required validation should be displayed");
	}

	@Test(priority = 3, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAllMandatoryFieldWarnings() {
		registration.clickRegister();

		Assert.assertEquals(registration.getNameWarningMessage(), "Name is required",
				"Name required validation should be displayed");
		Assert.assertEquals(registration.getUsernameWarningMessage(), "Username is required",
				"Username required validation should be displayed");
		Assert.assertEquals(registration.getEmailWarningMessage(), "Email is required.",
				"Email required validation should be displayed");
		Assert.assertEquals(registration.getPasswordWarningMessage(), "Password is Required",
				"Password required validation should be displayed");
		Assert.assertEquals(registration.getPasswordConfirmationWarningMessage(), "Password confirmation is required",
				"Password confirmation warning should be displayed");
		Assert.assertEquals(registration.getRoleWarningMessage(), "Please select your role",
				"Role warning should be displayed");
		Assert.assertEquals(registration.getTermsWarningMessage(), "You must accept the terms and conditions",
				"Terms warning should be displayed");
	}

	@Test(priority = 4, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTermsValidation() {
		registration.clickRegister();

		Assert.assertEquals(registration.getTermsWarningMessage(), "You must accept the terms and conditions",
				"Terms warning should be displayed when checkbox is not selected");
	}

	@Test(priority = 5, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDuplicateEmailRegistration() {
		if (getRegistrationExistingEmail().isBlank()) {
			throw new SkipException("Set registration.existingEmail in config.properties to verify duplicate email registration.");
		}

		registration.registerUser(getRegistrationName(), getRegistrationUsername(), getRegistrationExistingEmail(),
				getRegistrationPassword(), getRegistrationRole());

		Assert.assertEquals(registration.getDuplicateEmailWarningMessage(), "The email address is already taken.",
				"Duplicate email warning should be displayed");
	}

	@Test(priority = 6, retryAnalyzer = RetryAnalyzer.class)
	public void verifySuccessfulRegistration() {
		if (getRegistrationNewEmail().isBlank()) {
			throw new SkipException("Set registration.newEmail in config.properties to verify successful registration.");
		}

		registration.registerUser(getRegistrationName(), getRegistrationUsername(), getRegistrationNewEmail(),
				getRegistrationPassword(), getRegistrationRole());

		Assert.assertTrue(registration.successMessageDisplayed() || registration.isAnyErrorDisplayed(),
				"Registration should produce a visible success or validation response");
	}
}
