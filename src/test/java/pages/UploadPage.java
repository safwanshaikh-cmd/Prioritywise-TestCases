package pages;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;

/**
 * Page object for Upload functionality.
 * Handles book upload, chapter upload, and related operations.
 */
public class UploadPage extends BasePage {

	private final WebDriverWait pageWait;

	// Locators
	private static final By UPLOAD_PAGE_HEADING = By.xpath("//h1[contains(text(), 'Upload') or contains(text(), 'upload')]");

	// Book upload form locators
	private static final By BOOK_TITLE_INPUT = By.xpath("//input[@placeholder='Book Title' or @name='title' or contains(@aria-label, 'title')]");
	private static final By BOOK_DESCRIPTION_INPUT = By.xpath("//textarea[@placeholder='Description' or @name='description' or contains(@aria-label, 'description')]");
	private static final By BOOK_CATEGORY_SELECT = By.xpath("//select[@name='category' or contains(@aria-label, 'category')] | //div[contains(@class, 'category')]");
	private static final By BOOK_LANGUAGE_SELECT = By.xpath("//select[@name='language' or contains(@aria-label, 'language')] | //div[contains(@class, 'language')]");
	private static final By COVER_IMAGE_UPLOAD = By.xpath("//input[@type='file' and @accept='image/*'] | //input[@name='cover' or contains(@aria-label, 'cover')]");
	private static final By BOOK_FILE_UPLOAD = By.xpath("//input[@type='file' and @accept='.pdf,.mp3'] | //input[@name='file' or contains(@aria-label, 'file')]");
	private static final By SUBMIT_BUTTON = By.xpath("//button[contains(text(), 'Submit') or contains(text(), 'Upload') or @type='submit']");
	private static final By CANCEL_BUTTON = By.xpath("//button[contains(text(), 'Cancel') or contains(text(), 'Clear')]");

	// Chapter upload form locators
	private static final By CHAPTER_TITLE_INPUT = By.xpath("//input[@placeholder='Chapter Title' or @name='chapterTitle' or contains(@aria-label, 'chapter title')]");
	private static final By CHAPTER_FILE_UPLOAD = By.xpath("//input[@type='file' and @accept='audio/*'] | //input[@name='chapterFile' or contains(@aria-label, 'chapter')]");
	private static final By CHAPTER_SEQUENCE_INPUT = By.xpath("//input[@type='number' and @name='sequence' or contains(@aria-label, 'sequence')]");

	// Book listing locators
	private static final By BOOK_LIST_CONTAINER = By.xpath("//div[contains(@class, 'book-list') or contains(@class, 'uploaded-books')]");
	private static final By BOOK_CARD = By.xpath("//div[contains(@class, 'book') or contains(@class, 'item')]");
	private static final By BOOK_TITLE_IN_CARD = By.xpath(".//*[contains(@class, 'title') or contains(@class, 'name')]");
	private static final By BOOK_COVER_IMAGE = By.xpath(".//img[contains(@class, 'cover') or contains(@src, 'thumb')]");
	private static final By DELETE_BOOK_BUTTON = By.xpath(".//button[contains(text(), 'Delete') or contains(@class, 'delete')]");

	// Search and filter locators
	private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Search' or @type='search' or contains(@placeholder, 'search')]");
	private static final By CATEGORY_FILTER = By.xpath("//select[@name='categoryFilter'] | //div[contains(@class, 'filter') and contains(@class, 'category')]");
	private static final By LANGUAGE_FILTER = By.xpath("//select[@name='languageFilter'] | //div[contains(@class, 'filter') and contains(@class, 'language')]");
	private static final By CLEAR_FILTERS_BUTTON = By.xpath("//button[contains(text(), 'Clear') or contains(text(), 'Reset')]");

	// Validation message locators
	private static final By ERROR_MESSAGE = By.xpath("//*[contains(@class, 'error') or contains(@class, 'invalid') or contains(@role, 'alert')]");
	private static final By SUCCESS_MESSAGE = By.xpath("//*[contains(@class, 'success') or contains(@class, 'message')]");
	private static final By NO_DATA_MESSAGE = By.xpath("//*[contains(text(), 'No data') or contains(text(), 'No results') or contains(text(), 'Not found')]");

	public UploadPage(WebDriver driver) {
		super(driver);
		this.pageWait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	/**
	 * Check if Upload page is displayed
	 */
	public boolean isUploadPageDisplayed() {
		try {
			return pageWait.until(ExpectedConditions.visibilityOfElementLocated(UPLOAD_PAGE_HEADING)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Wait for upload page to load
	 */
	public void waitForUploadPageToLoad() {
		try {
			pageWait.until(ExpectedConditions.visibilityOfElementLocated(UPLOAD_PAGE_HEADING));
			LOGGER.info("Upload page loaded successfully");
		} catch (Exception e) {
			LOGGER.warning("Upload page heading not found: " + e.getMessage());
		}
	}

	// ================= BOOK UPLOAD METHODS =================

	/**
	 * Enter book title
	 */
	public void enterBookTitle(String title) {
		try {
			WebElement titleInput = pageWait.until(ExpectedConditions.visibilityOfElementLocated(BOOK_TITLE_INPUT));
			titleInput.clear();
			titleInput.sendKeys(title);
			LOGGER.info("Entered book title: " + title);
		} catch (Exception e) {
			LOGGER.severe("Failed to enter book title: " + e.getMessage());
		}
	}

	/**
	 * Enter book description
	 */
	public void enterBookDescription(String description) {
		try {
			WebElement descInput = driver.findElement(BOOK_DESCRIPTION_INPUT);
			descInput.clear();
			descInput.sendKeys(description);
			LOGGER.info("Entered book description");
		} catch (Exception e) {
			LOGGER.warning("Failed to enter book description: " + e.getMessage());
		}
	}

	/**
	 * Select book category
	 */
	public void selectBookCategory(String category) {
		try {
			WebElement categorySelect = driver.findElement(BOOK_CATEGORY_SELECT);
			// Handle both dropdown and clickable selection
			if (categorySelect.getTagName().equals("select")) {
				org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(categorySelect);
				select.selectByVisibleText(category);
			} else {
				categorySelect.click();
				WebElement option = driver.findElement(By.xpath("//*[contains(text(), '" + category + "')]"));
				option.click();
			}
			LOGGER.info("Selected category: " + category);
		} catch (Exception e) {
			LOGGER.warning("Failed to select category: " + e.getMessage());
		}
	}

	/**
	 * Select book language
	 */
	public void selectBookLanguage(String language) {
		try {
			WebElement languageSelect = driver.findElement(BOOK_LANGUAGE_SELECT);
			if (languageSelect.getTagName().equals("select")) {
				org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(languageSelect);
				select.selectByVisibleText(language);
			} else {
				languageSelect.click();
				WebElement option = driver.findElement(By.xpath("//*[contains(text(), '" + language + "')]"));
				option.click();
			}
			LOGGER.info("Selected language: " + language);
		} catch (Exception e) {
			LOGGER.warning("Failed to select language: " + e.getMessage());
		}
	}

	/**
	 * Upload cover image
	 */
	public void uploadCoverImage(String imagePath) {
		try {
			WebElement coverInput = driver.findElement(COVER_IMAGE_UPLOAD);
			coverInput.sendKeys(imagePath);
			LOGGER.info("Uploaded cover image: " + imagePath);
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.severe("Failed to upload cover image: " + e.getMessage());
		}
	}

	/**
	 * Upload book file (PDF/MP3)
	 */
	public void uploadBookFile(String filePath) {
		try {
			WebElement fileInput = driver.findElement(BOOK_FILE_UPLOAD);
			fileInput.sendKeys(filePath);
			LOGGER.info("Uploaded book file: " + filePath);
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.severe("Failed to upload book file: " + e.getMessage());
		}
	}

	/**
	 * Click Submit button
	 */
	public void clickSubmitButton() {
		try {
			WebElement submitBtn = pageWait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON));
			submitBtn.click();
			LOGGER.info("Clicked Submit button");
			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.severe("Failed to click Submit button: " + e.getMessage());
		}
	}

	/**
	 * Click Cancel button
	 */
	public void clickCancelButton() {
		try {
			WebElement cancelBtn = driver.findElement(CANCEL_BUTTON);
			cancelBtn.click();
			LOGGER.info("Clicked Cancel button");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.warning("Failed to click Cancel button: " + e.getMessage());
		}
	}

	// ================= CHAPTER UPLOAD METHODS =================

	/**
	 * Enter chapter title
	 */
	public void enterChapterTitle(String title) {
		try {
			WebElement titleInput = driver.findElement(CHAPTER_TITLE_INPUT);
			titleInput.clear();
			titleInput.sendKeys(title);
			LOGGER.info("Entered chapter title: " + title);
		} catch (Exception e) {
			LOGGER.warning("Failed to enter chapter title: " + e.getMessage());
		}
	}

	/**
	 * Upload chapter audio file
	 */
	public void uploadChapterFile(String filePath) {
		try {
			WebElement fileInput = driver.findElement(CHAPTER_FILE_UPLOAD);
			fileInput.sendKeys(filePath);
			LOGGER.info("Uploaded chapter file: " + filePath);
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.severe("Failed to upload chapter file: " + e.getMessage());
		}
	}

	/**
	 * Enter chapter sequence
	 */
	public void enterChapterSequence(int sequence) {
		try {
			WebElement seqInput = driver.findElement(CHAPTER_SEQUENCE_INPUT);
			seqInput.clear();
			seqInput.sendKeys(String.valueOf(sequence));
			LOGGER.info("Entered chapter sequence: " + sequence);
		} catch (Exception e) {
			LOGGER.warning("Failed to enter chapter sequence: " + e.getMessage());
		}
	}

	// ================= BOOK LISTING METHODS =================

	/**
	 * Get all uploaded books
	 */
	public List<WebElement> getUploadedBooks() {
		try {
			return driver.findElements(BOOK_CARD);
		} catch (Exception e) {
			LOGGER.warning("No uploaded books found: " + e.getMessage());
			return List.of();
		}
	}

	/**
	 * Get book count
	 */
	public int getBookCount() {
		return getUploadedBooks().size();
	}

	/**
	 * Get title of first book
	 */
	public String getFirstBookTitle() {
		try {
			WebElement firstBook = getUploadedBooks().get(0);
			WebElement titleElement = firstBook.findElement(BOOK_TITLE_IN_CARD);
			return titleElement.getText().trim();
		} catch (Exception e) {
			LOGGER.warning("Could not get book title: " + e.getMessage());
			return "";
		}
	}

	/**
	 * Check if book with given title exists in list
	 */
	public boolean isBookInList(String bookTitle) {
		try {
			List<WebElement> books = getUploadedBooks();
			for (WebElement book : books) {
				try {
					WebElement titleElement = book.findElement(BOOK_TITLE_IN_CARD);
					if (titleElement.getText().trim().equalsIgnoreCase(bookTitle)) {
						return true;
					}
				} catch (Exception e) {
					// Continue checking other books
				}
			}
		} catch (Exception e) {
			LOGGER.warning("Error checking book in list: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Delete first book in list
	 */
	public void deleteFirstBook() {
		try {
			WebElement firstBook = getUploadedBooks().get(0);
			WebElement deleteBtn = firstBook.findElement(DELETE_BOOK_BUTTON);
			deleteBtn.click();
			LOGGER.info("Clicked delete button on first book");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.warning("Failed to delete book: " + e.getMessage());
		}
	}

	// ================= SEARCH AND FILTER METHODS =================

	/**
	 * Search book by name
	 */
	public void searchBook(String bookName) {
		try {
			WebElement searchInput = pageWait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
			searchInput.clear();
			searchInput.sendKeys(bookName);
			LOGGER.info("Searched for book: " + bookName);
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.severe("Failed to search book: " + e.getMessage());
		}
	}

	/**
	 * Clear search
	 */
	public void clearSearch() {
		try {
			WebElement searchInput = driver.findElement(SEARCH_INPUT);
			searchInput.clear();
			LOGGER.info("Cleared search input");
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.warning("Failed to clear search: " + e.getMessage());
		}
	}

	/**
	 * Select category filter
	 */
	public void selectCategoryFilter(String category) {
		try {
			WebElement categoryFilter = driver.findElement(CATEGORY_FILTER);
			if (categoryFilter.getTagName().equals("select")) {
				org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(categoryFilter);
				select.selectByVisibleText(category);
			}
			LOGGER.info("Selected category filter: " + category);
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.warning("Failed to select category filter: " + e.getMessage());
		}
	}

	/**
	 * Select language filter
	 */
	public void selectLanguageFilter(String language) {
		try {
			WebElement languageFilter = driver.findElement(LANGUAGE_FILTER);
			if (languageFilter.getTagName().equals("select")) {
				org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(languageFilter);
				select.selectByVisibleText(language);
			}
			LOGGER.info("Selected language filter: " + language);
			Thread.sleep(500);
		} catch (Exception e) {
			LOGGER.warning("Failed to select language filter: " + e.getMessage());
		}
	}

	/**
	 * Click clear filters button
	 */
	public void clearFilters() {
		try {
			WebElement clearBtn = driver.findElement(CLEAR_FILTERS_BUTTON);
			clearBtn.click();
			LOGGER.info("Cleared all filters");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.warning("Failed to clear filters: " + e.getMessage());
		}
	}

	// ================= VALIDATION METHODS =================

	/**
	 * Get error message
	 */
	public String getErrorMessage() {
		try {
			WebElement errorElement = driver.findElement(ERROR_MESSAGE);
			return errorElement.getText().trim();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Get success message
	 */
	public String getSuccessMessage() {
		try {
			WebElement successElement = driver.findElement(SUCCESS_MESSAGE);
			return successElement.getText().trim();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Check if "No data found" message is displayed
	 */
	public boolean isNoDataFoundMessageDisplayed() {
		try {
			return driver.findElement(NO_DATA_MESSAGE).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check if any books are displayed
	 */
	public boolean hasBooks() {
		return getBookCount() > 0;
	}
}
