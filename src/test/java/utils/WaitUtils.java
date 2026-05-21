package utils;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration POLLING_INTERVAL = Duration.ofMillis(500);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this(driver, DEFAULT_TIMEOUT);
    }

    public WaitUtils(WebDriver driver, Duration timeout) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null");
        }
        this.driver = driver;
        int seconds = (int) timeout.getSeconds();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Math.max(1, seconds)));
        this.wait.pollingEvery(POLLING_INTERVAL);
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    public WaitUtils(WebDriver driver, int timeoutSeconds) {
        this(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // ==================== Element Visibility ====================

    public WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForElementVisible(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .pollingEvery(POLLING_INTERVAL)
                .ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitForAllElementsVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public boolean isElementVisible(By locator) {
        return isElementVisible(locator, Duration.ofSeconds(3));
    }

    public boolean isElementVisible(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Element Clickable ====================

    public WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForElementClickable(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .pollingEvery(POLLING_INTERVAL)
                .ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean isElementClickable(By locator) {
        return isElementClickable(locator, Duration.ofSeconds(3));
    }

    public boolean isElementClickable(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.elementToBeClickable(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Element Presence ====================

    public WebElement waitForElementPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WebElement> waitForAllElementsPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    // ==================== Element Invisibility ====================

    public boolean waitForElementInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean waitForElementInvisible(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .pollingEvery(POLLING_INTERVAL)
                .ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean isElementInvisible(By locator) {
        return isElementInvisible(locator, Duration.ofSeconds(3));
    }

    public boolean isElementInvisible(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Text Conditions ====================

    public boolean waitForTextToBePresentInElement(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text)) != null;
    }

    public boolean waitForTextToBePresentInElementValue(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementValue(locator, text));
    }

    // ==================== Attribute Conditions ====================

    public boolean waitForAttributeContains(By locator, String attribute, String value) {
        return wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
    }

    public boolean waitForAttributeToBe(By locator, String attribute, String value) {
        return wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    // ==================== Frame Conditions ====================

    public void waitForFrameAndSwitch(By locator) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    public void waitForFrameAndSwitch(int frameIndex) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIndex));
    }

    // ==================== Window/URL Conditions ====================

    public boolean waitForNumberOfWindows(int expectedCount) {
        return wait.until(ExpectedConditions.numberOfWindowsToBe(expectedCount));
    }

    public boolean waitForUrlContains(String fraction) {
        return wait.until(ExpectedConditions.urlContains(fraction));
    }

    public boolean waitForUrlMatches(String regex) {
        return wait.until(ExpectedConditions.urlMatches(regex));
    }

    public boolean waitForTitleContains(String title) {
        return wait.until(ExpectedConditions.titleContains(title));
    }

    public boolean waitForTitleIs(String title) {
        return wait.until(ExpectedConditions.titleIs(title));
    }

    // ==================== Page Load ====================

    public void waitForPageLoad() {
        wait.until(driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object state = js.executeScript("return document.readyState");
            return state != null && "complete".equals(state.toString());
        });
    }

    public void waitForPageLoad(Duration timeout) {
        new WebDriverWait(driver, timeout)
                .until(driver -> {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    Object state = js.executeScript("return document.readyState");
                    return state != null && "complete".equals(state.toString());
                });
    }

    // ==================== AJAX/Loader ====================

    public void waitForLoaderToDisappear(By loaderLocator) {
        try {
            waitForElementInvisible(loaderLocator);
        } catch (Exception e) {
            // Loader not found or already gone
        }
    }

    public void waitForLoaderToDisappear(By loaderLocator, Duration timeout) {
        try {
            waitForElementInvisible(loaderLocator, timeout);
        } catch (Exception e) {
            // Loader not found or already gone
        }
    }

    public void waitForAjaxToComplete() {
        wait.until(driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object ajaxComplete = js.executeScript("return typeof jQuery != 'undefined' ? jQuery.active == 0 : true");
            return ajaxComplete != null && (Boolean) ajaxComplete;
        });
    }

    public void waitForAjaxToComplete(Duration timeout) {
        new WebDriverWait(driver, timeout)
                .until(driver -> {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    Object ajaxComplete = js.executeScript("return typeof jQuery != 'undefined' ? jQuery.active == 0 : true");
                    return ajaxComplete != null && (Boolean) ajaxComplete;
                });
    }

    // ==================== Toast Messages ====================

    public void waitForToastToDisappear() {
        waitForToastToDisappear(Duration.ofSeconds(5));
    }

    public void waitForToastToDisappear(Duration timeout) {
        try {
            By toastLocator = By.xpath("//*[contains(@class,'toast') or contains(@class,'alert')]");
            waitForElementInvisible(toastLocator, timeout);
        } catch (Exception e) {
            // Toast may not exist
        }
    }

    public boolean waitForToastToAppear(By toastLocator) {
        return waitForElementVisible(toastLocator) != null;
    }

    // ==================== Navigation ====================

    public void waitForUrlToContain(String url) {
        waitForUrlContains(url);
    }

    // ==================== Retry Logic ====================

    public WebElement waitWithRetry(By locator, int maxAttempts) {
        return waitWithRetry(locator, maxAttempts, Duration.ofSeconds(1));
    }

    public WebElement waitWithRetry(By locator, int maxAttempts, Duration delayBetweenAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            try {
                WebElement element = waitForElementVisible(locator);
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                if (i == maxAttempts - 1) {
                    throw new NoSuchElementException("Element not found after " + maxAttempts + " attempts: " + locator);
                }
                sleep(delayBetweenAttempts);
            }
        }
        throw new NoSuchElementException("Element not found: " + locator);
    }

    public <T> T waitForFunction(Function<? super WebDriver, T> condition) {
        return wait.until(condition);
    }

    public <T> T waitForFunction(Function<? super WebDriver, T> condition, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .pollingEvery(POLLING_INTERVAL)
                .until(condition);
    }

    // ==================== Helper Methods ====================

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Wait for a specified duration in milliseconds.
     * Use for retry delays in retry loops.
     */
    public void waitForMilliseconds(long milliseconds) {
        sleep(Duration.ofMillis(milliseconds));
    }

    public WebDriver getDriver() {
        return driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }
}