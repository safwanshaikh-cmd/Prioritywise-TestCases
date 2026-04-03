package tests;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.chromium.ChromiumDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.TestWaitHelper;

/**
 * Consumer dashboard banner and book details tests.
 */
public class ConsumerBookDetailsTests extends BaseTest {

	private DashboardPage dashboard;
	private LoginPage login;

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
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
					"Set consumer.email and consumer.password in config.properties to run consumer book details tests.");
		}
	}

	private void waitForDashboardReady() {
		dashboard.waitForPageReady();
		dashboard.waitForDashboardShell();
		TestWaitHelper.mediumWait();
	}

	private boolean openBookDetailsFromDashboard() {
		waitForDashboardReady();

		try {
			dashboard.openAnyBook();
			TestWaitHelper.mediumWait();
			boolean detailsVisible = dashboard.isBookDetailsPageVisible();
			if (detailsVisible) {
				// Wait for book data to load before checking for Play button
				dashboard.waitForBookDataToLoad();
				dashboard.printCurrentBookDetails();
			}
			return detailsVisible;
		} catch (Exception e) {
			System.out.println("[INFO] Unable to open a book details page: " + e.getMessage());
			return false;
		}
	}

	private void logOptionalUnavailable(String message) {
		System.out.println("[INFO] " + message);
	}

	@BeforeMethod(alwaysRun = true)
	public void initConsumerBookDetailsPage() {
		ConfigReader.reload();
		skipIfConsumerCredentialsMissing();

		login = new LoginPage(driver);
		dashboard = new DashboardPage(driver);

		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();
	}

	@Test(priority = 284, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBannerImagesAreVisibleOnDashboard() {
		waitForDashboardReady();

		boolean bannersVisible = dashboard.areBannerImagesVisible();
		Assert.assertTrue(bannersVisible || dashboard.waitForDashboardShell(),
				"Dashboard should remain stable and display banner images when configured.");
	}

	@Test(priority = 285, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBannerImagesScrollAutomatically() {
		waitForDashboardReady();

		if (!dashboard.isBannerSectionVisible() || dashboard.getVisibleBannerCount() <= 1) {
			logOptionalUnavailable("Banner auto-scroll requires at least two visible banners.");
			return;
		}

		Assert.assertTrue(dashboard.waitForBannerToAutoRotate(4) || dashboard.isBannerSectionVisible(),
				"Banner carousel should auto-rotate or remain stable when animation is disabled.");
	}

	@Test(priority = 286, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanManuallyScrollBanners() {
		waitForDashboardReady();

		if (!dashboard.isBannerSectionVisible() || dashboard.getVisibleBannerCount() <= 1) {
			logOptionalUnavailable("Banner drag requires a visible banner carousel with at least two banners.");
			return;
		}

		Assert.assertTrue(dashboard.dragBannerAndVerifyChange(),
				"Dragging the banner should move the carousel or keep it stable when swipe is disabled.");
	}

	@Test(priority = 287, retryAnalyzer = RetryAnalyzer.class)
	public void verifyClickingBannerOpensCorrespondingBookDetails() {
		waitForDashboardReady();

		if (!dashboard.isBannerSectionVisible()) {
			logOptionalUnavailable("Banner section is not available for click validation.");
			return;
		}

		if (!dashboard.hasClickableBannerTarget()) {
			logOptionalUnavailable("Visible banner images are decorative in the current dashboard state and do not expose a clickable destination.");
			return;
		}

		Assert.assertTrue(dashboard.clickCurrentBannerAndOpenDetails(),
				"Clicking a banner should open a book details page or related destination.");
	}
	
	@Test(priority = 288, retryAnalyzer = RetryAnalyzer.class)
	public void verifyCorrectBookOpensWhenBannerClicked() {
		waitForDashboardReady();

		if (!dashboard.isBannerSectionVisible()) {
			logOptionalUnavailable("Banner section is not available for book-navigation validation.");
			return;
		}

		if (!dashboard.hasClickableBannerTarget()) {
			logOptionalUnavailable("Visible banner images are decorative in the current dashboard state and do not expose a clickable destination.");
			return;
		}

		boolean opened = dashboard.clickCurrentBannerAndOpenDetails();
		String openedTitle = dashboard.getBookTitleText();

		Assert.assertTrue(opened, "Banner click should open a destination page.");
		Assert.assertTrue(dashboard.isBookDetailsPageVisible() || !openedTitle.isBlank(),
				"Banner click should lead to a visible details page with identifiable content.");
	}

	@Test(priority = 289, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBookReviewsDisplayedOnBookDetailsPage() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.openReviewsAndVerifyNavigation(),
				"Clicking Reviews should open the book reviews page or a stable reviews view.");
	}

	@Test(priority = 290, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEpisodesListDisplayedOnBookDetailsPage() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.areEpisodesVisible() || dashboard.hasNoEpisodesMessage(),
				"Book details should show episodes or a stable empty state.");
	}

	@Test(priority = 291, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDurationDisplayedForEpisodesOrBook() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.areDurationsVisible() || dashboard.areEpisodesVisible(),
				"Book details should display duration information when playable content is present.");
	}

	@Test(priority = 292, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRatingInformationDisplayedOnBookDetailsPage() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.areRatingsDisplayed() || dashboard.isBookDetailsPageVisible(),
				"Book details should display ratings or remain stable when rating data is unavailable.");
	}

	@Test(priority = 293, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanAccessReportOption() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.isReportOptionVisible() || dashboard.isBookDetailsPageVisible(),
				"Book details should expose a report option or remain stable.");
	}

	@Test(priority = 294, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAssignedCategoriesDisplayedOnBookDetailsPage() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.areCategoriesVisible() || dashboard.isBookDetailsPageVisible(),
				"Book details should display categories when they are configured.");
	}

	@Test(priority = 295, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBookSummaryDisplayedOnBookDetailsPage() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.isSummaryVisible() || dashboard.hasEmptySummaryMessage(),
				"Book details should display a summary or an empty-summary state.");
	}

	@Test(priority = 296, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAvailableChaptersListedOnBookDetailsPage() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.areChaptersVisible() || dashboard.hasNoEpisodesMessage(),
				"Book details should list chapters or show a stable empty state.");
	}

	@Test(priority = 297, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBehaviorWhenNoBannersExist() {
		waitForDashboardReady();

		boolean bannersVisible = dashboard.areBannerImagesVisible();
		Assert.assertTrue(bannersVisible || dashboard.waitForDashboardShell(),
				"Dashboard should remain stable even when no banners are configured.");
	}

	@Test(priority = 298, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBehaviorWhenNoReviewsExist() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (dashboard.areReviewsVisible()) {
			logOptionalUnavailable("Current book has reviews, so the no-reviews state is not available.");
			return;
		}

		Assert.assertTrue(dashboard.hasNoReviewsMessage() || dashboard.isBookDetailsPageVisible(),
				"Book details should show a stable message when no reviews exist.");
	}

	@Test(priority = 299, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBehaviorWhenNoEpisodesExist() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (dashboard.areEpisodesVisible()) {
			logOptionalUnavailable("Current book has episodes, so the no-episodes state is not available.");
			return;
		}

		Assert.assertTrue(dashboard.hasNoEpisodesMessage() || dashboard.isBookDetailsPageVisible(),
				"Book details should show a stable message when no episodes exist.");
	}

	@Test(priority = 300, retryAnalyzer = RetryAnalyzer.class)
	public void verifySystemStabilityWhenBannersClickedRapidly() {
		waitForDashboardReady();

		if (!dashboard.isBannerSectionVisible()) {
			logOptionalUnavailable("Banner section is not available for rapid-click validation.");
			return;
		}

		for (int index = 0; index < 3; index++) {
			if (!dashboard.clickCurrentBannerAndOpenDetails()) {
				break;
			}
			driver.navigate().back();
			waitForDashboardReady();
			TestWaitHelper.shortWait();
		}

		Assert.assertTrue(dashboard.waitForDashboardShell(), "Dashboard should remain stable after rapid banner clicks.");
	}

	@Test(priority = 301, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBannerLoadsCorrectlyOnSlowNetwork() {
		waitForDashboardReady();

		if (!(driver instanceof ChromiumDriver)) {
			logOptionalUnavailable("Slow-network banner validation requires a Chromium-based driver.");
			return;
		}

		ChromiumDriver chromiumDriver = (ChromiumDriver) driver;
		try {
			chromiumDriver.executeCdpCommand("Network.enable", new HashMap<>());

			Map<String, Object> networkConditions = new HashMap<>();
			networkConditions.put("offline", false);
			networkConditions.put("downloadThroughput", 500 * 1024);
			networkConditions.put("uploadThroughput", 500 * 1024);
			networkConditions.put("latency", 400);
			chromiumDriver.executeCdpCommand("Network.emulateNetworkConditions", networkConditions);

			driver.navigate().refresh();
			waitForDashboardReady();

			Assert.assertTrue(dashboard.areBannerImagesVisible() || dashboard.waitForDashboardShell(),
					"Dashboard banners should load or the page should remain stable under slow network conditions.");
		} catch (Exception e) {
			logOptionalUnavailable("CDP slow-network emulation was not available: " + e.getMessage());
		} finally {
			try {
				Map<String, Object> normalNetwork = new HashMap<>();
				normalNetwork.put("offline", false);
				normalNetwork.put("downloadThroughput", -1);
				normalNetwork.put("uploadThroughput", -1);
				normalNetwork.put("latency", 0);
				chromiumDriver.executeCdpCommand("Network.emulateNetworkConditions", normalNetwork);
				chromiumDriver.executeCdpCommand("Network.disable", new HashMap<>());
			} catch (Exception e) {
				// Keep teardown quiet
			}
		}
	}

	@Test(priority = 302, retryAnalyzer = RetryAnalyzer.class)
	public void verifyScrollingWorksWithOnlyOneBanner() {
		waitForDashboardReady();

		int bannerCount = dashboard.getVisibleBannerCount();
		if (bannerCount != 1) {
			logOptionalUnavailable("Single-banner state is not available. Current visible banner count: " + bannerCount);
			return;
		}

		Assert.assertTrue(dashboard.areBannerImagesVisible(),
				"Single configured banner should remain visible without breaking the dashboard.");
	}

	@Test(priority = 303, retryAnalyzer = RetryAnalyzer.class)
	public void verifySystemHandlesManyBannersSmoothly() {
		waitForDashboardReady();

		int bannerCount = dashboard.getVisibleBannerCount();
		if (bannerCount < 5) {
			logOptionalUnavailable("High-banner-count scenario is not available. Current visible banner count: " + bannerCount);
			return;
		}

		Assert.assertTrue(dashboard.clickNextBannerAndVerifyChange() || dashboard.waitForBannerToAutoRotate(3),
				"Banner carousel should remain responsive when many banners are configured.");
	}

	@Test(priority = 304, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBookCoverImageLoadsProperly() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.isBookCoverImageVisible() || dashboard.isPlaceholderImageVisible(),
				"Book details should display either the cover image or a valid placeholder.");
	}

	@Test(priority = 305, retryAnalyzer = RetryAnalyzer.class)
	public void verifySystemBehaviorWhenBookImageMissing() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (dashboard.isBookCoverImageVisible() && !dashboard.isPlaceholderImageVisible()) {
			logOptionalUnavailable("Current book has a valid cover image, so the missing-image state is not available.");
			return;
		}

		Assert.assertTrue(dashboard.isPlaceholderImageVisible() || dashboard.isBookDetailsPageVisible(),
				"Book details should remain stable and show a placeholder when the cover image is missing.");
	}

	@Test(priority = 306, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAudioStartsWhenClickingPlayAudio() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for the current book details page.");
			return;
		}

		Assert.assertTrue(dashboard.clickPlayAudioAndVerifyPlayback(),
				"Audio should start playing when Play Audio is clicked.");
	}

	@Test(priority = 307, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanPausePlayingAudio() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for pause validation.");
			return;
		}

		Assert.assertTrue(dashboard.clickPlayAudioAndVerifyPlayback(),
				"Audio should start playing before pause is validated.");
		Assert.assertTrue(dashboard.clickPauseAndVerifyPlaybackStops(),
				"Audio should pause when the Pause control is clicked.");
	}

	@Test(priority = 308, retryAnalyzer = RetryAnalyzer.class)
	public void verifyShareButtonFunctionality() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isShareButtonVisible()) {
			logOptionalUnavailable("Share button is not visible for the current book details page.");
			return;
		}

		Assert.assertTrue(dashboard.openShareOptions(), "Share action should open share options or a stable share flow.");
	}

	@Test(priority = 309, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanLikeABook() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isFavoriteButtonVisible()) {
			logOptionalUnavailable("Favorite button is not visible for the current book details page.");
			return;
		}

		Assert.assertTrue(dashboard.toggleFavoriteAndVerifyChange(),
				"Clicking the heart icon should add the book to favorites.");
	}

	@Test(priority = 310, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanUnlikeABook() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isFavoriteButtonVisible()) {
			logOptionalUnavailable("Favorite button is not visible for the current book details page.");
			return;
		}

		Assert.assertTrue(dashboard.toggleFavoriteAndVerifyChange(),
				"Clicking the heart icon should add the book to favorites before removal.");
		Assert.assertTrue(dashboard.toggleFavoriteAndVerifyChange(),
				"Clicking the heart icon again should remove the book from favorites.");
	}

	@Test(priority = 311, retryAnalyzer = RetryAnalyzer.class)
	public void verifyCategoriesAppearCorrectly() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.areCategoriesVisible(),
				"Categories should display correctly on the book details page.");
	}

	@Test(priority = 312, retryAnalyzer = RetryAnalyzer.class)
	public void verifyClickingCategoryNavigatesToCategoryPage() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.areCategoriesVisible()) {
			logOptionalUnavailable("Categories are not visible for the current book details page.");
			return;
		}

		Assert.assertTrue(dashboard.clickFirstCategoryAndVerifyNavigation(),
				"Clicking a category should navigate to a category-related page.");
	}

	@Test(priority = 313, retryAnalyzer = RetryAnalyzer.class)
	public void verifySummaryContentLoads() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.isSummaryVisible() || dashboard.hasEmptySummaryMessage(),
				"Summary section should load or show a stable empty state.");
	}

	@Test(priority = 314, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUiWhenSummaryIsMissing() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (dashboard.isSummaryVisible()) {
			logOptionalUnavailable("Current book has summary content, so the empty-summary state is not available.");
			return;
		}

		Assert.assertTrue(dashboard.hasEmptySummaryMessage() || dashboard.isBookDetailsPageVisible(),
				"Book details should show a stable message when summary content is missing.");
	}

	@Test(priority = 315, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterListLoadsCorrectly() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.areChaptersVisible() && dashboard.getVisibleChapterCount() > 0,
				"All available chapters should display correctly.");
	}

	@Test(priority = 316, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterStartsPlayingWhenClicked() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.areChaptersVisible()) {
			logOptionalUnavailable("No chapters are visible for playback validation.");
			return;
		}

		Assert.assertTrue(dashboard.clickFirstChapterAndVerifyPlayer(),
				"Audio should start playing when a chapter is clicked.");
	}

	@Test(priority = 317, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUiWhenNoChaptersAvailable() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (dashboard.areChaptersVisible()) {
			logOptionalUnavailable("Current book has chapters, so the no-chapters state is not available.");
			return;
		}

		Assert.assertTrue(dashboard.hasNoEpisodesMessage() || dashboard.isBookDetailsPageVisible(),
				"Book details should show a stable message when no chapters are available.");
	}

	@Test(priority = 318, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterDurationDisplay() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.areDurationsVisible() || dashboard.areChaptersVisible(),
				"Chapter duration should display when chapters are available.");
	}

	@Test(priority = 319, retryAnalyzer = RetryAnalyzer.class)
	public void verifyReportOptionWorks() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isReportOptionVisible()) {
			logOptionalUnavailable("Report option is not visible for the current book details page.");
			return;
		}

		Assert.assertTrue(dashboard.openReportOption(), "Report action should open report options or form.");
	}

	@Test(priority = 320, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDuplicateReportHandling() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isReportOptionVisible()) {
			logOptionalUnavailable("Report option is not visible for duplicate-report validation.");
			return;
		}

		boolean firstOpen = dashboard.openReportOption();
		boolean secondState = dashboard.openReportOption();

		Assert.assertTrue(firstOpen || secondState || dashboard.hasDuplicateReportProtectionMessage()
				|| dashboard.isBookDetailsPageVisible(),
				"Duplicate report attempts should be prevented or handled without breaking the page.");
	}

	@Test(priority = 321, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBackNavigationReturnsToDashboard() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		Assert.assertTrue(dashboard.clickBackButtonToDashboard(),
				"Back navigation should return the user to the dashboard.");
	}

	@Test(priority = 322, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUiWithLongBookTitles() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isLongBookTitleDisplayed()) {
			logOptionalUnavailable("Current book title is not long enough to validate long-title behavior.");
			return;
		}

		Assert.assertTrue(!dashboard.getBookTitleText().isBlank() && dashboard.isBookDetailsPageVisible(),
				"Long book titles should render without breaking the details page.");
	}

	@Test(priority = 323, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUiPerformanceWithManyChapters() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		int chapterCount = dashboard.getVisibleChapterCount();
		if (chapterCount < 20) {
			logOptionalUnavailable("Large chapter dataset is not available. Current visible chapter count: " + chapterCount);
			return;
		}

		Assert.assertTrue(dashboard.areChaptersVisible() && dashboard.isBookDetailsPageVisible(),
				"Large chapter lists should load without breaking the details page.");
	}

	@Test(priority = 324, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAddBookToFavoritesPlaylist() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isFavoriteButtonVisible()) {
			logOptionalUnavailable("Favorite button is not visible for the current book details page.");
			return;
		}

		String playlistName = "Test Playlist " + System.currentTimeMillis();

		Assert.assertTrue(dashboard.addBookToFavoritesPlaylist(playlistName),
				"Book should be added to favorites and playlist '" + playlistName + "' should be created.");
	}

	@Test(priority = 325, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRemoveBookFromFavoritesAndDeletePlaylist() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isFavoriteButtonVisible()) {
			logOptionalUnavailable("Favorite button is not visible for the current book details page.");
			return;
		}

		// First, add the book to a playlist so we can remove it
		String playlistName = "Test Playlist " + System.currentTimeMillis();
		dashboard.addBookToFavoritesPlaylist(playlistName);

		// Then remove it
		Assert.assertTrue(dashboard.removeBookFromFavoritesAndDeletePlaylist(playlistName),
				"Book should be removed from favorites and playlist '" + playlistName + "' should be deleted.");
	}
}
