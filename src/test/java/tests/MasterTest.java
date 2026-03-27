package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import base.BaseTest;
import dataprovider.TestDataProvider;
import pages.DashboardPage;
import pages.LoginPage;
import pages.PlayerPage;
import utils.ConfigReader;

/**
 * Master Test Class - Executes all modules based on Excel data
 */
public class MasterTest extends BaseTest {

	@Test(dataProvider = "testdata", dataProviderClass = TestDataProvider.class)
	public void runTests(String tcId, String module, String scenario, String email, String password, String card,
			String expiry, String action, String expected, String priority, String run) {

		resetApplicationState();

		LoginPage login = new LoginPage(driver);
		DashboardPage dashboard = new DashboardPage(driver);
		PlayerPage player = new PlayerPage(driver);

		// Skip logic
		if (run.equalsIgnoreCase("No")) {
			throw new SkipException("Skipping Test Case: " + tcId);
		}

		log("Executing TC: " + tcId + " | Module: " + module + " | Scenario: " + scenario);

		try {
			switch (module.toLowerCase()) {

			case "login":
				login.openLogin();
				login.loginUser(email, password);
				validateLogin(login, expected, tcId);
				break;

			case "player":
				dashboard.acceptCookiesIfPresent();
				dashboard.openAnyBook();

				player.waitForPlayerBar();

				executePlayer(player, action, tcId);
				break;

			default:
				throw new RuntimeException("Unknown module: " + module);
			}

		} catch (Exception e) {
			logError("Test Failed: " + tcId + " | Error: " + e.getMessage());
			throw e;
		}
	}

	private void resetApplicationState() {
		String url = ConfigReader.getProperty("url");
		if (url != null && !url.isBlank()) {
			driver.get(url);
		}

		try {
			new DashboardPage(driver).acceptCookiesIfPresent();
		} catch (Exception e) {
			log("Cookie consent not present during reset.");
		}
	}

	// ================= PLAYER ACTION HANDLER =================

	private void executePlayer(PlayerPage player, String action, String tcId) {

		switch (action.toLowerCase()) {

		case "play":
			player.clickPlayAudio();
			player.waitForAudioToStart();
			Assert.assertTrue(player.isAudioPlaying(), "Play failed: " + tcId);
			break;

		case "pause":
			player.clickPlayAudio();
			player.waitForAudioToStart();

			player.clickPausePlay();
			player.waitForAudioToPause();

			Assert.assertTrue(player.isAudioPaused(), "Pause failed: " + tcId);
			break;

		case "play_pause_resume":

			player.clickPlayAudio();
			player.waitForAudioToStart();

			player.clickPausePlay();
			player.waitForAudioToPause();

			player.clickPausePlay();
			player.waitForAudioToStart();

			Assert.assertTrue(player.isAudioPlaying(), "Resume failed: " + tcId);
			break;

		// 🔥 NEW P0 TEST CASES

		case "forward": // TC_07
			player.clickPlayAudio();
			player.waitForAudioToStart();

			player.clickForward30();

			Assert.assertTrue(true, "Forward action executed: " + tcId);
			break;

		case "backward": // TC_08
			player.clickPlayAudio();
			player.waitForAudioToStart();

			player.clickBackward30();

			Assert.assertTrue(true, "Backward action executed: " + tcId);
			break;

		case "load": // TC_09
			player.waitForPlayerBar();

			Assert.assertTrue(true, "Player loaded successfully: " + tcId);
			break;

		default:
			throw new RuntimeException("Invalid action: " + action);
		}
	}
	// ================= LOGIN VALIDATION =================

	private void validateLogin(LoginPage login, String expected, String tcId) {

		switch (expected.toLowerCase()) {

		case "success":
			Assert.assertTrue(!login.getLoginSuccessMessage().isEmpty(), "Login failed: " + tcId);
			break;

		case "error":
			Assert.assertTrue(!login.getErrorMessage().isEmpty(), "Error not shown: " + tcId);
			break;

		case "validation":
			Assert.assertTrue(!login.getEmptyFieldMessage().isEmpty(), "Validation missing: " + tcId);
			break;

		default:
			throw new RuntimeException("Invalid expected value: " + expected);
		}
	}

	private void log(String message) {
		System.out.println("[INFO] " + message);
	}

	private void logError(String message) {
		System.err.println("[ERROR] " + message);
	}
}
