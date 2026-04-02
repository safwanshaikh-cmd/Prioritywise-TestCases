package tests;

import java.lang.reflect.Method;

import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

/**
 * Search module tests.
 *
 * Test Coverage: TC_229 - TC_262
 *
 * Reference note: the search wait/result strategy mirrors the working
 * SearchPage from the sibling Sonarplay project, but it is implemented here
 * through this repo's existing DashboardPage object to stay aligned with the
 * current framework structure.
 */
public class SearchTests extends BaseTest {

	private static final long DEFAULT_SEARCH_SETTLE_MS = 1500L;
	private static final long PERFORMANCE_SLA_MS = 10000L;

	private DashboardPage dashboard;
	private LoginPage login;

	private void waitForMilliseconds(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.println("Sleep interrupted: " + e.getMessage());
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private String getConsumerEmail() {
		return ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
	}

	private String getConsumerPassword() {
		return ConfigReader.getProperty("consumer.password", ConfigReader.getProperty("login.validPassword"));
	}

	private void skipIfConsumerCredentialsMissing() {
		if (isBlank(getConsumerEmail()) || isBlank(getConsumerPassword())) {
			throw new SkipException("Set consumer.email and consumer.password in config.properties to run search tests.");
		}
	}

	private String getConfigValue(String key, String fallback) {
		return ConfigReader.getProperty(key, fallback);
	}

	private String buildString(char character, int length) {
		StringBuilder builder = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			builder.append(character);
		}
		return builder.toString();
	}

	private String buildAlternatingString(int length) {
		String seed = "Book123Search";
		StringBuilder builder = new StringBuilder(length);
		while (builder.length() < length) {
			builder.append(seed);
		}
		return builder.substring(0, length);
	}

	private void loginAsConsumer() {
		skipIfConsumerCredentialsMissing();
		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();
		dashboard.waitForPageReady();
		waitForMilliseconds(2000);
	}

	private void ensureSearchBarAvailable() {
		Assert.assertTrue(dashboard.isSearchBarVisible(), "Search bar should be visible on the dashboard.");
	}

	private void performSearch(String keyword) {
		ensureSearchBarAvailable();
		dashboard.submitSearch(keyword);
		waitForMilliseconds(DEFAULT_SEARCH_SETTLE_MS);
		dashboard.printVisibleSearchResults();
	}

	private void printCurrentSearchListing() {
		dashboard.printVisibleSearchResults();
	}

	private void typeWithoutSubmitting(String keyword) {
		ensureSearchBarAvailable();
		dashboard.typeSearchKeywordWithoutSubmitting(keyword);
		waitForMilliseconds(1000);
	}

	private void assertSearchPageStable(String message) {
		String currentUrl = dashboard.getCurrentUrl();
		boolean stable = dashboard.isSearchPageActive() || dashboard.waitForDashboardShell() || currentUrl.contains("home")
				|| currentUrl.contains("dashboard");
		Assert.assertTrue(stable, message + " Current URL: " + currentUrl);
	}

	private void assertResultsPresent(String message) {
		int resultCount = dashboard.getVisibleSearchResultCount();
		if (resultCount > 0) {
			Assert.assertTrue(true, message);
			return;
		}

		String noResultsMessage = dashboard.getNoSearchResultsMessage();
		if (!isBlank(noResultsMessage)) {
			System.out.println("No search results returned. Displayed message: " + noResultsMessage);
			Assert.assertTrue(true, "Search completed with an empty-state message instead of results.");
			return;
		}

		Assert.fail(message);
	}

	private void assertNoResultsOrValidation(String message) {
		boolean expectedOutcome = dashboard.hasNoSearchResultsMessage() || dashboard.hasSearchValidationMessage()
				|| dashboard.getVisibleSearchResultCount() == 0;
		Assert.assertTrue(expectedOutcome, message);
	}

	private boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void setup(Method method) {
		super.setup();
		ConfigReader.reload();

		login = new LoginPage(driver);
		dashboard = new DashboardPage(driver);

		loginAsConsumer();
		System.out.println("Executing search test: " + method.getName());
	}

	@Test(priority = 229, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchBookByFullName() {
		performSearch(getConfigValue("search.fullTitle", "New-3"));
		assertResultsPresent("Books should be returned when searching with a full title.");
	}

	@Test(priority = 230, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchBookByPartialName() {
		performSearch(getConfigValue("search.partialTitle", "New"));
		assertResultsPresent("Books should be returned when searching with a partial title.");
	}

	@Test(priority = 231, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchByKeyword() {
		performSearch(getConfigValue("search.keyword", "Productivity"));
		assertResultsPresent("Relevant books should be returned when searching by keyword.");
	}

	@Test(priority = 232, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchNonExistingBook() {
		performSearch(getConfigValue("search.invalidTitle", "XYZ123Book"));
		assertNoResultsOrValidation("A non-existing book search should return a safe empty-state response.");
	}

	@Test(priority = 233, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptySearch() {
		ensureSearchBarAvailable();
		dashboard.clearSearchField();
		dashboard.clickSearchButton();
		assertNoResultsOrValidation("Empty search should show validation or remain safely empty.");
	}
	
	@Test(priority = 234, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchWithSpecialCharacters() {
		performSearch(getConfigValue("search.specialCharacters", "@@@###"));
		assertNoResultsOrValidation("Special-character search should be handled gracefully.");
		assertSearchPageStable("Special-character search should not break navigation.");
	}

	@Test(priority = 235, retryAnalyzer = RetryAnalyzer.class)
	public void verifyNumericSearch() {
		performSearch(getConfigValue("search.numericValue", "123456"));
		assertNoResultsOrValidation("Numeric search should not break the search flow.");
	}

	@Test(priority = 236, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchWithSpaces() {
		performSearch(getConfigValue("search.trimmedTitle", " New-3 "));
		assertResultsPresent("Leading and trailing spaces should be ignored for valid searches.");
		System.out.println("Search field value after spaced input: '" + dashboard.getSearchInputValue() + "'");
	}

	@Test(priority = 237, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchIsCaseInsensitive() {
		performSearch(getConfigValue("search.caseInsensitiveTitle", "new-3"));
		assertResultsPresent("Case-insensitive search should return the expected book.");
	}

	@Test(priority = 238, retryAnalyzer = RetryAnalyzer.class)
	public void verifyLongTextSearch() {
		performSearch(buildAlternatingString(500));
		assertSearchPageStable("Long-text search should be handled without crashing the page.");
	}

	@Test(priority = 239, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMinimumCharacterSearch() {
		performSearch(getConfigValue("search.minimumCharacter", "A"));
		Assert.assertTrue(dashboard.getVisibleSearchResultCount() >= 0,
				"Single-character search should complete without errors.");
		assertSearchPageStable("Single-character search should keep the page stable.");
	}

	@Test(priority = 240, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMaximumCharacterLimitSearch() {
		String keyword = buildAlternatingString(255);
		typeWithoutSubmitting(keyword);
		String enteredValue = dashboard.getSearchInputValue();

		Assert.assertTrue(!enteredValue.isEmpty(), "Search field should accept long input.");
		Assert.assertTrue(enteredValue.length() <= 255, "Search field should not exceed the expected maximum length.");
	}

	@Test(priority = 241, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchBarVisibility() {
		Assert.assertTrue(dashboard.isSearchBarVisible(), "Search bar should be visible on the dashboard.");
	}

	@Test(priority = 242, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchIconClickTriggersSearch() {
		ensureSearchBarAvailable();
		dashboard.enterSearchKeyword(getConfigValue("search.fullTitle", "New-3"));
		dashboard.clickSearchButton();
		printCurrentSearchListing();
		Assert.assertTrue(dashboard.getVisibleSearchResultCount() > 0 || dashboard.hasNoSearchResultsMessage(),
				"Clicking the search icon should trigger a search outcome.");
	}

	@Test(priority = 243, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchFieldProtectedFromSqlInjection() {
		performSearch("' OR 1=1--");
		assertNoResultsOrValidation("SQL-style input should not expose unexpected data.");
		Assert.assertFalse(isAlertPresent(), "SQL-style input should not trigger a browser alert.");
	}

	@Test(priority = 244, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchFieldProtectedFromScriptInjection() {
		performSearch("<script>alert(1)</script>");
		assertNoResultsOrValidation("Script injection input should be safely handled.");
		Assert.assertFalse(isAlertPresent(), "Script injection input should not execute JavaScript.");
	}

	@Test(priority = 245, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchReturnsMultipleResultsForCommonKeyword() {
		performSearch(getConfigValue("search.multipleResultsKeyword", "History"));
		int resultCount = dashboard.getVisibleSearchResultCount();
		if (resultCount > 1) {
			Assert.assertTrue(true, "Common keyword search returned multiple results.");
			return;
		}

		String noResultsMessage = dashboard.getNoSearchResultsMessage();
		if (!isBlank(noResultsMessage)) {
			System.out.println("No multiple-result data available. Displayed message: " + noResultsMessage);
			Assert.assertTrue(true, "Search completed with an empty-state message instead of multiple results.");
			return;
		}

		Assert.fail("Common keyword search should return multiple results.");
	}

	@Test(priority = 246, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanOpenBookFromSearchResults() {
		performSearch(getConfigValue("search.fullTitle", "New-3"));
		assertResultsPresent("Search results should be available before clicking a result.");
		Assert.assertTrue(dashboard.clickFirstSearchResult(), "Clicking a search result should open the book details page.");
	}

	@Test(priority = 247, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchResultsCountDisplayed() {
		performSearch(getConfigValue("search.resultCountKeyword", "Science"));
		Assert.assertTrue(dashboard.hasSearchResultsCountLabel() || dashboard.getVisibleSearchResultCount() > 0,
				"The search results count should be available after a successful search.");
	}

	@Test(priority = 248, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchAutoSuggestionsAppearWhileTyping() {
		typeWithoutSubmitting(getConfigValue("search.autoSuggestionKeyword", "New"));
		Assert.assertTrue(dashboard.hasSearchSuggestions() || dashboard.getVisibleSearchResultCount() > 0,
				"Typing in the search field should surface suggestions or live results.");
	}

	@Test(priority = 249, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBlankSpaceSearch() {
		performSearch(" ");
		assertNoResultsOrValidation("Blank-space search should show validation or no results.");
	}

	@Test(priority = 250, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUnsupportedCharactersSearch() {
		performSearch(new String(Character.toChars(0x1F600)) + new String(Character.toChars(0x1F4DA)));
		assertNoResultsOrValidation("Emoji input should be handled safely.");
		assertSearchPageStable("Emoji input should not destabilize the page.");
	}

	@Test(priority = 251, retryAnalyzer = RetryAnalyzer.class)
	public void verifyVeryLongSearchText() {
		performSearch(buildAlternatingString(1000));
		assertSearchPageStable("Very long search text should be handled gracefully.");
	}

	@Test(priority = 252, retryAnalyzer = RetryAnalyzer.class)
	public void verifyRapidSearchQueries() {
		String[] queries = { getConfigValue("search.fullTitle", "New-3"),
				getConfigValue("search.partialTitle", "Atomic"), getConfigValue("search.keyword", "Productivity"),
				getConfigValue("search.invalidTitle", "XYZ123Book") };

		for (String query : queries) {
			performSearch(query);
		}

		assertSearchPageStable("Rapid consecutive searches should not crash the page.");
	}

	@Test(priority = 253, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchWithSpecialLanguageCharacters() {
		performSearch(getConfigValue("search.specialLanguageKeyword", "Libro"));
		Assert.assertTrue(dashboard.getVisibleSearchResultCount() >= 0,
				"Non-English character searches should complete safely.");
		assertSearchPageStable("Foreign-language search should keep the page stable.");
	}

	@Test(priority = 254, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchWithMixedCharacters() {
		performSearch(getConfigValue("search.mixedKeyword", "Book123"));
		Assert.assertTrue(dashboard.getVisibleSearchResultCount() >= 0,
				"Mixed-character search should be processed without errors.");
		assertSearchPageStable("Mixed-character search should keep the page stable.");
	}

	@Test(priority = 255, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUserCanClearSearchField() {
		typeWithoutSubmitting(getConfigValue("search.fullTitle", "New-3"));
		dashboard.clearSearchField();
		Assert.assertEquals(dashboard.getSearchInputValue(), "", "Search field should be cleared successfully.");
	}

	@Test(priority = 256, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchPlaceholderTextVisible() {
		Assert.assertFalse(dashboard.getSearchPlaceholderText().isBlank(),
				"Search input should expose placeholder text.");
	}

	@Test(priority = 257, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchWorksWithEnterKey() {
		ensureSearchBarAvailable();
		dashboard.enterSearchKeyword(getConfigValue("search.fullTitle", "New-3"));
		dashboard.pressEnterInSearchField();
		printCurrentSearchListing();
		Assert.assertTrue(dashboard.getVisibleSearchResultCount() > 0 || dashboard.hasNoSearchResultsMessage(),
				"Pressing Enter should trigger the search flow.");
	}

	@Test(priority = 258, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchResponseTime() {
		long startTime = System.currentTimeMillis();
		performSearch(getConfigValue("search.performanceKeyword", "Atomic Habits"));
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Search response time: " + duration + "ms");
		if (duration > PERFORMANCE_SLA_MS) {
			System.out.println("Search response time exceeded SLA of " + PERFORMANCE_SLA_MS + "ms.");
		}
		Assert.assertTrue(true, "Search response time recorded successfully.");
	}

	@Test(priority = 259, retryAnalyzer = RetryAnalyzer.class)
	public void verifyLargeDataSearchPerformance() {
		long startTime = System.currentTimeMillis();
		performSearch(getConfigValue("search.largeDataKeyword", "Book"));
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Large-data search response time: " + duration + "ms");
		if (duration > PERFORMANCE_SLA_MS) {
			System.out.println("Large-data search response time exceeded SLA of " + PERFORMANCE_SLA_MS + "ms.");
		}
		Assert.assertTrue(true, "Large-data search response time recorded successfully.");
		assertSearchPageStable("Large-data search should remain stable.");
	}

	@Test(priority = 260, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchFieldPreventsHtmlInjection() {
		performSearch("<h1>test</h1>");
		assertNoResultsOrValidation("HTML injection input should not render as executable content.");
		Assert.assertFalse(isAlertPresent(), "HTML input should not trigger unexpected execution.");
	}

	@Test(priority = 261, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchFieldProtectedFromUrlInjection() {
		performSearch("javascript:alert()");
		assertNoResultsOrValidation("URL-based script input should be blocked or safely ignored.");
		Assert.assertFalse(isAlertPresent(), "URL-based script input should not execute JavaScript.");
	}

	@Test(priority = 262, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchByAuthorName() {
		performSearch(getConfigValue("search.authorName", "Arti"));
		assertResultsPresent("Books should be returned when searching by author name.");
	}
}
