package pages;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import base.BasePage;

/**
 * Page object for the registration page.
 */
public class RegistrationPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(RegistrationPage.class.getName());

	private static final By LOGIN_BUTTON = By.xpath(
			"(//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-9oks40 r-1tw7wh r-eu3ka r-1777fci r-uhung1 r-3o4zer'])[1]");
	private static final By REGISTER_LINK = By.xpath("//span[contains(text(),'Register')]");
	private static final By NAME = By.xpath("//input[@placeholder='Name']");
	private static final By USERNAME = By.xpath("//input[@placeholder='Username']");
	private static final By EMAIL = By.xpath("(//input[@placeholder='Email'])[2]");
	private static final By PASSWORD = By.xpath("(//input[@placeholder='Password'])[2]");
	private static final By CONFIRM_PASSWORD = By.xpath("(//input[@placeholder='Confirm password'])[1]");
	private static final By ROLE_DROPDOWN = By
			.xpath("//div[@tabindex='0' and .//div[normalize-space()='Select Role']]");
	private static final By CHECKBOX = By.xpath(
			"//div[.//div[normalize-space()='Terms and Conditions']]//div[@tabindex='0' or .//img[contains(@src,'check.')]][1]");
	private static final By TERMS_LABEL = By.xpath("//div[normalize-space()='Terms and Conditions']");
	private static final By REGISTER_BUTTON = By.xpath("//div[@tabindex='0' and .//div[normalize-space()='Register']]");
	private static final By SUCCESS_MESSAGE = By
			.xpath("(//div[@class='css-146c3p1 r-2c484d r-adyw6z r-cwly78 r-1x0uki6 r-q4m81j'])[1]");
	private static final By NEXT_BUTTON = By.xpath("//div[@tabindex='0' and .//div[normalize-space()='Next']]");
	private static final By NAME_REQUIRED_WARNING = By
			.xpath("//div[contains(@class,'css-146c3p1') and normalize-space()='Name is required']");
	private static final By USERNAME_REQUIRED_WARNING = By
			.xpath("//div[contains(@class,'css-146c3p1') and normalize-space()='Username is required']");
	private static final By EMAIL_REQUIRED_WARNING = By
			.xpath("//div[contains(@class,'css-146c3p1') and normalize-space()=' Email is required.']");
	private static final By PASSWORD_REQUIRED_WARNING = By
			.xpath("//div[contains(@class,'css-146c3p1') and normalize-space()='Password is Required']");
	private static final By PASSWORD_CONFIRMATION_WARNING = By
			.xpath("//div[contains(@class,'css-146c3p1') and normalize-space()='Password confirmation is required']");
	private static final By ROLE_WARNING = By
			.xpath("//div[contains(@class,'css-146c3p1') and normalize-space()='Please select your role']");
	private static final By TERMS_WARNING = By.xpath(
			"//div[contains(@class,'css-146c3p1') and normalize-space()='You must accept the terms and conditions']");
	private static final By ALL_ERROR_MESSAGES = By.xpath("//div[contains(@class,'css-146c3p1')]");
	private static final By DUPLICATE_EMAIL_WARNING = By
			.xpath("(//div[@class='css-146c3p1'][normalize-space()='The email address is already taken.'])[1]");

	public RegistrationPage(WebDriver driver) {
		super(driver);
	}

	public void openLogin() {
		click(LOGIN_BUTTON);
	}

	public void openRegistration() {
		click(REGISTER_LINK);
	}

	public void selectRole(String role) {
		click(ROLE_DROPDOWN);
		By roleOption = By.xpath("//div[contains(text(),'" + role + "')]");
		click(roleOption);
	}

	public void enterName(String fullName) {
		type(NAME, fullName);
	}

	public void enterUsername(String uname) {
		type(USERNAME, uname);
	}

	public void enterEmail(String mail) {
		type(EMAIL, mail);
	}

	public void enterPassword(String pass) {
		type(PASSWORD, pass);
	}

	public void enterConfirmPassword(String pass) {
		type(CONFIRM_PASSWORD, pass);
	}

	public void acceptTerms() {
		try {
			jsClick(CHECKBOX);
		} catch (Exception e) {
			jsClick(TERMS_LABEL);
		}
	}

	public void clickRegister() {
		jsClick(REGISTER_BUTTON);
	}

	public void clickNext() {
		click(NEXT_BUTTON);
	}

	public void registerUser(String fullName, String uname, String mail, String pass, String role) {
		enterName(fullName);
		enterUsername(uname);
		enterEmail(mail);
		enterPassword(pass);
		enterConfirmPassword(pass);
		selectRole(role);
		acceptTerms();
		clickRegister();
	}

	public boolean successMessageDisplayed() {
		return isDisplayed(SUCCESS_MESSAGE);
	}

	public boolean isDuplicateEmailWarningDisplayed() {
		return isDisplayed(DUPLICATE_EMAIL_WARNING);
	}

	public String getDuplicateEmailWarningMessage() {
		String warning = getText(DUPLICATE_EMAIL_WARNING);
		if (warning == null || warning.isEmpty()) {
			LOGGER.log(Level.FINE, "Duplicate Email Warning not found");
			return "";
		}
		LOGGER.log(Level.INFO, "Warning Message: {0}", warning);
		return warning;
	}

	public boolean emailRequiredWarningDisplayed() {
		return isDisplayed(EMAIL_REQUIRED_WARNING);
	}

	public String getNameWarningMessage() {
		return findValidationMessage("Name is required");
	}

	public String getUsernameWarningMessage() {
		return findValidationMessage("Username is required");
	}

	public String getEmailWarningMessage() {
		return findValidationMessage("Email is required.");
	}

	public String getPasswordWarningMessage() {
		return findValidationMessage("Password is Required");
	}

	public String getPasswordConfirmationWarningMessage() {
		return findValidationMessage("Password confirmation is required");
	}

	public String getRoleWarningMessage() {
		return findValidationMessage("Please select your role");
	}

	public boolean isTermsWarningDisplayed() {
		return isDisplayed(TERMS_WARNING);
	}

	public String getTermsWarningMessage() {
		return findValidationMessage("You must accept the terms and conditions");
	}

	public List<WebElement> getAllErrorMessages() {
		return driver.findElements(ALL_ERROR_MESSAGES);
	}

	public void printAllErrorMessages() {
		List<WebElement> errors = getAllErrorMessages();
		for (WebElement error : errors) {
			String msg = error.getText().trim();
			if (!msg.isEmpty()) {
				LOGGER.log(Level.INFO, "Validation Message: {0}", msg);
			}
		}
	}

	public boolean isAnyErrorDisplayed() {
		List<WebElement> errors = getAllErrorMessages();
		for (WebElement error : errors) {
			if (error.isDisplayed() && !error.getText().trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private String findValidationMessage(String expectedText) {
		List<WebElement> errors = getAllErrorMessages();
		for (WebElement error : errors) {
			String message = error.getText().trim();
			if (message.isEmpty()) {
				message = getElementTextViaJs(error).trim();
			}
			if (!message.isEmpty() && message.contains(expectedText)) {
				return message;
			}
		}
		String pageSource = driver.getPageSource();
		if (pageSource != null && pageSource.contains(expectedText)) {
			return expectedText;
		}
		return "";
	}

	private String getElementTextViaJs(WebElement element) {
		try {
			Object value = ((JavascriptExecutor) driver).executeScript(
					"return (arguments[0].innerText || arguments[0].textContent || '').trim();", element);
			return value == null ? "" : value.toString();
		} catch (Exception e) {
			return "";
		}
	}
}
