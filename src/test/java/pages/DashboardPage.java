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
 * Page object representing the dashboard.
 */
public class DashboardPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(DashboardPage.class.getName());
	private static final By BOOK_IMAGES = By.xpath("//img[contains(@src,'sonarplay')]");
	private static final By COOKIE_ACCEPT_BUTTON = By.xpath(
			"//button[contains(normalize-space(.),'Accept Cookies')]"
					+ " | //div[@tabindex='0' and contains(normalize-space(.),'Accept Cookies')]"
					+ " | //*[@role='button' and contains(normalize-space(.),'Accept Cookies')]");
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	private static final Duration SHORT_TIMEOUT = Duration.ofSeconds(3);

	private final WebDriverWait pageWait;

	public DashboardPage(WebDriver driver) {
		super(driver);
		this.pageWait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
	}

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

	public void acceptCookiesIfPresent() {
		try {
			WebDriverWait shortWait = new WebDriverWait(driver, SHORT_TIMEOUT);
			WebElement acceptButton = shortWait.until(ExpectedConditions.visibilityOfElementLocated(COOKIE_ACCEPT_BUTTON));

			if (acceptButton.isDisplayed()) {
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].scrollIntoView({block:'center', inline:'nearest'});", acceptButton);
				try {
					acceptButton.click();
				} catch (Exception e) {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptButton);
				}
				LOGGER.info("Cookie consent accepted.");
			}
		} catch (Exception e) {
			LOGGER.fine("Cookie popup not present or already handled.");
		}
	}

	private void waitForBooksToLoad() {
		pageWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(BOOK_IMAGES, 0));
	}

	private boolean isElementInViewport(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (Boolean) js.executeScript(
				"var rect = arguments[0].getBoundingClientRect();"
						+ "return (rect.top >= 0 && rect.left >= 0 && rect.bottom <= window.innerHeight && rect.right <= window.innerWidth);",
				element);
	}

	private void clickUsingJavaScript(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", element);
	}
}
