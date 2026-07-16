package pages;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Page object for the Transactions / Transaction History screen.
 *
 * <p>Owns:
 * <ul>
 *   <li>All transaction-list and transaction-detail locators</li>
 *   <li>Search, filter, and clear-search interactions</li>
 *   <li>Invoice-download interactions</li>
 *   <li>Per-card field extractors (plan, date, time, amount, status, payment
 *       method, duration)</li>
 *   <li>Validation helpers shared by {@code TransactionsTests}</li>
 *   <li>Login + open-page orchestration that takes the test from a logged-out
 *       state to a populated transactions screen</li>
 * </ul>
 *
 * <p>Follows the same conventions used in
 * {@code ChapterPage} / {@code ConsumerBookDetailsPage}: no
 * {@code By} field is declared inline in tests, helpers return
 * {@code boolean} / {@code String} / {@code int} / {@code List<String>}, and
 * assertions live in the test class.
 */
public class TransactionsPage extends BasePage {

	private final WebDriverWait pageWait;
	private final LoginPage login;
	private final DashboardPage dashboard;

	// =============== Locators ===============

	private static final By TRANSACTIONS_SCREEN = By.cssSelector("[data-testid='screen_transactions']");
	private static final By TRANSACTIONS_HEADER = By
			.xpath("//div[@data-testid='text_transaction_history' and normalize-space()='Transaction History']"
					+ " | //div[@data-testid='text_title' and normalize-space()='Transactions']"
					+ " | //*[@data-testid='text_transaction_history']"
					+ " | //*[contains(normalize-space(),'Transaction History')]"
					+ " | //*[contains(normalize-space(),'Transactions')]");
	private static final By TRANSACTION_CARD = By.cssSelector("[data-testid='container_transaction_item']");
	private static final By SUBSCRIPTION_TYPE = By.xpath(
			"//*[@data-testid='container_transaction_item']//*[normalize-space()='Gold' or normalize-space()='Silver' or normalize-space()='Bronze' or contains(normalize-space(),'Month') or contains(normalize-space(),'payment')]");
	private static final By DOWNLOAD_INVOICE = By
			.cssSelector("[data-testid='button_download_invoice'], [data-testid='text_download_invoice']");
	private static final By FILTER_OVERLAY = By.cssSelector("[data-testid='container_filter_overlay']");

	private static final By FILTER_BUTTON = By
			.xpath("//*[@data-testid='button_open_filter' or @data-testid='text_filter_button' or normalize-space()='Filter By']");
	private static final By FILTER_POPUP = By
			.xpath("//*[@data-testid='container_filter_overlay' or normalize-space()='Apply now' or normalize-space()='Successful' or normalize-space()='Cancelled' or normalize-space()='Refunded']");
	private static final By SEARCH_INPUT = By
			.xpath("//input[@data-testid='input_search' or @placeholder='Transaction ID' or @type='search' or @placeholder='search' or contains(@placeholder, 'Search')]");
	private static final By NO_DATA_FOUND = By
			.xpath("//*[contains(text(), 'No data found') or contains(text(), 'No results') or contains(text(), 'No transactions')]");
	private static final By SEARCH_SUBMIT_BUTTON = By
			.xpath("//button[@type='submit' or contains(@class, 'search')]");

	// Status / pattern constants kept here so the page object — not the test —
	// owns the source of truth for what counts as a "valid" transaction value.
	private static final String[] EXPECTED_STATUSES = {
			"Completed", "Cancelled", "Refunded", "Pending", "Processing"
	};
	private static final Pattern AMOUNT_HAS_CURRENCY_OR_DIGIT = Pattern.compile(".*[₹Rs$0-9].*");
	private static final Pattern AMOUNT_HAS_DIGIT = Pattern.compile(".*\\d+.*");
	private static final Pattern DATE_FORMAT_PRIMARY = Pattern.compile(".*\\d{4}-\\d{2}-\\d{2}.*");
	private static final Pattern DATE_FORMAT_FALLBACK = Pattern.compile(".*\\d{2}/\\d{2}/\\d{4}.*");
	private static final Pattern TIME_FORMAT = Pattern.compile("\\d{1,2}:\\d{2}:\\d{2}");
	private static final Pattern DURATION_HAS_PERIOD = Pattern.compile(".*(month|day|week|year).*",
			Pattern.CASE_INSENSITIVE);

	// =============== Constructor ===============

	public TransactionsPage(WebDriver driver) {
		super(driver);
		this.pageWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
		this.login = new LoginPage(driver);
		this.dashboard = new DashboardPage(driver);
	}

	// =============== Null-safe helpers (mirrors ChapterPage / ConsumerBookDetailsPage) ===============

	/** Returns {@code ""} for null inputs, trimming whitespace. */
	public String safeString(String value) {
		return value == null ? "" : value;
	}

	/** Trims, lower-cases, and returns the URL fragment or safe value. */
	public String safeLowerUrl(String value) {
		return safeString(value).toLowerCase(Locale.ROOT);
	}

	/**
	 * Returns the current page URL in lower-case, or {@code ""} on failure.
	 * Mirrors {@code ConsumerBookDetailsPage.getCurrentUrlSafely}.
	 */
	public String getCurrentUrlSafely() {
		try {
			String url = driver.getCurrentUrl();
			return url == null ? "" : url.toLowerCase(Locale.ROOT);
		} catch (Exception e) {
			return "";
		}
	}

	/** Sleep that rethrows {@link InterruptedException} as a runtime exception. */
	public void waitQuietly(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting " + millis + "ms", e);
		}
	}

	// =============== Login + open-page orchestration ===============

	/**
	 * Log in as a registered (consumer) user. Credentials come from
	 * {@code config.properties} via {@link ConfigReader}, with fallbacks to the
	 * generic login keys. No plaintext credentials are stored in source.
	 */
	public void loginAsRegisteredUser() {
		String email = ConfigReader.getProperty("consumer.email",
				ConfigReader.getProperty("login.validEmail"));
		String password = ConfigReader.getProperty("consumer.password",
				ConfigReader.getProperty("login.validPassword"));

		login.openLogin();
		login.loginUser(email, password);
		LoggerUtils.logInfo("Logged in as registered user");
	}

	/**
	 * Walk from the post-login dashboard to a populated Transactions page. The
	 * journey is asserted inside the page object (mirroring
	 * {@code ChapterPage.loginAsUploader}) so callers can treat the transactions
	 * screen as ready-to-use.
	 */
	public void openTransactionsPage() {
		if (!dashboard.waitForDashboardShell()) {
			throw new IllegalStateException(
					"Transactions page: Dashboard should be stable after login before opening Transactions");
		}

		boolean sideMenuOpened = dashboard.openSimpleSideMenu() || dashboard.openSideMenu();
		if (!sideMenuOpened) {
			throw new IllegalStateException("Transactions page: Side menu should open");
		}

		if (!dashboard.waitForSideMenuItemsLoaded()) {
			throw new IllegalStateException("Transactions page: Side menu items should load");
		}

		navigateToTransactions();
		waitForTransactionsPageToLoad();

		if (!isTransactionsPageDisplayed() && !hasTransactions()) {
			throw new IllegalStateException(
					"Transactions page: Transaction History should be reachable after clicking the side menu item");
		}

		if (dashboard.isSideMenuOpen()) {
			boolean closed = dashboard.closeSimpleSideMenu();
			if (!closed && dashboard.isSideMenuOpen()) {
				LoggerUtils.logInfo(
						"Side menu remained open after navigation; continuing because Transaction History was reached");
			}
		}
	}

	// =============== Navigation / page-load ===============

	/**
	 * Click the Transaction History item in the side menu.
	 */
	public void navigateToTransactions() {
		try {
			if (dashboard.isSimpleSideMenuOpen()) {
				dashboard.clickSimpleSideMenuItemAndCaptureUrl("transaction", "transactions",
						"transaction history", "payment history");
			} else {
				dashboard.clickSideMenuItemAndCaptureUrl("transaction", "transactions",
						"transaction history", "payment history");
			}
			pageWait.until(currentDriver -> isTransactionsPageDisplayed());
			LoggerUtils.logInfo("Navigated to Transactions page");
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to navigate to Transactions page: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Check if Transactions page is displayed (URL contains "transaction", or
	 * the page header is rendered, or the download-invoice control is present).
	 */
	public boolean isTransactionsPageDisplayed() {
		try {
			String safeUrl = getCurrentUrlSafely();
			return isAnyLocatorVisible(TRANSACTIONS_SCREEN, TRANSACTIONS_HEADER)
					|| safeUrl.contains("transaction")
					|| isAnyLocatorVisible(TRANSACTIONS_HEADER, DOWNLOAD_INVOICE);
		} catch (Exception e) {
			return false;
		}
	}

	public void waitForTransactionsPageToLoad() {
		try {
			pageWait.until(currentDriver -> isTransactionsPageDisplayed() || hasTransactions());
		} catch (Exception e) {
			LoggerUtils.logInfo("Transactions page did not finish loading cleanly: " + safeString(e.getMessage()));
		}
	}

	// =============== List / count ===============

	/**
	 * Get all transaction cards in the DOM. Returns an empty list on failure
	 * or when the screen has not yet hydrated.
	 */
	public List<WebElement> getTransactionCards() {
		try {
			List<WebElement> cards = driver.findElements(TRANSACTION_CARD);
			if (!cards.isEmpty()) {
				return cards;
			}
			return driver.findElements(SUBSCRIPTION_TYPE);
		} catch (Exception e) {
			LoggerUtils.logInfo("No transaction cards found: " + safeString(e.getMessage()));
			return List.of();
		}
	}

	public int getTransactionCount() {
		return getTransactionCards().size();
	}

	public boolean hasTransactions() {
		try {
			return isAnyLocatorVisible(SUBSCRIPTION_TYPE, DOWNLOAD_INVOICE)
					|| getTransactionCount() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void waitForTransactionsToLoad() {
		try {
			pageWait.until(currentDriver -> hasTransactions());
		} catch (Exception e) {
			LoggerUtils.logInfo("No transactions found: " + safeString(e.getMessage()));
		}
	}

	// =============== Per-card field extractors ===============

	public String getFirstTransactionPlan() {
		return firstFieldValue(matchesAnyIgnoreCase("Gold", "Silver", "Bronze"));
	}

	public String getFirstTransactionDate() {
		return firstFieldValue(t -> t.matches("\\d{4}-\\d{2}-\\d{2}"));
	}

	public String getFirstTransactionTime() {
		return firstFieldValue(t -> t.matches("\\d{2}:\\d{2}:\\d{2}"));
	}

	public String getFirstTransactionAmount() {
		return firstFieldValue(t -> t.matches("\\d+\\.\\d{2}"));
	}

	public String getFirstTransactionStatus() {
		return firstFieldValue(text -> {
			for (String expected : EXPECTED_STATUSES) {
				if (text.equalsIgnoreCase(expected)) {
					return true;
				}
			}
			return false;
		});
	}

	public String getFirstTransactionPaymentMethod() {
		return firstFieldValue(text -> text.equalsIgnoreCase("Card payment")
				|| text.equalsIgnoreCase("UPI payment")
				|| text.toLowerCase().contains("payment"));
	}

	public String getFirstTransactionDuration() {
		return firstFieldValue(t -> t.matches("(?i)\\d+\\s+(day|days|week|weeks|month|months|year|years)"));
	}

	@FunctionalInterface
	private interface TextPredicate {
		boolean matches(String text);
	}

	private static java.util.function.Predicate<String> matchesAnyIgnoreCase(String... values) {
		return text -> {
			for (String v : values) {
				if (text.equalsIgnoreCase(v)) {
					return true;
				}
			}
			return false;
		};
	}

	private String firstFieldValue(java.util.function.Predicate<String> predicate) {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			for (WebElement element : firstCard.findElements(By.xpath(".//div[normalize-space()]"))) {
				String text = safeString(element.getText()).trim();
				if (!text.isEmpty() && predicate.test(text)) {
					return text;
				}
			}
			return "";
		} catch (Exception e) {
			LoggerUtils.logInfo("Could not extract first-card field: " + safeString(e.getMessage()));
			return "";
		}
	}

	// =============== Filter ===============

	public void clickFilterButton() {
		try {
			WebElement filterBtn = pageWait.until(ExpectedConditions.elementToBeClickable(FILTER_BUTTON));
			try {
				filterBtn.click();
			} catch (Exception clickException) {
				Objects.requireNonNull((JavascriptExecutor) driver).executeScript("arguments[0].click();", filterBtn);
			}
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to click Filter button: " + safeString(e.getMessage()));
		}
	}

	public boolean isFilterPopupDisplayed() {
		try {
			return isAnyLocatorVisible(FILTER_OVERLAY, FILTER_POPUP);
		} catch (Exception e) {
			return false;
		}
	}

	// =============== Search ===============

	public void searchTransaction(String transactionId) {
		try {
			WebElement searchInput = pageWait
					.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
			searchInput.clear();
			searchInput.sendKeys(safeString(transactionId));
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to search transaction ID: " + safeString(e.getMessage()));
		}
	}

	public void clickSearchButton() {
		try {
			WebElement searchBtn = driver.findElement(SEARCH_SUBMIT_BUTTON);
			searchBtn.click();
			waitQuietly(1000);
		} catch (Exception e) {
			// Search button can be absent when the field auto-submits — keep silent.
		}
	}

	public void clearSearch() {
		try {
			WebElement searchInput = driver.findElement(SEARCH_INPUT);
			searchInput.clear();
			waitQuietly(500);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to clear search input: " + safeString(e.getMessage()));
		}
	}

	public boolean isNoDataFoundMessageDisplayed() {
		try {
			return driver.findElement(NO_DATA_FOUND).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// =============== Invoice ===============

	public void clickDownloadInvoice() {
		try {
			WebElement downloadBtn = pageWait.until(ExpectedConditions.elementToBeClickable(DOWNLOAD_INVOICE));
			downloadBtn.click();
			waitQuietly(2000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to click Download Invoice button: " + safeString(e.getMessage()));
		}
	}

	// =============== Validation helpers (moved from TransactionsTests) ===============

	public boolean isPlanNamePopulated(String plan) {
		if (plan == null) {
			return false;
		}
		String trimmed = plan.trim();
		return !trimmed.isEmpty();
	}

	public boolean isDatePopulated(String date) {
		return date != null && !date.trim().isEmpty();
	}

	public boolean isTimePopulated(String time) {
		return time != null && !time.trim().isEmpty();
	}

	public boolean isAmountPopulated(String amount) {
		if (amount == null) {
			return false;
		}
		String trimmed = amount.trim();
		return !trimmed.isEmpty()
				&& AMOUNT_HAS_CURRENCY_OR_DIGIT.matcher(trimmed).matches()
				&& AMOUNT_HAS_DIGIT.matcher(trimmed).matches();
	}

	public boolean isPaymentMethodPopulated(String method) {
		if (method == null) {
			return false;
		}
		String lowered = method.toLowerCase();
		return !lowered.trim().isEmpty()
				&& (lowered.contains("card") || lowered.contains("upi") || lowered.contains("payment"));
	}

	public boolean isDurationPopulated(String duration) {
		if (duration == null) {
			return false;
		}
		String trimmed = duration.trim();
		return !trimmed.isEmpty()
				&& DURATION_HAS_PERIOD.matcher(trimmed).matches()
				&& trimmed.matches(".*\\d+.*");
	}

	public boolean isOneOfExpectedStatuses(String status) {
		if (status == null) {
			return false;
		}
		String lowered = status.toLowerCase();
		for (String expected : EXPECTED_STATUSES) {
			if (lowered.contains(expected.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public boolean isDateFormatValid(String date) {
		if (date == null) {
			return false;
		}
		return DATE_FORMAT_PRIMARY.matcher(date).matches() || DATE_FORMAT_FALLBACK.matcher(date).matches();
	}

	public boolean isTimeFormatValid(String time) {
		return time != null && TIME_FORMAT.matcher(time).matches();
	}

	/** Returns a string of the given length made of repeated 'A' characters. */
	public String buildLongString(int length) {
		StringBuilder sb = new StringBuilder(Math.max(0, length));
		for (int i = 0; i < length; i++) {
			sb.append('A');
		}
		return sb.toString();
	}

	// =============== Internal ===============

	private boolean isAnyLocatorVisible(By... locators) {
		for (By locator : locators) {
			try {
				List<WebElement> elements = driver.findElements(locator);
				for (WebElement element : elements) {
					if (element.isDisplayed()) {
						return true;
					}
				}
			} catch (Exception e) {
				// Continue checking remaining locators.
			}
		}
		return false;
	}
}
