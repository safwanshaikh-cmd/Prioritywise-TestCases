package pages;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;

import base.BasePage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Page object for Upload functionality. Handles book upload, chapter upload,
 * listing/search/delete, and the file-resolution helpers used by the upload
 * test data set. Follows the same conventions as {@code ChapterPage} /
 * {@code ConsumerBookDetailsPage}: no {@code By} fields are declared inside
 * tests, helpers return {@code boolean} / {@code String} / {@code int} /
 * {@code List<String>}, and assertions live in the test class.
 */
public class UploadPage extends BasePage {

	private final WebDriverWait pageWait;
	private final LoginPage login;
	private final DashboardPage dashboard;
	private final CreatorSettingsPage creatorSettings;
	private final ForCreatorPage forCreatorPage;

	// Locators
	private static final By UPLOAD_PAGE_READY = By.xpath(
			"//*[@data-testid='input_book_title' or @data-testid='input_book_author' or @data-testid='input_book_summary']"
					+ " | //*[contains(translate(normalize-space(.), 'UPLOAD CONTENT', 'upload content'), 'upload content')]"
					+ " | //*[contains(translate(normalize-space(.), 'ADD BOOK', 'add book'), 'add book')]");

	// Book upload form locators
	private static final By BOOK_TITLE_INPUT = By
			.xpath("//input[@placeholder='Book Title' or @name='title' or contains(@aria-label, 'title')]");
	private static final By BOOK_DESCRIPTION_INPUT = By.xpath(
			"//textarea[@placeholder='Description' or @name='description' or contains(@aria-label, 'description')]");
	private static final By BOOK_CATEGORY_SELECT = By.xpath(
			"//select[@name='category' or contains(@aria-label, 'category')] | //div[contains(@class, 'category')]");
	private static final By BOOK_LANGUAGE_SELECT = By.xpath(
			"//select[@name='language' or contains(@aria-label, 'language')] | //div[contains(@class, 'language')]");
	private static final By COVER_IMAGE_UPLOAD = By.xpath(
			"//input[@type='file' and @accept='image/*'] | //input[@name='cover' or contains(@aria-label, 'cover')]");
	private static final By BOOK_FILE_UPLOAD = By
			.xpath("//input[@type='file' and (contains(translate(@accept,'PDFMP3AUDIO','pdfmp3audio'),'pdf')"
					+ " or contains(translate(@accept,'PDFMP3AUDIO','pdfmp3audio'),'mp3')"
					+ " or contains(translate(@accept,'PDFMP3AUDIO','pdfmp3audio'),'audio'))]"
					+ " | //input[@type='file' and (contains(translate(@name,'FILEBOOKUPLOAD','filebookupload'),'file')"
					+ " or contains(translate(@name,'FILEBOOKUPLOAD','filebookupload'),'book')"
					+ " or contains(translate(@aria-label,'FILEBOOKUPLOAD','filebookupload'),'file')"
					+ " or contains(translate(@aria-label,'FILEBOOKUPLOAD','filebookupload'),'book'))]");
	private static final By GENERIC_FILE_INPUTS = By.cssSelector("input[type='file']");
	private static final By BOOK_FILE_UPLOAD_TRIGGER = By
			.xpath("//*[self::div or self::button or self::span][@tabindex='0' or self::button]"
					+ "[contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'upload')"
					+ " or contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'book')"
					+ " or contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'pdf')"
					+ " or contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'mp3')"
					+ " or contains(translate(normalize-space(.),'UPLOAD FILEBOOK PDFMP3SELECT','upload filebook pdfmp3select'),'file')]"
					+ "[not(contains(translate(normalize-space(.),'PORTRAIT LANDSCAPE IMAGE COVER','portrait landscape image cover'),'portrait'))"
					+ " and not(contains(translate(normalize-space(.),'PORTRAIT LANDSCAPE IMAGE COVER','portrait landscape image cover'),'landscape'))"
					+ " and not(contains(translate(normalize-space(.),'PORTRAIT LANDSCAPE IMAGE COVER','portrait landscape image cover'),'image'))"
					+ " and not(contains(translate(normalize-space(.),'PORTRAIT LANDSCAPE IMAGE COVER','portrait landscape image cover'),'cover'))]");
	private static final By SUBMIT_BUTTON = By
			.xpath("//button[contains(text(), 'Submit') or contains(text(), 'Upload') or @type='submit']");
	private static final By CANCEL_BUTTON = By
			.xpath("//button[contains(text(), 'Cancel') or contains(text(), 'Clear')]");

	// Chapter upload form locators
	private static final By CHAPTER_TITLE_INPUT = By.xpath(
			"//input[@placeholder='Chapter Title' or @name='chapterTitle' or contains(@aria-label, 'chapter title')]");
	private static final By CHAPTER_FILE_UPLOAD = By.xpath(
			"//input[@type='file' and @accept='audio/*'] | //input[@name='chapterFile' or contains(@aria-label, 'chapter')]");
	private static final By CHAPTER_SEQUENCE_INPUT = By
			.xpath("//input[@type='number' and @name='sequence' or contains(@aria-label, 'sequence')]");

	// Book listing locators

	private static final By BOOK_CARD = By.xpath("//div[contains(@class, 'book') or contains(@class, 'item')]");
	private static final By BOOK_TITLE_IN_CARD = By
			.xpath(".//*[contains(@class, 'title') or contains(@class, 'name')]");

	private static final By DELETE_BOOK_BUTTON = By
			.xpath(".//button[contains(text(), 'Delete') or contains(@class, 'delete')]");

	// Search and filter locators
	private static final By SEARCH_INPUT = By
			.xpath("//input[@placeholder='Search' or @type='search' or contains(@placeholder, 'search')]");
	private static final By CATEGORY_FILTER = By.xpath(
			"//select[@name='categoryFilter'] | //div[contains(@class, 'filter') and contains(@class, 'category')]");
	private static final By LANGUAGE_FILTER = By.xpath(
			"//select[@name='languageFilter'] | //div[contains(@class, 'filter') and contains(@class, 'language')]");
	private static final By CLEAR_FILTERS_BUTTON = By
			.xpath("//button[contains(text(), 'Clear') or contains(text(), 'Reset')]");

	// Validation message locators
	private static final By ERROR_MESSAGE = By.xpath(
			"//*[@data-testid='toastText1' or @data-testid='toastText2' or contains(@class, 'error') or contains(@class, 'invalid') or contains(@role, 'alert')]");
	private static final By SUCCESS_MESSAGE = By.xpath(
			"//*[@data-testid='toastText1' or @data-testid='toastText2' or contains(@data-testid,'toast') or contains(@class, 'success') or contains(@class, 'message')]");
	private static final By NO_DATA_MESSAGE = By.xpath(
			"//*[contains(text(), 'No data') or contains(text(), 'No results') or contains(text(), 'Not found')]");

	// Inline form validation error labels (one per form field). The error
	// labels carry a data-testid like text_<field>_error and are rendered in
	// red under each field when validation fails (e.g. text_language_error,
	// text_country_error, text_summary_error). This locator matches the
	// real DOM shape, unlike the broken `div.r-howw7u` selector used by
	// CreatorSettingsPage.getValidationMessages.
	private static final By INLINE_VALIDATION_ERRORS = By.xpath(
			"//*[contains(@data-testid,'text_') and contains(@data-testid,'_error')]");

	public UploadPage(WebDriver driver) {
		super(driver);
		this.pageWait = new WebDriverWait(driver, Duration.ofSeconds(15));
		this.login = new LoginPage(driver);
		this.dashboard = new DashboardPage(driver);
		this.creatorSettings = new CreatorSettingsPage(driver);
		this.forCreatorPage = new ForCreatorPage(driver);
	}

	// =================== Null-safe + wait helpers (mirrors ChapterPage / ConsumerBookDetailsPage) ===================

	/** Returns {@code ""} for null inputs. */
	public String safeString(String value) {
		return value == null ? "" : value;
	}

	/** Trims + lower-cases a string. Returns {@code ""} on null. */
	public String safeLowerUrl(String value) {
		return safeString(value).toLowerCase(Locale.ROOT);
	}

	/**
	 * Returns the current page URL lower-cased, or {@code ""} on failure.
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

	/**
	 * Open a new browser tab via JavaScript and switch the driver focus to it.
	 * Returns the new tab's window handle. Mirrors the pattern used in
	 * {@code ChapterPage.openSecondaryTab}.
	 */
	public String openNewTabAndSwitchToIt() {
		Objects.requireNonNull((JavascriptExecutor) driver).executeScript(
				"window.open('about:blank','_blank');");
		List<String> handles = new java.util.ArrayList<>(driver.getWindowHandles());
		String newHandle = handles.get(handles.size() - 1);
		driver.switchTo().window(newHandle);
		return newHandle;
	}

	// =================== Login helpers (moved from UploaderTests) ===================

	/**
	 * Log in as the uploader account. Credentials come from
	 * {@code config.properties} via {@link ConfigReader}; falls back to
	 * {@code login.validEmail} / {@code login.validPassword} when the uploader
	 * keys are absent. Throws {@link org.testng.SkipException} if login cannot
	 * complete, mirroring {@code ChapterPage.loginAsUploader}.
	 */
	public void loginAsUploader() {
		try {
			String email = ConfigReader.getProperty("uploader.email",
					ConfigReader.getProperty("login.validEmail"));
			String password = ConfigReader.getProperty("uploader.password",
					ConfigReader.getProperty("login.validPassword"));
			if (email == null || email.isBlank() || password == null || password.isBlank()) {
				throw new SkipException(
						"uploader.email / uploader.password missing from config.properties");
			}
			login.openLogin();
			login.loginUser(email, password);
			login.clickNextAfterLogin();
			// Wait for the URL to leave the login screen — match the chapter-page
			// pattern rather than guessing a route segment (the app lands on the
			// base URL after login, not a /home URL).
			boolean settled = wait.waitForFunction(currentDriver -> {
				String url = currentDriver.getCurrentUrl();
				if (url == null) {
					return false;
				}
				String lower = url.toLowerCase(Locale.ROOT);
				return !lower.contains("/login") && !lower.contains("signin");
			}, Duration.ofSeconds(30));
			if (!settled || login.isOnLoginPage()) {
				throw new IllegalStateException(
						"Uploader login flow did not move past the login page");
			}
			LoggerUtils.logInfo("Logged in as uploader");
		} catch (org.testng.SkipException e) {
			throw e;
		} catch (Exception e) {
			throw new org.testng.SkipException("Failed to login as uploader: "
					+ safeString(e.getMessage()), e);
		}
	}

	/**
	 * Log in as the consumer account. Used by access-control tests that need
	 * a non-uploader session.
	 */
	public void loginAsConsumer() {
		try {
			String email = ConfigReader.getProperty("consumer.email",
					ConfigReader.getProperty("login.validEmail"));
			String password = ConfigReader.getProperty("consumer.password",
					ConfigReader.getProperty("login.validPassword"));
			if (email == null || email.isBlank() || password == null || password.isBlank()) {
				throw new SkipException(
						"consumer.email / consumer.password missing from config.properties");
			}
			login.openLogin();
			login.loginUser(email, password);
			login.clickNextAfterLogin();
			dashboard.waitForPageReady();
			LoggerUtils.logInfo("Logged in as consumer");
		} catch (org.testng.SkipException e) {
			throw e;
		} catch (Exception e) {
			throw new org.testng.SkipException("Failed to login as consumer: "
					+ safeString(e.getMessage()), e);
		}
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
			LoggerUtils.logInfo("Upload page loaded successfully");
		} catch (Exception e) {
			LoggerUtils.logInfo("Upload page ready state not found: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("Entered book title: " + safeString(title));
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to enter book title: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("Entered book description");
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to enter book description: " + safeString(e.getMessage()));
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
				org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(
						categorySelect);
				select.selectByVisibleText(category);
			} else {
				categorySelect.click();
				WebElement option = driver.findElement(By.xpath("//*[contains(text(), '" + category + "')]"));
				option.click();
			}
			LoggerUtils.logInfo("Selected category: " + safeString(category));
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to select category: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Select book language
	 */
	public void selectBookLanguage(String language) {
		try {
			WebElement languageSelect = driver.findElement(BOOK_LANGUAGE_SELECT);
			if (languageSelect.getTagName().equals("select")) {
				org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(
						languageSelect);
				select.selectByVisibleText(language);
			} else {
				languageSelect.click();
				WebElement option = driver.findElement(By.xpath("//*[contains(text(), '" + language + "')]"));
				option.click();
			}
			LoggerUtils.logInfo("Selected language: " + safeString(language));
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to select language: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Upload cover image
	 */
	public void uploadCoverImage(String imagePath) {
		try {
			WebElement coverInput = driver.findElement(COVER_IMAGE_UPLOAD);
			coverInput.sendKeys(imagePath);
			LoggerUtils.logInfo("Uploaded cover image: " + safeString(imagePath));
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to upload cover image: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Upload book file (PDF/MP3)
	 */
	public void uploadBookFile(String filePath) {
		try {
			WebElement fileInput = findBookFileInput();
			fileInput.sendKeys(filePath);
			LoggerUtils.logInfo("Uploaded book file: " + safeString(filePath));
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to upload book file: " + safeString(e.getMessage()));
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
				Objects.requireNonNull((org.openqa.selenium.JavascriptExecutor) driver)
						.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", trigger);
				waitQuietly(500);

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

		throw new org.openqa.selenium.NoSuchElementException(
				"No suitable book file input was found on the upload form.");
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
			LoggerUtils.logInfo("Clicked Submit button");
			waitQuietly(2000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to click Submit button: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Click Cancel button
	 */
	public void clickCancelButton() {
		try {
			WebElement cancelBtn = driver.findElement(CANCEL_BUTTON);
			cancelBtn.click();
			LoggerUtils.logInfo("Clicked Cancel button");
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to click Cancel button: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("Entered chapter title: " + safeString(title));
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to enter chapter title: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Upload chapter audio file
	 */
	public void uploadChapterFile(String filePath) {
		try {
			WebElement fileInput = driver.findElement(CHAPTER_FILE_UPLOAD);
			fileInput.sendKeys(filePath);
			LoggerUtils.logInfo("Uploaded chapter file: " + safeString(filePath));
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to upload chapter file: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("Entered chapter sequence: " + sequence);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to enter chapter sequence: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("No uploaded books found: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("Could not get book title: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("Error checking book in list: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("Clicked delete button on first book");
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to delete book: " + safeString(e.getMessage()));
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
			LoggerUtils.logInfo("Searched for book: " + safeString(bookName));
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to search book: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Clear search
	 */
	public void clearSearch() {
		try {
			WebElement searchInput = driver.findElement(SEARCH_INPUT);
			searchInput.clear();
			LoggerUtils.logInfo("Cleared search input");
			waitQuietly(500);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to clear search: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Select category filter
	 */
	public void selectCategoryFilter(String category) {
		try {
			WebElement categoryFilter = driver.findElement(CATEGORY_FILTER);
			if (categoryFilter.getTagName().equals("select")) {
				org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(
						categoryFilter);
				select.selectByVisibleText(category);
			}
			LoggerUtils.logInfo("Selected category filter: " + safeString(category));
			waitQuietly(500);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to select category filter: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Select language filter
	 */
	public void selectLanguageFilter(String language) {
		try {
			WebElement languageFilter = driver.findElement(LANGUAGE_FILTER);
			if (languageFilter.getTagName().equals("select")) {
				org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(
						languageFilter);
				select.selectByVisibleText(language);
			}
			LoggerUtils.logInfo("Selected language filter: " + safeString(language));
			waitQuietly(500);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to select language filter: " + safeString(e.getMessage()));
		}
	}

	/**
	 * Click clear filters button
	 */
	public void clearFilters() {
		try {
			WebElement clearBtn = driver.findElement(CLEAR_FILTERS_BUTTON);
			clearBtn.click();
			LoggerUtils.logInfo("Cleared all filters");
			waitQuietly(1000);
		} catch (Exception e) {
			LoggerUtils.logInfo("Failed to clear filters: " + safeString(e.getMessage()));
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
	 * Return the visible inline form validation errors (e.g.
	 * "Language selection is required", "Summary is required") as a list of
	 * trimmed strings, in document order. Returns an empty list if no inline
	 * errors are rendered.
	 *
	 * <p>Uses the actual DOM shape
	 * ({@code data-testid='text_<field>_error'}) instead of the broken
	 * CSS selector used by {@code CreatorSettingsPage.getValidationMessages}.
	 */
	public List<String> getValidationErrors() {
		List<String> errors = new java.util.ArrayList<>();
		try {
			List<WebElement> elements = driver.findElements(INLINE_VALIDATION_ERRORS);
			for (WebElement element : elements) {
				try {
					String text = safeString(element.getText()).trim();
					if (!text.isEmpty() && element.isDisplayed()) {
						errors.add(text);
					}
				} catch (Exception ignored) {
					// Element may have re-rendered between read and check.
				}
			}
		} catch (Exception e) {
			LoggerUtils.logInfo("Could not read inline validation errors: " + safeString(e.getMessage()));
		}
		return errors;
	}

	/**
	 * Get success message
	 */
	public String getSuccessMessage() {
		try {
			// Wait for any toast message to appear
			waitQuietly(1500);
			WebElement successElement = pageWait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE));
			String rawText = successElement.getText();
			String text = rawText == null ? "" : rawText.trim();
			// Also try to get text from child elements
			if (text.isEmpty()) {
				String innerText = successElement.getAttribute("innerText");
				text = innerText == null ? "" : innerText.trim();
			}
			if (text.isEmpty()) {
				String textContent = successElement.getAttribute("textContent");
				text = textContent == null ? "" : textContent.trim();
			}
			// Log the toast once (the app renders several duplicate toast nodes;
			// they all carry the same text, so logging each is just noise).
			List<WebElement> toasts = driver.findElements(SUCCESS_MESSAGE);
			LoggerUtils.logInfo("Toast elements found: " + toasts.size()
					+ " | text: [" + text + "]");
			return text;
		} catch (Exception e) {
			LoggerUtils.logInfo("No success message found: " + safeString(e.getMessage()));
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

	// =================== Orchestration helpers (moved from UploaderTests) ===================

	/**
	 * Navigate from the post-login landing to the Upload page via the side
	 * menu (For Creators → Add Book). Throws {@link IllegalStateException}
	 * if the landing page does not stabilise within 30 seconds.
	 */
	public void navigateToUploadPage() {
		try {
			boolean landingReady = wait.waitForFunction(currentDriver -> {
				return dashboard.waitForDashboardShell() || dashboard.isOnCreatorPage()
						|| dashboard.isUploadPageOpened() || dashboard.isHeaderLogoVisible()
						|| dashboard.isProfileIconVisible();
			}, Duration.ofSeconds(30));
			if (!landingReady) {
				throw new IllegalStateException(
						"Uploader navigation: Post-login landing page should be stable. Current URL: "
								+ getCurrentUrlSafely());
			}
			LoggerUtils.logInfo("Uploader landing page is stable. Current URL: " + getCurrentUrlSafely());

			creatorSettings.clickHamburgerMenu();
			creatorSettings.clickForCreators();
			creatorSettings.clickAddBook();

			waitForUploadPageToLoad();
			if (!isUploadPageDisplayed() && !dashboard.isUploadPageOpened()) {
				throw new IllegalStateException(
						"Uploader navigation: Upload page should open after clicking Add Book");
			}
			LoggerUtils.logInfo("Upload page load wait completed");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Failed to navigate to Upload page: "
					+ safeString(e.getMessage()), e);
		}
	}

	/**
	 * Open the For Creators listing page directly via URL.
	 */
	public void openForCreatorsListingPage() {
		String baseUrl = ConfigReader.getProperty("url", "https://web-splay.acceses.com/");
		if (!baseUrl.endsWith("/")) {
			baseUrl = baseUrl + "/";
		}
		driver.get(baseUrl + "show_uploader_books");
		forCreatorPage.waitForListingState();
	}

	/**
	 * Open the For Creators listing page and click the first listed book's edit
	 * control. Throws {@link SkipException} when the listing is empty.
	 */
	public void openFirstListedBookFromForCreators() {
		openForCreatorsListingPage();
		if (!forCreatorPage.hasBooks()) {
			throw new SkipException("No listed books are available on the For Creators page.");
		}
		creatorSettings.clickEditFirstContent();
	}

	/**
	 * Open an "Automation Book" via the header search box.
	 */
	public void openAutomationBookFromHeaderSearch() {
		dashboard.waitForPageReady();
		if (!dashboard.isSearchBarVisible()) {
			throw new IllegalStateException("Header search bar should be visible for Automation Book search");
		}
		dashboard.submitSearch("The Golem");
		dashboard.printVisibleSearchResults();
		if (!dashboard.clickFirstSearchResult()) {
			throw new IllegalStateException(
					"Automation Book search should open a matching book details page from the header search");
		}
		if (!dashboard.isBookDetailsPageVisible()) {
			throw new IllegalStateException(
					"Automation Book search should land on the book details page");
		}
		dashboard.waitForBookDataToLoad();
	}

	/**
	 * Print the current book + chapter details to the test log. Used by
	 * debugging-oriented tests that verify visible metadata.
	 */
	public void printBookAndChapterDetailsForTest(String testCaseId) {
		LoggerUtils.logInfo("===== " + testCaseId + " Book And Chapter Details =====");
		dashboard.printCurrentBookDetails();
		LoggerUtils.logInfo("===== End " + testCaseId + " Book And Chapter Details =====");
	}

	/**
	 * Read the current book title from the edit form, polling until the title
	 * value is populated (the React Native Web edit form hydrates the title
	 * input a moment after the form chrome renders, so a single read can
	 * return empty even though the edit screen is fully open).
	 *
	 * <p>Returns the non-blank title within 10 seconds, or the last observed
	 * value if the form never populates (so the assertion message carries
	 * the actual empty title for debugging).
	 */
	public String getCurrentBookTitleAfterEdit() {
		long deadline = System.currentTimeMillis() + Duration.ofSeconds(10).toMillis();
		String lastSeen = "";
		while (System.currentTimeMillis() < deadline) {
			lastSeen = safeString(creatorSettings.getCurrentTitle()).trim();
			if (!lastSeen.isEmpty()) {
				return lastSeen;
			}
			waitQuietly(200);
		}
		return lastSeen;
	}

	/**
	 * Fill the upload-form fields with valid data sourced from
	 * {@code config.properties} (with sensible defaults). Configurable keys:
	 * {@code uploadLanguage}, {@code uploadCountryCategory},
	 * {@code uploadCategory}, {@code uploadCountry}, {@code uploadGenre}.
	 */
	public void fillValidBookDetails(String title, String summary) {
		creatorSettings.waitForUploadForm();
		creatorSettings.enterTitle(title);
		creatorSettings.enterAuthor("Automation Tester");
		creatorSettings.selectLanguage(ConfigReader.getProperty("uploadLanguage", "English"));
		creatorSettings.selectCountryCategory(ConfigReader.getProperty("uploadCountryCategory", "Category B"));
		creatorSettings.selectCategory(ConfigReader.getProperty("uploadCategory", "Art"));
		creatorSettings.selectCountry(ConfigReader.getProperty("uploadCountry", "India"));
		creatorSettings.selectGenre(ConfigReader.getProperty("uploadGenre", "Drama"));
		creatorSettings.enterSummary(summary);
	}

	/**
	 * Resolve portrait and landscape image paths and upload both. Throws
	 * {@link SkipException} when either image is missing.
	 */
	public void uploadValidPortraitAndLandscapeImages() {
		String portraitImagePath = resolvePortraitImagePath();
		String landscapeImagePath = resolveLandscapeImagePath();
		if (portraitImagePath.isBlank() || landscapeImagePath.isBlank()) {
			throw new SkipException(
					"Valid portrait and landscape JPG/PNG images are required via config or Downloads.");
		}
		creatorSettings.uploadBookImages(portraitImagePath, landscapeImagePath);
		if (!creatorSettings.getPortraitCoverError().isBlank()) {
			throw new IllegalStateException(
					"Valid portrait image should upload without portrait validation error");
		}
		if (!creatorSettings.getLandscapeCoverError().isBlank()) {
			throw new IllegalStateException(
					"Valid landscape image should upload without landscape validation error");
		}
	}

	/**
	 * Drive the upload form through to the Audio chapter section:
	 * fill valid book details → upload images → save → enter audio chapter
	 * preparation mode.
	 */
	public void createValidBookAndReachAudioSection(String title, String summary) {
		fillValidBookDetails(title, summary);
		uploadValidPortraitAndLandscapeImages();
		creatorSettings.clickSave();
		creatorSettings.prepareForAudioChapterCreation();
	}

	/**
	 * {@link #createValidBookAndReachAudioSection(String, String)} followed by
	 * opening the Add Audio popup.
	 */
	public void createValidBookAndOpenAddAudio(String title, String summary) {
		createValidBookAndReachAudioSection(title, summary);
		creatorSettings.clickAddAudio();
	}

	/**
	 * Full journey: log in → navigate to Upload → fill form → reach audio
	 * section → open the For Creators listing. Returns the book title used.
	 */
	public String createValidBookAndOpenForCreatorsListing(String title, String summary) {
		navigateToUploadPage();
		createValidBookAndReachAudioSection(title, summary);
		openForCreatorsListingPage();
		return title;
	}

	// =================== File resolution helpers (moved from UploaderTests) ===================

	/**
	 * Returns a unique book title of the form {@code "Automation Book XXXXXX"}.
	 */
	public String createUniqueBookTitle() {
		return "Automation Book " + UUID.randomUUID().toString().substring(0, 6);
	}

	/** Resolve a path from config, falling back to {@code user.dir}/{configKey}. */
	public String resolveOptionalConfiguredPath(String configKey) {
		String configuredPath = ConfigReader.getProperty(configKey);
		if (configuredPath == null || configuredPath.isBlank()) {
			return "";
		}
		Path directPath = Paths.get(configuredPath);
		if (Files.exists(directPath)) {
			return directPath.toString();
		}
		Path resolvedPath = Paths.get(System.getProperty("user.dir"), configuredPath);
		if (Files.exists(resolvedPath)) {
			return resolvedPath.toString();
		}
		throw new IllegalStateException("Configured file not found for " + configKey + ": "
				+ configuredPath);
	}

	/** Resolve a portrait image path: config → Downloads first .png/.jpg/.jpeg. */
	public String resolvePortraitImagePath() {
		String configured = resolveOptionalConfiguredPath("uploadPortraitImagePath");
		if (!configured.isBlank()) {
			return configured;
		}
		return findFirstFileInDownloads(List.of(".png", ".jpg", ".jpeg"),
				"Portrait image not configured. Continuing without image upload because no JPG/PNG was found in Downloads.");
	}

	/** Resolve a landscape image path: config → Downloads first .png/.jpg/.jpeg. */
	public String resolveLandscapeImagePath() {
		String configured = resolveOptionalConfiguredPath("uploadLandscapeImagePath");
		if (!configured.isBlank()) {
			return configured;
		}
		return findFirstFileInDownloads(List.of(".png", ".jpg", ".jpeg"),
				"Landscape image not configured. Continuing without landscape image because no JPG/PNG was found in Downloads.");
	}

	/** Resolve an audio upload file path: config → Downloads first .mp3/.wav/.m4a/.aac. */
	public String resolveAudioUploadFilePath() {
		String configured = resolveOptionalConfiguredPath("uploadAudioFilePath");
		if (!configured.isBlank()) {
			return configured;
		}
		return findFirstFileInDownloads(List.of(".mp3", ".wav", ".m4a", ".aac"),
				"Audio file not configured. Add uploadAudioFilePath or place an audio file in Downloads.");
	}

	/** Resolve an oversized audio file path: config → Downloads audio ≥ 500 MB → synthesise 501 MB MP3. */
	public String resolveLargeAudioUploadFilePath() {
		String configured = resolveOptionalConfiguredPath("uploadLargeAudioFilePath");
		if (!configured.isBlank()) {
			return configured;
		}

		Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
		if (Files.exists(downloadsDirectory)) {
			try {
				String discovered = Files.list(downloadsDirectory).filter(Files::isRegularFile)
						.filter(path -> hasAnyExtension(path.getFileName().toString(),
								List.of(".mp3", ".wav", ".m4a", ".aac")))
						.filter(path -> isFileAtLeast(path, 500L * 1024L * 1024L))
						.sorted(Comparator.comparing(Path::toString)).map(Path::toString).findFirst().orElse("");
				if (!discovered.isBlank()) {
					return discovered;
				}
			} catch (IOException e) {
				LoggerUtils.logInfo("Unable to scan Downloads for oversized audio file: "
						+ safeString(e.getMessage()));
			}
		}

		try {
			return createTemporaryLargeMp3File(501L * 1024L * 1024L).toString();
		} catch (IOException e) {
			LoggerUtils.logInfo("Unable to generate oversized MP3 test file: "
					+ safeString(e.getMessage()));
			return "";
		}
	}

	/** Resolve a large image path: config → Downloads first image ≥ 5 MB. */
	public String resolveLargeImagePath(String configKey) {
		String configured = resolveOptionalConfiguredPath(configKey);
		if (!configured.isBlank()) {
			return configured;
		}
		return findLargeImageInDownloads();
	}

	/** Resolve an invalid upload file path (.exe/.txt/.bat/.sh) for negative tests. */
	public String resolveInvalidUploadPath() {
		String configured = resolveOptionalConfiguredPath("invalidUploadPath");
		if (!configured.isBlank()) {
			return configured;
		}
		String existing = findFirstFileInDownloads(List.of(".exe", ".txt", ".bat", ".sh"), null);
		if (!existing.isBlank()) {
			return existing;
		}
		try {
			Path testExeFile = createDummyExeFile();
			LoggerUtils.logInfo("Generated dummy .exe file for testing: " + testExeFile);
			return testExeFile.toString();
		} catch (IOException e) {
			LoggerUtils.logInfo("Failed to generate dummy .exe file: " + safeString(e.getMessage()));
			return "";
		}
	}

	private String findLargeImageInDownloads() {
		Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
		if (!Files.exists(downloadsDirectory)) {
			return "";
		}
		try {
			return Files.list(downloadsDirectory).filter(Files::isRegularFile)
					.filter(path -> hasAnyExtension(path.getFileName().toString(), List.of(".png", ".jpg", ".jpeg")))
					.filter(path -> isFileAtLeast(path, 5L * 1024L * 1024L))
					.sorted(Comparator.comparing(Path::toString)).map(Path::toString).findFirst().orElse("");
		} catch (IOException e) {
			LoggerUtils.logInfo("Unable to scan Downloads for oversized image: "
					+ safeString(e.getMessage()));
			return "";
		}
	}

	private String findFirstFileInDownloads(List<String> extensions, String missingMessage) {
		Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
		if (!Files.exists(downloadsDirectory)) {
			LoggerUtils.logInfo("Downloads directory not found at: " + downloadsDirectory);
			return "";
		}
		try {
			return Files.list(downloadsDirectory).filter(Files::isRegularFile)
					.filter(path -> hasAnyExtension(path.getFileName().toString(), extensions))
					.sorted(Comparator.comparing(Path::toString)).map(Path::toString).findFirst().orElseGet(() -> {
						if (missingMessage != null) {
							LoggerUtils.logInfo(missingMessage);
						}
						return "";
					});
		} catch (IOException e) {
			LoggerUtils.logInfo("Unable to scan Downloads directory: " + safeString(e.getMessage()));
			return "";
		}
	}

	private boolean isFileAtLeast(Path path, long minBytes) {
		try {
			return Files.size(path) >= minBytes;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean hasAnyExtension(String fileName, List<String> extensions) {
		String normalized = fileName.toLowerCase(Locale.ROOT);
		for (String extension : extensions) {
			if (normalized.endsWith(extension.toLowerCase(Locale.ROOT))) {
				return true;
			}
		}
		return false;
	}

	private Path createDummyExeFile() throws IOException {
		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "selenium-test-files");
		Files.createDirectories(tempDir);
		Path exeFile = tempDir.resolve("test-invalid-file.exe");
		String dummyContent = "This is a dummy file for testing invalid file format validation.\n"
				+ "This file is not a real executable and is only used for negative testing.\n"
				+ "Test: " + UUID.randomUUID().toString();
		Files.writeString(exeFile, dummyContent);
		LoggerUtils.logInfo("Created dummy .exe file at: " + exeFile);
		return exeFile;
	}

	private Path createTemporaryLargeMp3File(long sizeInBytes) throws IOException {
		Path tempFile = Files.createTempFile("uploader-large-audio-", ".mp3");
		try (RandomAccessFile largeFile = new RandomAccessFile(tempFile.toFile(), "rw")) {
			largeFile.setLength(sizeInBytes);
		}
		tempFile.toFile().deleteOnExit();
		return tempFile;
	}
}
