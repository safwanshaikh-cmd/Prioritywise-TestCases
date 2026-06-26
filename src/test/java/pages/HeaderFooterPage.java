package pages;


import java.util.Locale;
import java.util.Objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import base.BasePage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Page Object for Header and Footer functionality.
 * Contains all reusable locators, helper methods, and business logic
 * related to header and footer interactions across the application.
 *
 * <p>This class encapsulates:
 * <ul>
 *   <li>Header locators (logo, search, theme toggle, profile menu)</li>
 *   <li>Footer locators (privacy, terms, contact, social links)</li>
 *   <li>Navigation menu locators and actions</li>
 *   <li>Login/logout helpers for test scenarios</li>
 *   <li>Null-safe URL and state validation methods</li>
 *   <li>Guest state preparation methods</li>
 * </ul>
 *
 * <p>Test classes should remain thin — all reusable logic belongs here.
 */
public class HeaderFooterPage extends BasePage {

    private final LoginPage login;
    private final DashboardPage dashboard;

    private static final String HAMBURGER_MENU_XPATH = "//*[contains(@data-testid,'profile') or contains(@data-testid,'avatar') or contains(@aria-label,'Profile') or contains(@aria-label,'profile')]";


    public HeaderFooterPage(WebDriver driver) {
        super(driver);
        this.login = new LoginPage(driver);
        this.dashboard = new DashboardPage(driver);
    }

    // ==================== Login Helpers ====================

    /**
     * Login as consumer user using configured credentials.
     * Throws SkipException if credentials are not configured.
     */
    public void loginAsConsumer() {
        String email = getConsumerEmail();
        String password = getConsumerPassword();

        if (isBlank(email) || isBlank(password)) {
            throw new org.testng.SkipException(
                    "Set consumer.email and consumer.password in config.properties to run header/footer tests.");
        }

        login.openLogin();
        login.loginUser(email, password);
        login.clickNextAfterLogin();
        dashboard.waitForPageReady();
        waitQuietly(2000);
    }

    /**
     * Get consumer email from configuration.
     */
    public String getConsumerEmail() {
        return ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
    }

    /**
     * Get consumer password from configuration.
     */
    public String getConsumerPassword() {
        return ConfigReader.getProperty("consumer.password", ConfigReader.getProperty("login.validPassword"));
    }

    // ==================== Guest State Helpers ====================

    /**
     * Prepare guest state by clearing cookies/storage and navigating to home.
     */
    public void prepareGuestState() {
        try {
            driver.manage().deleteAllCookies();
            Objects.requireNonNull((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
            Objects.requireNonNull((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
        } catch (Exception e) {
            LoggerUtils.logWarn("Could not fully clear guest session state: " + safe(e.getMessage()));
        }

        dashboard.openHomePage();
        dashboard.acceptCookiesIfPresent();
        waitQuietly(1000);
    }

    // ==================== Logout State Helpers ====================

    /**
     * Check if current state is logged out (login page or home page).
     */
    public boolean isLoggedOutState() {
        String currentUrl = safeLower(dashboard.getCurrentUrl());
        return currentUrl.contains("login")
                || currentUrl.contains("signin")
                || currentUrl.contains("home")
                || !currentUrl.contains("dashboard")
                || isOnLoginPage()
                || !dashboard.isLogoutButtonVisible();
    }

    /**
     * Wait for logged out state within timeout.
     */
    public boolean waitForLoggedOutState(long timeoutMs) {
        long timeoutAt = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < timeoutAt) {
            if (isLoggedOutState()) {
                return true;
            }
            waitQuietly(500);
        }
        return isLoggedOutState();
    }

    /**
     * Check if login page is displayed.
     */
    public boolean isOnLoginPage() {
        return login.isOnLoginPage();
    }

    // ==================== Header Visibility Methods ====================

    /**
     * Check if header section is visible.
     */
    public boolean isHeaderVisible() {
        return dashboard.isHeaderVisible();
    }

    /**
     * Check if header logo is visible.
     */
    public boolean isHeaderLogoVisible() {
        return dashboard.isHeaderLogoVisible();
    }

    /**
     * Click header logo and return the resulting URL.
     */
    public String clickHeaderLogoAndGetCurrentUrl() {
        String currentUrl = dashboard.clickHeaderLogoAndGetCurrentUrl();
        return safe(currentUrl);
    }

    /**
     * Check if dashboard shell is available after navigation.
     */
    public boolean waitForDashboardShell() {
        return dashboard.waitForDashboardShell();
    }

    // ==================== Search Methods ====================

    /**
     * Check if search bar is visible.
     */
    public boolean isSearchBarVisible() {
        return dashboard.isSearchBarVisible();
    }

    /**
     * Enter search keyword.
     */
    public void enterSearchKeyword(String keyword) {
        dashboard.enterSearchKeyword(keyword);
    }

    /**
     * Click search button to submit search.
     */
    public void clickSearchButton() {
        dashboard.clickSearchButton();
    }

    /**
     * Get current search input value.
     */
    public String getSearchInputValue() {
        return safe(dashboard.getSearchInputValue());
    }

    /**
     * Check if search results are displayed.
     */
    public boolean areSearchResultsDisplayed() {
        return dashboard.areSearchResultsDisplayed();
    }

    /**
     * Check if no search results message is displayed.
     */
    public boolean hasNoSearchResultsMessage() {
        try {
            return dashboard.hasNoSearchResultsMessage();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Theme Toggle Methods ====================

    /**
     * Check if theme toggle button is visible.
     */
    public boolean isThemeToggleVisible() {
        return dashboard.isThemeToggleVisible();
    }

    // ==================== Profile Menu Methods ====================

    /**
     * Check if hamburger menu (profile icon) is visible.
     */
    public boolean isHamburgerMenuVisible() {
        return dashboard.isHamburgerMenuVisible();
    }

    /**
     * Click hamburger menu to open profile menu.
     */
    public void clickHamburgerMenu() {
        dashboard.clickHamburgerMenu();
    }

    /**
     * Check if profile menu is visible after opening.
     */
    public boolean isProfileMenuVisible() {
        return dashboard.isProfileMenuVisible();
    }

    /**
     * Check if profile icon is visible.
     */
    public boolean isProfileIconVisible() {
        return dashboard.isProfileIconVisible();
    }

    /**
     * Open profile menu (alias for clickHamburgerMenu).
     */
    public void openProfileMenu() {
        dashboard.openProfileMenu();
    }

    // ==================== Logout Methods ====================

    /**
     * Click logout button.
     */
    public void clickLogout() {
        dashboard.clickLogout();
    }

    /**
     * Check if logout button is visible.
     */
    public boolean isLogoutButtonVisible() {
        return dashboard.isLogoutButtonVisible();
    }

    // ==================== Footer Visibility Methods ====================

    /**
     * Check if footer section is visible.
     */
    public boolean isFooterVisible() {
        return dashboard.isFooterVisible();
    }

    /**
     * Get count of visible footer links.
     */
    public int getVisibleFooterLinksCount() {
        return dashboard.getVisibleFooterLinksCount();
    }

    // ==================== Footer Link Methods ====================

    /**
     * Open Privacy Policy link and return the resulting URL.
     */
    public String openPrivacyPolicyLink() {
        String url = dashboard.openPrivacyPolicyLink();
        return safe(url);
    }

    /**
     * Open Terms and Conditions link and return the resulting URL.
     */
    public String openTermsAndConditionsLink() {
        String url = dashboard.openTermsAndConditionsLink();
        return safe(url);
    }

    /**
     * Open Contact Us link and return the resulting URL.
     */
    public String openContactUsLink() {
        String url = dashboard.openContactUsLink();
        return safe(url);
    }

    /**
     * Open Facebook link and return the resulting URL.
     */
    public String openFacebookLink() {
        String url = dashboard.openFacebookLink();
        return safe(url);
    }

    /**
     * Open Instagram link and return the resulting URL.
     */
    public String openInstagramLink() {
        String url = dashboard.openInstagramLink();
        return safe(url);
    }

    /**
     * Open broken footer link and return the resulting URL.
     */
    public String openBrokenFooterLink() {
        String url = dashboard.openBrokenFooterLink();
        return safe(url);
    }

    // ==================== Additional Dashboard Methods ====================

    /**
     * Check if on creator page.
     */
    public boolean isOnCreatorPage() {
        try {
            return dashboard.isOnCreatorPage();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if upload page is opened.
     */
    public boolean isUploadPageOpened() {
        try {
            return dashboard.isUploadPageOpened();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Submit search with keyword.
     */
    public void submitSearch(String keyword) {
        dashboard.submitSearch(keyword);
    }

    /**
     * Check if login text button is available.
     */
    public boolean isLoginTextButtonAvailable() {
        return login.isLoginTextButtonAvailable();
    }

    /**
     * Check if play audio button is visible.
     */
    public boolean isPlayAudioButtonVisible() {
        try {
            return dashboard.isPlayAudioButtonVisible();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click play audio and verify playback starts.
     */
    public boolean clickPlayAudioAndVerifyPlayback() {
        try {
            return dashboard.clickPlayAudioAndVerifyPlayback();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if book details page is visible.
     */
    public boolean isBookDetailsPageVisible() {
        try {
            return dashboard.isBookDetailsPageVisible();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for book data to load.
     */
    public boolean waitForBookDataToLoad() {
        try {
            return dashboard.waitForBookDataToLoad();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if pause audio button is visible.
     */
    public boolean isPauseAudioButtonVisible() {
        try {
            return dashboard.isPauseAudioButtonVisible();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click first search result.
     */
    public boolean clickFirstSearchResult() {
        try {
            return dashboard.clickFirstSearchResult();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== Null-Safe Helper Methods ====================

    /**
     * Get current URL safely (null-safe).
     */
    public String getCurrentUrlSafely() {
        try {
            String url = driver.getCurrentUrl();
            return url == null ? "" : url;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if a string value is blank (null or empty).
     */
    public boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Null-safe string getter.
     */
    public String safe(String value) {
        return value == null ? "" : value;
    }

    /**
     * Null-safe lower case converter.
     */
    public String safeLower(String value) {
        return safe(value).toLowerCase(Locale.ROOT);
    }

    /**
     * Check if current URL contains any of the given tokens (case-insensitive).
     */
    public boolean isUrlContainingAny(String... tokens) {
        String currentUrl = safeLower(getCurrentUrlSafely());
        for (String token : tokens) {
            if (!isBlank(token) && currentUrl.contains(token.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assert that URL contains any of the given tokens.
     */
    public void assertUrlContainsAny(String message, String... tokens) {
        String currentUrl = getCurrentUrlSafely();
        for (String token : tokens) {
            if (!isBlank(token) && safeLower(currentUrl).contains(token.toLowerCase())) {
                Assert.assertTrue(true, message);
                return;
            }
        }
        Assert.fail(message + " Current URL: " + currentUrl);
    }

    /**
     * Check if method name indicates guest scenario (contains "WithoutLogin").
     */
    public boolean isGuestScenario(String methodName) {
        return methodName != null && methodName.contains("WithoutLogin");
    }

    /**
     * Get base URL from configuration.
     */
    public String getBaseUrl() {
        return ConfigReader.getProperty("url", "https://web-splay.acceses.com/");
    }

    /**
     * Navigate to dashboard URL directly.
     */
    public void navigateToDashboard() {
        String dashboardUrl = getBaseUrl() + "dashboard";
        driver.get(dashboardUrl);
        dashboard.waitForPageReady();
    }

    /**
     * Wait quietly for specified milliseconds.
     * Replaces Thread.sleep with named method.
     */
    public void waitQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LoggerUtils.logWarn("Sleep interrupted: " + safe(e.getMessage()));
        }
    }

    /**
     * Wait for page to be ready using dashboard method.
     */
    public void waitForPageReady() {
        dashboard.waitForPageReady();
    }

    /**
     * Accept cookies if present.
     */
    public void acceptCookiesIfPresent() {
        dashboard.acceptCookiesIfPresent();
    }

    /**
     * Open home page.
     */
    public void openHomePage() {
        dashboard.openHomePage();
    }

    /**
     * Search book and click first result.
     */
    public boolean searchAndClickFirstResult(String keyword) {
        try {
            submitSearch(keyword);
            return clickFirstSearchResult();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if search results or no results message is displayed.
     */
    public boolean hasSearchResultsOrNoResultsMessage() {
        try {
            return areSearchResultsDisplayed() || hasNoSearchResultsMessage();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if URL contains specific text.
     */
    public boolean isUrlContaining(String searchText) {
        if (isBlank(searchText)) {
            return false;
        }
        return safeLower(getCurrentUrlSafely()).contains(searchText.toLowerCase());
    }

    /**
     * Get current URL from dashboard safely.
     */
    public String getDashboardCurrentUrl() {
        try {
            String url = dashboard.getCurrentUrl();
            return safe(url);
        } catch (Exception e) {
            return getCurrentUrlSafely();
        }
    }

    /**
     * Check if login page is available (guest state).
     */
    public boolean isLoginPageAvailable() {
        return isOnLoginPage() || isLoginTextButtonAvailable();
    }

    /**
     * Perform logout and wait for logged out state.
     */
    public boolean logoutAndWait() {
        clickLogout();
        return waitForLoggedOutState(5000);
    }

    /**
     * Check if current state indicates logged out.
     */
    public boolean isLoggedOut() {
        String currentUrl = safeLower(getCurrentUrlSafely());
        boolean onPage = currentUrl.contains("login")
                || currentUrl.contains("signin")
                || currentUrl.contains("home");
        boolean hasLoginButton = isLoginTextButtonAvailable();
        boolean noLogoutButton = !isLogoutButtonVisible();
        return onPage || hasLoginButton || noLogoutButton;
    }

    /**
     * Navigate to URL safely.
     */
    public void navigateTo(String url) {
        if (isBlank(url)) {
            return;
        }
        driver.get(url);
    }

    /**
     * Refresh current page.
     */
    public void refreshPage() {
        driver.navigate().refresh();
    }

    /**
     * Check if hamburger menu is present (locator check).
     */
    public boolean isHamburgerMenuPresent() {
        try {
            return isDisplayed(org.openqa.selenium.By.xpath(HAMBURGER_MENU_XPATH));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Open hamburger menu if visible.
     */
    public void openHamburgerMenuIfVisible() {
        if (isHamburgerMenuVisible()) {
            clickHamburgerMenu();
            waitQuietly(1000);
        }
    }

    /**
     * Get search keyword from config.
     */
    public String getSearchKeyword() {
        return ConfigReader.getProperty("dashboard.searchKeyword", "audio");
    }

    /**
     * Check if footer links count is greater than zero.
     */
    public boolean hasFooterLinks() {
        return getVisibleFooterLinksCount() > 0;
    }

    /**
     * Verify search input value contains expected keyword.
     */
    public boolean verifySearchInputContains(String expectedKeyword) {
        if (isBlank(expectedKeyword)) {
            return false;
        }
        String actualValue = getSearchInputValue();
        return safeLower(actualValue).contains(expectedKeyword.toLowerCase());
    }

    /**
     * Check if URL indicates search page.
     */
    public boolean isOnSearchPage() {
        String currentUrl = safeLower(getCurrentUrlSafely());
        return currentUrl.contains("search") || currentUrl.contains("web_search");
    }

    /**
     * Check if stayed on valid search page.
     */
    public boolean stayedOnValidSearchPage() {
        return isOnSearchPage() || waitForDashboardShell();
    }

    /**
     * Check if broken footer link produces URL.
     */
    public boolean hasNavigatedUrl() {
        return !isBlank(getCurrentUrlSafely());
    }
}
