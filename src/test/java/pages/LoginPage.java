package pages;

<<<<<<< HEAD
import java.time.Duration;
=======
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
<<<<<<< HEAD
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
=======
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab

/**
 * Professional Page Object for the Login page. Locators are preserved exactly
 * as in the original file.
 */
public class LoginPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	// ---------------- Locators (unchanged) ----------------
	private static final By LOGIN_BUTTON_HOME = By.xpath(
			"(//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-9oks40 r-1tw7wh r-eu3ka r-1777fci r-uhung1 r-3o4zer'])[1]");

	private static final By EMAIL_FIELD = By.xpath("(//input[@placeholder='Email'])");

	private static final By PASSWORD_FIELD = By.xpath("//input[@placeholder='Password']");

	private static final By LOGIN_BUTTON = By.xpath("//div[@class='css-146c3p1'][normalize-space()='Login']");

	private static final By SUCCESSFUL_LOGIN_MESSAGE = By.xpath("//div[@data-testid='toastText1']");

	private static final By NEXT_BUTTON = By.xpath("//div[contains(text(),'Next')]");

	private static final By ERROR_MESSAGE = By
<<<<<<< HEAD
			.xpath("//div[@data-testid='toastText1'][contains(text(),'Invalid credentials.')]");
=======
			.xpath("//div[@class='css-g5y9jx r-1habvwh r-13awgt0 r-1777fci r-1kti4dy']");
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab

	private static final By EMPTY_FIELD_MESSAGE = By
			.xpath("//div[@class='css-146c3p1'][normalize-space()='Email is required.']");

	// Role displayed after login
	private static final By USER_ROLE = By.xpath("//div[contains(@class,'role')]");

<<<<<<< HEAD
	private static final By MESSAGE = By
			.xpath("//div[contains(@class,'message') or contains(@class,'alert') or contains(@class,'toast')]");

=======
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
	// ---------------- Actions ----------------

	public void openLogin() {
		click(LOGIN_BUTTON_HOME);
	}

	public void enterEmail(String email) {
		type(EMAIL_FIELD, email);
	}

	public void enterPassword(String password) {
		type(PASSWORD_FIELD, password);
	}

	public void clickLogin() {
		click(LOGIN_BUTTON);
	}

	public void waitForSuccessfulLoginMessage() {
		wait.waitForElementVisible(SUCCESSFUL_LOGIN_MESSAGE);
	}

	public void loginUser(String email, String password) {
		enterEmail(email);
		enterPassword(password);
		clickLogin();
	}

	// Capture role
	public String getLoggedInRole() {
		try {
			WebElement el = wait.waitForElementVisible(USER_ROLE);
			String role = el.getText();
			LOGGER.log(Level.INFO, "Logged In Role: {0}", role);
			return role;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to read logged in role: {0}", e.getMessage());
			return "";
		}
	}

	// Invalid User or Password error message
	public String getErrorMessage() {
		try {
			WebElement el = wait.waitForElementVisible(ERROR_MESSAGE);
			String error = el.getText();
			LOGGER.log(Level.INFO, "Error Message: {0}", error);
			return error;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Error message not found: {0}", e.getMessage());
			return "";
		}
	}

	// Empty fields validation message
	public String getEmptyFieldMessage() {
		try {
			WebElement el = wait.waitForElementVisible(EMPTY_FIELD_MESSAGE);
			String message = el.getText();
			LOGGER.log(Level.INFO, "Validation Message: {0}", message);
			return message;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Empty field message not found: {0}", e.getMessage());
			return "";
		}
	}

	// Capture Login Success Message
	public String getLoginSuccessMessage() {
		try {
<<<<<<< HEAD
			// Wait until text is actually present (IMPORTANT)
			WebElement el = wait.waitForElementVisible(SUCCESSFUL_LOGIN_MESSAGE);

			String message = el.getText().trim();

			LOGGER.log(Level.INFO, "Login Success Message: {0}", message);

			return message;

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Login success message not found or empty: {0}", e.getMessage());
=======
			WebElement el = wait.waitForElementVisible(SUCCESSFUL_LOGIN_MESSAGE);
			String message = el.getText();
			LOGGER.log(Level.INFO, "Login Success Message: {0}", message);
			return message;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Login success message not found: {0}", e.getMessage());
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
			return "";
		}
	}

	// Click Next button after login
	public void clickNextAfterLogin() {
		wait.waitForElementVisible(NEXT_BUTTON);
		click(NEXT_BUTTON);
	}

<<<<<<< HEAD
=======
	// Check if login success
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
	public boolean isLoginSuccessful() {
		return !getLoginSuccessMessage().isEmpty();
	}

<<<<<<< HEAD
=======
	// Check if error displayed
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
	public boolean isErrorDisplayed() {
		return !getErrorMessage().isEmpty();
	}

<<<<<<< HEAD
	public boolean isEmptyFieldValidationDisplayed() {
		return !getEmptyFieldMessage().isEmpty();
	}

	public String getLoginMessage() {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		By messageLocator = By.xpath("//div[contains(@class,'message') or " + "contains(@class,'alert') or "
				+ "contains(@class,'toast') or " + "contains(@class,'error') or " + "contains(@class,'success')]");

		try {
			WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(messageLocator));

			String text = message.getText().trim();
			System.out.println("Captured Message: " + text);

			return text;

		} catch (Exception e) {
			return "";
		}
	}
=======
	// Check empty validation
	public boolean isEmptyFieldValidationDisplayed() {
		return !getEmptyFieldMessage().isEmpty();
	}
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
}