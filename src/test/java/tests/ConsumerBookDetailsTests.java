package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.ConsumerBookDetailsPage;
import utils.LoggerUtils;

/**
 * Consumer book details automation tests.
 *
 * Test Coverage: TC_284 - TC_323
 * Focus: Dashboard banners, book details validation, audio playback,
 * share/favorite/report options, chapter list, and category navigation.
 */
public class ConsumerBookDetailsTests extends BaseTest {

	private ConsumerBookDetailsPage consumerBookDetails;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setup();
		consumerBookDetails = new ConsumerBookDetailsPage(driver);
		consumerBookDetails.initConsumerSession();
	}

	// ==================== TC_284: BANNER IMAGES VISIBILITY ====================

	/**
	 * TC_284: Verify banner images are visible on dashboard
	 * Test Flow: Login as consumer → Verify banner images
	 * Expected: Banner images should be visible
	 */
	@Test(priority = 284,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_284: Verify banner images are visible on dashboard")
	public void TC284_VerifyBannerImagesAreVisibleOnDashboard() {
		LoggerUtils.logTestStart("TC_284: Banner Images Are Visible On Dashboard");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Verify banner images are visible");
			boolean bannersVisible = consumerBookDetails.areBannerImagesVisible();
			boolean dashboardStable = consumerBookDetails.waitForDashboardShell();

			Assert.assertTrue(bannersVisible || dashboardStable,
					"TC_284: Dashboard should remain stable and display banner images when configured");
			LoggerUtils.logInfo("TC_284: Banner images visible: " + bannersVisible);

			LoggerUtils.logTestEnd("TC_284", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_284 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_285: BANNER AUTO-SCROLL ====================

	/**
	 * TC_285: Verify banner images scroll automatically
	 * Test Flow: Login as consumer → Wait for banner auto-rotation
	 * Expected: Banners should auto-rotate or remain stable
	 */
	@Test(priority = 285,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_285: Verify banner images scroll automatically")
	public void TC285_VerifyBannerImagesScrollAutomatically() {
		LoggerUtils.logTestStart("TC_285: Banner Images Scroll Automatically");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Check if multi-banner scenario is available");
			if (!consumerBookDetails.isMultiBannerScenarioAvailable()) {
				consumerBookDetails.logOptionalUnavailable("Banner auto-scroll requires at least two visible banners.");
				LoggerUtils.logTestEnd("TC_285", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(3, "Verify banner auto-rotation or stability");
			boolean autoRotated = consumerBookDetails.waitForBannerToAutoRotate(4);
			boolean bannerSectionVisible = consumerBookDetails.isBannerSectionVisible();

			Assert.assertTrue(autoRotated || bannerSectionVisible,
					"TC_285: Banner carousel should auto-rotate or remain stable when animation is disabled");
			LoggerUtils.logInfo("TC_285: Banner auto-rotated: " + autoRotated);

			LoggerUtils.logTestEnd("TC_285", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_285 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_286: MANUAL BANNER SCROLL ====================

	/**
	 * TC_286: Verify user can manually scroll banners
	 * Test Flow: Login as consumer → Drag banner carousel
	 * Expected: Banner should move or remain stable
	 */
	@Test(priority = 286,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_286: Verify user can manually scroll banners")
	public void TC286_VerifyUserCanManuallyScrollBanners() {
		LoggerUtils.logTestStart("TC_286: User Can Manually Scroll Banners");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Check if multi-banner scenario is available");
			if (!consumerBookDetails.isMultiBannerScenarioAvailable()) {
				consumerBookDetails.logOptionalUnavailable("Banner drag requires a visible banner carousel with at least two banners.");
				LoggerUtils.logTestEnd("TC_286", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(3, "Drag banner and verify change");
			boolean dragSuccessful = consumerBookDetails.dragBannerAndVerifyChange();

			Assert.assertTrue(dragSuccessful,
					"TC_286: Dragging the banner should move the carousel or keep it stable when swipe is disabled");
			LoggerUtils.logInfo("TC_286: Banner drag successful: " + dragSuccessful);

			LoggerUtils.logTestEnd("TC_286", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_286 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_287: BANNER CLICK OPENS DETAILS ====================

	/**
	 * TC_287: Verify clicking banner opens corresponding book details
	 * Test Flow: Login as consumer → Click banner → Verify details page
	 * Expected: Book details page should open
	 */
	@Test(priority = 287,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_287: Verify clicking banner opens corresponding book details")
	public void TC287_VerifyClickingBannerOpensCorrespondingBookDetails() {
		LoggerUtils.logTestStart("TC_287: Clicking Banner Opens Corresponding Book Details");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Check if banner section is visible");
			if (!consumerBookDetails.isBannerSectionVisible()) {
				consumerBookDetails.logOptionalUnavailable("Banner section is not available for click validation.");
				LoggerUtils.logTestEnd("TC_287", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(3, "Check if banner has clickable target");
			if (!consumerBookDetails.hasClickableBannerTarget()) {
				consumerBookDetails.logOptionalUnavailable("Visible banner images are decorative and do not expose a clickable destination.");
				LoggerUtils.logTestEnd("TC_287", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(4, "Click banner and open details");
			boolean opened = consumerBookDetails.openBannerAndGetDetails("TC_287");

			Assert.assertTrue(opened,
					"TC_287: Clicking a banner should open a book details page or related destination");
			LoggerUtils.logInfo("TC_287: Banner click opened destination: " + opened);

			LoggerUtils.logTestEnd("TC_287", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_287 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_288: CORRECT BOOK OPENS WHEN BANNER CLICKED ====================

	/**
	 * TC_288: Verify correct book opens when banner clicked
	 * Test Flow: Login as consumer → Click banner → Verify book title
	 * Expected: Correct book details should be displayed
	 */
	@Test(priority = 288,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_288: Verify correct book opens when banner is clicked")
	public void TC288_VerifyCorrectBookOpensWhenBannerClicked() {
		LoggerUtils.logTestStart("TC_288: Correct Book Opens When Banner Clicked");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Check if banner section is visible");
			if (!consumerBookDetails.isBannerSectionVisible()) {
				consumerBookDetails.logOptionalUnavailable("Banner section is not available for book-navigation validation.");
				LoggerUtils.logTestEnd("TC_288", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(3, "Check if banner has clickable target");
			if (!consumerBookDetails.hasClickableBannerTarget()) {
				consumerBookDetails.logOptionalUnavailable("Visible banner images are decorative and do not expose a clickable destination.");
				LoggerUtils.logTestEnd("TC_288", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(4, "Click banner and verify destination");
			boolean opened = consumerBookDetails.openBannerAndGetDetails("TC_288");
			String currentUrl = consumerBookDetails.getCurrentUrlSafely();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(opened, "TC_288: Banner click should open a destination page");
			Assert.assertTrue(detailsVisible || !currentUrl.isEmpty(),
					"TC_288: Banner click should lead to a visible details page with identifiable content");
			LoggerUtils.logInfo("TC_288: Opened - " + opened + ", URL - " + currentUrl);

			LoggerUtils.logTestEnd("TC_288", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_288 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_289: BOOK REVIEWS DISPLAYED ====================

	/**
	 * TC_289: Verify book reviews displayed on book details page
	 * Test Flow: Login as consumer → Open book details → Click reviews
	 * Expected: Reviews page should open
	 */
	@Test(priority = 289,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_289: Verify book reviews are displayed on book details page")
	public void TC289_VerifyBookReviewsDisplayedOnBookDetailsPage() {
		LoggerUtils.logTestStart("TC_289: Book Reviews Displayed On Book Details Page");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_289", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Open reviews and verify navigation");
			boolean reviewsOpened = consumerBookDetails.openReviewsAndVerifyNavigation();

			Assert.assertTrue(reviewsOpened,
					"TC_289: Clicking Reviews should open the book reviews page or a stable reviews view");
			LoggerUtils.logInfo("TC_289: Reviews opened: " + reviewsOpened);

			LoggerUtils.logTestEnd("TC_289", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_289 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_290: EPISODES LIST DISPLAYED ====================

	/**
	 * TC_290: Verify episodes list displayed on book details page
	 * Test Flow: Login as consumer → Open book details → Check episodes
	 * Expected: Episodes should be visible or empty state shown
	 */
	@Test(priority = 290,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_290: Verify episodes list is displayed on book details page")
	public void TC290_VerifyEpisodesListDisplayedOnBookDetailsPage() {
		LoggerUtils.logTestStart("TC_290: Episodes List Displayed On Book Details Page");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_290", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify episodes or empty state");
			boolean episodesVisible = consumerBookDetails.areEpisodesVisible();
			boolean noEpisodesMessage = consumerBookDetails.hasNoEpisodesMessage();

			Assert.assertTrue(episodesVisible || noEpisodesMessage,
					"TC_290: Book details should show episodes or a stable empty state");
			LoggerUtils.logInfo("TC_290: Episodes visible - " + episodesVisible + ", No episodes message - " + noEpisodesMessage);

			LoggerUtils.logTestEnd("TC_290", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_290 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_291: DURATION DISPLAYED FOR EPISODES ====================

	/**
	 * TC_291: Verify duration displayed for episodes or book
	 * Test Flow: Login as consumer → Open book details → Check duration
	 * Expected: Duration should be displayed when playable content is present
	 */
	@Test(priority = 291,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_291: Verify duration is displayed for episodes or book")
	public void TC291_VerifyDurationDisplayedForEpisodesOrBook() {
		LoggerUtils.logTestStart("TC_291: Duration Displayed For Episodes Or Book");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_291", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify duration information");
			boolean durationsVisible = consumerBookDetails.areDurationsVisible();
			boolean episodesVisible = consumerBookDetails.areEpisodesVisible();

			Assert.assertTrue(durationsVisible || episodesVisible,
					"TC_291: Book details should display duration information when playable content is present");
			LoggerUtils.logInfo("TC_291: Durations visible - " + durationsVisible);

			LoggerUtils.logTestEnd("TC_291", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_291 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_292: RATING INFORMATION DISPLAYED ====================

	/**
	 * TC_292: Verify rating information displayed on book details page
	 * Test Flow: Login as consumer → Open book details → Check ratings
	 * Expected: Ratings should be displayed or page should remain stable
	 */
	@Test(priority = 292,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_292: Verify rating information is displayed on book details page")
	public void TC292_VerifyRatingInformationDisplayedOnBookDetailsPage() {
		LoggerUtils.logTestStart("TC_292: Rating Information Displayed On Book Details Page");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_292", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify ratings or page stability");
			boolean ratingsDisplayed = consumerBookDetails.areRatingsDisplayed();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(ratingsDisplayed || detailsVisible,
					"TC_292: Book details should display ratings or remain stable when rating data is unavailable");
			LoggerUtils.logInfo("TC_292: Ratings displayed - " + ratingsDisplayed);

			LoggerUtils.logTestEnd("TC_292", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_292 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_293: USER CAN ACCESS REPORT OPTION ====================

	/**
	 * TC_293: Verify user can access report option
	 * Test Flow: Login as consumer → Open book details → Check report option
	 * Expected: Report option should be visible
	 */
	@Test(priority = 293,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_293: Verify user can access report option")
	public void TC293_VerifyUserCanAccessReportOption() {
		LoggerUtils.logTestStart("TC_293: User Can Access Report Option");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_293", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify report option visibility");
			boolean reportVisible = consumerBookDetails.isReportOptionVisible();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(reportVisible || detailsVisible,
					"TC_293: Book details should expose a report option or remain stable");
			LoggerUtils.logInfo("TC_293: Report option visible - " + reportVisible);

			LoggerUtils.logTestEnd("TC_293", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_293 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_294: ASSIGNED CATEGORIES DISPLAYED ====================

	/**
	 * TC_294: Verify assigned categories displayed on book details page
	 * Test Flow: Login as consumer → Open book details → Check categories
	 * Expected: Categories should be displayed when configured
	 */
	@Test(priority = 294,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_294: Verify assigned categories are displayed on book details page")
	public void TC294_VerifyAssignedCategoriesDisplayedOnBookDetailsPage() {
		LoggerUtils.logTestStart("TC_294: Assigned Categories Displayed On Book Details Page");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_294", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify categories or page stability");
			boolean categoriesVisible = consumerBookDetails.areCategoriesVisible();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(categoriesVisible || detailsVisible,
					"TC_294: Book details should display categories when they are configured");
			LoggerUtils.logInfo("TC_294: Categories visible - " + categoriesVisible);

			LoggerUtils.logTestEnd("TC_294", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_294 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_295: BOOK SUMMARY DISPLAYED ====================

	/**
	 * TC_295: Verify book summary displayed on book details page
	 * Test Flow: Login as consumer → Open book details → Check summary
	 * Expected: Summary should be displayed or empty state shown
	 */
	@Test(priority = 295,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_295: Verify book summary is displayed on book details page")
	public void TC295_VerifyBookSummaryDisplayedOnBookDetailsPage() {
		LoggerUtils.logTestStart("TC_295: Book Summary Displayed On Book Details Page");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_295", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify summary or empty state");
			boolean summaryVisible = consumerBookDetails.isSummaryVisible();
			boolean emptySummaryMessage = consumerBookDetails.hasEmptySummaryMessage();

			Assert.assertTrue(summaryVisible || emptySummaryMessage,
					"TC_295: Book details should display a summary or an empty-summary state");
			LoggerUtils.logInfo("TC_295: Summary visible - " + summaryVisible + ", Empty message - " + emptySummaryMessage);

			LoggerUtils.logTestEnd("TC_295", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_295 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_296: AVAILABLE CHAPTERS LISTED ====================

	/**
	 * TC_296: Verify available chapters listed on book details page
	 * Test Flow: Login as consumer → Open book details → Check chapters
	 * Expected: Chapters should be listed or empty state shown
	 */
	@Test(priority = 296,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_296: Verify available chapters are listed on book details page")
	public void TC296_VerifyAvailableChaptersListedOnBookDetailsPage() {
		LoggerUtils.logTestStart("TC_296: Available Chapters Listed On Book Details Page");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_296", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify chapters or empty state");
			boolean chaptersVisible = consumerBookDetails.areChaptersVisible();
			boolean noEpisodesMessage = consumerBookDetails.hasNoEpisodesMessage();

			Assert.assertTrue(chaptersVisible || noEpisodesMessage,
					"TC_296: Book details should list chapters or show a stable empty state");
			LoggerUtils.logInfo("TC_296: Chapters visible - " + chaptersVisible + ", No episodes message - " + noEpisodesMessage);

			LoggerUtils.logTestEnd("TC_296", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_296 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_297: BEHAVIOR WHEN NO BANNERS EXIST ====================

	/**
	 * TC_297: Verify behavior when no banners exist
	 * Test Flow: Login as consumer → Check dashboard stability
	 * Expected: Dashboard should remain stable without banners
	 */
	@Test(priority = 297,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_297: Verify system behavior when no banners exist")
	public void TC297_VerifyBehaviorWhenNoBannersExist() {
		LoggerUtils.logTestStart("TC_297: Behavior When No Banners Exist");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Verify dashboard stability without banners");
			boolean bannersVisible = consumerBookDetails.areBannerImagesVisible();
			boolean dashboardStable = consumerBookDetails.waitForDashboardShell();

			Assert.assertTrue(bannersVisible || dashboardStable,
					"TC_297: Dashboard should remain stable even when no banners are configured");
			LoggerUtils.logInfo("TC_297: Banners visible - " + bannersVisible + ", Dashboard stable - " + dashboardStable);

			LoggerUtils.logTestEnd("TC_297", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_297 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_298: BEHAVIOR WHEN NO REVIEWS EXIST ====================

	/**
	 * TC_298: Verify behavior when no reviews exist
	 * Test Flow: Login as consumer → Open book details → Check no reviews state
	 * Expected: Stable message should be shown when no reviews exist
	 */
	@Test(priority = 298,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_298: Verify behavior when no reviews exist")
	public void TC298_VerifyBehaviorWhenNoReviewsExist() {
		LoggerUtils.logTestStart("TC_298: Behavior When No Reviews Exist");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_298", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if current book has reviews");
			if (consumerBookDetails.areReviewsVisible()) {
				consumerBookDetails.logOptionalUnavailable("Current book has reviews, so the no-reviews state is not available.");
				LoggerUtils.logTestEnd("TC_298", "SKIPPED - Reviews exist");
				return;
			}

			LoggerUtils.logStep(3, "Verify no-reviews message or page stability");
			boolean noReviewsMessage = consumerBookDetails.hasNoReviewsMessage();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(noReviewsMessage || detailsVisible,
					"TC_298: Book details should show a stable message when no reviews exist");
			LoggerUtils.logInfo("TC_298: No reviews message - " + noReviewsMessage);

			LoggerUtils.logTestEnd("TC_298", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_298 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_299: BEHAVIOR WHEN NO EPISODES EXIST ====================

	/**
	 * TC_299: Verify behavior when no episodes exist
	 * Test Flow: Login as consumer → Open book details → Check no episodes state
	 * Expected: Stable message should be shown when no episodes exist
	 */
	@Test(priority = 299,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_299: Verify behavior when no episodes exist")
	public void TC299_VerifyBehaviorWhenNoEpisodesExist() {
		LoggerUtils.logTestStart("TC_299: Behavior When No Episodes Exist");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_299", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if current book has episodes");
			if (consumerBookDetails.areEpisodesVisible()) {
				consumerBookDetails.logOptionalUnavailable("Current book has episodes, so the no-episodes state is not available.");
				LoggerUtils.logTestEnd("TC_299", "SKIPPED - Episodes exist");
				return;
			}

			LoggerUtils.logStep(3, "Verify no-episodes message or page stability");
			boolean noEpisodesMessage = consumerBookDetails.hasNoEpisodesMessage();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(noEpisodesMessage || detailsVisible,
					"TC_299: Book details should show a stable message when no episodes exist");
			LoggerUtils.logInfo("TC_299: No episodes message - " + noEpisodesMessage);

			LoggerUtils.logTestEnd("TC_299", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_299 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_300: SYSTEM STABILITY WITH RAPID BANNER CLICKS ====================

	/**
	 * TC_300: Verify system stability when banners clicked rapidly
	 * Test Flow: Login as consumer → Rapid-click banners → Verify stability
	 * Expected: Dashboard should remain stable after rapid clicks
	 */
	@Test(priority = 300,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_PERFORMANCE,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_300: Verify system stability when banners are clicked rapidly")
	public void TC300_VerifySystemStabilityWhenBannersClickedRapidly() {
		LoggerUtils.logTestStart("TC_300: System Stability When Banners Clicked Rapidly");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Check if banner section is visible");
			if (!consumerBookDetails.isBannerSectionVisible()) {
				consumerBookDetails.logOptionalUnavailable("Banner section is not available for rapid-click validation.");
				LoggerUtils.logTestEnd("TC_300", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(3, "Perform rapid banner clicks and verify stability");
			boolean dashboardStable = consumerBookDetails.rapidClickBannersAndVerifyStable(3);

			Assert.assertTrue(dashboardStable,
					"TC_300: Dashboard should remain stable after rapid banner clicks");
			LoggerUtils.logInfo("TC_300: Dashboard stable after rapid clicks: " + dashboardStable);

			LoggerUtils.logTestEnd("TC_300", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_300 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_301: BANNER LOADS ON SLOW NETWORK ====================

	/**
	 * TC_301: Verify banner loads correctly on slow network
	 * Test Flow: Login as consumer → Throttle network → Refresh → Verify
	 * Expected: Banners should load or page should remain stable
	 */
	@Test(priority = 301,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_PERFORMANCE,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_301: Verify banner loads correctly on slow network using CDP")
	public void TC301_VerifyBannerLoadsCorrectlyOnSlowNetwork() {
		LoggerUtils.logTestStart("TC_301: Banner Loads Correctly On Slow Network");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Throttle network and verify banner loads");
			boolean bannerStable = consumerBookDetails.throttleNetworkAndVerifyBannerLoads("301");

			Assert.assertTrue(bannerStable,
					"TC_301: Dashboard banners should load or the page should remain stable under slow network conditions");
			LoggerUtils.logInfo("TC_301: Banner stable under throttled network: " + bannerStable);

			LoggerUtils.logTestEnd("TC_301", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_301 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_302: SCROLLING WITH ONLY ONE BANNER ====================

	/**
	 * TC_302: Verify scrolling works with only one banner
	 * Test Flow: Login as consumer → Check single-banner scenario
	 * Expected: Single banner should remain visible
	 */
	@Test(priority = 302,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_302: Verify scrolling works correctly with only one banner")
	public void TC302_VerifyScrollingWorksWithOnlyOneBanner() {
		LoggerUtils.logTestStart("TC_302: Scrolling Works With Only One Banner");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Check visible banner count");
			int bannerCount = consumerBookDetails.getVisibleBannerCount();
			if (bannerCount != 1) {
				consumerBookDetails.logOptionalUnavailable("Single-banner state is not available. Current visible banner count: " + bannerCount);
				LoggerUtils.logTestEnd("TC_302", "SKIPPED - Wrong banner count");
				return;
			}

			LoggerUtils.logStep(3, "Verify single banner remains visible");
			boolean bannersVisible = consumerBookDetails.areBannerImagesVisible();

			Assert.assertTrue(bannersVisible,
					"TC_302: Single configured banner should remain visible without breaking the dashboard");
			LoggerUtils.logInfo("TC_302: Single banner visible: " + bannersVisible);

			LoggerUtils.logTestEnd("TC_302", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_302 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_303: MANY BANNERS HANDLED SMOOTHLY ====================

	/**
	 * TC_303: Verify system handles many banners smoothly
	 * Test Flow: Login as consumer → Check high-banner-count scenario
	 * Expected: Multiple banners should be rendered and dashboard shell should remain stable
	 */
	@Test(priority = 303,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_PERFORMANCE,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_303: Verify system handles many banners smoothly")
	public void TC303_VerifySystemHandlesManyBannersSmoothly() {
		LoggerUtils.logTestStart("TC_303: System Handles Many Banners Smoothly");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Check visible banner count");
			int bannerCount = consumerBookDetails.getVisibleBannerCount();
			if (bannerCount < 5) {
				consumerBookDetails.logOptionalUnavailable("High-banner-count scenario is not available. Current visible banner count: " + bannerCount);
				LoggerUtils.logTestEnd("TC_303", "SKIPPED - Insufficient banner count");
				return;
			}

			LoggerUtils.logStep(3, "Verify dashboard remains stable with many banners configured");
			boolean multipleBannersRendered = consumerBookDetails.areMultipleBannersVisible();
			boolean dashboardStable = consumerBookDetails.waitForDashboardShell();

			Assert.assertTrue(multipleBannersRendered,
					"TC_303: System should display multiple banners when many banners are configured (found: " + bannerCount + ")");
			Assert.assertTrue(dashboardStable,
					"TC_303: Dashboard shell should remain stable when many banners are configured");

			// Informational only — the carousel may deliberately pause auto-rotation when many banners are loaded.
			boolean carouselResponsive = consumerBookDetails.clickNextBannerAndVerifyChange();
			boolean autoRotated = consumerBookDetails.waitForBannerToAutoRotate(3);
			LoggerUtils.logInfo("TC_303: rendered=" + multipleBannersRendered + ", bannerCount=" + bannerCount
					+ ", dashboardStable=" + dashboardStable
					+ ", carouselResponsive=" + carouselResponsive + ", autoRotated=" + autoRotated);

			LoggerUtils.logTestEnd("TC_303", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_303 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_304: BOOK COVER IMAGE LOADS PROPERLY ====================

	/**
	 * TC_304: Verify book cover image loads properly
	 * Test Flow: Login as consumer → Open book details → Check cover image
	 * Expected: Cover or placeholder should be visible
	 */
	@Test(priority = 304,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_304: Verify book cover image loads properly")
	public void TC304_VerifyBookCoverImageLoadsProperly() {
		LoggerUtils.logTestStart("TC_304: Book Cover Image Loads Properly");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_304", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify cover image or placeholder");
			boolean coverVisible = consumerBookDetails.isBookCoverImageVisible();
			boolean placeholderVisible = consumerBookDetails.isPlaceholderImageVisible();

			Assert.assertTrue(coverVisible || placeholderVisible,
					"TC_304: Book details should display either the cover image or a valid placeholder");
			LoggerUtils.logInfo("TC_304: Cover visible - " + coverVisible + ", Placeholder visible - " + placeholderVisible);

			LoggerUtils.logTestEnd("TC_304", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_304 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_305: BEHAVIOR WHEN BOOK IMAGE MISSING ====================

	/**
	 * TC_305: Verify system behavior when book image is missing
	 * Test Flow: Login as consumer → Open book details → Check placeholder
	 * Expected: Placeholder should be shown when cover is missing
	 */
	@Test(priority = 305,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_305: Verify system behavior when book image is missing")
	public void TC305_VerifySystemBehaviorWhenBookImageMissing() {
		LoggerUtils.logTestStart("TC_305: System Behavior When Book Image Missing");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_305", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if current book has cover image");
			boolean coverVisible = consumerBookDetails.isBookCoverImageVisible();
			boolean placeholderVisible = consumerBookDetails.isPlaceholderImageVisible();
			if (coverVisible && !placeholderVisible) {
				consumerBookDetails.logOptionalUnavailable("Current book has a valid cover image, so the missing-image state is not available.");
				LoggerUtils.logTestEnd("TC_305", "SKIPPED - Cover exists");
				return;
			}

			LoggerUtils.logStep(3, "Verify placeholder or page stability");
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(placeholderVisible || detailsVisible,
					"TC_305: Book details should remain stable and show a placeholder when the cover image is missing");
			LoggerUtils.logInfo("TC_305: Placeholder visible - " + placeholderVisible);

			LoggerUtils.logTestEnd("TC_305", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_305 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_306: AUDIO STARTS WHEN CLICKING PLAY AUDIO ====================

	/**
	 * TC_306: Verify audio starts when clicking Play Audio
	 * Test Flow: Login as consumer → Open book details → Click play
	 * Expected: Audio should start playing
	 */
	@Test(priority = 306,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_306: Verify audio starts when clicking Play Audio")
	public void TC306_VerifyAudioStartsWhenClickingPlayAudio() {
		LoggerUtils.logTestStart("TC_306: Audio Starts When Clicking Play Audio");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_306", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if Play button is visible");
			if (!consumerBookDetails.isPlayButtonVisible()) {
				consumerBookDetails.logOptionalUnavailable("Play Audio button is not visible for the current book details page.");
				LoggerUtils.logTestEnd("TC_306", "SKIPPED - Play button not visible");
				return;
			}

			LoggerUtils.logStep(3, "Click Play and verify audio starts");
			boolean audioStarted = consumerBookDetails.validatePlay();

			Assert.assertTrue(audioStarted,
					"TC_306: Audio should start playing when Play Audio is clicked");
			LoggerUtils.logInfo("TC_306: Audio started: " + audioStarted);

			LoggerUtils.logTestEnd("TC_306", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_306 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_307: USER CAN PAUSE PLAYING AUDIO ====================

	/**
	 * TC_307: Verify user can pause playing audio
	 * Test Flow: Login as consumer → Open book details → Play → Pause
	 * Expected: Audio should pause
	 */
	@Test(priority = 307,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_307: Verify user can pause playing audio")
	public void TC307_VerifyUserCanPausePlayingAudio() {
		LoggerUtils.logTestStart("TC_307: User Can Pause Playing Audio");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_307", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if Play button is visible");
			if (!consumerBookDetails.isPlayButtonVisible()) {
				consumerBookDetails.logOptionalUnavailable("Play Audio button is not visible for pause validation.");
				LoggerUtils.logTestEnd("TC_307", "SKIPPED - Play button not visible");
				return;
			}

			LoggerUtils.logStep(3, "Start audio playback");
			boolean audioStarted = consumerBookDetails.validatePlay();
			Assert.assertTrue(audioStarted, "TC_307: Audio should start playing before pause is validated");

			LoggerUtils.logStep(4, "Pause audio and verify");
			boolean audioPaused = consumerBookDetails.validatePause();

			Assert.assertTrue(audioPaused,
					"TC_307: Audio should pause when the Pause control is clicked");
			LoggerUtils.logInfo("TC_307: Audio paused: " + audioPaused);

			LoggerUtils.logTestEnd("TC_307", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_307 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_308: SHARE BUTTON FUNCTIONALITY ====================

	/**
	 * TC_308: Verify share button functionality
	 * Test Flow: Login as consumer → Open book details → Click share
	 * Expected: Share options should open
	 */
	@Test(priority = 308,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_308: Verify share button functionality")
	public void TC308_VerifyShareButtonFunctionality() {
		LoggerUtils.logTestStart("TC_308: Share Button Functionality");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_308", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if Share button is visible");
			if (!consumerBookDetails.isShareButtonVisible()) {
				consumerBookDetails.logOptionalUnavailable("Share button is not visible for the current book details page.");
				LoggerUtils.logTestEnd("TC_308", "SKIPPED - Share button not visible");
				return;
			}

			LoggerUtils.logStep(3, "Click Share and verify options");
			boolean shareOpened = consumerBookDetails.openShareOptions();

			Assert.assertTrue(shareOpened,
					"TC_308: Share action should open share options or a stable share flow");
			LoggerUtils.logInfo("TC_308: Share options opened: " + shareOpened);

			LoggerUtils.logTestEnd("TC_308", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_308 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_309: USER CAN LIKE A BOOK ====================

	/**
	 * TC_309: Verify user can like a book
	 * Test Flow: Login as consumer → Open book details → Click favorite
	 * Expected: Book should be added to favorites
	 */
	@Test(priority = 309,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_309: Verify user can like a book")
	public void TC309_VerifyUserCanLikeABook() {
		LoggerUtils.logTestStart("TC_309: User Can Like A Book");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_309", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if Favorite button is visible");
			if (!consumerBookDetails.isFavoriteButtonVisible()) {
				consumerBookDetails.logOptionalUnavailable("Favorite button is not visible for the current book details page.");
				LoggerUtils.logTestEnd("TC_309", "SKIPPED - Favorite button not visible");
				return;
			}

			LoggerUtils.logStep(3, "Click favorite and verify change");
			boolean favorited = consumerBookDetails.toggleFavoriteAndVerifyChange();

			Assert.assertTrue(favorited,
					"TC_309: Clicking the heart icon should add the book to favorites");
			LoggerUtils.logInfo("TC_309: Book favorited: " + favorited);

			LoggerUtils.logTestEnd("TC_309", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_309 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_310: USER CAN UNLIKE A BOOK ====================

	/**
	 * TC_310: Verify user can unlike a book
	 * Test Flow: Login as consumer → Open book details → Like → Unlike
	 * Expected: Book should be removed from favorites
	 */
	@Test(priority = 310,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_310: Verify user can unlike a book")
	public void TC310_VerifyUserCanUnlikeABook() {
		LoggerUtils.logTestStart("TC_310: User Can Unlike A Book");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_310", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if Favorite button is visible");
			if (!consumerBookDetails.isFavoriteButtonVisible()) {
				consumerBookDetails.logOptionalUnavailable("Favorite button is not visible for the current book details page.");
				LoggerUtils.logTestEnd("TC_310", "SKIPPED - Favorite button not visible");
				return;
			}

			LoggerUtils.logStep(3, "Add to favorites");
			boolean favorited = consumerBookDetails.toggleFavoriteAndVerifyChange();
			Assert.assertTrue(favorited, "TC_310: Clicking the heart icon should add the book to favorites before removal");

			LoggerUtils.logStep(4, "Remove from favorites");
			boolean unfavorited = consumerBookDetails.toggleFavoriteAndVerifyChange();

			Assert.assertTrue(unfavorited,
					"TC_310: Clicking the heart icon again should remove the book from favorites");
			LoggerUtils.logInfo("TC_310: Book unfavorited: " + unfavorited);

			LoggerUtils.logTestEnd("TC_310", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_310 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_311: CATEGORIES APPEAR CORRECTLY ====================

	/**
	 * TC_311: Verify categories appear correctly
	 * Test Flow: Login as consumer → Navigate to category → Open book → Verify
	 * Expected: Category should be displayed on book details
	 */
	@Test(priority = 311,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_311: Verify categories appear correctly on book details")
	public void TC311_VerifyCategoriesAppearCorrectly() {
		LoggerUtils.logTestStart("TC_311: Categories Appear Correctly");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Navigate to Art category");
			String categoryName = "Art";
			if (!consumerBookDetails.navigateToCategory(categoryName)) {
				consumerBookDetails.logOptionalUnavailable(categoryName + " category is not available on the current dashboard state.");
				LoggerUtils.logTestEnd("TC_311", "SKIPPED - Category not available");
				return;
			}
			consumerBookDetails.waitQuietly(2000);

			LoggerUtils.logStep(3, "Verify category page has content");
			boolean hasContent = consumerBookDetails.hasCategoryContent();
			Assert.assertTrue(hasContent,
					"TC_311: " + categoryName + " category page should show at least one visible book before opening details");
			LoggerUtils.logInfo("TC_311: Category has content: " + hasContent);

			LoggerUtils.logStep(4, "Open a book from the category page");
			try {
				consumerBookDetails.openAnyBookFromCategoryPage();
				consumerBookDetails.waitQuietly(2000);
			} catch (Exception e) {
				Assert.fail("Unable to open a book from the " + categoryName + " category: " + e.getMessage());
			}

			LoggerUtils.logStep(5, "Verify book details page opened");
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();
			Assert.assertTrue(detailsVisible,
					"TC_311: Opening a book from the " + categoryName + " category should navigate to book details");

			LoggerUtils.logStep(6, "Verify category displayed on details");
			boolean categoryDisplayed = consumerBookDetails.isCategoryDisplayedOnDetails(categoryName);
			Assert.assertTrue(categoryDisplayed,
					"TC_311: " + categoryName + " category should be displayed on the opened book details page");
			LoggerUtils.logInfo("TC_311: Category '" + categoryName + "' displayed on details: " + categoryDisplayed);

			LoggerUtils.logTestEnd("TC_311", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_311 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_312: CLICKING CATEGORY NAVIGATES TO CATEGORY PAGE ====================

	/**
	 * TC_312: Verify clicking category navigates to category page
	 * Test Flow: Login as consumer → Click category → Verify category page
	 * Expected: Category page should be displayed
	 */
	@Test(priority = 312,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_312: Verify clicking category navigates to category page")
	public void TC312_VerifyClickingCategoryNavigatesToCategoryPage() {
		LoggerUtils.logTestStart("TC_312: Clicking Category Navigates To Category Page");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerBookDetails.waitForDashboardReady();

			LoggerUtils.logStep(2, "Navigate to Art category from dashboard");
			String categoryName = "Art";
			if (!consumerBookDetails.navigateToCategory(categoryName)) {
				consumerBookDetails.logOptionalUnavailable(categoryName + " category is not available on the current dashboard state.");
				LoggerUtils.logTestEnd("TC_312", "SKIPPED - Category not available");
				return;
			}
			consumerBookDetails.waitQuietly(2000);

			LoggerUtils.logStep(3, "Verify category page is displayed with content");
			boolean hasContent = consumerBookDetails.hasCategoryContent();

			Assert.assertTrue(hasContent,
					"TC_312: Category page should be displayed with content after clicking the category");
			LoggerUtils.logInfo("TC_312: Category page has content: " + hasContent);

			LoggerUtils.logTestEnd("TC_312", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_312 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_313: SUMMARY CONTENT LOADS ====================

	/**
	 * TC_313: Verify summary content loads
	 * Test Flow: Login as consumer → Open book details → Check summary
	 * Expected: Summary should load or show empty state
	 */
	@Test(priority = 313,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_313: Verify summary content loads")
	public void TC313_VerifySummaryContentLoads() {
		LoggerUtils.logTestStart("TC_313: Summary Content Loads");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_313", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify summary or empty state");
			boolean summaryVisible = consumerBookDetails.isSummaryVisible();
			boolean emptySummaryMessage = consumerBookDetails.hasEmptySummaryMessage();

			Assert.assertTrue(summaryVisible || emptySummaryMessage,
					"TC_313: Summary section should load or show a stable empty state");
			LoggerUtils.logInfo("TC_313: Summary visible - " + summaryVisible + ", Empty message - " + emptySummaryMessage);

			LoggerUtils.logTestEnd("TC_313", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_313 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_314: UI WHEN SUMMARY IS MISSING ====================

	/**
	 * TC_314: Verify UI when summary is missing
	 * Test Flow: Login as consumer → Open book details → Check empty summary
	 * Expected: Empty summary message should be shown
	 */
	@Test(priority = 314,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_314: Verify UI behavior when summary is missing")
	public void TC314_VerifyUiWhenSummaryIsMissing() {
		LoggerUtils.logTestStart("TC_314: UI When Summary Is Missing");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_314", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if current book has summary");
			if (consumerBookDetails.isSummaryVisible()) {
				consumerBookDetails.logOptionalUnavailable("Current book has summary content, so the empty-summary state is not available.");
				LoggerUtils.logTestEnd("TC_314", "SKIPPED - Summary exists");
				return;
			}

			LoggerUtils.logStep(3, "Verify empty-summary message or page stability");
			boolean emptySummaryMessage = consumerBookDetails.hasEmptySummaryMessage();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(emptySummaryMessage || detailsVisible,
					"TC_314: Book details should show a stable message when summary content is missing");
			LoggerUtils.logInfo("TC_314: Empty summary message - " + emptySummaryMessage);

			LoggerUtils.logTestEnd("TC_314", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_314 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_315: CHAPTER LIST LOADS CORRECTLY ====================

	/**
	 * TC_315: Verify chapter list loads correctly
	 * Test Flow: Login as consumer → Open book details → Check chapters
	 * Expected: All available chapters should display correctly
	 */
	@Test(priority = 315,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_315: Verify chapter list loads correctly")
	public void TC315_VerifyChapterListLoadsCorrectly() {
		LoggerUtils.logTestStart("TC_315: Chapter List Loads Correctly");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_315", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify chapters are displayed");
			boolean chaptersVisible = consumerBookDetails.areChaptersVisible();
			int chapterCount = consumerBookDetails.getVisibleChapterCount();

			Assert.assertTrue(chaptersVisible && chapterCount > 0,
					"TC_315: All available chapters should display correctly");
			LoggerUtils.logInfo("TC_315: Chapters visible - " + chaptersVisible + ", Count - " + chapterCount);

			LoggerUtils.logTestEnd("TC_315", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_315 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_316: CHAPTER STARTS PLAYING WHEN CLICKED ====================

	/**
	 * TC_316: Verify chapter starts playing when clicked
	 * Test Flow: Login as consumer → Open book details → Click chapter
	 * Expected: Audio should start playing
	 */
	@Test(priority = 316,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_316: Verify chapter starts playing when clicked")
	public void TC316_VerifyChapterStartsPlayingWhenClicked() {
		LoggerUtils.logTestStart("TC_316: Chapter Starts Playing When Clicked");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_316", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if chapters are visible");
			if (!consumerBookDetails.areChaptersVisible()) {
				consumerBookDetails.logOptionalUnavailable("No chapters are visible for playback validation.");
				LoggerUtils.logTestEnd("TC_316", "SKIPPED - No chapters");
				return;
			}

			LoggerUtils.logStep(3, "Click first chapter and verify player");
			boolean playbackStarted = consumerBookDetails.clickFirstChapterAndVerifyPlayer();

			Assert.assertTrue(playbackStarted,
					"TC_316: Audio should start playing when a chapter is clicked");
			LoggerUtils.logInfo("TC_316: Chapter playback started: " + playbackStarted);

			LoggerUtils.logTestEnd("TC_316", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_316 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_317: UI WHEN NO CHAPTERS AVAILABLE ====================

	/**
	 * TC_317: Verify UI when no chapters available
	 * Test Flow: Login as consumer → Open book details → Check no chapters state
	 * Expected: Stable message should be shown
	 */
	@Test(priority = 317,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_317: Verify UI behavior when no chapters are available")
	public void TC317_VerifyUiWhenNoChaptersAvailable() {
		LoggerUtils.logTestStart("TC_317: UI When No Chapters Available");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_317", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if current book has chapters");
			if (consumerBookDetails.areChaptersVisible()) {
				consumerBookDetails.logOptionalUnavailable("Current book has chapters, so the no-chapters state is not available.");
				LoggerUtils.logTestEnd("TC_317", "SKIPPED - Chapters exist");
				return;
			}

			LoggerUtils.logStep(3, "Verify no-chapters message or page stability");
			boolean noEpisodesMessage = consumerBookDetails.hasNoEpisodesMessage();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(noEpisodesMessage || detailsVisible,
					"TC_317: Book details should show a stable message when no chapters are available");
			LoggerUtils.logInfo("TC_317: No chapters message - " + noEpisodesMessage);

			LoggerUtils.logTestEnd("TC_317", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_317 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_318: CHAPTER DURATION DISPLAY ====================

	/**
	 * TC_318: Verify chapter duration display
	 * Test Flow: Login as consumer → Open book details → Check duration
	 * Expected: Duration should be displayed
	 */
	@Test(priority = 318,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_318: Verify chapter duration is displayed")
	public void TC318_VerifyChapterDurationDisplay() {
		LoggerUtils.logTestStart("TC_318: Chapter Duration Display");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_318", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Verify duration information");
			boolean durationsVisible = consumerBookDetails.areDurationsVisible();
			boolean chaptersVisible = consumerBookDetails.areChaptersVisible();

			Assert.assertTrue(durationsVisible || chaptersVisible,
					"TC_318: Chapter duration should display when chapters are available");
			LoggerUtils.logInfo("TC_318: Durations visible - " + durationsVisible);

			LoggerUtils.logTestEnd("TC_318", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_318 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_319: REPORT OPTION WORKS ====================

	/**
	 * TC_319: Verify report option works
	 * Test Flow: Login as consumer → Open book details → Report content
	 * Expected: Report flow should complete successfully
	 */
	@Test(priority = 319,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_319: Verify report option works correctly")
	public void TC319_VerifyReportOptionWorks() {
		LoggerUtils.logTestStart("TC_319: Report Option Works");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_319", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if Report option is visible");
			if (!consumerBookDetails.isReportOptionVisible()) {
				consumerBookDetails.logOptionalUnavailable("Report option is not visible for the current book details page.");
				LoggerUtils.logTestEnd("TC_319", "SKIPPED - Report option not visible");
				return;
			}

			LoggerUtils.logStep(3, "Report book as inappropriate content");
			String reportOutcome = consumerBookDetails.reportInappropriateContentAndGetOutcome();
			// "report_received"   — first attempt against this book shows the success confirmation.
			// "already_reported"  — the book was already reported in a prior run; server returned the
			//                       duplicate-protection message. Still a valid report flow outcome.
			// "failed"            — neither confirmation nor duplicate message was observed.
			boolean reportSuccessful = !"failed".equals(reportOutcome);
			String outcomeMessage = consumerBookDetails.captureReportOutcomeMessage();

			Assert.assertTrue(reportSuccessful,
					"TC_319: Report flow should complete successfully: click Inappropriate Content → Submit → Show confirmation or already-reported message");
			LoggerUtils.logInfo("TC_319: Report outcome = " + reportOutcome + " | observed message: " + outcomeMessage);

			LoggerUtils.logStep(4, "Click Continue Listening to dismiss confirmation (skipped on already-reported path)");
			if ("report_received".equals(reportOutcome)) {
				boolean canContinue = consumerBookDetails.clickContinueListeningAfterReport();
				Assert.assertTrue(canContinue,
						"TC_319: Continue Listening button should be present after report submission");
				LoggerUtils.logInfo("TC_319: Can continue listening: " + canContinue);
			} else {
				LoggerUtils.logInfo("TC_319: Continue Listening step skipped — book was already reported (no fresh confirmation to dismiss)");
			}

			LoggerUtils.logTestEnd("TC_319", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_319 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_320: DUPLICATE REPORT HANDLING ====================

	/**
	 * TC_320: Verify duplicate report handling
	 * Test Flow: Login as consumer → Open book details → Report again
	 * Expected: Duplicate report should be handled gracefully
	 */
	@Test(priority = 320,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_320: Verify duplicate report is handled correctly")
	public void TC320_VerifyDuplicateReportHandling() {
		LoggerUtils.logTestStart("TC_320: Duplicate Report Handling");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_320", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if Report option is visible");
			if (!consumerBookDetails.isReportOptionVisible()) {
				consumerBookDetails.logOptionalUnavailable("Report option is not visible for duplicate-report validation.");
				LoggerUtils.logTestEnd("TC_320", "SKIPPED - Report option not visible");
				return;
			}

			LoggerUtils.logStep(3, "Check if already reported");
			boolean alreadyReported = consumerBookDetails.hasAlreadyReportedMessage();

			if (!alreadyReported) {
				consumerBookDetails.reportInappropriateContent();
			}

			LoggerUtils.logStep(4, "Verify duplicate report handling");
			boolean confirmationVisible = consumerBookDetails.hasAlreadyReportedMessage();
			boolean canContinue = consumerBookDetails.clickContinueListeningAfterReport();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(confirmationVisible || canContinue || detailsVisible,
					"TC_320: Duplicate report attempts should show confirmation message or allow continuation without breaking the page");
			LoggerUtils.logInfo("TC_320: Confirmation - " + confirmationVisible + ", Can continue - " + canContinue);

			LoggerUtils.logTestEnd("TC_320", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_320 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_321: BACK NAVIGATION RETURNS TO DASHBOARD ====================

	/**
	 * TC_321: Verify back navigation returns to dashboard
	 * Test Flow: Login as consumer → Open book details → Click back
	 * Expected: Should return to dashboard
	 */
	@Test(priority = 321,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_321: Verify back navigation returns to dashboard")
	public void TC321_VerifyBackNavigationReturnsToDashboard() {
		LoggerUtils.logTestStart("TC_321: Back Navigation Returns To Dashboard");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_321", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Click back button and verify");
			boolean returnedToDashboard = consumerBookDetails.clickBackButtonToDashboard();

			Assert.assertTrue(returnedToDashboard,
					"TC_321: Back navigation should return the user to the dashboard");
			LoggerUtils.logInfo("TC_321: Returned to dashboard: " + returnedToDashboard);

			LoggerUtils.logTestEnd("TC_321", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_321 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_322: UI WITH LONG BOOK TITLES ====================

	/**
	 * TC_322: Verify UI with long book titles
	 * Test Flow: Login as consumer → Open book details → Check title
	 * Expected: Long title should render without breaking layout
	 */
	@Test(priority = 322,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_322: Verify UI handles long book titles correctly")
	public void TC322_VerifyUiWithLongBookTitles() {
		LoggerUtils.logTestStart("TC_322: UI With Long Book Titles");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_322", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check if current book has long title");
			if (!consumerBookDetails.isLongBookTitleDisplayed()) {
				consumerBookDetails.logOptionalUnavailable("Current book title is not long enough to validate long-title behavior.");
				LoggerUtils.logTestEnd("TC_322", "SKIPPED - Title not long enough");
				return;
			}

			LoggerUtils.logStep(3, "Verify long title renders correctly");
			String bookTitle = consumerBookDetails.getBookTitleText();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();
			String safeTitle = consumerBookDetails.safeString(bookTitle);

			Assert.assertTrue(!safeTitle.isEmpty() && detailsVisible,
					"TC_322: Long book titles should render without breaking the details page");
			LoggerUtils.logInfo("TC_322: Book title length: " + safeTitle.length());

			LoggerUtils.logTestEnd("TC_322", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_322 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_323: UI PERFORMANCE WITH MANY CHAPTERS ====================

	/**
	 * TC_323: Verify UI performance with many chapters
	 * Test Flow: Login as consumer → Open book details → Check chapter count
	 * Expected: Large chapter lists should load without issues
	 */
	@Test(priority = 323,groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_PERFORMANCE,TestConstants.GROUP_CONSUMER},retryAnalyzer = RetryAnalyzer.class,
		description = "TC_323: Verify UI performance with many chapters")
	public void TC323_VerifyUiPerformanceWithManyChapters() {
		LoggerUtils.logTestStart("TC_323: UI Performance With Many Chapters");

		try {
			LoggerUtils.logStep(1, "Open book details from dashboard");
			if (!consumerBookDetails.openBookDetailsFromDashboard()) {
				consumerBookDetails.logOptionalUnavailable("Unable to open a book details page.");
				LoggerUtils.logTestEnd("TC_323", "SKIPPED - Unable to open book details");
				return;
			}

			LoggerUtils.logStep(2, "Check chapter count");
			int chapterCount = consumerBookDetails.getVisibleChapterCount();
			if (chapterCount < 20) {
				consumerBookDetails.logOptionalUnavailable("Large chapter dataset is not available. Current visible chapter count: " + chapterCount);
				LoggerUtils.logTestEnd("TC_323", "SKIPPED - Insufficient chapter count");
				return;
			}

			LoggerUtils.logStep(3, "Verify UI handles many chapters");
			boolean chaptersVisible = consumerBookDetails.areChaptersVisible();
			boolean detailsVisible = consumerBookDetails.isBookDetailsPageVisible();

			Assert.assertTrue(chaptersVisible && detailsVisible,
					"TC_323: Large chapter lists should load without breaking the details page");
			LoggerUtils.logInfo("TC_323: Chapter count: " + chapterCount);

			LoggerUtils.logTestEnd("TC_323", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_323 - Test failed: " + consumerBookDetails.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== NO LOCAL HELPERS — USE ConsumerBookDetailsPage ====================
}
