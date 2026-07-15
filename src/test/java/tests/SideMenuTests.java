package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.HeaderFooterPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Side menu automation tests.
 *
 * Test Coverage: TC_351 - TC_360
 * Focus: Hamburger toggle, side menu open/close, menu item visibility,
 * and navigation through the consumer side menu.
 */
public class SideMenuTests extends BaseTest {

	private DashboardPage dashboard;
	private HeaderFooterPage headerFooter;
	private LoginPage login;

	// Credentials for the dedicated Get 80% Off account (used only by TC_355).
	private static final String GET_80_OFF_EMAIL = "safwan.s11axis+0098@gmail.com";
	private static final String GET_80_OFF_PASSWORD = "Password@123";

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setup();
		ConfigReader.reload();
		dashboard = new DashboardPage(driver);
		headerFooter = new HeaderFooterPage(driver);
		login = new LoginPage(driver);
		headerFooter.loginAsConsumer();
		Assert.assertTrue(dashboard.waitForDashboardShell(),
				"Consumer dashboard should load after login before side menu tests.");
	}

	// ==================== TC_351: HAMBURGER CLICK OPENS MENU ====================

	/**
	 * TC_351: Hamburger click opens the side menu.
	 * Test Flow: Verify hamburger visible -> Click to open -> Confirm menu open.
	 * Expected: Side menu should be visible and remain open after the hamburger click.
	 */
	@Test(priority = 351, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_351: Verify hamburger click opens the side menu")
	public void TC351_VerifyHamburgerClickOpensMenu() {
		LoggerUtils.logTestStart("TC_351: Hamburger Click Opens Menu");

		try {
			LoggerUtils.logStep(1, "Verify the hamburger button is visible");
			boolean hamburgerVisible = dashboard.isHamburgerMenuVisible();
			LoggerUtils.logInfo("TC_351 - STEP 1: Hamburger visible: " + hamburgerVisible);
			Assert.assertTrue(hamburgerVisible,
					"TC_351: Hamburger button should be visible before opening the side menu");

			LoggerUtils.logStep(2, "Click the hamburger button to open the side menu");
			boolean menuOpened = dashboard.openSideMenu();
			LoggerUtils.logInfo("TC_351 - STEP 2: Side menu opened: " + menuOpened);
			Assert.assertTrue(menuOpened, "TC_351: Hamburger click should open the side menu");

			LoggerUtils.logStep(3, "Verify the side menu remains open after opening");
			boolean menuOpen = dashboard.isSideMenuOpen();
			LoggerUtils.logInfo("TC_351 - STEP 3: Side menu open after click: " + menuOpen);
			Assert.assertTrue(menuOpen, "TC_351: Side menu should remain visible after opening");

			LoggerUtils.logInfo("TC_351: Side menu opened successfully via hamburger click");

			LoggerUtils.logTestEnd("TC_351", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_351 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_352: HAMBURGER OPEN CLOSE TOGGLE ====================

	/**
	 * TC_352: Side menu opens and closes on hamburger toggle.
	 * Test Flow: Open the side menu -> Print items -> Close the side menu -> Confirm hidden.
	 * Expected: Side menu should close and become hidden after the toggle.
	 */
	@Test(priority = 352, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_352: Verify side menu opens and closes on hamburger toggle")
	public void TC352_VerifyHamburgerOpenCloseToggle() {
		LoggerUtils.logTestStart("TC_352: Hamburger Open Close Toggle");

		try {
			LoggerUtils.logStep(1, "Open the side menu using the hamburger button");
			boolean menuOpened = dashboard.openSimpleSideMenu();
			LoggerUtils.logInfo("TC_352 - STEP 1: Side menu opened: " + menuOpened);
			Assert.assertTrue(menuOpened, "TC_352: Side menu should open on hamburger click");

			LoggerUtils.logStep(2, "Print the visible side menu button names");
			dashboard.printSimpleSideMenuButtonNames();

			LoggerUtils.logStep(3, "Close the side menu and verify it is hidden");
			boolean menuClosed = dashboard.closeSimpleSideMenu();
			LoggerUtils.logInfo("TC_352 - STEP 3: Side menu closed: " + menuClosed);
			Assert.assertTrue(menuClosed, "TC_352: Side menu should close after printing buttons");
			Assert.assertFalse(dashboard.isSimpleSideMenuOpen(),
					"TC_352: Side menu should be hidden after closing");

			LoggerUtils.logInfo("TC_352: Side menu open and close toggle verified");

			LoggerUtils.logTestEnd("TC_352", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_352 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_353: ALL SIDE MENU ITEMS VISIBLE ====================

	/**
	 * TC_353: All side menu items are visible after opening.
	 * Test Flow: Open the side menu -> Wait for items -> Print item names.
	 * Expected: Side menu items should be visible after the menu opens.
	 */
	@Test(priority = 353, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_353: Verify all side menu items are visible")
	public void TC353_VerifyAllSideMenuItemsVisible() {
		LoggerUtils.logTestStart("TC_353: All Side Menu Items Visible");

		try {
			LoggerUtils.logStep(1, "Open the side menu using the hamburger button");
			boolean menuOpened = dashboard.openSimpleSideMenu();
			LoggerUtils.logInfo("TC_353 - STEP 1: Side menu opened: " + menuOpened);
			Assert.assertTrue(menuOpened, "TC_353: Side menu should open on hamburger click");

			LoggerUtils.logStep(2, "Verify the side menu buttons are visible after opening");
			boolean itemsVisible = dashboard.waitForSimpleSideMenuVisibility();
			LoggerUtils.logInfo("TC_353 - STEP 2: Side menu items visible: " + itemsVisible);
			Assert.assertTrue(itemsVisible, "TC_353: Side menu buttons should be visible after opening");

			LoggerUtils.logStep(3, "Print the visible side menu button names");
			dashboard.printSimpleSideMenuButtonNames();

			LoggerUtils.logInfo("TC_353: Side menu items visibility verified");

			LoggerUtils.logTestEnd("TC_353", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_353 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_354: HOME NAVIGATION ====================

	/**
	 * TC_354: Home menu item navigates to the dashboard URL.
	 * Test Flow: Open side menu -> Click Home -> Verify navigation URL.
	 * Expected: Clicking Home should navigate to the dashboard base URL.
	 */
	@Test(priority = 354, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_354: Verify Home menu item navigates to the dashboard URL")
	public void TC354_VerifyHomeNavigation() {
		LoggerUtils.logTestStart("TC_354: Home Navigation");

		try {
			verifySideMenuNavigation("TC_354", 1, "https://web-splay.acceses.com/", "home");
			LoggerUtils.logInfo("TC_354: Home navigation verified");

			LoggerUtils.logTestEnd("TC_354", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_354 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_355: GET 80% OFF NAVIGATION ====================

	/**
	 * TC_355: Get 80% Off menu item navigates to the payments URL.
	 * Test Flow: Switch to the dedicated Get 80% Off account -> Open side menu -> Click Get 80% Off -> Verify URL.
	 * Expected: Clicking Get 80% Off should navigate to the payments page.
	 */
	@Test(priority = 355, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_355: Verify Get 80% Off menu item navigates to the payments URL")
	public void TC355_VerifyGet80OffNavigation() {
		LoggerUtils.logTestStart("TC_355: Get 80% Off Navigation");

		try {
			// Get 80% Off is validated against a dedicated account. Log out the
			// generic consumer from setup and log in as the dedicated account so
			// the navigation reflects that account's subscription state.
			LoggerUtils.logStep(1, "Switch to the dedicated Get 80% Off account");
			headerFooter.clickLogout();
			login.openLogin();
			login.loginUser(GET_80_OFF_EMAIL, GET_80_OFF_PASSWORD);
			login.clickNextAfterLogin();
			dashboard.waitForPageReady();
			Assert.assertTrue(dashboard.waitForDashboardShell(),
					"TC_355: Get 80% Off account dashboard should load before opening the side menu");
			LoggerUtils.logInfo("TC_355 - STEP 1: Dedicated account session started");

			verifySideMenuNavigation("TC_355", 2, "https://web-splay.acceses.com/payments",
					"get 80% off", "80% off", "subscriptions", "subscription");
			LoggerUtils.logInfo("TC_355: Get 80% Off navigation verified");

			LoggerUtils.logTestEnd("TC_355", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_355 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_356: MOST FAVORITE NAVIGATION ====================

	/**
	 * TC_356: Most Favorite menu item navigates to the favorites URL.
	 * Test Flow: Open side menu -> Click Most Favorite -> Verify navigation URL.
	 * Expected: Clicking Most Favorite should navigate to the favorites page.
	 */
	@Test(priority = 356, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_356: Verify Most Favorite menu item navigates to the favorites URL")
	public void TC356_VerifyMostFavoriteNavigation() {
		LoggerUtils.logTestStart("TC_356: Most Favorite Navigation");

		try {
			verifySideMenuNavigation("TC_356", 1, "https://web-splay.acceses.com/favorites",
					"favorites", "favorite", "most favorite", "most favourite", "favourite");
			LoggerUtils.logInfo("TC_356: Most Favorite navigation verified");

			LoggerUtils.logTestEnd("TC_356", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_356 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_357: TRANSACTION HISTORY NAVIGATION ====================

	/**
	 * TC_357: Transaction History menu item navigates to the transactions URL.
	 * Test Flow: Open side menu -> Click Transaction History -> Verify navigation URL.
	 * Expected: Clicking Transaction History should navigate to the transactions page.
	 */
	@Test(priority = 357, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_357: Verify Transaction History menu item navigates to the transactions URL")
	public void TC357_VerifyTransactionHistoryNavigation() {
		LoggerUtils.logTestStart("TC_357: Transaction History Navigation");

		try {
			verifySideMenuNavigation("TC_357", 1, "https://web-splay.acceses.com/transactions",
					"transaction history", "transactions", "payment history", "order history");
			LoggerUtils.logInfo("TC_357: Transaction History navigation verified");

			LoggerUtils.logTestEnd("TC_357", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_357 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_358: ABOUT US NAVIGATION ====================

	/**
	 * TC_358: About Us menu item navigates to the about_us URL.
	 * Test Flow: Open side menu -> Click About Us -> Verify navigation URL.
	 * Expected: Clicking About Us should navigate to the about_us page.
	 */
	@Test(priority = 358, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_358: Verify About Us menu item navigates to the about_us URL")
	public void TC358_VerifyAboutUsNavigation() {
		LoggerUtils.logTestStart("TC_358: About Us Navigation");

		try {
			verifySideMenuNavigation("TC_358", 1, "https://web-splay.acceses.com/about_us",
					"about us", "about");
			LoggerUtils.logInfo("TC_358: About Us navigation verified");

			LoggerUtils.logTestEnd("TC_358", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_358 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_359: CONTACT NAVIGATION ====================

	/**
	 * TC_359: Contact menu item navigates to the contact_us URL.
	 * Test Flow: Open side menu -> Click Contact -> Verify navigation URL.
	 * Expected: Clicking Contact should navigate to the contact_us page.
	 */
	@Test(priority = 359, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_359: Verify Contact menu item navigates to the contact_us URL")
	public void TC359_VerifyContactNavigation() {
		LoggerUtils.logTestStart("TC_359: Contact Navigation");

		try {
			verifySideMenuNavigation("TC_359", 1, "https://web-splay.acceses.com/contact_us",
					"contact us", "contact");
			LoggerUtils.logInfo("TC_359: Contact navigation verified");

			LoggerUtils.logTestEnd("TC_359", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_359 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_360: DOWNLOAD APPS NAVIGATION ====================

	/**
	 * TC_360: Download Apps menu item is visible in the side menu.
	 * Test Flow: Open side menu -> Verify Download Apps button visible -> Print items.
	 * Expected: The Download Apps button should be visible in the open side menu.
	 */
	@Test(priority = 360, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_360: Verify Download Apps menu item is visible")
	public void TC360_VerifyDownloadAppsNavigation() {
		LoggerUtils.logTestStart("TC_360: Download Apps Navigation");

		try {
			LoggerUtils.logStep(1, "Open the side menu using the hamburger button");
			boolean menuOpened = dashboard.openSimpleSideMenu();
			LoggerUtils.logInfo("TC_360 - STEP 1: Side menu opened: " + menuOpened);
			Assert.assertTrue(menuOpened, "TC_360: Side menu should open on hamburger click");

			LoggerUtils.logStep(2, "Verify the Download Apps button is visible in the side menu");
			boolean downloadAppsVisible = dashboard
					.isSimpleSideMenuButtonVisible("download apps", "download app", "download");
			LoggerUtils.logInfo("TC_360 - STEP 2: Download Apps button visible: " + downloadAppsVisible);
			Assert.assertTrue(downloadAppsVisible,
					"TC_360: Download Apps button should be visible in the side menu");

			LoggerUtils.logStep(3, "Print the visible side menu button names");
			dashboard.printSimpleSideMenuButtonNames();

			LoggerUtils.logInfo("TC_360: Download Apps button visibility verified");

			LoggerUtils.logTestEnd("TC_360", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_360 - Test failed: " + dashboard.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * Open the side menu, click a menu item, and assert the navigation URL
	 * matches the expected destination (case-insensitive, null-safe).
	 *
	 * @param firstStep starting step number for the log output, so callers
	 *                 that run preliminary steps (e.g. the TC_355 account
	 *                 switch) keep their step numbers sequential.
	 */
	private void verifySideMenuNavigation(String testCaseId, int firstStep, String expectedUrl, String primaryLabel,
			String... alternateLabels) {
		LoggerUtils.logStep(firstStep, "Open the side menu using the hamburger button");
		boolean menuOpened = dashboard.openSimpleSideMenu();
		LoggerUtils.logInfo(testCaseId + " - STEP " + firstStep + ": Side menu opened: " + menuOpened);
		Assert.assertTrue(menuOpened, testCaseId + ": Side menu should open on hamburger click");

		LoggerUtils.logStep(firstStep + 1, "Click the menu item and capture the navigation URL");
		String currentUrl = dashboard.clickSimpleSideMenuItemAndCaptureUrl(primaryLabel, alternateLabels);
		LoggerUtils.logInfo(testCaseId + " - STEP " + (firstStep + 1) + ": Current URL after navigation: " + currentUrl);

		LoggerUtils.logStep(firstStep + 2, "Verify the navigation URL matches the expected destination");
		Assert.assertEquals(dashboard.safeLowerUrl(currentUrl), dashboard.safeLowerUrl(expectedUrl),
				testCaseId + ": Menu item should navigate to the expected URL. Current URL: " + currentUrl
						+ " | Expected URL: " + expectedUrl);
	}

	// ==================== LOCAL VALIDATION HELPERS — see verifySideMenuNavigation ====================
}
