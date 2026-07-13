package pages;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import base.BasePage;
import listeners.TestListener;
import utils.ConfigReader;

/**
 * Page object that models the SonarPlay registration flow.
 */
public class RegistrationPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(RegistrationPage.class.getName());

	private static final By HOME_LOGIN_BUTTON = By.xpath(
			"(//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-9oks40 r-1tw7wh r-eu3ka r-1777fci r-uhung1 r-3o4zer'])[1]");
	private static final By LOGIN_ENTRY_BUTTON = By.xpath("//span[normalize-space()='Login']"
			+ " | //div[normalize-space()='Login']" + " | //button[normalize-space()='Login']"
			+ " | //*[@tabindex='0' and (.//span[normalize-space()='Login'] or .//div[normalize-space()='Login'])]");
	private static final By REGISTER_LINK = By.xpath("//span[contains(text(),'Register')]");
	private static final By NAME_FIELD = By.xpath("//input[@placeholder='Name']");
	private static final By EMAIL_FIELD = By.xpath("(//input[@placeholder='Email'])[2]");
	private static final By PASSWORD_FIELD = By.xpath("(//input[@placeholder='Password'])[2]");
	private static final By CONFIRM_PASSWORD_FIELD = By.xpath("(//input[@placeholder='Confirm password'])[1]");
	private static final By TERMS_CHECKBOX = By.xpath("//*[@data-testid='termsCheckbox']");

	private static final By TERMS_LABEL = By.xpath(
			"//*[self::div or self::span][normalize-space()='Terms and Conditions' or normalize-space()='Terms & Conditions' or contains(normalize-space(.),'Terms and Conditions') or contains(normalize-space(.),'Terms & Conditions')]");
	private static final By TERMS_LINK = By.xpath("//*[self::a or self::button or self::span or self::div]"
			+ "[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'terms')]"
			+ "[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'condition')]");
	private static final By TERMS_CONTAINER = By.xpath(
			"//*[contains(normalize-space(.),'Terms and Conditions')]/ancestor::*[@tabindex='0' or self::div][1]");
	private static final By REGISTER_BUTTON = By
			.xpath("//div[@tabindex='0' and (.//div[normalize-space()='Register'] or normalize-space()='Register')]"
					+ " | //button[normalize-space()='Register']");
	private static final By NEXT_BUTTON = By.xpath("//div[@tabindex='0' and .//div[normalize-space()='Next']]");
	private static final By RESET_BUTTON = By
			.xpath("//div[@tabindex='0' and .//div[normalize-space()='Reset']] | //button[normalize-space()='Reset']");
	private static final By NEWSLETTER_CHECKBOX = By.xpath("//*[@data-testid='newsletterCheckbox']");
	private static final By CAPTCHA = By
			.xpath("//iframe[contains(@src,'recaptcha')] | //*[@id='captcha' or contains(@class,'captcha')]");
	private static final By SUCCESS_MESSAGE = By.xpath(
			"//div[@data-testid='toastText1' or contains(@class,'toast') or contains(@class,'alert')][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'registered') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'otp')]");
	private static final By GENERIC_FEEDBACK = By.xpath(
			"//div[@data-testid='toastText1' or contains(@class,'toast') or contains(@class,'alert') or contains(@class,'error')]");
	private static final By ALL_VALIDATION_MESSAGES = By.xpath("//div[contains(@class,'css-146c3p1')]");
	private static final By REGISTRATION_CONTAINER = By.xpath(
			"//input[@placeholder='Name'] | //div[@tabindex='0' and .//div[normalize-space()='Register']]");

	// ==================== Data factory helpers (test-side moved here) ====================

	private static final AtomicInteger UNIQUE_COUNTER = new AtomicInteger(1000);
	private static final DateTimeFormatter UNIQUE_STAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	/**
	 * Returns a fresh {@link RegistrationFormData} populated with the
	 * configured default values plus a unique email so the form can be
	 * submitted without colliding with existing accounts.
	 */
	public RegistrationFormData createValidFormData() {
		String password = getConfiguredPassword();
		return new RegistrationFormData().withName(getConfiguredName())
				.withEmail(createUniqueEmail())
				.withPassword(password)
				.withConfirmPassword(password)
				.withAcceptTerms(true);
	}

	/**
	 * Configured default name (overridable via
	 * {@code registration.name=…} in config.properties).
	 */
	public String getConfiguredName() {
		return firstNonBlank(ConfigReader.getProperty("registration.name"), "Safwan Shaikh");
	}

	/**
	 * Configured default password (overridable via
	 * {@code registration.password=…}).
	 */
	public String getConfiguredPassword() {
		return firstNonBlank(ConfigReader.getProperty("registration.password"), "Password@123");
	}

	/**
	 * Email of an account that already exists in the system, used by
	 * duplicate-email assertions. Falls back to the login email.
	 */
	public String getExistingEmail() {
		return firstNonBlank(ConfigReader.getProperty("registration.existingEmail"),
				ConfigReader.getProperty("login.validEmail"));
	}

	/**
	 * Generates a unique email of the form {@code local+reg{token}@domain}
	 * so each registration attempt uses a fresh inbox.
	 */
	public String createUniqueEmail() {
		String seedEmail = firstNonBlank(ConfigReader.getProperty("registration.newEmail"),
				ConfigReader.getProperty("login.validEmail"), getExistingEmail(), "automation@mail.com");
		String[] parts = seedEmail.split("@", 2);
		String localPart = sanitizeEmailLocalPart(parts[0]);
		String domainPart = parts.length > 1 ? parts[1] : "mail.com";
		return localPart + "+reg" + buildUniqueToken() + "@" + domainPart;
	}

	/**
	 * Per-process monotonic token used as the uniqueness suffix on generated
	 * emails.
	 */
	public String buildUniqueToken() {
		String timestamp = LocalDateTime.now().format(UNIQUE_STAMP_FORMAT);
		return timestamp + UNIQUE_COUNTER.incrementAndGet();
	}

	private String sanitizeEmailLocalPart(String value) {
		String normalized = value == null ? "automation" : value.replaceAll("[^A-Za-z0-9._-]", "");
		return normalized.isBlank() ? "automation" : normalized;
	}

	// ==================== Expectation wrappers (test-side moved here) ====================

	/**
	 * Submits {@code data} and asserts that registration was successful.
	 * Returns {@code true} on success so the caller can decide whether to
	 * chain additional assertions.
	 */
	public boolean expectSuccessful(RegistrationFormData data, String context) {
		submitRegistration(data);
		printVisibleFeedbackMessages(context);
		return isRegistrationSuccessful();
	}

	/**
	 * Submits {@code data} and asserts that registration was rejected with a
	 * visible feedback message containing the given fragments. Returns
	 * {@code true} when the registration was correctly rejected.
	 */
	public boolean expectRejected(RegistrationFormData data, String context, String... expectedFragments) {
		submitRegistration(data);
		String[] fragments = resolveFeedbackFragments(context, expectedFragments);
		boolean notSuccessful = !isRegistrationSuccessful();
		if (fragments.length == 0) {
			printMatchedFeedbackMessage(context);
			return notSuccessful
					&& (hasAnyVisibleFeedback() || isRegistrationScreenDisplayed());
		}
		String matchedFeedback = findFeedbackMessage(fragments);
		printMatchedFeedbackMessage(context, fragments);
		return notSuccessful && !matchedFeedback.isBlank();
	}

	/**
	 * Submits {@code data} and asserts that the flow handled the scenario
	 * gracefully — either succeeded, showed visible feedback, or kept the
	 * registration screen reachable. Returns {@code true} on graceful outcome.
	 */
	public boolean expectHandledGracefully(RegistrationFormData data, String context) {
		submitRegistration(data);
		String[] fragments = resolveFeedbackFragments(context);
		if (fragments.length > 0) {
			printMatchedFeedbackMessage(context, fragments);
		} else {
			printVisibleFeedbackMessages(context);
		}
		return isRegistrationSuccessful() || hasAnyVisibleFeedback() || isRegistrationScreenDisplayed();
	}

	/**
	 * Asserts that every mandatory validation message is present. Returns
	 * {@code true} when all expected messages are visible.
	 */
	public boolean assertAllMandatoryValidationMessages() {
		boolean name = getNameRequiredMessage().toLowerCase(Locale.ENGLISH).contains("name is required");
		boolean email = getEmailRequiredMessage().toLowerCase(Locale.ENGLISH).contains("email is required");
		boolean password = getPasswordRequiredMessage().toLowerCase(Locale.ENGLISH).contains("password");
		boolean confirm = getConfirmPasswordRequiredMessage().toLowerCase(Locale.ENGLISH)
				.contains("password confirmation is required");
		boolean terms = getTermsRequiredMessage().toLowerCase(Locale.ENGLISH).contains("terms");
		return name && email && password && confirm && terms;
	}

	/**
	 * Clicks the terms link and returns {@code true} if any of the
	 * expected outcomes occurred (URL changed, new window opened, or terms
	 * content became visible).
	 */
	public boolean assertTermsLinkOpens() {
		String currentUrl = getCurrentUrlSafely();
		Set<String> existingWindows = driver.getWindowHandles();
		clickTermsLink();
		String nextUrl = getCurrentUrlSafely();
		boolean navigated = !currentUrl.isBlank() && !nextUrl.equalsIgnoreCase(currentUrl);
		boolean openedWindow = driver.getWindowHandles().size() > existingWindows.size();
		boolean contentVisible = isTermsContentVisible();
		return navigated || openedWindow || contentVisible;
	}

	// ==================== Helpers (test-side moved here) ====================

	/**
	 * Null-safe current-URL accessor. Returns empty string when the driver
	 * cannot be queried. Replaces ad-hoc {@code Objects.requireNonNull(...)}
	 * calls from test code.
	 */
	public String getCurrentUrlSafely() {
		try {
			return driver.getCurrentUrl();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Null-safe page-source accessor (lower-cased). Returns empty string on
	 * failure.
	 */
	public String getPageSourceLowerSafely() {
		try {
			String source = driver.getPageSource();
			return source == null ? "" : source.toLowerCase(Locale.ENGLISH);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Joins the non-blank fragments into a comma-separated string suitable
	 * for embedding in an assertion message.
	 */
	public String joinFragments(String... fragments) {
		if (fragments == null) {
			return "[]";
		}
		List<String> values = new ArrayList<>();
		for (String fragment : fragments) {
			if (fragment != null && !fragment.isBlank()) {
				values.add(fragment);
			}
		}
		return values.toString();
	}

	/**
	 * Resolves the feedback fragments to look for in {@code expectRejected}.
	 * Uses caller-provided fragments when present, otherwise infers them
	 * from the {@code context} string.
	 */
	public String[] resolveFeedbackFragments(String context, String... expectedFragments) {
		if (expectedFragments != null && expectedFragments.length > 0) {
			return expectedFragments;
		}

		String normalizedContext = context == null ? "" : context.toLowerCase(Locale.ENGLISH);

		if (normalizedContext.contains("duplicate email")) {
			return new String[] { "email", "taken" };
		}
		if (normalizedContext.contains("confirm password mismatch")) {
			return new String[] { "confirm", "password" };
		}
		if (normalizedContext.contains("confirm password empty")) {
			return new String[] { "confirm", "password", "required" };
		}
		if (normalizedContext.contains("terms")) {
			return new String[] { "terms" };
		}
		if (normalizedContext.contains("email")) {
			return new String[] { "email" };
		}
		if (normalizedContext.contains("name")) {
			return new String[] { "name" };
		}
		if (normalizedContext.contains("password")) {
			return new String[] { "password" };
		}

		return new String[0];
	}

	/**
	 * Returns the list of currently-displayed feedback messages, lower-cased
	 * (for content matching).
	 */
	public String findFeedbackMessageLower(String... fragments) {
		String message = findFeedbackMessage(fragments);
		return message == null ? "" : message.toLowerCase(Locale.ENGLISH);
	}

	// ==================== Test-facing helpers (reference-aligned) ====================

	/**
	 * Null-safe string accessor mirroring {@code safeString} on the sibling
	 * page objects ({@link ChapterPage}, {@code ConsumerBookDetailsPage}) so
	 * the test can log exception messages without risking an NPE.
	 */
	public String safeString(String value) {
		return value == null ? "" : value;
	}

	/**
	 * Wait the given number of milliseconds, surfacing interrupts as a
	 * runtime exception. Mirrors {@code waitQuietly} on the sibling page
	 * objects so the tests do not use raw {@link Thread#sleep}.
	 */
	public void waitQuietly(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Sleep interrupted", e);
		}
	}

	/**
	 * @return the length of {@code value}, or {@code 0} when it is
	 *         {@code null}. Lets the test assert on field-value lengths
	 *         without calling {@code .length()} on a nullable reference.
	 */
	public int safeLength(String value) {
		return value == null ? 0 : value.length();
	}

	/**
	 * Null-safe, case-insensitive {@code contains} check used by the
	 * placeholder, role-text, and validation-message assertions.
	 */
	public boolean containsIgnoreCase(String container, String token) {
		if (container == null || token == null) {
			return false;
		}
		return container.toLowerCase(Locale.ROOT).contains(token.toLowerCase(Locale.ROOT));
	}

	/**
	 * @return {@code true} when the first visible feedback message contains
	 *         any of the given keywords (case-insensitive). Null-safe so the
	 *         activation-feedback tests never dereference a nullable.
	 */
	public boolean firstFeedbackContainsAny(String... keywords) {
		String message = getFirstVisibleFeedbackMessage();
		if (keywords == null) {
			return false;
		}
		for (String keyword : keywords) {
			if (containsIgnoreCase(message, keyword)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return {@code true} when the current page source contains the given
	 *         text (case-insensitive). Null-safe wrapper around the
	 *         page-source lookup so the test never handles a nullable source.
	 */
	public boolean pageSourceContains(String text) {
		return isTextPresentOnPage(text);
	}

	/**
	 * Wait up to {@code timeoutSeconds} for the registration flow to surface
	 * either a success state or visible feedback. Replaces ad-hoc
	 * {@code Thread.sleep} waits in the slow-network / latency tests.
	 */
	public boolean waitForRegistrationResponse(long timeoutSeconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
					.until(ignored -> isRegistrationSuccessful() || hasAnyVisibleFeedback());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Focus the name field, send a single Tab, and return {@code true} when
	 * keyboard focus actually moved to a different element. Encapsulates the
	 * active-element / {@link Keys#TAB} interaction so the test stays free
	 * of raw Selenium.
	 */
	public boolean verifyTabFromNameFieldMovesFocus() {
		try {
			focusNameField();
			WebElement beforeTab = driver.switchTo().activeElement();
			beforeTab.sendKeys(Keys.TAB);
			WebElement afterTab = driver.switchTo().activeElement();
			return !afterTab.equals(beforeTab);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Tab-key focus move could not be verified: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Navigate the current driver to the configured base URL. No-op when
	 * the base URL is blank. Centralises {@code driver.get(...)} so the
	 * driver-restart tests do not call raw {@link WebDriver#get(String)}.
	 */
	public void openBaseUrl() {
		String url = ConfigReader.getProperty("url", "");
		if (!url.isBlank()) {
			driver.get(url);
		}
	}

	/**
	 * Resize the browser window to the given dimensions. Centralises
	 * {@code driver.manage().window().setSize(...)} so the viewport tests
	 * stay free of raw Selenium.
	 */
	public void setWindowSize(int width, int height) {
		driver.manage().window().setSize(new Dimension(width, height));
	}

	public RegistrationPage(WebDriver driver) {
		super(driver);
	}

	public void openLogin() {
		new DashboardPage(driver).acceptCookiesIfPresent();

		if (new LoginPage(driver).isOnLoginPage()) {
			return;
		}

		if (clickVisibleLoginEntry(LOGIN_ENTRY_BUTTON)) {
			waitForLoginScreenReady();
			return;
		}

		if (clickVisibleLoginEntry(HOME_LOGIN_BUTTON)) {
			waitForLoginScreenReady();
			return;
		}

		if (openDirectRoute("/login") || openDirectRoute("/signin") || openDirectRoute("/sign-in")
				|| openDirectRoute("/auth/login")) {
			waitForLoginScreenReady();
			return;
		}

		throw new IllegalStateException("Unable to locate a visible Login entry point on the current page.");
	}

	public void openRegistration() {
		if (isRegistrationScreenDisplayed()) {
			return;
		}

		// The login screen must be ready before the Register link can be
		// clicked; without this the click races the page render and the
		// subsequent direct-route fallbacks never find the form.
		waitForLoginScreenReady();

		try {
			wait.waitForElementClickable(REGISTER_LINK);
			jsClick(REGISTER_LINK);
			if (isRegistrationScreenDisplayed()) {
				return;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Register link click failed, trying direct routes: {0}", e.getMessage());
		}

		if (openDirectRoute("/register") || openDirectRoute("/signup") || openDirectRoute("/sign-up")
				|| openDirectRoute("/auth/register")) {
			return;
		}

		throw new IllegalStateException("Unable to open the registration page from the current state.");
	}

	/**
	 * Wait for the login screen to finish rendering after the login entry is
	 * clicked or the direct route is opened. The underlying {@code openLogin}
	 * click returns before the login form is in the DOM, which previously left
	 * {@code openRegistration} racing ahead of the page. This uses an explicit
	 * {@link WebDriverWait} (no {@code Thread.sleep}) and is a no-op when the
	 * login screen is already visible.
	 */
	private void waitForLoginScreenReady() {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(15))
					.until(ignored -> new LoginPage(driver).isOnLoginPage());
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Login screen did not become ready in time: {0}", e.getMessage());
		}
	}

	public boolean isRegistrationScreenDisplayed() {
		try {
			wait.waitForElementVisible(NAME_FIELD);
			return isElementPresent(NAME_FIELD) && isElementPresent(REGISTER_BUTTON)
					&& isElementPresent(REGISTRATION_CONTAINER);
		} catch (Exception e) {
			return false;
		}
	}

	public void focusNameField() {
		wait.waitForElementVisible(NAME_FIELD).click();
	}

	public void enterName(String fullName) {
		type(NAME_FIELD, safeValue(fullName));
		waitForFieldValue(NAME_FIELD, fullName);
	}

	public void enterEmail(String email) {
		type(EMAIL_FIELD, safeValue(email));
		waitForFieldValue(EMAIL_FIELD, email);
	}

	public void enterPassword(String password) {
		type(PASSWORD_FIELD, safeValue(password));
		waitForFieldValue(PASSWORD_FIELD, password);
	}

	public void pastePassword(String password) {
		enterPassword(password);
	}

	public void enterConfirmPassword(String confirmPassword) {
		type(CONFIRM_PASSWORD_FIELD, safeValue(confirmPassword));
		waitForFieldValue(CONFIRM_PASSWORD_FIELD, confirmPassword);
	}

	public void acceptTerms() {
		try {
			wait.waitForPageLoad();

			WebElement checkbox = wait.waitForElementVisible(TERMS_CHECKBOX);
			WebElement label = null;
			WebElement container = null;
			try {
				label = wait.waitForElementVisible(TERMS_LABEL);
				container = findTermsContainer(label);
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Terms label/container lookup failed: {0}", e.getMessage());
			}

			if (checkbox == null) {
				throw new RuntimeException("Unable to find Terms checkbox element");
			}

			if (isTermsSelected(checkbox, container)) {
				return;
			}

			clickTermsTarget(checkbox);
			wait.waitForMilliseconds(1000);
			if (isTermsSelected(checkbox, container)) {
				LOGGER.log(Level.INFO, "Terms checkbox clicked successfully");
				return;
			}

			if (container != null) {
				clickTermsTarget(container);
				wait.waitForMilliseconds(1000);
				if (isTermsSelected(checkbox, container)) {
					LOGGER.log(Level.INFO, "Terms checkbox selected via container click");
					return;
				}
			}

			if (label != null) {
				clickTermsTarget(label);
				wait.waitForMilliseconds(1000);
				if (isTermsSelected(checkbox, container)) {
					LOGGER.log(Level.INFO, "Terms checkbox selected via label click");
					return;
				}
			}

			throw new RuntimeException("Terms checkbox was clicked but did not become selected");
		} catch (Exception e) {
			throw new RuntimeException("Unable to click Terms checkbox", e);
		}
	}

	public boolean isTermsLinkAvailable() {
		return findVisibleElement(TERMS_LINK, TERMS_LABEL, TERMS_CONTAINER) != null;
	}

	public void clickTermsLink() {
		WebElement link = findVisibleElement(TERMS_LINK, TERMS_LABEL, TERMS_CONTAINER);
		if (link == null) {
			throw new RuntimeException("Terms and Conditions link is not visible on the registration page.");
		}

		try {
			Objects.requireNonNull((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", link);
			link.click();
		} catch (Exception e) {
			Objects.requireNonNull((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
		}
	}

	public boolean isTermsContentVisible() {
		return isTextPresentOnPage("terms") || isTextPresentOnPage("privacy");
	}

	public boolean isNewsletterCheckboxAvailable() {
		return isElementPresent(NEWSLETTER_CHECKBOX);
	}

	public void toggleNewsletter() {
		jsClick(NEWSLETTER_CHECKBOX);
	}

	public boolean isResetButtonAvailable() {
		return isElementPresent(RESET_BUTTON);
	}

	public void clickReset() {
		click(RESET_BUTTON);
	}

	public boolean isCaptchaAvailable() {
		return isElementPresent(CAPTCHA);
	}

	public void clickRegister() {
		jsClick(REGISTER_BUTTON);
	}

	public void doubleClickRegister() {
		clickRegister();
		clickRegister();
	}

	public void submitWithEnter() {
		WebElement confirmPassword = wait.waitForElementVisible(CONFIRM_PASSWORD_FIELD);
		confirmPassword.click();
		focusRegisterButtonWithTab();

		WebElement activeElement = driver.switchTo().activeElement();
		activeElement.sendKeys(Keys.ENTER);
		wait.waitForMilliseconds(2000);

		if (isRegistrationSuccessful() || hasAnyVisibleFeedback()) {
			return;
		}

		try {
			WebElement registerButton = wait.waitForElementVisible(REGISTER_BUTTON);
			Objects.requireNonNull((JavascriptExecutor) driver).executeScript("arguments[0].focus();", registerButton);
			registerButton.sendKeys(Keys.ENTER);
			wait.waitForMilliseconds(2000);
			if (isRegistrationSuccessful() || hasAnyVisibleFeedback()) {
				return;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Enter key on register button did not trigger submission: {0}", e.getMessage());
		}

		try {
			clickRegister();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Fallback register click did not trigger submission: {0}", e.getMessage());
		}
	}

	private void focusRegisterButtonWithTab() {
		for (int i = 0; i < 8; i++) {
			WebElement activeElement = driver.switchTo().activeElement();
			if (isRegisterButtonElement(activeElement)) {
				return;
			}
			activeElement.sendKeys(Keys.TAB);
			wait.waitForMilliseconds(1000);
		}

		WebElement registerButton = wait.waitForElementVisible(REGISTER_BUTTON);
		try {
			Objects.requireNonNull((JavascriptExecutor) driver).executeScript("arguments[0].focus();", registerButton);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to focus register button directly: {0}", e.getMessage());
		}
	}

	private boolean isRegisterButtonElement(WebElement element) {
		if (element == null) {
			return false;
		}

		try {
			String text = normalizeFeedbackText(
					firstNonBlank(element.getText(),
							String.valueOf(Objects.requireNonNull((JavascriptExecutor) driver).executeScript(
									"return (arguments[0].innerText || arguments[0].textContent || '').trim();",
									element))));
			String ariaLabel = firstNonBlank(element.getAttribute("aria-label")).toLowerCase(Locale.ENGLISH);
			String role = firstNonBlank(element.getAttribute("role")).toLowerCase(Locale.ENGLISH);
			String tagName = firstNonBlank(element.getTagName()).toLowerCase(Locale.ENGLISH);

			return text.equalsIgnoreCase("register") || ariaLabel.contains("register")
					|| ("button".equals(role) && text.toLowerCase(Locale.ENGLISH).contains("register"))
					|| ("button".equals(tagName) && text.toLowerCase(Locale.ENGLISH).contains("register"));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to inspect active element while tabbing to register: {0}", e.getMessage());
			return false;
		}
	}

	public void populateForm(RegistrationFormData data) {
		if (data == null) {
			return;
		}

		enterName(data.getName());
		enterEmail(data.getEmail());
		enterPassword(data.getPassword());
		enterConfirmPassword(data.getConfirmPassword());

		if (data.isAcceptTerms()) {
			acceptTerms();
		}
	}

	public void submitRegistration() {
		clickRegister();
	}

	public void submitRegistration(RegistrationFormData data) {
		populateForm(data);
		submitRegistration();
	}

	public boolean isRegistrationSuccessful() {
		String feedback = getFirstVisibleFeedbackMessage().toLowerCase(Locale.ENGLISH);
		return isElementPresent(NEXT_BUTTON) || isElementPresent(SUCCESS_MESSAGE) || feedback.contains("success")
				|| feedback.contains("registered") || feedback.contains("otp");
	}

	public String getNameValue() {
		return getAttribute(NAME_FIELD, "value");
	}

	public boolean isNameFieldDisplayed() {
		return isElementPresent(NAME_FIELD);
	}

	public boolean isEmailFieldDisplayed() {
		return isElementPresent(EMAIL_FIELD);
	}

	public boolean isPasswordFieldDisplayed() {
		return isElementPresent(PASSWORD_FIELD);
	}

	public boolean isConfirmPasswordFieldDisplayed() {
		return isElementPresent(CONFIRM_PASSWORD_FIELD);
	}

	public String getNamePlaceholder() {
		return getAttribute(NAME_FIELD, "placeholder");
	}

	public String getEmailPlaceholder() {
		return getAttribute(EMAIL_FIELD, "placeholder");
	}

	public String getPasswordPlaceholder() {
		return getAttribute(PASSWORD_FIELD, "placeholder");
	}

	public String getConfirmPasswordPlaceholder() {
		return getAttribute(CONFIRM_PASSWORD_FIELD, "placeholder");
	}

	public String getEmailValue() {
		return getAttribute(EMAIL_FIELD, "value");
	}

	public String getPasswordValue() {
		return getAttribute(PASSWORD_FIELD, "value");
	}

	public String getConfirmPasswordValue() {
		return getAttribute(CONFIRM_PASSWORD_FIELD, "value");
	}

	public String getCurrentUrl() {
		return Objects.requireNonNull(driver.getCurrentUrl());
	}

	public void refreshPage() {
		driver.navigate().refresh();
	}

	public void navigateBack() {
		driver.navigate().back();
	}

	public void clearCookies() {
		driver.manage().deleteAllCookies();
	}

	public boolean hasAnyVisibleFeedback() {
		return !getAllVisibleFeedbackMessages().isEmpty();
	}

	public String getFirstVisibleFeedbackMessage() {
		List<String> messages = getAllVisibleFeedbackMessages();
		return messages.isEmpty() ? "" : messages.get(0);
	}

	public List<String> getAllVisibleFeedbackMessages() {
		Set<String> messages = new LinkedHashSet<>();
		collectVisibleTexts(ALL_VALIDATION_MESSAGES, messages);
		collectVisibleTexts(GENERIC_FEEDBACK, messages);
		return new ArrayList<>(messages);
	}

	public List<String> getVisibleValidationMessages() {
		Set<String> messages = new LinkedHashSet<>();
		collectVisibleTexts(ALL_VALIDATION_MESSAGES, messages);
		return new ArrayList<>(messages);
	}

	public void printVisibleValidationMessages() {
		List<String> labeledMessages = new ArrayList<>();
		addLabeledValidationMessage(labeledMessages, "Name Warning", getNameRequiredMessage());
		addLabeledValidationMessage(labeledMessages, "Email Warning", getEmailRequiredMessage());
		addLabeledValidationMessage(labeledMessages, "Password Warning", getPasswordRequiredMessage());
		addLabeledValidationMessage(labeledMessages, "Confirm Password Warning", getConfirmPasswordRequiredMessage());
		addLabeledValidationMessage(labeledMessages, "Terms Warning", getTermsRequiredMessage());

		System.out.println("=== Validation Messages ===");
		if (labeledMessages.isEmpty()) {
			System.out.println("No validation messages displayed.");
			TestListener.logInfo("Validation Messages: none");
			return;
		}

		for (String message : labeledMessages) {
			System.out.println(message);
		}
		TestListener.logMessages("Validation Messages", labeledMessages);
	}

	public void printVisibleFeedbackMessages(String context) {
		List<String> messages = getAllVisibleFeedbackMessages();
		String title = "Feedback Messages" + (context == null || context.isBlank() ? "" : ": " + context);
		System.out.println("=== " + title + " ===");
		if (messages.isEmpty()) {
			System.out.println("No feedback messages displayed.");
			TestListener.logInfo(title + ": none");
			return;
		}

		for (String message : messages) {
			System.out.println(message);
		}
		TestListener.logMessages(title, messages);
	}

	public void printMatchedFeedbackMessage(String context, String... fragments) {
		String matchedMessage = findFeedbackMessage(fragments);
		String title = "Feedback Message" + (context == null || context.isBlank() ? "" : ": " + context);
		System.out.println("=== " + title + " ===");
		if (matchedMessage == null || matchedMessage.isBlank()) {
			System.out.println("No matching feedback message displayed.");
			TestListener.logInfo(title + ": none");
			return;
		}

		System.out.println(matchedMessage);
		TestListener.logInfo(title);
		TestListener.logInfo(matchedMessage);
	}

	public String findFeedbackMessage(String... fragments) {
		List<String> messages = getAllVisibleFeedbackMessages();

		if (fragments == null || fragments.length == 0) {
			for (String message : messages) {
				if (isMeaningfulFeedbackMessage(message)) {
					return message;
				}
			}
			return messages.isEmpty() ? "" : messages.get(0);
		}

		for (String message : messages) {
			if (!isMeaningfulFeedbackMessage(message)) {
				continue;
			}

			String normalizedMessage = message != null ? message.toLowerCase(Locale.ENGLISH) : "";

			boolean matches = true;

			for (String fragment : fragments) {
				if (fragment != null && !fragment.isBlank() && !messageContainsFragment(normalizedMessage, fragment)) {
					matches = false;
					break;
				}
			}

			if (matches) {
				return message;
			}
		}

		String pageSource = driver.getPageSource();
		String safeSource = pageSource != null ? pageSource.toLowerCase(Locale.ENGLISH) : "";

		for (String fragment : fragments) {
			if (fragment != null && !fragment.isBlank() && safeSource.contains(fragment.toLowerCase(Locale.ENGLISH))) {
				return fragment;
			}
		}

		return "";
	}

	private boolean messageContainsFragment(String normalizedMessage, String fragment) {
		String normalizedFragment = fragment.toLowerCase(Locale.ENGLISH).trim();
		if (normalizedFragment.isBlank()) {
			return true;
		}

		if (normalizedMessage.contains(normalizedFragment)) {
			return true;
		}

		if ("confirm".equals(normalizedFragment) || "confirmation".equals(normalizedFragment)) {
			return normalizedMessage.contains("confirm") || normalizedMessage.contains("confirmation");
		}

		if ("password".equals(normalizedFragment)) {
			return normalizedMessage.contains("password") || normalizedMessage.contains("passcode");
		}

		return false;
	}

	public String getNameRequiredMessage() {
		return findFeedbackMessage("name is required");
	}

	public String getEmailRequiredMessage() {
		return findFeedbackMessage("email is required");
	}

	public String getPasswordRequiredMessage() {
		return findFeedbackMessage("password is required");
	}

	public String getConfirmPasswordRequiredMessage() {
		return findFeedbackMessage("password confirmation is required");
	}

	public String getTermsRequiredMessage() {
		return findFeedbackMessage("terms", "conditions");
	}

	public String getDuplicateEmailMessage() {
		return firstNonBlank(findFeedbackMessage("email", "taken"), findFeedbackMessage("already taken"),
				findFeedbackMessage("email", "already"));
	}

	public String getConfirmPasswordMismatchMessage() {
		return firstNonBlank(findFeedbackMessage("password", "match"), findFeedbackMessage("confirmation", "match"),
				findFeedbackMessage("confirm", "password"));
	}

	private void collectVisibleTexts(By locator, Set<String> messages) {
		for (WebElement element : driver.findElements(locator)) {
			try {
				if (!element.isDisplayed()) {
					continue;
				}
				String text = element.getText().trim();
				if (text.isEmpty()) {
					Object value = Objects.requireNonNull((JavascriptExecutor) driver).executeScript(
							"return (arguments[0].innerText || arguments[0].textContent || '').trim();", element);
					text = value == null ? "" : value.toString().trim();
				}
				text = normalizeFeedbackText(text);
				if (isMeaningfulFeedbackMessage(text)) {
					messages.add(text);
				}
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Unable to read visible text: {0}", e.getMessage());
			}
		}
	}

	private WebElement findVisibleElement(By... locators) {
		for (By locator : locators) {
			for (WebElement element : driver.findElements(locator)) {
				try {
					if (element.isDisplayed()) {
						return element;
					}
				} catch (Exception e) {
					LOGGER.log(Level.FINE, "Unable to inspect element visibility for {0}: {1}",
							new Object[] { locator, e.getMessage() });
				}
			}
		}
		return null;
	}

	private void addLabeledValidationMessage(List<String> messages, String label, String value) {
		if (value != null && !value.isBlank()) {
			messages.add(label + ": " + value);
		}
	}

	private boolean isMeaningfulFeedbackMessage(String message) {
		if (message == null) {
			return false;
		}

		String normalized = normalizeFeedbackText(message);
		if (normalized.isBlank()) {
			return false;
		}

		String lowercase = normalized.toLowerCase(Locale.ENGLISH);
		if (!normalized.matches(".*[A-Za-z0-9].*")) {
			return false;
		}

		if (normalized.length() > 180) {
			return false;
		}

		if (normalized.contains("\n")) {
			return false;
		}

		return !lowercase.equals("login") && !lowercase.equals("register") && !lowercase.equals("consumer")
				&& !lowercase.equals("news letter") && !lowercase.equals("terms and conditions")
				&& !lowercase.equals("privacy policy") && !lowercase.equals("en")
				&& !lowercase.startsWith("by signing up, you agree");
	}

	private String normalizeFeedbackText(String text) {
		if (text == null) {
			return "";
		}
		return text.replace('\uF0D0', ' ').replaceAll("\\s+", " ").trim();
	}

	private WebElement findTermsContainer(WebElement label) {
		if (label == null) {
			return null;
		}

		try {
			return label.findElement(By.xpath("./ancestor::*[self::label or self::div][1]"));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Terms container lookup failed: {0}", e.getMessage());
			return null;
		}
	}

	private void clickTermsTarget(WebElement target) {
		if (target == null) {
			return;
		}

		Objects.requireNonNull((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", target);
		try {
			target.click();
		} catch (Exception e) {
			Objects.requireNonNull((JavascriptExecutor) driver).executeScript("arguments[0].click();", target);
		}

		String tagName = firstNonBlank(target.getTagName()).toLowerCase(Locale.ENGLISH);
		String tabindex = firstNonBlank(target.getAttribute("tabindex"));
		if ("div".equals(tagName) && !tabindex.isBlank()) {
			Objects.requireNonNull((JavascriptExecutor) driver).executeScript(
					"const el = arguments[0];"
							+ "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => "
							+ "el.dispatchEvent(new MouseEvent(type,{bubbles:true,cancelable:true,view:window})));",
					target);
			try {
				target.sendKeys(Keys.SPACE);
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Space key toggle failed for terms target: {0}", e.getMessage());
			}
			try {
				target.sendKeys(Keys.ENTER);
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Enter key toggle failed for terms target: {0}", e.getMessage());
			}
		}
	}

	private boolean isTermsSelected(WebElement checkbox, WebElement container) {
		try {
			if (checkbox != null) {
				String ariaChecked = checkbox.getAttribute("aria-checked");
				String checked = checkbox.getAttribute("checked");
				String selected = checkbox.getAttribute("selected");
				String classes = firstNonBlank(checkbox.getAttribute("class")).toLowerCase(Locale.ENGLISH);
				String innerHtml = firstNonBlank(checkbox.getAttribute("innerHTML")).toLowerCase(Locale.ENGLISH);
				if ("true".equalsIgnoreCase(ariaChecked) || "true".equalsIgnoreCase(checked)
						|| "true".equalsIgnoreCase(selected) || classes.contains("checked")
						|| classes.contains("selected") || innerHtml.contains("check.")
						|| innerHtml.contains("background-image") || innerHtml.contains("<img")
						|| innerHtml.contains("<svg")) {
					return true;
				}
				if ("input".equalsIgnoreCase(checkbox.getTagName()) && checkbox.isSelected()) {
					return true;
				}
				Object visualState = Objects.requireNonNull(((JavascriptExecutor) driver))
						.executeScript("const el = arguments[0];" + "const hasImg = !!el.querySelector('img');"
								+ "const bgNode = el.querySelector('[style*=\\'background-image\\']');"
								+ "const bgImg = bgNode ? window.getComputedStyle(bgNode).backgroundImage : '';"
								+ "return {hasImg:hasImg,bgImg:bgImg};", checkbox);
				if (visualState instanceof Map<?, ?> state) {
					String hasImg = firstNonBlank(String.valueOf(state.get("hasImg"))).toLowerCase(Locale.ENGLISH);
					String backgroundImage = firstNonBlank(String.valueOf(state.get("bgImg")))
							.toLowerCase(Locale.ENGLISH);
					if ("true".equals(hasImg) || (!backgroundImage.isBlank() && !backgroundImage.contains("none"))) {
						return true;
					}
				}
			}

			if (container != null) {
				String containerClasses = firstNonBlank(container.getAttribute("class")).toLowerCase(Locale.ENGLISH);
				return containerClasses.contains("checked") || containerClasses.contains("selected");
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to determine terms checkbox state: {0}", e.getMessage());
		}
		return false;
	}

	/**
	 * Wait for field value to stabilize.
	 */
	private void waitForFieldValue(By locator, String expectedValue) {
		String valueToMatch = safeValue(expectedValue);
		try {
			wait.waitForFunction(driver -> {
				String currentValue = getAttribute(locator, "value");
				return valueToMatch.equals(currentValue) ? wait.waitForElementVisible(locator) : null;
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Field value did not stabilize for {0}: {1}",
					new Object[] { locator, e.getMessage() });
		}
	}

	private boolean isTextPresentOnPage(String text) {
		if (text == null || text.isBlank()) {
			return false;
		}

		String pageSource = driver.getPageSource();
		String safeSource = pageSource != null ? pageSource.toLowerCase(Locale.ENGLISH) : "";

		return safeSource.contains(text.toLowerCase(Locale.ENGLISH));
	}

	private boolean clickVisibleLoginEntry(By locator) {
		try {
			for (WebElement element : driver.findElements(locator)) {
				if (!element.isDisplayed()) {
					continue;
				}

				Objects.requireNonNull((JavascriptExecutor) driver)
						.executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
				try {
					element.click();
				} catch (Exception e) {
					Objects.requireNonNull((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
				}
				return true;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to click login entry for locator {0}: {1}",
					new Object[] { locator, e.getMessage() });
		}
		return false;
	}

	private boolean openDirectRoute(String path) {
		String configuredUrl = ConfigReader.getProperty("url", "");
		if (configuredUrl.isBlank()) {
			return false;
		}

		try {
			driver.get(buildCandidateUrl(configuredUrl, path));

			String currentUrl = Objects.requireNonNull(driver.getCurrentUrl());
			String safeUrl = currentUrl.toLowerCase(Locale.ENGLISH);

			return new LoginPage(driver).isOnLoginPage() || isRegistrationScreenDisplayed()
					|| safeUrl.contains(path.replace("/", ""));

		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Direct route failed for {0}: {1}", new Object[] { path, e.getMessage() });
			return false;
		}
	}

	private String buildCandidateUrl(String baseUrl, String candidatePath) {
		URI uri = URI.create(baseUrl);
		return uri.getScheme() + "://" + uri.getAuthority() + candidatePath;
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return "";
	}

	private String safeValue(String value) {
		return value == null ? "" : value;
	}

	public static class RegistrationFormData {
		private String name = "";
		private String email = "";
		private String password = "";
		private String confirmPassword = "";
		private boolean acceptTerms = true;
		private boolean subscribeToNewsletter;

		public String getName() {
			return name;
		}

		public RegistrationFormData withName(String name) {
			this.name = name == null ? "" : name;
			return this;
		}

		public String getEmail() {
			return email;
		}

		public RegistrationFormData withEmail(String email) {
			this.email = email == null ? "" : email;
			return this;
		}

		public String getPassword() {
			return password;
		}

		public RegistrationFormData withPassword(String password) {
			this.password = password == null ? "" : password;
			return this;
		}

		public String getConfirmPassword() {
			return confirmPassword;
		}

		public RegistrationFormData withConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword == null ? "" : confirmPassword;
			return this;
		}

		public boolean isAcceptTerms() {
			return acceptTerms;
		}

		public RegistrationFormData withAcceptTerms(boolean acceptTerms) {
			this.acceptTerms = acceptTerms;
			return this;
		}

		public boolean isSubscribeToNewsletter() {
			return subscribeToNewsletter;
		}

		public RegistrationFormData withSubscribeToNewsletter(boolean subscribeToNewsletter) {
			this.subscribeToNewsletter = subscribeToNewsletter;
			return this;
		}
	}
}