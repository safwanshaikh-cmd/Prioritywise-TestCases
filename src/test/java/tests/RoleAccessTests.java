package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.ForCreatorPage;
import pages.LoginPage;
import utils.ConfigReader;

/**
 * Role-Based Access Control Test Cases (TC_361 to TC_364)
 *
 * Tests for verifying that Consumer and Uploader roles have appropriate
 * access control to Creator functionality.
 */
public class RoleAccessTests extends BaseTest {

    private DashboardPage dashboard;
    private LoginPage login;

    @BeforeMethod(alwaysRun = true)
    public void initPagesAndLogin() {
        ConfigReader.reload();
        dashboard = new DashboardPage(driver);
        login = new LoginPage(driver);
    }

    // ================= CONSUMER ROLE TESTS =================

    @Test(priority = 361, retryAnalyzer = RetryAnalyzer.class, description = "TC_361: Verify 'For Creators' NOT visible for Consumer")
    public void verifyCreatorMenuNotVisibleForConsumer() {
        loginAsConsumer();

        Assert.assertTrue(dashboard.openSideMenu(),
            "TC_361: Side menu should open successfully");

        Assert.assertFalse(dashboard.isForCreatorsMenuVisible(),
            "TC_361: 'For Creators' option should NOT be visible for Consumer role");
    }

    @Test(priority = 364, retryAnalyzer = RetryAnalyzer.class, description = "TC_364: Verify Consumer blocked from Creator page via direct URL")
    public void verifyConsumerBlockedFromCreatorUrl() {
        loginAsConsumer();

        String creatorUrl = buildCreatorUrl();
        driver.get(creatorUrl);

        // Verify either redirect to login/dashboard or access denied message
        String currentUrl = driver.getCurrentUrl().toLowerCase();

        boolean isBlocked = currentUrl.contains("login")
            || currentUrl.contains("dashboard")
            || currentUrl.contains("access")
            || currentUrl.contains("denied")
            || dashboard.isAccessDeniedMessageVisible();

        Assert.assertTrue(isBlocked,
            "TC_364: Consumer should be blocked from accessing Creator page directly. Current URL: " + currentUrl);
    }

    // ================= UPLOADER ROLE TESTS =================

    @Test(priority = 362, retryAnalyzer = RetryAnalyzer.class, description = "TC_362: Verify 'For Creators' visible for Uploader")
    public void verifyCreatorMenuVisibleForUploader() {
        loginAsUploader();

        Assert.assertTrue(dashboard.openSideMenu(),
            "TC_362: Side menu should open successfully");

        Assert.assertTrue(dashboard.isForCreatorsMenuVisible(),
            "TC_362: 'For Creators' option should be visible for Uploader role");
    }

    @Test(priority = 363, retryAnalyzer = RetryAnalyzer.class, description = "TC_363: Verify Uploader can navigate to Creator page")
    public void verifyUploaderCanAccessCreatorPage() {
        loginAsUploader();

        String startingUrl = driver.getCurrentUrl();
        dashboard.openSideMenu();

        String targetUrl = dashboard.clickForCreatorsMenuAndCaptureUrl();

        // Verify navigation to creator page
        boolean navigated = targetUrl != null
            && !targetUrl.equals(startingUrl)
            && (targetUrl.toLowerCase().contains("creator")
                || targetUrl.toLowerCase().contains("for-creator")
                || dashboard.isOnCreatorPage());

        Assert.assertTrue(navigated,
            "TC_363: Uploader should navigate to Creator dashboard. URL: " + targetUrl);

        // Verify creator dashboard is loaded
        ForCreatorPage creatorPage = new ForCreatorPage(driver);
        Assert.assertTrue(creatorPage.verifyBookListing(),
            "TC_363: Creator dashboard should load and display content");
    }

    // ================= HELPER METHODS =================

    private void loginAsConsumer() {
        String email = ConfigReader.getProperty("consumer.email",
            ConfigReader.getProperty("login.validEmail"));
        String password = ConfigReader.getProperty("consumer.password",
            ConfigReader.getProperty("login.validPassword"));

        if (isBlank(email) || isBlank(password)) {
            throw new SkipException(
                "Set consumer.email and consumer.password in config.properties to run Consumer role tests.");
        }

        login.openLogin();
        login.loginUser(email, password);
        login.clickNextAfterLogin();
        dashboard.waitForPageReady();

        Assert.assertTrue(dashboard.waitForDashboardShell(),
            "Consumer dashboard should load after login");
    }

    private void loginAsUploader() {
        String email = ConfigReader.getProperty("uploader.email");
        String password = ConfigReader.getProperty("uploader.password");

        if (isBlank(email) || isBlank(password)) {
            throw new SkipException(
                "Set uploader.email and uploader.password in config.properties to run Uploader role tests.");
        }

        login.openLogin();
        login.loginUser(email, password);
        login.clickNextAfterLogin();
        dashboard.waitForPageReady();

        Assert.assertTrue(dashboard.waitForDashboardShell(),
            "Uploader dashboard should load after login");
    }

    private String buildCreatorUrl() {
        String baseUrl = ConfigReader.getProperty("url");
        if (baseUrl == null || baseUrl.isBlank()) {
            return "";
        }

        // Remove trailing slash
        baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        // Try common creator page routes
        String[] candidatePaths = { "/for-creator", "/for-creators", "/creator", "/creators", "/creator-dashboard" };

        for (String path : candidatePaths) {
            return baseUrl + path;
        }

        return baseUrl + "/for-creator";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
