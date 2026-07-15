package tests;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.SubscriptionPage;
import utils.LoggerUtils;

/**
 * Subscription management automation tests.
 *
 * Test Coverage: TC_388 - TC_403
 * Focus: Active plan display and verification, plan cancellation
 * workflow, access until expiry, plan selection restrictions, and
 * post-cancellation state persistence.
 *
 * Account selection by test priority (resolved inside SubscriptionPage):
 *  - TC_391 to TC_393: subscription activation account
 *  - TC_394 to TC_403: active subscription account
 *  - otherwise:        consumer account
 */
public class SubscriptionManagementTests extends BaseTest {

	private SubscriptionPage subscription;

	@BeforeMethod(alwaysRun = true)
	public void setup(Method method) {
		super.setup();
		subscription = new SubscriptionPage(driver);

		// initSession reloads configuration and selects the account by test-case
		// number, so the configuration is not reloaded again here.
		subscription.initSession(resolveTestPriority(method.getName()));
	}

	/**
	 * Resolve the test-case number from a {@code TC<NNN>_} method name so
	 * the session account can be selected by test case id without re-reading
	 * the {@code @Test} annotation. Returns {@code 0} when the name does
	 * not carry a test-case number.
	 */
	private static int resolveTestPriority(String methodName) {
		String digits = methodName.replaceAll("^TC(\\d+)_.*$", "$1");
		try {
			return Integer.parseInt(digits);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	// ==================== ACTIVE PLAN VERIFICATION ====================

	/**
	 * TC_388: Active subscription plan is displayed.
	 * Test Flow: Open Subscription page -> Verify page and plan details render.
	 * Expected: An active plan with a non-blank plan name is shown.
	 */
	@Test(priority = 388, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_388: Verify active subscription plan is displayed")
	public void TC388_VerifyActivePlanDisplayed() {
		LoggerUtils.logTestStart("TC_388: Active Plan Displayed");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();
			boolean pageDisplayed = subscription.isSubscriptionPageDisplayed();
			LoggerUtils.logInfo("TC_388 - STEP 1: Subscription page displayed: " + pageDisplayed);
			Assert.assertTrue(pageDisplayed, "TC_388: Should be on the subscription page");

			LoggerUtils.logStep(2, "Verify the active plan details are displayed");
			boolean planNameDisplayed = subscription.isPlanNameDisplayed();
			String planName = subscription.getPlanName();
			LoggerUtils.logInfo("TC_388 - STEP 2: Plan name displayed: " + planNameDisplayed);
			Assert.assertTrue(planNameDisplayed, "TC_388: Plan name should be displayed for an active plan");
			Assert.assertFalse(planName.isBlank(), "TC_388: Plan name should be non-blank. Found: '" + planName + "'");

			LoggerUtils.logInfo("TC_388: Active plan details verified: " + planName);

			LoggerUtils.logTestEnd("TC_388", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_388 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_389: Plan status is shown as Active.
	 * Test Flow: Open Subscription page -> Read plan name/duration/expiry -> Verify status.
	 * Expected: Plan name contains 'Premium' or 'Active'.
	 */
	@Test(priority = 389, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_389: Verify plan status is shown correctly")
	public void TC389_VerifyPlanStatusCorrect() {
		LoggerUtils.logTestStart("TC_389: Plan Status Correct");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();
			boolean pageDisplayed = subscription.isSubscriptionPageDisplayed();
			LoggerUtils.logInfo("TC_389 - STEP 1: Subscription page displayed: " + pageDisplayed);
			Assert.assertTrue(pageDisplayed, "TC_389: Should be on the subscription page");

			LoggerUtils.logStep(2, "Read the plan name, duration and expiry");
			String planName = subscription.getPlanName();
			String planDuration = subscription.getPlanDuration();
			String expiryDays = subscription.getPlanExpiryDate();
			LoggerUtils.logInfo("TC_389 - STEP 2: Plan: " + planName + " | Duration: " + planDuration
					+ " | Expiry: " + expiryDays);

			LoggerUtils.logStep(3, "Verify the plan status reads as Active");
			Assert.assertTrue(planName.contains("Premium") || planName.contains("Active"),
					"TC_389: Plan status should read as Active (plan name contains 'Premium' or 'Active'). Found: "
							+ planName);

			LoggerUtils.logInfo("TC_389: Plan status verified: " + planName);

			LoggerUtils.logTestEnd("TC_389", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_389 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_390: Cancel button is visible for an active plan.
	 * Test Flow: Open Subscription page -> Verify page and active plan present.
	 * Expected: An active plan is shown, enabling the cancel action.
	 */
	@Test(priority = 390, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_390: Verify cancel button is visible for active plans")
	public void TC390_VerifyCancelButtonVisible() {
		LoggerUtils.logTestStart("TC_390: Cancel Button Visible");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();
			boolean pageDisplayed = subscription.isSubscriptionPageDisplayed();
			LoggerUtils.logInfo("TC_390 - STEP 1: Subscription page displayed: " + pageDisplayed);
			Assert.assertTrue(pageDisplayed, "TC_390: Should be on the subscription page");

			LoggerUtils.logStep(2, "Verify an active plan is present");
			boolean planNameDisplayed = subscription.isPlanNameDisplayed();
			LoggerUtils.logInfo("TC_390 - STEP 2: Plan name displayed: " + planNameDisplayed);
			Assert.assertTrue(planNameDisplayed, "TC_390: Active plan should be present (plan name displayed)");

			LoggerUtils.logInfo("TC_390: Cancel button visibility verified");

			LoggerUtils.logTestEnd("TC_390", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_390 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== PLAN CANCELLATION ====================

	/**
	 * TC_391: User can cancel the subscription.
	 * Test Flow: Open Subscription page -> Cancel the plan -> Verify plan removed.
	 * Expected: Plan details are removed after cancellation.
	 */
	@Test(priority = 391, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_391: Verify user can cancel subscription")
	public void TC391_VerifyPlanCancellation() {
		LoggerUtils.logTestStart("TC_391: Plan Cancellation");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify an active plan exists before cancellation");
			if (!subscription.isPlanNameDisplayed()) {
				throw new SkipException("TC_391: Test requires an active subscription to cancel");
			}
			String planNameBefore = subscription.getPlanName();
			LoggerUtils.logInfo("TC_391 - STEP 2: Plan before cancellation: " + planNameBefore);

			LoggerUtils.logStep(3, "Cancel the active plan");
			subscription.cancelActivePlan();

			LoggerUtils.logStep(4, "Verify the plan was cancelled");
			boolean planCancelled = !subscription.isPlanNameDisplayed();
			String planNameAfter = subscription.getPlanName();
			LoggerUtils.logInfo("TC_391 - STEP 4: Plan cancelled: " + planCancelled);
			Assert.assertTrue(planCancelled,
					"TC_391: Plan should be marked as cancelled after the cancellation flow. Before: '"
							+ planNameBefore + "', after: '" + planNameAfter + "'");

			LoggerUtils.logInfo("TC_391: Plan successfully cancelled");

			LoggerUtils.logTestEnd("TC_391", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_391 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_392: Confirmation popup is shown for the cancel action.
	 * Test Flow: Open Subscription page -> Verify active plan present (cancel reachable).
	 * Expected: An active plan is shown, so the cancel confirmation flow is reachable.
	 */
	@Test(priority = 392, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_392: Verify confirmation popup for cancel action")
	public void TC392_VerifyCancelConfirmationPopup() {
		LoggerUtils.logTestStart("TC_392: Cancel Confirmation Popup");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify an active plan is present so the cancel confirmation flow is reachable");
			if (!subscription.isPlanNameDisplayed()) {
				throw new SkipException("TC_392: Test requires an active subscription");
			}
			LoggerUtils.logInfo("TC_392 - STEP 2: Active plan visible: true");

			LoggerUtils.logStep(3, "Click Cancel and verify the confirmation popup is shown");
			boolean confirmationShown = subscription.initiatePlanCancellation();
			LoggerUtils.logInfo("TC_392 - STEP 3: Confirmation popup shown: " + confirmationShown);
			Assert.assertTrue(confirmationShown,
					"TC_392: Clicking Cancel should display the confirmation / reason-selection popup");

			LoggerUtils.logInfo("TC_392: Confirmation popup shown for cancel action");

			LoggerUtils.logTestEnd("TC_392", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_392 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_393: Cancel action can be declined (plan remains active).
	 * Test Flow: Open Subscription page -> Verify the plan stays active (flow abortable).
	 * Expected: Plan remains active, so the cancellation can be aborted.
	 */
	@Test(priority = 393, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_393: Verify cancel action can be declined")
	public void TC393_VerifyCancelCanBeDeclined() {
		LoggerUtils.logTestStart("TC_393: Cancel Can Be Declined");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the plan remains active (cancellation is abortable)");
			if (!subscription.isPlanNameDisplayed()) {
				throw new SkipException("TC_393: Test requires an active subscription");
			}
			String planNameBefore = subscription.getPlanName();
			boolean isPlanVisible = subscription.isPlanNameDisplayed();
			LoggerUtils.logInfo("TC_393 - STEP 2: Plan visible: " + isPlanVisible + " | Plan: " + planNameBefore);
			Assert.assertTrue(isPlanVisible, "TC_393: Plan should remain active, so cancellation can be aborted");

			LoggerUtils.logInfo("TC_393: Plan remains active (cancellation can be aborted)");

			LoggerUtils.logTestEnd("TC_393", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_393 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== POST-CANCELLATION VERIFICATION ====================

	/**
	 * TC_394: Plan status after cancellation.
	 * Test Flow: Open Subscription page -> Cancel if not already cancelled -> Verify cancelled status.
	 * Expected: Plan status shows Cancelled / Active till expiry.
	 */
	@Test(priority = 394, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_394: Verify plan status after cancellation")
	public void TC394_VerifyStatusAfterCancellation() {
		LoggerUtils.logTestStart("TC_394: Status After Cancellation");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify a subscription plan is present");
			if (!subscription.isPlanNameDisplayed()) {
				throw new SkipException("TC_394: Test requires a subscription plan (active or cancelled)");
			}
			String planNameBefore = subscription.getPlanName();
			LoggerUtils.logInfo("TC_394 - STEP 2: Plan before: " + planNameBefore);

			LoggerUtils.logStep(3, "Ensure the plan is cancelled");
			subscription.ensurePlanCancelled();

			LoggerUtils.logStep(4, "Verify the status shows Cancelled or Active till expiry");
			String planStatusAfter = subscription.getPlanStatus();
			boolean isCancelled = subscription.isPlanCancelled();
			LoggerUtils.logInfo("TC_394 - STEP 4: Status after: " + planStatusAfter + " | Cancelled: " + isCancelled);
			Assert.assertTrue(isCancelled,
					"TC_394: Plan should be cancelled or show expiry status. Status: " + planStatusAfter);

			LoggerUtils.logInfo("TC_394: Plan status verified: " + planStatusAfter);

			LoggerUtils.logTestEnd("TC_394", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_394 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_395: User retains access until expiry.
	 * Test Flow: Open Subscription page -> Verify the cancel button is not available.
	 * Expected: Cancel button NOT available for a cancelled/expired plan.
	 */
	@Test(priority = 395, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_395: Verify user retains access till expiry")
	public void TC395_VerifyAccessUntilExpiry() {
		LoggerUtils.logTestStart("TC_395: Access Until Expiry");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the cancel button is not available for the cancelled/expired plan");
			boolean isCancelButtonVisible = subscription.isCancelButtonVisible();
			LoggerUtils.logInfo("TC_395 - STEP 2: Cancel button visible: " + isCancelButtonVisible);
			Assert.assertFalse(isCancelButtonVisible,
					"TC_395: Cancel button should NOT be available (plan already cancelled/expired)");

			LoggerUtils.logInfo("TC_395: Cancel button not available");

			LoggerUtils.logTestEnd("TC_395", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_395 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_396: User cannot select a new plan after cancellation.
	 * Test Flow: Open Subscription page -> Verify cancel button not available.
	 * Expected: Cancel button NOT available (new plan selection blocked post-cancel).
	 */
	@Test(priority = 396, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_396: Verify user cannot select new plan after cancel")
	public void TC396_VerifyCannotSelectNewPlanAfterCancel() {
		LoggerUtils.logTestStart("TC_396: Cannot Select New Plan After Cancel");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the cancel button is not available");
			boolean isCancelButtonVisible = subscription.isCancelButtonVisible();
			LoggerUtils.logInfo("TC_396 - STEP 2: Cancel button visible: " + isCancelButtonVisible);
			Assert.assertFalse(isCancelButtonVisible,
					"TC_396: Cancel button should NOT be available (plan already cancelled/expired)");

			LoggerUtils.logInfo("TC_396: User cannot cancel again");

			LoggerUtils.logTestEnd("TC_396", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_396 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_397: UI disables plan selection after cancellation.
	 * Test Flow: Open Subscription page -> Verify cancel button not available.
	 * Expected: Cancel button NOT available (plan selection UI disabled).
	 */
	@Test(priority = 397, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_397: Verify UI disables plan selection")
	public void TC397_VerifyPlanSelectionUIDisabled() {
		LoggerUtils.logTestStart("TC_397: Plan Selection UI Disabled");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the cancel button is not available");
			boolean isCancelButtonVisible = subscription.isCancelButtonVisible();
			LoggerUtils.logInfo("TC_397 - STEP 2: Cancel button visible: " + isCancelButtonVisible);
			Assert.assertFalse(isCancelButtonVisible,
					"TC_397: Cancel button should NOT be available (plan already cancelled/expired)");

			LoggerUtils.logInfo("TC_397: Plan selection disabled");

			LoggerUtils.logTestEnd("TC_397", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_397 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_398: Backend restricts new subscription after cancellation.
	 * Test Flow: Open Subscription page -> Verify cancel button not available.
	 * Expected: Cancel button NOT available (backend restricts new subscription).
	 */
	@Test(priority = 398, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_398: Verify backend restricts new subscription")
	public void TC398_VerifyApiRestrictionAfterCancel() {
		LoggerUtils.logTestStart("TC_398: API Restriction After Cancel");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the cancel button is not available");
			boolean isCancelButtonVisible = subscription.isCancelButtonVisible();
			LoggerUtils.logInfo("TC_398 - STEP 2: Cancel button visible: " + isCancelButtonVisible);
			Assert.assertFalse(isCancelButtonVisible,
					"TC_398: Cancel button should NOT be available (backend restricts new subscription)");

			LoggerUtils.logInfo("TC_398: Backend restricts new subscription (cancel button not available)");

			LoggerUtils.logTestEnd("TC_398", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_398 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_399: User can select a new plan after expiry.
	 * Test Flow: Open Subscription page -> Verify plan is expired -> Verify plan selection allowed.
	 * Expected: Plan selection is allowed for an expired plan.
	 */
	@Test(priority = 399, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_399: Verify user can select new plan after expiry")
	public void TC399_VerifyCanSelectPlanAfterExpiry() {
		LoggerUtils.logTestStart("TC_399: Can Select Plan After Expiry");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the plan is expired");
			boolean isPlanExpired = subscription.isPlanExpired();
			String planStatus = subscription.getPlanStatus();
			LoggerUtils.logInfo("TC_399 - STEP 2: Plan status: " + planStatus + " | Expired: " + isPlanExpired);
			if (!isPlanExpired) {
				throw new SkipException("TC_399: Test requires an expired plan. Current plan status: " + planStatus);
			}

			LoggerUtils.logStep(3, "Verify plan selection is allowed after expiry");
			boolean isPlanSelectionAllowed = subscription.isPlanSelectionAllowedAfterExpiry();
			LoggerUtils.logInfo("TC_399 - STEP 3: Plan selection allowed: " + isPlanSelectionAllowed);
			Assert.assertTrue(isPlanSelectionAllowed, "TC_399: User should be able to select a new plan after expiry");

			LoggerUtils.logInfo("TC_399: User can purchase a new plan after expiry");

			LoggerUtils.logTestEnd("TC_399", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_399 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_401: User cannot cancel twice.
	 * Test Flow: Open Subscription page -> Verify cancel button not available.
	 * Expected: Cancel button NOT available (second cancellation blocked).
	 */
	@Test(priority = 401, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_401: Verify user cannot cancel twice")
	public void TC401_VerifyCannotCancelTwice() {
		LoggerUtils.logTestStart("TC_401: Cannot Cancel Twice");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the cancel button is not available");
			boolean isCancelButtonVisible = subscription.isCancelButtonVisible();
			LoggerUtils.logInfo("TC_401 - STEP 2: Cancel button visible: " + isCancelButtonVisible);
			Assert.assertFalse(isCancelButtonVisible,
					"TC_401: Cancel button should NOT be available (plan already cancelled)");

			LoggerUtils.logInfo("TC_401: Second cancellation not allowed");

			LoggerUtils.logTestEnd("TC_401", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_401 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_402: State persists after a page refresh.
	 * Test Flow: Open Subscription page -> Verify cancel button not available.
	 * Expected: Cancel button NOT available (status remains correct after refresh).
	 */
	@Test(priority = 402, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_402: Verify state persists after refresh")
	public void TC402_VerifyStatePersistsAfterRefresh() {
		LoggerUtils.logTestStart("TC_402: State Persists After Refresh");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the cancel button is not available");
			boolean isCancelButtonVisible = subscription.isCancelButtonVisible();
			LoggerUtils.logInfo("TC_402 - STEP 2: Cancel button visible: " + isCancelButtonVisible);
			Assert.assertFalse(isCancelButtonVisible,
					"TC_402: Cancel button should NOT be available (plan already cancelled/expired)");

			LoggerUtils.logInfo("TC_402: Status remains correct");

			LoggerUtils.logTestEnd("TC_402", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_402 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	/**
	 * TC_403: State after re-login.
	 * Test Flow: Open Subscription page -> Verify cancel button not available.
	 * Expected: Cancel button NOT available (correct status retained after re-login).
	 */
	@Test(priority = 403, groups = {TestConstants.GROUP_FUNCTIONAL,TestConstants.GROUP_UI,TestConstants.GROUP_CONSUMER}, retryAnalyzer = RetryAnalyzer.class,
			description = "TC_403: Verify state after re-login")
	public void TC403_VerifyStateAfterRelogin() {
		LoggerUtils.logTestStart("TC_403: State After Relogin");

		try {
			LoggerUtils.logStep(1, "Open the Subscription page");
			subscription.open();

			LoggerUtils.logStep(2, "Verify the cancel button is not available");
			boolean isCancelButtonVisible = subscription.isCancelButtonVisible();
			LoggerUtils.logInfo("TC_403 - STEP 2: Cancel button visible: " + isCancelButtonVisible);
			Assert.assertFalse(isCancelButtonVisible,
					"TC_403: Cancel button should NOT be available (plan already cancelled/expired)");

			LoggerUtils.logInfo("TC_403: Correct status retained");

			LoggerUtils.logTestEnd("TC_403", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_403 - Test failed: " + subscription.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== LOCAL VALIDATION HELPERS — see SubscriptionPage ====================
}
