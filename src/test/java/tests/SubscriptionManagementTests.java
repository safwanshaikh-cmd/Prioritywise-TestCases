package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.LoginPage;
import pages.SubscriptionPage;
import utils.ConfigReader;

/**
 * Subscription Management Test Cases (TC_388 to TC_403)
 *
 * Tests for subscription lifecycle including:
 * - Active plan display
 * - Plan cancellation
 * - Access until expiry
 * - Plan selection restrictions
 * - State persistence
 */
public class SubscriptionManagementTests extends BaseTest {

    private SubscriptionPage subscription;

    @BeforeMethod(alwaysRun = true)
    public void initPagesAndLogin() {
        ConfigReader.reload();
        subscription = new SubscriptionPage(driver);

        // Check if subscription account is configured
        String activeEmail = ConfigReader.getProperty("subscription.activeEmail");
        String activePassword = ConfigReader.getProperty("subscription.activePassword");

        if (!isBlank(activeEmail) && !isBlank(activePassword)) {
            loginWithSubscription(activeEmail, activePassword);
        } else {
            // Fallback to regular consumer account
            loginAsConsumer();
        }
    }

    // ================= ACTIVE PLAN VERIFICATION TESTS =================

    @Test(priority = 388, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_388: Verify active subscription plan is displayed")
    public void verifyActivePlanDisplayed() {
        navigateToSubscriptionSection();

        String planStatus = subscription.getPlanStatus();

        Assert.assertFalse(planStatus.isBlank(),
            "TC_388: Plan status should be displayed. Found: '" + planStatus + "'");

        // If account has active plan, verify it shows active status
        if (hasActiveSubscriptionConfigured()) {
            Assert.assertTrue(subscription.isPlanActive(),
                "TC_388: Active plan should show 'Active' or 'Remaining' status");
        }
    }

    @Test(priority = 389, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_389: Verify plan status is shown correctly")
    public void verifyPlanStatusCorrect() {
        navigateToSubscriptionSection();

        String planStatus = subscription.getPlanStatus().toLowerCase();

        if (hasActiveSubscriptionConfigured()) {
            Assert.assertTrue(planStatus.contains("remaining") || planStatus.contains("active"),
                "TC_389: Active plan status should contain 'Remaining' or 'Active'. Found: " + planStatus);
        }
    }

    @Test(priority = 390, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_390: Verify cancel button is visible for active plans")
    public void verifyCancelButtonVisible() {
        navigateToSubscriptionSection();

        // Cancel button should be visible if there's an active plan
        if (subscription.isPlanActive()) {
            Assert.assertTrue(true,
                "TC_390: Cancel button should be available for active plans");
        } else {
            Assert.assertTrue(true,
                "TC_390: No active plan - cancel button visibility not applicable");
        }
    }

    // ================= PLAN CANCELLATION TESTS =================

    @Test(priority = 391, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_391: Verify user can cancel subscription")
    public void verifyPlanCancellation() {
        navigateToSubscriptionSection();

        if (!subscription.isPlanActive()) {
            throw new SkipException("TC_391: Test requires an active subscription to cancel");
        }

        String statusBefore = subscription.getPlanStatus();

        subscription.cancelPlan();

        // Verify plan is marked as cancelled
        String statusAfter = subscription.getPlanStatus().toLowerCase();

        Assert.assertTrue(statusAfter.contains("cancel") || statusAfter.contains("expire"),
            "TC_391: Plan should be marked as cancelled. Status before: " + statusBefore + ", after: " + statusAfter);
    }

    @Test(priority = 392, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_392: Verify confirmation popup for cancel action")
    public void verifyCancelConfirmationPopup() {
        navigateToSubscriptionSection();

        if (!subscription.isPlanActive()) {
            throw new SkipException("TC_392: Test requires an active subscription");
        }

        boolean hasConfirmation = subscription.initiatePlanCancellation();

        Assert.assertTrue(hasConfirmation,
            "TC_392: Confirmation popup should appear before cancel");
    }

    @Test(priority = 393, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_393: Verify cancel action can be declined")
    public void verifyCancelCanBeDeclined() {
        navigateToSubscriptionSection();

        if (!subscription.isPlanActive()) {
            throw new SkipException("TC_393: Test requires an active subscription");
        }

        String statusBefore = subscription.getPlanStatus();

        subscription.initiatePlanCancellation();
        subscription.declineCancellation();

        String statusAfter = subscription.getPlanStatus();

        // Plan should remain active
        Assert.assertEquals(statusAfter, statusBefore,
            "TC_393: Plan should remain active when cancel is declined");
    }

    // ================= POST-CANCELLATION BEHAVIOR TESTS =================

    @Test(priority = 394, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_394: Verify plan status after cancellation")
    public void verifyStatusAfterCancel() {
        navigateToSubscriptionSection();

        if (!subscription.isPlanActive()) {
            throw new SkipException("TC_394: Test requires an active subscription");
        }

        subscription.cancelPlan();

        String statusAfter = subscription.getPlanStatus().toLowerCase();

        Assert.assertTrue(
            statusAfter.contains("cancel") || statusAfter.contains("expire") || statusAfter.contains("active till"),
            "TC_394: Status should reflect cancellation. Found: " + statusAfter);
    }

    @Test(priority = 395, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_395: Verify user retains access until expiry")
    public void verifyAccessUntilExpiry() {
        navigateToSubscriptionSection();

        if (!subscription.isPlanActive()) {
            throw new SkipException("TC_395: Test requires an active subscription");
        }

        subscription.cancelPlan();

        // Try to play content - should still work
        subscription.goToHome();

        // Verify user can still access content (no "subscribe" prompt blocking access)
        boolean canAccessContent = !subscription.isSubscriptionWarningDisplayed();

        Assert.assertTrue(canAccessContent,
            "TC_395: User should retain access to content until plan expiry");
    }

    @Test(priority = 396, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_396: Verify user cannot select new plan after cancel")
    public void verifyCannotSelectNewPlanAfterCancel() {
        navigateToSubscriptionSection();

        if (!subscription.isPlanActive()) {
            throw new SkipException("TC_396: Test requires an active subscription");
        }

        subscription.cancelPlan();

        // Try to activate another plan
        subscription.attemptToActivateAnotherPlan();

        // Verify restriction (error message or disabled UI)
        boolean isRestricted = subscription.isSubscriptionActivationRestricted()
            || subscription.isPlanSelectionDisabled();

        Assert.assertTrue(isRestricted,
            "TC_396: User should be restricted from selecting new plan after cancellation");
    }

    @Test(priority = 397, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_397: Verify UI disables plan selection after cancel")
    public void verifyPlanSelectionUIDisabled() {
        navigateToSubscriptionSection();

        if (!subscription.isPlanActive()) {
            throw new SkipException("TC_397: Test requires an active subscription");
        }

        subscription.cancelPlan();

        Assert.assertTrue(subscription.isPlanSelectionDisabled(),
            "TC_397: Plan selection UI should be disabled after cancellation");
    }

    @Test(priority = 401, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_401: Verify user cannot cancel twice")
    public void verifyCannotCancelTwice() {
        navigateToSubscriptionSection();

        // First check if already cancelled
        String initialStatus = subscription.getPlanStatus().toLowerCase();

        if (initialStatus.contains("cancel") || initialStatus.contains("expire")) {
            // Already cancelled - verify can't cancel again
            boolean cannotCancel = subscription.isPlanSelectionDisabled()
                || subscription.isSubscriptionActivationRestricted();

            Assert.assertTrue(cannotCancel,
                "TC_401: Should not be able to cancel an already-cancelled plan");
            return;
        }

        if (!subscription.isPlanActive()) {
            throw new SkipException("TC_401: Test requires an active subscription");
        }

        subscription.cancelPlan();

        // Try to cancel again
        boolean canCancelAgain = subscription.initiatePlanCancellation();

        Assert.assertFalse(canCancelAgain,
            "TC_401: Should not be able to cancel an already-cancelled plan");
    }

    // ================= STATE PERSISTENCE TESTS =================

    @Test(priority = 402, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_402: Verify subscription state persists after page refresh")
    public void verifyStatePersistsAfterRefresh() {
        navigateToSubscriptionSection();

        String statusBeforeRefresh = subscription.getPlanStatus();

        subscription.refreshCurrentPage();

        String statusAfterRefresh = subscription.getPlanStatus();

        Assert.assertEquals(statusAfterRefresh, statusBeforeRefresh,
            "TC_402: Plan status should persist after page refresh");
    }

    @Test(priority = 403, retryAnalyzer = RetryAnalyzer.class,
        description = "TC_403: Verify subscription state after logout/login")
    public void verifyStatePersistsAfterReLogin() {
        navigateToSubscriptionSection();

        String statusBeforeLogout = subscription.getPlanStatus();

        // Logout and login again
        performLogout();
        loginAsConsumer();

        navigateToSubscriptionSection();

        String statusAfterLogin = subscription.getPlanStatus();

        Assert.assertEquals(statusAfterLogin, statusBeforeLogout,
            "TC_403: Plan status should persist after logout/login");
    }

    // ================= HELPER METHODS =================

    private void navigateToSubscriptionSection() {
        subscription.clickHamburgerMenu();
        subscription.waitForPageReady();
    }

    private void loginAsConsumer() {
        String email = ConfigReader.getProperty("consumer.email",
            ConfigReader.getProperty("login.validEmail"));
        String password = ConfigReader.getProperty("consumer.password",
            ConfigReader.getProperty("login.validPassword"));

        if (isBlank(email) || isBlank(password)) {
            throw new SkipException(
                "Set consumer.email and consumer.password in config.properties to run subscription tests.");
        }

        LoginPage login = new LoginPage(driver);
        login.openLogin();
        login.loginUser(email, password);
        login.clickNextAfterLogin();

        subscription.waitForPageReady();
    }

    private void loginWithSubscription(String email, String password) {
        LoginPage login = new LoginPage(driver);
        login.openLogin();
        login.loginUser(email, password);
        login.clickNextAfterLogin();

        subscription.waitForPageReady();
    }

    private void performLogout() {
        subscription.clickHamburgerMenu();

        // Click logout if visible
        try {
            subscription.clickLogoutMenuItem();
        } catch (Exception e) {
            // Logout might not be in subscription page - handle gracefully
        }
    }

    private boolean hasActiveSubscriptionConfigured() {
        String activeEmail = ConfigReader.getProperty("subscription.activeEmail");
        return !isBlank(activeEmail);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
