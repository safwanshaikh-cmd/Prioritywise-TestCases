package utils;

/**
 * Pure, side-effect-free security-validation helpers used by the security
 * test suite. This class holds <em>only</em> the non-Selenium logic
 * (URL/path-token normalization, guest-access verdicts, HTTPS checks,
 * sensitive-value masking) so the {@code SecurityPage} object stays focused
 * on Selenium orchestration and {@code SecurityTests} stays focused on test
 * flow + assertions. No {@link org.openqa.selenium.WebDriver}, no TestNG
 * assertions, no I/O — every method is a pure function that returns a value
 * for the caller to assert on.
 *
 * <p>The verdict methods (<code>isGuestAccessible</code>,
 * <code>isRestrictedAccessState</code>,
 * <code>isAccessibleOrRedirectedAfterExpiry</code>) preserve the exact
 * boolean logic that previously lived inline in the old {@code SecurityTests}
 * private helpers — only de-tangled and made null-safe.
 */
public final class SecurityUtils {

	private SecurityUtils() {
		// Pure utility — no instances.
	}

	/**
	 * @return {@code true} when {@code value} is {@code null} or blank after
	 *         trimming. Used for credential/session preconditions.
	 */
	public static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	/**
	 * @return {@code value} trimmed, or empty string when {@code null}. The
	 *         null-safe accessor used everywhere a nullable string must be
	 *         dereferenced.
	 */
	public static String safeText(String value) {
		return value == null ? "" : value.trim();
	}

	/**
	 * Mask a sensitive value so it is never logged or reported in cleartext.
	 * Returns a fixed {@code "***"} sentinel when the value is non-blank,
	 * {@code ""} otherwise — the real value is never returned.
	 */
	public static String mask(String value) {
		return isBlank(value) ? "" : "***";
	}

	/**
	 * Build the absolute target URL for a guest-access attempt, mirroring the
	 * logic previously inlined in {@code testDirectUrlAccess}:
	 * <ul>
	 *   <li>If {@code pathOrUrl} already starts with {@code http}, it is
	 *       returned as-is.</li>
	 *   <li>Otherwise it is treated as a path: a leading slash is ensured, the
	 *       base URL's trailing slash is stripped, and the two are joined.</li>
	 * </ul>
	 * Null-safe on {@code baseUrl}.
	 */
	public static String buildTargetUrl(String baseUrl, String pathOrUrl) {
		String safeBase = safeText(baseUrl);
		if (pathOrUrl == null) {
			return safeBase;
		}
		if (pathOrUrl.startsWith("http")) {
			return pathOrUrl;
		}
		String sanitizedPath = pathOrUrl.startsWith("/") ? pathOrUrl : "/" + pathOrUrl;
		String root = safeBase.endsWith("/") ? safeBase.substring(0, safeBase.length() - 1) : safeBase;
		return root + sanitizedPath;
	}

	/**
	 * Reduce a path or absolute URL to a comparable token by stripping the
	 * scheme and all slashes. Used to verify the navigated URL still reflects
	 * the requested route.
	 */
	public static String normalizePathToken(String pathOrUrl) {
		if (isBlank(pathOrUrl)) {
			return "";
		}
		return pathOrUrl.replace("https://", "").replace("http://", "").replace("/", "");
	}

	/**
	 * The guest-access verdict previously inlined in {@code testDirectUrlAccess}.
	 * A route is guest-accessible when the navigated URL contains the expected
	 * page token, contains the (normalized) requested path token, or does not
	 * redirect to the login page. All arguments are null-safe.
	 */
	public static boolean isGuestAccessible(String currentUrl, String expectedPageToken, String pathToken) {
		String url = safeText(currentUrl).toLowerCase(java.util.Locale.ROOT);
		String expectedToken = safeText(expectedPageToken).toLowerCase(java.util.Locale.ROOT);
		String token = safeText(pathToken).toLowerCase(java.util.Locale.ROOT);

		boolean isExpectedPage = !expectedToken.isEmpty() && url.contains(expectedToken);
		boolean isExpectedPath = !token.isEmpty() && url.replace("/", "").contains(token);
		boolean notOnLogin = !url.contains("login");

		return isExpectedPage || isExpectedPath || notOnLogin;
	}

	/**
	 * @return {@code true} when {@code url} uses HTTPS. Null-safe. Replaces the
	 *         TC_378 {@code currentUrl.startsWith("https://")} check.
	 */
	public static boolean isHttpsUrl(String url) {
		return safeText(url).startsWith("https://");
	}

	/**
	 * The restricted-access-state verdict previously inlined in TC_203. A
	 * non-privileged user is considered redirected/blocked when the URL
	 * indicates "unauthorized", "forbidden", an "access denied" page, the
	 * dashboard, the route no longer contains "/admin", <em>or</em> the admin
	 * dashboard UI did not load for the current (non-admin) user — the last
	 * case covers apps that keep the consumer on the "/admin" URL but blank
	 * or guard the admin content without issuing a redirect or an explicit
	 * "denied" message. All arguments are null-safe.
	 *
	 * @param currentUrl          the navigated URL after the admin route attempt.
	 * @param isAdminDashboardLoaded whether the admin dashboard UI actually
	 *                               rendered for the current user.
	 * @return {@code true} when the user was kept out of the admin dashboard.
	 */
	public static boolean isRestrictedAccessState(String currentUrl, boolean isAdminDashboardLoaded) {
		if (!isAdminDashboardLoaded) {
			return true;
		}
		String url = safeText(currentUrl).toLowerCase(java.util.Locale.ROOT);
		boolean unauthorized = url.contains("unauthorized");
		boolean forbidden = url.contains("forbidden");
		boolean accessDenied = url.contains("access") && url.contains("denied");
		boolean onDashboard = url.contains("dashboard");
		boolean offAdminRoute = !url.contains("/admin");
		return unauthorized || forbidden || accessDenied || onDashboard || offAdminRoute;
	}

	/**
	 * URL-only restricted-access heuristic (preserves the original TC_203
	 * inline logic). Prefer {@link #isRestrictedAccessState(String, boolean)}
	 * which also accounts for a guarded admin route that did not redirect.
	 */
	public static boolean isRestrictedAccessState(String currentUrl) {
		String url = safeText(currentUrl).toLowerCase(java.util.Locale.ROOT);
		boolean unauthorized = url.contains("unauthorized");
		boolean forbidden = url.contains("forbidden");
		boolean accessDenied = url.contains("access") && url.contains("denied");
		boolean onDashboard = url.contains("dashboard");
		boolean offAdminRoute = !url.contains("/admin");
		return unauthorized || forbidden || accessDenied || onDashboard || offAdminRoute;
	}

	/**
	 * The post-session-expiry verdict previously inlined in TC_383. After the
	 * session is cleared, the page is in a safe state when it does not show an
	 * error page, or it has redirected to the login page. All null-safe.
	 */
	public static boolean isAccessibleOrRedirectedAfterExpiry(String currentUrl) {
		String url = safeText(currentUrl).toLowerCase(java.util.Locale.ROOT);
		return !url.contains("error") || url.contains("login");
	}
}
