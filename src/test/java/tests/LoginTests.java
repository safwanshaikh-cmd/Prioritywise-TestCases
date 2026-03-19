package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

/**
 * Login Test Cases aligned with LoginPage
 */
public class LoginTests extends BaseTest {

	@Test(priority = 1)
	public void verifyValidLogin() {

		LoginPage login = new LoginPage(driver);

		login.openLogin();
		login.loginUser("valid@mail.com", "Password123");

		String successMsg = login.getLoginSuccessMessage();

		Assert.assertTrue(!successMsg.isEmpty(), "Login failed - success message not displayed");

		login.clickNextAfterLogin(); // if required
	}

	@Test(priority = 2)
	public void verifyInvalidPassword() {

		LoginPage login = new LoginPage(driver);

		login.openLogin();
		login.loginUser("valid@mail.com", "wrongpass");

		String errorMsg = login.getErrorMessage();

		Assert.assertTrue(!errorMsg.isEmpty(), "Error message not displayed for invalid login");
	}

	@Test(priority = 3)
	public void verifyEmptyFields() {

		LoginPage login = new LoginPage(driver);

		login.openLogin();
		login.clickLogin(); // without entering data

		String validationMsg = login.getEmptyFieldMessage();

		Assert.assertTrue(!validationMsg.isEmpty(), "Validation message not displayed for empty fields");
	}
}