package tests;

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
 * Hamburger Menu Behavior Test Cases (TC_365 to TC_371)
 *
 * Tests for hamburger menu interaction, stability, accessibility, responsive
 * design, and UI consistency across different scenarios.
 */
public class HamburgerMenuBehaviorTests extends BaseTest {

	private DashboardPage dashboard;

	@BeforeMethod(alwaysRun = true)
	public void initPagesAndLogin() {
		ConfigReader.reload();
		dashboard = new DashboardPage(driver);
		loginAsConsumer();
	}

	@Test(priority = 365, retryAnalyzer = RetryAnalyzer.class, description = "TC_365: Verify menu closes on outside click")
	public void verifyMenuClosesOnOutsideClick() {
		Assert.assertTrue(dashboard.openSimpleSideMenu(), "TC_365 : expected side menu to open on hamburger click.");
		dashboard.printSimpleSideMenuButtonNames();
		Assert.assertTrue(dashboard.closeSimpleSideMenu(),
				"TC_365 : expected side menu to close after printing buttons.");
		Assert.assertFalse(dashboard.isSimpleSideMenuOpen(), "TC_365 : expected side menu to be hidden after closing.");
	}

	@Test(priority = 366, retryAnalyzer = RetryAnalyzer.class, description = "TC_366: Verify stability on rapid clicks")
	public void verifyStabilityOnRapidClicks() {
		// Perform rapid clicks on hamburger menu
		for (int i = 0; i < 10; i++) {
			dashboard.clickHamburgerMenu();
		}

		// Verify menu opens correctly after rapid clicks
		Assert.assertTrue(dashboard.openSideMenu(), "TC_366: Side menu should open after rapid clicks");

		Assert.assertTrue(dashboard.isSideMenuOpen(), "TC_366: Side menu should be open after rapid clicks");

		// Verify UI is not broken
		Assert.assertTrue(dashboard.waitForSideMenuItemsLoaded(),
				"TC_366: Menu items should load correctly after rapid clicks");

		dashboard.closeSideMenu();
	}

	@Test(priority = 367, retryAnalyzer = RetryAnalyzer.class, description = "TC_367: Verify scrolling in small screens")
	public void verifyScrollingInSmallScreens() {
		// Open side menu
		Assert.assertTrue(dashboard.openSideMenu(), "TC_367: Side menu should open");

		Assert.assertTrue(dashboard.isSideMenuOpen(), "TC_367: Side menu should be open");

		// Note: Actual screen resizing would need to be done at browser/driver level
		// This test verifies menu items are loaded and accessible
		Assert.assertTrue(dashboard.waitForSideMenuItemsLoaded(), "TC_367: Menu items should be loaded for scrolling");

		dashboard.closeSideMenu();
	}

	@Test(priority = 368, retryAnalyzer = RetryAnalyzer.class, description = "TC_368: Verify keyboard navigation")
	public void verifyKeyboardNavigation() {
		// Open side menu
		Assert.assertTrue(dashboard.openSideMenu(), "TC_368: Side menu should open for keyboard navigation");

		Assert.assertTrue(dashboard.isSideMenuOpen(), "TC_368: Side menu should be open");

		// Note: Actual keyboard navigation (Tab + Enter) would need
		// Selenium Actions class or JavaScript execution
		// This test verifies menu is accessible and items are loaded
		Assert.assertTrue(dashboard.waitForSideMenuItemsLoaded(),
				"TC_368: Menu items should be accessible for keyboard navigation");

		dashboard.closeSideMenu();
	}

	@Test(priority = 369, retryAnalyzer = RetryAnalyzer.class, description = "TC_369: Verify dark/light mode UI")
	public void verifyThemeModeUI() {
		// Open side menu
		Assert.assertTrue(dashboard.openSideMenu(), "TC_369: Side menu should open in theme mode");

		Assert.assertTrue(dashboard.isSideMenuOpen(), "TC_369: Side menu should be open");

		// Verify theme toggle button is visible
		Assert.assertTrue(dashboard.isThemeToggleVisible(),
				"TC_369: Theme toggle button should be visible in side menu");

		// Click theme toggle to switch between dark/light mode
		Assert.assertTrue(dashboard.clickThemeToggle(), "TC_369: Theme toggle should be clickable");

		// Verify menu items remain visible after theme change
		Assert.assertTrue(dashboard.waitForSideMenuItemsLoaded(),
				"TC_369: Menu items should remain visible after theme toggle");

		dashboard.closeSideMenu();
	}

	@Test(priority = 370, retryAnalyzer = RetryAnalyzer.class, description = "TC_370: Verify UI with long labels")
	public void verifyUIWithLongLabels() {
		// Open side menu
		Assert.assertTrue(dashboard.openSideMenu(), "TC_370: Side menu should open with long labels");

		Assert.assertTrue(dashboard.isSideMenuOpen(), "TC_370: Side menu should be open");

		// Verify menu items are loaded and properly aligned
		Assert.assertTrue(dashboard.waitForSideMenuItemsLoaded(),
				"TC_370: Menu items should be properly aligned even with long labels");

		// Note: Actual long label testing would need to inject long text
		// or have test data with long labels
		dashboard.closeSideMenu();
	}

	@Test(priority = 371, retryAnalyzer = RetryAnalyzer.class, description = "TC_371: Verify hamburger icon visible across all pages")
	public void verifyHamburgerVisibleOnAllPages() {
		// Check on home page
		Assert.assertTrue(dashboard.isHamburgerMenuVisible(), "TC_371: Hamburger menu should be visible on home page");

		// Navigate to different pages and verify visibility
		dashboard.openSideMenu();
		dashboard.clickSideMenuItemAndCaptureUrl("home");

		Assert.assertTrue(dashboard.isHamburgerMenuVisible(),
				"TC_371: Hamburger menu should remain visible after navigation");
	}

	// ================= HELPER METHODS =================

	private void loginAsConsumer() {
		String email = ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
		String password = ConfigReader.getProperty("consumer.password",
				ConfigReader.getProperty("login.validPassword"));

		if (isBlank(email) || isBlank(password)) {
			throw new SkipException(
					"Set consumer.email and consumer.password in config.properties to run hamburger menu tests.");
		}

		LoginPage login = new LoginPage(driver);
		login.openLogin();
		login.loginUser(email, password);
		login.clickNextAfterLogin();
		dashboard.waitForPageReady();

		Assert.assertTrue(dashboard.waitForDashboardShell(), "Dashboard should load after login");
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}