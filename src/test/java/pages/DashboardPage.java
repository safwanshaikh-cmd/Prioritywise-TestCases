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

<<<<<<< HEAD
import base.BasePage;

=======
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
/**
 * Page Object representing the Dashboard. Handles interactions such as
 * selecting a book from the carousel and managing cookie consent popups.
 */
public class DashboardPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(DashboardPage.class.getName());

	// Locators
	private static final By BOOK_IMAGES = By.xpath("//img[contains(@src,'sonarplay')]");
	private static final By COOKIE_ACCEPT_BUTTON = By.xpath("//button[contains(text(),'Accept Cookies')]");

	// Wait configuration
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	private static final Duration SHORT_TIMEOUT = Duration.ofSeconds(3);

	private final WebDriverWait wait;

	public DashboardPage(WebDriver driver) {
		super(driver);
		this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
	}

	/**
	 * Opens any visible book from the carousel. Uses viewport detection and
	 * JavaScript click to handle overlays and animations.
	 */
	public void openAnyBook() {
		try {
			waitForBooksToLoad();

			List<WebElement> books = driver.findElements(BOOK_IMAGES);

			for (WebElement book : books) {
				if (isElementInViewport(book)) {
					clickUsingJavaScript(book);
					LOGGER.info("Successfully clicked a visible book from the carousel.");
					return;
				}
			}

			throw new IllegalStateException("No visible book found in viewport.");

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to open a book: {0}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Accepts cookie consent popup if present.
	 */
	public void acceptCookiesIfPresent() {
		try {
			WebDriverWait shortWait = new WebDriverWait(driver, SHORT_TIMEOUT);

			WebElement acceptButton = shortWait
					.until(ExpectedConditions.presenceOfElementLocated(COOKIE_ACCEPT_BUTTON));

			if (acceptButton.isDisplayed()) {
				acceptButton.click();
				LOGGER.info("Cookie consent accepted.");
			}

		} catch (Exception e) {
			LOGGER.fine("Cookie popup not present or already handled.");
		}
	}

	// =========================
	// Private Helper Methods
	// =========================

	/**
	 * Waits until book images are loaded on the page.
	 */
	private void waitForBooksToLoad() {
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(BOOK_IMAGES, 0));
	}

	/**
	 * Checks if an element is within the current viewport.
	 */
	private boolean isElementInViewport(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		return (Boolean) js.executeScript(
				"var rect = arguments[0].getBoundingClientRect();" + "return (rect.top >= 0 && rect.left >= 0 && "
						+ "rect.bottom <= window.innerHeight && rect.right <= window.innerWidth);",
				element);
	}

	/**
	 * Performs a JavaScript-based click to bypass overlay or animation issues.
	 */
	private void clickUsingJavaScript(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", element);
	}
}