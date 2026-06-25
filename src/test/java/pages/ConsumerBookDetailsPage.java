package pages;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.testng.Assert;
import org.testng.SkipException;

import base.BasePage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Page Object that orchestrates Consumer book-details flows that
 * span multiple underlying pages (Login, Dashboard, AudioPlayer).
 * Mirrors the conventions used by {@link ChapterPage}: thin wrappers
 * over the underlying page actions, with all multi-step orchestration,
 * CDP network throttling, banner-rapid-click loops, and wait/sync
 * logic kept inside this class so the {@code ConsumerBookDetailsTests}
 * test class stays lean.
 *
 * <p>This class is the home for:
 * <ul>
 *   <li>Consumer login and credential gating.</li>
 *   <li>Dashboard-shell stabilization before any banner / category
 *       validation.</li>
 *   <li>Opening a book-details page from the dashboard (any book,
 *       from a category, etc.) and stabilizing on it.</li>
 *   <li>Banner auto-rotation, manual drag, rapid-click loop, and
 *       slow-network emulation via CDP.</li>
 *   <li>Audio play / pause validation on the book details page.</li>
 *   <li>Share / favorite / report option visibility + interaction
 *       validation.</li>
 *   <li>Chapter list visibility, count, playback, and boundary
 *       checks (no chapters, many chapters).</li>
 *   <li>Null-safe URL accessor for tests that compare routes.</li>
 * </ul>
 */
public class ConsumerBookDetailsPage extends BasePage {

	private final LoginPage login;
	private final DashboardPage dashboard;
	private final AudioPlayerPage audioPlayer;

	public ConsumerBookDetailsPage(WebDriver driver) {
		super(driver);
		this.login = new LoginPage(driver);
		this.dashboard = new DashboardPage(driver);
		this.audioPlayer = new AudioPlayerPage(driver);
	}

	// ==================== Login ====================

	/**
	 * Login as the configured consumer user, click Next, and wait for
	 * the dashboard shell to settle.
	 */
	public void loginAsConsumer() {
		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();
	}

	/**
	 * Refresh config, then login as the consumer. Throws
	 * {@link SkipException} if the consumer credentials are missing or
	 * blank in {@code config.properties}.
	 */
	public void initConsumerSession() {
		ConfigReader.reload();
		skipIfConsumerCredentialsMissing();
		loginAsConsumer();
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

	// ==================== Dashboard stabilization ====================

	/**
	 * Wait for the dashboard page to be ready and the dashboard shell
	 * to settle, then give the data layer a short stabilization
	 * window.
	 */
	public void waitForDashboardReady() {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(),
				"Dashboard shell should be ready before proceeding.");
		waitQuietly(2000);
	}

	// ==================== Book-details opening ====================

	/**
	 * Open any book from the dashboard and stabilize on the resulting
	 * book-details page. Returns {@code true} if the details page is
	 * visible after stabilization, {@code false} if no book could be
	 * opened (the test should skip in that case).
	 */
	public boolean openBookDetailsFromDashboard() {
		waitForDashboardReady();
		try {
			dashboard.openAnyBook();
			waitQuietly(2000);
			boolean detailsVisible = dashboard.isBookDetailsPageVisible();
			if (detailsVisible) {
				dashboard.waitForBookDataToLoad();
				dashboard.printCurrentBookDetails();
			}
			return detailsVisible;
		} catch (Exception e) {
			LoggerUtils.logInfo("Unable to open a book details page: " + safeString(e.getMessage()));
			return false;
		}
	}

	// ==================== Banner validations ====================

	/**
	 * @return {@code true} if the dashboard banner section is currently
	 *         visible AND has more than one banner. Tests use this as
	 *         the gate before validating auto-rotation or drag.
	 */
	public boolean isMultiBannerScenarioAvailable() {
		return dashboard.isBannerSectionVisible() && dashboard.getVisibleBannerCount() > 1;
	}

	/**
	 * Log a soft-unavailable message for tests that cannot validate
	 * their scenario on the current dashboard state.
	 */
	public void logOptionalUnavailable(String message) {
		LoggerUtils.logInfo(message);
	}

	/**
	 * Click a banner and verify a destination opens. Returns the
	 * opened-page title text (or empty) for callers that need to
	 * verify the destination's content.
	 */
	public boolean openBannerAndGetDetails(String testCaseId) {
		boolean opened = dashboard.clickCurrentBannerAndOpenDetails();
		if (opened) {
			LoggerUtils.logInfo(testCaseId + " - STEP: Banner click opened a destination page");
		}
		return opened;
	}

	/**
	 * Run the rapid-banner-click loop: click → back → settle, repeated
	 * up to {@code iterations} times. Returns {@code true} if the
	 * dashboard shell is still stable after the loop.
	 */
	public boolean rapidClickBannersAndVerifyStable(int iterations) {
		for (int index = 0; index < iterations; index++) {
			if (!dashboard.clickCurrentBannerAndOpenDetails()) {
				break;
			}
			driver.navigate().back();
			waitForDashboardReady();
			waitQuietly(500);
		}
		return dashboard.waitForDashboardShell();
	}

	/**
	 * Throttle the network via CDP to a slow profile (500 KB/s both
	 * directions, 400ms latency), refresh the page, and return whether
	 * the dashboard / banner area is still stable. Throws
	 * {@link SkipException} if the driver is not Chromium-based. Always
	 * restores normal network conditions in a finally block.
	 */
	public boolean throttleNetworkAndVerifyBannerLoads(String testCaseId) {
		if (!(driver instanceof ChromiumDriver)) {
			throw new SkipException("TC_" + testCaseId + ": Slow-network banner validation requires a Chromium-based driver.");
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

			boolean ok = dashboard.areBannerImagesVisible() || dashboard.waitForDashboardShell();
			LoggerUtils.logInfo("TC_" + testCaseId + " - Banner stable under throttled network: " + ok);
			return ok;
		} catch (Exception e) {
			logOptionalUnavailable("CDP slow-network emulation was not available: " + safeString(e.getMessage()));
			return false;
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
				// Keep teardown quiet.
			}
		}
	}

	// ==================== Book-details content checks ====================

	/**
	 * @return {@code true} if the open book's details page exposes a
	 *         report option. Used as a precondition gate by report-
	 *         related tests.
	 */
	public boolean isReportOptionVisibleOnDetails() {
		return dashboard.isReportOptionVisible();
	}

	/**
	 * @return the visible chapter count on the open book-details page.
	 */
	public int getVisibleChapterCount() {
		return dashboard.getVisibleChapterCount();
	}

	/**
	 * @return the trimmed, lower-cased current URL, or empty string if
	 *         the driver cannot be queried.
	 */
	public String getCurrentUrlSafely() {
		try {
			String url = driver.getCurrentUrl();
			return url == null ? "" : url.toLowerCase(Locale.ROOT);
		} catch (Exception e) {
			return "";
		}
	}

	// ==================== Dashboard helpers ====================

	/**
	 * Navigate to the named category from the dashboard and open the
	 * first available book. Returns {@code true} if the book details
	 * page is visible after the navigation chain completes.
	 */
	public boolean navigateToCategoryAndOpenBook(String testCaseId, String categoryName) {
		if (!dashboard.navigateToCategory(categoryName)) {
			return false;
		}
		waitQuietly(2000);
		if (!dashboard.hasCategoryContent()) {
			return false;
		}
		try {
			dashboard.openAnyBookFromCategoryPage();
			waitQuietly(2000);
		} catch (Exception e) {
			Assert.fail("Unable to open a book from the " + categoryName + " category: " + safeString(e.getMessage()));
			return false;
		}
		return dashboard.isBookDetailsPageVisible();
	}

	/**
	 * @return {@code true} if any of the categories displayed on the
	 *         open book details page matches the given category name
	 *         (case-insensitive).
	 */
	public boolean isCategoryDisplayedOnDetails(String categoryName) {
		return dashboard.getAllCategoryTexts().stream()
				.anyMatch(category -> safeString(category).equalsIgnoreCase(safeString(categoryName)));
	}

	// ==================== Dashboard wrapper methods ====================

	/**
	 * @return {@code true} if banner images are visible on the dashboard
	 */
	public boolean areBannerImagesVisible() {
		return dashboard.areBannerImagesVisible();
	}

	/**
	 * @return {@code true} if the dashboard renders more than one banner
	 *         image. Used by TC_303 as a soft availability check for the
	 *         many-banner scenario, independent of carousel interactivity
	 *         (which may be paused or non-rotating when many banners are
	 *         configured).
	 */
	public boolean areMultipleBannersVisible() {
		return dashboard.areMultipleBannersVisible();
	}

	/**
	 * @return {@code true} if the dashboard shell is ready
	 */
	public boolean waitForDashboardShell() {
		return dashboard.waitForDashboardShell();
	}

	/**
	 * @return {@code true} if the banner section is visible
	 */
	public boolean isBannerSectionVisible() {
		return dashboard.isBannerSectionVisible();
	}

	/**
	 * @return the count of visible banners
	 */
	public int getVisibleBannerCount() {
		return dashboard.getVisibleBannerCount();
	}

	/**
	 * Wait for banner to auto-rotate
	 * @param waitSeconds number of seconds to wait
	 * @return {@code true} if banner rotated
	 */
	public boolean waitForBannerToAutoRotate(int waitSeconds) {
		return dashboard.waitForBannerToAutoRotate(waitSeconds);
	}

	/**
	 * @return {@code true} if drag-and-drop on banner works
	 */
	public boolean dragBannerAndVerifyChange() {
		return dashboard.dragBannerAndVerifyChange();
	}

	/**
	 * @return {@code true} if banner has clickable target
	 */
	public boolean hasClickableBannerTarget() {
		return dashboard.hasClickableBannerTarget();
	}

	/**
	 * Click next banner and verify it changed
	 * @return {@code true} if banner changed
	 */
	public boolean clickNextBannerAndVerifyChange() {
		return dashboard.clickNextBannerAndVerifyChange();
	}

	/**
	 * @return {@code true} if book details page is visible
	 */
	public boolean isBookDetailsPageVisible() {
		return dashboard.isBookDetailsPageVisible();
	}

	/**
	 * @return {@code true} if reviews can be opened and navigated
	 */
	public boolean openReviewsAndVerifyNavigation() {
		return dashboard.openReviewsAndVerifyNavigation();
	}

	/**
	 * @return {@code true} if episodes are visible
	 */
	public boolean areEpisodesVisible() {
		return dashboard.areEpisodesVisible();
	}

	/**
	 * @return {@code true} if no episodes message is shown
	 */
	public boolean hasNoEpisodesMessage() {
		return dashboard.hasNoEpisodesMessage();
	}

	/**
	 * @return {@code true} if durations are visible
	 */
	public boolean areDurationsVisible() {
		return dashboard.areDurationsVisible();
	}

	/**
	 * @return {@code true} if ratings are displayed
	 */
	public boolean areRatingsDisplayed() {
		return dashboard.areRatingsDisplayed();
	}

	/**
	 * @return {@code true} if report option is visible
	 */
	public boolean isReportOptionVisible() {
		return dashboard.isReportOptionVisible();
	}

	/**
	 * @return {@code true} if categories are visible
	 */
	public boolean areCategoriesVisible() {
		return dashboard.areCategoriesVisible();
	}

	/**
	 * @return {@code true} if summary is visible
	 */
	public boolean isSummaryVisible() {
		return dashboard.isSummaryVisible();
	}

	/**
	 * @return {@code true} if empty summary message is shown
	 */
	public boolean hasEmptySummaryMessage() {
		return dashboard.hasEmptySummaryMessage();
	}

	/**
	 * @return {@code true} if chapters are visible
	 */
	public boolean areChaptersVisible() {
		return dashboard.areChaptersVisible();
	}

	/**
	 * @return {@code true} if no reviews message is shown
	 */
	public boolean hasNoReviewsMessage() {
		return dashboard.hasNoReviewsMessage();
	}

	/**
	 * @return {@code true} if reviews are visible
	 */
	public boolean areReviewsVisible() {
		return dashboard.areReviewsVisible();
	}

	/**
	 * @return {@code true} if book cover image is visible
	 */
	public boolean isBookCoverImageVisible() {
		return dashboard.isBookCoverImageVisible();
	}

	/**
	 * @return {@code true} if placeholder image is visible
	 */
	public boolean isPlaceholderImageVisible() {
		return dashboard.isPlaceholderImageVisible();
	}

	/**
	 * @return {@code true} if share button is visible
	 */
	public boolean isShareButtonVisible() {
		return dashboard.isShareButtonVisible();
	}

	/**
	 * @return {@code true} if share options can be opened
	 */
	public boolean openShareOptions() {
		return dashboard.openShareOptions();
	}

	/**
	 * @return {@code true} if favorite button is visible
	 */
	public boolean isFavoriteButtonVisible() {
		return dashboard.isFavoriteButtonVisible();
	}

	/**
	 * @return {@code true} if favorite toggle works
	 */
	public boolean toggleFavoriteAndVerifyChange() {
		return dashboard.toggleFavoriteAndVerifyChange();
	}

	/**
	 * Navigate to category by name
	 * @param categoryName the category name
	 * @return {@code true} if navigation succeeded
	 */
	public boolean navigateToCategory(String categoryName) {
		return dashboard.navigateToCategory(categoryName);
	}

	/**
	 * @return {@code true} if category page has content
	 */
	public boolean hasCategoryContent() {
		return dashboard.hasCategoryContent();
	}

	/**
	 * Open any book from the category page
	 */
	public void openAnyBookFromCategoryPage() {
		dashboard.openAnyBookFromCategoryPage();
	}

	/**
	 * @return {@code true} if the current book title is long (>50 chars)
	 */
	public boolean isLongBookTitleDisplayed() {
		String title = dashboard.getBookTitleText();
		return title != null && title.length() > 50;
	}

	/**
	 * @return the book title text
	 */
	public String getBookTitleText() {
		return dashboard.getBookTitleText();
	}

	/**
	 * @return {@code true} if clicking first chapter starts player
	 */
	public boolean clickFirstChapterAndVerifyPlayer() {
		return dashboard.clickFirstChapterAndVerifyPlayer();
	}

	/**
	 * @return {@code true} if report inappropriate content succeeds
	 */
	public boolean reportInappropriateContent() {
		return dashboard.reportInappropriateContent();
	}

	/**
	 * Reports inappropriate content and returns which outcome was observed.
	 * Used by TC_319 to distinguish the "Report received" path from the
	 * "already reported / duplicate" path — both are valid report-flow
	 * completions on a re-run against the same book.
	 *
	 * @return one of: {@code "report_received"}, {@code "already_reported"},
	 *         {@code "failed"} (when the flow could not complete).
	 */
	public String reportInappropriateContentAndGetOutcome() {
		if (dashboard.hasAlreadyReportedMessage()) {
			LoggerUtils.logInfo("TC_319 - Report outcome: already_reported (server returned duplicate-protection message)");
			return "already_reported";
		}
		boolean freshReportAccepted = dashboard.reportInappropriateContent();
		if (freshReportAccepted) {
			// The fresh-report path may also surface the duplicate message
			// if the report was already submitted in a previous run.
			if (dashboard.hasAlreadyReportedMessage()) {
				LoggerUtils.logInfo("TC_319 - Report outcome: already_reported (server returned duplicate-protection message)");
				return "already_reported";
			}
			LoggerUtils.logInfo("TC_319 - Report outcome: report_received");
			return "report_received";
		}
		LoggerUtils.logInfo("TC_319 - Report outcome: failed (no confirmation and no duplicate message observed)");
		return "failed";
	}

	/**
	 * Captures the user-visible report outcome message from the page so
	 * tests can print what the user actually sees. Looks for the success
	 * confirmation first, then the duplicate-protection message.
	 *
	 * @return the matching snippet, or an empty string if none was found.
	 */
	public String captureReportOutcomeMessage() {
		String snippet = dashboard.findFirstMatchingReportSnippet();
		return snippet == null ? "" : snippet;
	}

	/**
	 * @return {@code true} if already reported message is visible
	 */
	public boolean hasAlreadyReportedMessage() {
		return dashboard.hasAlreadyReportedMessage();
	}

	/**
	 * @return {@code true} if continue listening can be clicked after report
	 */
	public boolean clickContinueListeningAfterReport() {
		return dashboard.clickContinueListeningAfterReport();
	}

	/**
	 * @return {@code true} if back navigation returns to dashboard
	 */
	public boolean clickBackButtonToDashboard() {
		return dashboard.clickBackButtonToDashboard();
	}

	// ==================== AudioPlayer wrapper methods ====================

	/**
	 * @return {@code true} if play button is visible
	 */
	public boolean isPlayButtonVisible() {
		return audioPlayer.isPlayButtonVisible();
	}

	/**
	 * @return {@code true} if audio playback starts
	 */
	public boolean validatePlay() {
		return audioPlayer.validatePlay();
	}

	/**
	 * @return {@code true} if audio pauses
	 */
	public boolean validatePause() {
		return audioPlayer.validatePause();
	}

	// ==================== Local helpers ====================

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	public void waitQuietly(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Sleep interrupted", e);
		}
	}

	public String safeString(String value) {
		return value == null ? "" : value;
	}
}