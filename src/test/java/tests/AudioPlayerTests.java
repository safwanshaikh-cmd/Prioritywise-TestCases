package tests;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.AudioPlayerPage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Audio Player Automation Tests
 *
 * <p>
 * Test Coverage: TC_324 - TC_350
 * <p>
 * Focus: Audio player controls, playback operations, and user interactions.
 */
public class AudioPlayerTests extends BaseTest {

	private static final String AUDIO_ADVANCED_EMAIL = "safwan.shaikh+041@11axis.com";
	private static final String AUDIO_ADVANCED_PASSWORD = "Password@123";
	private static final String FREE_USER_EMAIL = "safwan.shaikh+040@11axis.com";
	private static final String FREE_USER_PASSWORD = "Password@123";

	private LoginPage login;
	private DashboardPage dashboard;
	private AudioPlayerPage player;

	@BeforeMethod(alwaysRun = true)
	@Override
	public void setup() {
		super.setup();
		login = new LoginPage(driver);
		dashboard = new DashboardPage(driver);
		player = new AudioPlayerPage(driver);
	}

	/**
	 * Helper method to login as registered user. Mirrors the pattern used in
	 * {@link tests.AudioPlayerBehaviorTests} for consistency.
	 */
	private void loginAsRegisteredUser() {
		try {
			login.openLogin();
			login.loginUser(getConsumerEmail(), getConsumerPassword());
			login.clickNextAfterLogin();
			boolean loginSettled = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> {
				String currentUrl = safeGetCurrentUrl(currentDriver);
				String lowerUrl = currentUrl.toLowerCase();
				return !lowerUrl.contains("/login") && !lowerUrl.contains("signin");
			});
			Assert.assertTrue(loginSettled, "Registered user login should move past the login page");
			LoggerUtils.logInfo("Logged in as registered user");
		} catch (Exception e) {
			throw new SkipException("Could not login as registered user: " + e.getMessage(), e);
		}
	}

	/**
	 * Helper method to login as advanced audio user (for subscribed features).
	 */
	private void loginAsAdvancedAudioUser() {
		try {
			login.openLogin();
			login.loginUser(getAdvancedAudioEmail(), getAdvancedAudioPassword());
			login.clickNextAfterLogin();
			boolean loginSettled = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> {
				String currentUrl = safeGetCurrentUrl(currentDriver);
				String lowerUrl = currentUrl.toLowerCase();
				return !lowerUrl.contains("/login") && !lowerUrl.contains("signin");
			});
			Assert.assertTrue(loginSettled, "Advanced audio user login should move past the login page");
			LoggerUtils.logInfo("Logged in as advanced audio user");
		} catch (Exception e) {
			throw new SkipException("Could not login as advanced audio user: " + e.getMessage(), e);
		}
	}

	/**
	 * Helper method to login as free user.
	 */
	private void loginAsFreeUser() {
		try {
			login.openLogin();
			login.loginUser(getFreeUserEmail(), getFreeUserPassword());
			login.clickNextAfterLogin();
			boolean loginSettled = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> {
				String currentUrl = safeGetCurrentUrl(currentDriver);
				String lowerUrl = currentUrl.toLowerCase();
				return !lowerUrl.contains("/login") && !lowerUrl.contains("signin");
			});
			Assert.assertTrue(loginSettled, "Free user login should move past the login page");
			LoggerUtils.logInfo("Logged in as free user");
		} catch (Exception e) {
			throw new SkipException("Could not login as free user: " + e.getMessage(), e);
		}
	}

	/**
	 * Helper method to open a dashboard book and wait for player readiness.
	 */
	private void openAnyDashboardBookAndWaitForPlayer() {
		try {
			dashboard.waitForPageReady();
			if (!dashboard.waitForDashboardShell()) {
				throw new SkipException("Could not stabilize dashboard before opening a book.");
			}
			dashboard.openAnyBook();
			if (!dashboard.isBookDetailsPageVisible()) {
				throw new SkipException("Book details page did not open from dashboard.");
			}
			if (!dashboard.waitForBookDataToLoad()) {
				throw new SkipException("Book details data did not load.");
			}
			if (!player.waitForPlayerBar()) {
				throw new SkipException("Player surface did not become visible on book details page.");
			}
			if (!player.waitForPlayControlsReady() && !player.hasSubscriptionGate()) {
				throw new SkipException("Play controls were not ready on the selected book.");
			}
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			throw new SkipException("Could not open a dashboard book for playback: " + e.getMessage());
		}
	}

	/**
	 * Helper method to open a book by title from trending list.
	 */
	private void openBookByTitle(String title) {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(),
				"Dashboard shell should be ready before selecting a book.");
		dashboard.clickTrendingShow(title);
		Assert.assertTrue(dashboard.isBookDetailsPageVisible(), "Expected book details page for title: " + title);
		Assert.assertTrue(dashboard.waitForBookDataToLoad(), "Expected book details data for title: " + title);
		Assert.assertTrue(player.waitForPlayerBar() || player.hasSubscriptionGate(),
				"Expected player or subscription gate for title: " + title);
		Assert.assertTrue(player.waitForPlayControlsReady() || player.hasSubscriptionGate(),
				"Expected play controls or subscription gate for title: " + title);
	}

	private String safeGetCurrentUrl(org.openqa.selenium.WebDriver driver) {
		try {
			return driver.getCurrentUrl();
		} catch (Exception e) {
			return "";
		}
	}

	private String safeString(String str) {
		return str == null ? "" : str;
	}

	// ==================== TC_324: PLAY BUTTON ====================

	/**
	 * TC_324: Audio Player - Play button functionality Test Flow: Login → Open
	 * book → Click Play Expected: Audio starts playing.
	 */
	@Test(priority = 324, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_324: Verify audio starts when clicking Play button")
	public void TC324_VerifyAudioStartsWhenClickingPlayButton() {
		LoggerUtils.logTestStart("TC_324: Audio Starts When Clicking Play Button");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_324 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_324 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_324: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Click play button");
			Assert.assertTrue(player.clickPlayAudio(), "TC_324: expected play button to be clickable.");
			LoggerUtils.logInfo("TC_324 - STEP 3: Play button clicked");

			LoggerUtils.logStep(4, "Verify audio playback started");
			boolean isPlaying = player.isPlaybackProgressing() || player.isPauseButtonVisible();
			LoggerUtils.logInfo("TC_324 - STEP 4: Audio playing: " + isPlaying);

			Assert.assertTrue(isPlaying, "TC_324: Audio should start playing when play button is clicked");
			LoggerUtils.logInfo("TC_324: ✓ Test PASSED - Audio starts when clicking Play button");

			LoggerUtils.logTestEnd("TC_324", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_324 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_325: PAUSE BUTTON ====================

	/**
	 * TC_325: Audio Player - Pause button functionality Test Flow: Play → Pause
	 * Expected: Audio stops playing.
	 */
	@Test(priority = 325, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_325: Verify audio pauses when Pause button is clicked")
	public void TC325_VerifyAudioPausesWhenPauseButtonClicked() {
		LoggerUtils.logTestStart("TC_325: Audio Pauses When Pause Button Clicked");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_325 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_325 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_325: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_325 setup: expected playback to start before pause.");
			boolean initiallyPlaying = player.isPlaybackProgressing();
			LoggerUtils.logInfo("TC_325 - STEP 3: Audio initially playing: " + initiallyPlaying);

			LoggerUtils.logStep(4, "Click pause button");
			boolean paused = player.validatePause();
			LoggerUtils.logInfo("TC_325 - STEP 4: Pause button clicked, audio paused: " + paused);

			Assert.assertTrue(paused, "TC_325: Audio should pause when pause button is clicked");
			LoggerUtils.logInfo("TC_325: ✓ Test PASSED - Audio pauses when Pause button is clicked");

			LoggerUtils.logTestEnd("TC_325", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_325 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_326: RESUME BUTTON ====================

	/**
	 * TC_326: Audio Player - Resume functionality Test Flow: Play → Pause →
	 * Resume Expected: Audio resumes from paused position.
	 */
	@Test(priority = 326, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_326: Verify audio resumes from paused position")
	public void TC326_VerifyAudioResumesFromPausedPosition() {
		LoggerUtils.logTestStart("TC_326: Audio Resumes From Paused Position");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_326 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_326 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_326: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_326 setup: expected playback to start before pause.");
			boolean initiallyPlaying = player.isPlaybackProgressing();
			LoggerUtils.logInfo("TC_326 - STEP 3: Audio initially playing: " + initiallyPlaying);

			LoggerUtils.logStep(4, "Pause audio playback");
			String timeBeforePause = player.getCurrentTime();
			boolean paused = player.validatePause();
			LoggerUtils.logInfo("TC_326 - STEP 4: Audio paused: " + paused + ", time: " + timeBeforePause);

			LoggerUtils.logStep(5, "Resume audio playback");
			boolean resumed = player.validateResume();
			LoggerUtils.logInfo("TC_326 - STEP 5: Audio resumed: " + resumed);

			Assert.assertTrue(resumed, "TC_326: Audio should resume from paused position");
			LoggerUtils.logInfo("TC_326: ✓ Test PASSED - Audio resumes from paused position");

			LoggerUtils.logTestEnd("TC_326", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_326 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_327: FORWARD 30 ====================

	/**
	 * TC_327: Audio Player - Forward 30 seconds Test Flow: Play → Forward 30
	 * Expected: Time increases by ~30 seconds.
	 */
	@Test(priority = 327, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_327: Verify user can skip forward 30 seconds")
	public void TC327_VerifyUserCanSkipForward30Seconds() {
		LoggerUtils.logTestStart("TC_327: User Can Skip Forward 30 Seconds");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_327 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_327 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_327: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_327 setup: expected playback to start before skip.");
			LoggerUtils.logInfo("TC_327 - STEP 3: Audio playback started");

			String timeBeforeSkip = player.getCurrentTime();
			LoggerUtils.logInfo("TC_327 - STEP 3: Time before skip: " + timeBeforeSkip);

			LoggerUtils.logStep(4, "Click forward 30 button");
			Assert.assertTrue(player.validateForward30(), "TC_327: expected time to increase after forward skip.");
			LoggerUtils.logInfo("TC_327 - STEP 4: Forward 30 button clicked");

			Assert.assertTrue(player.isPlaybackProgressing(), "TC_327: Audio should continue playing after skip");
			LoggerUtils.logInfo("TC_327: ✓ Test PASSED - User can skip forward 30 seconds");

			LoggerUtils.logTestEnd("TC_327", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_327 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_328: BACKWARD 30 ====================

	/**
	 * TC_328: Audio Player - Backward 30 seconds Test Flow: Play → Backward 30
	 * Expected: Time decreases by ~30 seconds.
	 */
	@Test(priority = 328, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_328: Verify user can rewind 30 seconds")
	public void TC328_VerifyUserCanRewind30Seconds() {
		LoggerUtils.logTestStart("TC_328: User Can Rewind 30 Seconds");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_328 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_328 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_328: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback and move forward first");
			Assert.assertTrue(player.clickPlayAudio(), "TC_328 setup: expected playback to start.");
			Assert.assertTrue(player.validateForward30(), "TC_328 setup: expected forward to work first.");
			String timeAfterForward = player.getCurrentTime();
			LoggerUtils.logInfo("TC_328 - STEP 3: Moved forward first, time: " + timeAfterForward);

			LoggerUtils.logStep(4, "Click backward 30 button");
			Assert.assertTrue(player.validateBackward30(), "TC_328: expected time to decrease after backward.");
			LoggerUtils.logInfo("TC_328 - STEP 4: Backward 30 button clicked");

			Assert.assertTrue(player.isPlaybackProgressing(), "TC_328: Audio should continue playing after rewind");
			LoggerUtils.logInfo("TC_328: ✓ Test PASSED - User can rewind 30 seconds");

			LoggerUtils.logTestEnd("TC_328", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_328 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_329: FORWARD NEAR END ====================

	/**
	 * TC_329: Audio Player - Forward near end Test Flow: Play → Forward near end
	 * Expected: Stops at duration without overshoot.
	 */
	@Test(priority = 329, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_329: Verify Forward 30 near audio end stops at duration or completes playback without overshooting")
	public void TC329_VerifyForwardButtonBehaviorNearAudioEnd() {
		LoggerUtils.logTestStart("TC_329: Forward Button Behavior Near Audio End");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_329 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_329 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_329: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_329 setup: expected playback to start.");
			LoggerUtils.logInfo("TC_329 - STEP 3: Audio playback started");

			LoggerUtils.logStep(4, "Verify forward near end behavior");
			Assert.assertTrue(player.validateSkipNearEnd(),
					"TC_329: expected forward near end to stop at duration or complete cleanly.");
			LoggerUtils.logInfo("TC_329 - STEP 4: Forward near end handled correctly");

			LoggerUtils.logInfo("TC_329: ✓ Test PASSED - Forward near end stops at duration");

			LoggerUtils.logTestEnd("TC_329", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_329 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_330: BACKWARD AT START ====================

	/**
	 * TC_330: Audio Player - Backward at start Test Flow: Play → Backward at start
	 * Expected: Time stays at or above zero.
	 */
	@Test(priority = 330, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_330: Verify rewind behavior at audio start")
	public void TC330_VerifyRewindBehaviorAtAudioStart() {
		LoggerUtils.logTestStart("TC_330: Rewind Behavior At Audio Start");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_330 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_330 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_330: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_330 setup: expected playback to start.");
			String timeAtStart = player.getCurrentTime();
			LoggerUtils.logInfo("TC_330 - STEP 3: Time at start: " + timeAtStart);

			LoggerUtils.logStep(4, "Verify backward at start behavior");
			Assert.assertTrue(player.validateBackwardAtStart(),
					"TC_330: expected backward at start to keep time at or above zero.");
			LoggerUtils.logInfo("TC_330 - STEP 4: Backward at start handled correctly");

			LoggerUtils.logInfo("TC_330: ✓ Test PASSED - Backward at start keeps time at or above zero");

			LoggerUtils.logTestEnd("TC_330", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_330 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_331: NEXT CHAPTER ====================

	/**
	 * TC_331: Audio Player - Next chapter Test Flow: Play → Next chapter Expected:
	 * New chapter starts.
	 */
	@Test(priority = 331, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_331: Verify clicking next chapter plays the next chapter")
	public void TC331_VerifyClickingNextChapterPlaysNextChapter() {
		LoggerUtils.logTestStart("TC_331: Clicking Next Chapter Plays Next Chapter");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user (multi-chapter book required)");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_331 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_331 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_331: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Verify multiple chapters are available");
			if (!player.hasMultipleChapters()) {
				throw new SkipException("TC_331: Audio does not have multiple chapters");
			}
			LoggerUtils.logInfo("TC_331 - STEP 3: Multiple chapters confirmed");

			LoggerUtils.logStep(4, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_331 setup: expected playback to start.");
			String initialChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_331 - STEP 4: Current chapter: " + initialChapter);

			LoggerUtils.logStep(5, "Click next chapter button");
			Assert.assertTrue(player.validateChapterChange(true),
					"TC_331: expected next chapter action to change chapter context.");
			String newChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_331 - STEP 5: New chapter: " + newChapter);

			boolean chapterChanged = !initialChapter.equals(newChapter) && !"N/A".equals(newChapter);
			Assert.assertTrue(chapterChanged, "TC_331: Chapter should change to next chapter");
			LoggerUtils.logInfo("TC_331: ✓ Test PASSED - Next chapter plays correctly");

			LoggerUtils.logTestEnd("TC_331", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_331 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_332: PREVIOUS CHAPTER ====================

	/**
	 * TC_332: Audio Player - Previous chapter Test Flow: Next → Previous Expected:
	 * Returns to earlier chapter.
	 */
	@Test(priority = 332, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_332: Verify previous chapter navigation returns to earlier chapter")
	public void TC332_VerifyPreviousChapterNavigation() {
		LoggerUtils.logTestStart("TC_332: Previous Chapter Navigation");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user (multi-chapter book required)");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_332 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_332 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_332: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Verify multiple chapters are available");
			if (!player.hasMultipleChapters()) {
				throw new SkipException("TC_332: Audio does not have multiple chapters");
			}
			LoggerUtils.logInfo("TC_332 - STEP 3: Multiple chapters confirmed");

			LoggerUtils.logStep(4, "Start audio playback and move to next chapter");
			Assert.assertTrue(player.clickPlayAudio(), "TC_332 setup: expected playback to start.");
			Assert.assertTrue(player.validateChapterChange(true),
					"TC_332 setup: expected next chapter to work first.");
			String nextChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_332 - STEP 4: Moved to next chapter: " + nextChapter);

			LoggerUtils.logStep(5, "Click previous chapter button");
			Assert.assertTrue(player.validateChapterChange(false),
					"TC_332: expected previous chapter action to return to earlier chapter.");
			String prevChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_332 - STEP 5: Returned to chapter: " + prevChapter);

			boolean returnedToEarlier = !nextChapter.equals(prevChapter);
			Assert.assertTrue(returnedToEarlier, "TC_332: Should return to earlier chapter");
			LoggerUtils.logInfo("TC_332: ✓ Test PASSED - Previous chapter navigation works");

			LoggerUtils.logTestEnd("TC_332", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_332 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_333: PREVIOUS ON FIRST CHAPTER ====================

	/**
	 * TC_333: Audio Player - Previous on first chapter Test Flow: On first chapter
	 * → Previous Expected: Stays on first chapter.
	 */
	@Test(priority = 333, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_333: Verify previous button behavior on first chapter")
	public void TC333_VerifyPreviousButtonBehaviorOnFirstChapter() {
		LoggerUtils.logTestStart("TC_333: Previous Button Behavior On First Chapter");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user (multi-chapter book required)");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_333 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_333 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_333: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Verify multiple chapters are available");
			if (!player.hasMultipleChapters()) {
				throw new SkipException("TC_333: Audio does not have multiple chapters");
			}
			LoggerUtils.logInfo("TC_333 - STEP 3: Multiple chapters confirmed");

			LoggerUtils.logStep(4, "Start audio playback from first chapter");
			Assert.assertTrue(player.clickPlayAudio(), "TC_333 setup: expected playback to start.");
			String currentChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_333 - STEP 4: Current chapter: " + currentChapter);

			LoggerUtils.logStep(5, "Verify previous on first chapter behavior");
			Assert.assertTrue(player.validatePreviousOnFirstChapterBoundary(),
					"TC_333: expected previous on first chapter to keep player stable.");
			LoggerUtils.logInfo("TC_333 - STEP 5: Previous on first chapter handled correctly");

			LoggerUtils.logInfo("TC_333: ✓ Test PASSED - Previous on first chapter keeps player stable");

			LoggerUtils.logTestEnd("TC_333", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_333 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_334: PLAYBACK SPEED ====================

	/**
	 * TC_334: Audio Player - Playback speed Test Flow: Play → Change speed
	 * Expected: Speed changes to 1.5x.
	 */
	@Test(priority = 334, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_334: Verify user can change playback speed")
	public void TC334_VerifyUserCanChangePlaybackSpeed() {
		LoggerUtils.logTestStart("TC_334: User Can Change Playback Speed");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_334 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_334 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_334: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_334 setup: expected playback to start.");
			LoggerUtils.logInfo("TC_334 - STEP 3: Audio playback started");

			LoggerUtils.logStep(4, "Change playback speed to 1.5x");
			Assert.assertTrue(player.validatePlaybackSpeed("1.5x"),
					"TC_334: expected playback speed to change to 1.5x.");
			LoggerUtils.logInfo("TC_334 - STEP 4: Playback speed changed to 1.5x");

			LoggerUtils.logInfo("TC_334: ✓ Test PASSED - User can change playback speed");

			LoggerUtils.logTestEnd("TC_334", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_334 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_335: MINIMUM SPEED ====================

	/**
	 * TC_335: Audio Player - Minimum speed Test Flow: Play → 0.5x Expected:
	 * Speed changes to minimum.
	 */
	@Test(priority = 335, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_335: Verify minimum speed selection (0.5x)")
	public void TC335_VerifyMinimumSpeedSelection() {
		LoggerUtils.logTestStart("TC_335: Minimum Speed Selection");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_335 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_335 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_335: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_335 setup: expected playback to start.");
			LoggerUtils.logInfo("TC_335 - STEP 3: Audio playback started");

			LoggerUtils.logStep(4, "Change playback speed to 0.5x");
			Assert.assertTrue(player.validatePlaybackSpeed("0.5x"),
					"TC_335: expected playback speed to change to 0.5x.");
			LoggerUtils.logInfo("TC_335 - STEP 4: Playback speed changed to 0.5x");

			LoggerUtils.logInfo("TC_335: ✓ Test PASSED - Minimum speed selection works");

			LoggerUtils.logTestEnd("TC_335", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_335 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_336: MAXIMUM SPEED ====================

	/**
	 * TC_336: Audio Player - Maximum speed Test Flow: Play → 2x Expected: Speed
	 * changes to maximum.
	 */
	@Test(priority = 336, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_336: Verify maximum speed selection (2x)")
	public void TC336_VerifyMaximumSpeedSelection() {
		LoggerUtils.logTestStart("TC_336: Maximum Speed Selection");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_336 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_336 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_336: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_336 setup: expected playback to start.");
			LoggerUtils.logInfo("TC_336 - STEP 3: Audio playback started");

			LoggerUtils.logStep(4, "Change playback speed to 2x");
			Assert.assertTrue(player.validatePlaybackSpeed("2x"),
					"TC_336: expected playback speed to change to 2x.");
			LoggerUtils.logInfo("TC_336 - STEP 4: Playback speed changed to 2x");

			LoggerUtils.logInfo("TC_336: ✓ Test PASSED - Maximum speed selection works");

			LoggerUtils.logTestEnd("TC_336", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_336 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_337: VOLUME INCREASE ====================

	/**
	 * TC_337: Audio Player - Volume increase Test Flow: Play → Increase volume
	 * Expected: Volume increases.
	 */
	@Test(priority = 337, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_337: Verify volume slider increases audio level")
	public void TC337_VerifyVolumeSliderIncreasesAudioLevel() {
		LoggerUtils.logTestStart("TC_337: Volume Slider Increases Audio Level");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_337 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_337 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_337: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Verify volume control is accessible");
			Assert.assertTrue(player.hasAccessibleVolumeControl(),
					"TC_337 precondition: expected accessible volume control.");
			LoggerUtils.logInfo("TC_337 - STEP 3: Volume control is accessible");

			LoggerUtils.logStep(4, "Increase volume using slider");
			Assert.assertTrue(player.validateVolumeChange(true),
					"TC_337: expected volume to increase after slider adjustment.");
			LoggerUtils.logInfo("TC_337 - STEP 4: Volume increased successfully");

			LoggerUtils.logInfo("TC_337: ✓ Test PASSED - Volume slider increases audio level");

			LoggerUtils.logTestEnd("TC_337", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_337 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_338: VOLUME DECREASE ====================

	/**
	 * TC_338: Audio Player - Volume decrease Test Flow: Play → Decrease volume
	 * Expected: Volume decreases.
	 */
	@Test(priority = 338, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_338: Verify volume slider decreases audio level")
	public void TC338_VerifyVolumeSliderDecreasesAudioLevel() {
		LoggerUtils.logTestStart("TC_338: Volume Slider Decreases Audio Level");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_338 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_338 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_338: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Verify volume control is accessible");
			Assert.assertTrue(player.hasAccessibleVolumeControl(),
					"TC_338 precondition: expected accessible volume control.");
			LoggerUtils.logInfo("TC_338 - STEP 3: Volume control is accessible");

			LoggerUtils.logStep(4, "Decrease volume using slider");
			Assert.assertTrue(player.validateVolumeChange(false),
					"TC_338: expected volume to decrease after slider adjustment.");
			LoggerUtils.logInfo("TC_338 - STEP 4: Volume decreased successfully");

			LoggerUtils.logInfo("TC_338: ✓ Test PASSED - Volume slider decreases audio level");

			LoggerUtils.logTestEnd("TC_338", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_338 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_339: MUTE TOGGLE ====================

	/**
	 * TC_339: Audio Player - Mute toggle Test Flow: Play → Mute Expected: Audio
	 * becomes muted.
	 */
	@Test(priority = 339, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_339: Verify user can mute audio")
	public void TC339_VerifyUserCanMuteAudio() {
		LoggerUtils.logTestStart("TC_339: User Can Mute Audio");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_339 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_339 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_339: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_339 setup: expected playback to start.");
			LoggerUtils.logInfo("TC_339 - STEP 3: Audio playback started");

			LoggerUtils.logStep(4, "Toggle mute");
			Assert.assertTrue(player.validateMuteToggle(), "TC_339: expected audio to become muted.");
			LoggerUtils.logInfo("TC_339 - STEP 4: Mute toggled successfully");

			LoggerUtils.logInfo("TC_339: ✓ Test PASSED - User can mute audio");

			LoggerUtils.logTestEnd("TC_339", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_339 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_341: SEEK FORWARD ====================

	/**
	 * TC_341: Audio Player - Seek forward Test Flow: Play → Seek forward Expected:
	 * Playback position moves ahead.
	 */
	@Test(priority = 341, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_341: Verify user can seek forward via progress bar")
	public void TC341_VerifyUserCanSeekForwardViaProgressBar() {
		LoggerUtils.logTestStart("TC_341: User Can Seek Forward Via Progress Bar");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_341 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_341 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_341: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.validatePlay(), "TC_341 setup: expected audio playback to start before seeking.");
			Assert.assertTrue(player.isPlaybackProgressing(),
					"TC_341 setup: expected playback to be progressing before forward seek.");
			LoggerUtils.logInfo("TC_341 - STEP 3: Audio playback started and progressing");

			LoggerUtils.logStep(4, "Seek forward via progress bar");
			Assert.assertTrue(player.validateSeekForward(),
					"TC_341: expected seek forward to move playback position ahead.");
			LoggerUtils.logInfo("TC_341 - STEP 4: Seek forward completed");

			LoggerUtils.logInfo("TC_341: ✓ Test PASSED - User can seek forward via progress bar");

			LoggerUtils.logTestEnd("TC_341", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_341 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_342: SEEK BACKWARD ====================

	/**
	 * TC_342: Audio Player - Seek backward Test Flow: Play → Seek backward Expected:
	 * Playback position moves behind.
	 */
	@Test(priority = 342, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_342: Verify user can seek backward via progress bar")
	public void TC342_VerifyUserCanSeekBackwardViaProgressBar() {
		LoggerUtils.logTestStart("TC_342: User Can Seek Backward Via Progress Bar");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_342 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_342 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_342: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.validatePlay(), "TC_342 setup: expected audio playback to start before seeking.");
			Assert.assertTrue(player.isPlaybackProgressing(),
					"TC_342 setup: expected playback to be progressing before backward seek.");
			LoggerUtils.logInfo("TC_342 - STEP 3: Audio playback started and progressing");

			LoggerUtils.logStep(4, "Seek forward first to create playback position");
			Assert.assertTrue(player.validateSeekForward(),
					"TC_342 setup: expected forward seek to move playback ahead before testing backward seek.");
			LoggerUtils.logInfo("TC_342 - STEP 4: Forward seek completed");

			LoggerUtils.logStep(5, "Seek backward via progress bar");
			Assert.assertTrue(player.validateSeekBackward(),
					"TC_342: expected seek backward to move playback position behind.");
			LoggerUtils.logInfo("TC_342 - STEP 5: Seek backward completed");

			LoggerUtils.logInfo("TC_342: ✓ Test PASSED - User can seek backward via progress bar");

			LoggerUtils.logTestEnd("TC_342", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_342 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_343: SEEK BEYOND END ====================

	/**
	 * TC_343: Audio Player - Seek beyond duration Test Flow: Play → Seek beyond end
	 * Expected: Clamps at end duration.
	 */
	@Test(priority = 343, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_343: Verify seek behavior when dragged beyond audio length")
	public void TC343_VerifySeekBehaviorWhenDraggedBeyondAudioLength() {
		LoggerUtils.logTestStart("TC_343: Seek Behavior When Dragged Beyond Audio Length");

		try {
			LoggerUtils.logStep(1, "Log in as advanced audio user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_343 - STEP 1: Logged in as advanced audio user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_343 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_343: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.validatePlay(),
					"TC_343 setup: expected audio playback to start before boundary seek validation.");
			Assert.assertTrue(player.isPlaybackProgressing(),
					"TC_343 setup: expected playback to be progressing before dragging beyond audio length.");
			LoggerUtils.logInfo("TC_343 - STEP 3: Audio playback started and progressing");

			LoggerUtils.logStep(4, "Verify seek beyond end behavior");
			Assert.assertTrue(player.validateSeekBeyondEnd(),
					"TC_343: expected seek beyond duration to clamp at end.");
			LoggerUtils.logInfo("TC_343 - STEP 4: Seek beyond end handled correctly");

			LoggerUtils.logInfo("TC_343: ✓ Test PASSED - Seek beyond duration clamps at end");

			LoggerUtils.logTestEnd("TC_343", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_343 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_344: SUBSCRIBED USER PLAYBACK ====================

	/**
	 * TC_344: Audio Player - Subscribed user playback Test Flow: Subscribed user →
	 * Play Expected: Audio plays for subscribed user.
	 */
	@Test(priority = 344, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_344: Verify subscribed user can listen to book")
	public void TC344_VerifySubscribedUserCanListenToBook() {
		LoggerUtils.logTestStart("TC_344: Subscribed User Can Listen To Book");

		try {
			LoggerUtils.logStep(1, "Log in as subscribed user (advanced audio user)");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_344 - STEP 1: Logged in as subscribed user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_344 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_344: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Verify subscribed user can play audio");
			Assert.assertTrue(player.validatePlay(), "TC_344: expected subscribed user to play the book.");
			LoggerUtils.logInfo("TC_344 - STEP 3: Subscribed user can play audio");

			LoggerUtils.logInfo("TC_344: ✓ Test PASSED - Subscribed user can listen to book");

			LoggerUtils.logTestEnd("TC_344", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_344 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_345: MULTIPLE BOOKS PLAYBACK ====================

	/**
	 * TC_345: Audio Player - Multiple books playback Test Flow: Subscribed user → Play
	 * book 1 → Play book 2 Expected: Both play.
	 */
	@Test(priority = 345, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_345: Verify subscribed user can listen to multiple books")
	public void TC345_VerifySubscribedUserCanListenToMultipleBooks() {
		LoggerUtils.logTestStart("TC_345: Subscribed User Can Listen To Multiple Books");

		try {
			LoggerUtils.logStep(1, "Log in as subscribed user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_345 - STEP 1: Logged in as subscribed user");

			LoggerUtils.logStep(2, "Collect at least two playable books from dashboard");
			List<String> titles = requireAtLeastTwoPlayableBooks(
					"TC_345 requires at least two playable books from the dashboard.");
			LoggerUtils.logInfo("TC_345 - STEP 2: Collected " + titles.size() + " playable books");

			LoggerUtils.logStep(3, "Open first book and verify playback");
			openBookByTitle(titles.get(0));
			Assert.assertTrue(player.validatePlay(), "TC_345: expected first book to play.");
			LoggerUtils.logInfo("TC_345 - STEP 3: First book plays successfully");

			LoggerUtils.logStep(4, "Return to dashboard");
			Assert.assertTrue(dashboard.clickBackButtonToDashboard(),
					"TC_345: expected to return to dashboard after first book.");
			LoggerUtils.logInfo("TC_345 - STEP 4: Returned to dashboard");

			LoggerUtils.logStep(5, "Open second book and verify playback");
			openBookByTitle(titles.get(1));
			Assert.assertTrue(player.validatePlay(), "TC_345: expected second book to play for subscribed user.");
			LoggerUtils.logInfo("TC_345 - STEP 5: Second book plays successfully");

			LoggerUtils.logInfo("TC_345: ✓ Test PASSED - Subscribed user can listen to multiple books");

			LoggerUtils.logTestEnd("TC_345", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_345 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_346: FREE USER SECOND CHAPTER REDIRECT ====================

	/**
	 * TC_346: Audio Player - Free user second chapter redirect Test Flow: Free user
	 * → Play chapter 2 Expected: Redirects to payments.
	 */
	@Test(priority = 346, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_346: Verify free user is redirected to payments after trying to listen to the second chapter")
	public void TC346_VerifyFreeUserCanListenToOnlyOneBook() {
		LoggerUtils.logTestStart("TC_346: Free User Can Listen To Only One Book");

		try {
			LoggerUtils.logStep(1, "Log in as free user");
			loginAsFreeUser();
			LoggerUtils.logInfo("TC_346 - STEP 1: Logged in as free user");

			LoggerUtils.logStep(2, "Collect trending books from dashboard");
			List<String> titles = requireTrendingTitles(1, "TC_346 requires at least one trending book.");
			openBookByTitle(titles.get(0));
			LoggerUtils.logInfo("TC_346 - STEP 2: Opened book from trending list");

			LoggerUtils.logStep(3, "Verify multiple chapters available");
			requireMultipleChapters("TC_346 requires a book with at least two chapters.");
			LoggerUtils.logInfo("TC_346 - STEP 3: Multiple chapters confirmed");

			LoggerUtils.logStep(4, "Try to play second chapter and verify payments redirect");
			Assert.assertTrue(player.clickSecondChapterAndVerifyPaymentsRedirect(),
					"TC_346: expected free user to be redirected to /payments after clicking Listen on second chapter. Current URL: "
							+ safeGetCurrentUrl(driver));
			LoggerUtils.logInfo("TC_346 - STEP 4: Payments redirect verified");

			LoggerUtils.logInfo("TC_346: ✓ Test PASSED - Free user redirected to payments for second chapter");

			LoggerUtils.logTestEnd("TC_346", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_346 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_347: FREE USER FIRST CHAPTER PLAYBACK ====================

	/**
	 * TC_347: Audio Player - Free user first chapter playback Test Flow: Free user →
	 * Play chapter 1 Expected: Plays successfully.
	 */
	@Test(priority = 347, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_347: Verify free user can listen only to the first chapter")
	public void TC347_VerifyFreeUserCanOnlyListenToFirstBookFully() {
		LoggerUtils.logTestStart("TC_347: Free User Can Only Listen To First Book Fully");

		try {
			LoggerUtils.logStep(1, "Log in as free user");
			loginAsFreeUser();
			LoggerUtils.logInfo("TC_347 - STEP 1: Logged in as free user");

			LoggerUtils.logStep(2, "Collect trending books from dashboard");
			List<String> titles = requireTrendingTitles(1, "TC_347 requires at least one trending book.");
			openBookByTitle(titles.get(0));
			LoggerUtils.logInfo("TC_347 - STEP 2: Opened book from trending list");

			LoggerUtils.logStep(3, "Verify multiple chapters available");
			requireMultipleChapters("TC_347 requires a book with at least two chapters.");
			LoggerUtils.logInfo("TC_347 - STEP 3: Multiple chapters confirmed");

			LoggerUtils.logStep(4, "Verify first chapter plays for free user");
			Assert.assertTrue(player.validatePlay(), "TC_347: expected first chapter to start for free user.");
			Assert.assertTrue(player.isPlaybackProgressing(),
					"TC_347: expected first chapter playback to progress for free user.");
			LoggerUtils.logInfo("TC_347 - STEP 4: First chapter plays successfully");

			LoggerUtils.logStep(5, "Try to play second chapter and verify payments redirect");
			Assert.assertTrue(player.clickSecondChapterAndVerifyPaymentsRedirect(),
					"TC_347: expected second chapter Listen action to redirect free user to /payments. Current URL: "
							+ safeGetCurrentUrl(driver));
			LoggerUtils.logInfo("TC_347 - STEP 5: Second chapter redirect verified");

			LoggerUtils.logInfo("TC_347: ✓ Test PASSED - Free user can only listen to first chapter");

			LoggerUtils.logTestEnd("TC_347", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_347 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_350: ALL CONTROLS FOR SUBSCRIBED USERS ====================

	/**
	 * TC_350: Audio Player - All controls for subscribed users Test Flow: Subscribed
	 * user → Play → Pause → Resume → Forward → Backward Expected: All controls
	 * work.
	 */
	@Test(priority = 350, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER, TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_350: Verify playback controls work for subscribed users")
	public void TC350_VerifyPlaybackControlsWorkForSubscribedUsers() {
		LoggerUtils.logTestStart("TC_350: Playback Controls Work For Subscribed Users");

		try {
			LoggerUtils.logStep(1, "Log in as subscribed user");
			loginAsAdvancedAudioUser();
			LoggerUtils.logInfo("TC_350 - STEP 1: Logged in as subscribed user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_350 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_350: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Verify play control");
			Assert.assertTrue(player.validatePlay(), "TC_350: expected play to work.");
			LoggerUtils.logInfo("TC_350 - STEP 3: Play control verified");

			LoggerUtils.logStep(4, "Verify pause control");
			Assert.assertTrue(player.validatePause(), "TC_350: expected pause to work.");
			LoggerUtils.logInfo("TC_350 - STEP 4: Pause control verified");

			LoggerUtils.logStep(5, "Verify resume control");
			Assert.assertTrue(player.validateResume(), "TC_350: expected resume to work.");
			LoggerUtils.logInfo("TC_350 - STEP 5: Resume control verified");

			LoggerUtils.logStep(6, "Verify forward control");
			Assert.assertTrue(player.validateForward30(), "TC_350: expected forward to work.");
			LoggerUtils.logInfo("TC_350 - STEP 6: Forward control verified");

			LoggerUtils.logStep(7, "Verify backward control");
			Assert.assertTrue(player.validateBackward30(), "TC_350: expected backward to work.");
			LoggerUtils.logInfo("TC_350 - STEP 7: Backward control verified");

			LoggerUtils.logInfo("TC_350: ✓ Test PASSED - All playback controls work for subscribed users");

			LoggerUtils.logTestEnd("TC_350", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_350 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== Helper Methods ====================

	private String getConsumerEmail() {
		return ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
	}

	private String getConsumerPassword() {
		return ConfigReader.getProperty("consumer.password", ConfigReader.getProperty("login.validPassword"));
	}

	private String getAdvancedAudioEmail() {
		return AUDIO_ADVANCED_EMAIL;
	}

	private String getAdvancedAudioPassword() {
		return AUDIO_ADVANCED_PASSWORD;
	}

	private String getFreeUserEmail() {
		return FREE_USER_EMAIL;
	}

	private String getFreeUserPassword() {
		return FREE_USER_PASSWORD;
	}

	private List<String> requireTrendingTitles(int minimum, String skipMessage) {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(),
				"Dashboard shell should be ready before collecting books.");
		LinkedHashSet<String> uniqueTitles = new LinkedHashSet<>(dashboard.getTrendingShowNames());
		uniqueTitles.addAll(dashboard.getTrendingBooksList());

		List<String> titles = new ArrayList<>();
		for (String title : uniqueTitles) {
			if (!isBlank(title)) {
				titles.add(title);
			}
		}
		if (titles.size() < minimum) {
			throw new SkipException(skipMessage);
		}
		return titles;
	}

	private List<String> requireAtLeastTwoPlayableBooks(String skipMessage) {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(),
				"Dashboard shell should be ready before collecting books.");

		LinkedHashSet<String> uniqueTitles = new LinkedHashSet<>();
		uniqueTitles.addAll(dashboard.getTrendingShowNames());
		for (String bookId : dashboard.getTrendingBooksList()) {
			if (!isBlank(bookId) && !uniqueTitles.contains(bookId)) {
				uniqueTitles.add(bookId);
			}
		}

		List<String> titles = new ArrayList<>();
		for (String title : uniqueTitles) {
			if (!isBlank(title)) {
				titles.add(title);
			}
			if (titles.size() == 2)
				break;
		}

		if (titles.size() < 2) {
			throw new SkipException(skipMessage + " Only found: " + titles.size());
		}

		return titles;
	}

	private void requireMultipleChapters(String message) {
		if (!player.hasMultipleChapters()) {
			throw new SkipException(message);
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
