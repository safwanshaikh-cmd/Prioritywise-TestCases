package pages;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final Logger LOGGER = Logger.getLogger(UploadPage.class.getName());
	private final WebDriverWait pageWait;

	// Locators
	private static final By UPLOAD_PAGE_READY = By.xpath(
			"//*[@data-testid='input_book_title' or @data-testid='input_book_author' or @data-testid='input_book_summary']"
					+ " | //*[contains(translate(normalize-space(.), 'UPLOAD CONTENT', 'upload content'), 'upload content')]"
					+ " | //*[contains(translate(normalize-space(.), 'ADD BOOK', 'add book'), 'add book')]");

	// Book upload form locators
	private static final By BOOK_TITLE_INPUT = By.xpath("//input[@placeholder='Book Title' or @name='title' or contains(@aria-label, 'title')]");
	private static final By BOOK_DESCRIPTION_INPUT = By.xpath("//textarea[@placeholder='Description' or @name='description' or contains(@aria-label, 'description')]");
	private static final By BOOK_CATEGORY_SELECT = By.xpath("//select[@name='category' or contains(@aria-label, 'category')] | //div[contains(@class, 'category')]");
	private static final By BOOK_LANGUAGE_SELECT = By.xpath("//select[@name='language' or contains(@aria-label, 'language')] | //div[contains(@class, 'language')]");
	private static final By COVER_IMAGE_UPLOAD = By.xpath("//input[@type='file' and @accept='image/*'] | //input[@name='cover' or contains(@aria-label, 'cover')]");
	private static final By BOOK_FILE_UPLOAD = By.xpath(
			"//input[@type='file' and (contains(translate(@accept,'PDFMP3AUDIO','pdfmp3audio'),'pdf')"
					+ " or contains(translate(@accept,'PDFMP3AUDIO','pdfmp3audio'),'mp3')"
					+ " or contains(translate(@accept,'PDFMP3AUDIO','pdfmp3audio'),'audio'))]"
					+ " | //input[@type='file' and (contains(translate(@name,'FILEBOOKUPLOAD','filebookupload'),'file')"
					+ " or contains(translate(@name,'FILEBOOKUPLOAD','filebookupload'),'book')"
					+ " or contains(translate(@aria-label,'FILEBOOKUPLOAD','filebookupload'),'file')"
					+ " or contains(translate(@aria-label,'FILEBOOKUPLOAD','filebookupload'),'book'))]");
	private static final By GENERIC_FILE_INPUTS = By.cssSelector("input[type='file']");
	private static final By BOOK_FILE_UPLOAD_TRIGGER = By.xpath(
			"//*[self::div or self::button or self::span][@tabindex='0' or self::button]"
					+ "[contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'upload')"
					+ " or contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'book')"
					+ " or contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'pdf')"
					+ " or contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'mp3')"
					+ " or contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'file')]"
					+ "[not(contains(translate(normalize-space(.),'PORTRAIT LANDSCAPE IMAGE COVER','portrait landscape image cover'),'portrait'))"
					+ " and not(contains(translate(normalize-space(.),'PORTRAIT LANDSCAPE IMAGE COVER','portrait landscape image cover'),'landscape'))"
					+ " and not(contains(translate(normalize-space(.),'PORTRAIT LANDSCAPE IMAGE COVER','portrait landscape image cover'),'image'))"
					+ " and not(contains(translate(normalize-space(.),'PORTRAIT LANDSCAPE IMAGE COVER','portrait landscape image cover'),'cover'))]");
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
	private static final By ERROR_MESSAGE = By.xpath(
			"//*[@data-testid='toastText1' or @data-testid='toastText2' or contains(@class, 'error') or contains(@class, 'invalid') or contains(@role, 'alert')]");
	private static final By SUCCESS_MESSAGE = By.xpath(
			"//*[@data-testid='toastText1' or @data-testid='toastText2' or contains(@data-testid,'toast') or contains(@class, 'success') or contains(@class, 'message')]");
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
			return pageWait.until(ExpectedConditions.visibilityOfElementLocated(UPLOAD_PAGE_READY)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Wait for upload page to load
	 */
	public void waitForUploadPageToLoad() {
		try {
			pageWait.until(ExpectedConditions.visibilityOfElementLocated(UPLOAD_PAGE_READY));
			LOGGER.info("Upload page loaded successfully");
		} catch (Exception e) {
			LOGGER.warning("Upload page ready state not found: " + e.getMessage());
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
			WebElement fileInput = findBookFileInput();
			fileInput.sendKeys(filePath);
			LOGGER.info("Uploaded book file: " + filePath);
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.severe("Failed to upload book file: " + e.getMessage());
			throw new RuntimeException("Unable to upload book file: " + filePath, e);
		}
	}

	private WebElement findBookFileInput() {
		List<WebElement> directMatches = driver.findElements(BOOK_FILE_UPLOAD);
		WebElement directInput = findUsableBookFileInput(directMatches);
		if (directInput != null) {
			return directInput;
		}

		List<WebElement> existingInputs = driver.findElements(GENERIC_FILE_INPUTS);
		List<WebElement> triggers = driver.findElements(BOOK_FILE_UPLOAD_TRIGGER);
		for (WebElement trigger : triggers) {
			try {
				if (!trigger.isDisplayed()) {
					continue;
				}
				((org.openqa.selenium.JavascriptExecutor) driver)
						.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", trigger);
				Thread.sleep(500);

				List<WebElement> refreshedInputs = driver.findElements(GENERIC_FILE_INPUTS);
				WebElement newInput = findNewestUsableBookFileInput(existingInputs, refreshedInputs);
				if (newInput != null) {
					return newInput;
				}
			} catch (Exception e) {
				// Try the next trigger.
			}
		}

		WebElement fallbackInput = findUsableBookFileInput(driver.findElements(GENERIC_FILE_INPUTS));
		if (fallbackInput != null) {
			return fallbackInput;
		}

		List<WebElement> allFileInputs = driver.findElements(GENERIC_FILE_INPUTS);
		if (allFileInputs.size() >= 3) {
			return allFileInputs.get(allFileInputs.size() - 1);
		}

		throw new org.openqa.selenium.NoSuchElementException("No suitable book file input was found on the upload form.");
	}

	private WebElement findNewestUsableBookFileInput(List<WebElement> previousInputs, List<WebElement> currentInputs) {
		for (int i = currentInputs.size() - 1; i >= 0; i--) {
			WebElement candidate = currentInputs.get(i);
			if (!previousInputs.contains(candidate) && isUsableBookFileInput(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	private WebElement findUsableBookFileInput(List<WebElement> candidates) {
		for (int i = candidates.size() - 1; i >= 0; i--) {
			WebElement candidate = candidates.get(i);
			if (isUsableBookFileInput(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	private boolean isUsableBookFileInput(WebElement input) {
		try {
			String accept = String.valueOf(input.getAttribute("accept")).toLowerCase();
			String name = String.valueOf(input.getAttribute("name")).toLowerCase();
			String ariaLabel = String.valueOf(input.getAttribute("aria-label")).toLowerCase();
			String testId = String.valueOf(input.getAttribute("data-testid")).toLowerCase();
			String id = String.valueOf(input.getAttribute("id")).toLowerCase();

			boolean looksLikeImageOnly = accept.contains("image");
			boolean looksLikeBookFile = accept.contains("pdf") || accept.contains("mp3") || accept.contains("audio")
					|| name.contains("file") || name.contains("book") || ariaLabel.contains("file")
					|| ariaLabel.contains("book") || testId.contains("file") || testId.contains("book")
					|| id.contains("file") || id.contains("book") || id.contains("pdf") || id.contains("audio");

			return !looksLikeImageOnly && looksLikeBookFile;
		} catch (Exception e) {
			return false;
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
			WebElement successElement = pageWait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE));
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
