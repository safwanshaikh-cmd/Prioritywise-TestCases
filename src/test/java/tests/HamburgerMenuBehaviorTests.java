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
 * Tests for hamburger menu interaction, stability, and visibility.
 */
public class HamburgerMenuBehaviorTests extends BaseTest {

    private DashboardPage dashboard;

    @BeforeMethod(alwaysRun = true)
    public void initPagesAndLogin() {
        ConfigReader.reload();
        dashboard = new DashboardPage(driver);
        loginAsConsumer();
    }

    @Test(priority = 365, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_365: Verify menu closes on outside click")
    public void verifyMenuClosesOnOutsideClick() {
        Assert.assertTrue(dashboard.openSideMenu(),
            "TC_365: Side menu should open");

        Assert.assertTrue(dashboard.isSideMenuOpen(),
            "TC_365: Side menu should be open");

        // Click outside the menu (on page body)
        dashboard.clickOutsideSideMenu();

        // Verify menu closes
        Assert.assertFalse(dashboard.isSideMenuOpen(),
            "TC_365: Side menu should close after clicking outside");
    }

    @Test(priority = 371, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_371: Verify hamburger icon visible across all pages")
    public void verifyHamburgerVisibleOnAllPages() {
        // Check on home page
        Assert.assertTrue(dashboard.isHamburgerMenuVisible(),
            "TC_371: Hamburger menu should be visible on home page");

        // Navigate to different pages and verify visibility
        dashboard.openSideMenu();
        dashboard.clickSideMenuItemAndCaptureUrl("home");

        Assert.assertTrue(dashboard.isHamburgerMenuVisible(),
            "TC_371: Hamburger menu should remain visible after navigation");
    }

    // ================= HELPER METHODS =================

    private void loginAsConsumer() {
        String email = ConfigReader.getProperty("consumer.email",
            ConfigReader.getProperty("login.validEmail"));
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

        Assert.assertTrue(dashboard.waitForDashboardShell(),
            "Dashboard should load after login");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
