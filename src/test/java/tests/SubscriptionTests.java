package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.LoginPage;
import pages.SubscriptionPage;
import utils.ConfigReader;

/**
 * Phase-1 subscription coverage for the remaining high-priority scenarios.
 */
public class SubscriptionTests extends BaseTest {

	private String getActiveEmail() {
		String configured = ConfigReader.getProperty("subscription.activeEmail");
		if (configured != null && !configured.isBlank()) {
			return configured;
		}
		return ConfigReader.getProperty("login.validEmail");
	}

	private String getActivePassword() {
		String configured = ConfigReader.getProperty("subscription.activePassword");
		if (configured != null && !configured.isBlank()) {
			return configured;
		}
		return ConfigReader.getProperty("login.validPassword");
	}

	private String getRestrictedEmail() {
		String configured = ConfigReader.getProperty("subscription.restrictedEmail");
		if (configured != null && !configured.isBlank()) {
			return configured;
		}
		return ConfigReader.getProperty("login.inactiveEmail");
	}

	private String getRestrictedPassword() {
		String configured = ConfigReader.getProperty("subscription.restrictedPassword");
		if (configured != null && !configured.isBlank()) {
			return configured;
		}
		String inactivePassword = ConfigReader.getProperty("login.inactivePassword");
		if (inactivePassword != null && !inactivePassword.isBlank()) {
			return inactivePassword;
		}
		return ConfigReader.getProperty("login.validPassword");
	}

	private SubscriptionPage openSubscriptionPage(String email, String password) {
		if (email == null || email.isBlank() || password == null || password.isBlank()) {
			throw new SkipException("Subscription test credentials are not configured.");
		}

		LoginPage login = new LoginPage(driver);
		SubscriptionPage subscription = new SubscriptionPage(driver);

		login.openLogin();
		login.loginUser(email, password);
		subscription.clickHamburgerMenu();
		subscription.clickSubscription();
		Assert.assertTrue(subscription.isSubscriptionPageDisplayed(), "Subscription page should be displayed.");
		return subscription;
	}

	@Test(priority = 1, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDisabledPlanSelectionUi() {
		SubscriptionPage subscription = openSubscriptionPage(getActiveEmail(), getActivePassword());

		if (!subscription.isPlanActive()) {
			throw new SkipException("Configured account does not currently have an active plan to validate disabled UI.");
		}

		subscription.attemptToActivateAnotherPlan();

		Assert.assertTrue(subscription.isPlanSelectionDisabled() || subscription.isSubscriptionActivationRestricted(),
				"Plan selection should be disabled or restricted for an already active subscription.");
	}

	@Test(priority = 2, retryAnalyzer = RetryAnalyzer.class)
	public void verifyExpiryBoundaryHandling() {
		SubscriptionPage subscription = openSubscriptionPage(getRestrictedEmail(), getRestrictedPassword());

		String planStatus = subscription.getPlanStatus().toLowerCase();
		if (!(planStatus.contains("expired") || planStatus.contains("deactivated") || planStatus.contains("inactive"))) {
			throw new SkipException("Restricted/expiry-boundary account is not available in the current environment.");
		}

		subscription.refreshCurrentPage();
		String refreshedStatus = subscription.getPlanStatus().toLowerCase();

		Assert.assertTrue(refreshedStatus.contains("expired") || refreshedStatus.contains("deactivated")
				|| subscription.isPlanSelectionVisible(),
				"At expiry boundary the plan should move to restricted state and allow fresh plan selection.");
	}

	@Test(priority = 3, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPlayDuringExpiryTime() {
		SubscriptionPage subscription = openSubscriptionPage(getRestrictedEmail(), getRestrictedPassword());
		subscription.goToHome();
		subscription.playFirstBook();
		subscription.clickPlay();

		Assert.assertTrue(subscription.isSubscriptionWarningDisplayed() || !subscription.isAudioPlaying(),
				"Playback should be restricted or show a subscription warning for the restricted/expired account.");
	}
}
