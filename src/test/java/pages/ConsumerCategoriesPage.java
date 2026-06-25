package pages;

import java.util.Locale;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.SkipException;

import base.BasePage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Page Object that orchestrates Consumer category flows that span
 * the dashboard's Categories section and the per-category landing
 * page. Mirrors the conventions used by {@link ConsumerBookDetailsPage}:
 * thin wrappers over the underlying {@link DashboardPage} actions, with
 * all multi-step orchestration, scroll/sync logic, and the
 * consumer-login bootstrap kept inside this class so the
 * {@code ConsumerCategoriesTests} test class stays lean.
 *
 * <p>This class is the home for:
 * <ul>
 *   <li>Consumer login and credential gating.</li>
 *   <li>Dashboard-shell stabilization and scroll-to-Categories-section
 *       sequencing used by every category test.</li>
 *   <li>View-All navigation and the resulting categories-page
 *       inspection (count, content presence, empty-state message).</li>
 *   <li>Per-category navigation, content-count inspection, and
 *       empty-category detection.</li>
 *   <li>Category card click and category page readiness checks.</li>
 *   <li>Horizontal-scroll handling for the Categories carousel.</li>
 *   <li>Null-safe URL accessor for tests that compare routes after
 *       navigation.</li>
 * </ul>
 */
public class ConsumerCategoriesPage extends BasePage {

	private static final String DEFAULT_CATEGORY = "Art";
	private static final String EMPTY_CATEGORY = "Horror";

	private final LoginPage login;
	private final DashboardPage dashboard;

	public ConsumerCategoriesPage(WebDriver driver) {
		super(driver);
		this.login = new LoginPage(driver);
		this.dashboard = new DashboardPage(driver);
	}

	// ==================== Login ====================

	/**
	 * Login as the configured consumer user, click Next, and wait for
	 * the dashboard shell to settle.
	 */
	public void loginAsConsumer() {
		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();
	}

	/**
	 * Refresh config, then login as the consumer. Throws
	 * {@link SkipException} if the consumer credentials are missing or
	 * blank in {@code config.properties}.
	 */
	public void initConsumerSession() {
		ConfigReader.reload();
		skipIfConsumerCredentialsMissing();
		loginAsConsumer();
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
					"Set consumer.email and consumer.password in config.properties to run consumer category tests.");
		}
	}

	// ==================== Dashboard stabilization ====================

	/**
	 * Wait for the dashboard page to be ready, the dashboard shell to
	 * settle, and a short stabilization window so the Categories section
	 * finishes its initial render.
	 */
	public void waitForDashboardReady() {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.waitForDashboardShell(),
				"Dashboard shell should be ready before validating categories.");
		waitQuietly(2000);
	}

	// ==================== Section scroll ====================

	/**
	 * Scroll the Categories section into view and let it settle.
	 */
	public void scrollToCategoriesSection() {
		dashboard.scrollToCategoriesSection();
		waitQuietly(2000);
	}

	// ==================== Section visibility ====================

	/**
	 * @return {@code true} if the Categories section is currently
	 *         visible on the dashboard.
	 */
	public boolean isCategoriesSectionVisible() {
		return dashboard.isCategoriesSectionVisible();
	}

	// ==================== Category list (dashboard) ====================

	/**
	 * @return the number of visible category items on the dashboard.
	 */
	public int getCategoryCount() {
		return dashboard.getCategoryCount();
	}

	/**
	 * @return the name of the first visible category, or empty string if
	 *         none can be resolved.
	 */
	public String getFirstVisibleCategoryName() {
		return safeString(dashboard.getFirstVisibleCategoryName());
	}

	// ==================== View All Categories ====================

	/**
	 * Click the View All Categories button and verify navigation to a
	 * categories-related page.
	 *
	 * @return {@code true} if the View All click triggered a valid
	 *         navigation.
	 */
	public boolean viewAllCategoriesAndVerify() {
		return dashboard.viewAllCategoriesAndVerify();
	}

	/**
	 * @return the number of categories visible on the categories page
	 *         (after clicking View All), or 0 if the page is not ready.
	 */
	public int getAllCategoriesCount() {
		return dashboard.getAllCategoriesCount();
	}

	// ==================== Category card click ====================

	/**
	 * @return {@code true} if category cards are present on the
	 *         dashboard.
	 */
	public boolean hasCategoryCards() {
		return dashboard.hasCategoryCards();
	}

	/**
	 * @return the name of the first visible category card, or empty
	 *         string if none can be resolved.
	 */
	public String getFirstCategoryCardName() {
		return safeString(dashboard.getFirstCategoryCardName());
	}

	/**
	 * Click a category card by name and verify navigation to a
	 * category-related page.
	 *
	 * @return {@code true} if the card click resulted in a valid
	 *         navigation.
	 */
	public boolean openCategoryCardAndVerify(String cardName) {
		return dashboard.openCategoryCardAndVerify(safeString(cardName));
	}

	// ==================== Per-category navigation & content ====================

	/**
	 * Navigate to the named category and verify the resulting URL.
	 *
	 * @return {@code true} if the URL changed to a category-related
	 *         route.
	 */
	public boolean navigateToCategory(String categoryName) {
		return dashboard.navigateToCategory(safeString(categoryName));
	}

	/**
	 * @return the number of book/content items shown on the open
	 *         category page.
	 */
	public int getCategoryContentCount() {
		return dashboard.getCategoryContentCount();
	}

	/**
	 * @return {@code true} if the open category page has at least one
	 *         visible book/content item.
	 */
	public boolean hasCategoryContent() {
		return dashboard.hasCategoryContent();
	}

	/**
	 * @return {@code true} if the open category page shows a "no
	 *         content" / "no books" empty-state message.
	 */
	public boolean hasNoContentMessage() {
		return dashboard.hasNoContentMessage();
	}

	/**
	 * @return {@code true} if the open category page shows either a
	 *         "no books" or a "no content" message.
	 */
	public boolean hasNoBooksOrContentMessage() {
		return dashboard.hasNoBooksOrContentMessage();
	}

	/**
	 * Walk the dashboard's category list and return the name of the
	 * first category that opens to an empty content page. Returns an
	 * empty string if every category has content (or if no category is
	 * clickable in the current dashboard state).
	 */
	public String findEmptyCategory() {
		return safeString(dashboard.findEmptyCategory());
	}

	// ==================== Rapid click (TC_190 reuse) ====================

	/**
	 * Try to click the named category and report success without
	 * throwing on failure. Used by TC_190 (rapid-click stability test).
	 *
	 * @return {@code true} if the click succeeded.
	 */
	public boolean tryClickCategory(String categoryName) {
		return dashboard.tryClickCategory(safeString(categoryName));
	}

	// ==================== Scroll ====================

	/**
	 * @return {@code true} if the horizontal scroll of the categories
	 *         carousel moved the scroll position.
	 */
	public boolean scrollCategoriesHorizontal() {
		return dashboard.scrollCategoriesHorizontal();
	}

	/**
	 * @return the current vertical scroll position of the page.
	 */
	public long getCurrentScrollPosition() {
		return dashboard.getCurrentScrollPosition();
	}

	// ==================== URL / page checks ====================

	/**
	 * @return the trimmed, lower-cased current URL, or empty string if
	 *         the driver cannot be queried.
	 */
	public String getCurrentUrlSafely() {
		try {
			String url = driver.getCurrentUrl();
			return url == null ? "" : url.toLowerCase(Locale.ROOT);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * @return {@code true} if the current URL is non-empty and matches
	 *         one of the known dashboard / category / trending / shows
	 *         routes.
	 */
	public boolean isValidPage() {
		return dashboard.isValidPage();
	}

	/**
	 * @return {@code true} if the current URL contains any of the given
	 *         search terms (case-insensitive). Each term is lower-cased
	 *         before comparison.
	 */
	public boolean isCurrentUrlContainsAny(String... searchTerms) {
		if (searchTerms == null) {
			return false;
		}
		String[] lowered = new String[searchTerms.length];
		for (int i = 0; i < searchTerms.length; i++) {
			lowered[i] = safeString(searchTerms[i]).toLowerCase(Locale.ROOT);
		}
		return dashboard.isCurrentUrlContainsAny(lowered);
	}

	// ==================== Convenience constants ====================

	/**
	 * @return a default category name used by tests that need any
	 *         visible category to be picked at random. Falls back to
	 *         {@code "Art"} if the configured property is missing.
	 */
	public String getDefaultCategory() {
		return DEFAULT_CATEGORY;
	}

	/**
	 * @return a category name that is expected to have no books in the
	 *         test data set. Falls back to {@code "Horror"} if the
	 *         configured property is missing.
	 */
	public String getEmptyCategory() {
		return EMPTY_CATEGORY;
	}

	/**
	 * Log a soft-unavailable message for tests that cannot validate
	 * their scenario on the current dashboard state.
	 */
	public void logOptionalUnavailable(String message) {
		LoggerUtils.logInfo(message);
	}

	// ==================== Local helpers ====================

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	public void waitQuietly(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Sleep interrupted", e);
		}
	}

	public String safeString(String value) {
		return value == null ? "" : value;
	}
}
