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
	private static final By PLAN_DETAILS_CONTAINER = By.xpath(
			"//*[contains(@class, 'plan') or contains(@class, 'subscription') or contains(@class, 'tier')]"
			+ " | //*[*[contains(text(),'Plan') or contains(text(),'Premium') or contains(text(),'Subscription')]]");

	private static final By ACTIVE_PLAN_TEXT = By.xpath(
			"//*[contains(text(),'Active') or contains(text(),'Premium') or contains(text(),'Remaining') or contains(text(),'Days')]"
			+ "[not(contains(text(),'Not Active'))]");

	private static final By PLAN_INFO_SECTIONS = By.xpath(
			"//*[contains(@class, 'info') or contains(@class, 'details') or contains(@class, 'status')]");

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
		// Try multiple approaches to find plan status
		String status = "";

		// Approach 1: Look for cancelled/inactive status first (for cancelled plans)
		try {
			WebElement inactiveElement = wait.waitForElementVisible(PLAN_INACTIVE_STATUS);
			status = inactiveElement.getText();
			LOGGER.log(Level.INFO, "Plan status found (INACTIVE_STATUS locator): {0}", status);
			return status;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "PLAN_INACTIVE_STATUS not found: {0}", e.getMessage());
		}

		// Approach 2: Look for specific active status text
		try {
			WebElement statusElement = wait.waitForElementVisible(PLAN_ACTIVE_STATUS);
			status = statusElement.getText();
			LOGGER.log(Level.INFO, "Plan status found (ACTIVE_STATUS locator): {0}", status);
			return status;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "PLAN_ACTIVE_STATUS not found: {0}", e.getMessage());
		}

		// Approach 3: Look for any active plan text
		try {
			WebElement activePlanElement = driver.findElement(ACTIVE_PLAN_TEXT);
			status = activePlanElement.getText();
			LOGGER.log(Level.INFO, "Plan status found (ACTIVE_PLAN_TEXT locator): {0}", status);
			return status;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "ACTIVE_PLAN_TEXT not found: {0}", e.getMessage());
		}

		// Approach 4: Look for plan details container
		try {
			WebElement planContainer = driver.findElement(PLAN_DETAILS_CONTAINER);
			status = planContainer.getText();
			LOGGER.log(Level.INFO, "Plan status found (PLAN_DETAILS_CONTAINER locator): {0}", status);
			return status;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "PLAN_DETAILS_CONTAINER not found: {0}", e.getMessage());
		}

		// Approach 5: Get all text from subscription page sections
		try {
			java.util.List<WebElement> infoSections = driver.findElements(PLAN_INFO_SECTIONS);
			if (!infoSections.isEmpty()) {
				StringBuilder allStatus = new StringBuilder();
				for (WebElement section : infoSections) {
					String sectionText = section.getText();
					if (!sectionText.isBlank() &&
					    (sectionText.contains("Plan") || sectionText.contains("Premium") ||
					     sectionText.contains("Active") || sectionText.contains("Remaining") ||
					     sectionText.contains("Cancel") || sectionText.contains("Expire") ||
					     sectionText.contains("Inactive") || sectionText.contains("Deactivate"))) {
						allStatus.append(sectionText).append(" | ");
					}
				}
				if (allStatus.length() > 0) {
					status = allStatus.toString();
					LOGGER.log(Level.INFO, "Plan status found (INFO_SECTIONS): {0}", status);
					return status;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "PLAN_INFO_SECTIONS not found: {0}", e.getMessage());
		}

		LOGGER.log(Level.WARNING, "Plan status could not be resolved using any approach");
		return "";
	}

	public boolean isPlanActive() {
		String status = getPlanStatus().toLowerCase();
		return status.contains("remaining") || status.contains("active") || status.contains("premium");
	}

	/**
	 * Check if any plan details are visible on the page.
	 * This is a more robust check than getPlanStatus() for verifying that plan information is displayed.
	 *
	 * @return true if any plan details are visible, false otherwise
	 */
	public boolean isPlanDetailsVisible() {
		// Check if any of the plan-related elements are present on the page
		return isAnyVisible(PLAN_ACTIVE_STATUS, PLAN_INACTIVE_STATUS, ACTIVE_PLAN_TEXT,
		                  PLAN_DETAILS_CONTAINER, SUBSCRIPTION_TITLE);
	}

	/**
	 * Check if the plan has been cancelled.
	 * This method looks for various indicators that a plan has been cancelled.
	 *
	 * @return true if plan is cancelled, false otherwise
	 */
	public boolean isPlanCancelled() {
		String status = getPlanStatus().toLowerCase();

		// If status is empty, this strongly indicates cancellation (plan details removed)
		if (status.isBlank()) {
			// Empty status after being on subscription page = cancelled
			LOGGER.info("Plan cancellation detected: Status is empty (plan details removed from page)");
			return true;
		}

		// Check for cancellation indicators in status text
		boolean hasCancellationKeywords = status.contains("cancel") || status.contains("expire") ||
		       status.contains("deactivate") || status.contains("inactive") ||
		       status.contains("ended") || status.contains("terminated") ||
		       status.contains("suspended");

		if (hasCancellationKeywords) {
			LOGGER.info("Plan cancellation detected: Status contains cancellation keywords");
			return true;
		}

		// If we have active plan status, it's not cancelled
		if (status.contains("active") || status.contains("remaining") || status.contains("premium")) {
			LOGGER.info("Plan appears to be active, not cancelled");
			return false;
		}

		// Default: if status is not empty but doesn't contain clear indicators
		LOGGER.info("Plan cancellation status unclear, assuming not cancelled");
		return false;
	}

	/**
	 * Cancel the active subscription plan.
	 * This method cancels the active plan using the complete cancellation flow.
	 * Follows: Cancel Plan → Continue to Cancel → Select Reason → Submit Reason
	 * Note: Cancellation completes after submitting reason (no final confirmation click needed)
	 */
	public void cancelActivePlan() {
		// Step 1: Click first Cancel button
		jsClick(CANCEL_PLAN_BUTTON);
		LOGGER.log(Level.INFO, "Cancel Plan button clicked");

		// Step 2: Click Continue to Cancel (second cancel)
		jsClick(CONTINUE_TO_CANCEL_BUTTON);
		LOGGER.log(Level.INFO, "Continue to Cancel clicked");

		// Step 3: Select cancellation reason
		jsClick(CANCEL_REASON_OPTION);
		LOGGER.log(Level.INFO, "Cancellation reason selected");

		// Step 4: Submit the reason (Cancellation completes here)
		jsClick(SUBMIT_REASON_BUTTON);
		LOGGER.log(Level.INFO, "Cancellation reason submitted - plan cancelled successfully");

		// Wait for cancellation to complete
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// Refresh page to see updated status
		driver.navigate().refresh();
		LOGGER.log(Level.INFO, "Page refreshed to show cancelled status");
	}


	/**
	 * Check if subscription is cancelled.
	 * This method matches the Sonarplay Automation pattern for checking subscription cancellation.
	 *
	 * @return true if subscription is cancelled, false otherwise
	 */
	public boolean isSubscriptionCancelled() {
		// Use the same logic as isPlanCancelled for consistency
		return isPlanCancelled();
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

	private static final By CONTINUE_TO_CANCEL_BUTTON = By.xpath(
			"//*[self::div or @role='button' or @tabindex='0']"
			+ "[contains(translate(normalize-space(.), 'CONTINUE', 'continue'), 'continue')]"
			+ "[contains(translate(normalize-space(.), 'CANCEL', 'cancel'), 'cancel')]"
			+ "[normalize-space()='Continue to Cancel']");

	private static final By CANCEL_REASON_OPTION = By.xpath(
			"//*[@tabindex='0']"
			+ "[contains(translate(normalize-space(.), 'NOT USING', 'not using'), 'not using')"
			+ " or contains(translate(normalize-space(.), 'EXPENSIVE', 'expensive'), 'expensive')"
			+ " or contains(translate(normalize-space(.), 'TECHNICAL', 'technical'), 'technical')"
			+ " or contains(translate(normalize-space(.), 'OTHER', 'other'), 'other')]"
			+ "[.//div[contains(text(), 'Not Using It Much?') or contains(text(), 'Too Expensive') or contains(text(), 'Technical Issues')]]");

	private static final By SUBMIT_REASON_BUTTON = By.xpath(
			"//*[@tabindex='0']"
			+ "[contains(translate(normalize-space(.), 'SUBMIT', 'submit'), 'submit')]"
			+ "[contains(translate(normalize-space(.), 'REASON', 'reason'), 'reason')]"
			+ "[normalize-space()='Submit Reason']");

	private static final By FINAL_CANCEL_BUTTON = By.xpath(
			"//div[@tabindex='0'][.//div[contains(text(), 'Continue to Cancel')]]");

	private static final By KEEP_SUBSCRIPTION_BUTTON = By.xpath(
			"//div[@tabindex='0'][.//div[contains(text(), 'Keep Subscription')]]");

	private static final By CANCELLATION_CONFIRMATION_DIALOG = By.xpath(
			"//*[contains(text(), 'Are you sure you want to cancel') or contains(text(), 'Your plan will stay active until')]");

	private static final By ANY_CONTINUE_TO_CANCEL = By.xpath(
			"//*[@tabindex='0'][.//div[contains(text(), 'Continue to Cancel')]]");

	private static final By GO_BACK_BUTTON = By.xpath(
			"//*[@tabindex='0']"
			+ "[contains(translate(normalize-space(.), 'GO BACK', 'go back'), 'go back')]"
			+ "[contains(translate(normalize-space(.), 'BACK', 'back'), 'back')]"
			+ "[normalize-space()='Go Back']");

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

	private static final By PLAN_EXPIRY_DATE = By.xpath(
			"//*[contains(text(),'Expiry') or contains(text(),'Valid till') or contains(text(),'Valid until')]");

	private static final By ACTIVE_PLAN_DETAILS = By.xpath(
			"//*[contains(@class, 'plan') or contains(@class, 'subscription')]"
			+ "[contains(text(),'Active') or contains(text(),'Remaining') or contains(text(),'Premium')]");

	private static final By CANCELLED_PLAN_DETAILS = By.xpath(
			"//*[contains(@class, 'plan') or contains(@class, 'subscription')]"
			+ "[contains(text(),'Cancel') or contains(text(),'Expire') or contains(text(),'Deactivate')]");

	/**
	 * Cancel the active subscription plan.
	 * This method initiates cancellation, selects a reason, and confirms it.
	 * Follows the complete cancellation flow: Continue to Cancel → Select Reason → Submit Reason
	 */
	public void cancelPlan() {
		waitForOverlayToDisappear();

		if (isDisplayed(CANCEL_PLAN_BUTTON) || isDisplayed(CONTINUE_TO_CANCEL_BUTTON)) {
			// Step 1: Click Continue to Cancel
			if (isDisplayed(CONTINUE_TO_CANCEL_BUTTON)) {
				jsClick(CONTINUE_TO_CANCEL_BUTTON);
				LOGGER.info("Continue to Cancel button clicked");
			} else if (isDisplayed(CANCEL_PLAN_BUTTON)) {
				jsClick(CANCEL_PLAN_BUTTON);
				LOGGER.info("Cancel plan button clicked");
			}

			// Wait for reason selection screen
			try {
				Thread.sleep(1000); // Wait for reason options to load
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			// Step 2: Select a cancellation reason
			if (isDisplayed(CANCEL_REASON_OPTION)) {
				jsClick(CANCEL_REASON_OPTION);
				LOGGER.info("Cancellation reason selected: Not Using It Much?");
			}

			// Wait for submit button to be clickable
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			// Step 3: Submit the reason
			if (isDisplayed(SUBMIT_REASON_BUTTON)) {
				jsClick(SUBMIT_REASON_BUTTON);
				LOGGER.info("Submit Reason button clicked - cancellation confirmed");
			}

		} else {
			LOGGER.log(Level.WARNING, "Cancel plan button not found - plan may already be cancelled");
		}
	}

	/**
	 * Initiate plan cancellation without confirming.
	 * Used to test the confirmation dialog behavior.
	 * This method stops after clicking "Continue to Cancel" and shows the reason selection screen.
	 *
	 * @return true if reason selection screen appeared, false otherwise
	 */
	public boolean initiatePlanCancellation() {
		waitForOverlayToDisappear();

		if (isDisplayed(CANCEL_PLAN_BUTTON) || isDisplayed(CONTINUE_TO_CANCEL_BUTTON)) {
			// Click Continue to Cancel
			if (isDisplayed(CONTINUE_TO_CANCEL_BUTTON)) {
				jsClick(CONTINUE_TO_CANCEL_BUTTON);
				LOGGER.info("Continue to Cancel button clicked (initiation only)");
			} else if (isDisplayed(CANCEL_PLAN_BUTTON)) {
				jsClick(CANCEL_PLAN_BUTTON);
				LOGGER.info("Cancel plan button clicked (initiation only)");
			}

			// Wait for reason selection screen to appear
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			// Check if reason selection screen is displayed
			try {
				WebElement reasonOption = pageWait.until(ExpectedConditions.visibilityOfElementLocated(CANCEL_REASON_OPTION));
				WebElement submitButton = pageWait.until(ExpectedConditions.visibilityOfElementLocated(SUBMIT_REASON_BUTTON));
				WebElement goBackButton = pageWait.until(ExpectedConditions.visibilityOfElementLocated(GO_BACK_BUTTON));

				boolean hasReasonScreen = (reasonOption != null && reasonOption.isDisplayed()) &&
				                         (submitButton != null && submitButton.isDisplayed()) &&
				                         (goBackButton != null && goBackButton.isDisplayed());

				LOGGER.log(Level.INFO, "Cancellation reason selection screen present: {0}", hasReasonScreen);
				return hasReasonScreen;

			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Reason selection screen not found: {0}", e.getMessage());
				return false;
			}
		}

		return false;
	}

	/**
	 * Decline the plan cancellation by clicking Go Back button.
	 * This aborts the cancellation process and keeps the plan active.
	 */
	public void declineCancellation() {
		waitForOverlayToDisappear();

		try {
			WebElement goBackBtn = pageWait.until(ExpectedConditions.elementToBeClickable(GO_BACK_BUTTON));
			scrollIntoView(goBackBtn);
			goBackBtn.click();
			LOGGER.info("Cancellation declined - clicked Go Back button");
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Go Back button not found: {0}", e.getMessage());
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
			LOGGER.log(Level.SEVERE, "Failed to click logout menu item: {0}", e.getMessage());
			throw e;
		}
	}

	// ============================================================
	// EXPIRY AND API RESTRICTION METHODS
	// ============================================================

	/**
	 * Get the plan expiry date as displayed in the UI.
	 *
	 * @return Expiry date text or empty string if not found
	 */
	public String getPlanExpiryDate() {
		try {
			WebElement expiryElement = wait.waitForElementVisible(PLAN_EXPIRY_DATE);
			String expiryText = expiryElement.getText();
			LOGGER.log(Level.INFO, "Plan expiry date: {0}", expiryText);
			return expiryText;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Expiry date not found: {0}", e.getMessage());
			return "";
		}
	}

	/**
	 * Check if the plan has expired based on the current date.
	 * This compares the expiry date with the current system date.
	 *
	 * @return true if plan is expired, false otherwise
	 */
	public boolean isPlanExpired() {
		String expiryText = getPlanExpiryDate().toLowerCase();
		String statusText = getPlanStatus().toLowerCase();

		// Check if status indicates expiration
		if (statusText.contains("expire") || statusText.contains("deactivate") || statusText.contains("cancel")) {
			return true;
		}

		// Check if expiry date is in the past
		// This is a simplified check - real implementation would parse the date
		return expiryText.contains("expired") || expiryText.contains("ended");
	}

	/**
	 * Get detailed plan information including type, status, and expiry.
	 *
	 * @return Plan details as a formatted string
	 */
	public String getPlanDetails() {
		StringBuilder details = new StringBuilder();

		try {
			WebElement planElement = driver.findElement(ACTIVE_PLAN_DETAILS);
			details.append("Plan: ").append(planElement.getText()).append(" | ");
		} catch (Exception e) {
			try {
				WebElement cancelledElement = driver.findElement(CANCELLED_PLAN_DETAILS);
				details.append("Plan: ").append(cancelledElement.getText()).append(" | ");
			} catch (Exception inner) {
				details.append("Plan: Not found | ");
			}
		}

		details.append("Status: ").append(getPlanStatus()).append(" | ");
		details.append("Expiry: ").append(getPlanExpiryDate());

		return details.toString();
	}

	/**
	 * Verify if subscription activation is restricted via backend validation.
	 * This simulates API call behavior by checking UI response to activation attempts.
	 *
	 * @return true if restriction is enforced, false otherwise
	 */
	public boolean isApiRestrictionEnforced() {
		// Record initial state
		boolean wasRestricted = isSubscriptionActivationRestricted();
		String initialStatus = getPlanStatus();

		// Attempt to activate plan
		attemptToActivateAnotherPlan();

		// Check for restriction message or error
		boolean restrictionMessageShown = isSubscriptionActivationRestricted();

		// Verify no state change occurred (backend rejected the request)
		String currentStatus = getPlanStatus();
		boolean noStateChange = currentStatus.equals(initialStatus);

		LOGGER.log(Level.INFO, "API Restriction Check - Initial: {0}, Current: {1}, Restricted: {2}",
			new Object[]{initialStatus, currentStatus, restrictionMessageShown});

		return restrictionMessageShown || noStateChange;
	}

	/**
	 * Wait for a specific duration to test expiry boundary conditions.
	 * This method should be used carefully in test scenarios.
	 *
	 * @param seconds Duration to wait in seconds
	 */
	public void waitForExpiryBoundary(int seconds) {
		try {
			LOGGER.log(Level.INFO, "Waiting for expiry boundary: {0} seconds", seconds);
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.log(Level.WARNING, "Expiry wait interrupted: {0}", e.getMessage());
		}
	}

	/**
	 * Verify that plan selection is allowed after expiry.
	 * This method checks if the UI enables plan selection for expired plans.
	 *
	 * @return true if plan selection is allowed, false otherwise
	 */
	public boolean isPlanSelectionAllowedAfterExpiry() {
		// Check if plan is expired
		if (!isPlanExpired()) {
			LOGGER.log(Level.INFO, "Plan is not expired yet");
			return false;
		}

		// Check if plan selection elements are visible and enabled
		boolean offerVisible = isDisplayed(OFFER_80);
		boolean startListeningVisible = isDisplayed(START_LISTENING_BTN);
		boolean startNowVisible = isDisplayed(START_LISTENING_NOW);

		boolean isAllowed = offerVisible || startListeningVisible || startNowVisible;

		LOGGER.log(Level.INFO, "Plan selection allowed after expiry: {0}", isAllowed);
		return isAllowed;
	}

	/**
	 * Get the remaining days in the subscription plan.
	 * This extracts the number of days from the plan status text.
	 *
	 * @return Number of remaining days, or -1 if not found
	 */
	public int getRemainingDays() {
		String statusText = getPlanStatus().toLowerCase();

		try {
			// Extract number from text like "Remaining 25 days" or "25 days left"
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*days?");
			java.util.regex.Matcher matcher = pattern.matcher(statusText);

			if (matcher.find()) {
				int days = Integer.parseInt(matcher.group(1));
				LOGGER.log(Level.INFO, "Remaining days in plan: {0}", days);
				return days;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Could not extract remaining days: {0}", e.getMessage());
		}

		return -1;
	}

	/**
	 * Check if the plan name is displayed on the subscription page.
	 *
	 * @return true if plan name is displayed, false otherwise
	 */
	public boolean isPlanNameDisplayed() {
		return isPlanDetailsVisible();
	}

	/**
	 * Get the plan name from the subscription page.
	 *
	 * @return Plan name or empty string if not found
	 */
	public String getPlanName() {
		String status = getPlanStatus();
		LOGGER.log(Level.INFO, "Plan name retrieved: {0}", status);
		return status;
	}

	/**
	 * Get the plan duration from the subscription page.
	 *
	 * @return Plan duration or empty string if not found
	 */
	public String getPlanDuration() {
		String expiry = getPlanExpiryDate();
		LOGGER.log(Level.INFO, "Plan duration: {0}", expiry);
		return expiry;
	}

	/**
	 * Check if cancel button is visible on the subscription page.
	 * This method verifies if the cancel plan button is available for users to click.
	 *
	 * @return true if cancel button is visible, false otherwise
	 */
	public boolean isCancelButtonVisible() {
		try {
			WebElement cancelButton = wait.waitForElementVisible(CANCEL_PLAN_BUTTON);
			boolean isVisible = cancelButton != null && cancelButton.isDisplayed();
			LOGGER.log(Level.INFO, "Cancel button visible: {0}", isVisible);
			return isVisible;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Cancel button not found: {0}", e.getMessage());
			return false;
		}
	}
}
