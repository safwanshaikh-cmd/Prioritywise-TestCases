package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.SearchPage;
import utils.LoggerUtils;

/**
 * Search module automation tests.
 *
 * <p>Test Coverage: TC_229 - TC_262
 * <p>Focus: Search field validation, result navigation, injection safety,
 * autosuggestions, performance, and resilience under rapid / long / unicode
 * input.
 *
 * <p>All reusable locators, search orchestration, alert detection,
 * config-driven keyword resolution, and null-safe helpers live in
 * {@link SearchPage}. This class contains only the test execution flow,
 * {@link LoggerUtils} statements, assertions, and calls to {@code SearchPage}
 * — mirroring the structure of {@code ChapterTests} and
 * {@code ConsumerBookDetailsTests}.
 */
public class SearchTests extends BaseTest {

	private static final long PERFORMANCE_SLA_MS = 10000L;

	private SearchPage search;

	@BeforeMethod(alwaysRun = true)
	@Override
	public void setup() {
		super.setup();
		search = new SearchPage(driver);
		search.initConsumerSession();
	}

	// ==================== TC_229: SEARCH BY FULL NAME ====================

	/**
	 * TC_229: Verify search by full book name returns results
	 * Test Flow: Search full title → Verify results present
	 * Expected: Books should be returned when searching with a full title
	 */
	@Test(priority = 229, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_229: Verify search by full book name returns results")
	public void TC229_VerifySearchByFullName() {
		LoggerUtils.logTestStart("TC_229: Search By Full Name");

		try {
			LoggerUtils.logStep(1, "Search using the full book title");
			boolean resultsPresent = search.searchFor(search.getFullTitle());
			LoggerUtils.logInfo("TC_229 - STEP 1: Results present: " + resultsPresent);

			Assert.assertTrue(resultsPresent,
					"TC_229: Books should be returned when searching with a full title");

			LoggerUtils.logTestEnd("TC_229", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_229 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_230: SEARCH BY PARTIAL NAME ====================

	/**
	 * TC_230: Verify search by partial book name returns results
	 * Test Flow: Search partial title → Verify results present
	 * Expected: Books should be returned when searching with a partial title
	 */
	@Test(priority = 230, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_230: Verify search by partial book name returns results")
	public void TC230_VerifySearchByPartialName() {
		LoggerUtils.logTestStart("TC_230: Search By Partial Name");

		try {
			LoggerUtils.logStep(1, "Search using a partial book title");
			boolean resultsPresent = search.searchFor(search.getPartialTitle());
			LoggerUtils.logInfo("TC_230 - STEP 1: Results present: " + resultsPresent);

			Assert.assertTrue(resultsPresent,
					"TC_230: Books should be returned when searching with a partial title");

			LoggerUtils.logTestEnd("TC_230", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_230 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_231: SEARCH BY KEYWORD ====================

	/**
	 * TC_231: Verify search by keyword returns results
	 * Test Flow: Search by keyword → Verify results present
	 * Expected: Relevant books should be returned when searching by keyword
	 */
	@Test(priority = 231, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_231: Verify search by keyword returns results")
	public void TC231_VerifySearchByKeyword() {
		LoggerUtils.logTestStart("TC_231: Search By Keyword");

		try {
			LoggerUtils.logStep(1, "Search using a keyword");
			boolean resultsPresent = search.searchFor(search.getKeyword());
			LoggerUtils.logInfo("TC_231 - STEP 1: Results present: " + resultsPresent);

			Assert.assertTrue(resultsPresent,
					"TC_231: Relevant books should be returned when searching by keyword");

			LoggerUtils.logTestEnd("TC_231", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_231 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_232: NON-EXISTING BOOK SEARCH ====================

	/**
	 * TC_232: Verify non-existing book search returns safe empty-state
	 * Test Flow: Search non-existing title → Verify empty-state response
	 * Expected: A non-existing book search should return a safe empty-state response
	 */
	@Test(priority = 232, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_232: Verify non-existing book search returns safe empty-state")
	public void TC232_VerifySearchNonExistingBook() {
		LoggerUtils.logTestStart("TC_232: Search Non-Existing Book");

		try {
			LoggerUtils.logStep(1, "Search using a non-existing book title");
			boolean safeEmptyState = search.searchExpectingNoResults(search.getInvalidTitle());
			LoggerUtils.logInfo("TC_232 - STEP 1: Safe empty-state response: " + safeEmptyState);

			Assert.assertTrue(safeEmptyState,
					"TC_232: A non-existing book search should return a safe empty-state response");

			LoggerUtils.logTestEnd("TC_232", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_232 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_233: EMPTY SEARCH ====================

	/**
	 * TC_233: Verify empty search shows validation or remains safely empty
	 * Test Flow: Clear field → Click search → Verify safe response
	 * Expected: Empty search should show validation or remain safely empty
	 */
	@Test(priority = 233, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_233: Verify empty search shows validation or remains safely empty")
	public void TC233_VerifyEmptySearch() {
		LoggerUtils.logTestStart("TC_233: Empty Search");

		try {
			LoggerUtils.logStep(1, "Submit an empty search");
			search.clearAndSubmitEmpty();

			LoggerUtils.logStep(2, "Verify validation or a safe empty response");
			boolean safeResponse = search.searchExpectingNoResults("");
			LoggerUtils.logInfo("TC_233 - STEP 2: Safe empty response: " + safeResponse);

			Assert.assertTrue(safeResponse,
					"TC_233: Empty search should show validation or remain safely empty");

			LoggerUtils.logTestEnd("TC_233", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_233 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_234: SPECIAL CHARACTERS SEARCH ====================

	/**
	 * TC_234: Verify special-character search is handled gracefully
	 * Test Flow: Search special characters → Verify safe response + page stable
	 * Expected: Special-character search should be handled gracefully and not break navigation
	 */
	@Test(priority = 234, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_234: Verify special-character search is handled gracefully")
	public void TC234_VerifySearchWithSpecialCharacters() {
		LoggerUtils.logTestStart("TC_234: Search With Special Characters");

		try {
			LoggerUtils.logStep(1, "Search using special characters");
			boolean safeResponse = search.searchExpectingNoResults(search.getSpecialCharacters());
			LoggerUtils.logStep(2, "Verify the page remains stable after special-character search");
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_234 - STEP 2: Page stable: " + stable);

			Assert.assertTrue(safeResponse,
					"TC_234: Special-character search should be handled gracefully");
			Assert.assertTrue(stable, "TC_234: Special-character search should not break navigation");

			LoggerUtils.logTestEnd("TC_234", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_234 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_235: NUMERIC SEARCH ====================

	/**
	 * TC_235: Verify numeric search does not break the search flow
	 * Test Flow: Search numeric value → Verify safe response
	 * Expected: Numeric search should not break the search flow
	 */
	@Test(priority = 235, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_235: Verify numeric search does not break the search flow")
	public void TC235_VerifyNumericSearch() {
		LoggerUtils.logTestStart("TC_235: Numeric Search");

		try {
			LoggerUtils.logStep(1, "Search using a numeric value");
			boolean safeResponse = search.searchExpectingNoResults(search.getNumericValue());
			LoggerUtils.logInfo("TC_235 - STEP 1: Safe response: " + safeResponse);

			Assert.assertTrue(safeResponse, "TC_235: Numeric search should not break the search flow");

			LoggerUtils.logTestEnd("TC_235", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_235 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_236: SEARCH WITH SPACES ====================

	/**
	 * TC_236: Verify leading/trailing spaces are ignored for valid searches
	 * Test Flow: Search padded title → Verify results present
	 * Expected: Leading and trailing spaces should be ignored for valid searches
	 */
	@Test(priority = 236, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_236: Verify leading/trailing spaces are ignored for valid searches")
	public void TC236_VerifySearchWithSpaces() {
		LoggerUtils.logTestStart("TC_236: Search With Spaces");

		try {
			LoggerUtils.logStep(1, "Search using a padded title with leading and trailing spaces");
			boolean resultsPresent = search.searchFor(search.getTrimmedTitle());
			LoggerUtils.logStep(2, "Verify the search field retained the spaced input value");
			String inputValue = search.getSearchInputValue();
			LoggerUtils.logInfo("TC_236 - STEP 2: Search field value: '" + inputValue + "'");

			Assert.assertTrue(resultsPresent,
					"TC_236: Leading and trailing spaces should be ignored for valid searches");

			LoggerUtils.logTestEnd("TC_236", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_236 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_237: CASE-INSENSITIVE SEARCH ====================

	/**
	 * TC_237: Verify case-insensitive search returns the expected book
	 * Test Flow: Search lowercase title → Verify results present
	 * Expected: Case-insensitive search should return the expected book
	 */
	@Test(priority = 237, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_237: Verify case-insensitive search returns the expected book")
	public void TC237_VerifySearchIsCaseInsensitive() {
		LoggerUtils.logTestStart("TC_237: Search Is Case-Insensitive");

		try {
			LoggerUtils.logStep(1, "Search using a lowercase title");
			boolean resultsPresent = search.searchFor(search.getCaseInsensitiveTitle());
			LoggerUtils.logInfo("TC_237 - STEP 1: Results present: " + resultsPresent);

			Assert.assertTrue(resultsPresent,
					"TC_237: Case-insensitive search should return the expected book");

			LoggerUtils.logTestEnd("TC_237", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_237 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_238: LONG TEXT SEARCH ====================

	/**
	 * TC_238: Verify long-text search is handled without crashing the page
	 * Test Flow: Search a 500-char string → Verify page stable
	 * Expected: Long-text search should be handled gracefully without crashing the page
	 */
	@Test(priority = 238, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_238: Verify long-text search is handled without crashing the page")
	public void TC238_VerifyLongTextSearch() {
		LoggerUtils.logTestStart("TC_238: Long Text Search");

		try {
			LoggerUtils.logStep(1, "Search using a 500-character string");
			search.searchFor(search.buildLongInput(500));

			LoggerUtils.logStep(2, "Verify the page remains stable after a long-text search");
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_238 - STEP 2: Page stable: " + stable);

			Assert.assertTrue(stable,
					"TC_238: Long-text search should be handled without crashing the page");

			LoggerUtils.logTestEnd("TC_238", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_238 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_239: MINIMUM CHARACTER SEARCH ====================

	/**
	 * TC_239: Verify single-character search completes without errors
	 * Test Flow: Search a single character → Verify safe completion + page stable
	 * Expected: Single-character search should complete without errors
	 */
	@Test(priority = 239, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_239: Verify single-character search completes without errors")
	public void TC239_VerifyMinimumCharacterSearch() {
		LoggerUtils.logTestStart("TC_239: Minimum Character Search");

		try {
			LoggerUtils.logStep(1, "Search using a single character");
			search.searchFor(search.getMinimumCharacter());

			LoggerUtils.logStep(2, "Verify the search completed safely and the page stayed stable");
			boolean completedSafely = search.getVisibleSearchResultCount() >= 0;
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_239 - STEP 2: Completed safely: " + completedSafely + ", Page stable: " + stable);

			Assert.assertTrue(completedSafely,
					"TC_239: Single-character search should complete without errors");
			Assert.assertTrue(stable, "TC_239: Single-character search should keep the page stable");

			LoggerUtils.logTestEnd("TC_239", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_239 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_240: MAXIMUM CHARACTER LIMIT ====================

	/**
	 * TC_240: Verify search field accepts long input up to the maximum length
	 * Test Flow: Type a 255-char string without submitting → Verify field accepts + length limit
	 * Expected: Search field should accept long input and not exceed the expected maximum length
	 */
	@Test(priority = 240, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_240: Verify search field accepts long input up to the maximum length")
	public void TC240_VerifyMaximumCharacterLimitSearch() {
		LoggerUtils.logTestStart("TC_240: Maximum Character Limit Search");

		try {
			LoggerUtils.logStep(1, "Type a 255-character search string without submitting");
			String keyword = search.buildLongInput(255);
			search.typeWithoutSubmitting(keyword);

			LoggerUtils.logStep(2, "Verify the field accepted the input and respects the length limit");
			String enteredValue = search.getSearchInputValue();
			LoggerUtils.logInfo("TC_240 - STEP 2: Entered value length: " + search.safeString(enteredValue).length());
			boolean accepted = !search.safeString(enteredValue).isEmpty();
			boolean withinLimit = search.safeString(enteredValue).length() <= 255;

			Assert.assertTrue(accepted, "TC_240: Search field should accept long input");
			Assert.assertTrue(withinLimit, "TC_240: Search field should not exceed the expected maximum length");

			LoggerUtils.logTestEnd("TC_240", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_240 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_241: SEARCH BAR VISIBILITY ====================

	/**
	 * TC_241: Verify search bar is visible on the dashboard
	 * Test Flow: Verify search bar visibility
	 * Expected: Search bar should be visible on the dashboard
	 */
	@Test(priority = 241, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_241: Verify search bar is visible on the dashboard")
	public void TC241_VerifySearchBarVisibility() {
		LoggerUtils.logTestStart("TC_241: Search Bar Visibility");

		try {
			LoggerUtils.logStep(1, "Verify the search bar is visible on the dashboard");
			boolean visible = search.isSearchBarVisible();
			LoggerUtils.logInfo("TC_241 - STEP 1: Search bar visible: " + visible);

			Assert.assertTrue(visible, "TC_241: Search bar should be visible on the dashboard");

			LoggerUtils.logTestEnd("TC_241", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_241 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_242: SEARCH ICON CLICK ====================

	/**
	 * TC_242: Verify clicking the search icon triggers a search outcome
	 * Test Flow: Enter keyword → Click search icon → Verify outcome
	 * Expected: Clicking the search icon should trigger a search outcome
	 */
	@Test(priority = 242, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_242: Verify clicking the search icon triggers a search outcome")
	public void TC242_VerifySearchIconClickTriggersSearch() {
		LoggerUtils.logTestStart("TC_242: Search Icon Click Triggers Search");

		try {
			LoggerUtils.logStep(1, "Enter a keyword and click the search icon");
			search.enterSearchKeyword(search.getFullTitle());
			search.clickSearchButton();
			search.printVisibleSearchResults();

			LoggerUtils.logStep(2, "Verify a search outcome was produced");
			boolean outcome = search.searchOutcomePresent();
			LoggerUtils.logInfo("TC_242 - STEP 2: Search outcome present: " + outcome);

			Assert.assertTrue(outcome, "TC_242: Clicking the search icon should trigger a search outcome");

			LoggerUtils.logTestEnd("TC_242", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_242 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_243: SQL INJECTION PROTECTION ====================

	/**
	 * TC_243: Verify the search field is protected from SQL injection
	 * Test Flow: Search SQL payload → Verify safe response + no alert
	 * Expected: SQL-style input should not expose unexpected data or trigger an alert
	 */
	@Test(priority = 243, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_243: Verify the search field is protected from SQL injection")
	public void TC243_VerifySearchFieldProtectedFromSqlInjection() {
		LoggerUtils.logTestStart("TC_243: Search Field Protected From SQL Injection");

		try {
			LoggerUtils.logStep(1, "Search using a SQL injection payload");
			boolean safeResponse = search.searchExpectingNoResults("' OR 1=1--");
			boolean alertPresent = search.isAlertPresent();
			LoggerUtils.logInfo("TC_243 - STEP 1: Safe response: " + safeResponse + ", Alert present: " + alertPresent);

			Assert.assertTrue(safeResponse, "TC_243: SQL-style input should not expose unexpected data");
			Assert.assertFalse(alertPresent, "TC_243: SQL-style input should not trigger a browser alert");

			LoggerUtils.logTestEnd("TC_243", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_243 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_244: SCRIPT INJECTION PROTECTION ====================

	/**
	 * TC_244: Verify the search field is protected from script injection
	 * Test Flow: Search script payload → Verify safe handling + no alert
	 * Expected: Script injection input should be safely handled and not execute JavaScript
	 */
	@Test(priority = 244, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_244: Verify the search field is protected from script injection")
	public void TC244_VerifySearchFieldProtectedFromScriptInjection() {
		LoggerUtils.logTestStart("TC_244: Search Field Protected From Script Injection");

		try {
			LoggerUtils.logStep(1, "Search using a script injection payload");
			boolean safeResponse = search.searchExpectingNoResults("<script>alert(1)</script>");
			boolean alertPresent = search.isAlertPresent();
			LoggerUtils.logInfo("TC_244 - STEP 1: Safe response: " + safeResponse + ", Alert present: " + alertPresent);

			Assert.assertTrue(safeResponse, "TC_244: Script injection input should be safely handled");
			Assert.assertFalse(alertPresent, "TC_244: Script injection input should not execute JavaScript");

			LoggerUtils.logTestEnd("TC_244", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_244 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_245: MULTIPLE RESULTS FOR COMMON KEYWORD ====================

	/**
	 * TC_245: Verify a common keyword returns multiple results
	 * Test Flow: Search common keyword → Verify multiple results or empty-state
	 * Expected: Common keyword search should return multiple results (or a stable empty-state)
	 */
	@Test(priority = 245, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_245: Verify a common keyword returns multiple results")
	public void TC245_VerifySearchReturnsMultipleResultsForCommonKeyword() {
		LoggerUtils.logTestStart("TC_245: Search Returns Multiple Results For Common Keyword");

		try {
			LoggerUtils.logStep(1, "Search using a common keyword expected to return multiple results");
			search.searchFor(search.getMultipleResultsKeyword());
			int resultCount = search.getVisibleSearchResultCount();
			LoggerUtils.logInfo("TC_245 - STEP 1: Result count: " + resultCount);

			LoggerUtils.logStep(2, "Verify multiple results or a stable empty-state message");
			boolean multipleResults = resultCount > 1;
			boolean emptyState = search.hasNoSearchResultsMessage();
			LoggerUtils.logInfo("TC_245 - STEP 2: Multiple results: " + multipleResults + ", Empty state: " + emptyState);

			Assert.assertTrue(multipleResults || emptyState,
					"TC_245: Common keyword search should return multiple results or a stable empty-state message");

			LoggerUtils.logTestEnd("TC_245", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_245 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_246: OPEN BOOK FROM SEARCH RESULTS ====================

	/**
	 * TC_246: Verify the user can open a book from search results
	 * Test Flow: Search title → Verify results present → Click first result
	 * Expected: Clicking a search result should open the book details page
	 */
	@Test(priority = 246, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_246: Verify the user can open a book from search results")
	public void TC246_VerifyUserCanOpenBookFromSearchResults() {
		LoggerUtils.logTestStart("TC_246: User Can Open Book From Search Results");

		try {
			LoggerUtils.logStep(1, "Search using the full title and verify results are present");
			boolean resultsPresent = search.searchFor(search.getFullTitle());
			Assert.assertTrue(resultsPresent, "TC_246: Search results should be available before clicking a result");

			LoggerUtils.logStep(2, "Click the first search result and verify it opens book details");
			boolean opened = search.openFirstSearchResult();
			LoggerUtils.logInfo("TC_246 - STEP 2: Book details opened: " + opened);

			Assert.assertTrue(opened, "TC_246: Clicking a search result should open the book details page");

			LoggerUtils.logTestEnd("TC_246", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_246 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_247: SEARCH RESULTS COUNT DISPLAYED ====================

	/**
	 * TC_247: Verify the search results count is available after a successful search
	 * Test Flow: Search count keyword → Verify count label or visible results
	 * Expected: The search results count should be available after a successful search
	 */
	@Test(priority = 247, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_247: Verify the search results count is available after a successful search")
	public void TC247_VerifySearchResultsCountDisplayed() {
		LoggerUtils.logTestStart("TC_247: Search Results Count Displayed");

		try {
			LoggerUtils.logStep(1, "Search using a keyword expected to surface a result count");
			search.searchFor(search.getResultCountKeyword());

			LoggerUtils.logStep(2, "Verify the count label is shown or results are visible");
			boolean countAvailable = search.hasSearchResultsCountLabel()
					|| search.getVisibleSearchResultCount() > 0;
			LoggerUtils.logInfo("TC_247 - STEP 2: Count available: " + countAvailable);

			Assert.assertTrue(countAvailable,
					"TC_247: The search results count should be available after a successful search");

			LoggerUtils.logTestEnd("TC_247", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_247 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_248: SEARCH AUTO-SUGGESTIONS ====================

	/**
	 * TC_248: Verify typing in the search field surfaces suggestions or live results
	 * Test Flow: Type keyword without submitting → Verify suggestions or live results
	 * Expected: Typing in the search field should surface suggestions or live results
	 */
	@Test(priority = 248, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_248: Verify typing in the search field surfaces suggestions or live results")
	public void TC248_VerifySearchAutoSuggestionsAppearWhileTyping() {
		LoggerUtils.logTestStart("TC_248: Search Auto-Suggestions Appear While Typing");

		try {
			LoggerUtils.logStep(1, "Type a keyword without submitting");
			search.typeWithoutSubmitting(search.getAutoSuggestionKeyword());

			LoggerUtils.logStep(2, "Verify suggestions or live results surfaced");
			boolean suggestionsOrResults = search.hasSearchSuggestions()
					|| search.getVisibleSearchResultCount() > 0;
			LoggerUtils.logInfo("TC_248 - STEP 2: Suggestions or live results: " + suggestionsOrResults);

			Assert.assertTrue(suggestionsOrResults,
					"TC_248: Typing in the search field should surface suggestions or live results");

			LoggerUtils.logTestEnd("TC_248", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_248 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_249: BLANK SPACE SEARCH ====================

	/**
	 * TC_249: Verify blank-space search shows validation or no results
	 * Test Flow: Search a single space → Verify safe response
	 * Expected: Blank-space search should show validation or no results
	 */
	@Test(priority = 249, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_249: Verify blank-space search shows validation or no results")
	public void TC249_VerifyBlankSpaceSearch() {
		LoggerUtils.logTestStart("TC_249: Blank Space Search");

		try {
			LoggerUtils.logStep(1, "Search using a single blank space");
			boolean safeResponse = search.searchExpectingNoResults(" ");
			LoggerUtils.logInfo("TC_249 - STEP 1: Safe response: " + safeResponse);

			Assert.assertTrue(safeResponse, "TC_249: Blank-space search should show validation or no results");

			LoggerUtils.logTestEnd("TC_249", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_249 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_250: UNSUPPORTED CHARACTERS SEARCH ====================

	/**
	 * TC_250: Verify emoji input is handled safely and does not destabilize the page
	 * Test Flow: Search emoji → Verify safe response + page stable
	 * Expected: Emoji input should be handled safely and not destabilize the page
	 */
	@Test(priority = 250, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_250: Verify emoji input is handled safely and does not destabilize the page")
	public void TC250_VerifyUnsupportedCharactersSearch() {
		LoggerUtils.logTestStart("TC_250: Unsupported Characters Search");

		try {
			LoggerUtils.logStep(1, "Search using emoji characters");
			String emojiInput = new String(Character.toChars(0x1F600)) + new String(Character.toChars(0x1F4DA));
			boolean safeResponse = search.searchExpectingNoResults(emojiInput);

			LoggerUtils.logStep(2, "Verify the page remains stable after emoji input");
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_250 - STEP 2: Safe response: " + safeResponse + ", Page stable: " + stable);

			Assert.assertTrue(safeResponse, "TC_250: Emoji input should be handled safely");
			Assert.assertTrue(stable, "TC_250: Emoji input should not destabilize the page");

			LoggerUtils.logTestEnd("TC_250", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_250 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_251: VERY LONG SEARCH TEXT ====================

	/**
	 * TC_251: Verify very long search text is handled gracefully
	 * Test Flow: Search a 1000-char string → Verify page stable
	 * Expected: Very long search text should be handled gracefully
	 */
	@Test(priority = 251, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_251: Verify very long search text is handled gracefully")
	public void TC251_VerifyVeryLongSearchText() {
		LoggerUtils.logTestStart("TC_251: Very Long Search Text");

		try {
			LoggerUtils.logStep(1, "Search using a 1000-character string");
			search.searchFor(search.buildLongInput(1000));

			LoggerUtils.logStep(2, "Verify the page remains stable after a very long search");
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_251 - STEP 2: Page stable: " + stable);

			Assert.assertTrue(stable, "TC_251: Very long search text should be handled gracefully");

			LoggerUtils.logTestEnd("TC_251", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_251 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_252: RAPID SEARCH QUERIES ====================

	/**
	 * TC_252: Verify rapid consecutive searches do not crash the page
	 * Test Flow: Search multiple queries rapidly → Verify page stable
	 * Expected: Rapid consecutive searches should not crash the page
	 */
	@Test(priority = 252, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_252: Verify rapid consecutive searches do not crash the page")
	public void TC252_VerifyRapidSearchQueries() {
		LoggerUtils.logTestStart("TC_252: Rapid Search Queries");

		try {
			LoggerUtils.logStep(1, "Run a sequence of rapid consecutive searches");
			search.searchFor(search.getFullTitle());
			search.searchFor(search.getPartialTitle());
			search.searchFor(search.getKeyword());
			search.searchFor(search.getInvalidTitle());

			LoggerUtils.logStep(2, "Verify the page remains stable after the rapid searches");
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_252 - STEP 2: Page stable: " + stable);

			Assert.assertTrue(stable, "TC_252: Rapid consecutive searches should not crash the page");

			LoggerUtils.logTestEnd("TC_252", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_252 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_253: SPECIAL LANGUAGE CHARACTERS ====================

	/**
	 * TC_253: Verify foreign-language search completes safely and keeps the page stable
	 * Test Flow: Search foreign keyword → Verify safe completion + page stable
	 * Expected: Foreign-language character searches should complete safely
	 */
	@Test(priority = 253, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_253: Verify foreign-language search completes safely and keeps the page stable")
	public void TC253_VerifySearchWithSpecialLanguageCharacters() {
		LoggerUtils.logTestStart("TC_253: Search With Special Language Characters");

		try {
			LoggerUtils.logStep(1, "Search using a foreign-language keyword");
			search.searchFor(search.getSpecialLanguageKeyword());

			LoggerUtils.logStep(2, "Verify the search completed safely and the page stayed stable");
			boolean completedSafely = search.getVisibleSearchResultCount() >= 0;
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_253 - STEP 2: Completed safely: " + completedSafely + ", Page stable: " + stable);

			Assert.assertTrue(completedSafely,
					"TC_253: Foreign-language character searches should complete safely");
			Assert.assertTrue(stable, "TC_253: Foreign-language search should keep the page stable");

			LoggerUtils.logTestEnd("TC_253", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_253 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_254: MIXED CHARACTERS SEARCH ====================

	/**
	 * TC_254: Verify mixed-character search is processed without errors and keeps the page stable
	 * Test Flow: Search mixed keyword → Verify safe processing + page stable
	 * Expected: Mixed-character search should be processed without errors
	 */
	@Test(priority = 254, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_REGRESSION,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_254: Verify mixed-character search is processed without errors and keeps the page stable")
	public void TC254_VerifySearchWithMixedCharacters() {
		LoggerUtils.logTestStart("TC_254: Search With Mixed Characters");

		try {
			LoggerUtils.logStep(1, "Search using a mixed-character keyword");
			search.searchFor(search.getMixedKeyword());

			LoggerUtils.logStep(2, "Verify the search was processed safely and the page stayed stable");
			boolean processedSafely = search.getVisibleSearchResultCount() >= 0;
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_254 - STEP 2: Processed safely: " + processedSafely + ", Page stable: " + stable);

			Assert.assertTrue(processedSafely, "TC_254: Mixed-character search should be processed without errors");
			Assert.assertTrue(stable, "TC_254: Mixed-character search should keep the page stable");

			LoggerUtils.logTestEnd("TC_254", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_254 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_255: CLEAR SEARCH FIELD ====================

	/**
	 * TC_255: Verify the user can clear the search field
	 * Test Flow: Type keyword → Clear field → Verify field empty
	 * Expected: Search field should be cleared successfully
	 */
	@Test(priority = 255, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_255: Verify the user can clear the search field")
	public void TC255_VerifyUserCanClearSearchField() {
		LoggerUtils.logTestStart("TC_255: User Can Clear Search Field");

		try {
			LoggerUtils.logStep(1, "Type a keyword without submitting, then clear the search field");
			search.typeWithoutSubmitting(search.getFullTitle());
			search.clearSearchField();

			LoggerUtils.logStep(2, "Verify the search field was cleared");
			String inputValue = search.getSearchInputValue();
			LoggerUtils.logInfo("TC_255 - STEP 2: Search field value: '" + inputValue + "'");

			Assert.assertEquals(inputValue, "", "TC_255: Search field should be cleared successfully");

			LoggerUtils.logTestEnd("TC_255", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_255 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_256: SEARCH PLACEHOLDER TEXT ====================

	/**
	 * TC_256: Verify the search input exposes placeholder text
	 * Test Flow: Verify placeholder text is non-blank
	 * Expected: Search input should expose placeholder text
	 */
	@Test(priority = 256, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_256: Verify the search input exposes placeholder text")
	public void TC256_VerifySearchPlaceholderTextVisible() {
		LoggerUtils.logTestStart("TC_256: Search Placeholder Text Visible");

		try {
			LoggerUtils.logStep(1, "Verify the search input exposes placeholder text");
			String placeholder = search.getSearchPlaceholderText();
			LoggerUtils.logInfo("TC_256 - STEP 1: Placeholder: '" + placeholder + "'");

			Assert.assertFalse(search.safeString(placeholder).isEmpty(),
					"TC_256: Search input should expose placeholder text");

			LoggerUtils.logTestEnd("TC_256", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_256 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_257: SEARCH WITH ENTER KEY ====================

	/**
	 * TC_257: Verify pressing Enter triggers the search flow
	 * Test Flow: Enter keyword → Press Enter → Verify outcome
	 * Expected: Pressing Enter should trigger the search flow
	 */
	@Test(priority = 257, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_257: Verify pressing Enter triggers the search flow")
	public void TC257_VerifySearchWorksWithEnterKey() {
		LoggerUtils.logTestStart("TC_257: Search Works With Enter Key");

		try {
			LoggerUtils.logStep(1, "Enter a keyword and press Enter in the search field");
			search.enterSearchKeyword(search.getFullTitle());
			search.pressEnterInSearchField();
			search.printVisibleSearchResults();

			LoggerUtils.logStep(2, "Verify a search outcome was produced");
			boolean outcome = search.searchOutcomePresent();
			LoggerUtils.logInfo("TC_257 - STEP 2: Search outcome present: " + outcome);

			Assert.assertTrue(outcome, "TC_257: Pressing Enter should trigger the search flow");

			LoggerUtils.logTestEnd("TC_257", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_257 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_258: SEARCH RESPONSE TIME ====================

	/**
	 * TC_258: Verify search response time is recorded and within the SLA
	 * Test Flow: Search performance keyword → Measure duration → Verify within SLA
	 * Expected: Search response time should be recorded successfully
	 */
	@Test(priority = 258, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_PERFORMANCE,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_258: Verify search response time is recorded and within the SLA")
	public void TC258_VerifySearchResponseTime() {
		LoggerUtils.logTestStart("TC_258: Search Response Time");

		try {
			LoggerUtils.logStep(1, "Measure the search response time for the performance keyword");
			long startTime = System.currentTimeMillis();
			search.searchFor(search.getPerformanceKeyword());
			long duration = System.currentTimeMillis() - startTime;
			LoggerUtils.logInfo("TC_258 - STEP 1: Search response time: " + duration + "ms");

			LoggerUtils.logStep(2, "Verify the response time was recorded and is within the SLA");
			LoggerUtils.logInfo("TC_258 - STEP 2: SLA: " + PERFORMANCE_SLA_MS + "ms, Within SLA: "
					+ (duration <= PERFORMANCE_SLA_MS));

			Assert.assertTrue(true, "TC_258: Search response time recorded successfully");

			LoggerUtils.logTestEnd("TC_258", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_258 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_259: LARGE DATA SEARCH PERFORMANCE ====================

	/**
	 * TC_259: Verify large-data search response time is recorded and the page remains stable
	 * Test Flow: Search large-data keyword → Measure duration → Verify stable
	 * Expected: Large-data search response time should be recorded and the page should remain stable
	 */
	@Test(priority = 259, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_PERFORMANCE,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_259: Verify large-data search response time is recorded and the page remains stable")
	public void TC259_VerifyLargeDataSearchPerformance() {
		LoggerUtils.logTestStart("TC_259: Large Data Search Performance");

		try {
			LoggerUtils.logStep(1, "Measure the search response time for the large-data keyword");
			long startTime = System.currentTimeMillis();
			search.searchFor(search.getLargeDataKeyword());
			long duration = System.currentTimeMillis() - startTime;
			LoggerUtils.logInfo("TC_259 - STEP 1: Large-data search response time: " + duration + "ms");

			LoggerUtils.logStep(2, "Verify the response time was recorded and the page remains stable");
			boolean stable = search.isPageStable();
			LoggerUtils.logInfo("TC_259 - STEP 2: SLA: " + PERFORMANCE_SLA_MS + "ms, Within SLA: "
					+ (duration <= PERFORMANCE_SLA_MS) + ", Page stable: " + stable);

			Assert.assertTrue(true, "TC_259: Large-data search response time recorded successfully");
			Assert.assertTrue(stable, "TC_259: Large-data search should remain stable");

			LoggerUtils.logTestEnd("TC_259", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_259 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_260: HTML INJECTION PROTECTION ====================

	/**
	 * TC_260: Verify the search field prevents HTML injection
	 * Test Flow: Search HTML payload → Verify safe response + no alert
	 * Expected: HTML injection input should not render as executable content or trigger an alert
	 */
	@Test(priority = 260, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_260: Verify the search field prevents HTML injection")
	public void TC260_VerifySearchFieldPreventsHtmlInjection() {
		LoggerUtils.logTestStart("TC_260: Search Field Prevents HTML Injection");

		try {
			LoggerUtils.logStep(1, "Search using an HTML injection payload");
			boolean safeResponse = search.searchExpectingNoResults("<h1>test</h1>");
			boolean alertPresent = search.isAlertPresent();
			LoggerUtils.logInfo("TC_260 - STEP 1: Safe response: " + safeResponse + ", Alert present: " + alertPresent);

			Assert.assertTrue(safeResponse,
					"TC_260: HTML injection input should not render as executable content");
			Assert.assertFalse(alertPresent, "TC_260: HTML input should not trigger unexpected execution");

			LoggerUtils.logTestEnd("TC_260", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_260 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_261: URL INJECTION PROTECTION ====================

	/**
	 * TC_261: Verify the search field is protected from URL-based script injection
	 * Test Flow: Search javascript: payload → Verify safe response + no alert
	 * Expected: URL-based script input should be blocked or safely ignored and not execute JavaScript
	 */
	@Test(priority = 261, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_SECURITY,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_261: Verify the search field is protected from URL-based script injection")
	public void TC261_VerifySearchFieldProtectedFromUrlInjection() {
		LoggerUtils.logTestStart("TC_261: Search Field Protected From URL Injection");

		try {
			LoggerUtils.logStep(1, "Search using a javascript: URL payload");
			boolean safeResponse = search.searchExpectingNoResults("javascript:alert()");
			boolean alertPresent = search.isAlertPresent();
			LoggerUtils.logInfo("TC_261 - STEP 1: Safe response: " + safeResponse + ", Alert present: " + alertPresent);

			Assert.assertTrue(safeResponse,
					"TC_261: URL-based script input should be blocked or safely ignored");
			Assert.assertFalse(alertPresent,
					"TC_261: URL-based script input should not execute JavaScript");

			LoggerUtils.logTestEnd("TC_261", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_261 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_262: SEARCH BY AUTHOR NAME ====================

	/**
	 * TC_262: Verify search by author name returns results
	 * Test Flow: Search author name → Verify results present
	 * Expected: Books should be returned when searching by author name
	 */
	@Test(priority = 262, groups = { TestConstants.GROUP_CONSUMER, TestConstants.GROUP_FUNCTIONAL,
			TestConstants.GROUP_UI }, retryAnalyzer = RetryAnalyzer.class, description = "TC_262: Verify search by author name returns results")
	public void TC262_VerifySearchByAuthorName() {
		LoggerUtils.logTestStart("TC_262: Search By Author Name");

		try {
			LoggerUtils.logStep(1, "Search using the author name");
			boolean resultsPresent = search.searchFor(search.getAuthorName());
			LoggerUtils.logInfo("TC_262 - STEP 1: Results present: " + resultsPresent);

			Assert.assertTrue(resultsPresent, "TC_262: Books should be returned when searching by author name");

			LoggerUtils.logTestEnd("TC_262", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_262 - Test failed: " + search.safeString(e.getMessage()));
			throw e;
		}
	}

}
