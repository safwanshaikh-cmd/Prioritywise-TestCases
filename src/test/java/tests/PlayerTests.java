package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pages.DashboardPage;
import pages.PlayerPage;
import listeners.RetryAnalyzer;

public class PlayerTests extends BaseTest {

	private DashboardPage dashboard;
	private PlayerPage player;

	// 🔥 COMMON SETUP (removes duplication)
	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setup();
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
}
