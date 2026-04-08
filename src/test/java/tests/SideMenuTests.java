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
		Assert.assertTrue(dashboard.openSideMenu(), "TC_352: expected side menu to open on first hamburger click.");
		Assert.assertTrue(dashboard.closeSideMenu(), "TC_352: expected side menu to close on second hamburger click.");
		Assert.assertFalse(dashboard.isSideMenuOpen(), "TC_352: expected side menu to be hidden after closing.");
	}

	@Test(priority = 353, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAllSideMenuItemsVisible() {
		Assert.assertTrue(dashboard.openSideMenu(), "TC_353: expected side menu to open before visibility checks.");

		List<String> missing = dashboard.getMissingPrimarySideMenuItems();

		Assert.assertTrue(missing.isEmpty(), "TC_353 FAILED: Missing items: " + missing);
	}

	@Test(priority = 354, retryAnalyzer = RetryAnalyzer.class)
	public void verifyHomeNavigation() {
		Assert.assertTrue(dashboard.openSideMenu(), "TC_354: expected side menu to open before clicking Home.");
		String currentUrl = dashboard.clickSideMenuItemAndCaptureUrl("home");
		assertLiveUrl(caseId("354"), currentUrl, "", "/", "/dashboard", "/home");
		Assert.assertTrue(dashboard.waitForDashboardShell() || dashboard.matchesCurrentPage("dashboard", "home"),
				"TC_354: expected Home menu item to navigate to the dashboard/home view.");
	}

	@Test(priority = 355, retryAnalyzer = RetryAnalyzer.class)
	public void verifyGet80OffNavigation() {
		assertSideMenuNavigation("TC_355", "get 80% off", new String[] { "80% off" },
				new String[] { "/offer", "/offers", "/discount", "/subscription", "/subscriptions", "/plan",
						"/pricing" },
				"80% off", "offer", "discount", "subscription", "plan", "pricing");
	}

	@Test(priority = 356, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMostFavoriteNavigation() {
		assertSideMenuNavigation("TC_356", "most favorite", new String[] { "most favourite", "favorite", "favourite" },
				new String[] { "/favorite", "/favorites", "/favourite", "/favourites", "/wishlist" }, "favorite",
				"favourite");
	}

	@Test(priority = 357, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTransactionHistoryNavigation() {
		assertSideMenuNavigation("TC_357", "transaction history",
				new String[] { "transactions history", "transactions", "payment history", "order history" },
				new String[] { "/transaction-history", "/transactions", "/transaction", "/payment-history",
						"/order-history", "/orders" },
				"transaction", "history");
	}

	@Test(priority = 358, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAboutUsNavigation() {
		assertSideMenuNavigation("TC_358", "about us", new String[] { "about" },
				new String[] { "/about-us", "/about" }, "about");
	}

	@Test(priority = 359, retryAnalyzer = RetryAnalyzer.class)
	public void verifyContactNavigation() {
		assertSideMenuNavigation("TC_359", "contact", new String[] { "contact us" },
				new String[] { "/contact-us", "/contact" }, "contact");
	}

	@Test(priority = 360, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDownloadAppsNavigation() {
		assertSideMenuNavigation("TC_360", "download apps", new String[] { "download app", "download" },
				new String[] { "/download-apps", "/download-app", "/download", "/apps", "/app" }, "download", "app",
				"play store", "app store");
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

	private void assertSideMenuNavigation(String caseId, String primaryLabel, String[] alternateLabels,
			String[] expectedUrlPaths, String... expectedTokens) {
		Assert.assertTrue(dashboard.openSideMenu(), caseId + ": expected side menu to open before navigation.");
		String currentUrl = dashboard.clickSideMenuItemAndCaptureUrl(primaryLabel, alternateLabels);
		assertLiveUrl(caseId, currentUrl, expectedUrlPaths);
		Assert.assertTrue(dashboard.matchesCurrentPage(expectedTokens), caseId
				+ ": expected side menu navigation to reach the correct destination. Current URL: " + currentUrl);
	}

	private void assertLiveUrl(String caseId, String currentUrl, String... expectedPaths) {
		Assert.assertTrue(matchesAnyLiveUrl(currentUrl, expectedPaths), caseId
				+ ": expected live URL to match one of the configured routes. Current URL: " + currentUrl
				+ " | Expected routes: " + String.join(", ", buildExpectedUrls(expectedPaths)));
	}

	private boolean matchesAnyLiveUrl(String currentUrl, String... expectedPaths) {
		if (currentUrl == null || currentUrl.isBlank()) {
			return false;
		}

		String normalizedCurrentUrl = normalizeUrl(currentUrl);
		for (String expectedUrl : buildExpectedUrls(expectedPaths)) {
			if (normalizedCurrentUrl.equals(normalizeUrl(expectedUrl))) {
				return true;
			}
		}
		return false;
	}

	private String[] buildExpectedUrls(String... expectedPaths) {
		String baseUrl = normalizeBaseUrl(ConfigReader.getProperty("url"));
		if (expectedPaths == null || expectedPaths.length == 0) {
			return new String[] { baseUrl };
		}

		String[] expectedUrls = new String[expectedPaths.length];
		for (int i = 0; i < expectedPaths.length; i++) {
			expectedUrls[i] = buildExpectedUrl(baseUrl, expectedPaths[i]);
		}
		return expectedUrls;
	}

	private String buildExpectedUrl(String baseUrl, String path) {
		if (path == null || path.isBlank() || "/".equals(path)) {
			return baseUrl;
		}

		String normalizedPath = path.startsWith("/") ? path : "/" + path;
		return baseUrl + normalizedPath;
	}

	private String normalizeBaseUrl(String url) {
		if (url == null || url.isBlank()) {
			return "";
		}

		String normalized = url.trim();
		while (normalized.endsWith("/")) {
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		return normalized;
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

	private String caseId(String priority) {
		return "TC_" + priority;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
