package utils;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Utility wrapper around WebDriverWait with reusable explicit wait helpers.
 */
public class WaitUtils {

	private static final Logger LOGGER = Logger.getLogger(WaitUtils.class.getName());

	private final WebDriverWait wait;

	public WaitUtils(WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("WebDriver must not be null");
		}

		int timeout = ConfigReader.getInt("explicitWait", 10);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
	}

	public WebElement waitForElementVisible(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public WebElement waitForElementClickable(By locator) {
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	public WebElement waitForElementPresent(By locator) {
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	public boolean waitForElementInvisible(By locator) {
		return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	public void waitForTitleContains(String title) {
		wait.until(ExpectedConditions.titleContains(title));
	}

	public WebElement until(ExpectedCondition<WebElement> condition) {
		return wait.until(condition);
	}

	public void waitForPageLoad() {
		wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
	}

	public void waitForLoaderToDisappear(By loaderLocator) {
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Loader not found or already gone: {0}", e.getMessage());
		}
	}

	public void waitForToastToDisappear() {
		try {
			wait.until(ExpectedConditions
					.invisibilityOfElementLocated(By.xpath("//*[contains(text(),'Could not share')]")));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Toast not present: {0}", e.getMessage());
		}
	}

	public void waitForSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.log(Level.FINE, "Interrupted while waiting: {0}", e.getMessage());
		}
	}

	public WebElement waitWithRetry(By locator, int attempts) {

		for (int i = 0; i < attempts; i++) {
			try {
				return waitForElementVisible(locator);
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Retry {0} failed for {1}", new Object[] { i + 1, locator });
			}
		}

		throw new NoSuchElementException("Element not found after retries: " + locator);
	}
}
