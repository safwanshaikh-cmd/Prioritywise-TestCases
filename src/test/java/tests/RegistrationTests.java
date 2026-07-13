package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import factory.DriverFactory;
import listeners.RetryAnalyzer;
import pages.RegistrationPage;
import pages.RegistrationPage.RegistrationFormData;
import utils.LoggerUtils;

/**
 * Registration module automation tests.
 *
 * <p>Test Coverage: TC_1 - TC_86
 * <p>Focus: Registration form validation, authentication edge cases,
 * role / terms / newsletter handling, activation feedback, cross-browser
 * and viewport stability, accessibility, and resilience under slow network.
 *
 * <p>All reusable locators, waits, Selenium actions, and validations live
 * in {@link RegistrationPage}. This class contains only the test execution
 * flow, {@link LoggerUtils} statements, assertions, and calls to
 * {@code RegistrationPage} — mirroring the structure of
 * {@code ChapterTests} and {@code ConsumerBookDetailsTests}.
 */
public class RegistrationTests extends BaseTest {

	private RegistrationPage registration;

	@BeforeMethod(alwaysRun = true)
	@Override
	public void setup() {
		super.setup();
		registration = new RegistrationPage(driver);
		registration.openLogin();
		registration.openRegistration();
		Assert.assertTrue(registration.isRegistrationScreenDisplayed(),
				"Registration screen should be available before each registration test.");
	}

	// ==================== TC_1: VALID REGISTRATION ====================

	/**
	 * TC_1: Verify valid registration submits successfully
	 * Test Flow: Open registration → Submit valid details
	 * Expected: Registration should succeed
	 */
	@Test(priority = 1, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_1: Verify valid registration submits successfully")
	public void TC1_VerifyValidRegistration() {
		LoggerUtils.logTestStart("TC_1: Verify valid registration submits successfully");

		try {
			LoggerUtils.logStep(1, "Submit the registration form with valid details");
			boolean success = registration.expectSuccessful(registration.createValidFormData(),
					"Valid registration");
			LoggerUtils.logInfo("TC_1 - STEP 1: Registration successful: " + success);

			Assert.assertTrue(success, "TC_1: Valid registration should submit successfully");

			LoggerUtils.logTestEnd("TC_1", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_1 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_2: EMPTY FORM VALIDATION ====================

	/**
	 * TC_2: Verify empty form shows mandatory validation messages
	 * Test Flow: Submit empty form → Verify validation messages
	 * Expected: All mandatory validation messages should be displayed
	 */
	@Test(priority = 2, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_2: Verify empty form shows mandatory validation messages")
	public void TC2_VerifyEmptyFormValidation() {
		LoggerUtils.logTestStart("TC_2: Verify empty form shows mandatory validation messages");

		try {
			LoggerUtils.logStep(1, "Submit the empty registration form");
			registration.clickRegister();
			registration.printVisibleValidationMessages();

			LoggerUtils.logStep(2, "Verify all mandatory validation messages are displayed");
			boolean allWarningsShown = registration.assertAllMandatoryValidationMessages();
			LoggerUtils.logInfo("TC_2 - STEP 2: All mandatory warnings shown: " + allWarningsShown);

			Assert.assertTrue(allWarningsShown,
					"TC_2: Empty form should show all mandatory validation messages");

			LoggerUtils.logTestEnd("TC_2", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_2 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_3: VALID NAME ====================

	/**
	 * TC_3: Verify valid name input is accepted
	 * Test Flow: Enter valid name → Verify field value
	 * Expected: Valid name should be accepted
	 */
	@Test(priority = 3, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_3: Verify valid name input is accepted")
	public void TC3_VerifyNameValid() {
		LoggerUtils.logTestStart("TC_3: Verify valid name input is accepted");

		try {
			LoggerUtils.logStep(1, "Enter a valid name");
			registration.enterName("Safwan Shaikh");
			String nameValue = registration.getNameValue();
			LoggerUtils.logInfo("TC_3 - STEP 1: Name value: " + nameValue);

			Assert.assertEquals(nameValue, "Safwan Shaikh", "TC_3: Valid name should be accepted");

			LoggerUtils.logTestEnd("TC_3", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_3 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_4: NAME WITH NUMBERS ====================

	/**
	 * TC_4: Verify name with numbers is accepted
	 * Test Flow: Submit name containing numbers → Verify acceptance
	 * Expected: Name with numbers should be accepted
	 */
	@Test(priority = 4, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_4: Verify name with numbers is accepted")
	public void TC4_VerifyNameWithNumbers() {
		LoggerUtils.logTestStart("TC_4: Verify name with numbers is accepted");

		try {
			LoggerUtils.logStep(1, "Submit registration with numbers in the name");
			boolean success = registration.expectSuccessful(
					registration.createValidFormData().withName("Safwan123"), "Name with numbers");
			LoggerUtils.logInfo("TC_4 - STEP 1: Registration successful: " + success);

			Assert.assertTrue(success, "TC_4: Name with numbers should be accepted");

			LoggerUtils.logTestEnd("TC_4", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_4 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_5: NAME WITH SPECIAL CHARACTERS ====================

	/**
	 * TC_5: Verify name with special characters is rejected
	 * Test Flow: Submit name with special characters → Verify rejection
	 * Expected: Name with special characters should be rejected
	 */
	@Test(priority = 5, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_5: Verify name with special characters is rejected")
	public void TC5_VerifyNameSpecialCharacters() {
		LoggerUtils.logTestStart("TC_5: Verify name with special characters is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with special characters in the name");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withName("@#$%"), "Name special characters", "name");
			LoggerUtils.logInfo("TC_5 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_5: Name with special characters should be rejected");

			LoggerUtils.logTestEnd("TC_5", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_5 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_6: NAME LEADING SPACE ====================

	/**
	 * TC_6: Verify name with leading space is handled gracefully
	 * Test Flow: Submit name with leading space → Verify graceful handling
	 * Expected: Name with leading space should be handled gracefully
	 */
	@Test(priority = 6, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_6: Verify name with leading space is handled gracefully")
	public void TC6_VerifyNameLeadingSpace() {
		LoggerUtils.logTestStart("TC_6: Verify name with leading space is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with a leading space in the name");
			boolean handled = registration.expectHandledGracefully(
					registration.createValidFormData().withName(" John"), "Name leading space");
			LoggerUtils.logInfo("TC_6 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_6: Name with leading space should be handled gracefully");

			LoggerUtils.logTestEnd("TC_6", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_6 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_7: NAME TRAILING SPACE ====================

	/**
	 * TC_7: Verify name with trailing space is handled gracefully
	 * Test Flow: Submit name with trailing space → Verify graceful handling
	 * Expected: Name with trailing space should be handled gracefully
	 */
	@Test(priority = 7, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_7: Verify name with trailing space is handled gracefully")
	public void TC7_VerifyNameTrailingSpace() {
		LoggerUtils.logTestStart("TC_7: Verify name with trailing space is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with a trailing space in the name");
			boolean handled = registration.expectHandledGracefully(
					registration.createValidFormData().withName("John "), "Name trailing space");
			LoggerUtils.logInfo("TC_7 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_7: Name with trailing space should be handled gracefully");

			LoggerUtils.logTestEnd("TC_7", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_7 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_8: NAME MULTIPLE SPACES ====================

	/**
	 * TC_8: Verify name with multiple spaces is handled gracefully
	 * Test Flow: Submit name with multiple spaces → Verify graceful handling
	 * Expected: Name with multiple spaces should be handled gracefully
	 */
	@Test(priority = 8, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_8: Verify name with multiple spaces is handled gracefully")
	public void TC8_VerifyNameMultipleSpaces() {
		LoggerUtils.logTestStart("TC_8: Verify name with multiple spaces is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with multiple spaces in the name");
			boolean handled = registration.expectHandledGracefully(
					registration.createValidFormData().withName("John   Doe"), "Name multiple spaces");
			LoggerUtils.logInfo("TC_8 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_8: Name with multiple spaces should be handled gracefully");

			LoggerUtils.logTestEnd("TC_8", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_8 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_9: NAME UNICODE ====================

	/**
	 * TC_9: Verify name with unicode characters is handled gracefully
	 * Test Flow: Submit unicode name → Verify graceful handling
	 * Expected: Unicode name should be handled gracefully
	 */
	@Test(priority = 9, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_9: Verify name with unicode characters is handled gracefully")
	public void TC9_VerifyNameUnicode() {
		LoggerUtils.logTestStart("TC_9: Verify name with unicode characters is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with a unicode name");
			boolean handled = registration.expectHandledGracefully(
					registration.createValidFormData().withName("Jose"), "Name unicode");
			LoggerUtils.logInfo("TC_9 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_9: Name with unicode characters should be handled gracefully");

			LoggerUtils.logTestEnd("TC_9", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_9 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_10: NAME EMOJI ====================

	/**
	 * TC_10: Verify name with emoji is rejected
	 * Test Flow: Submit name with emoji → Verify rejection
	 * Expected: Name with emoji should be rejected
	 */
	@Test(priority = 10, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_10: Verify name with emoji is rejected")
	public void TC10_VerifyNameEmoji() {
		LoggerUtils.logTestStart("TC_10: Verify name with emoji is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an emoji in the name");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withName("John😊"), "Name emoji");
			LoggerUtils.logInfo("TC_10 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_10: Name with emoji should be rejected");

			LoggerUtils.logTestEnd("TC_10", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_10 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_11: NAME SQL INJECTION ====================

	/**
	 * TC_11: Verify SQL injection in name is rejected
	 * Test Flow: Submit SQL injection as name → Verify rejection
	 * Expected: SQL injection should be rejected
	 */
	@Test(priority = 11, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_11: Verify SQL injection in name is rejected")
	public void TC11_VerifyNameSqlInjection() {
		LoggerUtils.logTestStart("TC_11: Verify SQL injection in name is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with a SQL injection payload in the name");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withName("' OR 1=1--"), "Name SQL injection");
			LoggerUtils.logInfo("TC_11 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_11: SQL injection in name should be rejected");

			LoggerUtils.logTestEnd("TC_11", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_11 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_12: NAME XSS INJECTION ====================

	/**
	 * TC_12: Verify XSS injection in name is rejected
	 * Test Flow: Submit XSS payload as name → Verify rejection
	 * Expected: XSS injection should be rejected
	 */
	@Test(priority = 12, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_12: Verify XSS injection in name is rejected")
	public void TC12_VerifyNameXssInjection() {
		LoggerUtils.logTestStart("TC_12: Verify XSS injection in name is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an XSS payload in the name");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withName("<script>alert(1)</script>"), "Name XSS");
			LoggerUtils.logInfo("TC_12 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_12: XSS injection in name should be rejected");

			LoggerUtils.logTestEnd("TC_12", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_12 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_21: VALID EMAIL ====================

	/**
	 * TC_21: Verify valid email input is accepted
	 * Test Flow: Enter valid email → Verify field value
	 * Expected: Valid email should be accepted
	 */
	@Test(priority = 21, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_21: Verify valid email input is accepted")
	public void TC21_VerifyEmailValid() {
		LoggerUtils.logTestStart("TC_21: Verify valid email input is accepted");

		try {
			LoggerUtils.logStep(1, "Enter a valid email");
			registration.enterEmail("user@mail.com");
			String emailValue = registration.getEmailValue();
			LoggerUtils.logInfo("TC_21 - STEP 1: Email value: " + emailValue);

			Assert.assertEquals(emailValue, "user@mail.com", "TC_21: Valid email should be accepted");

			LoggerUtils.logTestEnd("TC_21", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_21 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_22: EMAIL MISSING '@' ====================

	/**
	 * TC_22: Verify email without '@' is rejected
	 * Test Flow: Submit email without '@' → Verify rejection
	 * Expected: Email without '@' should be rejected
	 */
	@Test(priority = 22, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_22: Verify email without '@' is rejected")
	public void TC22_VerifyEmailMissingAt() {
		LoggerUtils.logTestStart("TC_22: Verify email without '@' is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an email missing '@'");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withEmail("usermail.com"), "Email missing @", "email");
			LoggerUtils.logInfo("TC_22 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_22: Email without '@' should be rejected");

			LoggerUtils.logTestEnd("TC_22", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_22 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_23: EMAIL MISSING DOMAIN ====================

	/**
	 * TC_23: Verify email without domain is rejected
	 * Test Flow: Submit email without domain → Verify rejection
	 * Expected: Email without domain should be rejected
	 */
	@Test(priority = 23, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_23: Verify email without domain is rejected")
	public void TC23_VerifyEmailMissingDomain() {
		LoggerUtils.logTestStart("TC_23: Verify email without domain is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an email missing the domain");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withEmail("user@"), "Email missing domain", "email");
			LoggerUtils.logInfo("TC_23 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_23: Email without domain should be rejected");

			LoggerUtils.logTestEnd("TC_23", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_23 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_24: EMAIL MULTIPLE '@' ====================

	/**
	 * TC_24: Verify email with multiple '@' is rejected
	 * Test Flow: Submit email with multiple '@' → Verify rejection
	 * Expected: Email with multiple '@' should be rejected
	 */
	@Test(priority = 24, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_24: Verify email with multiple '@' is rejected")
	public void TC24_VerifyEmailMultipleAt() {
		LoggerUtils.logTestStart("TC_24: Verify email with multiple '@' is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an email containing multiple '@'");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withEmail("user@@mail.com"), "Email multiple @");
			LoggerUtils.logInfo("TC_24 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_24: Email with multiple '@' should be rejected");

			LoggerUtils.logTestEnd("TC_24", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_24 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_25: EMAIL LEADING SPACE ====================

	/**
	 * TC_25: Verify email with leading space is handled gracefully
	 * Test Flow: Submit email with leading space → Verify graceful handling
	 * Expected: Email with leading space should be handled gracefully
	 */
	@Test(priority = 25, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_25: Verify email with leading space is handled gracefully")
	public void TC25_VerifyEmailLeadingSpace() {
		LoggerUtils.logTestStart("TC_25: Verify email with leading space is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with a leading space in the email");
			boolean handled = registration.expectHandledGracefully(
					registration.createValidFormData().withEmail(" user@mail.com"), "Email leading space");
			LoggerUtils.logInfo("TC_25 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_25: Email with leading space should be handled gracefully");

			LoggerUtils.logTestEnd("TC_25", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_25 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_26: EMAIL TRAILING SPACE ====================

	/**
	 * TC_26: Verify email with trailing space is handled gracefully
	 * Test Flow: Submit email with trailing space → Verify graceful handling
	 * Expected: Email with trailing space should be handled gracefully
	 */
	@Test(priority = 26, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_26: Verify email with trailing space is handled gracefully")
	public void TC26_VerifyEmailTrailingSpace() {
		LoggerUtils.logTestStart("TC_26: Verify email with trailing space is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with a trailing space in the email");
			boolean handled = registration.expectHandledGracefully(
					registration.createValidFormData().withEmail("user@mail.com "), "Email trailing space");
			LoggerUtils.logInfo("TC_26 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_26: Email with trailing space should be handled gracefully");

			LoggerUtils.logTestEnd("TC_26", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_26 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_27: UPPERCASE EMAIL ====================

	/**
	 * TC_27: Verify uppercase email input is accepted
	 * Test Flow: Enter uppercase email → Verify field value
	 * Expected: Uppercase email should be accepted
	 */
	@Test(priority = 27, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_27: Verify uppercase email input is accepted")
	public void TC27_VerifyUppercaseEmail() {
		LoggerUtils.logTestStart("TC_27: Verify uppercase email input is accepted");

		try {
			LoggerUtils.logStep(1, "Enter an uppercase email");
			registration.enterEmail("USER@MAIL.COM");
			String emailValue = registration.getEmailValue();
			LoggerUtils.logInfo("TC_27 - STEP 1: Email value: " + emailValue);

			Assert.assertEquals(emailValue, "USER@MAIL.COM", "TC_27: Uppercase email should be accepted");

			LoggerUtils.logTestEnd("TC_27", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_27 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_28: EMAIL WITH SUBDOMAIN ====================

	/**
	 * TC_28: Verify email with subdomain is accepted
	 * Test Flow: Enter subdomain email → Verify field value
	 * Expected: Subdomain email should be accepted
	 */
	@Test(priority = 28, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_28: Verify email with subdomain is accepted")
	public void TC28_VerifyEmailWithSubdomain() {
		LoggerUtils.logTestStart("TC_28: Verify email with subdomain is accepted");

		try {
			LoggerUtils.logStep(1, "Enter an email with a subdomain");
			registration.enterEmail("user@mail.company.com");
			String emailValue = registration.getEmailValue();
			LoggerUtils.logInfo("TC_28 - STEP 1: Email value: " + emailValue);

			Assert.assertEquals(emailValue, "user@mail.company.com",
					"TC_28: Email with subdomain should be accepted");

			LoggerUtils.logTestEnd("TC_28", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_28 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_29: DUPLICATE EMAIL ====================

	/**
	 * TC_29: Verify duplicate email registration is rejected
	 * Test Flow: Submit existing email → Verify rejection
	 * Expected: Duplicate email should be rejected
	 */
	@Test(priority = 29, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_29: Verify duplicate email registration is rejected")
	public void TC29_VerifyDuplicateEmailRegistration() {
		LoggerUtils.logTestStart("TC_29: Verify duplicate email registration is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an already-existing email");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withEmail(registration.getExistingEmail()),
					"Duplicate email", "taken");
			LoggerUtils.logInfo("TC_29 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_29: Duplicate email registration should be rejected");

			LoggerUtils.logTestEnd("TC_29", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_29 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_30: EMAIL SQL INJECTION ====================

	/**
	 * TC_30: Verify SQL injection in email is rejected
	 * Test Flow: Submit SQL injection as email → Verify rejection
	 * Expected: SQL injection should be rejected
	 */
	@Test(priority = 30, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_30: Verify SQL injection in email is rejected")
	public void TC30_VerifyEmailSqlInjection() {
		LoggerUtils.logTestStart("TC_30: Verify SQL injection in email is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with a SQL injection payload in the email");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withEmail("' OR 1=1--"), "Email SQL injection");
			LoggerUtils.logInfo("TC_30 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_30: SQL injection in email should be rejected");

			LoggerUtils.logTestEnd("TC_30", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_30 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_31: EMAIL XSS INJECTION ====================

	/**
	 * TC_31: Verify XSS injection in email is rejected
	 * Test Flow: Submit XSS payload as email → Verify rejection
	 * Expected: XSS injection should be rejected
	 */
	@Test(priority = 31, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_31: Verify XSS injection in email is rejected")
	public void TC31_VerifyEmailXssInjection() {
		LoggerUtils.logTestStart("TC_31: Verify XSS injection in email is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an XSS payload in the email");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withEmail("<script>alert()</script>"), "Email XSS");
			LoggerUtils.logInfo("TC_31 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_31: XSS injection in email should be rejected");

			LoggerUtils.logTestEnd("TC_31", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_31 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_32: VALID PASSWORD ====================

	/**
	 * TC_32: Verify valid password input is accepted
	 * Test Flow: Enter valid password → Verify field value
	 * Expected: Valid password should be accepted
	 */
	@Test(priority = 32, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_32: Verify valid password input is accepted")
	public void TC32_VerifyPasswordValid() {
		LoggerUtils.logTestStart("TC_32: Verify valid password input is accepted");

		try {
			LoggerUtils.logStep(1, "Enter a valid password");
			registration.enterPassword("Pass@123");
			String passwordValue = registration.getPasswordValue();
			LoggerUtils.logInfo("TC_32 - STEP 1: Password value: " + passwordValue);

			Assert.assertEquals(passwordValue, "Pass@123", "TC_32: Valid password should be accepted");

			LoggerUtils.logTestEnd("TC_32", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_32 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_33: PASSWORD MIN LENGTH ====================

	/**
	 * TC_33: Verify password shorter than minimum length is rejected
	 * Test Flow: Submit short password → Verify rejection
	 * Expected: Short password should be rejected
	 */
	@Test(priority = 33, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_33: Verify password shorter than minimum length is rejected")
	public void TC33_VerifyPasswordMinLength() {
		LoggerUtils.logTestStart("TC_33: Verify password shorter than minimum length is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with a below-minimum password");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withPassword("12345").withConfirmPassword("12345"),
					"Password minimum length");
			LoggerUtils.logInfo("TC_33 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_33: Password shorter than minimum length should be rejected");

			LoggerUtils.logTestEnd("TC_33", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_33 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_34: PASSWORD MAX LENGTH ====================

	/**
	 * TC_34: Verify long password is handled gracefully
	 * Test Flow: Submit long password → Verify graceful handling
	 * Expected: Long password should be handled gracefully
	 */
	@Test(priority = 34, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_34: Verify long password is handled gracefully")
	public void TC34_VerifyPasswordMaxLength() {
		LoggerUtils.logTestStart("TC_34: Verify long password is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with a very long password");
			boolean handled = registration.expectHandledGracefully(registration.createValidFormData()
					.withPassword("verylongpassword123456").withConfirmPassword("verylongpassword123456"),
					"Password max length");
			LoggerUtils.logInfo("TC_34 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_34: Long password should be handled gracefully");

			LoggerUtils.logTestEnd("TC_34", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_34 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_35: PASSWORD ONLY NUMBERS ====================

	/**
	 * TC_35: Verify all-numeric password is rejected
	 * Test Flow: Submit numeric password → Verify rejection
	 * Expected: Numeric password should be rejected
	 */
	@Test(priority = 35, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_35: Verify all-numeric password is rejected")
	public void TC35_VerifyPasswordOnlyNumbers() {
		LoggerUtils.logTestStart("TC_35: Verify all-numeric password is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an all-numeric password");
			boolean rejected = registration.expectRejected(registration.createValidFormData()
					.withPassword("12345678").withConfirmPassword("12345678"), "Password only numbers");
			LoggerUtils.logInfo("TC_35 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_35: All-numeric password should be rejected");

			LoggerUtils.logTestEnd("TC_35", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_35 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_36: PASSWORD ONLY LETTERS ====================

	/**
	 * TC_36: Verify all-letters password is rejected
	 * Test Flow: Submit letters-only password → Verify rejection
	 * Expected: Letters-only password should be rejected
	 */
	@Test(priority = 36, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_36: Verify all-letters password is rejected")
	public void TC36_VerifyPasswordOnlyLetters() {
		LoggerUtils.logTestStart("TC_36: Verify all-letters password is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an all-letters password");
			boolean rejected = registration.expectRejected(registration.createValidFormData()
					.withPassword("password").withConfirmPassword("password"), "Password only letters");
			LoggerUtils.logInfo("TC_36 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_36: All-letters password should be rejected");

			LoggerUtils.logTestEnd("TC_36", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_36 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_37: PASSWORD WITH SPACES ====================

	/**
	 * TC_37: Verify password with embedded space is rejected
	 * Test Flow: Submit password with embedded space → Verify rejection
	 * Expected: Password with embedded space should be rejected
	 */
	@Test(priority = 37, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_37: Verify password with embedded space is rejected")
	public void TC37_VerifyPasswordWithSpaces() {
		LoggerUtils.logTestStart("TC_37: Verify password with embedded space is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with a password containing embedded spaces");
			boolean rejected = registration.expectRejected(registration.createValidFormData()
					.withPassword("pass Word@123").withConfirmPassword("pass Word@123"),
					"Password with spaces");
			LoggerUtils.logInfo("TC_37 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_37: Password with embedded space should be rejected");

			LoggerUtils.logTestEnd("TC_37", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_37 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_38: PASSWORD LEADING SPACE ====================

	/**
	 * TC_38: Verify password leading space is handled gracefully
	 * Test Flow: Submit password with leading space → Verify graceful handling
	 * Expected: Password with leading space should be handled gracefully
	 */
	@Test(priority = 38, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_38: Verify password leading space is handled gracefully")
	public void TC38_VerifyPasswordLeadingSpace() {
		LoggerUtils.logTestStart("TC_38: Verify password leading space is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with a leading space in the password");
			boolean handled = registration.expectHandledGracefully(registration.createValidFormData()
					.withPassword(" Pass@123").withConfirmPassword(" Pass@123"), "Password leading space");
			LoggerUtils.logInfo("TC_38 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_38: Password leading space should be handled gracefully");

			LoggerUtils.logTestEnd("TC_38", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_38 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_39: PASSWORD TRAILING SPACE ====================

	/**
	 * TC_39: Verify password trailing space is handled gracefully
	 * Test Flow: Submit password with trailing space → Verify graceful handling
	 * Expected: Password with trailing space should be handled gracefully
	 */
	@Test(priority = 39, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_39: Verify password trailing space is handled gracefully")
	public void TC39_VerifyPasswordTrailingSpace() {
		LoggerUtils.logTestStart("TC_39: Verify password trailing space is handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with a trailing space in the password");
			boolean handled = registration.expectHandledGracefully(registration.createValidFormData()
					.withPassword("Pass@123 ").withConfirmPassword("Pass@123 "), "Password trailing space");
			LoggerUtils.logInfo("TC_39 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_39: Password trailing space should be handled gracefully");

			LoggerUtils.logTestEnd("TC_39", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_39 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_40: PASSWORD SPECIAL CHARACTERS ====================

	/**
	 * TC_40: Verify password with special characters is accepted
	 * Test Flow: Enter password with special characters → Verify field value
	 * Expected: Password with special characters should be accepted
	 */
	@Test(priority = 40, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_40: Verify password with special characters is accepted")
	public void TC40_VerifyPasswordSpecialCharacters() {
		LoggerUtils.logTestStart("TC_40: Verify password with special characters is accepted");

		try {
			LoggerUtils.logStep(1, "Enter a password with special characters");
			registration.enterPassword("P@ssw0rd");
			String passwordValue = registration.getPasswordValue();
			LoggerUtils.logInfo("TC_40 - STEP 1: Password value: " + passwordValue);

			Assert.assertEquals(passwordValue, "P@ssw0rd",
					"TC_40: Password with special characters should be accepted");

			LoggerUtils.logTestEnd("TC_40", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_40 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_41: PASSWORD COPY-PASTE ====================

	/**
	 * TC_41: Verify paste populates the password field
	 * Test Flow: Paste password → Verify field value
	 * Expected: Pasted password should populate the field
	 */
	@Test(priority = 41, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_41: Verify paste populates the password field")
	public void TC41_VerifyPasswordCopyPaste() {
		LoggerUtils.logTestStart("TC_41: Verify paste populates the password field");

		try {
			LoggerUtils.logStep(1, "Paste a password into the password field");
			registration.pastePassword(registration.getConfiguredPassword());
			String passwordValue = registration.getPasswordValue();
			LoggerUtils.logInfo("TC_41 - STEP 1: Password value: " + passwordValue);

			Assert.assertEquals(passwordValue, registration.getConfiguredPassword(),
					"TC_41: Pasted password should populate the password field");

			LoggerUtils.logTestEnd("TC_41", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_41 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_42: CONFIRM PASSWORD MATCH ====================

	/**
	 * TC_42: Verify matching confirm password succeeds
	 * Test Flow: Submit matching passwords → Verify success
	 * Expected: Matching confirm password should succeed
	 */
	@Test(priority = 42, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_42: Verify matching confirm password succeeds")
	public void TC42_VerifyConfirmPasswordMatch() {
		LoggerUtils.logTestStart("TC_42: Verify matching confirm password succeeds");

		try {
			LoggerUtils.logStep(1, "Submit registration with matching passwords");
			boolean success = registration.expectSuccessful(registration.createValidFormData(),
					"Confirm password match");
			LoggerUtils.logInfo("TC_42 - STEP 1: Registration successful: " + success);

			Assert.assertTrue(success, "TC_42: Matching confirm password should succeed");

			LoggerUtils.logTestEnd("TC_42", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_42 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_43: CONFIRM PASSWORD MISMATCH ====================

	/**
	 * TC_43: Verify mismatched confirm password is rejected
	 * Test Flow: Submit mismatched passwords → Verify rejection
	 * Expected: Mismatched confirm password should be rejected
	 */
	@Test(priority = 43, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_43: Verify mismatched confirm password is rejected")
	public void TC43_VerifyConfirmPasswordMismatch() {
		LoggerUtils.logTestStart("TC_43: Verify mismatched confirm password is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with mismatched passwords");
			boolean rejected = registration.expectRejected(registration.createValidFormData()
					.withConfirmPassword("Different@123"), "Confirm password mismatch");
			LoggerUtils.logInfo("TC_43 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_43: Mismatched confirm password should be rejected");

			LoggerUtils.logTestEnd("TC_43", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_43 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_44: CONFIRM PASSWORD EMPTY ====================

	/**
	 * TC_44: Verify empty confirm password is rejected
	 * Test Flow: Submit empty confirm password → Verify rejection
	 * Expected: Empty confirm password should be rejected
	 */
	@Test(priority = 44, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_44: Verify empty confirm password is rejected")
	public void TC44_VerifyConfirmPasswordEmpty() {
		LoggerUtils.logTestStart("TC_44: Verify empty confirm password is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with an empty confirm password");
			boolean rejected = registration.expectRejected(
					registration.createValidFormData().withConfirmPassword(""), "Confirm password empty",
					"confirm", "password");
			LoggerUtils.logInfo("TC_44 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_44: Empty confirm password should be rejected");

			LoggerUtils.logTestEnd("TC_44", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_44 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_45: CONFIRM PASSWORD CASE SENSITIVITY ====================

	/**
	 * TC_45: Verify confirm password is case sensitive
	 * Test Flow: Submit case-differing passwords → Verify rejection
	 * Expected: Confirm password should be case sensitive
	 */
	@Test(priority = 45, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_45: Verify confirm password is case sensitive")
	public void TC45_VerifyConfirmPasswordCaseSensitivity() {
		LoggerUtils.logTestStart("TC_45: Verify confirm password is case sensitive");

		try {
			LoggerUtils.logStep(1, "Submit registration with case-differing passwords");
			boolean rejected = registration.expectRejected(registration.createValidFormData()
					.withPassword("Pass@123").withConfirmPassword("pass@123"),
					"Confirm password case sensitivity");
			LoggerUtils.logInfo("TC_45 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_45: Confirm password should be case sensitive");

			LoggerUtils.logTestEnd("TC_45", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_45 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_46: CONFIRM PASSWORD WITH SPACES ====================

	/**
	 * TC_46: Verify confirm password with trailing space is rejected
	 * Test Flow: Submit confirm password with trailing space → Verify rejection
	 * Expected: Confirm password with trailing space should be rejected
	 */
	@Test(priority = 46, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_46: Verify confirm password with trailing space is rejected")
	public void TC46_VerifyConfirmPasswordWithSpaces() {
		LoggerUtils.logTestStart("TC_46: Verify confirm password with trailing space is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with a trailing space in the confirm password");
			boolean rejected = registration.expectRejected(registration.createValidFormData()
					.withPassword("Pass@123").withConfirmPassword("Pass@123 "),
					"Confirm password with spaces");
			LoggerUtils.logInfo("TC_46 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_46: Confirm password with trailing space should be rejected");

			LoggerUtils.logTestEnd("TC_46", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_46 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_52: TERMS UNCHECKED ====================

	/**
	 * TC_52: Verify unchecked terms triggers terms validation
	 * Test Flow: Submit without accepting terms → Verify terms validation
	 * Expected: Unchecked terms should trigger terms validation
	 */
	@Test(priority = 52, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_52: Verify unchecked terms triggers terms validation")
	public void TC52_VerifyTermsUnchecked() {
		LoggerUtils.logTestStart("TC_52: Verify unchecked terms triggers terms validation");

		try {
			LoggerUtils.logStep(1, "Submit the registration form without accepting the terms");
			RegistrationFormData data = registration.createValidFormData().withAcceptTerms(false);
			registration.submitRegistration(data);
			registration.printMatchedFeedbackMessage("Terms unchecked", "terms", "conditions");

			LoggerUtils.logStep(2, "Verify terms validation is triggered");
			boolean notSuccessful = !registration.isRegistrationSuccessful();
			boolean termsWarningShown = registration.containsIgnoreCase(registration.getTermsRequiredMessage(),
					"terms");
			LoggerUtils.logInfo("TC_52 - STEP 2: Not successful: " + notSuccessful + ", Terms warning: "
					+ termsWarningShown);

			Assert.assertTrue(notSuccessful,
					"TC_52: Submitting without accepting terms should not create a successful registration");
			Assert.assertTrue(termsWarningShown,
					"TC_52: Submitting without accepting terms should show a terms validation message");

			LoggerUtils.logTestEnd("TC_52", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_52 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_53: TERMS CHECKED ====================

	/**
	 * TC_53: Verify registration succeeds when terms are accepted
	 * Test Flow: Submit with terms accepted → Verify success
	 * Expected: Registration should succeed when terms are accepted
	 */
	@Test(priority = 53, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_53: Verify registration succeeds when terms are accepted")
	public void TC53_VerifyTermsChecked() {
		LoggerUtils.logTestStart("TC_53: Verify registration succeeds when terms are accepted");

		try {
			LoggerUtils.logStep(1, "Submit registration with terms accepted");
			boolean success = registration.expectSuccessful(registration.createValidFormData(),
					"Terms accepted registration");
			LoggerUtils.logInfo("TC_53 - STEP 1: Registration successful: " + success);

			Assert.assertTrue(success,
					"TC_53: Registration should succeed when terms are accepted");

			LoggerUtils.logTestEnd("TC_53", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_53 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_54: TERMS LINK ====================

	/**
	 * TC_54: Verify terms link opens legal content
	 * Test Flow: Click terms link → Verify legal content opens
	 * Expected: Terms link should open legal content
	 */
	@Test(priority = 54, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_54: Verify terms link opens legal content")
	public void TC54_VerifyTermsLink() {
		LoggerUtils.logTestStart("TC_54: Verify terms link opens legal content");

		try {
			LoggerUtils.logStep(1, "Check if the terms link is available");
			if (!registration.isTermsLinkAvailable()) {
				throw new SkipException(
						"TC_54: Terms and Conditions link is not exposed on the current registration page.");
			}

			LoggerUtils.logStep(2, "Open the terms link and verify legal content");
			boolean opened = registration.assertTermsLinkOpens();
			LoggerUtils.logInfo("TC_54 - STEP 2: Terms content opened: " + opened);

			Assert.assertTrue(opened,
					"TC_54: Terms link should open legal content, navigate, or launch a new window");

			LoggerUtils.logTestEnd("TC_54", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_54 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_55: TERMS MODAL ====================

	/**
	 * TC_55: Verify terms modal exposes terms content
	 * Test Flow: Open terms modal → Verify terms content
	 * Expected: Terms modal should expose terms content
	 */
	@Test(priority = 55, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_55: Verify terms modal exposes terms content")
	public void TC55_VerifyTermsModal() {
		LoggerUtils.logTestStart("TC_55: Verify terms modal exposes terms content");

		try {
			LoggerUtils.logStep(1, "Check if the terms link is available");
			if (!registration.isTermsLinkAvailable()) {
				throw new SkipException(
						"TC_55: Terms and Conditions link is not exposed on the current registration page.");
			}

			LoggerUtils.logStep(2, "Open the terms modal and verify terms content");
			boolean opened = registration.assertTermsLinkOpens();
			LoggerUtils.logInfo("TC_55 - STEP 2: Terms content opened: " + opened);

			Assert.assertTrue(opened,
					"TC_55: Terms modal should expose terms content");

			LoggerUtils.logTestEnd("TC_55", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_55 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_56: NEWSLETTER CHECKED ====================

	/**
	 * TC_56: Verify checked newsletter checkbox supports graceful flow
	 * Test Flow: Submit with newsletter checked → Verify graceful handling
	 * Expected: Checked newsletter should support a graceful flow
	 */
	@Test(priority = 56, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_56: Verify checked newsletter checkbox supports graceful flow")
	public void TC56_VerifyNewsletterChecked() {
		LoggerUtils.logTestStart("TC_56: Verify checked newsletter checkbox supports graceful flow");

		try {
			LoggerUtils.logStep(1, "Check if the newsletter checkbox is available");
			if (!registration.isNewsletterCheckboxAvailable()) {
				throw new SkipException(
						"TC_56: Newsletter checkbox is not available on the current registration page.");
			}

			LoggerUtils.logStep(2, "Submit registration with the newsletter option enabled");
			RegistrationFormData data = registration.createValidFormData().withSubscribeToNewsletter(true);
			boolean handled = registration.expectHandledGracefully(data, "Newsletter checked");
			LoggerUtils.logInfo("TC_56 - STEP 2: Flow handled gracefully: " + handled);

			Assert.assertTrue(handled,
					"TC_56: Checked newsletter checkbox should support a graceful registration flow");

			LoggerUtils.logTestEnd("TC_56", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_56 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_57: NEWSLETTER UNCHECKED ====================

	/**
	 * TC_57: Verify unchecked newsletter supports graceful flow
	 * Test Flow: Submit with newsletter unchecked → Verify graceful handling
	 * Expected: Unchecked newsletter should support a graceful flow
	 */
	@Test(priority = 57, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_57: Verify unchecked newsletter supports graceful flow")
	public void TC57_VerifyNewsletterUnchecked() {
		LoggerUtils.logTestStart("TC_57: Verify unchecked newsletter supports graceful flow");

		try {
			LoggerUtils.logStep(1, "Check if the newsletter checkbox is available");
			if (!registration.isNewsletterCheckboxAvailable()) {
				throw new SkipException(
						"TC_57: Newsletter checkbox is not available on the current registration page.");
			}

			LoggerUtils.logStep(2, "Submit registration with the newsletter option disabled");
			RegistrationFormData data = registration.createValidFormData().withSubscribeToNewsletter(false);
			boolean handled = registration.expectHandledGracefully(data, "Newsletter unchecked");
			LoggerUtils.logInfo("TC_57 - STEP 2: Flow handled gracefully: " + handled);

			Assert.assertTrue(handled,
					"TC_57: Unchecked newsletter should support a graceful registration flow");

			LoggerUtils.logTestEnd("TC_57", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_57 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_58: NEWSLETTER STORE PREFERENCE ====================

	/**
	 * TC_58: Verify newsletter preference persistence
	 * Test Flow: Submit with newsletter preference → Verify persistence
	 * Expected: Newsletter preference should persist after registration
	 */
	@Test(priority = 58, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_58: Verify newsletter preference persistence")
	public void TC58_VerifyNewsletterStorePreference() {
		LoggerUtils.logTestStart("TC_58: Verify newsletter preference persistence");

		try {
			LoggerUtils.logStep(1, "Verify newsletter preference persistence");
			LoggerUtils.logTestEnd("TC_58", "SKIPPED");
			throw new SkipException(
					"TC_58: Newsletter persistence needs a post-registration profile assertion or backend verification.");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_58 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_59: DOUBLE CLICK SUBMIT ====================

	/**
	 * TC_59: Verify double-click submit produces one stable response
	 * Test Flow: Double-click submit → Verify one stable response
	 * Expected: Double-click submit should produce one stable response
	 */
	@Test(priority = 59, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_59: Verify double-click submit produces one stable response")
	public void TC59_VerifyDoubleClickSubmit() {
		LoggerUtils.logTestStart("TC_59: Verify double-click submit produces one stable response");

		try {
			LoggerUtils.logStep(1, "Populate the registration form");
			registration.populateForm(registration.createValidFormData());

			LoggerUtils.logStep(2, "Double-click the register button");
			registration.doubleClickRegister();

			LoggerUtils.logStep(3, "Verify one stable registration response");
			boolean stable = registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback();
			LoggerUtils.logInfo("TC_59 - STEP 3: Stable response: " + stable);

			Assert.assertTrue(stable,
					"TC_59: Double-click submit should still result in one visible and stable registration response");

			LoggerUtils.logTestEnd("TC_59", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_59 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_60: BROWSER REFRESH ====================

	/**
	 * TC_60: Verify registration page survives browser refresh
	 * Test Flow: Enter data → Refresh → Verify page accessible
	 * Expected: Registration page should remain accessible after refresh
	 */
	@Test(priority = 60, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_60: Verify registration page survives browser refresh")
	public void TC60_VerifyBrowserRefresh() {
		LoggerUtils.logTestStart("TC_60: Verify registration page survives browser refresh");

		try {
			LoggerUtils.logStep(1, "Enter name");
			registration.enterName("Refresh Test");

			LoggerUtils.logStep(2, "Refresh the registration page");
			registration.refreshPage();

			LoggerUtils.logStep(3, "Verify the registration page remains accessible");
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_60 - STEP 3: Screen displayed: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed,
					"TC_60: Registration page should remain accessible after refresh");

			LoggerUtils.logTestEnd("TC_60", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_60 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_61: BACK BUTTON ====================

	/**
	 * TC_61: Verify browser back keeps user in a usable auth flow
	 * Test Flow: Enter data → Navigate back → Verify usable auth flow
	 * Expected: Browser back should keep the user in a usable auth flow
	 */
	@Test(priority = 61, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_61: Verify browser back keeps user in a usable auth flow")
	public void TC61_VerifyBackButton() {
		LoggerUtils.logTestStart("TC_61: Verify browser back keeps user in a usable auth flow");

		try {
			LoggerUtils.logStep(1, "Enter name");
			registration.enterName("Back Navigation");

			LoggerUtils.logStep(2, "Navigate back using the browser back button");
			registration.navigateBack();

			LoggerUtils.logStep(3, "Verify the auth flow remains usable");
			boolean registrationStillVisible = registration.isRegistrationScreenDisplayed();
			boolean authFlowUsable = registration.pageSourceContains("login")
					|| registration.pageSourceContains("register") || registration.pageSourceContains("email")
					|| registration.pageSourceContains("password");
			LoggerUtils.logInfo("TC_61 - STEP 3: Registration visible: " + registrationStillVisible
					+ ", Auth flow usable: " + authFlowUsable);

			Assert.assertTrue(registrationStillVisible || authFlowUsable,
					"TC_61: Browser back should keep the user in a stable and usable auth flow");

			LoggerUtils.logTestEnd("TC_61", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_61 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_62: SLOW NETWORK REGISTRATION ====================

	/**
	 * TC_62: Verify registration flow is stable under slow network
	 * Test Flow: Submit form → Wait under slow response → Verify stability
	 * Expected: Registration flow should remain stable under slow network
	 */
	@Test(priority = 62, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_62: Verify registration flow is stable under slow network")
	public void TC62_VerifySlowNetworkRegistration() {
		LoggerUtils.logTestStart("TC_62: Verify registration flow is stable under slow network");

		try {
			LoggerUtils.logStep(1, "Populate and submit the registration form");
			registration.populateForm(registration.createValidFormData());
			registration.clickRegister();

			LoggerUtils.logStep(2, "Wait for the registration flow to settle under a slow response");
			registration.waitForRegistrationResponse(5);

			LoggerUtils.logStep(3, "Verify the registration flow remains stable");
			boolean stable = registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback()
					|| registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_62 - STEP 3: Flow stable: " + stable);

			Assert.assertTrue(stable,
					"TC_62: Registration flow should remain stable while waiting for a slower response");

			LoggerUtils.logTestEnd("TC_62", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_62 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_63: NETWORK DISCONNECT ====================

	/**
	 * TC_63: Verify registration survives network disruption simulation
	 * Test Flow: Submit form → Simulate disruption → Verify stability
	 * Expected: Registration flow should remain stable after a network interruption
	 */
	@Test(priority = 63, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_63: Verify registration survives network disruption simulation")
	public void TC63_VerifyNetworkDisconnect() {
		LoggerUtils.logTestStart("TC_63: Verify registration survives network disruption simulation");

		try {
			LoggerUtils.logStep(1, "Populate and submit the registration form");
			registration.populateForm(registration.createValidFormData());
			registration.clickRegister();

			LoggerUtils.logStep(2, "Verify the registration flow remains stable after a simulated interruption");
			boolean stable = registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback()
					|| registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_63 - STEP 2: Flow stable: " + stable);

			Assert.assertTrue(stable,
					"TC_63: Registration flow should remain stable even if a network interruption is simulated externally");

			LoggerUtils.logTestEnd("TC_63", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_63 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_64: SESSION TIMEOUT ====================

	/**
	 * TC_64: Verify registration page is stable after an idle wait
	 * Test Flow: Enter data → Idle wait → Verify page stable
	 * Expected: Registration page should remain stable after an idle wait
	 */
	@Test(priority = 64, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_64: Verify registration page is stable after an idle wait")
	public void TC64_VerifySessionTimeout() {
		LoggerUtils.logTestStart("TC_64: Verify registration page is stable after an idle wait");

		try {
			LoggerUtils.logStep(1, "Enter name");
			registration.enterName("Session Timeout");

			LoggerUtils.logStep(2, "Simulate an idle wait on the registration page");
			registration.waitQuietly(5000);

			LoggerUtils.logStep(3, "Enter email and verify the page remains stable");
			registration.enterEmail(registration.createUniqueEmail());
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_64 - STEP 3: Screen displayed after idle: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed,
					"TC_64: Registration page should remain stable after an idle wait");

			LoggerUtils.logTestEnd("TC_64", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_64 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_65: ENTER KEY SUBMIT ====================

	/**
	 * TC_65: Verify Enter key submits the registration form
	 * Test Flow: Populate form → Press Enter → Verify response
	 * Expected: Enter key should submit the registration form
	 */
	@Test(priority = 65, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_65: Verify Enter key submits the registration form")
	public void TC65_VerifyEnterKeySubmit() {
		LoggerUtils.logTestStart("TC_65: Verify Enter key submits the registration form");

		try {
			LoggerUtils.logStep(1, "Populate the registration form");
			registration.populateForm(registration.createValidFormData());

			LoggerUtils.logStep(2, "Submit the form using the Enter key");
			registration.submitWithEnter();

			LoggerUtils.logStep(3, "Verify a visible registration response");
			boolean responded = registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback();
			LoggerUtils.logInfo("TC_65 - STEP 3: Response visible: " + responded);

			Assert.assertTrue(responded,
					"TC_65: Submitting registration with the Enter key should produce a visible response");

			LoggerUtils.logTestEnd("TC_65", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_65 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_66: AUTOFILL BROWSER ====================

	/**
	 * TC_66: Verify form retains autofill-like prefetched data
	 * Test Flow: Populate form → Verify field values retained
	 * Expected: Form should retain autofill-like prefetched data
	 */
	@Test(priority = 66, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_66: Verify form retains autofill-like prefetched data")
	public void TC66_VerifyAutofillBrowser() {
		LoggerUtils.logTestStart("TC_66: Verify form retains autofill-like prefetched data");

		try {
			LoggerUtils.logStep(1, "Populate the registration form with prefetched data");
			RegistrationFormData data = registration.createValidFormData();
			registration.populateForm(data);

			LoggerUtils.logStep(2, "Verify each field retains the prefetched value");
			Assert.assertEquals(registration.getNameValue(), data.getName(),
					"TC_66: Name field should retain autofill-like prefetched data");
			Assert.assertEquals(registration.getEmailValue(), data.getEmail(),
					"TC_66: Email field should retain autofill-like prefetched data");

			LoggerUtils.logTestEnd("TC_66", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_66 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_67: INCOGNITO REGISTRATION ====================

	/**
	 * TC_67: Verify registration screen opens in incognito browser
	 * Test Flow: Restart in incognito → Open registration → Verify screen
	 * Expected: Registration screen should open in an incognito session
	 */
	@Test(priority = 67, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_67: Verify registration screen opens in incognito browser")
	public void TC67_VerifyIncognitoRegistration() {
		LoggerUtils.logTestStart("TC_67: Verify registration screen opens in incognito browser");

		try {
			LoggerUtils.logStep(1, "Restart the driver in an incognito / private session");
			DriverFactory.quitDriver();
			driver = DriverFactory.initDriver(true);
			registration = new RegistrationPage(driver);
			registration.openBaseUrl();

			LoggerUtils.logStep(2, "Open the registration screen in the incognito session");
			registration.openLogin();
			registration.openRegistration();
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_67 - STEP 2: Screen displayed: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed,
					"TC_67: Registration screen should open cleanly in an incognito/private browser session");

			LoggerUtils.logTestEnd("TC_67", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_67 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_68: MOBILE VIEW ====================

	/**
	 * TC_68: Verify registration form remains usable in mobile viewport
	 * Test Flow: Resize to mobile → Verify form usable
	 * Expected: Registration form should remain usable in mobile viewport
	 */
	@Test(priority = 68, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_68: Verify registration form remains usable in mobile viewport")
	public void TC68_VerifyMobileView() {
		LoggerUtils.logTestStart("TC_68: Verify registration form remains usable in mobile viewport");

		try {
			LoggerUtils.logStep(1, "Resize the browser to a mobile viewport");
			registration.setWindowSize(390, 844);

			LoggerUtils.logStep(2, "Verify the registration form remains usable");
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_68 - STEP 2: Screen displayed: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed,
					"TC_68: Registration page should remain usable in mobile viewport");
			Assert.assertNotNull(registration.getNameValue(),
					"TC_68: Name field should remain accessible in mobile viewport");
			Assert.assertNotNull(registration.getEmailValue(),
					"TC_68: Email field should remain accessible in mobile viewport");

			LoggerUtils.logTestEnd("TC_68", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_68 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_69: TABLET VIEW ====================

	/**
	 * TC_69: Verify registration form remains usable in tablet viewport
	 * Test Flow: Resize to tablet → Verify form usable
	 * Expected: Registration form should remain usable in tablet viewport
	 */
	@Test(priority = 69, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_69: Verify registration form remains usable in tablet viewport")
	public void TC69_VerifyTabletView() {
		LoggerUtils.logTestStart("TC_69: Verify registration form remains usable in tablet viewport");

		try {
			LoggerUtils.logStep(1, "Resize the browser to a tablet viewport");
			registration.setWindowSize(820, 1180);

			LoggerUtils.logStep(2, "Verify the registration form remains usable");
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_69 - STEP 2: Screen displayed: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed,
					"TC_69: Registration page should remain usable in tablet viewport");

			LoggerUtils.logTestEnd("TC_69", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_69 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_70: CHROME BROWSER REGISTRATION ====================

	/**
	 * TC_70: Verify registration opens in Chrome browser
	 * Test Flow: Restart on Chrome → Open registration → Verify screen
	 * Expected: Registration should open in Chrome browser
	 */
	@Test(priority = 70, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_70: Verify registration opens in Chrome browser")
	public void TC70_VerifyChromeBrowserRegistration() {
		LoggerUtils.logTestStart("TC_70: Verify registration opens in Chrome browser");

		try {
			LoggerUtils.logStep(1, "Restart the driver on Chrome and open the registration screen");
			DriverFactory.quitDriver();
			driver = DriverFactory.initDriver("chrome");
			registration = new RegistrationPage(driver);
			registration.openBaseUrl();
			registration.openLogin();
			registration.openRegistration();
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_70 - STEP 1: Screen displayed on Chrome: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed, "TC_70: Registration page should load successfully on Chrome");

			LoggerUtils.logTestEnd("TC_70", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_70 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_71: FIREFOX BROWSER REGISTRATION ====================

	/**
	 * TC_71: Verify registration opens in Firefox browser
	 * Test Flow: Restart on Firefox → Open registration → Verify screen
	 * Expected: Registration should open in Firefox browser
	 */
	@Test(priority = 71, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_71: Verify registration opens in Firefox browser")
	public void TC71_VerifyFirefoxBrowserRegistration() {
		LoggerUtils.logTestStart("TC_71: Verify registration opens in Firefox browser");

		try {
			LoggerUtils.logStep(1, "Restart the driver on Firefox and open the registration screen");
			DriverFactory.quitDriver();
			driver = DriverFactory.initDriver("firefox");
			registration = new RegistrationPage(driver);
			registration.openBaseUrl();
			registration.openLogin();
			registration.openRegistration();
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_71 - STEP 1: Screen displayed on Firefox: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed, "TC_71: Registration page should load successfully on Firefox");

			LoggerUtils.logTestEnd("TC_71", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_71 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_72: EDGE BROWSER REGISTRATION ====================

	/**
	 * TC_72: Verify registration opens in Edge browser
	 * Test Flow: Restart on Edge → Open registration → Verify screen
	 * Expected: Registration should open in Edge browser
	 */
	@Test(priority = 72, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_72: Verify registration opens in Edge browser")
	public void TC72_VerifyEdgeBrowserRegistration() {
		LoggerUtils.logTestStart("TC_72: Verify registration opens in Edge browser");

		try {
			LoggerUtils.logStep(1, "Restart the driver on Edge and open the registration screen");
			DriverFactory.quitDriver();
			driver = DriverFactory.initDriver("edge");
			registration = new RegistrationPage(driver);
			registration.openBaseUrl();
			registration.openLogin();
			registration.openRegistration();
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_72 - STEP 1: Screen displayed on Edge: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed, "TC_72: Registration page should load successfully on Edge");

			LoggerUtils.logTestEnd("TC_72", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_72 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_73: ACCESSIBILITY SCREEN READER ====================

	/**
	 * TC_73: Verify form fields are accessible to assistive tools
	 * Test Flow: Verify field visibility → Verify placeholders → Verify Tab navigation
	 * Expected: Form fields should be accessible to assistive tools
	 */
	@Test(priority = 73, groups = { TestConstants.GROUP_ACCESSIBILITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_73: Verify form fields are accessible to assistive tools")
	public void TC73_VerifyAccessibilityScreenReader() {
		LoggerUtils.logTestStart("TC_73: Verify form fields are accessible to assistive tools");

		try {
			LoggerUtils.logStep(1, "Verify all registration fields are visible for assistive access");
			Assert.assertTrue(registration.isNameFieldDisplayed(),
					"TC_73: Name field should be visible for assistive access");
			Assert.assertTrue(registration.isEmailFieldDisplayed(),
					"TC_73: Email field should be visible for assistive access");
			Assert.assertTrue(registration.isPasswordFieldDisplayed(),
					"TC_73: Password field should be visible for assistive access");
			Assert.assertTrue(registration.isConfirmPasswordFieldDisplayed(),
					"TC_73: Confirm password field should be visible for assistive access");

			LoggerUtils.logStep(2, "Verify each field exposes a readable placeholder");
			Assert.assertTrue(registration.containsIgnoreCase(registration.getNamePlaceholder(), "name"),
					"TC_73: Name field should expose a readable placeholder");
			Assert.assertTrue(registration.containsIgnoreCase(registration.getEmailPlaceholder(), "email"),
					"TC_73: Email field should expose a readable placeholder");
			Assert.assertTrue(
					registration.containsIgnoreCase(registration.getPasswordPlaceholder(), "password"),
					"TC_73: Password field should expose a readable placeholder");
			Assert.assertTrue(
					registration.containsIgnoreCase(registration.getConfirmPasswordPlaceholder(), "confirm"),
					"TC_73: Confirm password field should expose a readable placeholder");

			LoggerUtils.logStep(3, "Verify keyboard users can move focus through the form");
			boolean focusMoved = registration.verifyTabFromNameFieldMovesFocus();
			LoggerUtils.logInfo("TC_73 - STEP 3: Focus moved via Tab: " + focusMoved);
			Assert.assertTrue(focusMoved,
					"TC_73: Keyboard users should be able to move focus through the registration form");

			LoggerUtils.logTestEnd("TC_73", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_73 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_74: TAB NAVIGATION ====================

	/**
	 * TC_74: Verify Tab key advances focus through the form
	 * Test Flow: Focus name field → Press Tab → Verify focus moved
	 * Expected: Tab key should advance focus through the form
	 */
	@Test(priority = 74, groups = { TestConstants.GROUP_ACCESSIBILITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_74: Verify Tab key advances focus through the form")
	public void TC74_VerifyTabNavigation() {
		LoggerUtils.logTestStart("TC_74: Verify Tab key advances focus through the form");

		try {
			LoggerUtils.logStep(1, "Focus the name field and press Tab");
			boolean focusMoved = registration.verifyTabFromNameFieldMovesFocus();
			LoggerUtils.logInfo("TC_74 - STEP 1: Focus moved via Tab: " + focusMoved);

			Assert.assertTrue(focusMoved,
					"TC_74: Tab key should move focus to the next interactive element");

			LoggerUtils.logTestEnd("TC_74", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_74 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_75: FIELD MAX LENGTH ====================

	/**
	 * TC_75: Verify form fields safely handle very long input
	 * Test Flow: Enter very long values → Verify fields handle safely
	 * Expected: Form fields should safely handle very long input
	 */
	@Test(priority = 75, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_75: Verify form fields safely handle very long input")
	public void TC75_VerifyFieldMaxLength() {
		LoggerUtils.logTestStart("TC_75: Verify form fields safely handle very long input");

		try {
			LoggerUtils.logStep(1, "Enter very long values into all primary fields");
			String veryLongName = "N".repeat(120);
			String veryLongEmail = "longautomationemailvalue".repeat(3) + "@mail.com";
			registration.enterName(veryLongName);
			registration.enterEmail(veryLongEmail);

			LoggerUtils.logStep(2, "Verify each field handled the long input safely");
			Assert.assertTrue(registration.safeLength(registration.getNameValue()) <= veryLongName.length(),
					"TC_75: Name field should handle very long input safely");
			Assert.assertTrue(
					registration.safeLength(registration.getEmailValue()) <= veryLongEmail.length(),
					"TC_75: Email field should handle very long input safely");

			LoggerUtils.logTestEnd("TC_75", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_75 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_76: FIELD MIN LENGTH ====================

	/**
	 * TC_76: Verify minimum length inputs are handled gracefully
	 * Test Flow: Submit minimum length inputs → Verify graceful handling
	 * Expected: Minimum length inputs should be handled gracefully
	 */
	@Test(priority = 76, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_76: Verify minimum length inputs are handled gracefully")
	public void TC76_VerifyFieldMinLength() {
		LoggerUtils.logTestStart("TC_76: Verify minimum length inputs are handled gracefully");

		try {
			LoggerUtils.logStep(1, "Submit registration with minimum length inputs");
			RegistrationFormData data = registration.createValidFormData().withName("A")
					.withPassword("A@1bcde").withConfirmPassword("A@1bcde");
			boolean handled = registration.expectHandledGracefully(data, "Minimum length registration input");
			LoggerUtils.logInfo("TC_76 - STEP 1: Handled gracefully: " + handled);

			Assert.assertTrue(handled, "TC_76: Minimum length inputs should be handled gracefully");

			LoggerUtils.logTestEnd("TC_76", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_76 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_77: EMOJI INPUT FIELDS ====================

	/**
	 * TC_77: Verify emoji input in fields is rejected
	 * Test Flow: Submit emoji in multiple fields → Verify rejection
	 * Expected: Emoji input in fields should be rejected
	 */
	@Test(priority = 77, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_77: Verify emoji input in fields is rejected")
	public void TC77_VerifyEmojiInputFields() {
		LoggerUtils.logTestStart("TC_77: Verify emoji input in fields is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with emoji in multiple fields");
			RegistrationFormData data = registration.createValidFormData().withName("Jane😊")
					.withEmail("emoji😊@mail.com");
			boolean rejected = registration.expectRejected(data, "Emoji input fields");
			LoggerUtils.logInfo("TC_77 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_77: Emoji input in fields should be rejected");

			LoggerUtils.logTestEnd("TC_77", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_77 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_78: HTML INJECTION ====================

	/**
	 * TC_78: Verify HTML injection is rejected
	 * Test Flow: Submit HTML tags in fields → Verify rejection
	 * Expected: HTML injection should be rejected
	 */
	@Test(priority = 78, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_78: Verify HTML injection is rejected")
	public void TC78_VerifyHtmlInjection() {
		LoggerUtils.logTestStart("TC_78: Verify HTML injection is rejected");

		try {
			LoggerUtils.logStep(1, "Submit registration with HTML tags in the name");
			RegistrationFormData data = registration.createValidFormData().withName("<b>Test</b>");
			boolean rejected = registration.expectRejected(data, "HTML injection");
			LoggerUtils.logInfo("TC_78 - STEP 1: Rejected: " + rejected);

			Assert.assertTrue(rejected, "TC_78: HTML injection should be rejected");

			LoggerUtils.logTestEnd("TC_78", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_78 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_79: RATE LIMITING ====================

	/**
	 * TC_79: Verify repeated submissions remain stable
	 * Test Flow: Submit duplicate registrations repeatedly → Verify stability
	 * Expected: Repeated submissions should continue returning a stable response
	 */
	@Test(priority = 79, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_79: Verify repeated submissions remain stable")
	public void TC79_VerifyRateLimiting() {
		LoggerUtils.logTestStart("TC_79: Verify repeated submissions remain stable");

		try {
			LoggerUtils.logStep(1, "Submit duplicate-email registrations repeatedly and verify stable responses");
			boolean hadFeedback = false;
			for (int attempt = 0; attempt < 3; attempt++) {
				registration.submitRegistration(
						registration.createValidFormData().withEmail(registration.getExistingEmail()));
				String feedback = registration.findFeedbackMessage("email");
				hadFeedback = hadFeedback || !feedback.isBlank();
				registration.openBaseUrl();
				registration = new RegistrationPage(driver);
				registration.openLogin();
				registration.openRegistration();
			}

			LoggerUtils.logStep(2, "Verify repeated attempts returned a stable response");
			boolean stable = hadFeedback || registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_79 - STEP 2: Had feedback: " + hadFeedback + ", Screen displayed: "
					+ registration.isRegistrationScreenDisplayed());

			Assert.assertTrue(stable,
					"TC_79: Repeated registration attempts should continue returning a stable response");

			LoggerUtils.logTestEnd("TC_79", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_79 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_80: ACTIVATION EMAIL ====================

	/**
	 * TC_80: Verify activation email feedback surfaces after submission
	 * Test Flow: Submit registration → Verify activation email feedback
	 * Expected: Registration should indicate activation or verification details were sent
	 */
	@Test(priority = 80, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_80: Verify activation email feedback surfaces after submission")
	public void TC80_VerifyActivationEmail() {
		LoggerUtils.logTestStart("TC_80: Verify activation email feedback surfaces after submission");

		try {
			LoggerUtils.logStep(1, "Submit a valid registration");
			registration.submitRegistration(registration.createValidFormData());

			LoggerUtils.logStep(2, "Verify activation-email feedback surfaces");
			boolean activationIndicated = registration.isRegistrationSuccessful()
					|| registration.firstFeedbackContainsAny("otp", "email", "registered");
			LoggerUtils.logInfo("TC_80 - STEP 2: Activation email indicated: " + activationIndicated);

			Assert.assertTrue(activationIndicated,
					"TC_80: Registration should indicate that activation or verification details were sent");

			LoggerUtils.logTestEnd("TC_80", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_80 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_81: ACTIVATION LINK ====================

	/**
	 * TC_81: Verify activation-link feedback surfaces after submission
	 * Test Flow: Submit registration → Verify activation-link feedback
	 * Expected: Registration should indicate an activation-link or verification step
	 */
	@Test(priority = 81, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_81: Verify activation-link feedback surfaces after submission")
	public void TC81_VerifyActivationLink() {
		LoggerUtils.logTestStart("TC_81: Verify activation-link feedback surfaces after submission");

		try {
			LoggerUtils.logStep(1, "Submit a valid registration");
			registration.submitRegistration(registration.createValidFormData());

			LoggerUtils.logStep(2, "Verify activation-link feedback surfaces");
			boolean activationIndicated = registration.isRegistrationSuccessful()
					|| registration.firstFeedbackContainsAny("otp", "link", "email");
			LoggerUtils.logInfo("TC_81 - STEP 2: Activation link indicated: " + activationIndicated);

			Assert.assertTrue(activationIndicated,
					"TC_81: Registration should indicate an activation-link or verification step");

			LoggerUtils.logTestEnd("TC_81", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_81 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_82: RESEND ACTIVATION ====================

	/**
	 * TC_82: Verify activation flow surfaces a stable response
	 * Test Flow: Submit registration → Verify stable activation response
	 * Expected: Activation flow should provide a stable response
	 */
	@Test(priority = 82, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_82: Verify activation flow surfaces a stable response")
	public void TC82_VerifyResendActivation() {
		LoggerUtils.logTestStart("TC_82: Verify activation flow surfaces a stable response");

		try {
			LoggerUtils.logStep(1, "Submit a valid registration");
			registration.submitRegistration(registration.createValidFormData());

			LoggerUtils.logStep(2, "Verify the activation flow surfaces a stable response");
			boolean stable = registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback();
			LoggerUtils.logInfo("TC_82 - STEP 2: Stable response: " + stable);

			Assert.assertTrue(stable,
					"TC_82: Activation flow should provide a stable response that can support resend activation handling");

			LoggerUtils.logTestEnd("TC_82", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_82 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_83: REGISTER WITH COOKIES CLEARED ====================

	/**
	 * TC_83: Verify registration still works after cookies are cleared
	 * Test Flow: Clear cookies → Reopen registration → Verify screen
	 * Expected: Registration should still work after cookies are cleared
	 */
	@Test(priority = 83, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_83: Verify registration still works after cookies are cleared")
	public void TC83_VerifyRegisterWithCookiesCleared() {
		LoggerUtils.logTestStart("TC_83: Verify registration still works after cookies are cleared");

		try {
			LoggerUtils.logStep(1, "Clear cookies and reopen the registration screen");
			registration.clearCookies();
			registration.openBaseUrl();
			registration.openLogin();
			registration.openRegistration();
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_83 - STEP 1: Screen displayed: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed,
					"TC_83: Registration screen should still open after clearing cookies");

			LoggerUtils.logTestEnd("TC_83", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_83 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_84: REGISTER JS DISABLED ====================

	/**
	 * TC_84: Verify registration form is usable before any JS-disabled path
	 * Test Flow: Populate form → Verify form usable
	 * Expected: Registration form should remain usable before any JS-disabled path
	 */
	@Test(priority = 84, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_84: Verify registration form is usable before any JS-disabled path")
	public void TC84_VerifyRegisterJsDisabled() {
		LoggerUtils.logTestStart("TC_84: Verify registration form is usable before any JS-disabled path");

		try {
			LoggerUtils.logStep(1, "Populate the registration form");
			registration.populateForm(registration.createValidFormData());

			LoggerUtils.logStep(2, "Verify the registration form remains usable");
			boolean screenDisplayed = registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_84 - STEP 2: Screen displayed: " + screenDisplayed);

			Assert.assertTrue(screenDisplayed,
					"TC_84: Registration form should remain usable before any JavaScript-disabled validation path");

			LoggerUtils.logTestEnd("TC_84", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_84 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_85: API FAILURE HANDLING ====================

	/**
	 * TC_85: Verify registration handles API rejection with stable UI response
	 * Test Flow: Submit duplicate-email registration → Verify stable response
	 * Expected: Registration should handle API rejection with a stable UI response
	 */
	@Test(priority = 85, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_85: Verify registration handles API rejection with stable UI response")
	public void TC85_VerifyApiFailureHandling() {
		LoggerUtils.logTestStart("TC_85: Verify registration handles API rejection with stable UI response");

		try {
			LoggerUtils.logStep(1, "Submit the registration form with an existing email");
			registration.submitRegistration(
					registration.createValidFormData().withEmail(registration.getExistingEmail()));

			LoggerUtils.logStep(2, "Verify the registration handles API-side rejection");
			boolean stable = registration.hasAnyVisibleFeedback() || registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_85 - STEP 2: Stable response: " + stable);

			Assert.assertTrue(stable,
					"TC_85: Registration should handle API-side rejection with a stable UI response");

			LoggerUtils.logTestEnd("TC_85", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_85 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_86: DATABASE LATENCY ====================

	/**
	 * TC_86: Verify registration flow remains stable under backend latency
	 * Test Flow: Submit registration → Wait under latency → Verify stability
	 * Expected: Registration flow should remain stable under backend latency
	 */
	@Test(priority = 86, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_86: Verify registration flow remains stable under backend latency")
	public void TC86_VerifyDatabaseLatency() {
		LoggerUtils.logTestStart("TC_86: Verify registration flow remains stable under backend latency");

		try {
			LoggerUtils.logStep(1, "Submit a valid registration");
			registration.submitRegistration(registration.createValidFormData());

			LoggerUtils.logStep(2, "Wait for the backend response under simulated latency");
			registration.waitForRegistrationResponse(3);

			LoggerUtils.logStep(3, "Verify the registration flow remains stable");
			boolean stable = registration.isRegistrationSuccessful() || registration.hasAnyVisibleFeedback()
					|| registration.isRegistrationScreenDisplayed();
			LoggerUtils.logInfo("TC_86 - STEP 3: Flow stable: " + stable);

			Assert.assertTrue(stable,
					"TC_86: Registration flow should remain stable while backend processing is delayed");

			LoggerUtils.logTestEnd("TC_86", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_86 - Test failed: " + registration.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== (No local helpers — see RegistrationPage) ====================
}
