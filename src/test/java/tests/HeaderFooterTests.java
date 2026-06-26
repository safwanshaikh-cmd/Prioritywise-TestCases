package tests;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.HeaderFooterPage;
import utils.LoggerUtils;

/**
 * Header and footer module tests.
 *
 * <p>Test Coverage: TC_204 - TC_228
 *
 * <p>Run with: mvn test -Dtest=HeaderFooterTests
 *
 * <p>Account: Consumer (with guest scenarios for specific tests)
 */
public class HeaderFooterTests extends BaseTest {

	private HeaderFooterPage headerFooter;

	@BeforeMethod(alwaysRun = true)
	public void setup(Method method) {
		super.setup();
		headerFooter = new HeaderFooterPage(driver);

		// Prepare test state based on scenario type
		if (headerFooter.isGuestScenario(method.getName())) {
			LoggerUtils.logInfo("Preparing guest state for: " + method.getName());
			headerFooter.prepareGuestState();
		} else {
			LoggerUtils.logInfo("Logging in as consumer for: " + method.getName());
			headerFooter.loginAsConsumer();
		}
	}

	// ==================== TC_204: HEADER VISIBILITY ====================

	/**
	 * TC_204: Header - Verify header section is visible on dashboard
	 */
	@Test(priority = 204, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_204: Verify header section is visible on dashboard")
	public void TC204_VerifyHeaderSectionVisibleOnDashboard() {
		LoggerUtils.logTestStart("TC_204: Header Section Visible on Dashboard");

		try {
			LoggerUtils.logStep(1, "Verify header section is visible");
			boolean isHeaderVisible = headerFooter.isHeaderVisible();
			LoggerUtils.logInfo("TC_204 - STEP 1: Header visible: " + isHeaderVisible);

			Assert.assertTrue(isHeaderVisible, "TC_204: Header section should be visible on dashboard");
			LoggerUtils.logInfo("TC_204: Header section verified successfully");

			LoggerUtils.logTestEnd("TC_204", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_204 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_205: LOGO NAVIGATION ====================

	/**
	 * TC_205: Header - Verify clicking logo redirects to dashboard
	 */
	@Test(priority = 205, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_205: Verify clicking logo redirects to dashboard")
	public void TC205_VerifyClickingLogoRedirectsToDashboard() {
		LoggerUtils.logTestStart("TC_205: Logo Navigation to Dashboard");

		try {
			String searchKeyword = headerFooter.getSearchKeyword();

			LoggerUtils.logStep(1, "Navigate away from dashboard using search");
			Assert.assertTrue(headerFooter.isSearchBarVisible(),
					"TC_205: Search bar should be visible for navigation away from dashboard");
			headerFooter.enterSearchKeyword(searchKeyword);
			headerFooter.clickSearchButton();
			headerFooter.waitQuietly(2000);

			String navigatedUrl = headerFooter.getCurrentUrlSafely();
			boolean navigatedAway = headerFooter.isOnSearchPage()
					|| headerFooter.areSearchResultsDisplayed()
					|| headerFooter.hasNoSearchResultsMessage();
			LoggerUtils.logInfo("TC_205 - STEP 1: Navigated URL: " + navigatedUrl);
			LoggerUtils.logInfo("TC_205 - STEP 1: Navigated away from dashboard: " + navigatedAway);

			Assert.assertTrue(navigatedAway,
					"TC_205: User should be able to navigate away from dashboard before clicking the logo");

			LoggerUtils.logStep(2, "Click header logo and verify navigation");
			Assert.assertTrue(headerFooter.isHeaderLogoVisible(),
					"TC_205: Header logo should be visible");
			String currentUrl = headerFooter.clickHeaderLogoAndGetCurrentUrl();
			boolean dashboardReady = headerFooter.waitForDashboardShell();
			LoggerUtils.logInfo("TC_205 - STEP 2: URL after logo click: " + currentUrl);
			LoggerUtils.logInfo("TC_205 - STEP 2: Dashboard ready after logo click: " + dashboardReady);

			Assert.assertTrue(dashboardReady,
					"TC_205: Dashboard page should remain available after clicking logo");

			LoggerUtils.logStep(3, "Verify redirected to dashboard or home");
			String baseUrl = headerFooter.getBaseUrl().toLowerCase();
			boolean isBaseLanding = currentUrl.equals(baseUrl)
					|| currentUrl.equals(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/");
			if (isBaseLanding) {
				Assert.assertTrue(true, "TC_205: Clicking logo redirected to base URL");
				LoggerUtils.logInfo("TC_205: Logo click redirected to base landing page");
			} else {
				headerFooter.assertUrlContainsAny("TC_205: Clicking logo should redirect to dashboard or home",
						"dashboard", "home");
			}

			LoggerUtils.logTestEnd("TC_205", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_205 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_206: SEARCH BAR ====================

	/**
	 * TC_206: Header - Verify search bar accepts input
	 */
	@Test(priority = 206, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_206: Verify search bar accepts input")
	public void TC206_VerifySearchBarAcceptsInput() {
		LoggerUtils.logTestStart("TC_206: Search Bar Input Acceptance");

		try {
			String searchKeyword = headerFooter.getSearchKeyword();

			LoggerUtils.logStep(1, "Verify search bar is visible and enter keyword");
			Assert.assertTrue(headerFooter.isSearchBarVisible(),
					"TC_206: Search bar should be visible on dashboard");
			headerFooter.enterSearchKeyword(searchKeyword);
			LoggerUtils.logInfo("TC_206 - STEP 1: Entered search keyword: " + searchKeyword);

			LoggerUtils.logStep(2, "Verify search bar accepted the input");
			String searchValue = headerFooter.getSearchInputValue();
			boolean containsKeyword = headerFooter.verifySearchInputContains(searchKeyword);
			LoggerUtils.logInfo("TC_206 - STEP 2: Search input value: " + searchValue);
			LoggerUtils.logInfo("TC_206 - STEP 2: Contains keyword: " + containsKeyword);

			Assert.assertTrue(containsKeyword,
					"TC_206: Search bar should accept the entered keyword");
			LoggerUtils.logInfo("TC_206: Search bar input verified successfully");

			LoggerUtils.logTestEnd("TC_206", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_206 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_207: SEARCH INVALID DATA ====================

	/**
	 * TC_207: Header - Verify search behavior with invalid input
	 */
	@Test(priority = 207, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_207: Verify search behavior with invalid input")
	public void TC207_VerifySearchBehaviorWithInvalidInput() {
		LoggerUtils.logTestStart("TC_207: Search Behavior With Invalid Input");

		try {
			String invalidKeyword = "###@@@";

			LoggerUtils.logStep(1, "Enter invalid search keyword and submit");
			Assert.assertTrue(headerFooter.isSearchBarVisible(),
					"TC_207: Search bar should be visible on dashboard");
			headerFooter.enterSearchKeyword(invalidKeyword);
			headerFooter.clickSearchButton();
			headerFooter.waitQuietly(2000);
			LoggerUtils.logInfo("TC_207 - STEP 1: Entered invalid keyword: " + invalidKeyword);

			LoggerUtils.logStep(2, "Verify invalid input is handled gracefully");
			String currentUrl = headerFooter.getCurrentUrlSafely();
			String searchValue = headerFooter.getSearchInputValue();
			boolean hasResults = headerFooter.areSearchResultsDisplayed();
			boolean hasNoResultsMessage = headerFooter.hasNoSearchResultsMessage();
			boolean stayedOnValidSearchPage = headerFooter.stayedOnValidSearchPage();
			LoggerUtils.logInfo("TC_207 - STEP 2: Current URL: " + currentUrl);
			LoggerUtils.logInfo("TC_207 - STEP 2: Search value retained: " + searchValue);
			LoggerUtils.logInfo("TC_207 - STEP 2: Has results: " + hasResults);
			LoggerUtils.logInfo("TC_207 - STEP 2: Has no-results message: " + hasNoResultsMessage);
			LoggerUtils.logInfo("TC_207 - STEP 2: Stayed on valid search page: " + stayedOnValidSearchPage);

			Assert.assertTrue(headerFooter.verifySearchInputContains(invalidKeyword),
					"TC_207: Search field should retain the invalid input value");
			Assert.assertTrue(hasNoResultsMessage || !hasResults || stayedOnValidSearchPage,
					"TC_207: Invalid search input should be handled gracefully");
			LoggerUtils.logInfo("TC_207: Invalid search input verified successfully");

			LoggerUtils.logTestEnd("TC_207", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_207 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_212: THEME TOGGLE VISIBILITY ====================

	/**
	 * TC_212: Header - Verify theme toggle button is visible
	 */
	@Test(priority = 212, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_212: Verify theme toggle button is visible")
	public void TC212_VerifyThemeToggleButtonVisible() {
		LoggerUtils.logTestStart("TC_212: Theme Toggle Button Visibility");

		try {
			LoggerUtils.logStep(1, "Verify theme toggle button is visible in header");
			boolean isVisible = headerFooter.isThemeToggleVisible();
			LoggerUtils.logInfo("TC_212 - STEP 1: Theme toggle visible: " + isVisible);

			Assert.assertTrue(isVisible,
					"TC_212: Theme toggle button should be visible in header");
			LoggerUtils.logInfo("TC_212: Theme toggle button verified successfully");

			LoggerUtils.logTestEnd("TC_212", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_212 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_216: PROFILE ICON ====================

	/**
	 * TC_216: Header - Verify profile icon is visible
	 */
	@Test(priority = 216, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_216: Verify profile icon is visible")
	public void TC216_VerifyProfileIconVisible() {
		LoggerUtils.logTestStart("TC_216: Profile Icon Visibility");

		try {
			LoggerUtils.logStep(1, "Verify hamburger button (profile icon) is visible in header");
			boolean isVisible = headerFooter.isHamburgerMenuVisible();
			LoggerUtils.logInfo("TC_216 - STEP 1: Hamburger menu visible: " + isVisible);

			Assert.assertTrue(isVisible,
					"TC_216: Hamburger button should be visible in header");
			LoggerUtils.logInfo("TC_216: Hamburger button verified successfully");

			LoggerUtils.logTestEnd("TC_216", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_216 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_217: PROFILE MENU ====================

	/**
	 * TC_217: Header - Verify clicking profile icon opens menu
	 */
	@Test(priority = 217, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_217: Verify clicking profile icon opens menu")
	public void TC217_VerifyClickingProfileIconOpensMenu() {
		LoggerUtils.logTestStart("TC_217: Profile Menu Opening");

		try {
			LoggerUtils.logStep(1, "Verify hamburger button is visible");
			Assert.assertTrue(headerFooter.isHamburgerMenuVisible(),
					"TC_217: Hamburger button should be visible in header");

			LoggerUtils.logStep(2, "Click hamburger button and verify menu opens");
			headerFooter.clickHamburgerMenu();
			boolean isMenuVisible = headerFooter.isProfileMenuVisible();
			LoggerUtils.logInfo("TC_217 - STEP 2: Profile menu visible after click: " + isMenuVisible);

			Assert.assertTrue(isMenuVisible,
					"TC_217: Profile menu should be displayed after clicking hamburger button");
			LoggerUtils.logInfo("TC_217: Profile menu opening verified successfully");

			LoggerUtils.logTestEnd("TC_217", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_217 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_218: LOGOUT ====================

	/**
	 * TC_218: Header - Verify user can logout
	 */
	@Test(priority = 218, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_218: Verify user can logout")
	public void TC218_VerifyUserCanLogout() {
		LoggerUtils.logTestStart("TC_218: User Logout Functionality");

		try {
			LoggerUtils.logStep(1, "Click logout and wait for logged out state");
			boolean loggedOut = headerFooter.logoutAndWait();
			LoggerUtils.logInfo("TC_218 - STEP 1: User logged out: " + loggedOut);

			Assert.assertTrue(loggedOut,
					"TC_218: User should be logged out successfully");
			LoggerUtils.logInfo("TC_218: User logout verified successfully");

			LoggerUtils.logTestEnd("TC_218", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_218 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_219: LOGOUT SESSION ====================

	/**
	 * TC_219: Header - Verify session ends after logout
	 */
	@Test(priority = 219, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_219: Verify session ends after logout")
	public void TC219_VerifySessionEndsAfterLogout() {
		LoggerUtils.logTestStart("TC_219: Session Termination After Logout");

		try {
			LoggerUtils.logStep(1, "Logout and wait for logged out state");
			boolean loggedOut = headerFooter.logoutAndWait();
			LoggerUtils.logInfo("TC_219 - STEP 1: User logged out: " + loggedOut);

			Assert.assertTrue(loggedOut,
					"TC_219: User should be logged out before re-accessing dashboard");

			LoggerUtils.logStep(2, "Navigate to dashboard URL and verify session ended");
			headerFooter.navigateToDashboard();
			headerFooter.waitForPageReady();
			headerFooter.openHamburgerMenuIfVisible();
			boolean sessionEnded = headerFooter.isLoginPageAvailable()
					|| !headerFooter.isLogoutButtonVisible();
			LoggerUtils.logInfo("TC_219 - STEP 2: Session ended: " + sessionEnded);

			Assert.assertTrue(sessionEnded,
					"TC_219: User session should end after logout");
			LoggerUtils.logInfo("TC_219: Session termination verified successfully");

			LoggerUtils.logTestEnd("TC_219", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_219 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_220: FOOTER VISIBILITY ====================

	/**
	 * TC_220: Footer - Verify footer section is displayed
	 */
	@Test(priority = 220, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_220: Verify footer section is displayed")
	public void TC220_VerifyFooterSectionDisplayed() {
		LoggerUtils.logTestStart("TC_220: Footer Section Display");

		try {
			LoggerUtils.logStep(1, "Verify footer section is visible");
			boolean isVisible = headerFooter.isFooterVisible();
			LoggerUtils.logInfo("TC_220 - STEP 1: Footer visible: " + isVisible);

			Assert.assertTrue(isVisible,
					"TC_220: Footer section should be visible");
			LoggerUtils.logInfo("TC_220: Footer section verified successfully");

			LoggerUtils.logTestEnd("TC_220", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_220 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_221: FOOTER LINKS DISPLAY ====================

	/**
	 * TC_221: Footer - Verify all footer links are displayed
	 */
	@Test(priority = 221, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_221: Verify all footer links are displayed")
	public void TC221_VerifyAllFooterLinksDisplayed() {
		LoggerUtils.logTestStart("TC_221: Footer Links Display");

		try {
			LoggerUtils.logStep(1, "Verify footer is visible");
			Assert.assertTrue(headerFooter.isFooterVisible(),
					"TC_221: Footer should be visible");

			LoggerUtils.logStep(2, "Verify footer links are displayed");
			int linkCount = headerFooter.getVisibleFooterLinksCount();
			boolean hasLinks = linkCount > 0;
			LoggerUtils.logInfo("TC_221 - STEP 2: Footer links count: " + linkCount);

			Assert.assertTrue(hasLinks,
					"TC_221: Footer links should be displayed");
			LoggerUtils.logInfo("TC_221: Footer links verified successfully");

			LoggerUtils.logTestEnd("TC_221", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_221 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_222: PRIVACY POLICY LINK ====================

	/**
	 * TC_222: Footer - Verify privacy policy page opens
	 */
	@Test(priority = 222, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_222: Verify privacy policy page opens")
	public void TC222_VerifyPrivacyPolicyPageOpens() {
		LoggerUtils.logTestStart("TC_222: Privacy Policy Link Navigation");

		try {
			LoggerUtils.logStep(1, "Verify footer is visible");
			Assert.assertTrue(headerFooter.isFooterVisible(),
					"TC_222: Footer should be visible");

			LoggerUtils.logStep(2, "Open Privacy Policy link and verify navigation");
			String currentUrl = headerFooter.openPrivacyPolicyLink();
			LoggerUtils.logInfo("TC_222 - STEP 2: Navigated URL: " + currentUrl);

			headerFooter.assertUrlContainsAny("TC_222: Privacy Policy page should open", "privacy");
			LoggerUtils.logInfo("TC_222: Privacy Policy link navigation verified successfully");

			LoggerUtils.logTestEnd("TC_222", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_222 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_223: TERMS & CONDITIONS LINK ====================

	/**
	 * TC_223: Footer - Verify terms page opens
	 */
	@Test(priority = 223, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_223: Verify terms page opens")
	public void TC223_VerifyTermsPageOpens() {
		LoggerUtils.logTestStart("TC_223: Terms and Conditions Link Navigation");

		try {
			LoggerUtils.logStep(1, "Verify footer is visible");
			Assert.assertTrue(headerFooter.isFooterVisible(),
					"TC_223: Footer should be visible");

			LoggerUtils.logStep(2, "Open Terms link and verify navigation");
			String currentUrl = headerFooter.openTermsAndConditionsLink();
			LoggerUtils.logInfo("TC_223 - STEP 2: Navigated URL: " + currentUrl);

			headerFooter.assertUrlContainsAny("TC_223: Terms page should open", "terms");
			LoggerUtils.logInfo("TC_223: Terms link navigation verified successfully");

			LoggerUtils.logTestEnd("TC_223", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_223 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_224: CONTACT US LINK ====================

	/**
	 * TC_224: Footer - Verify contact page opens
	 */
	@Test(priority = 224, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_224: Verify contact page opens")
	public void TC224_VerifyContactPageOpens() {
		LoggerUtils.logTestStart("TC_224: Contact Us Link Navigation");

		try {
			LoggerUtils.logStep(1, "Verify footer is visible");
			Assert.assertTrue(headerFooter.isFooterVisible(),
					"TC_224: Footer should be visible");

			LoggerUtils.logStep(2, "Open Contact Us link and verify navigation");
			String currentUrl = headerFooter.openContactUsLink();
			LoggerUtils.logInfo("TC_224 - STEP 2: Navigated URL: " + currentUrl);

			headerFooter.assertUrlContainsAny("TC_224: Contact page should open", "contact");
			LoggerUtils.logInfo("TC_224: Contact link navigation verified successfully");

			LoggerUtils.logTestEnd("TC_224", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_224 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_225: FACEBOOK LINK ====================

	/**
	 * TC_225: Footer - Verify Facebook link redirects correctly
	 */
	@Test(priority = 225, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_225: Verify Facebook link redirects correctly")
	public void TC225_VerifyFacebookLinkRedirectsCorrectly() {
		LoggerUtils.logTestStart("TC_225: Facebook Link Navigation");

		try {
			LoggerUtils.logStep(1, "Verify footer is visible");
			Assert.assertTrue(headerFooter.isFooterVisible(),
					"TC_225: Footer should be visible");

			LoggerUtils.logStep(2, "Open Facebook link and verify navigation");
			String currentUrl = headerFooter.openFacebookLink();
			LoggerUtils.logInfo("TC_225 - STEP 2: Navigated URL: " + currentUrl);

			headerFooter.assertUrlContainsAny("TC_225: Facebook page should open", "facebook");
			LoggerUtils.logInfo("TC_225: Facebook link navigation verified successfully");

			LoggerUtils.logTestEnd("TC_225", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_225 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_226: INSTAGRAM LINK ====================

	/**
	 * TC_226: Footer - Verify Instagram link redirects correctly
	 */
	@Test(priority = 226, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_226: Verify Instagram link redirects correctly")
	public void TC226_VerifyInstagramLinkRedirectsCorrectly() {
		LoggerUtils.logTestStart("TC_226: Instagram Link Navigation");

		try {
			LoggerUtils.logStep(1, "Verify footer is visible");
			Assert.assertTrue(headerFooter.isFooterVisible(),
					"TC_226: Footer should be visible");

			LoggerUtils.logStep(2, "Open Instagram link and verify navigation");
			String currentUrl = headerFooter.openInstagramLink();
			LoggerUtils.logInfo("TC_226 - STEP 2: Navigated URL: " + currentUrl);

			headerFooter.assertUrlContainsAny("TC_226: Instagram page should open", "instagram");
			LoggerUtils.logInfo("TC_226: Instagram link navigation verified successfully");

			LoggerUtils.logTestEnd("TC_226", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_226 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_227: BROKEN FOOTER LINK ====================

	/**
	 * TC_227: Footer - Verify behavior when footer link is broken
	 */
	@Test(priority = 227, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_227: Verify behavior when footer link is broken")
	public void TC227_VerifyBehaviorWhenFooterLinkIsBroken() {
		LoggerUtils.logTestStart("TC_227: Broken Footer Link Behavior");

		try {
			LoggerUtils.logStep(1, "Verify footer is visible");
			Assert.assertTrue(headerFooter.isFooterVisible(),
					"TC_227: Footer should be visible");

			LoggerUtils.logStep(2, "Open broken footer link and verify navigation");
			String currentUrl = headerFooter.openBrokenFooterLink();
			currentUrl = headerFooter.safeLower(currentUrl);
			LoggerUtils.logInfo("TC_227 - STEP 2: Navigated URL: " + currentUrl);

			Assert.assertTrue(headerFooter.hasNavigatedUrl(),
					"TC_227: Broken footer link should still produce a navigated URL");
			Assert.assertTrue(currentUrl.contains("broken-footer-link-automation"),
					"TC_227: Broken footer link should navigate to the known invalid destination");
			Assert.assertFalse(currentUrl.contains("privacy"),
					"TC_227: Broken footer link should not resolve to the valid Privacy Policy destination");
			LoggerUtils.logInfo("TC_227: Broken footer link behavior verified successfully");

			LoggerUtils.logTestEnd("TC_227", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_227 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_228: FOOTER LINKS WITHOUT LOGIN ====================

	/**
	 * TC_228: Footer - Verify footer links accessible without login
	 */
	@Test(priority = 228, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_228: Verify footer links accessible without login")
	public void TC228_VerifyFooterLinksAccessibleWithoutLogin() {
		LoggerUtils.logTestStart("TC_228: Footer Links Access Without Login");

		try {
			LoggerUtils.logStep(1, "Verify footer is visible for guest users");
			Assert.assertTrue(headerFooter.isFooterVisible(),
					"TC_228: Footer should be visible for guest users");

			LoggerUtils.logStep(2, "Open Privacy Policy link and verify access without login");
			String privacyUrl = headerFooter.openPrivacyPolicyLink();
			LoggerUtils.logInfo("TC_228 - STEP 2: Navigated URL: " + privacyUrl);

			headerFooter.assertUrlContainsAny("TC_228: Privacy Policy should be accessible without login",
					"privacy");
			LoggerUtils.logInfo("TC_228: Footer links access without login verified successfully");

			LoggerUtils.logTestEnd("TC_228", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_228 - Test failed: " + headerFooter.safe(e.getMessage()));
			throw e;
		}
	}

	// ==================== (No local helpers — see HeaderFooterPage) ====================
}
