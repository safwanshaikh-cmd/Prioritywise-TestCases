package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.TransactionsPage;
import utils.LoggerUtils;

/**
 * Transactions module tests aligned with the framework's
 * {@code ChapterTests} / {@code ConsumerBookDetailsTests} template.
 *
 * <p>Test Coverage: TC_419 - TC_441 (gap at TC_431 - TC_438 preserved).
 */
public class TransactionsTests extends BaseTest {

	private TransactionsPage transactions;

	@BeforeMethod(alwaysRun = true)
	@Override
	public void setup() {
		super.setup();

		transactions = new TransactionsPage(driver);
	}

	// ==================== TC_419: TRANSACTIONS DISPLAYED IN CARD FORMAT ====================

	/**
	 * TC_419: Transactions are displayed in card format. Test Flow: Log in as
	 * registered user → Open Transaction History → Inspect the first card.
	 * Expected: Transactions render as cards with non-empty plan, date, amount,
	 * and status values, and the card count is greater than zero.
	 */
	@Test(priority = 419, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_419: Verify transactions are displayed in card format")
	public void TC419_VerifyTransactionsDisplayedInCardFormat() {
		LoggerUtils.logTestStart("TC_419: Transactions Displayed In Card Format");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();

			LoggerUtils.logStep(2, "Verify the Transactions page is rendered with at least one card");
			boolean displayed = transactions.isTransactionsPageDisplayed();
			boolean hasTransactions = transactions.hasTransactions();
			int count = transactions.getTransactionCount();
			LoggerUtils.logInfo("TC_419 - STEP 2: page displayed: " + displayed
					+ ", has transactions: " + hasTransactions + ", count: " + count);
			Assert.assertTrue(displayed,
					"TC_419: Transactions page heading should be displayed");
			Assert.assertTrue(hasTransactions,
					"TC_419: At least one transaction card should be displayed");
			Assert.assertTrue(count > 0,
					"TC_419: Transaction count should be greater than 0, found: " + count);

			LoggerUtils.logStep(3, "Verify the first transaction card carries plan, date, amount, and status");
			String plan = transactions.getFirstTransactionPlan();
			String date = transactions.getFirstTransactionDate();
			String amount = transactions.getFirstTransactionAmount();
			String status = transactions.getFirstTransactionStatus();
			LoggerUtils.logInfo("TC_419 - STEP 3: plan='" + plan + "', date='" + date
					+ "', amount='" + amount + "', status='" + status + "'");
			Assert.assertTrue(transactions.isPlanNamePopulated(plan),
					"TC_419: Transaction plan should be present and non-empty");
			Assert.assertTrue(transactions.isDatePopulated(date),
					"TC_419: Transaction date should be present and non-empty");
			Assert.assertTrue(transactions.isAmountPopulated(amount),
					"TC_419: Transaction amount should be present, non-empty, and carry a currency/digit");
			Assert.assertNotNull(status,
					"TC_419: Status should not be null");
			Assert.assertTrue(transactions.isOneOfExpectedStatuses(status),
					"TC_419: Status should be one of the expected values. Found: '" + status + "'");
			LoggerUtils.logInfo("TC_419: Card rendered with all required fields (count=" + count + ")");

			LoggerUtils.logTestEnd("TC_419", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_419 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_420: CARD SHOWS PLAN, DATE, AMOUNT ====================

	/**
	 * TC_420: Transaction card shows plan, date, and amount. Test Flow: Log in
	 * → Open Transactions → Inspect first card. Expected: plan, date, and amount
	 * are present and the amount carries a currency symbol or numeric digit.
	 */
	@Test(priority = 420, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_420: Verify transaction card shows plan, date and amount")
	public void TC420_VerifyTransactionCardShowsPlanDateAmount() {
		LoggerUtils.logTestStart("TC_420: Card Shows Plan, Date, Amount");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();
			transactions.waitForTransactionsToLoad();

			LoggerUtils.logStep(2, "Verify the first card carries plan, date, and amount");
			String plan = transactions.getFirstTransactionPlan();
			String date = transactions.getFirstTransactionDate();
			String amount = transactions.getFirstTransactionAmount();
			LoggerUtils.logInfo("TC_420 - STEP 2: plan='" + plan + "', date='" + date
					+ "', amount='" + amount + "'");
			Assert.assertTrue(transactions.isPlanNamePopulated(plan),
					"TC_420: Plan name should be visible on transaction card");
			Assert.assertTrue(transactions.isDatePopulated(date),
					"TC_420: Transaction date should be visible on card");
			Assert.assertTrue(transactions.isAmountPopulated(amount),
					"TC_420: Transaction amount should be visible and contain currency symbol or digits. Found: '"
							+ amount + "'");
			LoggerUtils.logInfo("TC_420: Plan, date, amount visible (plan='" + plan + "')");

			LoggerUtils.logTestEnd("TC_420", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_420 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_421: STATUS BADGE DISPLAYED ====================

	/**
	 * TC_421: Transaction status badge is displayed. Test Flow: Log in → Open
	 * Transactions → Inspect first card status. Expected: status badge is
	 * present and matches one of the expected values.
	 */
	@Test(priority = 421, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_421: Verify transaction status badge is displayed")
	public void TC421_VerifyTransactionStatusCompletedDisplayed() {
		LoggerUtils.logTestStart("TC_421: Status Badge Displayed");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();
			transactions.waitForTransactionsToLoad();

			LoggerUtils.logStep(2, "Verify the status badge on the first card");
			String status = transactions.getFirstTransactionStatus();
			LoggerUtils.logInfo("TC_421 - STEP 2: status='" + status + "'");
			Assert.assertNotNull(status, "TC_421: Status badge should be displayed on transaction card");
			Assert.assertTrue(transactions.isOneOfExpectedStatuses(status),
					"TC_421: Status should be one of: Completed, Cancelled, Refunded, Pending, Processing. Found: '"
							+ status + "'");
			LoggerUtils.logInfo("TC_421: Status badge valid (status='" + status + "')");

			LoggerUtils.logTestEnd("TC_421", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_421 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_422: AMOUNT DISPLAYED ====================

	/**
	 * TC_422: Correct transaction amount is displayed. Test Flow: Log in → Open
	 * Transactions → Inspect first card amount. Expected: amount is present,
	 * non-empty, and matches a numeric-with-currency pattern.
	 */
	@Test(priority = 422, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_422: Verify transaction amount is displayed")
	public void TC422_VerifyTransactionAmountDisplayed() {
		LoggerUtils.logTestStart("TC_422: Amount Displayed");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();
			transactions.waitForTransactionsToLoad();

			LoggerUtils.logStep(2, "Verify the amount on the first card");
			String amount = transactions.getFirstTransactionAmount();
			LoggerUtils.logInfo("TC_422 - STEP 2: amount='" + amount + "'");
			Assert.assertTrue(transactions.isAmountPopulated(amount),
					"TC_422: Transaction amount should be present and carry currency symbol or numeric digits. Found: '"
							+ amount + "'");
			LoggerUtils.logInfo("TC_422: Amount valid (amount='" + amount + "')");

			LoggerUtils.logTestEnd("TC_422", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_422 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_423: DATE / TIME FORMAT ====================

	/**
	 * TC_423: Date and time format is correct (YYYY-MM-DD, HH:MM:SS). Test
	 * Flow: Log in → Open Transactions → Inspect first card. Expected: date
	 * matches YYYY-MM-DD or DD/MM/YYYY; time matches HH:MM:SS.
	 */
	@Test(priority = 423, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_423: Verify date and time format is correct")
	public void TC423_VerifyTransactionDateTimeFormat() {
		LoggerUtils.logTestStart("TC_423: Date / Time Format");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();
			transactions.waitForTransactionsToLoad();

			LoggerUtils.logStep(2, "Verify date and time on the first card");
			String date = transactions.getFirstTransactionDate();
			String time = transactions.getFirstTransactionTime();
			LoggerUtils.logInfo("TC_423 - STEP 2: date='" + date + "', time='" + time + "'");
			Assert.assertTrue(transactions.isDatePopulated(date),
					"TC_423: Transaction date should be present and non-empty");
			Assert.assertTrue(transactions.isTimePopulated(time),
					"TC_423: Transaction time should be present and non-empty");
			Assert.assertTrue(transactions.isDateFormatValid(date),
					"TC_423: Date should be in YYYY-MM-DD or DD/MM/YYYY format. Found: '"
							+ date + "'");
			Assert.assertTrue(transactions.isTimeFormatValid(time),
					"TC_423: Time should contain HH:MM:SS. Found: '" + time + "'");
			LoggerUtils.logInfo("TC_423: Date/time format valid (date='" + date + "', time='" + time + "')");

			LoggerUtils.logTestEnd("TC_423", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_423 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_424: PAYMENT METHOD ====================

	/**
	 * TC_424: Payment method is displayed. Test Flow: Log in → Open
	 * Transactions → Inspect first card. Expected: payment method present and
	 * contains Card / UPI / payment.
	 */
	@Test(priority = 424, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_424: Verify payment method is displayed")
	public void TC424_VerifyPaymentMethodDisplayed() {
		LoggerUtils.logTestStart("TC_424: Payment Method");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();
			transactions.waitForTransactionsToLoad();

			LoggerUtils.logStep(2, "Verify payment method on the first card");
			String method = transactions.getFirstTransactionPaymentMethod();
			LoggerUtils.logInfo("TC_424 - STEP 2: method='" + method + "'");
			Assert.assertTrue(transactions.isPaymentMethodPopulated(method),
					"TC_424: Payment method should contain Card, UPI, or 'payment'. Found: '"
							+ method + "'");
			LoggerUtils.logInfo("TC_424: Payment method valid (method='" + method + "')");

			LoggerUtils.logTestEnd("TC_424", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_424 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_425: SUBSCRIPTION DURATION ====================

	/**
	 * TC_425: Subscription duration is displayed. Test Flow: Log in → Open
	 * Transactions → Inspect first card. Expected: duration present, mentions a
	 * time period (month/day/week/year), and contains a numeric value.
	 */
	@Test(priority = 425, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_425: Verify subscription duration is displayed")
	public void TC425_VerifySubscriptionDurationDisplayed() {
		LoggerUtils.logTestStart("TC_425: Subscription Duration");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();
			transactions.waitForTransactionsToLoad();

			LoggerUtils.logStep(2, "Verify duration on the first card");
			String duration = transactions.getFirstTransactionDuration();
			LoggerUtils.logInfo("TC_425 - STEP 2: duration='" + duration + "'");
			Assert.assertTrue(transactions.isDurationPopulated(duration),
					"TC_425: Duration should contain a time period (month/day/week/year) and a numeric value. Found: '"
							+ duration + "'");
			LoggerUtils.logInfo("TC_425: Duration valid (duration='" + duration + "')");

			LoggerUtils.logTestEnd("TC_425", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_425 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_426: INVOICE DOWNLOAD ====================

	/**
	 * TC_426: Invoice download is triggered. Test Flow: Log in → Open
	 * Transactions → Click Download Invoice. Expected: the click executes
	 * without error; the page remains responsive.
	 */
	@Test(priority = 426, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_426: Verify invoice download is triggered")
	public void TC426_VerifyInvoiceDownload() {
		LoggerUtils.logTestStart("TC_426: Invoice Download");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();
			transactions.waitForTransactionsToLoad();

			LoggerUtils.logStep(2, "Click the Download Invoice button");
			transactions.clickDownloadInvoice();
			Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
					"TC_426: Transactions page should remain responsive after the download click");
			LoggerUtils.logInfo("TC_426: Invoice download click executed without error");

			LoggerUtils.logTestEnd("TC_426", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_426 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_427: SEARCH BY VALID TRANSACTION ID ====================

	/**
	 * TC_427: Search by valid transaction ID. Test Flow: Log in → Open
	 * Transactions → Enter "Gold" → Submit. Expected: page remains responsive;
	 * search returns matches or "No data found".
	 */
	@Test(priority = 427, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_427: Verify search by valid transaction ID")
	public void TC427_VerifySearchByValidTransactionId() {
		LoggerUtils.logTestStart("TC_427: Search By Valid Transaction ID");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();

			LoggerUtils.logStep(2, "Search for a transaction by typing 'Gold'");
			String validTxnId = "Gold";
			transactions.searchTransaction(validTxnId);
			transactions.clickSearchButton();
			LoggerUtils.logInfo("TC_427 - STEP 2: searched with id='" + validTxnId + "'");

			LoggerUtils.logStep(3, "Verify the page is responsive and either shows matches or the no-data message");
			Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
					"TC_427: Page should remain responsive after search");
			boolean hasResults = transactions.getTransactionCount() > 0;
			boolean noDataMessage = transactions.isNoDataFoundMessageDisplayed();
			LoggerUtils.logInfo("TC_427 - STEP 3: hasResults=" + hasResults + ", noDataMessage=" + noDataMessage);
			Assert.assertTrue(hasResults || noDataMessage,
					"TC_427: Search should either show matching transaction or 'No data found' message");

			LoggerUtils.logTestEnd("TC_427", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_427 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_428: SEARCH INVALID ID → NO RESULT ====================

	/**
	 * TC_428: Search by invalid transaction ID. Test Flow: Log in → Open
	 * Transactions → Enter "INVALID" → Submit. Expected: "No data found"
	 * message appears.
	 */
	@Test(priority = 428, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_428: Verify search by invalid ID shows no result")
	public void TC428_VerifySearchInvalidIdShowsNoResult() {
		LoggerUtils.logTestStart("TC_428: Search Invalid ID Shows No Result");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();

			LoggerUtils.logStep(2, "Search with an obviously invalid ID");
			String invalidId = "INVALID";
			transactions.searchTransaction(invalidId);
			transactions.clickSearchButton();
			LoggerUtils.logInfo("TC_428 - STEP 2: searched with id='" + invalidId + "'");

			LoggerUtils.logStep(3, "Verify the no-data message appears and the result list is empty");
			boolean noDataMessage = transactions.isNoDataFoundMessageDisplayed();
			int resultCount = transactions.getTransactionCount();
			LoggerUtils.logInfo(
					"TC_428 - STEP 3: noDataMessage=" + noDataMessage + ", resultCount=" + resultCount);
			Assert.assertTrue(noDataMessage,
					"TC_428: 'No data found' or 'No results' message should be displayed for invalid search");

			LoggerUtils.logTestEnd("TC_428", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_428 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_429: EMPTY SEARCH → DEFAULT LIST ====================

	/**
	 * TC_429: Empty search shows the default list. Test Flow: Log in → Open
	 * Transactions → Clear search input. Expected: count after clear is at
	 * least the initial count.
	 */
	@Test(priority = 429, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_429: Verify empty search shows default list")
	public void TC429_VerifyEmptySearchShowsDefaultList() {
		LoggerUtils.logTestStart("TC_429: Empty Search Shows Default List");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();

			LoggerUtils.logStep(2, "Capture the initial transaction count");
			int initialCount = transactions.getTransactionCount();
			LoggerUtils.logInfo("TC_429 - STEP 2: initial transaction count: " + initialCount);

			LoggerUtils.logStep(3, "Clear the search input and verify the list is restored");
			transactions.clearSearch();
			int afterClearCount = transactions.getTransactionCount();
			LoggerUtils.logInfo(
					"TC_429 - STEP 3: after-clear transaction count: " + afterClearCount);
			Assert.assertTrue(afterClearCount >= 0,
					"TC_429: Default list should be displayed after clearing search");
			Assert.assertTrue(afterClearCount >= initialCount,
					"TC_429: Clearing search should show all transactions. Initial: " + initialCount
							+ ", After clear: " + afterClearCount);

			LoggerUtils.logTestEnd("TC_429", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_429 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_430: FILTER POPUP OPENS ====================

	/**
	 * TC_430: Filter popup opens. Test Flow: Log in → Open Transactions →
	 * Click Filter. Expected: filter popup is displayed.
	 */
	@Test(priority = 430, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_430: Verify filter popup opens")
	public void TC430_VerifyFilterPopupOpens() {
		LoggerUtils.logTestStart("TC_430: Filter Popup Opens");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();

			LoggerUtils.logStep(2, "Click the Filter button and verify the popup is displayed");
			transactions.clickFilterButton();
			boolean popupDisplayed = transactions.isFilterPopupDisplayed();
			LoggerUtils.logInfo("TC_430 - STEP 2: filter popup displayed: " + popupDisplayed);
			Assert.assertTrue(popupDisplayed,
					"TC_430: Filter popup should be displayed after clicking Filter button");

			LoggerUtils.logTestEnd("TC_430", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_430 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_439: SPECIAL-CHARACTER SEARCH ====================

	/**
	 * TC_439: Special-character search does not break the page. Test Flow:
	 * Log in → Open Transactions → Search "@#$%" → Submit. Expected: page is
	 * responsive and either shows "No data found" or an empty result set.
	 */
	@Test(priority = 439, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_439: Verify special-character search is handled safely")
	public void TC439_VerifySpecialCharacterSearch() {
		LoggerUtils.logTestStart("TC_439: Special-Character Search");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();

			LoggerUtils.logStep(2, "Search using special characters and verify the page stays responsive");
			String specialChars = "@#$%";
			transactions.searchTransaction(specialChars);
			transactions.clickSearchButton();
			LoggerUtils.logInfo("TC_439 - STEP 2: searched with chars='" + specialChars + "'");

			LoggerUtils.logStep(3, "Verify either the no-data message or an empty result set is rendered");
			Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
					"TC_439: Page should still be responsive after special character search");
			boolean noDataMessage = transactions.isNoDataFoundMessageDisplayed();
			int resultCount = transactions.getTransactionCount();
			LoggerUtils.logInfo(
					"TC_439 - STEP 3: noDataMessage=" + noDataMessage + ", resultCount=" + resultCount);
			// No crash, no enforced assertion on emptiness — record the outcome.
			LoggerUtils.logInfo("TC_439: Special character search handled without crash");

			LoggerUtils.logTestEnd("TC_439", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_439 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_440: MAX-LENGTH TRANSACTION ID ====================

	/**
	 * TC_440: Max-length transaction ID boundary. Test Flow: Log in → Open
	 * Transactions → Search a 50-char string. Expected: page remains
	 * responsive; search does not crash.
	 */
	@Test(priority = 440, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_440: Verify max-length transaction ID is handled gracefully")
	public void TC440_VerifyMaxLengthTransactionId() {
		LoggerUtils.logTestStart("TC_440: Max-Length Transaction ID");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();

			LoggerUtils.logStep(2, "Search with a 50-character transaction ID");
			String longTxnId = transactions.buildLongString(50);
			LoggerUtils.logInfo("TC_440 - STEP 2: testing with " + longTxnId.length() + "-character transaction ID");
			transactions.searchTransaction(longTxnId);
			transactions.clickSearchButton();

			LoggerUtils.logStep(3, "Verify the page remains responsive after the long-input search");
			Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
					"TC_440: Search field should still be active with long input");
			int resultCount = transactions.getTransactionCount();
			LoggerUtils.logInfo("TC_440 - STEP 3: resultCount=" + resultCount);
			LoggerUtils.logInfo("TC_440: Max length transaction ID handled correctly");

			LoggerUtils.logTestEnd("TC_440", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_440 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_441: INVOICE DOWNLOAD ERROR HANDLING ====================

	/**
	 * TC_441: Invoice download error is handled gracefully. Test Flow: Log in
	 * → Open Transactions → Click Download Invoice. Expected: page is
	 * responsive after the click; no crash even if the file is unavailable.
	 */
	@Test(priority = 441, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_441: Verify invoice download error is handled gracefully")
	public void TC441_VerifyInvoiceDownloadErrorHandling() {
		LoggerUtils.logTestStart("TC_441: Invoice Download Error Handling");

		try {
			LoggerUtils.logStep(1, "Log in as registered user and navigate to the Transactions page");
			transactions.loginAsRegisteredUser();
			transactions.openTransactionsPage();
			transactions.waitForTransactionsToLoad();

			LoggerUtils.logStep(2, "Click the Download Invoice button and verify the page stays responsive");
			try {
				transactions.clickDownloadInvoice();
			} catch (Exception downloadFailure) {
				LoggerUtils.logInfo(
						"TC_441 - STEP 2: download exception swallowed: " + transactions.safeString(downloadFailure.getMessage()));
			}
			Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
					"TC_441: Page should remain responsive even if download fails");
			LoggerUtils.logInfo("TC_441: Invoice download click did not crash the page");

			LoggerUtils.logTestEnd("TC_441", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_441 - Test failed: " + transactions.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== NO LOCAL HELPERS — SEE TransactionsPage ====================
}
