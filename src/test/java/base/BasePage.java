package base;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utils.ScreenshotUtils;
import utils.WaitUtils;

/**
 * BasePage provides common WebDriver helpers used by page objects. Enhanced
 * with retry, logging, and screenshot support.
 */
public class BasePage {

	private static final Logger LOGGER = Logger.getLogger(BasePage.class.getName());

	protected WebDriver driver;
	protected WaitUtils wait;

	public BasePage(WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("WebDriver must not be null");
		}
		this.driver = driver;
		this.wait = new WaitUtils(driver);
	}

	// 🔥 Click with retry + fallback + logging
	public void click(By locator) {
		if (locator == null) {
			throw new IllegalArgumentException("Locator must not be null");
		}

		int attempts = 0;

		while (attempts < 2) {
			try {
				WebElement element = wait.waitForElementClickable(locator);
				scrollIntoView(element);
				element.click();
				return;
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Click failed (attempt {0}) on {1}: {2}",
						new Object[] { attempts + 1, locator, e.getMessage() });
				attempts++;
			}
		}

		LOGGER.log(Level.INFO, "Falling back to JS click for: {0}", locator);
		jsClick(locator);
	}

	// 🔥 Type with logging
	public void type(By locator, String text) {
		if (locator == null) {
			throw new IllegalArgumentException("Locator must not be null");
		}

		try {
			WebElement element = wait.waitForElementVisible(locator);
			scrollIntoView(element);
			element.clear();
			if (containsNonBmpCharacters(text)) {
				((JavascriptExecutor) driver).executeScript(
						"const el = arguments[0];"
								+ "const value = arguments[1] == null ? '' : arguments[1];"
								+ "el.focus();"
								+ "el.value = value;"
								+ "el.dispatchEvent(new Event('input', { bubbles: true }));"
								+ "el.dispatchEvent(new Event('change', { bubbles: true }));"
								+ "el.blur();",
						element, text);
			} else {
				element.sendKeys(text);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Typing failed for {0}: {1}", new Object[] { locator, e.getMessage() });
			takeScreenshot("type_failure");
			throw e;
		}
	}

	// 🔥 Get text safely
	public String getText(By locator) {
		if (locator == null)
			return "";

		try {
			return wait.waitForElementVisible(locator).getText();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "getText failed for {0}: {1}", new Object[] { locator, e.getMessage() });
			return "";
		}
	}

	// 🔥 Check display safely
	public boolean isDisplayed(By locator) {
		if (locator == null)
			return false;

		try {
			return wait.waitForElementVisible(locator).isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Element not displayed: {0}", locator);
			return false;
		}
	}

	// 🔥 Scroll into view
	public void scrollToElement(By locator) {
		try {
			WebElement element = wait.waitForElementVisible(locator);
			scrollIntoView(element);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Scroll failed for {0}: {1}", new Object[] { locator, e.getMessage() });
		}
	}

	// 🔥 JS click
	public void jsClick(By locator) {
		try {
			WebElement element = wait.waitForElementVisible(locator);
			scrollIntoView(element);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "JS click failed for {0}: {1}", new Object[] { locator, e.getMessage() });
			takeScreenshot("jsclick_failure");
			throw e;
		}
	}

	// 🔥 Frame handling
	public void switchToFrame(By locator) {
		try {
			WebElement frame = wait.waitForElementVisible(locator);
			driver.switchTo().frame(frame);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Frame switch failed: {0}", e.getMessage());
			throw e;
		}
	}

	public void switchToDefault() {
		driver.switchTo().defaultContent();
	}

	// 🔥 NEW: Safe get attribute
	public String getAttribute(By locator, String attribute) {
		try {
			return wait.waitForElementVisible(locator).getAttribute(attribute);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "getAttribute failed: {0}", e.getMessage());
			return "";
		}
	}

	// 🔥 NEW: Explicit wait click (force wait)
	public void safeClick(By locator) {
		WebElement element = wait.waitForElementClickable(locator);
		scrollIntoView(element);
		element.click();
	}

	// 🔥 Screenshot helper
	private void takeScreenshot(String name) {
		try {
			ScreenshotUtils.capture(driver, name + "_" + System.currentTimeMillis());
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Screenshot failed: {0}", e.getMessage());
		}
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

	private void scrollIntoView(WebElement element) {
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
	}
}
