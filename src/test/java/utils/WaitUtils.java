package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
<<<<<<< HEAD

=======
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
<<<<<<< HEAD
 * Utility wrapper around WebDriverWait. Provides reusable explicit wait
 * methods.
 */
public class WaitUtils {

	private static final Logger LOGGER = Logger.getLogger(WaitUtils.class.getName());

	private final WebDriver driver;
	private final WebDriverWait wait;

	public WaitUtils(WebDriver driver) {

		if (driver == null)
			throw new IllegalArgumentException("WebDriver must not be null");

		this.driver = driver;

		int timeout = ConfigReader.getInt("explicitWait", 10);

		this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
	}

	// 🔥 Wait for visibility
	public WebElement waitForElementVisible(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	// 🔥 Wait for clickable
	public WebElement waitForElementClickable(By locator) {
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	// 🔥 Wait for presence (DOM only)
	public WebElement waitForElementPresent(By locator) {
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	// 🔥 Wait for invisibility
	public boolean waitForElementInvisible(By locator) {
		return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	// 🔥 Wait for title
	public void waitForTitleContains(String title) {
		wait.until(ExpectedConditions.titleContains(title));
	}

	// 🔥 Generic wait (keep your method)
	public WebElement until(ExpectedCondition<WebElement> condition) {
		return wait.until(condition);
	}

	// 🔥 Wait for page load
	public void waitForPageLoad() {
		wait.until(
				driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
	}

	// 🔥 Wait for loader/spinner (VERY IMPORTANT in real apps)
	public void waitForLoaderToDisappear(By loaderLocator) {
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Loader not found or already gone: {0}", e.getMessage());
		}
	}

	// 🔥 Improved toast handling
	public void waitForToastToDisappear() {
		try {
			wait.until(ExpectedConditions
					.invisibilityOfElementLocated(By.xpath("//*[contains(text(),'Could not share')]")));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Toast not present: {0}", e.getMessage());
		}
	}

	// 🔥 Safe sleep (use only when needed)
	public void waitForSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.log(Level.FINE, "Interrupted while waiting: {0}", e.getMessage());
		}
	}

	// 🔥 Retry wait (handles flaky elements)
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
=======
 * Small utility wrapper around WebDriverWait.
 * Provides commonly used wait helpers for page objects and tests.
 */
public class WaitUtils {

    private static final Logger LOGGER = Logger.getLogger(WaitUtils.class.getName());

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        if (driver == null) throw new IllegalArgumentException("WebDriver must not be null");
        this.driver = driver;
        int timeout = 10;
        try {
            String cfg = ConfigReader.getProperty("explicitWait");
            if (cfg != null && !cfg.isBlank()) timeout = Integer.parseInt(cfg);
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Invalid explicitWait value, defaulting to 10s: {0}", e.getMessage());
        }
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public WebElement waitForElementVisible(By locator) {

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForTitleContains(String title) {
        wait.until(ExpectedConditions.titleContains(title));
    }

    public WebElement until(ExpectedCondition<WebElement> condition) {
        return wait.until(condition);
    }

    public void waitForToastToDisappear() {

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            shortWait.until(ExpectedConditions
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

>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
}