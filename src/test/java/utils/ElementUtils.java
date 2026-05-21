package utils;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

/**
 * Enterprise-grade element interaction utilities with safe methods that handle
 * common Selenium issues like StaleElementReferenceException and ElementClickInterceptedException.
 * Uses explicit waits instead of Thread.sleep.
 */
public class ElementUtils {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    public ElementUtils(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    // ==================== Safe Click Methods ====================

    public void safeClick(By locator) {
        safeClick(locator, 3);
    }

    public void safeClick(By locator, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                WebElement element = waitUtils.waitForElementClickable(locator);
                scrollIntoView(element);
                element.click();
                return;
            } catch (StaleElementReferenceException e) {
                if (i == maxRetries - 1) {
                    throw e;
                }
            } catch (ElementClickInterceptedException e) {
                if (i == maxRetries - 1) {
                    jsClick(locator);
                    return;
                }
                waitForPageLoad();
            }
        }
    }

    public void safeClick(WebElement element) {
        safeClick(element, 3);
    }

    public void safeClick(WebElement element, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                scrollIntoView(element);
                element.click();
                return;
            } catch (StaleElementReferenceException e) {
                if (i == maxRetries - 1) {
                    throw e;
                }
            } catch (ElementClickInterceptedException e) {
                if (i == maxRetries - 1) {
                    jsClick(element);
                    return;
                }
                waitForPageLoad();
            }
        }
    }

    public void jsClick(By locator) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        jsClick(element);
    }

    public void jsClick(WebElement element) {
        scrollIntoView(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void actionsClick(By locator) {
        WebElement element = waitUtils.waitForElementClickable(locator);
        scrollIntoView(element);
        new Actions(driver).click(element).perform();
    }

    // ==================== Safe SendKeys Methods ====================

    public void safeSendKeys(By locator, String text) {
        safeSendKeys(locator, text, 3);
    }

    public void safeSendKeys(By locator, String text, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                WebElement element = waitUtils.waitForElementVisible(locator);
                scrollIntoView(element);
                element.clear();

                if (containsNonBmpCharacters(text)) {
                    setValueUsingJs(element, text);
                } else {
                    element.sendKeys(text);
                }
                return;
            } catch (StaleElementReferenceException e) {
                if (i == maxRetries - 1) {
                    throw e;
                }
            }
        }
    }

    public void safeSendKeys(WebElement element, String text) {
        scrollIntoView(element);
        element.clear();

        if (containsNonBmpCharacters(text)) {
            setValueUsingJs(element, text);
        } else {
            element.sendKeys(text);
        }
    }

    public void appendText(By locator, String text) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        scrollIntoView(element);
        element.sendKeys(text);
    }

    public void sendSpecialKey(By locator, Keys key) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        element.sendKeys(key);
    }

    // ==================== Safe Get Text Methods ====================

    public String safeGetText(By locator) {
        return safeGetText(locator, "");
    }

    public String safeGetText(By locator, String defaultValue) {
        try {
            return waitUtils.waitForElementVisible(locator).getText().trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String safeGetText(WebElement element) {
        return safeGetText(element, "");
    }

    public String safeGetText(WebElement element, String defaultValue) {
        try {
            return element.getText().trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // ==================== Safe Get Attribute Methods ====================

    public String safeGetAttribute(By locator, String attribute) {
        return safeGetAttribute(locator, attribute, "");
    }

    public String safeGetAttribute(By locator, String attribute, String defaultValue) {
        try {
            return waitUtils.waitForElementVisible(locator).getAttribute(attribute);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String safeGetAttribute(WebElement element, String attribute) {
        return safeGetAttribute(element, attribute, "");
    }

    public String safeGetAttribute(WebElement element, String attribute, String defaultValue) {
        try {
            return element.getAttribute(attribute);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // ==================== Displayed/Enabled/Selected State ====================

    public boolean isDisplayed(By locator) {
        return waitUtils.isElementVisible(locator);
    }

    public boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled(By locator) {
        try {
            return waitUtils.waitForElementVisible(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSelected(By locator) {
        try {
            return waitUtils.waitForElementVisible(locator).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSelected(WebElement element) {
        try {
            return element.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Find Elements ====================

    public WebElement findElement(By locator) {
        return waitUtils.waitForElementVisible(locator);
    }

    public WebElement findElement(By locator, Duration timeout) {
        return waitUtils.waitForElementVisible(locator, timeout);
    }

    public List<WebElement> findElements(By locator) {
        return waitUtils.waitForAllElementsVisible(locator);
    }

    public WebElement findElementOrNull(By locator) {
        return findElementOrNull(locator, Duration.ofSeconds(3));
    }

    public WebElement findElementOrNull(By locator, Duration timeout) {
        try {
            return waitUtils.waitForElementVisible(locator, timeout);
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== Dropdown/Select Methods ====================

    public void selectByVisibleText(By locator, String text) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        new Select(element).selectByVisibleText(text);
    }

    public void selectByValue(By locator, String value) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        new Select(element).selectByValue(value);
    }

    public void selectByIndex(By locator, int index) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        new Select(element).selectByIndex(index);
    }

    public String getFirstSelectedOption(By locator) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        return new Select(element).getFirstSelectedOption().getText().trim();
    }

    // ==================== Checkbox/Radio Methods ====================

    public void check(By locator) {
        if (!isSelected(locator)) {
            safeClick(locator);
        }
    }

    public void uncheck(By locator) {
        if (isSelected(locator)) {
            safeClick(locator);
        }
    }

    public void toggle(By locator) {
        safeClick(locator);
    }

    // ==================== Frame Methods ====================

    public void switchToFrame(By locator) {
        waitUtils.waitForFrameAndSwitch(locator);
    }

    public void switchToFrame(int index) {
        driver.switchTo().frame(index);
    }

    public void switchToFrame(WebElement frameElement) {
        driver.switchTo().frame(frameElement);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    // ==================== Scroll Methods ====================

    public void scrollIntoView(By locator) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        scrollIntoView(element);
    }

    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    public void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void scrollBy(int x, int y) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    // ==================== Hover/Focus Methods ====================

    public void hoverOver(By locator) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        new Actions(driver).moveToElement(element).perform();
    }

    public void hoverOver(WebElement element) {
        new Actions(driver).moveToElement(element).perform();
    }

    public void focusOn(By locator) {
        WebElement element = waitUtils.waitForElementVisible(locator);
        new Actions(driver).moveToElement(element).click().perform();
    }

    public void doubleClick(By locator) {
        WebElement element = waitUtils.waitForElementClickable(locator);
        new Actions(driver).doubleClick(element).perform();
    }

    public void rightClick(By locator) {
        WebElement element = waitUtils.waitForElementClickable(locator);
        new Actions(driver).contextClick(element).perform();
    }

    // ==================== Drag and Drop ====================

    public void dragAndDrop(By sourceLocator, By targetLocator) {
        WebElement source = waitUtils.waitForElementVisible(sourceLocator);
        WebElement target = waitUtils.waitForElementVisible(targetLocator);
        new Actions(driver).dragAndDrop(source, target).perform();
    }

    public void dragAndDrop(By sourceLocator, int xOffset, int yOffset) {
        WebElement source = waitUtils.waitForElementVisible(sourceLocator);
        new Actions(driver).dragAndDropBy(source, xOffset, yOffset).perform();
    }

    // ==================== Helper Methods ====================

    private void waitForPageLoad() {
        waitUtils.waitForPageLoad(Duration.ofSeconds(2));
    }

    private boolean containsNonBmpCharacters(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (Character.isSurrogate(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private void setValueUsingJs(WebElement element, String value) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
            "const el = arguments[0];" +
            "const val = arguments[1] == null ? '' : arguments[1];" +
            "el.focus();" +
            "el.value = val;" +
            "el.dispatchEvent(new Event('input', { bubbles: true }));" +
            "el.dispatchEvent(new Event('change', { bubbles: true }));" +
            "el.blur();",
            element, value);
    }

    public WaitUtils getWaitUtils() {
        return waitUtils;
    }
}
