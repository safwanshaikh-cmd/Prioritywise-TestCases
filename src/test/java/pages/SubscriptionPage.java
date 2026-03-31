package pages;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
		waitForOverlayToDisappear();
		jsClick(START_LISTENING_NOW);
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
}
