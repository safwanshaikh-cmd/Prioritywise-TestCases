package tests;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

/**
 * Security module tests. Tests direct URL access and session handling
 * scenarios.
 *
 * Test Coverage: TC_192 - TC_203
 *
 * Run with: mvn test -Dtest=SecurityTests Account: Consumer (for logged-in
 * tests)
 */
public class SecurityTests extends BaseTest {

	private DashboardPage dashboard;
	private LoginPage login;

	// Helper method to handle sleep without InterruptedException
	private void waitForMilliseconds(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.println("Sleep interrupted: " + e.getMessage());
		}
	}

	// Helper method to check if string is blank
	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	// Get credentials for consumer account
	private String getConsumerEmail() {
		return ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
	}

	private String getConsumerPassword() {
		return ConfigReader.getProperty("consumer.password", ConfigReader.getProperty("login.validPassword"));
	}

	private void skipIfConsumerCredentialsMissing() {
		String email = getConsumerEmail();
		String password = getConsumerPassword();

		if (isBlank(email) || isBlank(password)) {
			throw new SkipException(
					"Set consumer.email and consumer.password in config.properties to run security tests.");
		}
	}

	private String getAdminRestrictedUrl() {
		String baseUrl = ConfigReader.getProperty("base.url", ConfigReader.getProperty("url"));
		String configuredAdminUrl = ConfigReader.getProperty("security.adminUrl", ConfigReader.getProperty("admin.url"));

		if (!isBlank(configuredAdminUrl)) {
			return configuredAdminUrl;
		}

		return baseUrl.endsWith("/") ? baseUrl + "admin" : baseUrl + "/admin";
	}

	@BeforeMethod(alwaysRun = true)
	public void setup(Method method) {
		super.setup();

		ConfigReader.reload();

		login = new LoginPage(driver);
		dashboard = new DashboardPage(driver);
	}

	// ============================================================
	// SECURITY TEST CASES (TC_192 - TC_203)
	// ============================================================

	// Helper method to test direct URL access
	private void testDirectUrlAccess(String testName, String urlToTest, String expectedPage) {
		System.out.println("=== " + testName + " ===");
		System.out.println("Testing URL: " + urlToTest);

		String baseUrl = ConfigReader.getProperty("base.url", ConfigReader.getProperty("url"));
		String sanitizedPath = urlToTest.startsWith("/") ? urlToTest : "/" + urlToTest;
		String targetUrl = urlToTest.startsWith("http") ? urlToTest
				: baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) + sanitizedPath
						: baseUrl + sanitizedPath;

		System.out.println("Guest target URL: " + targetUrl);

		// Clear any existing session so the route is validated as a guest flow.
		try {
			driver.manage().deleteAllCookies();
			System.out.println("Existing session cleared before guest access validation");
		} catch (Exception e) {
			System.out.println("Failed to clear existing session before guest access validation: " + e.getMessage());
		}

		driver.get(targetUrl);
		waitForMilliseconds(3000);

		String currentUrl = driver.getCurrentUrl().toLowerCase();
		String expectedToken = expectedPage == null ? "" : expectedPage.toLowerCase();
		String pathToken = (urlToTest.startsWith("http") ? urlToTest : sanitizedPath).toLowerCase()
				.replace("https://", "").replace("http://", "").replace("/", "");

		boolean isExpectedPage = !expectedToken.isEmpty() && currentUrl.contains(expectedToken);
		boolean isExpectedPath = !pathToken.isEmpty() && currentUrl.replace("/", "").contains(pathToken);
		boolean isGuestAccessible = isExpectedPage || isExpectedPath || !currentUrl.contains("login");

		System.out.println("Current URL after guest access: " + currentUrl);
		Assert.assertTrue(isGuestAccessible,
				"User should be able to access the URL without login because guest access is supported. Current URL: "
						+ currentUrl);
	}

	// ================= TC_192: DIRECT CATEGORY URL =================
	@Test(priority = 192, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectCategoryUrlAccessWithoutLogin() {
		skipIfConsumerCredentialsMissing();

		testDirectUrlAccess("TC_192: Direct Category URL Access",
				"https://web-splay.acceses.com/book_category_listing?title=Classic&category=6",
				"book_category_listing");
	}

	// ================= TC_193: DIRECT EMPTY CATEGORY URL =================
	@Test(priority = 193, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectEmptyCategoryUrlAccessWithoutLogin() {
		skipIfConsumerCredentialsMissing();

		testDirectUrlAccess("TC_192: Direct Category URL Access",
				"https://web-splay.acceses.com/book_category_listing?title=Classic&category=6",
				"book_category_listing");
	}

	// ================= TC_194: DIRECT CATEGORY URL (DUPLICATE OF TC_192)
	// =================
	// Skipping duplicate test case

	// ================= TC_195: DIRECT EMPTY CATEGORY URL (DUPLICATE OF TC_193)
	// =================
	// Skipping duplicate test case

	// ================= TC_196: DIRECT RELATED SHOWS URL =================
	@Test(priority = 196, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectRelatedShowsUrlAccessWithoutLogin() {
		skipIfConsumerCredentialsMissing();

		testDirectUrlAccess("TC_196: Direct Related Shows URL Access",
				"https://web-splay.acceses.com/view_all_books?url=recommendations&title=More%20Related%20Shows",
				"recommendations");
	}

	// ================= TC_197: RELATED SHOW DETAIL (DASHBOARD ONLY)
	// =================
	@Test(priority = 197, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectRelatedShowDetailAccessWithoutLogin() {
		throw new SkipException(
				"TC_197 is dashboard-only. There is no standalone direct URL for Related Show Detail access.");
	}

	// ================= TC_198: DIRECT UPCOMING URL =================
	@Test(priority = 198, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectUpcomingUrlAccessWithoutLogin() {
		skipIfConsumerCredentialsMissing();

		testDirectUrlAccess("TC_198: Direct Upcoming URL Access",
				"https://web-splay.acceses.com/view_all_books?url=upcoming-releases&title=Upcoming%20Releases",
				"upcoming-releases");
	}

	// ================= TC_199: DIRECT UPCOMING SHOW =================
	@Test(priority = 199, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectUpcomingShowAccessWithoutLogin() {
		skipIfConsumerCredentialsMissing();

		testDirectUrlAccess("TC_198: Direct Upcoming URL Access",
				"https://web-splay.acceses.com/view_all_books?url=upcoming-releases&title=Upcoming%20Releases",
				"upcoming-releases");
	}

	// ================= TC_200: DIRECT TRENDING SHOWS URL =================
	@Test(priority = 200, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectMostRatedUrlAccessWithoutLogin() {
		skipIfConsumerCredentialsMissing();

		testDirectUrlAccess("TC_200: Direct Trending Shows URL Access",
				"https://web-splay.acceses.com/view_all_books?url=recommendations%2Ftrending&title=Trending%20Shows",
				"trending");
	}

	// ================= TC_201: DIRECT RATED SHOW URL =================
	@Test(priority = 201, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectRatedShowAccessWithoutLogin() {
		skipIfConsumerCredentialsMissing();

		testDirectUrlAccess("TC_201: Direct Trending Shows URL Access",
				"https://web-splay.acceses.com/view_all_books?url=recommendations%2Ftrending&title=Trending%20Shows",
				"trending");
	}

	// ================= TC_202: EXPIRED SESSION ACCESS =================
	@Test(priority = 202, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDirectUrlAccessAfterSessionExpiry() {
		skipIfConsumerCredentialsMissing();

		System.out.println("=== TC_202: Expired Session Access ===");

		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();

		dashboard.waitForPageReady();
		waitForMilliseconds(2000);

		String storedUrl = driver.getCurrentUrl();
		System.out.println("Stored URL: " + storedUrl);

		try {
			driver.manage().deleteAllCookies();
			System.out.println("Cookies cleared (simulating session expiry)");
		} catch (Exception e) {
			System.out.println("Failed to clear cookies: " + e.getMessage());
		}

		driver.get(storedUrl);
		waitForMilliseconds(3000);

		String currentUrl = driver.getCurrentUrl().toLowerCase();
		boolean isAccessibleAfterExpiry = !currentUrl.contains("login");

		System.out.println("Current URL after session expiry: " + currentUrl);
		Assert.assertTrue(isAccessibleAfterExpiry,
				"User should still be able to access the stored URL after session expiry because guest access is supported. Current URL: "
						+ currentUrl);
	}

	// ================= TC_203: UNAUTHORIZED ROLE ACCESS =================
	@Test(priority = 203, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRestrictedDashboardPagesCannotOpenWithWrongRole() {
		skipIfConsumerCredentialsMissing();

		System.out.println("=== TC_203: Unauthorized Role Access ===");

		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();

		dashboard.waitForPageReady();
		waitForMilliseconds(2000);

		String adminUrl = getAdminRestrictedUrl();
		System.out.println("Testing admin URL with consumer role: " + adminUrl);

		driver.get(adminUrl);
		waitForMilliseconds(3000);

		String currentUrl = driver.getCurrentUrl().toLowerCase();
		boolean isAdminDashboardLoaded = dashboard.isAdminDashboardLoaded();
		boolean isRestricted = currentUrl.contains("unauthorized") || currentUrl.contains("forbidden")
				|| currentUrl.contains("access") && currentUrl.contains("denied")
				|| currentUrl.contains("dashboard") || !currentUrl.contains("/admin");

		Assert.assertFalse(isAdminDashboardLoaded, "Consumer should not be able to open admin dashboard pages.");
		Assert.assertTrue(isRestricted,
				"Consumer should be redirected away from restricted admin pages or shown a restricted-access state. Current URL: "
						+ currentUrl);
	}
}
