package tests;

import java.util.List;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

public class SideMenuTests extends BaseTest {

	private DashboardPage dashboard;
	private LoginPage login;

	@BeforeMethod(alwaysRun = true)
	public void initPagesAndLogin() {
		ConfigReader.reload();
		dashboard = new DashboardPage(driver);
		login = new LoginPage(driver);
		loginAsConsumer();
	}

	@Test(priority = 351, retryAnalyzer = RetryAnalyzer.class)
	public void verifyHamburgerClickOpensMenu() {
		Assert.assertTrue(dashboard.isHamburgerMenuVisible(), "TC_351: expected hamburger button to be visible.");
		Assert.assertTrue(dashboard.openSideMenu(), "TC_351: expected hamburger click to open the side menu.");
		Assert.assertTrue(dashboard.isSideMenuOpen(), "TC_351: expected side menu to remain visible after opening.");
	}

	@Test(priority = 352, retryAnalyzer = RetryAnalyzer.class)
	public void verifyHamburgerOpenCloseToggle() {
		Assert.assertTrue(dashboard.openSimpleSideMenu(), "TC_352: expected side menu to open on hamburger click.");
		dashboard.printSimpleSideMenuButtonNames();
		Assert.assertTrue(dashboard.closeSimpleSideMenu(), "TC_352: expected side menu to close after printing buttons.");
		Assert.assertFalse(dashboard.isSimpleSideMenuOpen(), "TC_352: expected side menu to be hidden after closing.");
	}

	@Test(priority = 353, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAllSideMenuItemsVisible() {
		Assert.assertTrue(dashboard.openSimpleSideMenu(), "TC_353: expected side menu to open on hamburger click.");
		Assert.assertTrue(dashboard.waitForSimpleSideMenuVisibility(),
				"TC_353: expected side menu buttons to be visible after opening.");
		dashboard.printSimpleSideMenuButtonNames();
	}

	@Test(priority = 354, retryAnalyzer = RetryAnalyzer.class)
	public void verifyHomeNavigation() {
		assertSimpleSideMenuNavigation("TC_354", "https://web-splay.acceses.com/", "home");
	}

	@Test(priority = 355, retryAnalyzer = RetryAnalyzer.class)
	public void verifyGet80OffNavigation() {
		assertSimpleSideMenuNavigation("TC_355", "https://web-splay.acceses.com/payments/deactivate-plan", "get 80% off",
				"80% off", "subscriptions", "subscription");
	}

	@Test(priority = 356, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMostFavoriteNavigation() {
		assertSimpleSideMenuNavigation("TC_356", "https://web-splay.acceses.com/favorites", "favorites", "favorite",
				"most favorite", "most favourite", "favourite");
	}

	@Test(priority = 357, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTransactionHistoryNavigation() {
		assertSimpleSideMenuNavigation("TC_357", "https://web-splay.acceses.com/transactions", "transaction history",
				"transactions", "payment history", "order history");
	}

	@Test(priority = 358, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAboutUsNavigation() {
		assertSimpleSideMenuNavigation("TC_358", "https://web-splay.acceses.com/about_us", "about us", "about");
	}

	@Test(priority = 359, retryAnalyzer = RetryAnalyzer.class)
	public void verifyContactNavigation() {
		assertSimpleSideMenuNavigation("TC_359", "https://web-splay.acceses.com/contact_us", "contact us", "contact");
	}

	@Test(priority = 360, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDownloadAppsNavigation() {
		Assert.assertTrue(dashboard.openSimpleSideMenu(), "TC_360: expected side menu to open on hamburger click.");
		Assert.assertTrue(dashboard.waitForSimpleSideMenuVisibility(),
				"TC_360: expected side menu buttons to be visible after opening.");
		Assert.assertTrue(dashboard.isSimpleSideMenuButtonVisible("download apps", "download app", "download"),
				"TC_360: expected Download Apps button to be visible in the side menu.");
		dashboard.printSimpleSideMenuButtonNames();
	}

	private void loginAsConsumer() {
		String email = ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
		String password = ConfigReader.getProperty("consumer.password",
				ConfigReader.getProperty("login.validPassword"));

		if (isBlank(email) || isBlank(password)) {
			throw new SkipException(
					"Set consumer.email and consumer.password in config.properties to run side menu tests.");
		}

		login.openLogin();
		login.loginUser(email, password);
		login.clickNextAfterLogin();
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(), "Consumer dashboard should load after login.");
	}

	private void assertSimpleSideMenuNavigation(String caseId, String expectedUrl, String primaryLabel,
			String... alternateLabels) {
		Assert.assertTrue(dashboard.openSimpleSideMenu(), caseId + ": expected side menu to open on hamburger click.");
		Assert.assertTrue(dashboard.waitForSimpleSideMenuVisibility(),
				caseId + ": expected side menu buttons to be visible after opening.");
		String currentUrl = dashboard.clickSimpleSideMenuItemAndCaptureUrl(primaryLabel, alternateLabels);
		Assert.assertEquals(normalizeUrl(currentUrl), normalizeUrl(expectedUrl),
				caseId + ": expected navigation URL to match. Current URL: " + currentUrl + " | Expected URL: "
						+ expectedUrl);
	}

	private String normalizeUrl(String url) {
		if (url == null) {
			return "";
		}

		String normalized = url.trim();
		while (normalized.endsWith("/")) {
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		return normalized.toLowerCase();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
