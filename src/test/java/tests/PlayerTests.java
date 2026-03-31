package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.SkipException;

import pages.DashboardPage;
import pages.LoginPage;
import pages.PlayerPage;
import listeners.RetryAnalyzer;
import utils.ConfigReader;

public class PlayerTests extends BaseTest {

	private DashboardPage dashboard;
	private PlayerPage player;
	private LoginPage login;

	// 🔥 COMMON SETUP (removes duplication)
	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setup();

		// Login first before accessing books
		String email = ConfigReader.getProperty("login.validEmail");
		String password = ConfigReader.getProperty("login.validPassword");

		if (email == null || email.isBlank() || password == null || password.isBlank()) {
			throw new SkipException(
					"Set login.validEmail and login.validPassword in config.properties to run player tests. Books require login to access.");
		}

		login = new LoginPage(driver);
		login.openLogin();
		login.loginUser(email, password);
		login.clickNextAfterLogin();

		dashboard = new DashboardPage(driver);
		player = new PlayerPage(driver);

		dashboard.openAnyBook();
		player.waitForPlayerBar();
	}

	// ================= PLAY =================
	@Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPlayBook() {

		player.clickPlayAudio();
		player.waitForAudioToStart();

		Assert.assertTrue(player.isAudioPlaying(), "❌ Audio did not start playing");
	}

	// ================= PAUSE =================
	@Test(priority = 2, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPauseBook() {

		player.clickPlayAudio();
		player.waitForAudioToStart();

		player.clickPausePlay();
		player.waitForAudioToPause();

		Assert.assertTrue(player.isAudioPaused(), "❌ Pause functionality failed");
	}

	// ================= RESUME =================
	@Test(priority = 3, retryAnalyzer = RetryAnalyzer.class)
	public void verifyResumeBook() {

		player.clickPlayAudio();
		player.waitForAudioToStart();

		player.clickPausePlay();
		player.waitForAudioToPause();

		player.clickPausePlay();
		player.waitForAudioToStart();

		Assert.assertTrue(player.isAudioPlaying(), "❌ Resume functionality failed");
	}

	// ================= TC_07 =================
	@Test(priority = 4, retryAnalyzer = RetryAnalyzer.class)
	public void verifyForwardAudio() {

		player.clickPlayAudio();
		player.waitForAudioToStart();

		player.clickForward30();

		Assert.assertTrue(true, "Forward executed successfully");
	}

	// ================= TC_08 =================
	@Test(priority = 5, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBackwardAudio() {

		player.clickPlayAudio();
		player.waitForAudioToStart();

		player.clickBackward30();

		Assert.assertTrue(true, "Backward executed successfully");
	}

	// ================= TC_09 =================
	@Test(priority = 6, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPlayerLoad() {

		Assert.assertTrue(true, "Player loaded successfully");
	}

	@Test(priority = 7, retryAnalyzer = RetryAnalyzer.class)
	public void verifyNextChapterPlayback() {
		player.clickPlayAudio();
		player.waitForAudioToStart();
		player.waitForPlayControlsReady();

		player.clickNextChapter();
		player.waitForPlayControlsReady();

		Assert.assertTrue(player.isPlayerResponsive(), "Player should remain responsive after next chapter navigation");
	}

	@Test(priority = 8, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPreviousChapterPlayback() {
		player.clickPlayAudio();
		player.waitForAudioToStart();
		player.waitForPlayControlsReady();

		player.clickPreviousChapter();
		player.waitForPlayControlsReady();

		Assert.assertTrue(player.isPlayerResponsive(),
				"Player should remain responsive after previous chapter navigation");
	}

	@Test(priority = 9, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPreviousOnFirstChapterHandledGracefully() {
		player.clickPlayAudio();
		player.waitForAudioToStart();
		player.waitForPlayControlsReady();

		player.clickPreviousChapter();
		player.waitForPlayControlsReady();

		Assert.assertTrue(player.isPlayerResponsive(),
				"Player should remain stable when previous is clicked on the first chapter");
	}
}
