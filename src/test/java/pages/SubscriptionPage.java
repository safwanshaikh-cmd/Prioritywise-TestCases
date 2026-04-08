package pages;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;

/**
 * Page object for subscription-related flows.
 * Reuses the same interaction style already used across the framework.
 */
public class SubscriptionPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(SubscriptionPage.class.getName());

	private static final By HAMBURGER_MENU = By.xpath("//img[contains(@src,'ic_menu') and @draggable='false']");
	private static final By SUBSCRIPTION_MENU = By.xpath("//div[text()='Subscriptions']");
	private static final By OFFER_80 = By.xpath("//*[contains(text(),'80% Off')]");
	private static final By START_LISTENING_BTN = By.xpath("//*[contains(text(),'Start Listening')]");
	private static final By START_LISTENING_NOW = By.xpath("//div[normalize-space()='Start Listening Now']");
	private static final By OVERLAY = By.xpath("//div[contains(@style,'rgba(0, 0, 0')]");
	private static final By PLAN_ACTIVE_STATUS = By.xpath("//*[contains(text(),'Remaining')]");
	private static final By PLAN_INACTIVE_STATUS = By
			.xpath("//*[contains(text(),'Plan Deactivated') or contains(text(),'Expired')]");
	private static final By SUBSCRIPTION_RESTRICTION_MSG = By.xpath(
			"//*[contains(text(),'already have') or contains(text(),'not eligible') or contains(text(),'cannot')]");
	private static final By SUBSCRIPTION_WARNING = By.xpath(
			"//*[contains(text(),'Subscribe') or contains(text(),'Upgrade') or contains(text(),'expired') or contains(text(),'Plan expired')]");
	private static final By SUBSCRIPTION_TITLE = By.xpath("//div[contains(text(),'Subscription Plan')]");
	private static final By FIRST_BOOK = By.xpath("(//img[contains(@src,'sonarplay')])[1]");
	private static final By PLAY_BUTTON = By.xpath("//img[contains(@src,'ic_play')]/ancestor::div[@tabindex='0']");
	private static final By PAUSE_ICON = By.xpath("//img[contains(@src,'ic_pause')]");

	private final WebDriverWait pageWait;

	public SubscriptionPage(WebDriver driver) {
		super(driver);
		this.pageWait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	public void clickHamburgerMenu() {
		waitForOverlayToDisappear();
		jsClick(HAMBURGER_MENU);
		LOGGER.info("Hamburger menu clicked");
	}

	public void clickSubscription() {
		waitForOverlayToDisappear();
		jsClick(SUBSCRIPTION_MENU);
		LOGGER.info("Subscription menu clicked");
	}

	public boolean isSubscriptionPageDisplayed() {
		try {
			return wait.waitForElementVisible(SUBSCRIPTION_TITLE).isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Subscription page is not visible: {0}", e.getMessage());
			return false;
		}
	}

	public String getPlanStatus() {
		try {
			return wait.waitForElementVisible(PLAN_ACTIVE_STATUS).getText();
		} catch (Exception e) {
			try {
				return wait.waitForElementVisible(PLAN_INACTIVE_STATUS).getText();
			} catch (Exception inner) {
				LOGGER.log(Level.FINE, "Plan status could not be resolved: {0}", inner.getMessage());
				return "";
			}
		}
	}

	public boolean isPlanActive() {
		String status = getPlanStatus().toLowerCase();
		return status.contains("remaining");
	}

	public boolean isPlanSelectionVisible() {
		return isAnyVisible(OFFER_80, START_LISTENING_BTN, START_LISTENING_NOW);
	}

	public boolean isPlanSelectionDisabled() {
		if (!isPlanActive()) {
			return false;
		}

		if (isSubscriptionActivationRestricted()) {
			return true;
		}

		List<WebElement> offerElements = driver.findElements(OFFER_80);
		for (WebElement element : offerElements) {
			if (element.isDisplayed() && !element.isEnabled()) {
				return true;
			}
		}

		List<WebElement> startElements = driver.findElements(START_LISTENING_BTN);
		for (WebElement element : startElements) {
			if (element.isDisplayed() && !element.isEnabled()) {
				return true;
			}
		}

		return !isPlanSelectionVisible();
	}

	public void attemptToActivateAnotherPlan() {
		if (isDisplayed(OFFER_80)) {
			jsClick(OFFER_80);
		}

		if (isDisplayed(START_LISTENING_BTN)) {
			jsClick(START_LISTENING_BTN);
		}

		waitForOverlayToDisappear();
		if (isDisplayed(START_LISTENING_NOW)) {
			jsClick(START_LISTENING_NOW);
		}
	}

	public void click80() {
		jsClick(OFFER_80);
		LOGGER.info("Clicked subscription offer");
	}

	public void clickStartListening() {
		jsClick(START_LISTENING_BTN);
		LOGGER.info("Clicked Start Listening");
	}

	public void clickStartListeningNow() {
		jsClick(START_LISTENING_NOW);
		waitForOverlayToDisappear(); // Wait AFTER clicking (Sonarplay pattern)

		// Add small wait to allow payment page to start loading
		try {
			Thread.sleep(1000); // Wait 1 second for page transition to start
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		LOGGER.info("Clicked Start Listening Now");
	}

	public boolean isSubscriptionActivationRestricted() {
		try {
			return wait.waitForElementVisible(SUBSCRIPTION_RESTRICTION_MSG).isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Restriction message not displayed: {0}", e.getMessage());
			return false;
		}
	}

	public void goToHome() {
		jsClick(By.xpath("//*[contains(text(),'Home')]"));
		LOGGER.info("Navigated to home");
	}

	public void playFirstBook() {
		click(FIRST_BOOK);
		LOGGER.info("Opened first available book");
	}

	public void clickPlay() {
		if (isDisplayed(PLAY_BUTTON)) {
			jsClick(PLAY_BUTTON);
			LOGGER.info("Play button clicked");
		}
	}

	public boolean isAudioPlaying() {
		try {
			return wait.waitForElementVisible(PAUSE_ICON).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isSubscriptionWarningDisplayed() {
		try {
			return wait.waitForElementVisible(SUBSCRIPTION_WARNING).isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Subscription warning not displayed: {0}", e.getMessage());
			return false;
		}
	}

	public void refreshCurrentPage() {
		driver.navigate().refresh();
		waitForOverlayToDisappear();
		LOGGER.info("Current page refreshed");
	}

	public void waitForPageReady() {
		pageWait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
				.equals("complete"));
	}

	// ================= ADDITIONAL METHODS FROM SONARPLAY =================

	public boolean is80OffOfferDisplayed() {
		try {
			return wait.waitForElementVisible(OFFER_80).isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "80% Off offer not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isPaymentPageDisplayed() {
		try {
			String currentUrl = driver.getCurrentUrl().toLowerCase();
			return currentUrl.contains("payment")
				|| currentUrl.contains("checkout")
				|| currentUrl.contains("razorpay")
				|| !driver.findElements(By.xpath("//iframe")).isEmpty();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Payment page not visible: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Closes the sidebar if it's open (Sonarplay pattern)
	 */
	public void closeSidebarIfOpen() {
		try {
			java.util.List<WebElement> closeBtns = driver.findElements(By.xpath("//div[@data-testid='pressable_close_sidebar']"));

			if (!closeBtns.isEmpty()) {
				// Use JavaScript directly (Sonarplay pattern) since jsClick() doesn't accept WebElement
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtns.get(0));
				LOGGER.info("Sidebar closed");
				waitForOverlayToDisappear();
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Sidebar close not needed or failed: {0}", e.getMessage());
		}
	}

	private boolean isAnyVisible(By... locators) {
		for (By locator : locators) {
			try {
				if (isDisplayed(locator)) {
					return true;
				}
			} catch (Exception e) {
				LOGGER.log(Level.FINEST, "Visibility check failed for {0}: {1}", new Object[] { locator, e.getMessage() });
			}
		}
		return false;
	}

	private void waitForOverlayToDisappear() {
		try {
			wait.waitForElementInvisible(OVERLAY);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Overlay not present or already gone: {0}", e.getMessage());
		}
	}

	// ============================================================
	// SUBSCRIPTION CANCELLATION METHODS
	// ============================================================

	private static final By CANCEL_PLAN_BUTTON = By.xpath(
			"//*[self::button or @role='button' or @tabindex='0']"
			+ "[contains(translate(normalize-space(.), 'CANCEL', 'cancel'), 'cancel')]"
			+ "[contains(translate(normalize-space(.), 'PLAN', 'plan'), 'plan') or contains(translate(normalize-space(.), 'SUBSCRIPTION', 'subscription'), 'subscription')]");

	private static final By CANCEL_CONFIRMATION_DIALOG = By.xpath(
			"//*[contains(translate(normalize-space(.), 'CANCEL', 'cancel'), 'cancel')]"
			+ "[contains(translate(normalize-space(.), 'PLAN', 'plan'), 'plan') or contains(translate(normalize-space(.), 'SUBSCRIPTION', 'subscription'), 'subscription')]"
			+ "[ancestor::*[contains(@class, 'modal') or contains(@class, 'dialog') or contains(@role, 'dialog')]]");

	private static final By CONFIRM_CANCEL_BUTTON = By.xpath(
			"//*[self::button or @role='button']"
			+ "[contains(translate(normalize-space(.), 'YES', 'yes'), 'yes')"
			+ " or contains(translate(normalize-space(.), 'CONFIRM', 'confirm'), 'confirm')]"
			+ "[ancestor::*[contains(@class, 'modal') or contains(@class, 'dialog') or contains(@role, 'dialog')]]");

	private static final By DECLINE_CANCEL_BUTTON = By.xpath(
			"//*[self::button or @role='button']"
			+ "[contains(translate(normalize-space(.), 'NO', 'no'), 'no')"
			+ " or contains(translate(normalize-space(.), 'KEEP', 'keep'), 'keep')]"
			+ "[ancestor::*[contains(@class, 'modal') or contains(@class, 'dialog') or contains(@role, 'dialog')]]");

	private static final By LOGOUT_MENU_ITEM = By.xpath(
			"//*[normalize-space()='Logout' or normalize-space()='Log Out']"
			+ " | //*[@tabindex='0' and contains(normalize-space(.), 'Logout')]");

	/**
	 * Cancel the active subscription plan.
	 * This method initiates cancellation and confirms it.
	 */
	public void cancelPlan() {
		waitForOverlayToDisappear();

		if (isDisplayed(CANCEL_PLAN_BUTTON)) {
			jsClick(CANCEL_PLAN_BUTTON);
			LOGGER.info("Cancel plan button clicked");

			// Wait for confirmation dialog and click confirm
			try {
				WebElement confirmBtn = pageWait.until(ExpectedConditions.elementToBeClickable(CONFIRM_CANCEL_BUTTON));
				scrollIntoView(confirmBtn);
				confirmBtn.click();
				LOGGER.info("Cancellation confirmed");
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Confirmation dialog not present: {0}", e.getMessage());
			}
		} else {
			LOGGER.log(Level.WARNING, "Cancel plan button not found - plan may already be cancelled");
		}
	}

	/**
	 * Initiate plan cancellation without confirming.
	 * Used to test the confirmation dialog behavior.
	 *
	 * @return true if confirmation dialog appeared, false otherwise
	 */
	public boolean initiatePlanCancellation() {
		waitForOverlayToDisappear();

		if (isDisplayed(CANCEL_PLAN_BUTTON)) {
			jsClick(CANCEL_PLAN_BUTTON);
			LOGGER.info("Cancel plan button clicked (initiation only)");

			// Check for confirmation dialog
			try {
				WebElement dialog = pageWait.until(ExpectedConditions.visibilityOfElementLocated(CANCEL_CONFIRMATION_DIALOG));
				boolean hasDialog = dialog != null && dialog.isDisplayed();
				LOGGER.log(Level.INFO, "Confirmation dialog present: {0}", hasDialog);
				return hasDialog;
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Confirmation dialog not found: {0}", e.getMessage());
				return false;
			}
		}

		return false;
	}

	/**
	 * Decline the plan cancellation by clicking No/Keep button.
	 */
	public void declineCancellation() {
		waitForOverlayToDisappear();

		try {
			WebElement declineBtn = pageWait.until(ExpectedConditions.elementToBeClickable(DECLINE_CANCEL_BUTTON));
			scrollIntoView(declineBtn);
			declineBtn.click();
			LOGGER.info("Cancellation declined");
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Decline button not found: {0}", e.getMessage());
		}
	}

	/**
	 * Click logout menu item from hamburger menu.
	 */
	public void clickLogoutMenuItem() {
		waitForOverlayToDisappear();

		try {
			WebElement logoutBtn = pageWait.until(ExpectedConditions.elementToBeClickable(LOGOUT_MENU_ITEM));
			scrollIntoView(logoutBtn);
			jsClick((By) logoutBtn);
			LOGGER.info("Logout menu item clicked");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click logout menu item: {.out.println(e.getMessage()");
			throw e;
		}
	}
}
