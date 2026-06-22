package pages;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utils.LoggerUtils;

/**
 * Page object for the Contact Us form.
 *
 * <p>Mirrors the locator + action conventions used by
 * {@link LoginPage} / {@link DashboardPage}: locators declared as private
 * static {@code By} constants, public methods that wrap the interactions,
 * and a thin introspection surface ({@link #isFormAvailable()},
 * {@link #isUploadAvailable()}) for the tests to gate on.
 *
 * <p>The upload control on this page is currently rendered as a CSS-styled
 * placeholder div that does not always expose a scriptable
 * {@code <input type="file">} in the DOM. The page object therefore exposes
 * two distinct accessors:
 *
 * <ul>
 *   <li>{@link #getUploadPlaceholder()} — always returns the visible
 *       "Please upload file" div if it is on the page, never {@code null}
 *       when the page is loaded.</li>
 *   <li>{@link #getFileInput()} — returns the underlying
 *       {@code <input type="file">} if it is currently in the DOM, else
 *       {@code null}. May briefly return non-null between a click on the
 *       placeholder and the OS file-picker dialog stealing focus, which
 *       {@link #revealFileInput(Duration)} exploits via polling.</li>
 * </ul>
 */
public class ContactUsPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(ContactUsPage.class.getName());

	// ==================== Form fields ====================

	private static final By SUBJECT_FIELD_PRIMARY = By.xpath("(//input[@placeholder='Subject *'])[2]");
	private static final By SUBJECT_FIELD_FALLBACK = By.xpath("//input[@placeholder='Subject *']");
	private static final By SUBJECT_FIELD_GENERIC = By
			.xpath("//input[@type='text'] | //input[@name*='subject' or @placeholder*='subject']");

	private static final By MESSAGE_FIELD_PRIMARY = By.xpath("(//textarea[@placeholder='Message *'])[2]");
	private static final By MESSAGE_FIELD_FALLBACK = By.xpath("//textarea[@placeholder='Message *']");
	private static final By MESSAGE_FIELD_GENERIC = By
			.xpath("//textarea[@name*='message' or @placeholder*='message']");

	private static final By SUBMIT_BUTTON_PRIMARY = By
			.xpath("//div[@tabindex='0']//div[contains(text(),'Submit')]");
	private static final By SUBMIT_BUTTON_FALLBACK = By.xpath(
			"//button[contains(text(),'Submit')] | //input[@type='submit']"
					+ " | //*[@role='button' and contains(text(),'Submit')]"
					+ " | //div[@tabindex='0' and .//div[normalize-space()='Submit']]");

	private static final By GENERIC_SUBMIT_BUTTON = By.xpath(
			"//button[@type='submit'] | //input[@type='submit']"
					+ " | //*[contains(text(),'Submit') or contains(text(),'Send')]");

	// ==================== Upload area ====================

	/**
	 * Visible "Please upload file" placeholder. Present on every render of
	 * the form, so {@link #isUploadAvailable()} uses this as the cheap
	 * gate.
	 */
	private static final By UPLOAD_PLACEHOLDER = By.xpath(
			"//div[contains(., 'Please upload file')]"
					+ " | //div[contains(@class,'upload') and (contains(., 'upload') or contains(., 'Upload'))]"
					+ " | //*[@role='button' and (contains(., 'upload') or contains(., 'Upload'))]");

	/**
	 * Hidden / scriptable file input. May not exist in the DOM until the
	 * placeholder is clicked (and the React component mounts a real input
	 * via a portal or via display:none).
	 */
	private static final By FILE_INPUT_DIRECT = By.xpath("//input[@type='file']");

	private static final By[] FILE_INPUT_FALLBACKS = new By[] {
			By.xpath("//div[contains(., 'Please upload file')]/..//input[@type='file']"),
			By.xpath("//div[contains(., 'Please upload file')]/following-sibling::input[@type='file']"),
			By.xpath("//div[contains(@class,'upload')]//input[@type='file']")
	};

	// ==================== Lifecycle ====================

	public ContactUsPage(WebDriver driver) {
		super(driver);
	}

	// ==================== Form introspection ====================

	/**
	 * @return {@code true} if the Subject, Message, and Submit elements are
	 *         all present on the page.
	 */
	public boolean isFormAvailable() {
		waitForUploadPlaceholderToSettle();
		return findSubjectField() != null && findMessageField() != null && findSubmitButton() != null;
	}

	/**
	 * @return {@code true} if the visible "Please upload file" placeholder
	 *         is on the page. Cheap — does not probe for the underlying
	 *         {@code <input type="file">}.
	 */
	public boolean isUploadAvailable() {
		try {
			return driver.findElement(UPLOAD_PLACEHOLDER) != null;
		} catch (Exception e) {
			return false;
		}
	}

	// ==================== Subject / Message / Submit ====================

	/**
	 * Fill the Subject field. Tries multiple locator strategies in turn
	 * (primary, fallback, generic) so the call survives minor DOM tweaks.
	 *
	 * @throws RuntimeException if no Subject field is found.
	 */
	public void fillSubject(String value) {
		WebElement field = findSubjectField();
		if (field == null) {
			throw new RuntimeException("ContactUsPage: Subject field not found");
		}
		field.clear();
		field.sendKeys(value);
		LoggerUtils.logInfo("ContactUsPage: Subject filled");
	}

	/**
	 * Fill the Message field. Tries multiple locator strategies in turn.
	 *
	 * @throws RuntimeException if no Message field is found.
	 */
	public void fillMessage(String value) {
		WebElement field = findMessageField();
		if (field == null) {
			throw new RuntimeException("ContactUsPage: Message field not found");
		}
		field.clear();
		field.sendKeys(value);
		LoggerUtils.logInfo("ContactUsPage: Message filled");
	}

	/**
	 * Click the Submit button.
	 *
	 * @throws RuntimeException if no Submit button is found.
	 */
	public void clickSubmit() {
		WebElement button = findSubmitButton();
		if (button == null) {
			throw new RuntimeException("ContactUsPage: Submit button not found");
		}
		button.click();
		LoggerUtils.logInfo("ContactUsPage: Submit clicked");
	}

	/**
	 * Click any submit-shaped control on the page. Used by the
	 * mandatory-field-validation test which wants to submit an empty form
	 * to trigger validation messages.
	 */
	public void clickAnySubmitControl() {
		try {
			driver.findElement(GENERIC_SUBMIT_BUTTON).click();
		} catch (Exception e) {
			throw new RuntimeException("ContactUsPage: No submit-shaped element found", e);
		}
		LoggerUtils.logInfo("ContactUsPage: Generic submit clicked");
	}

	// ==================== Upload area ====================

	/**
	 * @return the visible upload placeholder (the "Please upload file"
	 *         div), or {@code null} if it is not on the page.
	 */
	public WebElement getUploadPlaceholder() {
		try {
			return driver.findElement(UPLOAD_PLACEHOLDER);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return the underlying {@code <input type="file">} if it is in the
	 *         DOM right now, else {@code null}. Tries the direct xpath
	 *         first (cheapest), then sibling / descendant fallbacks.
	 */
	public WebElement getFileInput() {
		try {
			return driver.findElement(FILE_INPUT_DIRECT);
		} catch (Exception ignored) {
			// Fall through to fallback strategies.
		}
		for (By fallback : FILE_INPUT_FALLBACKS) {
			try {
				return driver.findElement(fallback);
			} catch (Exception ignored) {
				// try next
			}
		}
		return null;
	}

	/**
	 * @return the visible Message textarea, or {@code null} if it is not on
	 *         the page. Tests use this to read the current {@code value}
	 *         attribute (e.g. for boundary-length checks).
	 */
	public WebElement getMessageField() {
		return findMessageField();
	}

	/**
	 * @return the visible Subject input, or {@code null} if it is not on
	 *         the page. Tests use this to read the current {@code value}
	 *         attribute.
	 */
	public WebElement getSubjectField() {
		return findSubjectField();
	}

	/**
	 * Click the upload placeholder. Used to trigger the page's upload
	 * handler, which on some implementations mounts a real
	 * {@code <input type="file">} into the DOM (briefly, before the OS
	 * file picker dialog steals focus).
	 *
	 * @return {@code true} if the click was issued without throwing.
	 */
	public boolean clickUploadPlaceholder() {
		WebElement placeholder = getUploadPlaceholder();
		if (placeholder == null) {
			return false;
		}
		try {
			placeholder.click();
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Upload placeholder click failed: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Best-effort attempt to surface a scriptable file input.
	 *
	 * <p>Polls for up to {@code timeout} after clicking the placeholder.
	 * Returns the input as soon as it appears, or {@code null} if it
	 * never does within the budget.
	 *
	 * @param timeout how long to keep polling after the click.
	 * @return the input element if it materialises, else {@code null}.
	 */
	public WebElement revealFileInput(Duration timeout) {
		// Cheap path: input already in the DOM.
		WebElement direct = getFileInput();
		if (direct != null) {
			return direct;
		}

		// Click the placeholder to trigger the page's handler.
		boolean clicked = clickUploadPlaceholder();
		if (!clicked) {
			return null;
		}

		long deadline = System.currentTimeMillis() + timeout.toMillis();
		while (System.currentTimeMillis() < deadline) {
			WebElement input = getFileInput();
			if (input != null) {
				return input;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				return null;
			}
		}
		return null;
	}

	/**
	 * Convenience overload that defaults to a 2-second poll window — long
	 * enough to catch a React portal mount, short enough that a real
	 * OS-picker-flow skip stays snappy.
	 */
	public WebElement revealFileInput() {
		return revealFileInput(Duration.ofSeconds(2));
	}

	// ==================== Locator strategy helpers ====================

	private WebElement findSubjectField() {
		return findFirstPresent(SUBJECT_FIELD_PRIMARY, SUBJECT_FIELD_FALLBACK, SUBJECT_FIELD_GENERIC);
	}

	private WebElement findMessageField() {
		return findFirstPresent(MESSAGE_FIELD_PRIMARY, MESSAGE_FIELD_FALLBACK, MESSAGE_FIELD_GENERIC);
	}

	private WebElement findSubmitButton() {
		return findFirstPresent(SUBMIT_BUTTON_PRIMARY, SUBMIT_BUTTON_FALLBACK);
	}

	private WebElement findFirstPresent(By... locators) {
		for (By locator : locators) {
			try {
				return driver.findElement(locator);
			} catch (Exception ignored) {
				// try next
			}
		}
		return null;
	}

	private void waitForUploadPlaceholderToSettle() {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(d -> {
						try {
							List<WebElement> placeholders = driver.findElements(UPLOAD_PLACEHOLDER);
							return !placeholders.isEmpty();
						} catch (Exception ignored) {
							return false;
						}
					});
		} catch (Exception ignored) {
			// Best effort — the form-availability check below will give a clearer error.
		}
	}

	// ==================== SAFE WRAPPER METHODS ====================

	/**
	 * Safe getCurrentUrl wrapper that handles null and returns an empty
	 * string if the URL cannot be read.
	 *
	 * @param driver the WebDriver instance.
	 * @return current URL or empty string if unavailable.
	 */
	public static String safeGetCurrentUrl(WebDriver driver) {
		try {
			String url = Objects.requireNonNull(driver.getCurrentUrl());
			return url;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Safe getPageSource wrapper that handles null and returns an empty
	 * string if the page source cannot be read.
	 *
	 * @param driver the WebDriver instance.
	 * @return page source or empty string if unavailable.
	 */
	public static String safeGetPageSource(WebDriver driver) {
		try {
			String source = driver.getPageSource();
			return source != null ? source : "";
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Safe getAttribute wrapper that handles null elements, null attribute
	 * names, and any exception thrown by {@link WebElement#getAttribute}.
	 *
	 * @param element       the WebElement.
	 * @param attributeName the attribute name.
	 * @return attribute value or empty string if unavailable.
	 */
	public static String safeGetAttribute(WebElement element, String attributeName) {
		if (element == null || attributeName == null) {
			return "";
		}
		try {
			String value = element.getAttribute(attributeName);
			return value != null ? value : "";
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Safe string wrapper that converts {@code null} to an empty string.
	 *
	 * @param str the string to check.
	 * @return the string or empty string if null.
	 */
	public static String safeString(String str) {
		return str != null ? str : "";
	}

	/**
	 * Safe string equality check that handles {@code null} on either side.
	 *
	 * @param str1 first string.
	 * @param str2 second string.
	 * @return {@code true} if strings are equal (both null or equal content).
	 */
	public static boolean safeStringEquals(String str1, String str2) {
		if (str1 == null && str2 == null) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		return str1.equals(str2);
	}
}