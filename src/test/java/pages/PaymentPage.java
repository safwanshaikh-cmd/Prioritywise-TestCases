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

	public boolean isPaymentPageLoaded() {
		try {
			// Check for multiple payment page indicators
			boolean razorpayVisible = !driver.findElements(RAZORPAY_BTN).isEmpty();
			boolean paymentIframe = !driver.findElements(By.xpath("//iframe")).isEmpty();
			boolean payButtonVisible = !driver.findElements(PAY_BTN).isEmpty();
			boolean cardFormVisible = !driver.findElements(CARD_NUMBER).isEmpty();

			// Payment page is loaded if ANY of these indicators are present
			boolean paymentPageLoaded = razorpayVisible || paymentIframe || payButtonVisible || cardFormVisible;

			if (paymentPageLoaded) {
				LOGGER.info("Payment page loaded successfully");
			}

			return paymentPageLoaded;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Payment page is not loaded: {0}", e.getMessage());
			return false;
		}
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

	public void refreshPaymentPage() {
		driver.navigate().refresh();
		LOGGER.info("Payment page refreshed");
	}

	public boolean isPaymentPageStableAfterRefresh() {
		try {
			refreshPaymentPage();
			return isPaymentPageLoaded() || driver.getCurrentUrl().toLowerCase().contains("payment");
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Payment page was not stable after refresh: {0}", e.getMessage());
			return false;
		}
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

	/**
	 * Waits for payment page to load after clicking Start Listening Now
	 * Provides proper wait time for page transition and overlay disappearance
	 */
	public boolean waitForPaymentPageToLoad() {
		try {
			// Wait up to 10 seconds for payment page indicators
			long startTime = System.currentTimeMillis();
			long timeout = 10000; // 10 seconds

			while (System.currentTimeMillis() - startTime < timeout) {
				if (isPaymentPageLoaded()) {
					LOGGER.info("Payment page detected and loaded successfully");
					return true;
				}
				Thread.sleep(500); // Check every 500ms
			}

			LOGGER.warning("Payment page did not load within timeout");
			return false;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Error waiting for payment page: {0}", e.getMessage());
			return false;
		}
	}
}
