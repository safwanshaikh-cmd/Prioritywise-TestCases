package pages;

import org.openqa.selenium.WebDriver;
import org.testng.SkipException;

import base.BasePage;
import utils.ConfigReader;
import utils.LoggerUtils;
import utils.SecurityUtils;

/**
 * Page object that orchestrates security-validation flows (direct-URL guest
 * access, session expiry, role-based access control, payment-flow HTTPS, and
 * session invalidation). Mirrors the conventions used by {@link ChapterPage}
 * and {@link ConsumerBookDetailsPage}: a thin wrapper over the existing
 * {@link LoginPage} / {@link DashboardPage} / {@link SubscriptionPage} APIs,
 * with all multi-step orchestration (login-as-role, session clearing, direct
 * navigation, URL-redirection verification) kept inside this class so the
 * {@code SecurityTests} test class stays lean and free of Selenium/locators.
 *
 * <p>Pure (non-Selenium) verdict logic — guest-access / restricted-access /
 * HTTPS / post-expiry checks, URL building, path-token normalization,
 * sensitive-value masking — lives in {@link SecurityUtils}, which this page
 * composes. No TestNG assertions are made here; the page returns booleans and
 * current-URL strings for the test to assert on.
 *
 * <p>This class is the home for:
 * <ul>
 *   <li>Consumer login + credential gating before each logged-in security
 *       test.</li>
 *   <li>Session clearing via {@link DashboardPage#clearSession()} (cookies +
 *       localStorage + sessionStorage — far more complete than a bare
 *       {@code deleteAllCookies}).</li>
 *   <li>Guest direct-URL access that returns the guest-access verdict.</li>
 *   <li>Admin/restricted-route navigation for role-based access tests.</li>
 *   <li>Payment-sidebar orchestration + HTTPS verification for the payment
 *       security tests.</li>
 *   <li>Null-safe URL accessors and a non-flaky settle wait, mirroring the
 *       sibling page objects.</li>
 * </ul>
 */
public class SecurityPage extends BasePage {

	private final LoginPage login;
	private final DashboardPage dashboard;
	private final SubscriptionPage subscription;

	public SecurityPage(WebDriver driver) {
		super(driver);
		this.login = new LoginPage(driver);
		this.dashboard = new DashboardPage(driver);
		this.subscription = new SubscriptionPage(driver);
	}

	// ==================== Login / session ====================

	/**
	 * Reload config, gate on consumer credentials, then log in as the
	 * configured consumer and wait for the dashboard to settle. Throws
	 * {@link SkipException} when the consumer credentials are missing.
	 */
	public void initConsumerSession() {
		ConfigReader.reload();
		skipIfConsumerCredentialsMissing();
		loginAsConsumer();
	}

	/**
	 * Log in as the configured consumer and stabilize on the post-login
	 * dashboard. Used by the logged-in security tests (role, session-expiry,
	 * payment) as their first step.
	 */
	public void loginAsConsumer() {
		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();
		dashboard.waitForPageReady();
	}

	/**
	 * Clear the active session (cookies + localStorage + sessionStorage) via
	 * {@link DashboardPage#clearSession()}. Replaces every bare
	 * {@code driver.manage().deleteAllCookies()} site in the old tests.
	 */
	public void clearSession() {
		dashboard.clearSession();
	}

	public boolean isOnLoginPage() {
		return login.isOnLoginPage();
	}

	public boolean isRedirectedToLogin() {
		return login.isUrlContaining("login");
	}

	private String getConsumerEmail() {
		return ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
	}

	private String getConsumerPassword() {
		return ConfigReader.getProperty("consumer.password", ConfigReader.getProperty("login.validPassword"));
	}

	private void skipIfConsumerCredentialsMissing() {
		if (SecurityUtils.isBlank(getConsumerEmail()) || SecurityUtils.isBlank(getConsumerPassword())) {
			throw new SkipException(
					"Set consumer.email and consumer.password in config.properties to run security tests.");
		}
	}

	// ==================== Direct-URL / guest access ====================

	/**
	 * @return the configured application base URL.
	 */
	public String getBaseUrl() {
		return ConfigReader.getProperty("url", "");
	}

	/**
	 * @return the admin route used by the role-based access test
	 *         ({@code <baseUrl>/admin}). The old {@code security.adminUrl} /
	 *         {@code admin.url} config keys are intentionally not read — they
	 *         were never present in config.properties, so the runtime
	 *         behavior is identical.
	 */
	public String getAdminRestrictedUrl() {
		String baseUrl = getBaseUrl();
		return baseUrl.endsWith("/") ? baseUrl + "admin" : baseUrl + "/admin";
	}

	/**
	 * Clear any existing session, navigate directly to {@code urlToTest} as a
	 * guest, wait for the page to load, and return the guest-access verdict.
	 * <p>
	 * Replaces the old test-side {@code testDirectUrlAccess} — the page does
	 * <em>not</em> assert; it returns {@code true} when the route is
	 * guest-accessible (expected page token or path token present in the
	 * navigated URL, or no redirect to login) so the test can assert on it.
	 *
	 * @param urlToTest    the absolute URL or path to access directly.
	 * @param expectedPage the expected page token (e.g. {@code book_category_listing});
	 *                     may be {@code null}/blank.
	 * @return {@code true} when the route is guest-accessible.
	 */
	public boolean accessGuestUrlAndVerify(String urlToTest, String expectedPage) {
		clearSession();
		String targetUrl = SecurityUtils.buildTargetUrl(getBaseUrl(), urlToTest);
		try {
			driver.get(targetUrl);
			dashboard.waitForPageReady();
		} catch (Exception e) {
			LoggerUtils.logInfo("Guest navigation to " + targetUrl + " failed: " + safeString(e.getMessage()));
		}
		String currentUrl = getCurrentUrlSafely();
		String pathToken = SecurityUtils.normalizePathToken(
				urlToTest != null && urlToTest.startsWith("http") ? urlToTest : urlToTest);
		return SecurityUtils.isGuestAccessible(currentUrl, expectedPage, pathToken);
	}

	/**
	 * Load {@code url} and return the resulting current URL. Used by the
	 * session-expiry test (store URL → clear session → reload stored URL →
	 * inspect the resulting URL).
	 */
	public String accessUrlAndReturnCurrent(String url) {
		if (SecurityUtils.isBlank(url)) {
			return getCurrentUrlSafely();
		}
		try {
			driver.get(url);
			dashboard.waitForPageReady();
		} catch (Exception e) {
			LoggerUtils.logInfo("Navigation to stored URL failed: " + safeString(e.getMessage()));
		}
		return getCurrentUrlSafely();
	}

	// ==================== Role-based / restricted access ====================

	/**
	 * @return {@code true} when the admin dashboard is loaded for the current
	 *         user. Delegates to {@link DashboardPage#isAdminDashboardLoaded()}.
	 */
	public boolean isAdminDashboardLoaded() {
		return dashboard.isAdminDashboardLoaded();
	}

	/**
	 * Navigate to {@code adminUrl} (as the logged-in consumer) and return the
	 * resulting current URL so the test can assert the consumer was
	 * redirected/blocked (not the admin dashboard).
	 */
	public String openAdminUrlAndGetResult(String adminUrl) {
		try {
			driver.get(adminUrl);
			dashboard.waitForPageReady();
		} catch (Exception e) {
			LoggerUtils.logInfo("Admin route navigation failed: " + safeString(e.getMessage()));
		}
		return getCurrentUrlSafely();
	}

	/**
	 * Verdict for the role-based access test: the current user is kept out of
	 * the admin dashboard when either the admin UI did not load OR the URL
	 * indicates an explicit redirect/restricted state. Delegates the verdict
	 * to {@link SecurityUtils#isRestrictedAccessState(String, boolean)} so the
	 * test does not assemble the boolean itself.
	 *
	 * @param currentUrl          the navigated URL after the admin route attempt.
	 * @param isAdminDashboardLoaded whether the admin dashboard UI rendered.
	 * @return {@code true} when the user was kept out of the admin dashboard.
	 */
	public boolean isRestrictedAccessState(String currentUrl, boolean isAdminDashboardLoaded) {
		return SecurityUtils.isRestrictedAccessState(currentUrl, isAdminDashboardLoaded);
	}

	// ==================== Payment-flow security ====================

	/**
	 * Open the payment surface via the simple side menu, then dismiss the
	 * sidebar. Replaces the inline {@code dashboard.openSimpleSideMenu()} +
	 * {@code subscription.closeSidebarIfOpen()} sequence in the payment
	 * security tests.
	 */
	public void openPaymentSidebar() {
		dashboard.openSimpleSideMenu();
		subscription.closeSidebarIfOpen();
	}

	/**
	 * @return {@code true} when the current page is served over HTTPS. The
	 *         verdict itself lives in {@link SecurityUtils#isHttpsUrl(String)}.
	 */
	public boolean isCurrentUrlHttps() {
		return SecurityUtils.isHttpsUrl(getCurrentUrlSafely());
	}

	/**
	 * Refresh the current page (used after clearing the session to observe
	 * the post-expiry state). Delegates to {@link BasePage#refresh()} which
	 * waits for the page load.
	 */
	public void refreshPage() {
		refresh();
	}

	// ==================== Null-safe / wait helpers ====================

	/**
	 * @return the current URL, or empty string if the driver cannot be
	 *         queried. Replaces ad-hoc
	 *         {@code Objects.requireNonNull(driver.getCurrentUrl())} calls.
	 */
	public String getCurrentUrlSafely() {
		try {
			return driver.getCurrentUrl();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * @return the value, or empty string if it is {@code null}. Useful for
	 *         null-safe concatenation when logging exception messages.
	 *         Mirrors the sibling page objects.
	 */
	public String safeString(String value) {
		return value == null ? "" : value;
	}

	/**
	 * Wait the given number of milliseconds, surfacing interrupts as a runtime
	 * exception. Mirrors {@code waitQuietly} on the sibling page objects so the
	 * tests do not use raw {@link Thread#sleep}.
	 */
	public void waitQuietly(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Sleep interrupted", e);
		}
	}
}
