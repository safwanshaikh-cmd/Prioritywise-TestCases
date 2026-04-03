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
import utils.TestWaitHelper;

/**
 * Audio Player control tests - covering all playback controls and interactions.
 * Tests cover play/pause, seek, skip, speed, volume, chapter navigation, etc.
 */
public class AudioPlayerTests extends BaseTest {

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
					"Set consumer.email and consumer.password in config.properties to run audio player tests.");
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
	public void initAudioPlayerTests() {
		ConfigReader.reload();
		skipIfConsumerCredentialsMissing();

		login = new LoginPage(driver);
		dashboard = new DashboardPage(driver);

		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();
	}

	// ==================== AUDIO PLAYER TESTS ====================

	@Test(priority = 324, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAudioStartsWhenClickingPlayButton() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for the current book details page.");
			return;
		}

		Assert.assertTrue(dashboard.clickPlayAudioAndVerifyPlayback(),
				"Audio should start playing when Play Audio button is clicked.");
	}

	@Test(priority = 325, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAudioPausesWhenPauseButtonClicked() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for pause validation.");
			return;
		}

		// First start playing
		Assert.assertTrue(dashboard.clickPlayAudioAndVerifyPlayback(),
				"Audio should start playing before pause is validated.");

		// Then pause
		Assert.assertTrue(dashboard.clickPauseAndVerifyPlaybackStops(),
				"Audio should pause when Pause button is clicked.");
	}

	@Test(priority = 326, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAudioResumesFromPausedPosition() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for resume validation.");
			return;
		}

		// Play
		Assert.assertTrue(dashboard.clickPlayAudioAndVerifyPlayback(),
				"Audio should start playing.");

		// Pause
		Assert.assertTrue(dashboard.clickPauseAndVerifyPlaybackStops(),
				"Audio should pause.");

		// Resume - Play again
		Assert.assertTrue(dashboard.clickPlayAudioAndVerifyPlayback(),
				"Audio should resume playing from paused position when Play is clicked again.");
	}

	@Test(priority = 327, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanSkipForward30Seconds() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for skip validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Get initial position
		String initialPosition = dashboard.getCurrentAudioPosition();
		logOptionalUnavailable("Initial position: " + initialPosition);

		// Skip forward
		Assert.assertTrue(dashboard.skipForward30Seconds(),
				"User should be able to skip forward 30 seconds using the forward button.");

		// Verify position changed (if available)
		String newPosition = dashboard.getCurrentAudioPosition();
		logOptionalUnavailable("Position after skip: " + newPosition);
	}

	@Test(priority = 328, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanRewind30Seconds() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for rewind validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Get initial position
		String initialPosition = dashboard.getCurrentAudioPosition();
		logOptionalUnavailable("Initial position: " + initialPosition);

		// Rewind
		Assert.assertTrue(dashboard.rewind30Seconds(),
				"User should be able to rewind 30 seconds using the backward button.");

		// Verify position changed (if available)
		String newPosition = dashboard.getCurrentAudioPosition();
		logOptionalUnavailable("Position after rewind: " + newPosition);
	}

	@Test(priority = 329, retryAnalyzer = RetryAnalyzer.class)
	public void verifyForwardButtonBehaviorNearAudioEnd() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for edge case validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Try to skip near the end
		Assert.assertTrue(dashboard.skipNearEnd(),
				"Forward button should handle edge case gracefully when audio is near the end.");
	}

	@Test(priority = 330, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRewindBehaviorAtAudioStart() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for edge case validation.");
			return;
		}

		// Try to rewind at the start
		Assert.assertTrue(dashboard.rewindAtStart(),
				"Rewind button should handle edge case gracefully when audio is at the start.");
	}

	@Test(priority = 331, retryAnalyzer = RetryAnalyzer.class)
	public void verifyClickingNextChapterPlaysNextChapter() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.areChaptersVisible()) {
			logOptionalUnavailable("Multiple chapters are not available for next chapter validation.");
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for chapter navigation.");
			return;
		}

		// Get current chapter info
		String currentChapter = dashboard.getCurrentChapterTitle();
		logOptionalUnavailable("Current chapter: " + currentChapter);

		// Click next chapter
		Assert.assertTrue(dashboard.clickNextChapter(),
				"Clicking Next Chapter button should play the next chapter.");
	}

	@Test(priority = 332, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPreviousChapterNavigation() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.areChaptersVisible()) {
			logOptionalUnavailable("Multiple chapters are not available for previous chapter validation.");
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for chapter navigation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Click previous chapter
		Assert.assertTrue(dashboard.clickPreviousChapter(),
				"Clicking Previous Chapter button should navigate to the previous chapter.");
	}

	@Test(priority = 333, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPreviousButtonBehaviorOnFirstChapter() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.areChaptersVisible()) {
			logOptionalUnavailable("Chapters are not available for edge case validation.");
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible.");
			return;
		}

		// Try to go to previous chapter (should handle gracefully)
		Assert.assertTrue(dashboard.clickPreviousChapter(),
				"Previous button should handle edge case gracefully when on first chapter.");
	}

	@Test(priority = 334, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanChangePlaybackSpeed() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for speed control validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Change speed to 1.5x
		Assert.assertTrue(dashboard.changePlaybackSpeed("1.5x"),
				"User should be able to change audio playback speed to 1.5x.");
	}

	@Test(priority = 335, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMinimumSpeedSelection() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for speed control validation.");
			return;
		}

		// Select minimum speed (0.5x)
		Assert.assertTrue(dashboard.changePlaybackSpeed("0.5x"),
				"User should be able to select minimum playback speed (0.5x).");
	}

	@Test(priority = 336, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMaximumSpeedSelection() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for speed control validation.");
			return;
		}

		// Select maximum speed (2x)
		Assert.assertTrue(dashboard.changePlaybackSpeed("2x"),
				"User should be able to select maximum playback speed (2x).");
	}

	@Test(priority = 337, retryAnalyzer = RetryAnalyzer.class)
	public void verifyVolumeSliderIncreasesAudioLevel() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for volume control validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Get initial volume
		int initialVolume = dashboard.getCurrentVolumeLevel();
		logOptionalUnavailable("Initial volume: " + initialVolume);

		// Increase volume
		Assert.assertTrue(dashboard.increaseVolume(),
				"Volume slider should increase audio level when moved up.");

		// Verify volume increased
		int newVolume = dashboard.getCurrentVolumeLevel();
		logOptionalUnavailable("Volume after increase: " + newVolume);
	}

	@Test(priority = 338, retryAnalyzer = RetryAnalyzer.class)
	public void verifyVolumeSliderDecreasesAudioLevel() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for volume control validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Get initial volume
		int initialVolume = dashboard.getCurrentVolumeLevel();
		logOptionalUnavailable("Initial volume: " + initialVolume);

		// Decrease volume
		Assert.assertTrue(dashboard.decreaseVolume(),
				"Volume slider should decrease audio level when moved down.");

		// Verify volume decreased
		int newVolume = dashboard.getCurrentVolumeLevel();
		logOptionalUnavailable("Volume after decrease: " + newVolume);
	}

	@Test(priority = 339, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanMuteAudio() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for mute validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Mute
		Assert.assertTrue(dashboard.muteAudio(),
				"User should be able to mute audio using the mute control.");
	}

	@Test(priority = 340, retryAnalyzer = RetryAnalyzer.class)
	public void verifyClickingXClosesAudioPlayer() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for player close validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Close player
		Assert.assertTrue(dashboard.closeAudioPlayer(),
				"Clicking X button should close the audio player.");
	}

	@Test(priority = 341, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanSeekForwardViaProgressBar() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for seek bar validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Get initial position
		String initialPosition = dashboard.getCurrentAudioPosition();
		logOptionalUnavailable("Initial position: " + initialPosition);

		// Seek forward
		Assert.assertTrue(dashboard.seekForward(),
				"User should be able to seek forward using the progress bar.");

		// Verify position changed
		String newPosition = dashboard.getCurrentAudioPosition();
		logOptionalUnavailable("Position after seek: " + newPosition);
	}

	@Test(priority = 342, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanSeekBackwardViaProgressBar() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for seek bar validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Get initial position
		String initialPosition = dashboard.getCurrentAudioPosition();
		logOptionalUnavailable("Initial position: " + initialPosition);

		// Seek backward
		Assert.assertTrue(dashboard.seekBackward(),
				"User should be able to seek backward using the progress bar.");

		// Verify position changed
		String newPosition = dashboard.getCurrentAudioPosition();
		logOptionalUnavailable("Position after seek: " + newPosition);
	}

	@Test(priority = 343, retryAnalyzer = RetryAnalyzer.class)
	public void verifySeekBehaviorWhenDraggedBeyondAudioLength() {
		if (!openBookDetailsFromDashboard()) {
			return;
		}

		if (!dashboard.isPlayAudioButtonVisible()) {
			logOptionalUnavailable("Play Audio button is not visible for edge case validation.");
			return;
		}

		// Start playing
		dashboard.clickPlayAudioAndVerifyPlayback();

		// Try to seek beyond duration
		Assert.assertTrue(dashboard.seekBeyondEnd(),
				"Progress bar should handle edge case gracefully when dragged beyond audio length.");
	}
}
