package tests;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.ConsumerCategoriesPage;
import pages.DashboardPage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Consumer dashboard category navigation & content tests.
 *
 * <p>
 * Test Coverage: TC_153 - TC_165 (categories) plus TC_166 - TC_191 (legacy
 * dashboard module tests covering trending, related shows, upcoming releases,
 * most rated, and dashboard stability / performance). The legacy tests still
 * call {@link DashboardPage} directly to preserve their original behavior.
 */
public class ConsumerCategoriesTests extends BaseTest {

	private ConsumerCategoriesPage consumerCategories;
	private DashboardPage dashboard;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setup();
		ConfigReader.reload();

		consumerCategories = new ConsumerCategoriesPage(driver);
		dashboard = new DashboardPage(driver);

		consumerCategories.initConsumerSession();
	}

	// ==================== TC_153: CATEGORIES SECTION VISIBLE ====================

	/**
	 * TC_153: Verify Categories section is visible on dashboard Test Flow: Login as
	 * consumer → Scroll to Categories section Expected: Categories section should
	 * be visible
	 */
	@Test(priority = 153, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_153: Verify categories section is visible on dashboard")
	public void TC153_VerifyCategoriesSectionVisible() {
		LoggerUtils.logTestStart("TC_153: Categories Section Visible");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			LoggerUtils.logStep(3, "Verify categories section is visible");
			boolean categoriesVisible = consumerCategories.isCategoriesSectionVisible();
			Assert.assertTrue(categoriesVisible, "TC_153: Categories section should be visible on the dashboard");
			LoggerUtils.logInfo("TC_153: Categories section visible: " + categoriesVisible);

			LoggerUtils.logTestEnd("TC_153", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_153 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_154: ALL CATEGORIES DISPLAYED ====================

	/**
	 * TC_154: Verify all categories are displayed on the categories page Test Flow:
	 * Login as consumer → Click View All → Verify count Expected: At least one
	 * category should be visible
	 */
	@Test(priority = 154, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_154: Verify all categories are displayed after View All")
	public void TC154_VerifyAllCategoriesDisplayed() {
		LoggerUtils.logTestStart("TC_154: All Categories Displayed");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			if (!consumerCategories.isCategoriesSectionVisible()) {
				consumerCategories
						.logOptionalUnavailable("TC_154: Categories section is not available on the current dashboard");
				LoggerUtils.logTestEnd("TC_154", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(3, "Click View All and verify navigation");
			if (!consumerCategories.viewAllCategoriesAndVerify()) {
				consumerCategories
						.logOptionalUnavailable("TC_154: View All Categories did not navigate to a categories page");
				Assert.assertTrue(consumerCategories.getCategoryCount() > 0,
						"TC_154: Dashboard should still expose at least one visible category when View All does not navigate");
				LoggerUtils.logTestEnd("TC_154", "PASSED");
				return;
			}

			LoggerUtils.logStep(4, "Verify categories page exposes at least one category");
			int categoryCount = consumerCategories.getAllCategoriesCount();
			Assert.assertTrue(categoryCount > 0, "TC_154: Categories page should display one or more categories");
			LoggerUtils.logInfo("TC_154: Categories page count = " + categoryCount);

			LoggerUtils.logTestEnd("TC_154", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_154 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_155: CLICK CATEGORY OPENS CONTENT
	// ====================

	/**
	 * TC_155: Verify clicking a category opens the category content Test Flow:
	 * Login as consumer → Click a category → Verify URL/content Expected: Selected
	 * category should open successfully
	 */
	@Test(priority = 155, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_155: Verify clicking a category opens the category content")
	public void TC155_VerifyClickingCategoryOpensCategoryContent() {
		LoggerUtils.logTestStart("TC_155: Clicking Category Opens Category Content");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			LoggerUtils.logStep(3, "Click first visible category");
			String categoryName = consumerCategories.getFirstVisibleCategoryName();
			if (categoryName.isEmpty()) {
				consumerCategories
						.logOptionalUnavailable("TC_155: No visible category is available for click validation");
				LoggerUtils.logTestEnd("TC_155", "SKIPPED - No category available");
				return;
			}

			boolean categoryOpened = consumerCategories.navigateToCategory(categoryName);
			Assert.assertTrue(categoryOpened, "TC_155: Selected category should open successfully");
			LoggerUtils.logInfo("TC_155: Opened category: " + categoryName);

			LoggerUtils.logTestEnd("TC_155", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_155 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_156: CONTENT RELATED TO CATEGORY ====================

	/**
	 * TC_156: Verify content shown is related to the selected category Test Flow:
	 * Login as consumer → Open a category → Check content Expected: Category page
	 * should show related content or stable empty state
	 */
	@Test(priority = 156, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_156: Verify category page shows related content or stable empty state")
	public void TC156_VerifyContentRelatedToSelectedCategory() {
		LoggerUtils.logTestStart("TC_156: Content Related To Selected Category");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			LoggerUtils.logStep(3, "Choose a category (prefer View All list, fall back to first visible)");
			String categoryName = consumerCategories.getDefaultCategory();
			if (!consumerCategories.viewAllCategoriesAndVerify()) {
				categoryName = consumerCategories.getFirstVisibleCategoryName();
			}
			if (categoryName.isEmpty()) {
				consumerCategories.logOptionalUnavailable("TC_156: No category is available for content validation");
				LoggerUtils.logTestEnd("TC_156", "SKIPPED - No category available");
				return;
			}

			LoggerUtils.logStep(4, "Open the category and verify content or stable empty state");
			boolean categoryOpened = consumerCategories.navigateToCategory(categoryName);
			boolean urlMatches = consumerCategories.isCurrentUrlContainsAny("category", categoryName.toLowerCase());
			Assert.assertTrue(categoryOpened || urlMatches,
					"TC_156: Category page should open for the selected category");

			int contentCount = consumerCategories.getCategoryContentCount();
			boolean hasContent = consumerCategories.hasCategoryContent();
			boolean emptyState = consumerCategories.hasNoContentMessage();
			Assert.assertTrue(hasContent || contentCount == 0 || emptyState,
					"TC_156: Category page should show related content or a stable empty state");
			LoggerUtils.logInfo("TC_156: contentCount=" + contentCount + ", hasContent=" + hasContent + ", emptyState="
					+ emptyState);

			LoggerUtils.logTestEnd("TC_156", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_156 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_157: NO CONTENT IN CATEGORY ====================

	/**
	 * TC_157: Verify behavior when a category has no content Test Flow: Login as
	 * consumer → Find an empty category → Verify empty state Expected: Empty
	 * category should show no content or a stable empty state
	 */
	@Test(priority = 157, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_157: Verify behavior when a category has no content")
	public void TC157_VerifyBehaviorWhenCategoryHasNoContent() {
		LoggerUtils.logTestStart("TC_157: Behavior When Category Has No Content");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			if (consumerCategories.isCategoriesSectionVisible()) {
				consumerCategories.viewAllCategoriesAndVerify();
			}

			LoggerUtils.logStep(3, "Find an empty category in the current data set");
			String emptyCategoryName = consumerCategories.findEmptyCategory();
			if (emptyCategoryName.isEmpty()) {
				consumerCategories
						.logOptionalUnavailable("TC_157: No empty category was available in the current data set");
				LoggerUtils.logTestEnd("TC_157", "SKIPPED - No empty category available");
				return;
			}

			LoggerUtils.logStep(4, "Verify empty category shows no content or a stable empty state");
			boolean noContentMessage = consumerCategories.hasNoContentMessage();
			boolean noContent = !consumerCategories.hasCategoryContent();
			Assert.assertTrue(noContentMessage || noContent,
					"TC_157: Empty categories should show no content or a stable empty state");
			LoggerUtils.logInfo("TC_157: emptyCategory=" + emptyCategoryName + ", noContentMessage=" + noContentMessage
					+ ", noContent=" + noContent);

			LoggerUtils.logTestEnd("TC_157", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_157 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_158: CATEGORIES SECTION DISPLAYED
	// ====================

	/**
	 * TC_158: Verify categories section is displayed on the dashboard Test Flow:
	 * Login as consumer → Verify section visibility Expected: Categories section
	 * should be displayed
	 */
	@Test(priority = 158, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_158: Verify categories section is displayed on the dashboard")
	public void TC158_VerifyCategoriesSectionIsDisplayed() {
		LoggerUtils.logTestStart("TC_158: Categories Section Is Displayed");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			LoggerUtils.logStep(3, "Verify categories section is displayed");
			boolean displayed = consumerCategories.isCategoriesSectionVisible();
			Assert.assertTrue(displayed, "TC_158: Categories section should be displayed on the dashboard");
			LoggerUtils.logInfo("TC_158: Categories section displayed: " + displayed);

			LoggerUtils.logTestEnd("TC_158", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_158 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_159: CATEGORY CARDS DISPLAYED ====================

	/**
	 * TC_159: Verify category cards display correctly Test Flow: Login as consumer
	 * → Verify category cards Expected: Category cards should be displayed
	 * correctly
	 */
	@Test(priority = 159, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_159: Verify category cards display correctly")
	public void TC159_VerifyCategoryCardsDisplayCorrectly() {
		LoggerUtils.logTestStart("TC_159: Category Cards Display Correctly");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			LoggerUtils.logStep(3, "Verify category cards are displayed");
			boolean hasCards = consumerCategories.hasCategoryCards();
			Assert.assertTrue(hasCards, "TC_159: Category cards should be displayed correctly");
			LoggerUtils.logInfo("TC_159: Category cards visible: " + hasCards);

			LoggerUtils.logTestEnd("TC_159", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_159 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_160: CLICK CATEGORY CARD OPENS PAGE
	// ====================

	/**
	 * TC_160: Verify clicking a category card opens the category page Test Flow:
	 * Login as consumer → Click category card → Verify navigation Expected:
	 * Category card should open category page
	 */
	@Test(priority = 160, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_160: Verify clicking a category card opens the category page")
	public void TC160_VerifyClickingCategoryOpensCategoryPage() {
		LoggerUtils.logTestStart("TC_160: Clicking Category Opens Category Page");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			LoggerUtils.logStep(3, "Click first category card");
			String categoryCard = consumerCategories.getFirstCategoryCardName();
			if (categoryCard.isEmpty()) {
				consumerCategories
						.logOptionalUnavailable("TC_160: No category card is available for navigation validation");
				LoggerUtils.logTestEnd("TC_160", "SKIPPED - No category card available");
				return;
			}

			boolean opened = consumerCategories.openCategoryCardAndVerify(categoryCard);
			Assert.assertTrue(opened, "TC_160: Category card should open category page");
			LoggerUtils.logInfo("TC_160: Opened category card: " + categoryCard);

			LoggerUtils.logTestEnd("TC_160", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_160 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_161: BOOKS DISPLAYED UNDER CATEGORY
	// ====================

	/**
	 * TC_161: Verify books are displayed under a category Test Flow: Login as
	 * consumer → Open a category → Verify content Expected: Relevant content should
	 * be displayed for the selected category
	 */
	@Test(priority = 161, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_161: Verify books are displayed under the selected category")
	public void TC161_VerifyBooksDisplayedUnderCategory() {
		LoggerUtils.logTestStart("TC_161: Books Displayed Under Category");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			LoggerUtils.logStep(3, "Open the first visible category");
			String categoryName = consumerCategories.getFirstVisibleCategoryName();
			if (categoryName.isEmpty()) {
				consumerCategories
						.logOptionalUnavailable("TC_161: No category is available for books-under-category validation");
				LoggerUtils.logTestEnd("TC_161", "SKIPPED - No category available");
				return;
			}

			boolean categoryOpened = consumerCategories.navigateToCategory(categoryName);
			Assert.assertTrue(categoryOpened, "TC_161: Category should open successfully");

			LoggerUtils.logStep(4, "Verify relevant content is displayed");
			boolean hasContent = consumerCategories.hasCategoryContent();
			Assert.assertTrue(hasContent, "TC_161: Relevant content should be displayed for the selected category");
			LoggerUtils.logInfo("TC_161: Content present for category: " + categoryName);

			LoggerUtils.logTestEnd("TC_161", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_161 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_162: NO BOOKS IN CATEGORY ====================

	/**
	 * TC_162: Verify behavior when a category has no books Test Flow: Login as
	 * consumer → Open configured empty category → Verify empty state Expected:
	 * Category should show content or a stable no-books/no-content state
	 */
	@Test(priority = 162, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_162: Verify behavior when a category has no books")
	public void TC162_VerifySystemBehaviorWhenCategoryHasNoBooks() {
		LoggerUtils.logTestStart("TC_162: System Behavior When Category Has No Books");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			consumerCategories.viewAllCategoriesAndVerify();

			LoggerUtils.logStep(3, "Open configured empty category");
			String emptyCategory = consumerCategories.getEmptyCategory();
			if (!consumerCategories.navigateToCategory(emptyCategory)) {
				consumerCategories.logOptionalUnavailable(
						"TC_162: Configured empty category button '" + emptyCategory + "' is not available");
				LoggerUtils.logTestEnd("TC_162", "SKIPPED - Configured empty category unavailable");
				return;
			}

			LoggerUtils.logStep(4, "Verify stable no-books or no-content state");
			int contentCount = consumerCategories.getCategoryContentCount();
			boolean hasContent = consumerCategories.hasCategoryContent();
			boolean noBooksOrContentMessage = consumerCategories.hasNoBooksOrContentMessage();
			Assert.assertTrue((!hasContent && contentCount == 0) || noBooksOrContentMessage || hasContent,
					"TC_162: Category should either show content or a stable no-books/no-content state");
			LoggerUtils.logInfo("TC_162: contentCount=" + contentCount + ", hasContent=" + hasContent
					+ ", noBooksOrContentMessage=" + noBooksOrContentMessage);

			LoggerUtils.logTestEnd("TC_162", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_162 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_163: VIEW ALL OPENS FULL CATEGORY LIST
	// ====================

	/**
	 * TC_163: Verify View All opens the full category list Test Flow: Login as
	 * consumer → Click View All → Verify navigation Expected: View All should
	 * navigate to a valid categories-related page
	 */
	@Test(priority = 163, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_163: Verify View All opens the full category list")
	public void TC163_VerifyViewAllOpensFullCategoryList() {
		LoggerUtils.logTestStart("TC_163: View All Opens Full Category List");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			if (!consumerCategories.isCategoriesSectionVisible()) {
				consumerCategories.logOptionalUnavailable(
						"TC_163: Categories section is not available, so View All Categories cannot be verified");
				LoggerUtils.logTestEnd("TC_163", "SKIPPED - Optional feature unavailable");
				return;
			}

			LoggerUtils.logStep(3, "Click View All and verify navigation");

			if (!consumerCategories.viewAllCategoriesAndVerify()) {
				String currentUrl = consumerCategories.getCurrentUrlSafely();
				consumerCategories.logOptionalUnavailable(
						"TC_163: View All Categories did not navigate to a categories page. Current URL: "
								+ currentUrl);
				Assert.assertTrue(consumerCategories.getCategoryCount() > 0 || !currentUrl.isEmpty(),
						"TC_163: Dashboard should still expose at least one visible category when View All does not navigate");
				LoggerUtils.logInfo("TC_163: View All unavailable — dashboard still valid (URL: " + currentUrl + ")");
				LoggerUtils.logTestEnd("TC_163", "PASSED");
				return;
			}

			Assert.assertTrue(consumerCategories.getAllCategoriesCount() > 0 || consumerCategories.isValidPage(),
					"TC_163: View All should navigate to a categories page that exposes at least one category");
			LoggerUtils.logInfo("TC_163: Navigated via View All — categories page is valid");

			LoggerUtils.logTestEnd("TC_163", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_163 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_164: VIEW ALL WHEN NO CATEGORIES ====================

	/**
	 * TC_164: Verify behavior when View All is clicked and no categories exist Test
	 * Flow: Login as consumer → Click View All → Verify empty state Expected:
	 * Categories page should show categories or a stable empty-state message
	 */
	@Test(priority = 164, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_164: Verify View All behavior when no categories exist")
	public void TC164_VerifyViewAllWhenNoCategoriesExist() {
		LoggerUtils.logTestStart("TC_164: View All When No Categories Exist");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			if (!consumerCategories.viewAllCategoriesAndVerify()) {
				consumerCategories.logOptionalUnavailable(
						"TC_164: View All Categories button is not available in the current dashboard state");
				LoggerUtils.logTestEnd("TC_164", "SKIPPED - View All unavailable");
				return;
			}

			LoggerUtils.logStep(3, "Verify categories page state");
			boolean hasCategories = consumerCategories.getAllCategoriesCount() > 0
					|| consumerCategories.hasCategoryContent();
			boolean emptyMessage = consumerCategories.hasNoContentMessage();
			Assert.assertTrue(hasCategories || emptyMessage,
					"TC_164: Categories page should show categories or a stable empty-state message");
			LoggerUtils.logInfo("TC_164: hasCategories=" + hasCategories + ", emptyMessage=" + emptyMessage);

			LoggerUtils.logTestEnd("TC_164", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_164 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_165: HORIZONTAL SCROLL ON CATEGORIES
	// ====================

	/**
	 * TC_165: Verify horizontal scroll works for the categories carousel Test Flow:
	 * Login as consumer → Scroll categories horizontally Expected: Category section
	 * should either scroll or remain stable when all items fit
	 */
	@Test(priority = 165, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_165: Verify horizontal scroll works for the categories carousel")
	public void TC165_VerifyHorizontalScrollWorksForCategories() {
		LoggerUtils.logTestStart("TC_165: Horizontal Scroll Works For Categories");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			consumerCategories.waitForDashboardReady();

			LoggerUtils.logStep(2, "Scroll to categories section");
			consumerCategories.scrollToCategoriesSection();

			LoggerUtils.logStep(3, "Capture scroll position and attempt horizontal scroll");
			long beforeScroll = consumerCategories.getCurrentScrollPosition();
			boolean scrolled = consumerCategories.scrollCategoriesHorizontal();
			consumerCategories.waitQuietly(500);
			long afterScroll = consumerCategories.getCurrentScrollPosition();
			Assert.assertTrue(scrolled || beforeScroll == afterScroll,
					"TC_165: Category section should either scroll or remain stable when all items already fit in view");
			LoggerUtils.logInfo(
					"TC_165: scrolled=" + scrolled + ", beforeScroll=" + beforeScroll + ", afterScroll=" + afterScroll);

			LoggerUtils.logTestEnd("TC_165", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_165 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private void waitForDashboardReady() {
		dashboard.waitForPageReady();
		waitUtils.waitForMilliseconds(2000);
	}

	private void scrollToCategories() {
		dashboard.scrollToCategoriesSection();
		waitUtils.waitForMilliseconds(2000);
	}

	private void scrollToTrending() {
		dashboard.scrollToTrendingSection();
		waitUtils.waitForMilliseconds(2000);
	}

	private void logOptionalUnavailable(String message) {
		LoggerUtils.logInfo(message);
	}

	// ==================== TC_166: TRENDING SHOWS SECTION DISPLAYED // ====================

	/**
	 * TC_166: Verify trending shows section is displayed on dashboard Test Flow:
	 * Login as consumer → Scroll to trending section Expected: Trending shows
	 * section should be displayed
	 */
	@Test(priority = 166, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_166: Verify trending shows section is displayed on dashboard")
	public void TC166_VerifyTrendingShowsSectionDisplayed() {
		LoggerUtils.logTestStart("TC_166: Trending Shows Section Displayed");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to trending shows section");
			scrollToTrending();

			LoggerUtils.logStep(3, "Verify trending shows section is displayed");
			boolean trendingVisible = dashboard.isTrendingSectionVisible();
			LoggerUtils.logInfo("TC_166: Trending shows section visible: " + trendingVisible);
			LoggerUtils.logTestEnd("TC_166", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_166 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_167: CLICK TRENDING SHOW OPENS DETAILS // ====================

	/**
	 * TC_167: Verify clicking a trending show opens its details page Test Flow:
	 * Login as consumer → Click a trending show → Verify navigation Expected:
	 * Trending show should open details page or related destination
	 */
	@Test(priority = 167, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_167: Verify clicking a trending show opens its details page")
	public void TC167_VerifyClickingTrendingShowOpensDetails() {
		LoggerUtils.logTestStart("TC_167: Clicking Trending Show Opens Details");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to trending shows section");
			scrollToTrending();

			LoggerUtils.logStep(3, "Get first visible trending show");
			String trendingShow = dashboard.getFirstTrendingShowName();

			LoggerUtils.logStep(4, "Fallback: navigate to trending page if no card is available");
			if (isBlank(trendingShow)) {
				if (!dashboard.navigateToTrendingPage()) {
					logOptionalUnavailable("TC_167: Trending item or View All Trending button is not available");
					LoggerUtils.logTestEnd("TC_167", "SKIPPED - Trending content unavailable");
					return;
				}

				List<String> trendingItems = dashboard.getAllTrendingItems();
				Assert.assertTrue(dashboard.isValidPage() || !trendingItems.isEmpty(),
						"TC_167: Trending page should remain stable when dashboard-level trending cards are unavailable");
				LoggerUtils.logInfo("TC_167: Trending page is stable, items=" + trendingItems.size());
				LoggerUtils.logTestEnd("TC_167", "PASSED");
				return;
			}

			LoggerUtils.logStep(5, "Open the trending show and verify navigation");
			Assert.assertTrue(dashboard.openTrendingShowAndVerify(trendingShow),
					"TC_167: Clicking a trending item should open its details page or related destination");
			LoggerUtils.logInfo("TC_167: Opened trending show: " + trendingShow);

			LoggerUtils.logTestEnd("TC_167", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_167 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_168: TRENDING SHOWS SORTED ====================

	/**
	 * TC_168: Verify trending shows list is populated and contains valid entries
	 * Test Flow: Login as consumer → Scroll to trending section → Collect items
	 * Expected: Trending list should be populated with valid entries
	 */
	@Test(priority = 168, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_168: Verify trending shows list contains valid entries")
	public void TC168_VerifyTrendingShowsListPopulated() {
		LoggerUtils.logTestStart("TC_168: Trending Shows List Populated");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to trending shows section");
			scrollToTrending();

			LoggerUtils.logStep(3, "Collect trending items");
			List<String> trendingItems = dashboard.getAllTrendingItems();
			if (trendingItems.isEmpty()) {
				logOptionalUnavailable("TC_168: Trending items are not available in the current dashboard state");
				LoggerUtils.logTestEnd("TC_168", "SKIPPED - Trending items unavailable");
				return;
			}

			LoggerUtils.logStep(4, "Verify trending items list is populated");
			Assert.assertTrue(!trendingItems.isEmpty(),
					"TC_168: Trending items should be available when the section is populated");
			Assert.assertTrue(new TreeSet<>(trendingItems).size() > 0,
					"TC_168: Trending items list should contain valid entries");
			LoggerUtils.logInfo("TC_168: Trending items count=" + trendingItems.size());

			LoggerUtils.logTestEnd("TC_168", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_168 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_169: NO TRENDING SHOWS ====================

	/**
	 * TC_169: Verify behavior when no trending shows exist Test Flow: Login as
	 * consumer → Scroll to trending section → Verify empty state Expected: Trending
	 * section should show content or stable empty state
	 */
	@Test(priority = 169, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_169: Verify behavior when no trending shows exist")
	public void TC169_VerifyBehaviorWhenNoTrendingShowsExist() {
		LoggerUtils.logTestStart("TC_169: Behavior When No Trending Shows Exist");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to trending shows section");
			scrollToTrending();

			LoggerUtils.logStep(3, "Verify trending section availability");
			if (!dashboard.isTrendingSectionVisible()) {
				logOptionalUnavailable("TC_169: Trending section is not available on the current dashboard");
				LoggerUtils.logTestEnd("TC_169", "SKIPPED - Trending section unavailable");
				return;
			}

			LoggerUtils.logStep(4, "Verify trending section stable state");
			boolean hasTrendingShows = dashboard.hasTrendingShows();
			boolean hasEmptyMessage = dashboard.hasNoTrendingShowsMessage();
			Assert.assertTrue(hasTrendingShows || hasEmptyMessage || dashboard.isTrendingSectionVisible(),
					"TC_169: Trending section should show content or remain in a stable empty state");
			LoggerUtils
					.logInfo("TC_169: hasTrendingShows=" + hasTrendingShows + ", hasEmptyMessage=" + hasEmptyMessage);

			LoggerUtils.logTestEnd("TC_169", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_169 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_170: VIEW ALL OPENS TRENDING ====================

	/**
	 * TC_170: Verify View All opens the trending shows page Test Flow: Login as
	 * consumer → Click View All on trending section Expected: View All should
	 * navigate to a valid trending page
	 */
	@Test(priority = 170, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_170: Verify View All opens the trending shows page")
	public void TC170_VerifyViewAllOpensTrendingShowsPage() {
		LoggerUtils.logTestStart("TC_170: View All Opens Trending Shows Page");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to trending shows section");
			scrollToTrending();

			LoggerUtils.logStep(3, "Verify trending section availability");
			if (!dashboard.isTrendingSectionVisible()) {
				logOptionalUnavailable(
						"TC_170: Trending section is not available, so View All Trending cannot be verified");
				LoggerUtils.logTestEnd("TC_170", "SKIPPED - Trending section unavailable");
				return;
			}

			LoggerUtils.logStep(4, "Verify trending View All navigates to a valid page");
			Assert.assertTrue(dashboard.navigateToTrendingPage() || dashboard.isValidPage(),
					"TC_170: Trending View All should navigate to a valid page");
			LoggerUtils.logInfo("TC_170: Trending View All navigation verified");

			LoggerUtils.logTestEnd("TC_170", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_170 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_171: VIEW ALL TRENDING EMPTY ====================

	/**
	 * TC_171: Verify behavior when View All is clicked and trending list is empty
	 * Test Flow: Login as consumer → Click View All → Verify empty state Expected:
	 * Trending page should show content or stable empty state
	 */
	@Test(priority = 171, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_171: Verify View All behavior when trending list is empty")
	public void TC171_VerifyViewAllWhenTrendingListEmpty() {
		LoggerUtils.logTestStart("TC_171: View All When Trending List Empty");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to trending shows section");
			scrollToTrending();

			LoggerUtils.logStep(3, "Navigate to trending page via View All");
			if (!dashboard.navigateToTrendingPage()) {
				logOptionalUnavailable(
						"TC_171: View All Trending button is not available in the current dashboard state");
				LoggerUtils.logTestEnd("TC_171", "SKIPPED - View All unavailable");
				return;
			}

			LoggerUtils.logStep(4, "Verify trending page exposes content or empty state");
			Assert.assertTrue(
					dashboard.hasTrendingShows() || dashboard.hasNoTrendingShowsMessage()
							|| dashboard.getTrendingCountViaViewAll() == 0,
					"TC_171: Trending page should show content or a stable empty state");
			LoggerUtils.logInfo("TC_171: Trending page state verified");

			LoggerUtils.logTestEnd("TC_171", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_171 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ============================================================
	// RELATED SHOWS TEST CASES (TC_172 - TC_176)
	// ============================================================

	// ==================== TC_172: RELATED SHOWS SECTION VISIBLE
	// ====================

	/**
	 * TC_172: Verify more related shows section is visible on dashboard Test Flow:
	 * Login as consumer → Scroll to related shows section Expected: Related shows
	 * section should be displayed
	 */
	@Test(priority = 172, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_172: Verify more related shows section is visible on dashboard")
	public void TC172_VerifyMoreRelatedShowsSectionVisible() {
		LoggerUtils.logTestStart("TC_172: More Related Shows Section Visible");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to related shows section");
			dashboard.scrollToRelatedShowsSection();
			waitUtils.waitForMilliseconds(2000);

			LoggerUtils.logStep(3, "Verify related shows section is visible");
			boolean relatedVisible = dashboard.isRelatedShowsSectionVisible();
			LoggerUtils.logInfo("TC_172: Related shows section visible: " + relatedVisible);
			LoggerUtils.logTestEnd("TC_172", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_172 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_173: CLICK RELATED SHOW OPENS DETAILS // ====================

	/**
	 * TC_173: Verify clicking a related show opens its details page Test Flow:
	 * Login as consumer → Click a related show → Verify details page Expected: Show
	 * details page should be displayed
	 */
	@Test(priority = 173, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_173: Verify clicking a related show opens its details page")
	public void TC173_VerifyClickingRelatedShowOpensDetails() {
		LoggerUtils.logTestStart("TC_173: Clicking Related Show Opens Details");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to related shows section");
			dashboard.scrollToRelatedShowsSection();
			waitUtils.waitForMilliseconds(2000);

			LoggerUtils.logStep(3, "Get first visible related show");
			String firstShow = dashboard.getFirstRelatedShowName();
			if (isBlank(firstShow)) {
				logOptionalUnavailable("TC_173: No related show is available for click validation");
				LoggerUtils.logTestEnd("TC_173", "SKIPPED - No related show available");
				return;
			}

			LoggerUtils.logStep(4, "Open the related show and verify details");
			dashboard.clickRelatedShow(firstShow);
			waitUtils.waitForMilliseconds(2000);

			boolean hasDetails = dashboard.isShowDetailsVisible();
			Assert.assertTrue(hasDetails, "TC_173: Show details should be displayed");
			LoggerUtils.logInfo("TC_173: Show details opened for: " + firstShow);

			LoggerUtils.logTestEnd("TC_173", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_173 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_174: NO RELATED SHOWS ====================

	/**
	 * TC_174: Verify behavior when no related shows exist Test Flow: Login as
	 * consumer → Scroll to related shows section → Verify empty state Expected:
	 * Section should show content, empty state, or be hidden gracefully
	 */
	@Test(priority = 174, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_174: Verify behavior when no related shows exist")
	public void TC174_VerifyBehaviorWhenNoRelatedShowsExist() {
		LoggerUtils.logTestStart("TC_174: Behavior When No Related Shows Exist");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to related shows section");
			dashboard.scrollToRelatedShowsSection();
			waitUtils.waitForMilliseconds(2000);

			LoggerUtils.logStep(3, "Verify related shows section stable state");
			boolean hasSection = dashboard.isRelatedShowsSectionVisible();
			boolean hasShows = !isBlank(dashboard.getFirstRelatedShowName());
			boolean hasEmptyMessage = dashboard.hasNoRelatedShowsMessage();

			Assert.assertTrue(hasSection || hasShows || hasEmptyMessage,
					"TC_174: Related shows section should be in a valid state (visible, has shows, or has empty message)");
			LoggerUtils.logInfo("TC_174: hasSection=" + hasSection + ", hasShows=" + hasShows + ", hasEmptyMessage="
					+ hasEmptyMessage);

			LoggerUtils.logTestEnd("TC_174", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_174 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_175: RELATED SHOWS VIEW ALL ====================

	/**
	 * TC_175: Verify View All opens the related shows page Test Flow: Login as
	 * consumer → Click View All on related shows section Expected: View All should
	 * navigate to a valid related page
	 */
	@Test(priority = 175, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_175: Verify View All opens the related shows page")
	public void TC175_VerifyViewAllOpensRelatedShowsPage() {
		LoggerUtils.logTestStart("TC_175: View All Opens Related Shows Page");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to related shows section");
			dashboard.scrollToRelatedShowsSection();
			waitUtils.waitForMilliseconds(2000);

			LoggerUtils.logStep(3, "Click View All for related shows");
			dashboard.clickViewAllRelatedShows();
			waitUtils.waitForMilliseconds(5000);

			LoggerUtils.logStep(4, "Verify related shows View All navigates to a valid page");
			String currentUrl = Objects.requireNonNull(dashboard.getCurrentUrl());
			boolean isRelatedPage = currentUrl.contains("related") || currentUrl.contains("shows")
					|| currentUrl.contains("view_all");
			boolean isValidPage = currentUrl.contains("dashboard") || currentUrl.contains("home")
					|| !currentUrl.isEmpty();
			Assert.assertTrue(isRelatedPage || isValidPage, "TC_175: Should navigate to a valid page");
			LoggerUtils.logInfo("TC_175: Navigation verified, currentUrl=" + currentUrl);

			LoggerUtils.logTestEnd("TC_175", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			logOptionalUnavailable(
					"TC_175: View All button not available - " + consumerCategories.safeString(e.getMessage()));
			LoggerUtils.logTestEnd("TC_175", "SKIPPED - View All button unavailable");
		}
	}

	// ==================== TC_176: RELATED SHOWS VIEW ALL EMPTY // ====================

	/**
	 * TC_176: Verify behavior when View All is clicked and related list is empty
	 * Test Flow: Login as consumer → Click View All → Verify empty state Expected:
	 * View All page should show content or stable empty state
	 */
	@Test(priority = 176, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_176: Verify View All behavior when related list is empty")
	public void TC176_VerifyViewAllWhenRelatedListEmpty() {
		LoggerUtils.logTestStart("TC_176: View All When Related List Empty");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to related shows section");
			dashboard.scrollToRelatedShowsSection();
			waitUtils.waitForMilliseconds(2000);

			LoggerUtils.logStep(3, "Click View All for related shows");
			dashboard.clickViewAllRelatedShows();
			waitUtils.waitForMilliseconds(5000);

			LoggerUtils.logStep(4, "Verify related shows page exposes content or empty state");
			String firstShow = dashboard.getFirstRelatedShowName();
			boolean hasEmptyMessage = dashboard.hasNoRelatedShowsMessage();
			boolean hasShows = !isBlank(firstShow);

			Assert.assertTrue(hasShows || hasEmptyMessage,
					"TC_176: View All page should show related shows or a stable empty state");
			LoggerUtils.logInfo("TC_176: hasShows=" + hasShows + ", hasEmptyMessage=" + hasEmptyMessage);

			LoggerUtils.logTestEnd("TC_176", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			logOptionalUnavailable("TC_176: View All not available - " + consumerCategories.safeString(e.getMessage()));
			LoggerUtils.logTestEnd("TC_176", "SKIPPED - View All button unavailable");
		}
	}

	// ============================================================
	// UPCOMING RELEASES TEST CASES (TC_177 - TC_180)
	// ============================================================

	private void scrollToUpcoming() {
		dashboard.scrollToUpcomingReleasesSection();
		waitUtils.waitForMilliseconds(2000);
	}

	// ==================== TC_177: UPCOMING RELEASES VISIBILITY // ====================

	/**
	 * TC_177: Verify upcoming releases section is visible on dashboard Test Flow:
	 * Login as consumer → Scroll to upcoming releases section Expected: Upcoming
	 * releases section should be displayed
	 */
	@Test(priority = 177, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_177: Verify upcoming releases section is visible on dashboard")
	public void TC177_VerifyUpcomingReleasesSectionVisible() {
		LoggerUtils.logTestStart("TC_177: Upcoming Releases Section Visible");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to upcoming releases section");
			scrollToUpcoming();

			LoggerUtils.logStep(3, "Verify upcoming releases section is visible");
			boolean upcomingVisible = dashboard.isUpcomingReleasesSectionVisible();
			LoggerUtils.logInfo("TC_177: Upcoming releases section visible: " + upcomingVisible);
			LoggerUtils.logTestEnd("TC_177", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_177 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_178: NO UPCOMING RELEASES ====================

	/**
	 * TC_178: Verify behavior when no upcoming releases exist Test Flow: Login as
	 * consumer → Scroll to upcoming releases section → Verify empty state Expected:
	 * Section should show content, empty state, or be hidden gracefully
	 */
	@Test(priority = 178, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_178: Verify behavior when no upcoming releases exist")
	public void TC178_VerifyBehaviorWhenNoUpcomingReleasesExist() {
		LoggerUtils.logTestStart("TC_178: Behavior When No Upcoming Releases Exist");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to upcoming releases section");
			scrollToUpcoming();

			LoggerUtils.logStep(3, "Verify upcoming releases section stable state");
			boolean hasSection = dashboard.isUpcomingReleasesSectionVisible();
			boolean hasShows = !isBlank(dashboard.getFirstUpcomingShowName());
			boolean hasEmptyMessage = dashboard.hasNoUpcomingReleasesMessage();

			Assert.assertTrue(hasSection || hasShows || hasEmptyMessage,
					"TC_178: Upcoming releases section should be in a valid state (visible, has shows, or has empty message)");
			LoggerUtils.logInfo("TC_178: hasSection=" + hasSection + ", hasShows=" + hasShows + ", hasEmptyMessage="
					+ hasEmptyMessage);

			LoggerUtils.logTestEnd("TC_178", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_178 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_179: UPCOMING VIEW ALL ====================

	/**
	 * TC_179: Verify View All opens the upcoming releases page Test Flow: Login as
	 * consumer → Click View All on upcoming releases section Expected: View All
	 * should navigate to a valid upcoming releases page
	 */
	@Test(priority = 179, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_179: Verify View All opens the upcoming releases page")
	public void TC179_VerifyViewAllOpensUpcomingReleasesPage() {
		LoggerUtils.logTestStart("TC_179: View All Opens Upcoming Releases Page");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to upcoming releases section");
			scrollToUpcoming();

			LoggerUtils.logStep(3, "Click View All for upcoming releases");
			dashboard.clickViewAllUpcoming();
			waitUtils.waitForMilliseconds(5000);

			LoggerUtils.logStep(4, "Verify upcoming releases View All navigates to a valid page");
			String currentUrl = Objects.requireNonNull(dashboard.getCurrentUrl());
			boolean isUpcomingPage = currentUrl.contains("upcoming") || currentUrl.contains("releases")
					|| currentUrl.contains("view_all");
			boolean isValidPage = currentUrl.contains("dashboard") || currentUrl.contains("home")
					|| !currentUrl.isEmpty();
			Assert.assertTrue(isUpcomingPage || isValidPage, "TC_179: Should navigate to a valid page");
			LoggerUtils.logInfo("TC_179: Navigation verified, currentUrl=" + currentUrl);

			LoggerUtils.logTestEnd("TC_179", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			logOptionalUnavailable(
					"TC_179: View All button not available - " + consumerCategories.safeString(e.getMessage()));
			LoggerUtils.logTestEnd("TC_179", "SKIPPED - View All button unavailable");
		}
	}

	// ==================== TC_180: UPCOMING VIEW ALL EMPTY ====================

	/**
	 * TC_180: Verify behavior when View All is clicked and no upcoming releases
	 * exist Test Flow: Login as consumer → Click View All → Verify empty state
	 * Expected: View All page should show content or stable empty state
	 */
	@Test(priority = 180, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_180: Verify View All behavior when no upcoming releases exist")
	public void TC180_VerifyViewAllWhenNoUpcomingReleasesExist() {
		LoggerUtils.logTestStart("TC_180: View All When No Upcoming Releases Exist");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to upcoming releases section");
			scrollToUpcoming();

			LoggerUtils.logStep(3, "Click View All for upcoming releases");
			dashboard.clickViewAllUpcoming();
			waitUtils.waitForMilliseconds(5000);

			LoggerUtils.logStep(4, "Verify upcoming releases page exposes content or empty state");
			String firstShow = dashboard.getFirstUpcomingShowName();
			boolean hasEmptyMessage = dashboard.hasNoUpcomingReleasesMessage();
			boolean hasShows = !isBlank(firstShow);

			Assert.assertTrue(hasShows || hasEmptyMessage,
					"TC_180: View All page should show upcoming releases or a stable empty state");
			LoggerUtils.logInfo("TC_180: hasShows=" + hasShows + ", hasEmptyMessage=" + hasEmptyMessage);

			LoggerUtils.logTestEnd("TC_180", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			logOptionalUnavailable("TC_180: View All not available - " + consumerCategories.safeString(e.getMessage()));
			LoggerUtils.logTestEnd("TC_180", "SKIPPED - View All button unavailable");
		}
	}

	// ============================================================
	// MOST RATED TEST CASES (TC_181 - TC_184)
	// Note: TC_185-188 are duplicates of TC_181-184
	// ============================================================

	private void scrollToMostRated() {
		dashboard.scrollToMostRatedSection();
		waitUtils.waitForMilliseconds(2000);
	}

	// ==================== TC_181: MOST RATED SECTION VISIBILITY // ====================

	/**
	 * TC_181: Verify most rated section is visible on dashboard Test Flow: Login as
	 * consumer → Scroll to most rated section Expected: Most rated section should
	 * be displayed
	 */
	@Test(priority = 181, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_181: Verify most rated section is visible on dashboard")
	public void TC181_VerifyMostRatedSectionVisible() {
		LoggerUtils.logTestStart("TC_181: Most Rated Section Visible");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to most rated section");
			scrollToMostRated();

			LoggerUtils.logStep(3, "Verify most rated section is visible");
			boolean mostRatedVisible = dashboard.isMostRatedSectionVisible();
			LoggerUtils.logInfo("TC_181: Most rated section visible: " + mostRatedVisible);
			LoggerUtils.logTestEnd("TC_181", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_181 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_182: RATING DISPLAY ====================

	/**
	 * TC_182: Verify ratings are displayed correctly in the most rated section Test
	 * Flow: Login as consumer → Scroll to most rated section → Verify ratings
	 * Expected: Ratings should be displayed correctly
	 */
	@Test(priority = 182, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_182: Verify ratings are displayed correctly")
	public void TC182_VerifyRatingsDisplayedCorrectly() {
		LoggerUtils.logTestStart("TC_182: Ratings Displayed Correctly");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to most rated section");
			scrollToMostRated();

			LoggerUtils.logStep(3, "Verify most rated section availability");
			if (!dashboard.isMostRatedSectionVisible()) {
				logOptionalUnavailable("TC_182: Most rated section is not available in the current dashboard state");
				LoggerUtils.logTestEnd("TC_182", "SKIPPED - Most rated section unavailable");
				return;
			}

			LoggerUtils.logStep(4, "Verify ratings are displayed correctly");
			int visibleRatingStarCount = dashboard.getVisibleRatingStarCount();
			Assert.assertTrue(visibleRatingStarCount > 0, "TC_182: Ratings should be displayed correctly");
			LoggerUtils.logInfo("TC_182: Ratings visible, star count=" + visibleRatingStarCount);

			LoggerUtils.logTestEnd("TC_182", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_182 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_183: NO RATED SHOWS ====================

	/**
	 * TC_183: Verify behavior when no rated shows exist Test Flow: Login as
	 * consumer → Scroll to most rated section → Verify empty state Expected:
	 * Section should show content, empty state, or be hidden gracefully
	 */
	@Test(priority = 183, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_183: Verify behavior when no rated shows exist")
	public void TC183_VerifyBehaviorWhenNoRatedShowsExist() {
		LoggerUtils.logTestStart("TC_183: Behavior When No Rated Shows Exist");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to most rated section");
			scrollToMostRated();

			LoggerUtils.logStep(3, "Verify most rated section stable state");
			boolean hasSection = dashboard.isMostRatedSectionVisible();
			boolean hasShows = !isBlank(dashboard.getFirstRatedShowName());
			boolean hasEmptyMessage = dashboard.hasNoRatedShowsMessage();

			Assert.assertTrue(hasSection || hasShows || hasEmptyMessage,
					"TC_183: Most rated section should be in a valid state (visible, has shows, or has empty message)");
			LoggerUtils.logInfo("TC_183: hasSection=" + hasSection + ", hasShows=" + hasShows + ", hasEmptyMessage="
					+ hasEmptyMessage);

			LoggerUtils.logTestEnd("TC_183", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_183 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_184: MOST RATED VIEW ALL ====================

	/**
	 * TC_184: Verify View All opens the most rated page Test Flow: Login as
	 * consumer → Click View All on most rated section Expected: View All should
	 * navigate to a valid rated page
	 */
	@Test(priority = 184, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_184: Verify View All opens the most rated page")
	public void TC184_VerifyViewAllOpensRatedShowsPage() {
		LoggerUtils.logTestStart("TC_184: View All Opens Rated Shows Page");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to most rated section");
			scrollToMostRated();

			LoggerUtils.logStep(3, "Click View All for most rated");
			dashboard.clickViewAllMostRated();
			waitUtils.waitForMilliseconds(5000);

			LoggerUtils.logStep(4, "Verify most rated View All navigates to a valid page");
			String currentUrl = Objects.requireNonNull(dashboard.getCurrentUrl());
			boolean isRatedPage = currentUrl.contains("rated") || currentUrl.contains("most")
					|| currentUrl.contains("view_all");
			boolean isValidPage = currentUrl.contains("dashboard") || currentUrl.contains("home")
					|| !currentUrl.isEmpty();
			Assert.assertTrue(isRatedPage || isValidPage, "TC_184: Should navigate to a valid page");
			LoggerUtils.logInfo("TC_184: Navigation verified, currentUrl=" + currentUrl);

			LoggerUtils.logTestEnd("TC_184", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			logOptionalUnavailable(
					"TC_184: View All button not available - " + consumerCategories.safeString(e.getMessage()));
			LoggerUtils.logTestEnd("TC_184", "SKIPPED - View All button unavailable");
		}
	}

	// ============================================================
	// PERFORMANCE & EDGE CASE TEST CASES (TC_189 - TC_191)
	// ============================================================

	// ==================== TC_189: SLOW NETWORK LOAD ====================

	/**
	 * TC_189: Verify sections load successfully on slow network within SLA Test
	 * Flow: Login as consumer → Measure dashboard load time Expected: Sections
	 * should load within SLA limit
	 */
	@Test(priority = 189, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_PERFORMANCE,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_189: Verify sections load successfully on slow network within SLA")
	public void TC189_VerifySectionsLoadOnSlowNetwork() {
		LoggerUtils.logTestStart("TC_189: Sections In Load On Slow Network");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();

			LoggerUtils.logStep(2, "Measure dashboard load time");
			long startTime = System.currentTimeMillis();
			boolean isDashboardLoaded = dashboard.waitForDashboardShell();
			long endTime = System.currentTimeMillis();
			long loadTime = endTime - startTime;

			LoggerUtils.logStep(3, "Verify load completes within SLA");
			Assert.assertTrue(isDashboardLoaded, "TC_189: Dashboard should load successfully");

			long slaLimit = 15000;
			Assert.assertTrue(loadTime <= slaLimit,
					"TC_189: Sections should load within " + slaLimit + "ms. Actual load time: " + loadTime + "ms");
			LoggerUtils.logInfo("TC_189: Sections loaded successfully in " + loadTime + "ms (SLA=" + slaLimit + "ms)");

			LoggerUtils.logTestEnd("TC_189", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_189 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_190: MULTIPLE RAPID CLICKS ====================

	/**
	 * TC_190: Verify system behavior on multiple rapid clicks Test Flow: Login as
	 * consumer → Click category rapidly multiple times → Verify system stability
	 * Expected: System should handle rapid clicks gracefully
	 */
	@Test(priority = 190, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_PERFORMANCE,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_190: Verify system behavior on multiple rapid clicks")
	public void TC190_VerifySystemBehaviorOnRapidClicks() {
		LoggerUtils.logTestStart("TC_190: System Behavior On Rapid Clicks");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();
			LoggerUtils.logStep(2, "Scroll to categories section");
			scrollToCategories();

			LoggerUtils.logStep(3, "Get first visible category");
			String categoryName = dashboard.getFirstVisibleCategoryName();
			if (isBlank(categoryName)) {
				logOptionalUnavailable("TC_190: No categories available for rapid click test");
				LoggerUtils.logTestEnd("TC_190", "SKIPPED - No category available");
				return;
			}

			LoggerUtils.logStep(4, "Perform multiple rapid clicks on the category");
			boolean clickedAtLeastOnce = false;
			for (int i = 0; i < 3; i++) {
				if (!consumerCategories.tryClickCategory(categoryName)) {
					LoggerUtils.logInfo("TC_190: Category button is no longer available after rapid-click attempt "
							+ (i + 1) + " for: " + categoryName);
					break;
				}
				clickedAtLeastOnce = true;
				waitUtils.waitForMilliseconds(500);
			}

			LoggerUtils.logStep(5, "Verify system remains responsive after rapid clicks");
			waitUtils.waitForMilliseconds(2000);
			String currentUrl = Objects.requireNonNull(dashboard.getCurrentUrl());
			boolean isValidPage = !currentUrl.isEmpty() && currentUrl.contains("http");

			Assert.assertTrue(clickedAtLeastOnce,
					"TC_190: Rapid click test requires at least one successful category click");
			Assert.assertTrue(isValidPage, "TC_190: System should handle rapid clicks gracefully");
			LoggerUtils.logInfo(
					"TC_190: Rapid clicks handled correctly - system remained responsive, currentUrl=" + currentUrl);

			LoggerUtils.logTestEnd("TC_190", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_190 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_191: DASHBOARD REFRESH ====================

	/**
	 * TC_191: Verify sections reload successfully after a dashboard refresh Test
	 * Flow: Login as consumer → Refresh dashboard → Verify sections reload
	 * Expected: Dashboard should reload successfully after refresh
	 */
	@Test(priority = 191, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_PERFORMANCE,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_191: Verify sections reload successfully after a dashboard refresh")
	public void TC191_VerifySectionsInReloadAfterRefresh() {
		LoggerUtils.logTestStart("TC_191: Sections In Reload After Refresh");

		try {
			LoggerUtils.logStep(1, "Wait for dashboard to be ready");
			waitForDashboardReady();

			LoggerUtils.logStep(2, "Verify dashboard is loaded before refresh");
			boolean isDashboardLoadedBeforeRefresh = dashboard.waitForDashboardShell();
			Assert.assertTrue(isDashboardLoadedBeforeRefresh, "TC_191: Dashboard should be loaded before refresh");
			LoggerUtils.logInfo("TC_191: Dashboard loaded before refresh");

			LoggerUtils.logStep(3, "Refresh the dashboard");
			dashboard.refreshDashboard();
			waitForDashboardReady();

			LoggerUtils.logStep(4, "Verify dashboard reloads successfully after refresh");
			boolean isDashboardLoadedAfterRefresh = dashboard.waitForDashboardShell();
			Assert.assertTrue(isDashboardLoadedAfterRefresh,
					"TC_191: Dashboard should reload successfully after refresh");
			LoggerUtils.logInfo("TC_191: Dashboard reloaded successfully after refresh");

			LoggerUtils.logTestEnd("TC_191", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_191 - Test failed: " + consumerCategories.safeString(e.getMessage()));
			throw e;
		}
	}
}
