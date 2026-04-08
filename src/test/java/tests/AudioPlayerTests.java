package tests;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.AudioPlayerPage;
import pages.DashboardPage;
import pages.LoginPage;
import pages.PaymentPage;
import pages.SubscriptionPage;
import utils.ConfigReader;

public class AudioPlayerTests extends BaseTest {

	private static final String AUDIO_ADVANCED_EMAIL = "safwan.shaikh+041@11axis.com";
	private static final String AUDIO_ADVANCED_PASSWORD = "Password@123";

	private DashboardPage dashboard;
	private LoginPage login;
	private AudioPlayerPage audioPlayer;

	@BeforeMethod(alwaysRun = true)
	public void initPages() {
		ConfigReader.reload();
		dashboard = new DashboardPage(driver);
		login = new LoginPage(driver);
		audioPlayer = new AudioPlayerPage(driver);
	}

	@Test(priority = 324, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAudioStartsWhenClickingPlayButton() {
		openConsumerBook();
		Assert.assertTrue(audioPlayer.validatePlay(), "TC_324: expected playback to start after clicking Play.");
	}

	@Test(priority = 325, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAudioPausesWhenPauseButtonClicked() {
		openConsumerBook();
		Assert.assertTrue(audioPlayer.validatePause(), "TC_325: expected playback time to stop changing after Pause.");
	}

	@Test(priority = 326, retryAnalyzer = RetryAnalyzer.class)
	public void verifyAudioResumesFromPausedPosition() {
		openConsumerBook();
		Assert.assertTrue(audioPlayer.validateResume(), "TC_326: expected playback to resume from paused position.");
	}

	@Test(priority = 327, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanSkipForward30Seconds() {
		openConsumerBook();
		Assert.assertTrue(audioPlayer.validateForward30(), "TC_327: expected playback time to increase after forward.");
	}

	@Test(priority = 328, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanRewind30Seconds() {
		openConsumerBook();
		Assert.assertTrue(audioPlayer.validateForward30(), "TC_328 setup: expected playback to move forward first.");
		Assert.assertTrue(audioPlayer.validateBackward30(),
				"TC_328: expected playback time to decrease after backward.");
	}

	@Test(priority = 329, retryAnalyzer = RetryAnalyzer.class)
	public void verifyForwardButtonBehaviorNearAudioEnd() {
		openConsumerBook();
		Assert.assertTrue(audioPlayer.validateSkipNearEnd(),
				"TC_329: expected forward near end to clamp within duration.");
	}

	@Test(priority = 330, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRewindBehaviorAtAudioStart() {
		openConsumerBook();
		Assert.assertTrue(audioPlayer.validateBackwardAtStart(),
				"TC_330: expected backward at start to keep playback at or above zero.");
	}

	@Test(priority = 331, retryAnalyzer = RetryAnalyzer.class)
	public void verifyClickingNextChapterPlaysNextChapter() {
		openAdvancedAudioBook();
		requireMultipleChapters("TC_331 requires a book with multiple chapters.");
		Assert.assertTrue(audioPlayer.validateChapterChange(true),
				"TC_331: expected next chapter action to change chapter context.");
	}

	@Test(priority = 332, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPreviousChapterNavigation() {
		openAdvancedAudioBook();
		requireMultipleChapters("TC_332 requires a book with multiple chapters.");
		Assert.assertTrue(audioPlayer.validateChapterChange(true),
				"TC_332 setup: expected next chapter to work first.");
		Assert.assertTrue(audioPlayer.validateChapterChange(false),
				"TC_332: expected previous chapter action to return to earlier chapter.");
	}

	@Test(priority = 333, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPreviousButtonBehaviorOnFirstChapter() {
		openAdvancedAudioBook();
		requireMultipleChapters("TC_333 requires a book with multiple chapters.");
		Assert.assertTrue(audioPlayer.validatePreviousOnFirstChapterBoundary(),
				"TC_333: expected previous on first chapter to keep player stable.");
	}

	@Test(priority = 334, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanChangePlaybackSpeed() {
		openAdvancedAudioBook();
		Assert.assertTrue(audioPlayer.validatePlaybackSpeed("1.5x"),
				"TC_334: expected playback speed to change to 1.5x.");
	}

	@Test(priority = 335, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMinimumSpeedSelection() {
		openAdvancedAudioBook();
		Assert.assertTrue(audioPlayer.validatePlaybackSpeed("0.5x"),
				"TC_335: expected playback speed to change to 0.5x.");
	}

	@Test(priority = 336, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMaximumSpeedSelection() {
		openAdvancedAudioBook();
		Assert.assertTrue(audioPlayer.validatePlaybackSpeed("2x"), "TC_336: expected playback speed to change to 2x.");
	}

	@Test(priority = 337, retryAnalyzer = RetryAnalyzer.class)
	public void verifyVolumeSliderIncreasesAudioLevel() {
		openAdvancedAudioBook();
		Assert.assertTrue(audioPlayer.hasAccessibleVolumeControl(),
				"TC_337 precondition failed: expected an accessible volume control or readable media volume after playback started.");
		Assert.assertTrue(audioPlayer.validateVolumeChange(true),
				"TC_337: expected volume to increase after slider adjustment.");
	}

	@Test(priority = 338, retryAnalyzer = RetryAnalyzer.class)
	public void verifyVolumeSliderDecreasesAudioLevel() {
		openAdvancedAudioBook();
		Assert.assertTrue(audioPlayer.hasAccessibleVolumeControl(),
				"TC_338 precondition failed: expected an accessible volume control or readable media volume after playback started.");
		Assert.assertTrue(audioPlayer.validateVolumeChange(false),
				"TC_338: expected volume to decrease after slider adjustment.");
	}

	@Test(priority = 339, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanMuteAudio() {
		openAdvancedAudioBook();
		Assert.assertTrue(audioPlayer.validateMuteToggle(), "TC_339: expected audio to become muted.");
	}

	@Test(priority = 341, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanSeekForwardViaProgressBar() {
		openAdvancedAudioBook();

		Assert.assertTrue(audioPlayer.validatePlay(), "TC_341 setup: expected audio playback to start before seeking.");

		Assert.assertTrue(audioPlayer.isPlaybackProgressing(),
				"TC_341 setup: expected playback to be progressing before forward seek.");

		Assert.assertTrue(audioPlayer.validateSeekForward(),
				"TC_341: expected seek forward to move playback position ahead.");
	}

	@Test(priority = 342, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanSeekBackwardViaProgressBar() {
		openAdvancedAudioBook();

		Assert.assertTrue(audioPlayer.validatePlay(), "TC_342 setup: expected audio playback to start before seeking.");

		Assert.assertTrue(audioPlayer.isPlaybackProgressing(),
				"TC_342 setup: expected playback to be progressing before backward seek.");

		Assert.assertTrue(audioPlayer.validateSeekForward(),
				"TC_342 setup: expected forward seek to move playback ahead before testing backward seek.");

		Assert.assertTrue(audioPlayer.validateSeekBackward(),
				"TC_342: expected seek backward to move playback position behind.");
	}

	@Test(priority = 343, retryAnalyzer = RetryAnalyzer.class)
	public void verifySeekBehaviorWhenDraggedBeyondAudioLength() {
		openAdvancedAudioBook();

		Assert.assertTrue(audioPlayer.validatePlay(),
				"TC_343 setup: expected audio playback to start before boundary seek validation.");

		Assert.assertTrue(audioPlayer.isPlaybackProgressing(),
				"TC_343 setup: expected playback to be progressing before dragging beyond audio length.");

		Assert.assertTrue(audioPlayer.validateSeekBeyondEnd(),
				"TC_343: expected seek beyond duration to clamp at end.");
	}

	@Test(priority = 344, retryAnalyzer = RetryAnalyzer.class)
	public void verifySubscribedUserCanListenToBook() {
		openSubscribedBook();
		Assert.assertTrue(audioPlayer.validatePlay(), "TC_344: expected subscribed user to play the book.");
	}

	@Test(priority = 345, retryAnalyzer = RetryAnalyzer.class)
	public void verifySubscribedUserCanListenToMultipleBooks() {
		loginSubscribedUser();

		List<String> titles = requireAtLeastTwoPlayableBooks(
				"TC_345 requires at least two playable books for subscribed user.");

		openBookByTitle(titles.get(0));
		Assert.assertTrue(audioPlayer.validatePlay(), "TC_345: expected first book to play.");
		Assert.assertTrue(dashboard.clickBackButtonToDashboard(),
				"TC_345: expected to return to dashboard after first book.");

		openBookByTitle(titles.get(1));
		Assert.assertTrue(audioPlayer.validatePlay(), "TC_345: expected second book to play for subscribed user.");
	}

	@Test(priority = 346, retryAnalyzer = RetryAnalyzer.class)
	public void verifyFreeUserCanListenToOnlyOneBook() {
		loginFreeUser();
		List<String> titles = requireTrendingTitles(2, "TC_346 requires at least two trending books.");
		openBookByTitle(titles.get(0));
		Assert.assertTrue(audioPlayer.validatePlay(), "TC_346: expected first book to play for free user.");
		returnToDashboard();
		openBookByTitle(titles.get(1));
		Assert.assertTrue(audioPlayer.validateRestrictedPlaybackBlocked() || audioPlayer.hasFreeUserLimitIndicator(),
				"TC_346: expected second book to be blocked for free user.");
	}

	@Test(priority = 347, retryAnalyzer = RetryAnalyzer.class)
	public void verifyFreeUserCanOnlyListenToFirstBookFully() {
		loginFreeUser();
		List<String> titles = requireTrendingTitles(2, "TC_347 requires at least two trending books.");
		openBookByTitle(titles.get(0));
		Assert.assertTrue(audioPlayer.validatePlay(), "TC_347: expected first book to start.");
		Assert.assertTrue(audioPlayer.isPlaybackProgressing(), "TC_347: expected first book playback to progress.");
		returnToDashboard();
		openBookByTitle(titles.get(1));
		Assert.assertTrue(audioPlayer.validateRestrictedPlaybackBlocked() || audioPlayer.hasFreeUserLimitIndicator(),
				"TC_347: expected subsequent books to be blocked for free user.");
	}

	@Test(priority = 350, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPlaybackControlsWorkForSubscribedUsers() {
		openSubscribedBook();
		Assert.assertTrue(audioPlayer.validatePlay(), "TC_350 : expected play to work.");
		Assert.assertTrue(audioPlayer.validatePause(), "TC_350 : expected pause to work.");
		Assert.assertTrue(audioPlayer.validateResume(), "TC_350 : expected resume to work.");
		Assert.assertTrue(audioPlayer.validateForward30(), "TC_350 : expected forward to work.");
		Assert.assertTrue(audioPlayer.validateBackward30(), "TC_350 : expected backward to work.");
	}

	private void openConsumerBook() {
		loginAs(getConsumerEmail(), getConsumerPassword());
		openAnyBookAndWaitForPlayer();
	}

	private void openAdvancedAudioBook() {
		loginAs(getAdvancedAudioEmail(), getAdvancedAudioPassword());
		openAnyBookAndWaitForPlayer();
	}

	private void openSubscribedBook() {
		loginSubscribedUser();
		openAnyBookAndWaitForPlayer();
	}

	private void loginSubscribedUser() {
		loginAs(getAdvancedAudioEmail(), getAdvancedAudioPassword());
	}

	private void loginFreeUser() {
		loginAs(getAdvancedAudioEmail(), getAdvancedAudioPassword());
	}

	private void loginAs(String email, String password) {
		if (isBlank(email) || isBlank(password)) {
			throw new SkipException("Audio test credentials are missing in config.properties.");
		}
		login.openLogin();
		login.loginUser(email, password);
		login.clickNextAfterLogin();
	}

	private void openAnyBookAndWaitForPlayer() {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(), "Dashboard shell should be ready before opening a book.");
		dashboard.openAnyBook();
		Assert.assertTrue(dashboard.isBookDetailsPageVisible(), "Book details page should open from the dashboard.");
		Assert.assertTrue(dashboard.waitForBookDataToLoad(), "Book details should load before audio validation.");
		Assert.assertTrue(audioPlayer.waitForPlayerBar() || audioPlayer.hasSubscriptionGate(),
				"Audio player surface or subscription gate should be visible.");
		Assert.assertTrue(audioPlayer.waitForPlayControlsReady() || audioPlayer.hasSubscriptionGate(),
				"Play controls or subscription gate should be ready before audio validation.");
	}

	private void openBookByTitle(String title) {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(),
				"Dashboard shell should be ready before selecting a book.");
		dashboard.clickTrendingShow(title);
		Assert.assertTrue(dashboard.isBookDetailsPageVisible(), "Expected book details page for title: " + title);
		Assert.assertTrue(dashboard.waitForBookDataToLoad(), "Expected book details data for title: " + title);
		Assert.assertTrue(audioPlayer.waitForPlayerBar() || audioPlayer.hasSubscriptionGate(),
				"Expected player or subscription gate for title: " + title);
		Assert.assertTrue(audioPlayer.waitForPlayControlsReady() || audioPlayer.hasSubscriptionGate(),
				"Expected play controls or subscription gate for title: " + title);
	}

	private void returnToDashboard() {
		driver.navigate().back();
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(), "Expected dashboard after navigating back.");
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

	private void requireMultipleChapters(String message) {
		if (!audioPlayer.hasMultipleChapters()) {
			throw new SkipException(message);
		}
	}

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

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private List<String> requireAtLeastTwoPlayableBooks(String skipMessage) {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell());

		LinkedHashSet<String> uniqueTitles = new LinkedHashSet<>();

		// Source 1
		uniqueTitles.addAll(dashboard.getTrendingShowNames());
		for (String bookId : dashboard.getTrendingBooksList()) {
			if (!isBlank(bookId) && !uniqueTitles.contains(bookId)) {
				uniqueTitles.add(bookId);
			}
		}

		// 🔥 Add more sources if available
		// uniqueTitles.addAll(dashboard.getContinueListeningTitles());
		// uniqueTitles.addAll(dashboard.getRecommendedShowNames());

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
}
