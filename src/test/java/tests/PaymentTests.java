package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.PaymentPage;

/**
 * Payment / Subscription Test Cases
 */
public class PaymentTests extends BaseTest {

	@Test(priority = 7)
	public void verifyInvalidPayment() {

		PaymentPage payment = new PaymentPage(driver);

		payment.selectRazorpay();
		payment.makePayment("4111111111111111", "1225", "123");

		Assert.assertTrue(payment.isPaymentFailed(), "Payment should fail but it passed");
	}

	@Test(priority = 8)
	public void verifyValidPayment() {

		PaymentPage payment = new PaymentPage(driver);

		payment.selectRazorpay();

		// Replace with valid test card if available
		payment.makePayment("5555555555554444", "1225", "123");

		Assert.assertTrue(payment.isPaymentSuccessful(), "Payment was not successful");
	}
}