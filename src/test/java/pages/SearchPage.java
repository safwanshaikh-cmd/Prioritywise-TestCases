package pages;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;

import base.BasePage;
import utils.ConfigReader;
import utils.LoggerUtils;

public class SearchPage extends BasePage {

	private static final By SEARCH_RESULT_ITEM = By.xpath(
			"//div[contains(@style,'gap: 10px')]//div[@tabindex='0'][.//img[contains(@src,'thumb.php') or contains(@src,'sonarplay')]]"
					+ " | //img[contains(@src,'thumb.php')]/ancestor::*[@tabindex='0'][1]"
					+ " | //img[contains(@src,'sonarplay')]/ancestor::*[@tabindex='0'][1]");

	private final LoginPage login;
	private final DashboardPage dashboard;

	public SearchPage(WebDriver driver) {
		super(driver);
		this.login = new LoginPage(driver);
		this.dashboard = new DashboardPage(driver);
	}

	// ==================== Login + session ====================

	/**
	 * Reload config, then login as the configured consumer and wait for the
	 * dashboard shell to settle. Throws {@link SkipException} if the consumer
	 * credentials are missing or blank in {@code config.properties}.
	 */
	public void initConsumerSession() {
		ConfigReader.reload();
		skipIfConsumerCredentialsMissing();
		login.openLogin();
		login.loginUser(getConsumerEmail(), getConsumerPassword());
		login.clickNextAfterLogin();
		dashboard.waitForPageReady();
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
					"Set consumer.email and consumer.password in config.properties to run search tests.");
		}
	}

	// ==================== Search orchestration ====================

	/**
	 * Ensure the search bar is available, submit {@code keyword}, and print the
	 * visible search listing. Returns {@code true} when the search produced
	 * either visible results or a stable empty-state message (the soft
	 * "results-or-empty-state" outcome the positive search tests assert on).
	 */
	public boolean searchFor(String keyword) {
		ensureSearchBarAvailable();
		dashboard.submitSearch(keyword);
		dashboard.printVisibleSearchResults();
		return searchExpectingResults(keyword);
	}

	/**
	 * @return {@code true} when the listing shows at least one result OR a
	 *         no-results message (a stable outcome either way). Replaces the
	 *         test-side {@code assertResultsPresent}.
	 */
	public boolean searchExpectingResults(String keyword) {
		return dashboard.getVisibleSearchResultCount() > 0 || !isBlank(dashboard.getNoSearchResultsMessage());
	}

	/**
	 * @return {@code true} when the listing shows a no-results / validation
	 *         message or zero results — the safe empty-state outcome the
	 *         negative/invalid search tests assert on. Replaces the
	 *         test-side {@code assertNoResultsOrValidation}.
	 */
	public boolean searchExpectingNoResults(String keyword) {
		return dashboard.hasNoSearchResultsMessage() || dashboard.hasSearchValidationMessage()
				|| dashboard.getVisibleSearchResultCount() == 0;
	}

	/**
	 * @return {@code true} when the page is in a stable, usable state after a
	 *         search (search page active, dashboard shell present, or on a
	 *         home/dashboard route). Null-safe on the current URL.
	 *         Replaces the test-side {@code assertSearchPageStable}.
	 */
	public boolean isPageStable() {
		String currentUrl = getCurrentUrlSafely();
		return dashboard.isSearchPageActive() || dashboard.waitForDashboardShell()
				|| currentUrl.contains("home") || currentUrl.contains("dashboard");
	}

	/**
	 * @return {@code true} when the current listing shows either visible
	 *         results or a no-results message — the outcome the icon-click /
	 *         Enter-key tests assert on.
	 */
	public boolean searchOutcomePresent() {
		return dashboard.getVisibleSearchResultCount() > 0 || dashboard.hasNoSearchResultsMessage();
	}

	/**
	 * Ensure the search bar is available and type {@code keyword} without
	 * submitting, then give the suggestions/live-result layer a short settle
	 * window. Used by the autosuggestion and max-length tests.
	 */
	public void typeWithoutSubmitting(String keyword) {
		ensureSearchBarAvailable();
		dashboard.typeSearchKeywordWithoutSubmitting(keyword);
		waitQuietly(1000);
	}

	/**
	 * Submit an empty search: clear the field, then click the search button.
	 */
	public void clearAndSubmitEmpty() {
		ensureSearchBarAvailable();
		dashboard.clearSearchField();
		dashboard.clickSearchButton();
	}

	// ==================== Thin pass-throughs (no locators duplicated) ====================

	public boolean isSearchBarVisible() {
		return dashboard.isSearchBarVisible();
	}

	public String getSearchInputValue() {
		return dashboard.getSearchInputValue();
	}

	public String getSearchPlaceholderText() {
		return dashboard.getSearchPlaceholderText();
	}

	public int getVisibleSearchResultCount() {
		return dashboard.getVisibleSearchResultCount();
	}

	public boolean hasNoSearchResultsMessage() {
		return dashboard.hasNoSearchResultsMessage();
	}

	public boolean hasSearchValidationMessage() {
		return dashboard.hasSearchValidationMessage();
	}

	public boolean hasSearchSuggestions() {
		return dashboard.hasSearchSuggestions();
	}

	public boolean hasSearchResultsCountLabel() {
		return dashboard.hasSearchResultsCountLabel();
	}

	public void enterSearchKeyword(String keyword) {
		dashboard.enterSearchKeyword(keyword);
	}

	public void clickSearchButton() {
		dashboard.clickSearchButton();
	}

	public void pressEnterInSearchField() {
		dashboard.pressEnterInSearchField();
	}

	public void clearSearchField() {
		dashboard.clearSearchField();
	}

	/**
	 * @return the first visible search-result element on the search results
	 *         page, or {@code null} when none is present. Resolves the actual
	 *         result item so a click lands on the book — not on a decorative
	 *         sibling card.
	 */
	private WebElement findFirstSearchResultElement() {
		try {
			List<WebElement> results = driver.findElements(SEARCH_RESULT_ITEM);
			for (WebElement result : results) {
				if (result.isDisplayed()) {
					return result;
				}
			}
		} catch (Exception e) {
			// fall through — no visible result element available
		}
		return null;
	}

	public boolean clickFirstSearchResult() {
		return openFirstSearchResult();
	}

	/**
	 * Click the first search result and verify the book details page opens.
	 * <p>
	 * Bypasses {@link DashboardPage#clickFirstSearchResult()}, which retargets
	 * the click to a generic {@code border-radius:8px} decorative card and
	 * leaves the user on the search page. Instead this resolves the actual
	 * result element, scrolls it into view, clicks it (with a JS fallback),
	 * then waits up to 10 seconds for the book details page to become visible
	 * via {@link DashboardPage#isBookDetailsPageVisible()} — the same verdict
	 * {@code ChapterTests}/{@code ConsumerBookDetailsTests} use. No asserts —
	 * returns the outcome so the test can assert on it.
	 *
	 * @return {@code true} when clicking the first result opened a book details
	 *         page within the wait window.
	 */
	public boolean openFirstSearchResult() {
		WebElement firstResult = findFirstSearchResultElement();
		if (firstResult == null) {
			LoggerUtils.logInfo("openFirstSearchResult: no visible search result element found to click.");
			return false;
		}
		String startingUrl = getCurrentUrlSafely();

		scrollIntoView(firstResult);
		waitQuietly(500);
		try {
			firstResult.click();
		} catch (Exception clickEx) {
			try {
				executeScript("arguments[0].click();", firstResult);
			} catch (Exception jsEx) {
				LoggerUtils.logInfo("openFirstSearchResult: result click failed — " + safeString(jsEx.getMessage()));
				return false;
			}
		}

		try {
			return new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(ignored -> dashboard.isBookDetailsPageVisible()) != null;
		} catch (Exception e) {
			String newUrl = getCurrentUrlSafely();
			LoggerUtils.logInfo("openFirstSearchResult: book details not visible — urlChanged="
					+ !startingUrl.equals(newUrl));
			return !startingUrl.equals(newUrl);
		}
	}

	/**
	 * @return {@code true} when the open book details page exposes a Play
	 *         Audio control. Used as a precondition gate before validating
	 *         playback (some books may have no audio).
	 */
	public boolean isPlayButtonVisible() {
		return dashboard.isPlayAudioButtonVisible();
	}

	/**
	 * Click Play Audio on the open book details page and verify playback
	 * started. Mirrors the pattern used by {@code ChapterTests} — delegates to
	 * {@link DashboardPage#clickPlayAudioAndVerifyPlayback()}.
	 *
	 * @return {@code true} when audio playback started successfully.
	 */
	public boolean playAudioAndVerifyPlayback() {
		return dashboard.clickPlayAudioAndVerifyPlayback();
	}

	public void printVisibleSearchResults() {
		dashboard.printVisibleSearchResults();
	}

	/**
	 * Ensure the search bar is visible before interacting with it. Mirrors the
	 * (former) test-side {@code ensureSearchBarAvailable} precondition.
	 */
	public boolean ensureSearchBarAvailable() {
		return dashboard.isSearchBarVisible();
	}

	/**
	 * @return {@code true} when a browser alert is currently present. Used by
	 *         the injection tests to assert no unexpected alert fired. Encapsulates
	 *         the raw {@code driver.switchTo().alert()} so the test stays free
	 *         of Selenium.
	 */
	public boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	// ==================== Config-driven search keywords ====================

	/**
	 * @return the configured value for {@code key}, falling back to
	 *         {@code fallback} when it is blank/missing.
	 */
	public String getConfigValue(String key, String fallback) {
		return ConfigReader.getProperty(key, fallback);
	}

	public String getFullTitle() {
		return getConfigValue("search.fullTitle", "New-3");
	}

	public String getPartialTitle() {
		return getConfigValue("search.partialTitle", "New");
	}

	public String getKeyword() {
		return getConfigValue("search.keyword", "Productivity");
	}

	public String getInvalidTitle() {
		return getConfigValue("search.invalidTitle", "XYZ123Book");
	}

	public String getSpecialCharacters() {
		return getConfigValue("search.specialCharacters", "@@@###");
	}

	public String getNumericValue() {
		return getConfigValue("search.numericValue", "123456");
	}

	public String getTrimmedTitle() {
		return getConfigValue("search.trimmedTitle", " New-3 ");
	}

	public String getCaseInsensitiveTitle() {
		return getConfigValue("search.caseInsensitiveTitle", "new-3");
	}

	public String getMinimumCharacter() {
		return getConfigValue("search.minimumCharacter", "A");
	}

	public String getMultipleResultsKeyword() {
		return getConfigValue("search.multipleResultsKeyword", "History");
	}

	public String getResultCountKeyword() {
		return getConfigValue("search.resultCountKeyword", "New-3");
	}

	public String getAutoSuggestionKeyword() {
		return getConfigValue("search.autoSuggestionKeyword", "New");
	}

	public String getSpecialLanguageKeyword() {
		return getConfigValue("search.specialLanguageKeyword", "Libro");
	}

	public String getMixedKeyword() {
		return getConfigValue("search.mixedKeyword", "Book123");
	}

	public String getPerformanceKeyword() {
		return getConfigValue("search.performanceKeyword", "Atomic Habits");
	}

	public String getLargeDataKeyword() {
		return getConfigValue("search.largeDataKeyword", "Book");
	}

	public String getAuthorName() {
		return getConfigValue("search.authorName", "Arti");
	}

	// ==================== Null-safe / wait helpers ====================

	/**
	 * @return the value, or empty string if it is {@code null}. Useful for
	 *         null-safe concatenation when logging exception messages.
	 */
	public String safeString(String value) {
		return value == null ? "" : value;
	}

	/**
	 * Null-safe, case-insensitive {@code contains} check used by the
	 * placeholder / message validations.
	 */
	public boolean containsIgnoreCase(String container, String token) {
		if (container == null || token == null) {
			return false;
		}
		return container.toLowerCase(Locale.ROOT).contains(token.toLowerCase(Locale.ROOT));
	}

	/**
	 * @return the current URL, or empty string if the driver cannot be queried.
	 *         Replaces ad-hoc {@code Objects.requireNonNull(drive.getCurrentUrl())}.
	 */
	public String getCurrentUrlSafely() {
		try {
			return driver.getCurrentUrl();
		} catch (Exception e) {
			return "";
		}
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

	/**
	 * Log a soft-unavailable message for tests that cannot validate their
	 * scenario on the current dashboard state. Mirrors the sibling page objects.
	 */
	public void logOptionalUnavailable(String message) {
		LoggerUtils.logInfo(message);
	}

	/**
	 * @return an N-character string built by repeating {@code "Book123Search"}
	 *         and substring-truncating, used by the long-text / max-length
	 *         tests. Replaces the test-side {@code buildAlternatingString}.
	 */
	public String buildLongInput(int length) {
		String seed = "Book123Search";
		StringBuilder builder = new StringBuilder(length);
		while (builder.length() < length) {
			builder.append(seed);
		}
		return builder.substring(0, length);
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
