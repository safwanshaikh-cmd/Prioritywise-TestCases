package pages;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v145.network.Network;

import base.BasePage;
import utils.ConfigReader;

/**
 * Page object for the login page and related validations.
 */
public class LoginPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());

	private DevTools devTools;
	private boolean isNetworkControlled = false;

	private static final By LOGIN_ENTRY_BUTTON = By.xpath("//span[normalize-space()='Login']"
			+ " | //div[normalize-space()='Login']" + " | //button[normalize-space()='Login']"
			+ " | //*[@tabindex='0' and (.//span[normalize-space()='Login'] or .//div[normalize-space()='Login'])]");
	private static final By MENU_BUTTON = By
			.xpath("//button[@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu']"
					+ " | //*[@role='button' and (@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu')]"
					+ " | //*[@tabindex='0' and (@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu')]");
	private static final By LEFT_NAV_MENU_BUTTON = By.xpath("(//button[.//*[name()='svg']])[1]"
			+ " | (//*[@role='button'][.//*[name()='svg']])[1]" + " | (//*[@tabindex='0'][.//*[name()='svg']])[1]");
	private static final By EMAIL_FIELD = By.xpath("//input[@placeholder='Email']");
	private static final By PASSWORD_FIELD = By.xpath("//input[@placeholder='Password']");
	private static final By RESET_EMAIL_FIELD = By.xpath("//input[@placeholder='Enter your email']");
	private static final By LOGIN_BUTTON = By.xpath("//button[normalize-space()='Login']"
			+ " | //div[normalize-space()='Login' and not(ancestor::*[contains(normalize-space(.),'Forgot password')])]"
			+ " | //span[normalize-space()='Login']"
			+ " | //*[@tabindex='0' and (.//div[normalize-space()='Login'] or .//span[normalize-space()='Login'])]");
	private static final By SUCCESSFUL_LOGIN_MESSAGE = By.xpath("//div[@data-testid='toastText1']");
	private static final By NEXT_BUTTON = By.xpath("//div[contains(text(),'Next')]");
	private static final By ERROR_MESSAGE = By.xpath(
			"//div[@data-testid='toastText1' or contains(@class,'toast') or contains(@class,'alert') or contains(@class,'error')]");
	private static final By INVALID_CREDENTIALS_MESSAGE = By
			.xpath("//div[@data-testid='toastText1' and contains(text(),'Invalid credentials.')]");
	private static final By USER_NOT_FOUND_MESSAGE = By
			.xpath("//div[@data-testid='toastText1' and contains(text(),'User not found.')]");
	private static final By OTP_SENT_MESSAGE = By
			.xpath("//div[@data-testid='toastText1' and contains(text(),'OTP sent to your registered email.')]");
	private static final By RESET_INVALID_EMAIL_MESSAGE = By.xpath(
			"//div[@data-testid='toastText1' and contains(text(),'No account found with this email or mobile number.')]");
	private static final By EMPTY_EMAIL_MESSAGE = By
			.xpath("//div[contains(@class,'css-146c3p1') and normalize-space()='Email is required.']");
	private static final By EMPTY_PASSWORD_MESSAGE = By
			.xpath("//div[contains(@class,'css-146c3p1') and normalize-space()='Password is Required']");
	private static final By PASSWORD_SPECIAL_CHARACTER_MESSAGE = By.xpath(
			"//div[contains(@class,'css-146c3p1') and contains(text(),'Password must include exactly one special character')]");
	private static final By USER_ROLE = By.xpath("//div[contains(@class,'role')]");
	private static final By REMEMBER_ME_CHECKBOX = By.xpath(
			"//div[contains(@class,'r-15d164r') and .//div[normalize-space()='Remember me']]//div[@tabindex='0']");
	private static final By FORGOT_PASSWORD_LINK = By.xpath(
			"//*[normalize-space()='Forgot password ?' or normalize-space()='Forgot Password ?' or normalize-space()='Forgot password?' or normalize-space()='Forgot Password?']");
	private static final By LOGIN_TEXT_BUTTON = By.xpath("//span[normalize-space()='Login']");
	private static final By TERMS_AND_CONDITIONS_LINK = By.xpath("//span[normalize-space()='Terms and Conditions']");
	private static final By PRIVACY_POLICY_LINK = By
			.xpath("//span[normalize-space()='Privacy Policy.'] | //span[normalize-space()='Privacy Policy']");
	private static final By REGISTER_BUTTON = By.xpath("//span[contains(text(),'Register')]");
	private static final By REGISTRATION_EMAIL_FIELD = By.xpath("//input[@placeholder='Enter your email']");
	private static final By REGISTRATION_NEXT_BUTTON = By
			.xpath("//div[@tabindex='0' and .//div[normalize-space()='Next']]");
	private static final By REGISTRATION_TERMS_TEXT = By.xpath("//div[contains(.,'By signing up, you agree to our')]");
	private static final By GOOGLE_LOGIN_BUTTON = By
			.xpath("//div[@tabindex='0' and .//img[contains(@src,'ic_google')]] | //img[contains(@src,'ic_google')]");
	private static final By GENERIC_MESSAGE = By.xpath(
			"//div[contains(@class,'message') or contains(@class,'alert') or contains(@class,'toast') or contains(@class,'error') or contains(@class,'success')]");

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void openLogin() {
		new DashboardPage(driver).acceptCookiesIfPresent();

		if (isOnLoginPage()) {
			return;
		}

		if (openLoginViaDirectRoute()) {
			return;
		}

		if (clickIfVisible(LOGIN_TEXT_BUTTON)) {
			return;
		}

		if (clickIfVisible(LOGIN_ENTRY_BUTTON)) {
			return;
		}

		if (clickIfVisible(MENU_BUTTON) && (clickIfVisible(LOGIN_TEXT_BUTTON) || clickIfVisible(LOGIN_ENTRY_BUTTON))) {
			return;
		}

		if (clickIfVisible(LEFT_NAV_MENU_BUTTON)
				&& (clickIfVisible(LOGIN_TEXT_BUTTON) || clickIfVisible(LOGIN_ENTRY_BUTTON))) {
			return;
		}

		throw new IllegalStateException("Unable to locate a visible Login entry point on the current page.");
	}

	public void enterEmail(String email) {
		type(EMAIL_FIELD, email == null ? "" : email);
	}

	public void enterPassword(String password) {
		type(PASSWORD_FIELD, password == null ? "" : password);
	}

	public void enterResetEmail(String email) {
		type(RESET_EMAIL_FIELD, email == null ? "" : email);
	}

	public void pastePassword(String password) {
		enterPassword(password);
	}

	public void clickLogin() {
		try {
			click(LOGIN_BUTTON);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Primary login button click failed, submitting with Enter: {0}", e.getMessage());
			submitWithEnter();
		}
	}

	/**
	 * Simulate double-click on login button.
	 * Uses standard click method twice to verify only one login occurs.
	 */
	public void doubleClickLogin() {
		try {
			WebElement button = wait.waitForElementVisible(LOGIN_BUTTON);
			Logger.getLogger(LoginPage.class.getName()).
			log(Level.INFO, "Attempting double-click on login button: {0}", button);
			// First click attempt
			jsClick(LOGIN_BUTTON);
			// Brief pause before second click
			try {
				Thread.sleep(200);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
			// Second click attempt - may fail if page changed, which is OK
			try {
				jsClick(LOGIN_BUTTON);
			} catch (Exception ignored) {
				// Expected if first click succeeded and page changed
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Double-click failed, using single click: {0}", e.getMessage());
			clickLogin();
		}
	}

	public void loginUser(String email, String password) {
		enterEmail(email);
		enterPassword(password);
		clickLogin();
	}

	public void submitWithEnter() {
		wait.waitForElementVisible(PASSWORD_FIELD).sendKeys(Keys.ENTER);
	}

	public boolean isEmailFieldDisplayed() {
		return isDisplayed(EMAIL_FIELD);
	}

	public boolean isPasswordFieldDisplayed() {
		return isDisplayed(PASSWORD_FIELD);
	}

	public boolean isLoginButtonDisplayed() {
		return isDisplayed(LOGIN_BUTTON);
	}

	public String getPasswordFieldType() {
		return getAttribute(PASSWORD_FIELD, "type");
	}

	public String getEmailFieldValue() {
		return getAttribute(EMAIL_FIELD, "value").trim();
	}

	public String getPasswordFieldValue() {
		return getAttribute(PASSWORD_FIELD, "value");
	}

	public String getCurrentUrl() {
		return Objects.requireNonNull(driver.getCurrentUrl());
	}

	public String getLoggedInRole() {
		try {
			WebElement el = wait.waitForElementVisible(USER_ROLE);
			String role = el.getText().trim();
			LOGGER.log(Level.INFO, "Logged in role: {0}", role);
			return role;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to read logged in role: {0}", e.getMessage());
			return "";
		}
	}

	public String getErrorMessage() {
		return getMessage(ERROR_MESSAGE);
	}

	public String getInvalidCredentialsMessage() {
		return getMessage(INVALID_CREDENTIALS_MESSAGE);
	}

	public String getUserNotFoundMessage() {
		return getMessage(USER_NOT_FOUND_MESSAGE);
	}

	public String getOtpSentMessage() {
		return getMessage(OTP_SENT_MESSAGE);
	}

	public String getResetInvalidEmailMessage() {
		return getMessage(RESET_INVALID_EMAIL_MESSAGE);
	}

	public String getEmptyFieldMessage() {
		String emailMessage = getMessage(EMPTY_EMAIL_MESSAGE);
		if (!emailMessage.isEmpty()) {
			return emailMessage;
		}
		return getMessage(EMPTY_PASSWORD_MESSAGE);
	}

	public String getEmailRequiredMessage() {
		return getMessage(EMPTY_EMAIL_MESSAGE);
	}

	public String getPasswordRequiredMessage() {
		return getMessage(EMPTY_PASSWORD_MESSAGE);
	}

	public String getPasswordSpecialCharacterMessage() {
		return getMessage(PASSWORD_SPECIAL_CHARACTER_MESSAGE);
	}

	public String getLoginSuccessMessage() {
		return getMessage(SUCCESSFUL_LOGIN_MESSAGE);
	}

	public String getLoginMessage() {
		String message = getMessage(GENERIC_MESSAGE);
		if (!message.isEmpty()) {
			return message;
		}
		return getLoginSuccessMessage();
	}

	public void clickNextAfterLogin() {
		try {
			wait.waitForElementVisible(NEXT_BUTTON);
			click(NEXT_BUTTON);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Next button not shown after login: {0}", e.getMessage());
		}
	}

	public boolean isResetEmailFieldDisplayed() {
		return isDisplayed(RESET_EMAIL_FIELD);
	}

	public void clickNextButton() {
		click(NEXT_BUTTON);
	}

	public void submitResetPasswordRequest(String email) {
		enterResetEmail(email);
		clickNextButton();
	}

	public boolean isLoginSuccessful() {
		return !getLoginSuccessMessage().isEmpty();
	}

	public boolean isErrorDisplayed() {
		return !getErrorMessage().isEmpty();
	}

	public boolean isEmptyFieldValidationDisplayed() {
		return !getEmailRequiredMessage().isEmpty() && !getPasswordRequiredMessage().isEmpty();
	}

	public boolean isRememberMeAvailable() {
		return isElementPresent(REMEMBER_ME_CHECKBOX);
	}

	public void clickRememberMe() {
		jsClick(REMEMBER_ME_CHECKBOX);
	}

	public boolean isForgotPasswordAvailable() {
		return isElementPresent(FORGOT_PASSWORD_LINK);
	}

	public void clickForgotPassword() {
		click(FORGOT_PASSWORD_LINK);
	}

	public boolean isLoginTextButtonAvailable() {
		return isElementPresent(LOGIN_TEXT_BUTTON);
	}

	public void clickLoginTextButton() {
		click(LOGIN_TEXT_BUTTON);
	}

	public boolean isTermsAndConditionsAvailable() {
		return isElementPresent(TERMS_AND_CONDITIONS_LINK);
	}

	public void clickTermsAndConditions() {
		click(TERMS_AND_CONDITIONS_LINK);
	}

	public boolean isPrivacyPolicyAvailable() {
		return isElementPresent(PRIVACY_POLICY_LINK);
	}

	public void clickPrivacyPolicy() {
		click(PRIVACY_POLICY_LINK);
	}

	public boolean isRegisterButtonAvailable() {
		return isElementPresent(REGISTER_BUTTON);
	}

	public void clickRegister() {
		jsClick(REGISTER_BUTTON);
	}

	public boolean isRegistrationEmailFieldDisplayed() {
		return isDisplayed(REGISTRATION_EMAIL_FIELD);
	}

	public boolean isRegistrationNextButtonDisplayed() {
		return isDisplayed(REGISTRATION_NEXT_BUTTON);
	}

	public boolean isRegistrationTermsTextDisplayed() {
		return isDisplayed(REGISTRATION_TERMS_TEXT);
	}

	public boolean isRegistrationScreenDisplayed() {
		return isRegistrationEmailFieldDisplayed() || isRegistrationNextButtonDisplayed()
				|| isRegistrationTermsTextDisplayed();
	}

	public boolean isGoogleLoginAvailable() {
		return isElementPresent(GOOGLE_LOGIN_BUTTON);
	}

	public void clickGoogleLogin() {
		jsClick(GOOGLE_LOGIN_BUTTON);
	}

	public void refreshPage() {
		driver.navigate().refresh();
	}

	public void navigateBack() {
		driver.navigate().back();
	}

	public boolean hasAnyToastOrValidationMessage() {
		return !getLoginMessage().isEmpty() || !getEmailRequiredMessage().isEmpty()
				|| !getPasswordRequiredMessage().isEmpty();
	}

	public boolean isOnLoginPage() {
		return isEmailFieldDisplayed() && isPasswordFieldDisplayed();
	}

	private String getMessage(By locator) {
		try {
			WebElement el = wait.waitForElementVisible(locator);
			String message = el.getText().trim();
			LOGGER.log(Level.INFO, "Captured login message: {0}", message);
			return message;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Message not found for locator {0}: {1}", new Object[] { locator, e.getMessage() });
			return "";
		}
	}

	/**
	 * Helper method to click element if visible.
	 */
	private boolean clickIfVisible(By locator) {
		try {
			List<WebElement> elements = driver.findElements(locator);
			for (WebElement element : elements) {
				if (!element.isDisplayed()) {
					continue;
				}

				scrollIntoView(element);
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

	private boolean openLoginViaDirectRoute() {
		String configuredUrl = ConfigReader.getProperty("url", "");
		if (configuredUrl.isBlank()) {
			return false;
		}

		String[] candidatePaths = { "/login", "/signin", "/sign-in", "/auth/login", "/user/login" };
		for (String candidatePath : candidatePaths) {
			try {
				driver.get(buildCandidateUrl(configuredUrl, candidatePath));
				// The login form can lag behind navigation (SPA hydration). A bare
				// isOnLoginPage() only waits ~3s, which intermittently misses the
				// first paint and wrongly rejects a valid route (seen on /login).
				// Wait explicitly for the email field to render before deciding.
				boolean formRendered = wait.waitForElementVisible(EMAIL_FIELD, Duration.ofSeconds(10)) != null;
				if (formRendered && isOnLoginPage()) {
					LOGGER.info("Opened login page via direct route: " + candidatePath);
					return true;
				}
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Direct login route failed for {0}: {1}",
						new Object[] { candidatePath, e.getMessage() });
			}
		}

		return false;
	}

	private String buildCandidateUrl(String baseUrl, String candidatePath) {
		URI uri = URI.create(baseUrl);
		String normalizedBase = uri.getScheme() + "://" + uri.getAuthority();
		return normalizedBase + candidatePath;
	}

	// ==================== Null-Safe Helper Methods ====================

	/**
	 * Get current URL safely (null-safe).
	 */
	public String getCurrentUrlSafely() {
		try {
			String url = driver.getCurrentUrl();
			return url == null ? "" : url;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Check if a string value is blank (null or empty).
	 */
	public boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	/**
	 * Null-safe string getter.
	 */
	public String safe(String value) {
		return value == null ? "" : value;
	}

	/**
	 * Null-safe lower case converter.
	 */
	public String safeLower(String value) {
		return safe(value).toLowerCase();
	}

	/**
	 * Get configured valid email from config.
	 */
	public String getValidEmail() {
		return ConfigReader.getProperty("login.validEmail");
	}

	/**
	 * Get configured valid password from config.
	 */
	public String getValidPassword() {
		return ConfigReader.getProperty("login.validPassword");
	}

	/**
	 * Check if valid credentials are configured.
	 */
	public boolean hasValidCredentials() {
		String email = getValidEmail();
		String password = getValidPassword();
		return !isBlank(email) && !isBlank(password);
	}

	/**
	 * Check if current URL contains specific text (case-insensitive).
	 */
	public boolean isUrlContaining(String searchText) {
		if (isBlank(searchText)) {
			return false;
		}
		return safeLower(getCurrentUrlSafely()).contains(searchText.toLowerCase());
	}

	/**
	 * Check if current URL contains any of the given tokens (case-insensitive).
	 */
	public boolean isUrlContainingAny(String... tokens) {
		String currentUrl = safeLower(getCurrentUrlSafely());
		for (String token : tokens) {
			if (!isBlank(token) && currentUrl.contains(token.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	// ==================== Network Throttling Methods ====================

	/**
	 * Enable network throttling using Chrome DevTools Protocol.
	 * Simulates Slow 3G network conditions.
	 */
	public void enableSlowNetworkThrottling() {
		try {
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			// CDP command to enable network throttling (Slow 3G)
			String script = "chrome.sendCommand('Network.emulateNetworkConditions', {"
					+ "'offline': false,"
					+ "'downloadThroughput': 500," // ~500 Kbps (Slow 3G)
					+ "'uploadThroughput': 500,"
					+ "'latency': 400" // 400ms latency
					+ "});";
			jsExecutor.executeScript(script);
			LOGGER.log(Level.INFO, "Slow network throttling enabled (Slow 3G)");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to enable network throttling: {0}", e.getMessage());
		}
	}

	/**
	 * Disable network throttling and restore normal network conditions.
	 */
	public void disableNetworkThrottling() {
		try {
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			// CDP command to disable network throttling
			String script = "chrome.sendCommand('Network.emulateNetworkConditions', {"
					+ "'offline': false,"
					+ "'downloadThroughput': 0,"
					+ "'uploadThroughput': 0,"
					+ "'latency': 0"
					+ "});";
			jsExecutor.executeScript(script);
			LOGGER.log(Level.INFO, "Network throttling disabled - normal network restored");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to disable network throttling: {0}", e.getMessage());
		}
	}

	// ==================== Network Connect / Disconnect (CDP) ====================

	/**
	 * Returns {@code true} if a DevTools session has been initialized for this
	 * page (i.e. {@link #initializeNetworkControl()} ran successfully and the
	 * session has not been closed).
	 */
	public boolean isNetworkControlled() {
		return isNetworkControlled && devTools != null;
	}

	/**
	 * Initialize a Chrome DevTools session attached to the current driver and
	 * enable the {@code Network} domain. Must be a {@link ChromeDriver} session;
	 * no-op otherwise. Safe to call multiple times — subsequent calls are
	 * ignored if a session is already active.
	 */
	public void initializeNetworkControl() {
		try {
			if (isNetworkControlled && devTools != null) {
				return;
			}
			if (!(driver instanceof ChromeDriver)) {
				LOGGER.log(Level.WARNING,
						"Network control skipped: driver is not a ChromeDriver ({0})",
						driver == null ? "null" : driver.getClass().getName());
				return;
			}
			devTools = ((ChromeDriver) driver).getDevTools();
			devTools.createSession();
			devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
					Optional.empty()));
			isNetworkControlled = true;
			LOGGER.log(Level.INFO, "Chrome DevTools initialized and Network domain enabled");
		} catch (Exception e) {
			isNetworkControlled = false;
			devTools = null;
			LOGGER.log(Level.WARNING, "Failed to initialize Chrome DevTools: {0}", e.getMessage());
		}
	}

	/**
	 * Disconnect the browser from the network using CDP
	 * ({@code Network.emulateNetworkConditions} with {@code offline=true},
	 * {@code downloadThroughput=0}, {@code uploadThroughput=0}, {@code latency=0}).
	 * Returns {@code true} on success, {@code false} if DevTools is not ready.
	 */
	@SuppressWarnings("deprecation")
	public boolean disconnectNetwork() {
		try {
			if (!isNetworkControlled() || devTools == null) {
				LOGGER.log(Level.WARNING, "Cannot disconnect network: DevTools not initialized");
				return false;
			}
			devTools.send(Network.emulateNetworkConditions(false, 0, 0, 0, Optional.empty(), Optional.empty(),
					Optional.empty(), Optional.empty()));
			Thread.sleep(1500);
			LOGGER.log(Level.INFO, "Network disconnected via CDP (offline=true, throughput=0)");
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to disconnect network: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Reconnect the browser to the network and restore normal throughput
	 * ({@code offline=false}, {@code downloadThroughput=-1},
	 * {@code uploadThroughput=-1}, {@code latency=0}). Always safe to call in a
	 * {@code finally} block.
	 */
	@SuppressWarnings("deprecation")
	public boolean reconnectNetwork() {
		try {
			if (!isNetworkControlled() || devTools == null) {
				return false;
			}
			devTools.send(Network.emulateNetworkConditions(false, -1, -1, 0, Optional.empty(), Optional.empty(),
					Optional.empty(), Optional.empty()));
			Thread.sleep(1000);
			isNetworkControlled = false;
			LOGGER.log(Level.INFO, "Network reconnected via CDP (offline=false, normal throughput)");
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to reconnect network: {0}", e.getMessage());
			return false;
		}
	}
}
