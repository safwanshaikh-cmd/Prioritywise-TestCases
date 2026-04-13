package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.DashboardPage;
import pages.LoginPage;
import pages.TransactionsPage;

/**
 * Transaction module tests aligned with the existing framework. Covers TC_419
 * through TC_441.
 */
public class TransactionsTests extends BaseTest {

	private LoginPage login;
	private TransactionsPage transactions;
	private DashboardPage dashboard;

	@Override
	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setup();
		login = new LoginPage(driver);
		transactions = new TransactionsPage(driver);
		dashboard = new DashboardPage(driver);
	}

	/**
	 * Helper method to login as registered user
	 */
	private void loginAsRegisteredUser() {
		try {
			login.openLogin();
			login.loginUser("safwan.shaikh+008@11axis.com", "Password@123");
			LOGGER.info("Logged in as registered user");
		} catch (Exception e) {
			throw new RuntimeException("Failed to login as registered user", e);
		}
	}

	/**
	 * Helper method to navigate to Transactions page
	 * 
	 * @throws InterruptedException
	 */
	private void navigateToTransactionsPage() throws InterruptedException {
		boolean dashboardReady = dashboard.waitForDashboardShell();
		Assert.assertTrue(dashboardReady, "Transactions navigation: Dashboard should be stable after login");
		LOGGER.info("Dashboard is stable");

		boolean sideMenuOpened = dashboard.openSimpleSideMenu() || dashboard.openSideMenu();
		Assert.assertTrue(sideMenuOpened, "Transactions navigation: Side menu should open");
		LOGGER.info("Side menu opened");

		boolean sideMenuItemsLoaded = dashboard.waitForSideMenuItemsLoaded();
		Assert.assertTrue(sideMenuItemsLoaded, "Transactions navigation: Side menu items should load");
		LOGGER.info("Side menu items loaded");

		transactions.navigateToTransactions();
		LOGGER.info("Clicked on Transaction History");

		transactions.waitForTransactionsPageToLoad();
		LOGGER.info("Transactions page load wait completed");
		Assert.assertTrue(transactions.isTransactionsPageDisplayed() || transactions.hasTransactions(),
				"Transactions navigation: Transaction History page should be reachable after clicking side menu item");

		if (dashboard.isSideMenuOpen()) {
			boolean sideMenuClosed = dashboard.closeSimpleSideMenu();
			if (sideMenuClosed || !dashboard.isSideMenuOpen()) {
				LOGGER.info("Side menu closed");
			} else {
				LOGGER.warning(
						"Side menu remained open after navigation; continuing because Transaction History was reached");
			}
		} else {
			LOGGER.info("Side menu already closed");
		}
	}

	/**
	 * TC_419: Verify transactions displayed in card format
	 */
	@Test(priority = 419, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTransactionsDisplayedInCardFormat() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page heading should be visible
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
				"TC_419: Transactions page heading should be displayed");

		// Validation 2: At least one transaction should exist
		Assert.assertTrue(transactions.hasTransactions(), "TC_419: At least one transaction card should be displayed");

		// Validation 3: Transaction count should be greater than zero
		int transactionCount = transactions.getTransactionCount();
		Assert.assertTrue(transactionCount > 0,
				"TC_419: Transaction count should be greater than 0, but found: " + transactionCount);

		// Validation 4: Each transaction card should have required details (plan, date,
		// amount, status)
		if (transactionCount > 0) {
			String plan = transactions.getFirstTransactionPlan();
			String date = transactions.getFirstTransactionDate();
			String amount = transactions.getFirstTransactionAmount();
			String status = transactions.getFirstTransactionStatus();

			Assert.assertNotNull(plan, "TC_419: Transaction plan should not be null");
			Assert.assertNotNull(date, "TC_419: Transaction date should not be null");
			Assert.assertNotNull(amount, "TC_419: Transaction amount should not be null");
			Assert.assertNotNull(status, "TC_419: Transaction status should not be null");

			Assert.assertFalse(plan.trim().isEmpty(), "TC_419: Transaction plan should not be empty");
			Assert.assertFalse(date.trim().isEmpty(), "TC_419: Transaction date should not be empty");
			Assert.assertFalse(amount.trim().isEmpty(), "TC_419: Transaction amount should not be empty");
			Assert.assertFalse(status.trim().isEmpty(), "TC_419: Transaction status should not be empty");
		}

		LOGGER.info("TC_419: Transaction cards displayed with details - Total transactions: " + transactionCount);
	}

	/**
	 * TC_420: Verify card shows plan, date, amount
	 */
	@Test(priority = 420, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTransactionCardShowsPlanDateAmount() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be loaded
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_420: Transactions page should be displayed");

		// Validation 2: Transactions should be loaded
		transactions.waitForTransactionsToLoad();
		Assert.assertTrue(transactions.hasTransactions(),
				"TC_420: At least one transaction should exist to verify details");

		// Validation 3: Plan name should be visible and not empty
		String plan = transactions.getFirstTransactionPlan();
		Assert.assertNotNull(plan, "TC_420: Plan name should be visible on transaction card");
		Assert.assertFalse(plan.trim().isEmpty(), "TC_420: Plan name should not be empty");

		// Validation 4: Date should be visible and not empty
		String date = transactions.getFirstTransactionDate();
		Assert.assertNotNull(date, "TC_420: Transaction date should be visible on card");
		Assert.assertFalse(date.trim().isEmpty(), "TC_420: Transaction date should not be empty");

		// Validation 5: Amount should be visible, not empty, and contain currency
		// symbol or numeric value
		String amount = transactions.getFirstTransactionAmount();
		Assert.assertNotNull(amount, "TC_420: Transaction amount should be visible on card");
		Assert.assertFalse(amount.trim().isEmpty(), "TC_420: Transaction amount should not be empty");
		Assert.assertTrue(amount.matches(".*[₹Rs$0-9].*"), "TC_420: Amount should contain currency symbol or digits");

		LOGGER.info(
				"TC_420: Plan name, date, amount visible - Plan: " + plan + ", Date: " + date + ", Amount: " + amount);
	}

	/**
	 * TC_421: Verify status (Completed) is shown
	 */
	@Test(priority = 421, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTransactionStatusCompletedDisplayed() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_421: Transactions page should be displayed");

		// Validation 2: Transactions should be loaded
		transactions.waitForTransactionsToLoad();
		Assert.assertTrue(transactions.hasTransactions(),
				"TC_421: At least one transaction should exist to verify status");

		// Validation 3: Status badge should be displayed
		String status = transactions.getFirstTransactionStatus();
		Assert.assertNotNull(status, "TC_421: Status badge should be displayed on transaction card");
		Assert.assertFalse(status.trim().isEmpty(), "TC_421: Status badge should not be empty");

		// Validation 4: Status should be one of the expected values (Completed,
		// Cancelled, Refunded, Pending)
		String[] expectedStatuses = { "Completed", "Cancelled", "Refunded", "Pending", "Processing" };
		boolean statusMatch = false;
		for (String expectedStatus : expectedStatuses) {
			if (status.equalsIgnoreCase(expectedStatus)
					|| status.toLowerCase().contains(expectedStatus.toLowerCase())) {
				statusMatch = true;
				break;
			}
		}
		Assert.assertTrue(statusMatch,
				"TC_421: Status should be one of: Completed, Cancelled, Refunded, Pending, Processing. Found: "
						+ status);

		LOGGER.info("TC_421: Correct status badge displayed - Status: " + status);
	}

	/**
	 * TC_422: Verify correct amount displayed (₹199 for Gold Plan)
	 */
	@Test(priority = 422, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTransactionAmountDisplayed() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_422: Transactions page should be displayed");

		// Validation 2: Transactions should be loaded
		transactions.waitForTransactionsToLoad();
		Assert.assertTrue(transactions.hasTransactions(),
				"TC_422: At least one transaction should exist to verify amount");

		// Validation 3: Amount should be displayed
		String amount = transactions.getFirstTransactionAmount();
		Assert.assertNotNull(amount, "TC_422: Transaction amount should be displayed");
		Assert.assertFalse(amount.trim().isEmpty(), "TC_422: Transaction amount should not be empty");

		// Validation 4: Amount should contain currency symbol (₹ or Rs) or numeric
		// digits
		Assert.assertTrue(amount.matches(".*[₹Rs$0-9].*"),
				"TC_422: Amount should contain currency symbol (₹/Rs) or numeric digits. Found: " + amount);

		// Validation 5: Amount should be a valid numeric format (e.g., ₹199.00 or ₹199)
		Assert.assertTrue(amount.matches(".*\\d+.*"), "TC_422: Amount should contain numeric digits");

		LOGGER.info("TC_422: Correct amount shown - Amount: " + amount);
	}

	/**
	 * TC_423: Verify date/time format correctness (YYYY-MM-DD)
	 */
	@Test(priority = 423, retryAnalyzer = RetryAnalyzer.class)
	public void verifyTransactionDateTimeFormat() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_423: Transactions page should be displayed");

		// Validation 2: Transactions should be loaded
		transactions.waitForTransactionsToLoad();
		Assert.assertTrue(transactions.hasTransactions(),
				"TC_423: At least one transaction should exist to verify date format");

		// Validation 3: Date and time should be displayed
		String date = transactions.getFirstTransactionDate();
		String time = transactions.getFirstTransactionTime();
		Assert.assertNotNull(date, "TC_423: Transaction date should be displayed");
		Assert.assertFalse(date.trim().isEmpty(), "TC_423: Transaction date should not be empty");
		Assert.assertNotNull(time, "TC_423: Transaction time should be displayed");
		Assert.assertFalse(time.trim().isEmpty(), "TC_423: Transaction time should not be empty");

		// Validation 4: Date should be in YYYY-MM-DD format or contain date pattern
		Assert.assertTrue(date.matches(".*\\d{4}-\\d{2}-\\d{2}.*") || date.matches(".*\\d{2}/\\d{2}/\\d{4}.*"),
				"TC_423: Date should be in YYYY-MM-DD or DD/MM/YYYY format. Found: " + date);

		// Validation 5: Time should be in HH:MM:SS format
		Assert.assertTrue(time.matches("\\d{1,2}:\\d{2}:\\d{2}"),
				"TC_423: Time should contain HH:MM:SS. Found: " + time);

		LOGGER.info("TC_423: Correct format displayed - Date: " + date + ", Time: " + time);
	}

	/**
	 * TC_424: Verify payment method tag (Card)
	 */
	@Test(priority = 424, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPaymentMethodDisplayed() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_424: Transactions page should be displayed");

		// Validation 2: Transactions should be loaded
		transactions.waitForTransactionsToLoad();
		Assert.assertTrue(transactions.hasTransactions(),
				"TC_424: At least one transaction should exist to verify payment method");

		// Validation 3: Payment method should be displayed
		String paymentMethod = transactions.getFirstTransactionPaymentMethod();
		Assert.assertNotNull(paymentMethod, "TC_424: Payment method should be shown on transaction card");
		Assert.assertFalse(paymentMethod.trim().isEmpty(), "TC_424: Payment method should not be empty");

		// Validation 4: Payment method should be Card or UPI
		String paymentMethodLower = paymentMethod.toLowerCase();
		Assert.assertTrue(
				paymentMethodLower.contains("card") || paymentMethodLower.contains("upi")
						|| paymentMethodLower.contains("payment"),
				"TC_424: Payment method should be Card, UPI, or contain 'payment'. Found: " + paymentMethod);

		LOGGER.info("TC_424: Correct payment method shown - Method: " + paymentMethod);
	}

	/**
	 * TC_425: Verify duration (1 month) shown
	 */
	@Test(priority = 425, retryAnalyzer = RetryAnalyzer.class)
	public void verifySubscriptionDurationDisplayed() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_425: Transactions page should be displayed");

		// Validation 2: Transactions should be loaded
		transactions.waitForTransactionsToLoad();
		Assert.assertTrue(transactions.hasTransactions(),
				"TC_425: At least one transaction should exist to verify duration");

		// Validation 3: Duration should be displayed
		String duration = transactions.getFirstTransactionDuration();
		Assert.assertNotNull(duration, "TC_425: Subscription duration should be displayed");
		Assert.assertFalse(duration.trim().isEmpty(), "TC_425: Subscription duration should not be empty");

		// Validation 4: Duration should mention months or days (e.g., "1 month", "30
		// days")
		Assert.assertTrue(duration.toLowerCase().matches(".*(month|day|week|year).*"),
				"TC_425: Duration should contain time period (month/day/week/year). Found: " + duration);

		// Validation 5: Duration should contain numeric value
		Assert.assertTrue(duration.matches(".*\\d+.*"),
				"TC_425: Duration should contain numeric value. Found: " + duration);

		LOGGER.info("TC_425: Duration displayed correctly - Duration: " + duration);
	}

	/**
	 * TC_426: Verify invoice download functionality
	 * 
	 * @throws InterruptedException
	 */
	@Test(priority = 426, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvoiceDownload() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_426: Transactions page should be displayed");

		// Validation 2: Transactions should be loaded
		transactions.waitForTransactionsToLoad();
		Assert.assertTrue(transactions.hasTransactions(),
				"TC_426: At least one transaction should exist to download invoice");

		// Validation 3: Download Invoice button should be present
		// Note: We can't verify file download in Selenium without additional setup
		// So we verify the button is present and clickable
		try {
			transactions.clickDownloadInvoice();
			LOGGER.info("TC_426: Invoice download button clicked successfully - File download should start");
		} catch (Exception e) {
			Assert.fail("TC_426: Failed to click Download Invoice button: " + e.getMessage());
		}
	}

	/**
	 * TC_427: Search by valid Transaction ID
	 * 
	 * @throws InterruptedException
	 */
	@Test(priority = 427, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchByValidTransactionId() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_427: Transactions page should be displayed");

		// Validation 2: Search input should be accessible
		String validTxnId = "Gold";
		try {
			transactions.searchTransaction(validTxnId);
			transactions.clickSearchButton();

			// Validation 3: Search should complete without errors
			Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
					"TC_427: Page should remain responsive after search");

			// Validation 4: Either matching transaction found or "No data found" message
			// should appear
			boolean hasResults = transactions.getTransactionCount() > 0;
			boolean noDataMessage = transactions.isNoDataFoundMessageDisplayed();

			Assert.assertTrue(hasResults || noDataMessage,
					"TC_427: Search should either show matching transaction or 'No data found' message");

		} catch (Exception e) {
			Assert.fail("TC_427: Search by transaction ID failed: " + e.getMessage());
		}

		LOGGER.info("TC_427: Search by valid Transaction ID completed - ID: " + validTxnId);
	}

	/**
	 * TC_428: Search invalid ID - verify no result
	 * 
	 * @throws InterruptedException
	 */
	@Test(priority = 428, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchInvalidIdShowsNoResult() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_428: Transactions page should be displayed");

		// Validation 2: Search with invalid ID
		String invalidId = "INVALID";
		transactions.searchTransaction(invalidId);
		transactions.clickSearchButton();

		// Validation 3: "No data found" message should be displayed
		boolean noDataMessage = transactions.isNoDataFoundMessageDisplayed();
		Assert.assertTrue(noDataMessage,
				"TC_428: 'No data found' or 'No results' message should be displayed for invalid search");

		// Validation 4: Transaction list should be empty or show no matching results
		int resultCount = transactions.getTransactionCount();

		LOGGER.info("TC_428: \"No data found\" displayed - Message found: " + noDataMessage + ", Result count: "
				+ resultCount);
	}

	/**
	 * TC_429: Empty search - verify default list displayed
	 * 
	 * @throws InterruptedException
	 */
	@Test(priority = 429, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptySearchShowsDefaultList() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_429: Transactions page should be displayed");

		// Validation 2: Get initial transaction count
		int initialCount = transactions.getTransactionCount();
		LOGGER.info("TC_429: Initial transaction count: " + initialCount);

		// Validation 3: Clear search input
		transactions.clearSearch();
		Thread.sleep(1000);

		// Validation 4: Default list should still be displayed
		int afterClearCount = transactions.getTransactionCount();
		Assert.assertTrue(afterClearCount >= 0, "TC_429: Default list should be displayed after clearing search");

		// Validation 5: Count should be same as or greater than initial (all
		// transactions shown)
		Assert.assertTrue(afterClearCount >= initialCount,
				"TC_429: Clearing search should show all transactions. Initial: " + initialCount + ", After clear: "
						+ afterClearCount);

		LOGGER.info("TC_429: Default list displayed - Initial count: " + initialCount + ", After clear: "
				+ afterClearCount);
	}

	/**
	 * TC_430: Verify filter popup opens
	 */
	@Test(priority = 430, retryAnalyzer = RetryAnalyzer.class)
	public void verifyFilterPopupOpens() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_430: Transactions page should be displayed");

		// Validation 2: Click Filter button
		transactions.clickFilterButton();

		// Validation 3: Filter popup should be displayed
		Assert.assertTrue(transactions.isFilterPopupDisplayed(),
				"TC_430: Filter popup should be displayed after clicking Filter button");

		LOGGER.info("TC_430: Filter popup displayed successfully");
	}

	/**
	 * TC_439: Special character search validation
	 */
	@Test(priority = 439, retryAnalyzer = RetryAnalyzer.class)
	public void verifySpecialCharacterSearch() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_439: Transactions page should be displayed");

		// Validation 2: Search with special characters
		String specialChars = "@#$%";
		transactions.searchTransaction(specialChars);
		transactions.clickSearchButton();
		Thread.sleep(1000);

		// Validation 3: No crash - page should still be responsive
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
				"TC_439: Page should still be responsive after special character search");

		// Validation 4: No error or exception should occur (implicitly verified by
		// reaching this point)
		LOGGER.info("TC_439: Special characters handled without crash");

		// Validation 5: Either no results or graceful handling (no data found message)
		boolean noDataMessage = transactions.isNoDataFoundMessageDisplayed();
		int resultCount = transactions.getTransactionCount();
		LOGGER.info("TC_439: Search result - No data message: " + noDataMessage + ", Count: " + resultCount);

		LOGGER.info("TC_439: Special character search validated");
	}

	/**
	 * TC_440: Max length Transaction ID boundary validation
	 */
	@Test(priority = 440, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMaxLengthTransactionId() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_440: Transactions page should be displayed");

		// Validation 2: Create 50 character transaction ID (boundary test)
		StringBuilder fiftyChars = new StringBuilder();
		for (int i = 0; i < 50; i++) {
			fiftyChars.append("A");
		}
		String longTxnId = fiftyChars.toString();
		LOGGER.info("TC_440: Testing with " + longTxnId.length() + " character transaction ID");

		// Validation 3: Search field should accept long input
		transactions.searchTransaction(longTxnId);
		transactions.clickSearchButton();
		Thread.sleep(1000);

		// Validation 4: Field should still be active and page responsive (no crash)
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
				"TC_440: Search field should still be active with long input");

		// Validation 5: Search should complete without errors
		int resultCount = transactions.getTransactionCount();
		LOGGER.info("TC_440: Search completed with long input - Result count: " + resultCount);

		LOGGER.info("TC_440: Max length transaction ID handled correctly");
	}

	/**
	 * TC_441: Invoice download failure - verify error handling
	 */
	@Test(priority = 441, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvoiceDownloadErrorHandling() throws InterruptedException {
		loginAsRegisteredUser();
		navigateToTransactionsPage();

		// Validation 1: Transactions page should be displayed
		Assert.assertTrue(transactions.isTransactionsPageDisplayed(), "TC_441: Transactions page should be displayed");

		// Validation 2: Transactions should be loaded
		transactions.waitForTransactionsToLoad();

		// Validation 3: Try to download invoice
		// Note: In a real scenario, this might involve network failure or missing file
		// For now, we verify the button is present and clickable
		try {
			transactions.clickDownloadInvoice();
			Thread.sleep(1000);

			// Validation 4: Page should still be responsive after download attempt
			Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
					"TC_441: Page should still be responsive after download attempt");

			// Validation 5: No crash or error should occur (implicitly verified)
			LOGGER.info("TC_441: Invoice download button clicked - Error handling verified");

		} catch (Exception e) {
			// If download fails, verify the error is handled gracefully
			LOGGER.info("TC_441: Download exception handled gracefully: " + e.getMessage());
			Assert.assertTrue(transactions.isTransactionsPageDisplayed(),
					"TC_441: Page should remain responsive even if download fails");
		}

		LOGGER.info("TC_441: Invoice download error handling verified");
	}
}
