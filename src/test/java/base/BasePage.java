package base;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utils.ElementUtils;
import utils.ScreenshotUtils;
import utils.WaitUtils;

/**
 * BasePage provides common WebDriver helpers used by all page objects.
 * Enhanced with retry logic, logging, and screenshot support.
 * Uses explicit waits throughout - no Thread.sleep usage.
 */
public class BasePage {

    protected static final Logger LOGGER = Logger.getLogger(BasePage.class.getName());

    protected WebDriver driver;
    protected WaitUtils wait;
    protected ElementUtils elementUtils;

    public BasePage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null");
        }
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        this.elementUtils = new ElementUtils(driver);
    }

    // ==================== Click Methods ====================

    /**
     * Click on element with retry and JS fallback.
     */
    public void click(By locator) {
        try {
            elementUtils.safeClick(locator, 3);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Standard click failed, falling back to JS click: {0}", e.getMessage());
            jsClick(locator);
        }
    }

    /**
     * Click on element using JavaScript (fallback method).
     */
    public void jsClick(By locator) {
        elementUtils.jsClick(locator);
    }

    /**
     * Click on WebElement with safe handling.
     */
    public void click(WebElement element) {
        elementUtils.safeClick(element, 3);
    }

    /**
     * Click using Actions class for special cases.
     */
    public void actionsClick(By locator) {
        elementUtils.actionsClick(locator);
    }

    // ==================== Input Methods ====================

    /**
     * Type text into element with proper clearing and non-BMP character handling.
     */
    public void type(By locator, String text) {
        elementUtils.safeSendKeys(locator, text, 3);
    }

    /**
     * Type using WebElement.
     */
    public void type(WebElement element, String text) {
        elementUtils.safeSendKeys(element, text);
    }

    /**
     * Append text without clearing.
     */
    public void appendText(By locator, String text) {
        elementUtils.appendText(locator, text);
    }

    /**
     * Send special keys (Enter, Tab, etc.)
     */
    public void sendKeys(By locator, Keys key) {
        elementUtils.sendSpecialKey(locator, key);
    }

    // ==================== Get Text Methods ====================

    /**
     * Get text from element safely.
     */
    public String getText(By locator) {
        return elementUtils.safeGetText(locator, "");
    }

    /**
     * Get text from WebElement safely.
     */
    public String getText(WebElement element) {
        return elementUtils.safeGetText(element, "");
    }

    // ==================== Attribute Methods ====================

    /**
     * Get attribute value safely.
     */
    public String getAttribute(By locator, String attribute) {
        return elementUtils.safeGetAttribute(locator, attribute, "");
    }

    /**
     * Get attribute from WebElement.
     */
    public String getAttribute(WebElement element, String attribute) {
        return elementUtils.safeGetAttribute(element, attribute, "");
    }

    // ==================== State Methods ====================

    /**
     * Check if element is displayed.
     */
    public boolean isDisplayed(By locator) {
        return elementUtils.isDisplayed(locator);
    }

    /**
     * Check if WebElement is displayed.
     */
    public boolean isDisplayed(WebElement element) {
        return elementUtils.isDisplayed(element);
    }

    /**
     * Check if element is enabled.
     */
    public boolean isEnabled(By locator) {
        return elementUtils.isEnabled(locator);
    }

    /**
     * Check if element is selected.
     */
    public boolean isSelected(By locator) {
        return elementUtils.isSelected(locator);
    }

    // ==================== Visibility Wait Methods ====================

    /**
     * Wait for element to be visible.
     */
    public WebElement waitForVisibility(By locator) {
        return wait.waitForElementVisible(locator);
    }

    /**
     * Wait for element to be visible with custom timeout.
     */
    public WebElement waitForVisibility(By locator, Duration timeout) {
        return wait.waitForElementVisible(locator, timeout);
    }

    /**
     * Wait for element to be clickable.
     */
    public WebElement waitForClickable(By locator) {
        return wait.waitForElementClickable(locator);
    }

    /**
     * Wait for element to be clickable with custom timeout.
     */
    public WebElement waitForClickable(By locator, Duration timeout) {
        return wait.waitForElementClickable(locator, timeout);
    }

    /**
     * Wait for element to be invisible/hidden.
     */
    public boolean waitForInvisibility(By locator) {
        return wait.waitForElementInvisible(locator);
    }

    // ==================== Frame Methods ====================

    /**
     * Switch to frame by locator.
     */
    public void switchToFrame(By locator) {
        elementUtils.switchToFrame(locator);
    }

    /**
     * Switch to frame by index.
     */
    public void switchToFrame(int index) {
        elementUtils.switchToFrame(index);
    }

    /**
     * Switch to default content.
     */
    public void switchToDefault() {
        elementUtils.switchToDefaultContent();
    }

    // ==================== Scroll Methods ====================

    /**
     * Scroll element into view.
     */
    public void scrollIntoView(By locator) {
        elementUtils.scrollIntoView(locator);
    }

    /**
     * Scroll WebElement into view.
     */
    public void scrollIntoView(WebElement element) {
        elementUtils.scrollIntoView(element);
    }

    /**
     * Scroll to top of page.
     */
    public void scrollToTop() {
        elementUtils.scrollToTop();
    }

    /**
     * Scroll to bottom of page.
     */
    public void scrollToBottom() {
        elementUtils.scrollToBottom();
    }

    // ==================== Hover/Focus Methods ====================

    /**
     * Hover over element.
     */
    public void hoverOver(By locator) {
        elementUtils.hoverOver(locator);
    }

    /**
     * Hover over WebElement.
     */
    public void hoverOver(WebElement element) {
        elementUtils.hoverOver(element);
    }

    /**
     * Double click on element.
     */
    public void doubleClick(By locator) {
        elementUtils.doubleClick(locator);
    }

    // ==================== Select/Dropdown Methods ====================

    /**
     * Select option by visible text.
     */
    public void selectByVisibleText(By locator, String text) {
        elementUtils.selectByVisibleText(locator, text);
    }

    /**
     * Get first selected option text.
     */
    public String getFirstSelectedOption(By locator) {
        return elementUtils.getFirstSelectedOption(locator);
    }

    // ==================== Navigation Methods ====================

    /**
     * Get current URL.
     */
    public String getCurrentUrl() {
        return Objects.requireNonNull(driver.getCurrentUrl());
    }

    /**
     * Refresh page and wait for load.
     */
    public void refresh() {
        driver.navigate().refresh();
        wait.waitForPageLoad();
    }

    /**
     * Navigate back.
     */
    public void navigateBack() {
        driver.navigate().back();
    }

    /**
     * Navigate forward.
     */
    public void navigateForward() {
        driver.navigate().forward();
    }

    // ==================== Wait/Loader Methods ====================

    /**
     * Wait for page to fully load.
     */
    public void waitForPageLoad() {
        wait.waitForPageLoad();
    }

    /**
     * Wait for loader/spinner to disappear.
     */
    public void waitForLoaderToDisappear(By loaderLocator) {
        wait.waitForLoaderToDisappear(loaderLocator);
    }

    /**
     * Wait for loader with custom timeout.
     */
    public void waitForLoaderToDisappear(By loaderLocator, Duration timeout) {
        wait.waitForLoaderToDisappear(loaderLocator, timeout);
    }

    /**
     * Wait for AJAX to complete.
     */
    public void waitForAjax() {
        wait.waitForAjaxToComplete();
    }

    /**
     * Wait for toast message to disappear.
     */
    public void waitForToastToDisappear() {
        wait.waitForToastToDisappear();
    }

    // ==================== Find Elements ====================

    /**
     * Find single element.
     */
    public WebElement findElement(By locator) {
        return wait.waitForElementVisible(locator);
    }

    /**
     * Find multiple elements.
     */
    public List<WebElement> findElements(By locator) {
        return wait.waitForAllElementsVisible(locator);
    }

    /**
     * Check if element exists (present in DOM).
     */
    public boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    // ==================== Checkbox Methods ====================

    /**
     * Check checkbox if not already checked.
     */
    public void check(By locator) {
        elementUtils.check(locator);
    }

    /**
     * Uncheck checkbox if not already unchecked.
     */
    public void uncheck(By locator) {
        elementUtils.uncheck(locator);
    }

    // ==================== Screenshot Helper ====================

    /**
     * Take screenshot with custom name.
     */
    protected void takeScreenshot(String name) {
        try {
            ScreenshotUtils.capture(driver, name + "_" + System.currentTimeMillis());
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Screenshot capture failed: {0}", e.getMessage());
        }
    }

    // ==================== URL Helpers ====================

    /**
     * Check if current URL contains text.
     */
    public boolean isUrlContaining(String text) {
        return getCurrentUrl().toLowerCase().contains(text.toLowerCase());
    }

    /**
     * Wait for URL to contain text.
     */
    public void waitForUrlContains(String text) {
        wait.waitForUrlContains(text);
    }

    // ==================== Title Helpers ====================

    /**
     * Get page title.
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Check if title contains text.
     */
    public boolean isTitleContaining(String text) {
        return getPageTitle().toLowerCase().contains(text.toLowerCase());
    }

    /**
     * Wait for title to contain text.
     */
    public void waitForTitleContains(String text) {
        wait.waitForTitleContains(text);
    }

    // ==================== Get Driver/Utils ====================

    public WebDriver getDriver() {
        return driver;
    }

    public WaitUtils getWaitUtils() {
        return wait;
    }

    public ElementUtils getElementUtils() {
        return elementUtils;
    }

    // ==================== JavaScript Utilities ====================

    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    public String getBodyText() {
        return (String) ((JavascriptExecutor) driver).executeScript("return document.body ? document.body.innerText : '';");
    }

    public String getStringFromScript(String script, Object... args) {
        Object result = ((JavascriptExecutor) driver).executeScript(script, args);
        return result == null ? "" : String.valueOf(result);
    }

    public long getLongFromScript(String script, Object... args) {
        Object result = ((JavascriptExecutor) driver).executeScript(script, args);
        if (result instanceof Number) {
            return ((Number) result).longValue();
        }
        return 0L;
    }

    public boolean getBooleanFromScript(String script, Object... args) {
        Object result = ((JavascriptExecutor) driver).executeScript(script, args);
        return result instanceof Boolean && (Boolean) result;
    }

    public int countElements(String selector) {
        try {
            long count = getLongFromScript("return document.querySelectorAll(arguments[0]).length;", selector);
            return (int) count;
        } catch (Exception e) {
            return 0;
        }
    }

    public int countClickableElements() {
        try {
            return (int) getLongFromScript(
                "return document.querySelectorAll('button, [onclick], [role=\"button\"], a').length;");
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean checkElementExists(String selector) {
        return countElements(selector) > 0;
    }
}
