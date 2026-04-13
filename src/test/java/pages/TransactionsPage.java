package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigReader;

/**
 * Page object for Transactions page.
 * Handles all transaction-related operations like viewing, filtering, and searching.
 */
public class TransactionsPage extends BasePage {

	private final WebDriverWait pageWait;

	// Locators
	private static final String TRANSACTIONS_HEADING = "//h1[contains(text(), 'Transactions') or contains(text(), 'Transaction')]";
	private static final String TRANSACTION_CARD = "//div[contains(@class, 'transaction') or contains(@class, 'card')]";
	private static final String FILTER_BUTTON = "//button[contains(text(), 'Filter') or contains(@class, 'filter') or @data-testid='button_filter']";
	private static final String FILTER_POPUP = "//div[contains(@class, 'filter') or contains(@class, 'modal') or contains(@class, 'popup')]";
	private static final String SEARCH_INPUT = "//input[@type='search' or @placeholder='search' or contains(@placeholder, 'Search')]";
	private static final String DOWNLOAD_INVOICE_BUTTON = "//button[contains(text(), 'Download') or contains(text(), 'Invoice')]";
	private static final String APPLY_FILTER_BUTTON = "//button[contains(text(), 'Apply') or contains(text(), 'Apply Now')]";

	// Filter locators
	private static final String FILTER_STATUS_COMPLETED = "//label[contains(text(), 'Completed') or .//input[@value='completed']";
	private static final String FILTER_STATUS_CANCELLED = "//label[contains(text(), 'Cancelled') or .//input[@value='cancelled']";
	private static final String FILTER_STATUS_REFUNDED = "//label[contains(text(), 'Refunded') or .//input[@value='refunded']";
	private static final String FILTER_PAYMENT_METHOD_CARD = "//label[contains(text(), 'Card') or .//input[@value='card']";
	private static final String FILTER_PAYMENT_METHOD_UPI = "//label[contains(text(), 'UPI') or .//input[@value='upi']";

	public TransactionsPage(WebDriver driver) {
		super(driver);
		this.pageWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
	}

	/**
	 * Navigate to Transactions page from dashboard
	 */
	public void navigateToTransactions() {
		try {
			DashboardPage dashboard = new DashboardPage(driver);
			dashboard.clickSideMenuItemAndCaptureUrl("transaction", "transactions", "transaction history", "payment history");
			pageWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TRANSACTIONS_HEADING)));
			LOGGER.info("Navigated to Transactions page");
		} catch (Exception e) {
			LOGGER.severe("Failed to navigate to Transactions page: " + e.getMessage());
		}
	}

	/**
	 * Check if Transactions page is displayed
	 */
	public boolean isTransactionsPageDisplayed() {
		try {
			return pageWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TRANSACTIONS_HEADING))).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get all transaction cards
	 */
	public List<WebElement> getTransactionCards() {
		try {
			return driver.findElements(By.xpath(TRANSACTION_CARD));
		} catch (Exception e) {
			LOGGER.warning("No transaction cards found: " + e.getMessage());
			return List.of();
		}
	}

	/**
	 * Get transaction count
	 */
	public int getTransactionCount() {
		return getTransactionCards().size();
	}

	/**
	 * Get details from first transaction card
	 */
	public String getFirstTransactionPlan() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			WebElement planElement = firstCard.findElement(By.xpath(".//*[contains(text(), 'Plan') or contains(@class, 'plan')]"));
			return planElement.getText();
		} catch (Exception e) {
			LOGGER.warning("Could not get plan from first transaction: " + e.getMessage());
			return "";
		}
	}

	public String getFirstTransactionDate() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			WebElement dateElement = firstCard.findElement(By.xpath(".//*[contains(@class, 'date') or contains(@class, 'time')]"));
			return dateElement.getText();
		} catch (Exception e) {
			LOGGER.warning("Could not get date from first transaction: " + e.getMessage());
			return "";
		}
	}

	public String getFirstTransactionAmount() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			WebElement amountElement = firstCard.findElement(By.xpath(".//*[contains(@class, 'amount') or contains(text(), '₹') or contains(text(), 'Rs')]"));
			return amountElement.getText();
		} catch (Exception e) {
			LOGGER.warning("Could not get amount from first transaction: " + e.getMessage());
			return "";
		}
	}

	/**
	 * Get status badge text from first transaction
	 */
	public String getFirstTransactionStatus() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			WebElement statusElement = firstCard.findElement(By.xpath(".//*[contains(@class, 'status') or contains(@class, 'badge')]"));
			return statusElement.getText().trim();
		} catch (Exception e) {
			LOGGER.warning("Could not get status from first transaction: " + e.getMessage());
			return "";
		}
	}

	/**
	 * Get payment method from first transaction
	 */
	public String getFirstTransactionPaymentMethod() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			WebElement paymentElement = firstCard.findElement(By.xpath(".//*[contains(@class, 'payment') or contains(text(), 'Card') or contains(text(), 'UPI')]"));
			return paymentElement.getText();
		} catch (Exception e) {
			LOGGER.warning("Could not get payment method from first transaction: " + e.getMessage());
			return "";
		}
	}

	/**
	 * Get duration from first transaction
	 */
	public String getFirstTransactionDuration() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			WebElement durationElement = firstCard.findElement(By.xpath(".//*[contains(@class, 'duration') or contains(text(), 'month')]"));
			return durationElement.getText();
		} catch (Exception e) {
			LOGGER.warning("Could not get duration from first transaction: " + e.getMessage());
			return "";
		}
	}

	/**
	 * Click Filter button
	 */
	public void clickFilterButton() {
		try {
			WebElement filterBtn = pageWait.until(ExpectedConditions.elementToBeClickable(By.xpath(FILTER_BUTTON)));
			filterBtn.click();
			LOGGER.info("Clicked Filter button");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.severe("Failed to click Filter button: " + e.getMessage());
		}
	}

	/**
	 * Check if filter popup is displayed
	 */
	public boolean isFilterPopupDisplayed() {
		try {
			return driver.findElement(By.xpath(FILTER_POPUP)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Select filter by status
	 */
	public void selectFilterByStatus(String status) {
		try {
			String statusLocator = null;
			switch (status.toLowerCase()) {
				case "completed":
					statusLocator = FILTER_STATUS_COMPLETED;
					break;
				case "cancelled":
					statusLocator = FILTER_STATUS_CANCELLED;
					break;
				case "refunded":
					statusLocator = FILTER_STATUS_REFUNDED;
					break;
				default:
					throw new IllegalArgumentException("Unknown status: " + status);
			}

			WebElement statusElement = driver.findElement(By.xpath(statusLocator));
			statusElement.click();
			LOGGER.info("Selected filter by status: " + status);
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.severe("Failed to select filter by status " + status + ": " + e.getMessage());
		}
	}

	/**
	 * Select filter by payment method
	 */
	public void selectFilterByPaymentMethod(String paymentMethod) {
		try {
			String paymentLocator = null;
			switch (paymentMethod.toLowerCase()) {
				case "card":
					paymentLocator = FILTER_PAYMENT_METHOD_CARD;
					break;
				case "upi":
					paymentLocator = FILTER_PAYMENT_METHOD_UPI;
					break;
				default:
					throw new IllegalArgumentException("Unknown payment method: " + paymentMethod);
			}

			WebElement paymentElement = driver.findElement(By.xpath(paymentLocator));
			paymentElement.click();
			LOGGER.info("Selected filter by payment method: " + paymentMethod);
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.severe("Failed to select filter by payment method " + paymentMethod + ": " + e.getMessage());
		}
	}

	/**
	 * Click Apply Filter button
	 */
	public void clickApplyFilter() {
		try {
			WebElement applyBtn = pageWait.until(ExpectedConditions.elementToBeClickable(By.xpath(APPLY_FILTER_BUTTON)));
			applyBtn.click();
			LOGGER.info("Clicked Apply Filter button");
			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.severe("Failed to click Apply Filter button: " + e.getMessage());
		}
	}

	/**
	 * Search transaction by ID
	 */
	public void searchTransaction(String transactionId) {
		try {
			WebElement searchInput = pageWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SEARCH_INPUT)));
			searchInput.clear();
			searchInput.sendKeys(transactionId);
			LOGGER.info("Searched for transaction ID: " + transactionId);
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.severe("Failed to search transaction ID " + transactionId + ": " + e.getMessage());
		}
	}

	/**
	 * Click search button (if exists)
	 */
	public void clickSearchButton() {
		try {
			WebElement searchBtn = driver.findElement(By.xpath("//button[@type='submit' or contains(@class, 'search')]"));
			searchBtn.click();
			LOGGER.info("Clicked search button");
			Thread.sleep(1000);
		} catch (Exception e) {
			// Search button might not exist (auto-search on input)
			LOGGER.fine("Search button not found or auto-search enabled");
		}
	}

	/**
	 * Clear search input
	 */
	public void clearSearch() {
		try {
			WebElement searchInput = driver.findElement(By.xpath(SEARCH_INPUT));
			searchInput.clear();
			LOGGER.info("Cleared search input");
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.warning("Failed to clear search input: " + e.getMessage());
		}
	}

	/**
	 * Check if "No data found" message is displayed
	 */
	public boolean isNoDataFoundMessageDisplayed() {
		try {
			return driver.findElement(By.xpath("//*[contains(text(), 'No data found') or contains(text(), 'No results') or contains(text(), 'No transactions')]")).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Click Download Invoice button
	 */
	public void clickDownloadInvoice() {
		try {
			WebElement downloadBtn = pageWait.until(ExpectedConditions.elementToBeClickable(By.xpath(DOWNLOAD_INVOICE_BUTTON)));
			downloadBtn.click();
			LOGGER.info("Clicked Download Invoice button");
			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.severe("Failed to click Download Invoice button: " + e.getMessage());
		}
	}

	/**
	 * Check if any transactions are displayed
	 */
	public boolean hasTransactions() {
		return getTransactionCount() > 0;
	}

	/**
	 * Wait for transactions to load
	 */
	public void waitForTransactionsToLoad() {
		try {
			pageWait.until(ExpectedConditions.presenceOfAllElementsLocated(By.xpath(TRANSACTION_CARD)));
			LOGGER.info("Transactions loaded successfully");
		} catch (Exception e) {
			LOGGER.warning("No transactions found: " + e.getMessage());
		}
	}
}
