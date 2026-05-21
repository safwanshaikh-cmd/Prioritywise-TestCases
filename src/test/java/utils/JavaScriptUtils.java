package utils;

import java.time.Duration;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Enterprise-grade JavaScript execution utilities for Selenium.
 * Provides safe JavaScript interactions that can bypass certain Selenium limitations.
 */
public class JavaScriptUtils {

    private final WebDriver driver;
    private final JavascriptExecutor jsExecutor;

    public JavaScriptUtils(WebDriver driver) {
        this.driver = driver;
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    // ==================== Click Methods ====================

    public void click(By locator) {
        WebElement element = driver.findElement(locator);
        click(element);
    }

    public void click(WebElement element) {
        scrollIntoView(element);
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    public void jsClick(By locator) {
        WebElement element = driver.findElement(locator);
        jsClick(element);
    }

    public void jsClick(WebElement element) {
        scrollIntoView(element);
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    public void clickAndWaitForNavigation(WebElement element, int timeoutSeconds) {
        scrollIntoView(element);
        String script = "arguments[0].click(); return window.location.href;";
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
            .until(d -> {
                String currentUrl = (String) jsExecutor.executeScript(script, element);
                return currentUrl != null && !currentUrl.equals(driver.getCurrentUrl());
            });
    }

    // ==================== Scroll Methods ====================

    public void scrollIntoView(WebElement element) {
        scrollIntoView(element, ScrollAlignment.CENTER);
    }

    public void scrollIntoView(WebElement element, ScrollAlignment alignment) {
        String script = String.format(
            "arguments[0].scrollIntoView({block:'%s', inline:'nearest'});",
            alignment.block);
        jsExecutor.executeScript(script, element);
    }

    public void scrollBy(int x, int y) {
        jsExecutor.executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    public void scrollToTop() {
        jsExecutor.executeScript("window.scrollTo(0, 0);");
    }

    public void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void scrollToElementCenter(WebElement element) {
        jsExecutor.executeScript(
            "var element = arguments[0];" +
            "var elementRect = element.getBoundingClientRect();" +
            "var absoluteElementTop = elementRect.top + window.pageYOffset;" +
            "var middle = absoluteElementTop - (window.innerHeight / 2);" +
            "window.scrollTo(0, middle);",
            element);
    }

    // ==================== Value/Text Methods ====================

    public void setValue(WebElement element, String value) {
        jsExecutor.executeScript(
            "var el = arguments[0];" +
            "var val = arguments[1];" +
            "el.focus();" +
            "el.value = val;" +
            "el.dispatchEvent(new Event('input', { bubbles: true }));" +
            "el.dispatchEvent(new Event('change', { bubbles: true }));" +
            "el.blur();",
            element, value);
    }

    public void setValueWithDelay(WebElement element, String value, int delayMs) {
        jsExecutor.executeScript(
            "var el = arguments[0];" +
            "var val = arguments[1];" +
            "var delay = arguments[2];" +
            "el.focus();" +
            "var index = 0;" +
            "var interval = setInterval(function() {" +
            "  el.value = val.substring(0, index + 1);" +
            "  el.dispatchEvent(new Event('input', { bubbles: true }));" +
            "  index++;" +
            "  if (index >= val.length) {" +
            "    clearInterval(interval);" +
            "    el.blur();" +
            "    el.dispatchEvent(new Event('change', { bubbles: true }));" +
            "  }" +
            "}, delay);",
            element, value, delayMs);
    }

    public String getValue(WebElement element) {
        return (String) jsExecutor.executeScript("return arguments[0].value;", element);
    }

    public void clearValue(WebElement element) {
        jsExecutor.executeScript("arguments[0].value = '';", element);
        element.clear();
    }

    public String getText(WebElement element) {
        return (String) jsExecutor.executeScript("return arguments[0].textContent;", element);
    }

    public String getInnerText(WebElement element) {
        return (String) jsExecutor.executeScript("return arguments[0].innerText;", element);
    }

    // ==================== Attribute Methods ====================

    public void setAttribute(WebElement element, String attribute, String value) {
        jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attribute, value);
    }

    public void removeAttribute(WebElement element, String attribute) {
        jsExecutor.executeScript("arguments[0].removeAttribute(arguments[1]);", element, attribute);
    }

    public String getAttribute(WebElement element, String attribute) {
        Object result = jsExecutor.executeScript("return arguments[0].getAttribute(arguments[1]);", element, attribute);
        return result != null ? result.toString() : null;
    }

    // ==================== Style Methods ====================

    public void highlightElement(WebElement element) {
        jsExecutor.executeScript(
            "arguments[0].style.border='3px solid red';" +
            "arguments[0].style.backgroundColor='yellow';",
            element);
    }

    public void unhighlightElement(WebElement element) {
        jsExecutor.executeScript(
            "arguments[0].style.border='';" +
            "arguments[0].style.backgroundColor='';",
            element);
    }

    public boolean isElementInView(WebElement element) {
        Boolean result = (Boolean) jsExecutor.executeScript(
            "var elem = arguments[0];" +
            "var rect = elem.getBoundingClientRect();" +
            "return (rect.top >= 0 && rect.left >= 0 && " +
            "rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && " +
            "rect.right <= (window.innerWidth || document.documentElement.clientWidth));",
            element);
        return result != null && result;
    }

    // ==================== Page/Document Methods ====================

    public String getPageTitle() {
        return (String) jsExecutor.executeScript("return document.title;");
    }

    public String getCurrentUrl() {
        return (String) jsExecutor.executeScript("return window.location.href;");
    }

    public void navigateTo(String url) {
        jsExecutor.executeScript("window.location.href = arguments[0];", url);
    }

    public void navigateBack() {
        jsExecutor.executeScript("window.history.back();");
    }

    public void navigateForward() {
        jsExecutor.executeScript("window.history.forward();");
    }

    public void refreshPage() {
        jsExecutor.executeScript("window.location.reload();");
    }

    public boolean isPageReady() {
        Object result = jsExecutor.executeScript("return document.readyState");
        return result != null && "complete".equals(result.toString());
    }

    public void waitForPageReady(int timeoutSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
            .until(d -> isPageReady());
    }

    // ==================== AJAX/Fetch Methods ====================

    public boolean isAjaxComplete() {
        Object result = jsExecutor.executeScript(
            "return (typeof jQuery != 'undefined' && jQuery.active == 0) || " +
            "(typeof fetch != 'undefined' && !window.pendingRequests);");
        return result == null || (Boolean) result;
    }

    public int getAjaxCallsInProgress() {
        Object result = jsExecutor.executeScript(
            "return typeof jQuery != 'undefined' ? jQuery.active : 0;");
        return result != null ? ((Number) result).intValue() : 0;
    }

    public void waitForAjaxComplete(int timeoutSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
            .until(d -> isAjaxComplete());
    }

    // ==================== Visibility Methods ====================

    public void hideElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].style.display = 'none';", element);
    }

    public void showElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].style.display = '';", element);
    }

    public void removeElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].remove();", element);
    }

    public boolean isElementDisplayed(WebElement element) {
        Object result = jsExecutor.executeScript(
            "var el = arguments[0];" +
            "return el.offsetWidth > 0 && el.offsetHeight > 0 && " +
            "getComputedStyle(el).visibility != 'hidden' && " +
            "getComputedStyle(el).display != 'none';",
            element);
        return result == null || (Boolean) result;
    }

    // ==================== Hover/Focus Methods ====================

    public void hover(WebElement element) {
        jsExecutor.executeScript(
            "var event = new MouseEvent('mouseover', {" +
            "  'view': window, 'bubbles': true, 'cancelable': true" +
            "});" +
            "arguments[0].dispatchEvent(event);",
            element);
    }

    public void focus(WebElement element) {
        jsExecutor.executeScript("arguments[0].focus();", element);
    }

    public void blur(WebElement element) {
        jsExecutor.executeScript("arguments[0].blur();", element);
    }

    // ==================== Generic Script Execution ====================

    public Object executeScript(String script, Object... args) {
        return jsExecutor.executeScript(script, args);
    }

    public Object executeAsyncScript(String script, Object... args) {
        return jsExecutor.executeAsyncScript(script, args);
    }

    // ==================== Enum for Scroll Alignment ====================

    public enum ScrollAlignment {
        TOP("start"),
        CENTER("center"),
        BOTTOM("end");

        private final String block;

        ScrollAlignment(String block) {
            this.block = block;
        }
    }
}