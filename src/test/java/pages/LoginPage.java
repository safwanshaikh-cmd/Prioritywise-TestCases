package pages;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import base.BasePage;
import listeners.TestListener;
import utils.ConfigReader;

/**
 * Page object for the login page and related validations.
 */
public class LoginPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());
	private static final String INVALID_CREDENTIALS_TEXT = "Invalid credentials.";
	private static final String USER_NOT_FOUND_TEXT = "User not found.";
	private static final String OTP_SENT_TEXT = "OTP sent to your registered email.";
	private static final String RESET_INVALID_EMAIL_TEXT = "No account found with this email or mobile number.";
	private static final String EMAIL_REQUIRED_TEXT = "Email is required.";
	private static final String PASSWORD_REQUIRED_TEXT = "Password is Required";

	private static final By LOGIN_BUTTON_HOME = By.xpath(
			"//div[@tabindex='0' and (.//div[normalize-space()='Login'] or .//span[normalize-space()='Login'])]"
					+ " | //div[contains(@class,'css-146c3p1') and normalize-space()='Login']");
	private static final By EMAIL_FIELD = By.xpath("//input[@placeholder='Email']");
	private static final By PASSWORD_FIELD = By.xpath("//input[@placeholder='Password']");
	private static final By RESET_EMAIL_FIELD = By.xpath("//input[@placeholder='Enter your email']");
	private static final By LOGIN_BUTTON = By.xpath(
			"//div[@tabindex='0' and .//div[normalize-space()='Login']]"
					+ " | //button[normalize-space()='Login']"
					+ " | //div[contains(@class,'css-146c3p1') and contains(@style,'poppins_medium') and normalize-space()='Login']"
					+ " | //div[contains(@class,'css-146c3p1') and normalize-space()='Login']");
	private static final By TOAST_CONTAINER = By.xpath("//*[@data-testid='toastContentContainer']");
	private static final By TOAST_TEXT = By.xpath("//*[@data-testid='toastContentContainer']//*[@data-testid='toastText1']");
	private static final By SUCCESSFUL_LOGIN_MESSAGE = TOAST_TEXT;
	private static final By NEXT_BUTTON = By.xpath("//div[contains(text(),'Next')]");
	private static final By ERROR_MESSAGE = By.xpath(
			"//*[@data-testid='toastContentContainer']//*[@data-testid='toastText1']"
					+ " | //div[@data-testid='toastText1' or contains(@class,'toast') or contains(@class,'alert') or contains(@class,'error')]");
	private static final By INVALID_CREDENTIALS_MESSAGE = By
			.xpath("//div[@data-testid='toastText1' and contains(text(),'Invalid credentials.')]");
	private static final By USER_NOT_FOUND_MESSAGE = By
			.xpath("//div[@data-testid='toastText1' and contains(text(),'User not found.')]");
	private static final By OTP_SENT_MESSAGE = By
			.xpath("//div[@data-testid='toastText1' and contains(text(),'OTP sent to your registered email.')]");
	private static final By RESET_INVALID_EMAIL_MESSAGE = By.xpath(
			"//div[@data-testid='toastText1' and contains(text(),'No account found with this email or mobile number.')]");
	private static final By EMPTY_EMAIL_MESSAGE = By.xpath(
			"//div[contains(@class,'css-146c3p1') and normalize-space()='Email is required.']");
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
	private static final By PRIVACY_POLICY_LINK = By.xpath("//span[normalize-space()='Privacy Policy.'] | //span[normalize-space()='Privacy Policy']");
	private static final By REGISTER_BUTTON = By.xpath("//span[contains(text(),'Register')]");
	private static final By REGISTRATION_EMAIL_FIELD = By.xpath("//input[@placeholder='Enter your email']");
	private static final By REGISTRATION_NEXT_BUTTON = By.xpath(
			"//div[@tabindex='0' and .//div[normalize-space()='Next']]");
	private static final By REGISTRATION_TERMS_TEXT = By.xpath(
			"//div[contains(.,'By signing up, you agree to our')]");
	private static final By GOOGLE_LOGIN_BUTTON = By.xpath(
			"//div[@tabindex='0' and .//img[contains(@src,'ic_google')]] | //img[contains(@src,'ic_google')]");
	private static final By GENERIC_MESSAGE = By.xpath(
			"//div[contains(@class,'message') or contains(@class,'alert') or contains(@class,'toast') or contains(@class,'error') or contains(@class,'success')]");

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void openLogin() {
		click(LOGIN_BUTTON_HOME);
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
		click(LOGIN_BUTTON);
	}

	public void doubleClickLogin() {
		click(LOGIN_BUTTON);
		click(LOGIN_BUTTON);
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
		return driver.getCurrentUrl();
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
		return getVisibleMessageNow(ERROR_MESSAGE);
	}

	public String getInvalidCredentialsMessage() {
		return firstNonBlank(getVisibleMessageNow(INVALID_CREDENTIALS_MESSAGE),
				findLoginMessageContaining("invalid credentials"));
	}

	public String getUserNotFoundMessage() {
		return firstNonBlank(getVisibleMessageNow(USER_NOT_FOUND_MESSAGE), findLoginMessageContaining("user not found"));
	}

	public String getOtpSentMessage() {
		return firstNonBlank(getVisibleMessageNow(OTP_SENT_MESSAGE), findLoginMessageContaining("otp sent"));
	}

	public String getResetInvalidEmailMessage() {
		return firstNonBlank(getVisibleMessageNow(RESET_INVALID_EMAIL_MESSAGE),
				findLoginMessageContaining("no account found"));
	}

	public String getEmptyFieldMessage() {
		String emailMessage = getVisibleMessageNow(EMPTY_EMAIL_MESSAGE);
		if (!emailMessage.isEmpty()) {
			return emailMessage;
		}
		return getVisibleMessageNow(EMPTY_PASSWORD_MESSAGE);
	}

	public String getEmailRequiredMessage() {
		return firstNonBlank(getVisibleMessageNow(EMPTY_EMAIL_MESSAGE), findLoginMessageContaining("email is required"));
	}

	public String getPasswordRequiredMessage() {
		return firstNonBlank(getVisibleMessageNow(EMPTY_PASSWORD_MESSAGE),
				findLoginMessageContaining("password is required"));
	}

	public String getPasswordSpecialCharacterMessage() {
		return getVisibleMessageNow(PASSWORD_SPECIAL_CHARACTER_MESSAGE);
	}

	public String getLoginSuccessMessage() {
		String message = getVisibleMessageNow(SUCCESSFUL_LOGIN_MESSAGE);
		String normalized = message.toLowerCase();

		if (normalized.contains("success") || normalized.contains("welcome") || normalized.contains("logged in")) {
			return message;
		}

		if (isElementPresentNow(NEXT_BUTTON) || !getLoggedInRole().isBlank()) {
			return "Login successful";
		}

		return "";
	}

	public String getLoginMessage() {
		return firstNonBlank(getVisibleMessageNow(TOAST_TEXT), getVisibleMessageNow(INVALID_CREDENTIALS_MESSAGE),
				getVisibleMessageNow(USER_NOT_FOUND_MESSAGE), getVisibleMessageNow(RESET_INVALID_EMAIL_MESSAGE),
				getVisibleMessageNow(OTP_SENT_MESSAGE), getVisibleMessageNow(ERROR_MESSAGE),
				getVisibleMessageNow(GENERIC_MESSAGE), getVisibleMessageNow(SUCCESSFUL_LOGIN_MESSAGE));
	}

	public void printLoginMessage(String context) {
		printAndCaptureLoginMessage(context);
	}

	public String printAndCaptureLoginMessage(String context) {
		String title = "Login Message" + (context == null || context.isBlank() ? "" : ": " + context);
		String message = waitForLoginMessage();
		System.out.println("=== " + title + " ===");
		if (message.isBlank()) {
			System.out.println("No login message displayed.");
			TestListener.logInfo(title + ": none");
			return "";
		}

		System.out.println(message);
		TestListener.logInfo(title);
		TestListener.logInfo(message);
		return message;
	}

	public String waitForLoginMessage() {
		long deadline = System.currentTimeMillis() + 3000;

		while (System.currentTimeMillis() < deadline) {
			String message = firstNonBlank(getLoginMessage(), getVisibleMessageNow(EMPTY_EMAIL_MESSAGE),
					getVisibleMessageNow(EMPTY_PASSWORD_MESSAGE), getVisibleMessageNow(PASSWORD_SPECIAL_CHARACTER_MESSAGE));
			if (!message.isBlank()) {
				return message;
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.log(Level.FINE, "Interrupted while waiting for login message: {0}", e.getMessage());
				break;
			}
		}

		return "";
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

	public String getExpectedInvalidCredentialsText() {
		return INVALID_CREDENTIALS_TEXT;
	}

	public String getExpectedUserNotFoundText() {
		return USER_NOT_FOUND_TEXT;
	}

	public String getExpectedOtpSentText() {
		return OTP_SENT_TEXT;
	}

	public String getExpectedResetInvalidEmailText() {
		return RESET_INVALID_EMAIL_TEXT;
	}

	public String getExpectedEmailRequiredText() {
		return EMAIL_REQUIRED_TEXT;
	}

	public String getExpectedPasswordRequiredText() {
		return PASSWORD_REQUIRED_TEXT;
	}

	public void openForgotPasswordFlow() {
		if (isLoginTextButtonAvailable()) {
			clickLoginTextButton();
		}
		clickForgotPassword();
	}

	private String getVisibleMessageNow(By locator) {
		try {
			driver.manage().timeouts().implicitlyWait(Duration.ZERO);
			try {
				for (WebElement element : driver.findElements(locator)) {
					if (!element.isDisplayed()) {
						continue;
					}

					String text = element.getText().trim();
					if (!text.isEmpty()) {
						LOGGER.log(Level.INFO, "Captured visible login message: {0}", text);
						return text;
					}
				}
			} finally {
				driver.manage().timeouts()
						.implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicitWait", 10)));
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Visible message not found for locator {0}: {1}",
					new Object[] { locator, e.getMessage() });
		}
		return "";
	}

	private String findLoginMessageContaining(String... fragments) {
		String message = getLoginMessage();
		if (message.isBlank()) {
			return "";
		}

		String normalizedMessage = message.toLowerCase();
		for (String fragment : fragments) {
			if (fragment != null && !fragment.isBlank() && !normalizedMessage.contains(fragment.toLowerCase())) {
				return "";
			}
		}
		return message;
	}

	private boolean isElementPresent(By locator) {
		try {
			List<WebElement> elements = driver.findElements(locator);
			return elements.stream().anyMatch(WebElement::isDisplayed);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isElementPresentNow(By locator) {
		try {
			driver.manage().timeouts().implicitlyWait(Duration.ZERO);
			try {
				List<WebElement> elements = driver.findElements(locator);
				return elements.stream().anyMatch(WebElement::isDisplayed);
			} finally {
				driver.manage().timeouts()
						.implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicitWait", 10)));
			}
		} catch (Exception e) {
			return false;
		}
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return "";
	}
}
