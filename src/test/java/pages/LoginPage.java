package pages;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import base.BasePage;
import utils.ConfigReader;

/**
 * Page object for the login page and related validations.
 */
public class LoginPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());

	private static final By LOGIN_BUTTON_HOME = By.xpath(
			"(//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-9oks40 r-1tw7wh r-eu3ka r-1777fci r-uhung1 r-3o4zer'])[1]");
	private static final By LOGIN_ENTRY_BUTTON = By.xpath(
			"//span[normalize-space()='Login']"
					+ " | //div[normalize-space()='Login']"
					+ " | //button[normalize-space()='Login']"
					+ " | //*[@tabindex='0' and (.//span[normalize-space()='Login'] or .//div[normalize-space()='Login'])]");
	private static final By MENU_BUTTON = By.xpath(
			"//button[@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu']"
					+ " | //*[@role='button' and (@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu')]"
					+ " | //*[@tabindex='0' and (@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu')]");
	private static final By LEFT_NAV_MENU_BUTTON = By.xpath(
			"(//button[.//*[name()='svg']])[1]"
					+ " | (//*[@role='button'][.//*[name()='svg']])[1]"
					+ " | (//*[@tabindex='0'][.//*[name()='svg']])[1]");
	private static final By EMAIL_FIELD = By.xpath("//input[@placeholder='Email']");
	private static final By PASSWORD_FIELD = By.xpath("//input[@placeholder='Password']");
	private static final By RESET_EMAIL_FIELD = By.xpath("//input[@placeholder='Enter your email']");
	private static final By LOGIN_BUTTON = By.xpath("//div[@class='css-146c3p1'][normalize-space()='Login']");
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

		if (clickIfVisible(LOGIN_BUTTON_HOME)) {
			return;
		}

		if (openLoginViaDirectRoute()) {
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

	private boolean isElementPresent(By locator) {
		try {
			List<WebElement> elements = driver.findElements(locator);
			return elements.stream().anyMatch(WebElement::isDisplayed);
		} catch (Exception e) {
			return false;
		}
	}

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
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
				}
				return true;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to click login entry for locator {0}: {1}",
					new Object[] { locator, e.getMessage() });
		}
		return false;
	}

	private void scrollIntoView(WebElement element) {
		try {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to scroll login element into view: {0}", e.getMessage());
		}
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
				if (isOnLoginPage()) {
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
}
