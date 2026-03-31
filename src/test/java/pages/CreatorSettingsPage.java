package pages;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

/**
 * Page object for Creator Settings and Content Management.
 * Handles upload, edit, and content operations.
 */
public class CreatorSettingsPage {

	private static final Logger LOGGER = Logger.getLogger(CreatorSettingsPage.class.getName());

	private final WebDriver driver;
	private final WebDriverWait wait;
	private final JavascriptExecutor js;

	public CreatorSettingsPage(WebDriver driver) {
		if (driver == null)
			throw new IllegalArgumentException("WebDriver must not be null");
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		this.js = (JavascriptExecutor) driver;
	}

	// ================= LOCATORS =================
	private static final By EDIT_BUTTON_BY_TESTID = By.cssSelector("[data-testid^='button_edit_book_']");
	private static final By EDIT_BUTTON = By.xpath(
			"(//*[@data-testid='scroll_books_table']//*[self::div or self::span][normalize-space()='Edit']/ancestor::*[@tabindex='0'][1])[1]");
	private static final By TITLE_FIELD = By.cssSelector("[data-testid='input_book_title']");
	private static final By AUTHOR_FIELD = By.cssSelector("[data-testid='input_book_author']");
	private static final By SUMMARY_FIELD = By.cssSelector("[data-testid='input_book_summary']");

	// ================= NAVIGATION =================

	public void clickHamburgerMenu() {
		wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

		By HAMBURGER_MENU = By.xpath("//img[contains(@src,'ic_menu') and @draggable='false']");
		WebElement menu = wait.until(ExpectedConditions.presenceOfElementLocated(HAMBURGER_MENU));

		js.executeScript("arguments[0].click();", menu);
		LOGGER.log(Level.INFO, "Hamburger menu clicked successfully");
	}

	public void clickForCreators() {
		By FOR_CREATORS_MENU = By.xpath("//div[text()='For Creators']");

		for (int i = 0; i < 3; i++) {
			try {
				WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(FOR_CREATORS_MENU));
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
				js.executeScript("arguments[0].click();", el);
				LOGGER.info("For Creators clicked");
				return;
			} catch (Exception e) {
				LOGGER.warning("Retry For Creators: " + (i + 1));
				clickHamburgerMenu();
			}
		}
	}

	public void clickUploadContent() {
		By UPLOAD_CONTENT_MENU = By.xpath("//div[contains(text(),'Upload Content')]");
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(UPLOAD_CONTENT_MENU));
		js.executeScript("arguments[0].click();", element);
		LOGGER.log(Level.INFO, "Upload Content menu clicked");
	}

	public void clickTransactionHistory() {
		By TRANSACTION_HISTORY = By.xpath("//div[@data-testid='text_transaction_history']");
		WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(TRANSACTION_HISTORY));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
		js.executeScript("arguments[0].click();", el);
		LOGGER.log(Level.INFO, "Transaction History clicked");
	}

	// ================= UPLOAD FORM =================

	public void waitForUploadForm() {
		By TITLE_FIELD = By.cssSelector("[data-testid='input_book_title']");
		wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE_FIELD));
		LOGGER.log(Level.INFO, "Upload Content form loaded successfully");
	}

	public String getCurrentTitle() {
		By TITLE_FIELD = By.cssSelector("[data-testid='input_book_title']");
		WebElement titleInput = wait.until(ExpectedConditions.presenceOfElementLocated(TITLE_FIELD));
		String value = titleInput.getAttribute("value");
		return value == null ? "" : value.trim();
	}

	public void clickEditFirstContent() {
		WebElement editButton = findFirstVisibleEditButton();
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", editButton);

		try {
			wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", editButton);
		}

		// Wait for edit form to load
		wait.until(ExpectedConditions.or(
				ExpectedConditions.presenceOfElementLocated(TITLE_FIELD),
				ExpectedConditions.presenceOfElementLocated(AUTHOR_FIELD),
				ExpectedConditions.presenceOfElementLocated(SUMMARY_FIELD)
		));
		LOGGER.log(Level.INFO, "Edit button clicked and edit form loaded");
	}

	private WebElement findFirstVisibleEditButton() {
		// First try to find by data-testid
		List<WebElement> testIdMatches = driver.findElements(EDIT_BUTTON_BY_TESTID);
		for (WebElement candidate : testIdMatches) {
			try {
				if (candidate.isDisplayed()) {
					return candidate;
				}
			} catch (StaleElementReferenceException e) {
				// Retry with the next candidate
			}
		}

		// Fallback to XPath locator
		return wait.until(ExpectedConditions.elementToBeClickable(EDIT_BUTTON));
	}

	public void clickAddBook() {
		By ADD_BOOK_BUTTON = By.xpath("//div[contains(text(),'Add Book') or contains(text(),'+ Add Book')]");
		WebElement addBookButton = wait.until(ExpectedConditions.elementToBeClickable(ADD_BOOK_BUTTON));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", addBookButton);
		js.executeScript("arguments[0].click();", addBookButton);
		LOGGER.log(Level.INFO, "Add Book button clicked");
	}

	public void logVisibleWarnings() {
		By WARNING_ELEMENTS = By.xpath(
				"//*[contains(@data-testid,'error') or @data-testid='toastText1' or @data-testid='toastText2']");

		List<String> messages = new ArrayList<>();
		for (WebElement element : driver.findElements(WARNING_ELEMENTS)) {
			try {
				if (element.isDisplayed()) {
					String text = element.getText();
					if (text != null && !text.trim().isEmpty()) {
						messages.add(text.trim());
					}
				}
			} catch (Exception e) {
				// Ignore transient elements
			}
		}

		if (!messages.isEmpty()) {
			LOGGER.log(Level.INFO, "------ Visible Warnings ------");
			for (String msg : messages) {
				LOGGER.log(Level.INFO, msg);
			}
			LOGGER.log(Level.INFO, "------------------------------");
		} else {
			LOGGER.log(Level.INFO, "No visible warnings were captured");
		}
	}
}
