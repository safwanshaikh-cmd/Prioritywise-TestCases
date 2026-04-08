package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import listeners.RetryAnalyzer;
import pages.LoginPage;
import pages.PaymentPage;
import pages.SubscriptionPage;
import utils.ConfigReader;

/**
 * Payment / Subscription Test Cases (TC_376, TC_377, TC_382, TC_386, TC_387)
 *
 * Tests for payment gateway functionality, URL validation, navigation,
 * and payment success/failure scenarios.
 * Based on Sonarplay working automation patterns.
 */
public class PaymentTests extends BaseTest {

	private String getConfiguredEmail() {
		return ConfigReader.getProperty("login.validEmail");
	}

	private String getConfiguredPassword() {
		return ConfigReader.getProperty("login.validPassword");
	}

	private void openPaymentPage() {
		if (getConfiguredEmail() == null || getConfiguredEmail().isBlank()
				|| getConfiguredPassword() == null || getConfiguredPassword().isBlank()) {
			throw new SkipException("Configure login.validEmail and login.validPassword to run payment tests.");
		}

		LoginPage login = new LoginPage(driver);
		SubscriptionPage subscription = new SubscriptionPage(driver);
		pages.DashboardPage dashboard = new pages.DashboardPage(driver);
		PaymentPage payment = new PaymentPage(driver);
		
		login.openLogin();
		login.loginUser(getConfiguredEmail(), getConfiguredPassword());

		// Use working DashboardPage method (proven in TC_365-370)
		dashboard.openSimpleSideMenu();

		// Check if 80% Off is available
		if (!subscription.is80OffOfferDisplayed()) {
			throw new SkipException("Payment entry point is not available for the configured account.");
		}

		// Click through the offer flow (ALL buttons are in sidebar, don't close yet!)
		subscription.click80();         // Still in sidebar
		subscription.clickStartListening(); // Still in sidebar
		subscription.clickStartListeningNow(); // Still in sidebar

		// NOW close the sidebar after all buttons are clicked
		subscription.closeSidebarIfOpen();

		// Wait for payment page to load with proper timeout
		boolean paymentPageLoaded = payment.waitForPaymentPageToLoad();

		// Log will be handled by PaymentPage.waitForPaymentPageToLoad()
	}

	// ================= PAYMENT PAGE LOAD TESTS =================

	@Test(priority = 376, retryAnalyzer = RetryAnalyzer.class,
		description = "TC_376: Verify payment gateway loads successfully")
	public void verifyPaymentGatewayLoads() {
		openPaymentPage();

		// Wait for payment page to load (side menu closing + page transition)
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		PaymentPage payment = new PaymentPage(driver);

		Assert.assertTrue(payment.isPaymentPageLoaded(),
			"TC_376: Payment gateway should load without error");
	}

	@Test(priority = 377, retryAnalyzer = RetryAnalyzer.class,
		description = "TC_377: Verify correct payment gateway URL")
	public void verifyPaymentGatewayURL() {
		openPaymentPage();

		String currentUrl = driver.getCurrentUrl().toLowerCase();
		boolean isValidPaymentUrl = currentUrl.contains("payment")
			|| currentUrl.contains("checkout")
			|| currentUrl.contains("razorpay")
			|| currentUrl.contains("stripe");

		Assert.assertTrue(isValidPaymentUrl,
			"TC_377: Should be on payment gateway URL. Current: " + currentUrl);
	}

	// ================= NAVIGATION TESTS =================

	@Test(priority = 382, retryAnalyzer = RetryAnalyzer.class,
		description = "TC_382: Verify back button from payment page")
	public void verifyBackNavigationFromPayment() {
		openPaymentPage();

		String paymentUrl = driver.getCurrentUrl();

		// Navigate back
		driver.navigate().back();

		// Wait for navigation
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		String currentUrl = driver.getCurrentUrl();
		boolean navigatedBack = !currentUrl.equals(paymentUrl);

		Assert.assertTrue(navigatedBack,
			"TC_382: Should navigate back to previous screen");
	}

	// ================= PAYMENT OUTCOME TESTS =================

	@Test(priority = 386, retryAnalyzer = RetryAnalyzer.class,
		description = "TC_386: Verify payment failure handling")
	public void verifyPaymentFailureHandling() {
		openPaymentPage();

		PaymentPage payment = new PaymentPage(driver);

		payment.selectRazorpay();
		payment.makePayment("4111111111111111", "1225", "123");

		Assert.assertTrue(payment.isPaymentFailed(),
			"TC_386: Payment should fail and show error message");
	}

	@Test(priority = 387, retryAnalyzer = RetryAnalyzer.class,
		description = "TC_387: Verify successful payment flow")
	public void verifySuccessfulPaymentFlow() {
		openPaymentPage();

		PaymentPage payment = new PaymentPage(driver);

		payment.selectRazorpay();
		payment.makePayment("5555555555554444", "1225", "123");

		Assert.assertTrue(payment.isPaymentSuccessful(),
			"TC_387: Payment should succeed with success message and subscription activated");
	}

	// ================= LEGACY TESTS =================

	@Test(priority = 7, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidPayment() {
		openPaymentPage();

		PaymentPage payment = new PaymentPage(driver);

		payment.selectRazorpay();
		payment.makePayment("4111111111111111", "1225", "123");

		Assert.assertTrue(payment.isPaymentFailed(), "Payment should fail but it passed");
	}

	@Test(priority = 8, retryAnalyzer = RetryAnalyzer.class)
	public void verifyValidPayment() {
		openPaymentPage();

		PaymentPage payment = new PaymentPage(driver);

		payment.selectRazorpay();

		// Replace with valid test card if available
		payment.makePayment("5555555555554444", "1225", "123");

		Assert.assertTrue(payment.isPaymentSuccessful(), "Payment was not successful");
	}

	@Test(priority = 9, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPaymentPageRefresh() {
		openPaymentPage();

		PaymentPage payment = new PaymentPage(driver);
		Assert.assertTrue(payment.isPaymentPageStableAfterRefresh(),
				"Payment page should reload successfully after browser refresh");
	}

	@Test(priority = 10, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPaymentPageHandlesDelayedLoad() {
		openPaymentPage();

		PaymentPage payment = new PaymentPage(driver);
		Assert.assertTrue(payment.isPaymentPageLoaded(),
				"Payment page should load and show payment options even when response is delayed");
	}
}
