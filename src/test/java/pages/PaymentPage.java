package pages;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import base.BasePage;

/**
 * Page object for the payment flow. Handles payment iframe switching for
 * entering card details.
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
	private static final By PAYMENT_IFRAME = By.xpath("//iframe");

	public PaymentPage(WebDriver driver) {
		super(driver);
	}

	// ================= IFRAME HANDLING =================

	/**
	 * Switches to the payment iframe to enter card details
	 */
	public void switchToPaymentFrame() {
		try {
			WebElement iframe = wait.waitForElementVisible(PAYMENT_IFRAME);
			driver.switchTo().frame(iframe);
			LOGGER.info("Switched to payment iframe");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to switch to payment iframe: {0}", e.getMessage());
		}
	}

	/**
	 * Switches back to default content after iframe operations
	 */
	public void switchToDefaultContent() {
		try {
			driver.switchTo().defaultContent();
			LOGGER.info("Switched back to default content");
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Already in default content or failed to switch: {0}", e.getMessage());
		}
	}

	// ================= PAYMENT ACTIONS =================

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
		// Switch to payment iframe before entering details
		switchToPaymentFrame();

		try {
			type(CARD_NUMBER, card);
			type(EXPIRY_DATE, expiry);
			type(CVV, cvv);
			LOGGER.info("Card details entered in payment iframe");
		} finally {
			// Switch back to default content
			switchToDefaultContent();
		}
	}

	public void clickPay() {
		// Switch to payment iframe before clicking pay button
		switchToPaymentFrame();

		try {
			click(PAY_BTN);
			LOGGER.info("Pay button clicked in payment iframe");
		} finally {
			// Switch back to default content
			switchToDefaultContent();
		}
	}

	public void makePayment(String card, String expiry, String cvv) {
		// Enter card details (handles iframe switching internally)
		enterCardDetails(card, expiry, cvv);

		// Click pay button (handles iframe switching internally)
		clickPay();
	}

	public void refreshPaymentPage() {
		driver.navigate().refresh();
		LOGGER.info("Payment page refreshed");
	}

	public boolean isPaymentPageStableAfterRefresh() {
		try {
			refreshPaymentPage();

			String currentUrl = driver.getCurrentUrl();
			String safeUrl = currentUrl != null ? currentUrl.toLowerCase() : "";

			return isPaymentPageLoaded() || safeUrl.contains("payment");

		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Payment page was not stable after refresh: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isPaymentFailed() {
		try {
			// Check for error message in default content
			boolean errorInDefault = !driver.findElements(ERROR_MESSAGE).isEmpty();
			if (errorInDefault) {
				LOGGER.info("Payment failure message displayed (default content)");
				return true;
			}

			// Check for error message in payment iframe
			switchToPaymentFrame();
			try {
				boolean errorInIframe = !driver.findElements(ERROR_MESSAGE).isEmpty();
				LOGGER.info("Payment failure message displayed (payment iframe)");
				return errorInIframe;
			} finally {
				switchToDefaultContent();
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failure message not found: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isPaymentSuccessful() {
		try {
			// Check for success message in default content
			boolean successInDefault = !driver.findElements(SUCCESS_MESSAGE).isEmpty();
			if (successInDefault) {
				LOGGER.info("Payment success message displayed (default content)");
				return true;
			}

			// Check for success message in payment iframe
			switchToPaymentFrame();
			try {
				boolean successInIframe = !driver.findElements(SUCCESS_MESSAGE).isEmpty();
				LOGGER.info("Payment success message displayed (payment iframe)");
				return successInIframe;
			} finally {
				switchToDefaultContent();
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Success message not found: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Waits for payment page to load after clicking Start Listening Now Provides
	 * proper wait time for page transition and overlay disappearance
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
