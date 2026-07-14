package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.SecurityPage;
import utils.LoggerUtils;
import utils.SecurityUtils;

/**
 * Security module automation tests.
 *
 * <p>Test Coverage: TC_192 - TC_203, TC_378, TC_383
 * <p>Focus: Direct-URL guest access, session expiry / invalidation,
 * role-based access control, payment-page HTTPS, and payment-flow session
 * expiry — safe, controlled validations in the test environment only.
 *
 * <p>All reusable navigation, session handling, role-access orchestration,
 * URL-redirection checks, and null-safe helpers live in {@link SecurityPage}
 * and {@link SecurityUtils}. This class contains only the test execution flow,
 * {@link LoggerUtils} statements, assertions, and calls to {@code SecurityPage}
 * / {@code SecurityUtils} — mirroring the structure of {@code ChapterTests}
 * and {@code ConsumerBookDetailsTests}.
 *
 * <p>Run with: {@code mvn test -Dtest=SecurityTests}. Account: Consumer (for
 * logged-in tests). No real attacks, credential abuse, DoS, or unauthorized
 * data access are performed — these are controlled, defensive validations only.
 */
public class SecurityTests extends BaseTest {

	// Guest-access target URLs (configurable base; same values the old tests inlined).
	private static final String URL_CATEGORY_LISTING =
			"https://web-splay.acceses.com/book_category_listing?title=Classic&category=6";
	private static final String URL_RELATED_SHOWS =
			"https://web-splay.acceses.com/view_all_books?url=recommendations&title=More%20Related%20Shows";
	private static final String URL_UPCOMING =
			"https://web-splay.acceses.com/view_all_books?url=upcoming-releases&title=Upcoming%20Releases";
	private static final String URL_TRENDING =
			"https://web-splay.acceses.com/view_all_books?url=recommendations%2Ftrending&title=Trending%20Shows";

	private SecurityPage security;

	@BeforeMethod(alwaysRun = true)
	@Override
	public void setup() {
		super.setup();
		security = new SecurityPage(driver);
	}

	// ==================== TC_192: DIRECT CATEGORY URL ====================

	/**
	 * TC_192: Verify direct category URL access without login
	 * Test Flow: Clear session → Access category URL → Verify guest access
	 * Expected: The category listing URL should be accessible without login
	 */
	@Test(priority = 192, groups = { TestConstants.GROUP_SECURITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_192: Verify direct category URL access without login")
	public void TC192_VerifyDirectCategoryUrlAccessWithoutLogin() {
		LoggerUtils.logTestStart("TC_192: Direct Category URL Access");

		try {
			LoggerUtils.logStep(1, "Clear any existing session for guest access");
			security.clearSession();

			LoggerUtils.logStep(2, "Access the category listing URL directly without login");
			boolean accessible = security.accessGuestUrlAndVerify(URL_CATEGORY_LISTING, "book_category_listing");
			LoggerUtils.logInfo("TC_192 - STEP 2: Guest accessible: " + accessible);

			Assert.assertTrue(accessible, "TC_192: Category listing URL should be accessible without login. Current URL: "
					+ security.getCurrentUrlSafely());

			LoggerUtils.logTestEnd("TC_192", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_192 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_196: DIRECT RELATED SHOWS URL ====================

	/**
	 * TC_196: Verify direct related-shows URL access without login
	 * Test Flow: Clear session → Access related-shows URL → Verify guest access
	 * Expected: The related-shows URL should be accessible without login
	 */
	@Test(priority = 196, groups = { TestConstants.GROUP_SECURITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_196: Verify direct related-shows URL access without login")
	public void TC196_VerifyDirectRelatedShowsUrlAccessWithoutLogin() {
		LoggerUtils.logTestStart("TC_196: Direct Related Shows URL Access");

		try {
			LoggerUtils.logStep(1, "Clear any existing session for guest access");
			security.clearSession();

			LoggerUtils.logStep(2, "Access the related-shows URL directly without login");
			boolean accessible = security.accessGuestUrlAndVerify(URL_RELATED_SHOWS, "recommendations");
			LoggerUtils.logInfo("TC_196 - STEP 2: Guest accessible: " + accessible);

			Assert.assertTrue(accessible,
					"TC_196: Related-shows URL should be accessible without login. Current URL: "
							+ security.getCurrentUrlSafely());

			LoggerUtils.logTestEnd("TC_196", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_196 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_197: RELATED SHOW DETAIL (DASHBOARD-ONLY) ====================

	/**
	 * TC_197: Verify related-show-detail access without login
	 * Test Flow: N/A — there is no standalone direct URL for related-show detail
	 * Expected: Permanently skipped — the related-show detail is dashboard-only
	 */
	@Test(priority = 197, groups = { TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_FUNCTIONAL }, retryAnalyzer = RetryAnalyzer.class, description = "TC_197: Verify related-show-detail access without login")
	public void TC197_VerifyDirectRelatedShowDetailAccessWithoutLogin() {
		LoggerUtils.logTestStart("TC_197: Direct Related Show Detail Access");

		try {
			LoggerUtils.logStep(1, "Confirm the related-show detail has no standalone direct URL");
			LoggerUtils.logTestEnd("TC_197", "SKIPPED");
			throw new SkipException(
					"TC_197 is dashboard-only. There is no standalone direct URL for Related Show Detail access.");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_197 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_198: DIRECT UPCOMING URL ====================

	/**
	 * TC_198: Verify direct upcoming URL access without login
	 * Test Flow: Clear session → Access upcoming URL → Verify guest access
	 * Expected: The upcoming-releases URL should be accessible without login
	 */
	@Test(priority = 198, groups = { TestConstants.GROUP_SECURITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_198: Verify direct upcoming URL access without login")
	public void TC198_VerifyDirectUpcomingUrlAccessWithoutLogin() {
		LoggerUtils.logTestStart("TC_198: Direct Upcoming URL Access");

		try {
			LoggerUtils.logStep(1, "Clear any existing session for guest access");
			security.clearSession();

			LoggerUtils.logStep(2, "Access the upcoming-releases URL directly without login");
			boolean accessible = security.accessGuestUrlAndVerify(URL_UPCOMING, "upcoming-releases");
			LoggerUtils.logInfo("TC_198 - STEP 2: Guest accessible: " + accessible);

			Assert.assertTrue(accessible,
					"TC_198: Upcoming-releases URL should be accessible without login. Current URL: "
							+ security.getCurrentUrlSafely());

			LoggerUtils.logTestEnd("TC_198", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_198 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_200: DIRECT TRENDING / MOST-RATED URL ====================

	/**
	 * TC_200: Verify direct trending/most-rated URL access without login
	 * Test Flow: Clear session → Access trending URL → Verify guest access
	 * Expected: The trending-recommendations URL should be accessible without login
	 */
	@Test(priority = 200, groups = { TestConstants.GROUP_SECURITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_200: Verify direct trending/most-rated URL access without login")
	public void TC200_VerifyDirectMostRatedUrlAccessWithoutLogin() {
		LoggerUtils.logTestStart("TC_200: Direct Trending Shows URL Access");

		try {
			LoggerUtils.logStep(1, "Clear any existing session for guest access");
			security.clearSession();

			LoggerUtils.logStep(2, "Access the trending-recommendations URL directly without login");
			boolean accessible = security.accessGuestUrlAndVerify(URL_TRENDING, "trending");
			LoggerUtils.logInfo("TC_200 - STEP 2: Guest accessible: " + accessible);

			Assert.assertTrue(accessible,
					"TC_200: Trending URL should be accessible without login. Current URL: "
							+ security.getCurrentUrlSafely());

			LoggerUtils.logTestEnd("TC_200", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_200 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_202: EXPIRED SESSION ACCESS ====================

	/**
	 * TC_202: Verify direct URL access after session expiry
	 * Test Flow: Login → Store URL → Clear session → Reload stored URL → Verify still accessible
	 * Expected: The stored URL should remain accessible after session expiry (guest access supported)
	 */
	@Test(priority = 202, groups = { TestConstants.GROUP_SECURITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_202: Verify direct URL access after session expiry")
	public void TC202_VerifyDirectUrlAccessAfterSessionExpiry() {
		LoggerUtils.logTestStart("TC_202: Direct URL Access After Session Expiry");

		try {
			LoggerUtils.logStep(1, "Log in as consumer and capture the post-login URL");
			security.loginAsConsumer();
			String storedUrl = security.getCurrentUrlSafely();
			LoggerUtils.logInfo("TC_202 - STEP 1: Stored post-login URL captured");

			LoggerUtils.logStep(2, "Clear the session to simulate expiry");
			security.clearSession();
			LoggerUtils.logInfo("TC_202 - STEP 2: Session cleared (simulating expiry)");

			LoggerUtils.logStep(3, "Reload the stored URL and verify it is still accessible");
			String currentUrl = security.accessUrlAndReturnCurrent(storedUrl);
			boolean accessible = SecurityUtils.isGuestAccessible(currentUrl, "", "");
			LoggerUtils.logInfo("TC_202 - STEP 3: Accessible after expiry: " + accessible);

			Assert.assertTrue(accessible,
					"TC_202: Stored URL should remain accessible after session expiry. Current URL: " + currentUrl);

			LoggerUtils.logTestEnd("TC_202", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_202 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_203: UNAUTHORIZED ROLE ACCESS ====================

	/**
	 * TC_203: Verify consumer cannot open restricted admin pages
	 * Test Flow: Login as consumer → Open admin URL → Verify admin dashboard not loaded + restricted state
	 * Expected: The consumer should be redirected away from admin pages or shown a restricted-access state
	 */
	@Test(priority = 203, groups = { TestConstants.GROUP_SECURITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_203: Verify consumer cannot open restricted admin pages")
	public void TC203_VerifyRestrictedDashboardPagesCannotOpenWithWrongRole() {
		LoggerUtils.logTestStart("TC_203: Unauthorized Role Access");

		try {
			LoggerUtils.logStep(1, "Log in as the consumer (non-admin role)");
			security.loginAsConsumer();
			String adminUrl = security.getAdminRestrictedUrl();
			LoggerUtils.logInfo("TC_203 - STEP 1: Admin URL under test resolved");

			LoggerUtils.logStep(2, "Open the admin URL as the consumer and observe the result");
			String currentUrl = security.openAdminUrlAndGetResult(adminUrl);
			LoggerUtils.logInfo("TC_203 - STEP 2: Resolved URL after admin route access");

			LoggerUtils.logStep(3, "Verify the admin dashboard is not loaded and access is restricted");
			boolean adminLoaded = security.isAdminDashboardLoaded();
			boolean restricted = security.isRestrictedAccessState(currentUrl, adminLoaded);
			LoggerUtils.logInfo("TC_203 - STEP 3: Admin loaded: " + adminLoaded + ", Restricted: " + restricted);

			Assert.assertFalse(adminLoaded, "TC_203: Consumer should not be able to open the admin dashboard.");
			Assert.assertTrue(restricted,
					"TC_203: Consumer should be redirected away from restricted admin pages or shown a restricted-access state. Current URL: "
							+ currentUrl);

			LoggerUtils.logTestEnd("TC_203", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_203 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_378: PAYMENT PAGE HTTPS ====================

	/**
	 * TC_378: Verify HTTPS is used on the payment page
	 * Test Flow: Login → Open payment surface → Verify HTTPS
	 * Expected: The payment page should be served over HTTPS
	 */
	@Test(priority = 378, groups = { TestConstants.GROUP_SECURITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_PERFORMANCE }, retryAnalyzer = RetryAnalyzer.class, description = "TC_378: Verify HTTPS is used on the payment page")
	public void TC378_VerifyPaymentPageHTTPS() {
		LoggerUtils.logTestStart("TC_378: Payment Page HTTPS Verification");

		try {
			LoggerUtils.logStep(1, "Log in as the consumer");
			security.loginAsConsumer();

			LoggerUtils.logStep(2, "Open the payment surface via the side menu");
			security.openPaymentSidebar();

			LoggerUtils.logStep(3, "Verify the payment page is served over HTTPS");
			boolean isHttps = security.isCurrentUrlHttps();
			LoggerUtils.logInfo("TC_378 - STEP 3: Payment page HTTPS: " + isHttps);

			Assert.assertTrue(isHttps, "TC_378: Payment page should use HTTPS. Current URL: "
					+ security.getCurrentUrlSafely());

			LoggerUtils.logTestEnd("TC_378", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_378 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_383: PAYMENT-FLOW SESSION EXPIRY ====================

	/**
	 * TC_383: Verify session expiry behavior on the payment flow
	 * Test Flow: Login → Open payment → Clear session → Refresh → Verify accessible or redirected to login
	 * Expected: After expiry the page should remain accessible (guest) or redirect to login
	 */
	@Test(priority = 383, groups = { TestConstants.GROUP_SECURITY, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_PERFORMANCE }, retryAnalyzer = RetryAnalyzer.class, description = "TC_383: Verify session expiry behavior on the payment flow")
	public void TC383_VerifyPaymentFlowSessionExpiry() {
		LoggerUtils.logTestStart("TC_383: Payment Session Expiry Test");

		try {
			LoggerUtils.logStep(1, "Log in as the consumer and open the payment surface");
			security.loginAsConsumer();
			security.openPaymentSidebar();

			LoggerUtils.logStep(2, "Clear the session to simulate expiry");
			security.clearSession();
			LoggerUtils.logInfo("TC_383 - STEP 2: Session cleared (simulating expiry)");

			LoggerUtils.logStep(3, "Refresh and verify the page is accessible or redirected to login");
			security.refreshPage();
			String currentUrl = security.getCurrentUrlSafely();
			boolean safeState = SecurityUtils.isAccessibleOrRedirectedAfterExpiry(currentUrl);
			LoggerUtils.logInfo("TC_383 - STEP 3: Safe state after expiry: " + safeState);

			Assert.assertTrue(safeState,
					"TC_383: After session expiry the page should allow guest access or redirect to login. Current URL: "
							+ currentUrl);

			LoggerUtils.logTestEnd("TC_383", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_383 - Test failed: " + security.safeString(e.getMessage()));
			throw e;
		}
	}

}
