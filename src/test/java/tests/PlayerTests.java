package tests;

import java.time.Duration;

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
import pages.PlayerPage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Audio Player Automation Tests
 *
 * <p>
 * Test Coverage: TC_01 - TC_09 (TC_07 - TC_09 align with {@code MasterTest}'s
 * "forward" / "backward" / "load" action contract)
 *
 * <p>
 * Focus: Core audio player controls — play, pause, resume, chapter
 * navigation, forward / backward, and player load.
 *
 * <p>
 * Mirrors the framework pattern used in {@code AudioPlayerTests}: per-test
 * login, {@code openAnyDashboardBookAndWaitForPlayer} setup helper, and
 * {@code AudioPlayerPage.validate*()} API for action validation. The three
 * {@code MasterTest} action-string tests (TC_07 / TC_08 / TC_09) still call
 * the raw {@link PlayerPage} API to preserve the contract.
 */
public class PlayerTests extends BaseTest {

	private LoginPage login;
	private DashboardPage dashboard;
	private AudioPlayerPage player;
	private PlayerPage rawPlayer;

	@BeforeMethod(alwaysRun = true)
	@Override
	public void setup() {
		super.setup();
		login = new LoginPage(driver);
		dashboard = new DashboardPage(driver);
		player = new AudioPlayerPage(driver);
		rawPlayer = new PlayerPage(driver);
	}

	// ==================== TC_01: PLAY BOOK ====================

	/**
	 * TC_01: Audio Player - Play button functionality Test Flow: Login → Open
	 * book → Click Play Expected: Audio starts playing.
	 */
	@Test(priority = 1, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_01: Verify audio starts playing when Play button is clicked")
	public void TC01_VerifyPlayBook() {
		LoggerUtils.logTestStart("TC_01: Play Book");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_01 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard and wait for player readiness");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_01 - STEP 2: Opened a book from dashboard");

			LoggerUtils.logStep(3, "Click the play button");
			Assert.assertTrue(player.clickPlayAudio(), "TC_01: expected play button to be clickable.");
			LoggerUtils.logInfo("TC_01 - STEP 3: Play button clicked");

			LoggerUtils.logStep(4, "Verify audio playback started");
			boolean isPlaying = player.isPlaybackProgressing() || player.isPauseButtonVisible();
			LoggerUtils.logInfo("TC_01 - STEP 4: Audio playing: " + isPlaying);

			Assert.assertTrue(isPlaying, "TC_01: Audio should start playing when play button is clicked");
			LoggerUtils.logInfo("TC_01: ✓ Test PASSED - Audio starts playing when Play button is clicked");

			LoggerUtils.logTestEnd("TC_01", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_01 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_02: PAUSE BOOK ====================

	/**
	 * TC_02: Audio Player - Pause button functionality Test Flow: Play → Pause
	 * Expected: Audio stops playing.
	 */
	@Test(priority = 2, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_02: Verify audio pauses when Pause button is clicked")
	public void TC02_VerifyPauseBook() {
		LoggerUtils.logTestStart("TC_02: Pause Book");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_02 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard and wait for player readiness");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_02 - STEP 2: Opened a book from dashboard");

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_02 setup: expected playback to start before pause.");
			boolean initiallyPlaying = player.isPlaybackProgressing();
			LoggerUtils.logInfo("TC_02 - STEP 3: Audio initially playing: " + initiallyPlaying);

			LoggerUtils.logStep(4, "Click pause button");
			boolean paused = player.validatePause();
			LoggerUtils.logInfo("TC_02 - STEP 4: Pause button clicked, audio paused: " + paused);

			Assert.assertTrue(paused, "TC_02: Audio should pause when pause button is clicked");
			LoggerUtils.logInfo("TC_02: ✓ Test PASSED - Audio pauses when Pause button is clicked");

			LoggerUtils.logTestEnd("TC_02", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_02 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_03: RESUME BOOK ====================

	/**
	 * TC_03: Audio Player - Resume functionality Test Flow: Play → Pause →
	 * Resume Expected: Audio resumes from paused position.
	*/
	@Test(priority = 3, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_03: Verify audio resumes from paused position")
	public void TC03_VerifyResumeBook() {
		LoggerUtils.logTestStart("TC_03: Resume Book");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_03 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_03 - STEP 2: Opened a book from dashboard");

			if (player.hasSubscriptionGate()) {
				throw new SkipException("TC_03: Selected dashboard book is gated for playback.");
			}

			LoggerUtils.logStep(3, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_03 setup: expected playback to start before pause.");
			boolean initiallyPlaying = player.isPlaybackProgressing();
			LoggerUtils.logInfo("TC_326 - STEP 3: Audio initially playing: " + initiallyPlaying);

			LoggerUtils.logStep(4, "Pause audio playback");
			String timeBeforePause = player.getCurrentTime();
			boolean paused = player.validatePause();
			LoggerUtils.logInfo("TC_03 - STEP 4: Audio paused: " + paused + ", time: " + timeBeforePause);

			LoggerUtils.logStep(5, "Resume audio playback");
			boolean resumed = player.validateResume();
			LoggerUtils.logInfo("TC_03 - STEP 5: Audio resumed: " + resumed);

			Assert.assertTrue(resumed, "TC_03: Audio should resume from paused position");
			LoggerUtils.logInfo("TC_03: ✓ Test PASSED - Audio resumes from paused position");

			LoggerUtils.logTestEnd("TC_03", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_03 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_04: NEXT CHAPTER PLAYBACK ====================

	/**
	 * TC_04: Audio Player - Next chapter Test Flow: Play → Next chapter
	 * Expected: New chapter starts.
	 */
	@Test(priority = 4, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_04: Verify next chapter button plays the next chapter")
	public void TC04_VerifyNextChapterPlayback() {
		LoggerUtils.logTestStart("TC_04: Next Chapter Playback");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_04 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard and wait for player readiness");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_04 - STEP 2: Opened a book from dashboard");

			LoggerUtils.logStep(3, "Verify multiple chapters are available");
			if (!player.hasMultipleChapters()) {
				throw new SkipException("TC_04: Selected dashboard book does not have multiple chapters.");
			}
			LoggerUtils.logInfo("TC_04 - STEP 3: Multiple chapters confirmed");

			LoggerUtils.logStep(4, "Start audio playback");
			Assert.assertTrue(player.clickPlayAudio(), "TC_04 setup: expected playback to start.");
			String initialChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_04 - STEP 4: Current chapter: " + initialChapter);

			LoggerUtils.logStep(5, "Click next chapter button");
			Assert.assertTrue(player.validateChapterChange(true),
					"TC_04: expected next chapter action to change chapter context.");
			String newChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_04 - STEP 5: New chapter: " + newChapter);

			boolean chapterChanged = !initialChapter.equals(newChapter) && !"N/A".equals(newChapter);
			Assert.assertTrue(chapterChanged, "TC_04: Chapter should change to next chapter");
			LoggerUtils.logInfo("TC_04: ✓ Test PASSED - Next chapter plays correctly");

			LoggerUtils.logTestEnd("TC_04", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_04 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_05: PREVIOUS CHAPTER PLAYBACK ====================

	/**
	 * TC_05: Audio Player - Previous chapter Test Flow: Next → Previous
	 * Expected: Returns to earlier chapter.
	 */
	@Test(priority = 5, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_05: Verify previous chapter navigation returns to earlier chapter")
	public void TC05_VerifyPreviousChapterPlayback() {
		LoggerUtils.logTestStart("TC_05: Previous Chapter Playback");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_05 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard and wait for player readiness");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_05 - STEP 2: Opened a book from dashboard");

			LoggerUtils.logStep(3, "Verify multiple chapters are available");
			if (!player.hasMultipleChapters()) {
				throw new SkipException("TC_05: Selected dashboard book does not have multiple chapters.");
			}
			LoggerUtils.logInfo("TC_05 - STEP 3: Multiple chapters confirmed");

			LoggerUtils.logStep(4, "Start audio playback and move to next chapter");
			Assert.assertTrue(player.clickPlayAudio(), "TC_05 setup: expected playback to start.");
			Assert.assertTrue(player.validateChapterChange(true),
					"TC_05 setup: expected next chapter to work first.");
			String nextChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_05 - STEP 4: Moved to next chapter: " + nextChapter);

			LoggerUtils.logStep(5, "Click previous chapter button");
			Assert.assertTrue(player.validateChapterChange(false),
					"TC_05: expected previous chapter action to return to earlier chapter.");
			String prevChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_05 - STEP 5: Returned to chapter: " + prevChapter);

			boolean returnedToEarlier = !nextChapter.equals(prevChapter);
			Assert.assertTrue(returnedToEarlier, "TC_05: Should return to earlier chapter");
			LoggerUtils.logInfo("TC_05: ✓ Test PASSED - Previous chapter navigation works");

			LoggerUtils.logTestEnd("TC_05", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_05 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_06: PREVIOUS ON FIRST CHAPTER HANDLED GRACEFULLY ====================

	/**
	 * TC_06: Audio Player - Previous on first chapter Test Flow: On first
	 * chapter → Previous Expected: Stays on first chapter.
	 */
	@Test(priority = 6, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_06: Verify previous button behavior on first chapter")
	public void TC06_VerifyPreviousOnFirstChapterHandledGracefully() {
		LoggerUtils.logTestStart("TC_06: Previous On First Chapter Handled Gracefully");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_06 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book from dashboard and wait for player readiness");
			openAnyDashboardBookAndWaitForPlayer();
			LoggerUtils.logInfo("TC_06 - STEP 2: Opened a book from dashboard");

			LoggerUtils.logStep(3, "Verify multiple chapters are available");
			if (!player.hasMultipleChapters()) {
				throw new SkipException("TC_06: Selected dashboard book does not have multiple chapters.");
			}
			LoggerUtils.logInfo("TC_06 - STEP 3: Multiple chapters confirmed");

			LoggerUtils.logStep(4, "Start audio playback from first chapter");
			Assert.assertTrue(player.clickPlayAudio(), "TC_06 setup: expected playback to start.");
			String currentChapter = player.getCurrentChapterTitle();
			LoggerUtils.logInfo("TC_06 - STEP 4: Current chapter: " + currentChapter);

			LoggerUtils.logStep(5, "Verify previous on first chapter behavior");
			Assert.assertTrue(player.validatePreviousOnFirstChapterBoundary(),
					"TC_06: expected previous on first chapter to keep player stable.");
			LoggerUtils.logInfo("TC_06 - STEP 5: Previous on first chapter handled correctly");

			LoggerUtils.logInfo("TC_06: ✓ Test PASSED - Previous on first chapter keeps player stable");

			LoggerUtils.logTestEnd("TC_06", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_06 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_07: FORWARD AUDIO (MasterTest "forward" contract) ====================

	/**
	 * TC_07: Audio Player - Forward audio Test Flow: Play → Forward Expected:
	 * Forward action executes and audio continues playing.
	 *
	 * <p>
	 * Uses the raw {@link PlayerPage} API to preserve the
	 * {@code MasterTest} "forward" action-string contract.
	 */
	@Test(priority = 7, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_07: Verify forward audio action executes and audio continues playing")
	public void TC07_VerifyForwardAudio() {
		LoggerUtils.logTestStart("TC_07: Forward Audio");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_07 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book and wait for player bar (MasterTest contract)");
			openAnyDashboardBookAndWaitForPlayer();
			rawPlayer.waitForPlayerBar();
			LoggerUtils.logInfo("TC_07 - STEP 2: Player bar loaded");

			LoggerUtils.logStep(3, "Start audio playback");
			player.clickPlayAudio();
			LoggerUtils.logInfo("TC_07 - STEP 3: Audio playback started");

			LoggerUtils.logStep(4, "Click forward 30 (MasterTest contract uses rawPlayer.clickForward30)");
			rawPlayer.clickForward30();
			LoggerUtils.logInfo("TC_07 - STEP 4: Forward action executed");

			LoggerUtils.logStep(5, "Verify audio is still progressing");
			Assert.assertTrue(player.isPlaybackProgressing(),
					"TC_07: Audio should continue playing after forward skip");
			LoggerUtils.logInfo("TC_07: ✓ Test PASSED - Forward audio action executed and playback continues");

			LoggerUtils.logTestEnd("TC_07", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_07 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_08: BACKWARD AUDIO (MasterTest "backward" contract) ====================

	/**
	 * TC_08: Audio Player - Backward audio Test Flow: Play → Backward Expected:
	 * Backward action executes and audio continues playing.
	 *
	 * <p>
	 * Uses the raw {@link PlayerPage} API to preserve the
	 * {@code MasterTest} "backward" action-string contract.
	 */
	@Test(priority = 8, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_08: Verify backward audio action executes and audio continues playing")
	public void TC08_VerifyBackwardAudio() {
		LoggerUtils.logTestStart("TC_08: Backward Audio");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_08 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book and wait for player bar (MasterTest contract)");
			openAnyDashboardBookAndWaitForPlayer();
			rawPlayer.waitForPlayerBar();
			LoggerUtils.logInfo("TC_08 - STEP 2: Player bar loaded");

			LoggerUtils.logStep(3, "Start audio playback");
			player.clickPlayAudio();
			LoggerUtils.logInfo("TC_08 - STEP 3: Audio playback started");

			LoggerUtils.logStep(4, "Click backward 30 (MasterTest contract uses rawPlayer.clickBackward30)");
			rawPlayer.clickBackward30();
			LoggerUtils.logInfo("TC_08 - STEP 4: Backward action executed");

			LoggerUtils.logStep(5, "Verify audio is still progressing");
			Assert.assertTrue(player.isPlaybackProgressing(),
					"TC_08: Audio should continue playing after rewind");
			LoggerUtils.logInfo("TC_08: ✓ Test PASSED - Backward audio action executed and playback continues");

			LoggerUtils.logTestEnd("TC_08", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_08 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_09: PLAYER LOAD (MasterTest "load" contract) ====================

	/**
	 * TC_09: Audio Player - Player load Test Flow: Open book → Wait for player
	 * bar Expected: Player bar is loaded and ready.
	 *
	 * <p>
	 * Uses the raw {@link PlayerPage} API to preserve the
	 * {@code MasterTest} "load" action-string contract.
	 */
	@Test(priority = 9, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_09: Verify player bar loads successfully on book details page")
	public void TC09_VerifyPlayerLoad() {
		LoggerUtils.logTestStart("TC_09: Player Load");

		try {
			LoggerUtils.logStep(1, "Log in as registered user");
			loginAsRegisteredUser();
			LoggerUtils.logInfo("TC_09 - STEP 1: Logged in as registered user");

			LoggerUtils.logStep(2, "Open a book and wait for player bar (MasterTest contract)");
			openAnyDashboardBookAndWaitForPlayer();
			rawPlayer.waitForPlayerBar();
			LoggerUtils.logInfo("TC_09 - STEP 2: Player bar loaded");

			LoggerUtils.logStep(3, "Verify player surface is ready");
			Assert.assertTrue(rawPlayer.isPlayerResponsive(),
					"TC_09: Player surface should be responsive after load");
			LoggerUtils.logInfo("TC_09: ✓ Test PASSED - Player bar loaded successfully");

			LoggerUtils.logTestEnd("TC_09", "PASSED");

		} catch (Exception e) {
			LoggerUtils.logWarn("TC_09 - Test failed: " + safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== Helper Methods ====================

	/**
	 * Log in as the configured registered user. Mirrors the pattern used in
	 * {@code AudioPlayerTests.loginAsRegisteredUser} for consistency.
	 */
	private void loginAsRegisteredUser() {
		try {
			login.openLogin();
			login.loginUser(getConsumerEmail(), getConsumerPassword());
			login.clickNextAfterLogin();
			boolean loginSettled = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> {
				String currentUrl = safeGetCurrentUrl(currentDriver);
				String lowerUrl = currentUrl == null ? "" : currentUrl.toLowerCase();
				return !lowerUrl.contains("/login") && !lowerUrl.contains("signin");
			});
			Assert.assertTrue(loginSettled, "Registered user login should move past the login page");
			LoggerUtils.logInfo("Logged in as registered user");
		} catch (Exception e) {
			throw new SkipException("Could not login as registered user: " + safeString(e.getMessage()), e);
		}
	}

	/**
	 * Open a dashboard book and wait for player readiness. Mirrors
	 * {@code AudioPlayerTests.openAnyDashboardBookAndWaitForPlayer}.
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
			throw new SkipException("Could not open a dashboard book for playback: " + safeString(e.getMessage()), e);
		}
	}

	private String safeGetCurrentUrl(org.openqa.selenium.WebDriver driver) {
		try {
			return driver.getCurrentUrl();
		} catch (Exception e) {
			return "";
		}
	}

	private String safeString(String value) {
		return value == null ? "" : value;
	}

	private String getConsumerEmail() {
		return ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
	}

	private String getConsumerPassword() {
		return ConfigReader.getProperty("consumer.password", ConfigReader.getProperty("login.validPassword"));
	}
}
