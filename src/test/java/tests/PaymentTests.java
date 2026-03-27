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
 * Payment / Subscription Test Cases
 */
public class PaymentTests extends BaseTest {

	private String getConfiguredEmail() {
		return ConfigReader.getProperty("login.validEmail");
	}

	private String getConfiguredPassword() {
		return ConfigReader.getProperty("login.validPassword");
	}

	private void openPaymentPage() {
		if (getConfiguredEmail() == null || getConfiguredEmail().isBlank() || getConfiguredPassword() == null
				|| getConfiguredPassword().isBlank()) {
			throw new SkipException("Configure login.validEmail and login.validPassword to run payment tests.");
		}

		LoginPage login = new LoginPage(driver);
		SubscriptionPage subscription = new SubscriptionPage(driver);

		login.openLogin();
		login.loginUser(getConfiguredEmail(), getConfiguredPassword());

		if (!subscription.isPlanSelectionVisible()) {
			throw new SkipException("Payment entry point is not available for the configured account.");
		}

		subscription.click80();
		subscription.clickStartListening();
		subscription.clickStartListeningNow();
	}

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
