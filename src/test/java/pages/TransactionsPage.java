package pages;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utils.ConfigReader;

/**
 * Page object for Transactions page.
 * Handles all transaction-related operations like viewing, filtering, and searching.
 */
public class TransactionsPage extends BasePage {

	private final WebDriverWait pageWait;

	// Locators
	private static final By TRANSACTIONS_SCREEN = By.cssSelector("[data-testid='screen_transactions']");
	private static final By TRANSACTIONS_HEADER = By.xpath(
			"//div[@data-testid='text_transaction_history' and normalize-space()='Transaction History']"
					+ " | //div[@data-testid='text_title' and normalize-space()='Transactions']"
					+ " | //*[@data-testid='text_transaction_history']"
					+ " | //*[contains(normalize-space(),'Transaction History')]"
					+ " | //*[contains(normalize-space(),'Transactions')]");
	private static final By TRANSACTION_CARD = By.cssSelector("[data-testid='container_transaction_item']");
	private static final By SUBSCRIPTION_TYPE = By.xpath(
			"//*[@data-testid='container_transaction_item']//*[normalize-space()='Gold' or normalize-space()='Silver' or normalize-space()='Bronze' or contains(normalize-space(),'Month') or contains(normalize-space(),'payment')]");
	private static final By DOWNLOAD_INVOICE = By.cssSelector("[data-testid='button_download_invoice'], [data-testid='text_download_invoice']");
	private static final By FILTER_OVERLAY = By.cssSelector("[data-testid='container_filter_overlay']");
	private static final String FILTER_BUTTON = "//*[@data-testid='button_open_filter' or @data-testid='text_filter_button' or normalize-space()='Filter By']";
	private static final String FILTER_POPUP = "//*[@data-testid='container_filter_overlay' or @data-testid='button_apply_filters' or @data-testid='text_apply_filters' or normalize-space()='Apply now' or normalize-space()='Successful' or normalize-space()='Cancelled' or normalize-space()='Refunded']";
	private static final String SEARCH_INPUT = "//input[@data-testid='input_search' or @placeholder='Transaction ID' or @type='search' or @placeholder='search' or contains(@placeholder, 'Search')]";
	private static final String DOWNLOAD_INVOICE_BUTTON = "//*[@data-testid='button_download_invoice' or @data-testid='text_download_invoice' or contains(text(), 'Download') or contains(text(), 'Invoice')]";
	private static final String APPLY_FILTER_BUTTON = "//*[@data-testid='button_apply_filters' or @data-testid='text_apply_filters' or normalize-space()='Apply now' or normalize-space()='Apply Now']";

	// Filter locators
	private static final String FILTER_STATUS_SUCCESSFUL = "//*[@tabindex='0' or @role='button' or self::div][.//*[normalize-space()='Successful'] or normalize-space()='Successful']";
	private static final String FILTER_STATUS_CANCELLED = "//*[@tabindex='0' or @role='button' or self::div][.//*[normalize-space()='Cancelled'] or normalize-space()='Cancelled']";
	private static final String FILTER_STATUS_REFUNDED = "//*[@tabindex='0' or @role='button' or self::div][.//*[normalize-space()='Refunded'] or normalize-space()='Refunded']";
	private static final String FILTER_PAYMENT_METHOD_CARD = "//*[@tabindex='0' or @role='button' or self::div][.//*[contains(normalize-space(),'Card')] or contains(normalize-space(),'Card')]";
	private static final String FILTER_PAYMENT_METHOD_UPI = "//*[@tabindex='0' or @role='button' or self::div][.//*[contains(normalize-space(),'UPI')] or contains(normalize-space(),'UPI')]";
	private static final String FILTER_TAB_DATE = "//*[@data-testid='button_filter_tab'][.//*[@data-testid='text_filter_tab' and normalize-space()='Date'] or normalize-space()='Date']";
	private static final String FILTER_TAB_PAYMENT_METHOD = "//*[@data-testid='button_filter_tab'][.//*[@data-testid='text_filter_tab' and normalize-space()='Payment method'] or normalize-space()='Payment method']";
	private static final String FILTER_DATE_START = "//*[@tabindex='0' or @role='button' or self::div][.//*[normalize-space()='Start Date'] or normalize-space()='Start Date']";
	private static final String FILTER_DATE_END = "//*[@tabindex='0' or @role='button' or self::div][.//*[normalize-space()='End Date'] or normalize-space()='End Date']";
	private static final String CLEAR_STATUS_FILTER = "//*[normalize-space()='status']/following::*[@tabindex='0'][1]";
	private static final String CLOSE_FILTER_POPUP = "//*[normalize-space()='' or contains(@style,'ionicons')]";

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
			if (dashboard.isSimpleSideMenuOpen()) {
				dashboard.clickSimpleSideMenuItemAndCaptureUrl("transaction", "transactions", "transaction history",
						"payment history");
			} else {
				dashboard.clickSideMenuItemAndCaptureUrl("transaction", "transactions", "transaction history",
						"payment history");
			}
			pageWait.until(driver -> isTransactionsPageDisplayed());
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
			return isAnyTransactionLocatorVisible(TRANSACTIONS_SCREEN, TRANSACTIONS_HEADER)
					|| driver.getCurrentUrl().toLowerCase().contains("transaction")
					|| isAnyTransactionLocatorVisible(TRANSACTIONS_HEADER, DOWNLOAD_INVOICE);
		} catch (Exception e) {
			return false;
		}
	}

	public void waitForTransactionsPageToLoad() {
		try {
			pageWait.until(driver -> isTransactionsPageDisplayed() || hasTransactions());
		} catch (Exception e) {
			LOGGER.warning("Transactions page did not finish loading cleanly: " + e.getMessage());
		}
	}

	/**
	 * Get all transaction cards
	 */
	public List<WebElement> getTransactionCards() {
		try {
			List<WebElement> cards = driver.findElements(TRANSACTION_CARD);
			if (!cards.isEmpty()) {
				return cards;
			}
			return driver.findElements(SUBSCRIPTION_TYPE);
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
			for (WebElement element : firstCard.findElements(By.xpath(".//div[normalize-space()]"))) {
				String text = element.getText().trim();
				if (text.equalsIgnoreCase("Gold") || text.equalsIgnoreCase("Silver") || text.equalsIgnoreCase("Bronze")) {
					return text;
				}
			}
			return "";
		} catch (Exception e) {
			LOGGER.warning("Could not get plan from first transaction: " + e.getMessage());
			return "";
		}
	}

	public String getFirstTransactionDate() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			for (WebElement element : firstCard.findElements(By.xpath(".//div[normalize-space()]"))) {
				String text = element.getText().trim();
				if (text.matches("\\d{4}-\\d{2}-\\d{2}")) {
					return text;
				}
			}
			return "";
		} catch (Exception e) {
			LOGGER.warning("Could not get date from first transaction: " + e.getMessage());
			return "";
		}
	}

	public String getFirstTransactionTime() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			for (WebElement element : firstCard.findElements(By.xpath(".//div[normalize-space()]"))) {
				String text = element.getText().trim();
				if (text.matches("\\d{2}:\\d{2}:\\d{2}")) {
					return text;
				}
			}
			return "";
		} catch (Exception e) {
			LOGGER.warning("Could not get time from first transaction: " + e.getMessage());
			return "";
		}
	}

	public String getFirstTransactionAmount() {
		try {
			WebElement firstCard = getTransactionCards().get(0);
			for (WebElement element : firstCard.findElements(By.xpath(".//div[normalize-space()]"))) {
				String text = element.getText().trim();
				if (text.matches("\\d+\\.\\d{2}")) {
					return text;
				}
			}
			return "";
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
			for (WebElement element : firstCard.findElements(By.xpath(".//div[normalize-space()]"))) {
				String text = element.getText().trim();
				if (text.equalsIgnoreCase("Completed") || text.equalsIgnoreCase("Cancelled")
						|| text.equalsIgnoreCase("Refunded") || text.equalsIgnoreCase("Pending")
						|| text.equalsIgnoreCase("Processing")) {
					return text;
				}
			}
			return "";
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
			for (WebElement element : firstCard.findElements(By.xpath(".//div[normalize-space()]"))) {
				String text = element.getText().trim();
				if (text.equalsIgnoreCase("Card payment") || text.equalsIgnoreCase("UPI payment")
						|| text.toLowerCase().contains("payment")) {
					return text;
				}
			}
			return "";
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
			for (WebElement element : firstCard.findElements(By.xpath(".//div[normalize-space()]"))) {
				String text = element.getText().trim();
				if (text.matches("(?i)\\d+\\s+(day|days|week|weeks|month|months|year|years)")) {
					return text;
				}
			}
			return "";
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
			try {
				filterBtn.click();
			} catch (Exception clickException) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", filterBtn);
			}
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
			return isAnyTransactionLocatorVisible(FILTER_OVERLAY, By.xpath(FILTER_POPUP));
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
				case "successful":
					statusLocator = FILTER_STATUS_SUCCESSFUL;
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
			pageWait.until(ExpectedConditions.visibilityOfElementLocated(FILTER_OVERLAY));
			WebElement statusElement = pageWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(statusLocator)));
			try {
				statusElement.click();
			} catch (Exception clickException) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", statusElement);
			}
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
			pageWait.until(ExpectedConditions.visibilityOfElementLocated(FILTER_OVERLAY));
			List<WebElement> applyButtons = driver.findElements(By.xpath(APPLY_FILTER_BUTTON));
			if (applyButtons.isEmpty()) {
				LOGGER.info("Apply Filter button not present; filter appears to auto-apply");
				return;
			}
			WebElement applyBtn = applyButtons.get(0);
			try {
				applyBtn.click();
			} catch (Exception clickException) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyBtn);
			}
			LOGGER.info("Clicked Apply Filter button");
			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.severe("Failed to click Apply Filter button: " + e.getMessage());
		}
	}

	public void openDateFilterTab() {
		try {
			WebElement dateTab = pageWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(FILTER_TAB_DATE)));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateTab);
			LOGGER.info("Opened Date filter tab");
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.severe("Failed to open Date filter tab: " + e.getMessage());
		}
	}

	public void openPaymentMethodFilterTab() {
		try {
			WebElement paymentTab = pageWait.until(
					ExpectedConditions.visibilityOfElementLocated(By.xpath(FILTER_TAB_PAYMENT_METHOD)));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", paymentTab);
			LOGGER.info("Opened Payment method filter tab");
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.severe("Failed to open Payment method filter tab: " + e.getMessage());
		}
	}

	public boolean isStartDateFilterVisible() {
		return isAnyTransactionLocatorVisible(By.xpath(FILTER_DATE_START));
	}

	public boolean isEndDateFilterVisible() {
		return isAnyTransactionLocatorVisible(By.xpath(FILTER_DATE_END));
	}

	public void clearStatusFilter() {
		try {
			WebElement clearButton = pageWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CLEAR_STATUS_FILTER)));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", clearButton);
			LOGGER.info("Cleared status filter");
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.warning("Failed to clear status filter: " + e.getMessage());
		}
	}

	public void closeFilterPopup() {
		try {
			List<WebElement> closeIcons = driver.findElements(By.xpath(CLOSE_FILTER_POPUP));
			if (closeIcons.isEmpty()) {
				LOGGER.info("Close filter popup icon not found");
				return;
			}
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeIcons.get(0));
			LOGGER.info("Closed filter popup");
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.warning("Failed to close filter popup: " + e.getMessage());
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
			WebElement downloadBtn = pageWait.until(ExpectedConditions.elementToBeClickable(DOWNLOAD_INVOICE));
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
		try {
			return isAnyTransactionLocatorVisible(SUBSCRIPTION_TYPE, DOWNLOAD_INVOICE) || getTransactionCount() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Wait for transactions to load
	 */
	public void waitForTransactionsToLoad() {
		try {
			pageWait.until(driver -> hasTransactions());
			LOGGER.info("Transactions loaded successfully");
		} catch (Exception e) {
			LOGGER.warning("No transactions found: " + e.getMessage());
		}
	}

	private boolean isAnyTransactionLocatorVisible(By... locators) {
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

	/**
	 * Wait for Transactions page to load after navigation
	 */
	public void waitForTransactionsPageToLoad() {
		try {
			pageWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TRANSACTIONS_HEADING)));
			LOGGER.info("Transactions page heading visible");
		} catch (Exception e) {
			LOGGER.warning("Transactions page heading not found: " + e.getMessage());
		}
	}

	/**
	 * Get all transaction statuses from displayed transactions
	 * @return List of status strings
	 */
	public java.util.List<String> getAllTransactionStatuses() {
		java.util.List<String> statuses = new java.util.ArrayList<>();
		try {
			List<WebElement> cards = getTransactionCards();
			for (WebElement card : cards) {
				try {
					WebElement statusElement = card.findElement(By.xpath(".//*[contains(@class, 'status') or contains(@class, 'badge')]"));
					String status = statusElement.getText().trim();
					if (!status.isEmpty()) {
						statuses.add(status);
					}
				} catch (Exception e) {
					LOGGER.warning("Could not get status from card: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			LOGGER.warning("Error getting all transaction statuses: " + e.getMessage());
		}
		return statuses;
	}
}
