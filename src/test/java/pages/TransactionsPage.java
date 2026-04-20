package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utils.ConfigReader;

/**
 * Page object for Transactions page. Handles transaction-related operations
 * like viewing, searching, and invoice download.
 */
public class TransactionsPage extends BasePage {

	private final WebDriverWait pageWait;

	// Locators
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
	private static final String FILTER_BUTTON = "//*[@data-testid='button_open_filter' or @data-testid='text_filter_button' or normalize-space()='Filter By']";
	private static final String FILTER_POPUP = "//*[@data-testid='container_filter_overlay' or normalize-space()='Apply now' or normalize-space()='Successful' or normalize-space()='Cancelled' or normalize-space()='Refunded']";
	private static final String SEARCH_INPUT = "//input[@data-testid='input_search' or @placeholder='Transaction ID' or @type='search' or @placeholder='search' or contains(@placeholder, 'Search')]";

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
			pageWait.until(currentDriver -> isTransactionsPageDisplayed());
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
			String currentUrl = driver.getCurrentUrl();
			String safeUrl = currentUrl != null ? currentUrl.toLowerCase() : "";

			return isAnyTransactionLocatorVisible(TRANSACTIONS_SCREEN, TRANSACTIONS_HEADER)
					|| safeUrl.contains("transaction")
					|| isAnyTransactionLocatorVisible(TRANSACTIONS_HEADER, DOWNLOAD_INVOICE);

		} catch (Exception e) {
			return false;
		}
	}

	public void waitForTransactionsPageToLoad() {
		try {
			pageWait.until(currentDriver -> isTransactionsPageDisplayed() || hasTransactions());
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
				if (text.equalsIgnoreCase("Gold") || text.equalsIgnoreCase("Silver")
						|| text.equalsIgnoreCase("Bronze")) {
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
	 * Search transaction by ID
	 */
	public void searchTransaction(String transactionId) {
		try {
			WebElement searchInput = pageWait
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SEARCH_INPUT)));
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
			WebElement searchBtn = driver
					.findElement(By.xpath("//button[@type='submit' or contains(@class, 'search')]"));
			searchBtn.click();
			LOGGER.info("Clicked search button");
			Thread.sleep(1000);
		} catch (Exception e) {
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
			return driver.findElement(By.xpath(
					"//*[contains(text(), 'No data found') or contains(text(), 'No results') or contains(text(), 'No transactions')]"))
					.isDisplayed();
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
			pageWait.until(currentDriver -> hasTransactions());
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
}
