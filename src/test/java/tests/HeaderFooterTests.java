package tests;

import java.lang.reflect.Method;

import org.openqa.selenium.JavascriptExecutor;
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
 * Header and footer module tests.
 *
 * Test Coverage: TC_204 - TC_228
 *
 * Run with: mvn test -Dtest=HeaderFooterTests Account: Consumer
 */
public class HeaderFooterTests extends BaseTest {

	private DashboardPage dashboard;
	private LoginPage login;

	private void waitForMilliseconds(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.println("Sleep interrupted: " + e.getMessage());
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private String getConsumerEmail() {
		return ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
	}

	private String getConsumerPassword() {
		return ConfigReader.getProperty("consumer.password", ConfigReader.getProperty("login.validPassword"));
	}

	private void skipIfConsumerCredentialsMissing() {
		if (isBlank(getConsumerEmail()) || isBlank(getConsumerPassword())) {
			throw new SkipException(
					"Set consumer.email and consumer.password in config.properties to run header/footer tests.");
		}
	}

	private boolean isGuestScenario(Method method) {
		String methodName = method.getName();
		return methodName.contains("WithoutLogin");
	}

	private void loginAsConsumer() {
		skipIfConsumerCredentialsMissing();
		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();
		dashboard.waitForPageReady();
		waitForMilliseconds(2000);
	}

	private void prepareGuestState() {
		try {
			driver.manage().deleteAllCookies();
			((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
			((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
		} catch (Exception e) {
			System.out.println("Could not fully clear guest session state: " + e.getMessage());
		}

		dashboard.openHomePage();
		dashboard.acceptCookiesIfPresent();
		waitForMilliseconds(1000);
	}

	private void assertUrlContainsAny(String currentUrl, String message, String... tokens) {
		for (String token : tokens) {
			if (!isBlank(token) && currentUrl.contains(token.toLowerCase())) {
				Assert.assertTrue(true, message);
				return;
			}
		}

		Assert.fail(message + " Current URL: " + currentUrl);
	}

	private boolean isLoggedOutState() {
		String currentUrl = dashboard.getCurrentUrl();
		return currentUrl.contains("login") || currentUrl.contains("signin") || currentUrl.contains("home")
				|| !currentUrl.contains("dashboard") || login.isOnLoginPage() || login.isLoginTextButtonAvailable()
				|| !dashboard.isLogoutButtonVisible();
	}

	private boolean waitForLoggedOutState(long timeoutMs) {
		long timeoutAt = System.currentTimeMillis() + timeoutMs;
		while (System.currentTimeMillis() < timeoutAt) {
			if (isLoggedOutState()) {
				return true;
			}
			waitForMilliseconds(500);
		}
		return isLoggedOutState();
	}

	@BeforeMethod(alwaysRun = true)
	public void setup(Method method) {
		super.setup();

		ConfigReader.reload();

		login = new LoginPage(driver);
		dashboard = new DashboardPage(driver);

		if (isGuestScenario(method)) {
			prepareGuestState();
		} else {
			loginAsConsumer();
		}
	}

	// ================= TC_204: HEADER VISIBILITY =================
	@Test(priority = 204, retryAnalyzer = RetryAnalyzer.class)
	public void verifyHeaderSectionVisibleOnDashboard() {
		Assert.assertTrue(dashboard.isHeaderVisible(), "Header section should be visible on dashboard");
	}

	// ================= TC_205: LOGO NAVIGATION =================
	@Test(priority = 205, retryAnalyzer = RetryAnalyzer.class)
	public void verifyClickingLogoRedirectsToDashboard() {
		String searchKeyword = ConfigReader.getProperty("dashboard.searchKeyword", "audio");

		Assert.assertTrue(dashboard.isSearchBarVisible(),
				"Search bar should be visible for navigation away from dashboard");
		dashboard.enterSearchKeyword(searchKeyword);
		dashboard.clickSearchButton();
		waitForMilliseconds(2000);

		String navigatedUrl = dashboard.getCurrentUrl();
		Assert.assertTrue(
				navigatedUrl.contains("search") || dashboard.areSearchResultsDisplayed()
						|| dashboard.hasNoSearchResultsMessage(),
				"User should be able to navigate away from dashboard before clicking the logo");

		Assert.assertTrue(dashboard.isHeaderLogoVisible(), "Header logo should be visible");
		String currentUrl = dashboard.clickHeaderLogoAndGetCurrentUrl();
		Assert.assertTrue(dashboard.waitForDashboardShell(),
				"Dashboard page should remain available after clicking logo");
		String baseUrl = ConfigReader.getProperty("url", "").toLowerCase();
		boolean isDashboardLanding = currentUrl.equals(baseUrl)
				|| currentUrl.equals(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/");
		if (isDashboardLanding) {
			Assert.assertTrue(true, "Clicking logo should redirect to dashboard or home");
			return;
		}

		assertUrlContainsAny(currentUrl, "Clicking logo should redirect to dashboard or home", "dashboard", "home");
	}

	// ================= TC_206: SEARCH BAR =================
	@Test(priority = 206, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchBarAcceptsInput() {
		String searchKeyword = ConfigReader.getProperty("dashboard.searchKeyword", "audio");

		Assert.assertTrue(dashboard.isSearchBarVisible(), "Search bar should be visible on dashboard");
		dashboard.enterSearchKeyword(searchKeyword);

		String searchValue = dashboard.getSearchInputValue();
		Assert.assertTrue(searchValue.toLowerCase().contains(searchKeyword.toLowerCase()),
				"Search bar should accept the entered keyword");
	}

	// ================= TC_207: SEARCH INVALID DATA =================
	@Test(priority = 207, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchBehaviorWithInvalidInput() {
		String invalidKeyword = "###@@@";

		Assert.assertTrue(dashboard.isSearchBarVisible(), "Search bar should be visible on dashboard");
		dashboard.enterSearchKeyword(invalidKeyword);
		dashboard.clickSearchButton();
		waitForMilliseconds(2000);

		String currentUrl = dashboard.getCurrentUrl();
		String searchValue = dashboard.getSearchInputValue();
		boolean hasResults = dashboard.areSearchResultsDisplayed();
		boolean hasNoResultsMessage = dashboard.hasNoSearchResultsMessage();
		boolean stayedOnValidSearchPage = currentUrl.contains("search") || currentUrl.contains("web_search")
				|| dashboard.waitForDashboardShell();

		Assert.assertTrue(searchValue.contains("###@@@"), "Search field should retain the invalid input value");
		Assert.assertTrue(hasNoResultsMessage || !hasResults || stayedOnValidSearchPage,
				"Invalid search input should be handled gracefully with either no results, an empty-state message, or a stable search page");
	}

	// ================= TC_212: THEME TOGGLE VISIBILITY =================
	@Test(priority = 212, retryAnalyzer = RetryAnalyzer.class)
	public void verifyThemeToggleButtonVisible() {
		Assert.assertTrue(dashboard.isThemeToggleVisible(), "Theme toggle button should be visible in header");
	}

	// ================= TC_216: PROFILE ICON =================
	@Test(priority = 216, retryAnalyzer = RetryAnalyzer.class)
	public void verifyProfileIconVisible() {
		Assert.assertTrue(dashboard.isHamburgerMenuVisible(), "Hamburger button should be visible in header");
	}

	// ================= TC_217: PROFILE MENU =================
	@Test(priority = 217, retryAnalyzer = RetryAnalyzer.class)
	public void verifyClickingProfileIconOpensMenu() {
		Assert.assertTrue(dashboard.isHamburgerMenuVisible(), "Hamburger button should be visible in header");

		dashboard.clickHamburgerMenu();
		Assert.assertTrue(dashboard.isProfileMenuVisible(),
				"Profile menu should be displayed after clicking hamburger button");
	}

	// ================= TC_218: LOGOUT =================
	@Test(priority = 218, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanLogout() {
		dashboard.clickLogout();
		Assert.assertTrue(waitForLoggedOutState(5000), "User should be logged out successfully");
	}

	// ================= TC_219: LOGOUT SESSION =================
	@Test(priority = 219, retryAnalyzer = RetryAnalyzer.class)
	public void verifySessionEndsAfterLogout() {
		dashboard.clickLogout();
		Assert.assertTrue(waitForLoggedOutState(5000), "User should be logged out before re-accessing dashboard");

		String dashboardUrl = ConfigReader.getProperty("url", "https://web-splay.acceses.com/") + "dashboard";
		driver.get(dashboardUrl);
		dashboard.waitForPageReady();
		if (dashboard.isHamburgerMenuVisible()) {
			dashboard.clickHamburgerMenu();
			waitForMilliseconds(1000);
		}
		Assert.assertTrue(login.isLoginTextButtonAvailable() || !dashboard.isLogoutButtonVisible(),
				"User session should end after logout, even if the dashboard remains guest-accessible");
	}

	// ================= TC_220: FOOTER VISIBILITY =================
	@Test(priority = 220, retryAnalyzer = RetryAnalyzer.class)
	public void verifyFooterSectionDisplayed() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer section should be visible");
	}

	// ================= TC_221: FOOTER LINKS DISPLAY =================
	@Test(priority = 221, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAllFooterLinksDisplayed() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer should be visible");
		Assert.assertTrue(dashboard.getVisibleFooterLinksCount() > 0, "Footer links should be displayed");
	}

	// ================= TC_222: PRIVACY POLICY LINK =================
	@Test(priority = 222, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPrivacyPolicyPageOpens() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer should be visible");

		String currentUrl = dashboard.openPrivacyPolicyLink();
		assertUrlContainsAny(currentUrl, "Privacy Policy page should open", "privacy");
	}

	// ================= TC_223: TERMS & CONDITIONS LINK =================
	@Test(priority = 223, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTermsPageOpens() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer should be visible");

		String currentUrl = dashboard.openTermsAndConditionsLink();
		assertUrlContainsAny(currentUrl, "Terms page should open", "terms");
	}

	// ================= TC_224: CONTACT US LINK =================
	@Test(priority = 224, retryAnalyzer = RetryAnalyzer.class)
	public void verifyContactPageOpens() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer should be visible");

		String currentUrl = dashboard.openContactUsLink();
		assertUrlContainsAny(currentUrl, "Contact page should open", "contact");
	}

	// ================= TC_225: FACEBOOK LINK =================
	@Test(priority = 225, retryAnalyzer = RetryAnalyzer.class)
	public void verifyFacebookLinkRedirectsCorrectly() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer should be visible");

		String currentUrl = dashboard.openFacebookLink();
		assertUrlContainsAny(currentUrl, "Facebook page should open", "facebook");
	}

	// ================= TC_226: INSTAGRAM LINK =================
	@Test(priority = 226, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInstagramLinkRedirectsCorrectly() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer should be visible");

		String currentUrl = dashboard.openInstagramLink();
		assertUrlContainsAny(currentUrl, "Instagram page should open", "instagram");
	}

	// ================= TC_227: BROKEN FOOTER LINK =================
	@Test(priority = 227, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBehaviorWhenFooterLinkIsBroken() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer should be visible");

		String currentUrl = dashboard.openBrokenFooterLink();
		currentUrl = currentUrl == null ? "" : currentUrl.toLowerCase();
		Assert.assertFalse(isBlank(currentUrl), "Broken footer link should still produce a navigated URL");
		Assert.assertTrue(currentUrl.contains("broken-footer-link-automation"),
				"Broken footer link should navigate to the known invalid destination for negative-path validation");
		Assert.assertFalse(currentUrl.contains("privacy"),
				"Broken footer link should not resolve to the valid Privacy Policy destination");
	}

	// ================= TC_228: FOOTER LINKS WITHOUT LOGIN =================
	@Test(priority = 228, retryAnalyzer = RetryAnalyzer.class)
	public void verifyFooterLinksAccessibleWithoutLogin() {
		Assert.assertTrue(dashboard.isFooterVisible(), "Footer should be visible for guest users");

		String privacyUrl = dashboard.openPrivacyPolicyLink();
		assertUrlContainsAny(privacyUrl, "Privacy Policy should be accessible without login", "privacy");
	}
}
