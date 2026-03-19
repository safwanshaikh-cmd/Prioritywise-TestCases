package pages;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import base.BasePage;

/**
 * Page object for the payment flow.
 */
public class PaymentPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(PaymentPage.class.getName());

	private static final By RAZORPAY_BTN = By.xpath("//*[contains(text(),'Razorpay')]");
	private static final By CARD_NUMBER = By.name("card[number]");
	private static final By EXPIRY_DATE = By.name("card[expiry]");
	private static final By CVV = By.name("card[cvv]");
	private static final By PAY_BTN = By.xpath("//button[contains(text(),'Pay')]");
	private static final By ERROR_MESSAGE = By.xpath("//*[contains(text(),'failed') or contains(text(),'declined')]");
	private static final By SUCCESS_MESSAGE = By
			.xpath("//*[contains(text(),'success') or contains(text(),'Payment successful')]");

	public PaymentPage(WebDriver driver) {
		super(driver);
	}

	public void selectRazorpay() {
		click(RAZORPAY_BTN);
		LOGGER.info("Razorpay option selected");
	}

	public void enterCardDetails(String card, String expiry, String cvv) {
		type(CARD_NUMBER, card);
		type(EXPIRY_DATE, expiry);
		type(CVV, cvv);
	}

	public void clickPay() {
		click(PAY_BTN);
	}

	public void makePayment(String card, String expiry, String cvv) {
		enterCardDetails(card, expiry, cvv);
		clickPay();
	}

	public boolean isPaymentFailed() {
		try {
			wait.waitForElementVisible(ERROR_MESSAGE);
			LOGGER.info("Payment failure message displayed");
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failure message not found");
			return false;
		}
	}

	public boolean isPaymentSuccessful() {
		try {
			wait.waitForElementVisible(SUCCESS_MESSAGE);
			LOGGER.info("Payment success message displayed");
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Success message not found");
			return false;
		}
	}
}
