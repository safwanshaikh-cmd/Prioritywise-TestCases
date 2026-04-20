package pages;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.Keys;

/**
 * Page object for Creator Settings and Content Management. Handles upload,
 * edit, and content operations.
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
	private static final By ADD_BOOK_BUTTON_BY_TESTID = By.cssSelector("[data-testid='button_add_book_desktop']");
	private static final By ADD_BOOK_BUTTON_BY_TEXT = By.xpath(
			"//*[self::div or self::span][@data-testid='text_add_book_desktop' or contains(normalize-space(),'Add Book')]/ancestor::*[@tabindex='0'][1]");
	private static final By HAMBURGER_MENU = By.xpath("//img[contains(@src,'ic_menu') and @draggable='false']");
	private static final By FOR_CREATORS_MENU = By.xpath("//div[text()='For Creators']");
	private static final By UPLOAD_CONTENT_MENU = By.xpath("//div[contains(text(),'Upload Content')]");
	private static final By TRANSACTION_HISTORY = By.xpath("//div[@data-testid='text_transaction_history']");
	private static final By TITLE_FIELD = By.cssSelector("[data-testid='input_book_title']");
	private static final By TITLE_FIELD_FALLBACK = By.xpath(
			"//input[@placeholder='Title' or @name='title' or @data-testid='input_title' or contains(@placeholder,'Title')]");
	private static final By AUTHOR_FIELD = By.cssSelector("[data-testid='input_book_author']");
	private static final By SUMMARY_FIELD = By.cssSelector("[data-testid='input_book_summary']");
	private static final By LANGUAGE_DROPDOWN = By.cssSelector("[data-testid='select_language']");
	private static final By SAVE_BUTTON = By.xpath("//div[text()='Save']");
	private static final By NEXT_BUTTON = By.xpath("//div[contains(text(),'Next') or @type='button' and contains(text(),'Next')] | //button[contains(text(),'Next')]");
	private static final By VALIDATION_MESSAGES = By.cssSelector("div.r-howw7u");
	private static final By COUNTRYCATEGORY_DROPDOWN = By.cssSelector("[data-testid='select_country_category']");
	private static final By CATEGORY = By.cssSelector("[data-testid='dropdown_category']");
	private static final By COUNTRY_DROPDOWN = By.cssSelector("[data-testid='dropdown_country']");
	private static final By CATEGORY_SEARCH_INPUT = By.xpath("//input[@placeholder='Search category name']");
	private static final By COUNTRY_SEARCH_INPUT = By.xpath("//input[@placeholder='Search country name']");
	private static final By SELECT_GENRE = By.cssSelector("[data-testid='select_genre']");
	private static final By SUMMARY_FIELD_UPLOAD = By.cssSelector("[data-testid='input_book_summary']");
	private static final By PORTRAIT_UPLOAD_BUTTON = By.cssSelector("[data-testid='button_upload_portrait_cover']");
	private static final By LANDSCAPE_UPLOAD_BUTTON = By.cssSelector("[data-testid='button_upload_landscape_cover']");
	private static final By IMAGE_FILE_UPLOAD = By
			.cssSelector("input[type='file'][accept*='image'], input[accept*='image'], input[type='file']");
	private static final By PORTRAIT_COVER_ERROR = By.cssSelector("[data-testid='text_portrait_cover_error']");
	private static final By LANDSCAPE_COVER_ERROR = By.cssSelector("[data-testid='text_landscape_cover_error']");
	private static final By WARNING_ELEMENTS = By.xpath(
			"//*[contains(@data-testid,'error') or @data-testid='toastText1' or @data-testid='toastText2']");
	private static final By AUDIO_UPLOAD_SCREEN = By.cssSelector("[data-testid='screen_upload_audio_file']");
	private static final By AUDIO_UPLOAD_TITLE = By.cssSelector("[data-testid='text_upload_audio_title']");
	private static final By AUDIO_MODAL_CONTENT = By.cssSelector("[data-testid='container_modal_content']");
	private static final By BACK_TO_SUMMARY_BUTTON = By.cssSelector("[data-testid='button_back_to_summary']");
	private static final By ADD_AUDIO_BUTTON = By.cssSelector("[data-testid='button_add_audio']");
	private static final By CHAPTER_NAME_INPUT = By.cssSelector("[data-testid='input_chapter_name']");
	private static final By CHAPTER_SUMMARY_INPUT = By.cssSelector("[data-testid='input_chapter_summary']");
	private static final By AUDIO_FILE_BUTTON = By.cssSelector("[data-testid='button_select_audio_file']");
	private static final By AUDIO_CHAPTER_SAVE_BUTTON = By.cssSelector("[data-testid='button_save_audio_chapter']");
	private static final By AUDIO_FILE_INPUT = By.cssSelector("input[type='file'][accept*='audio'], input[type='file']");
	private static final By AUDIO_CLOSE_BUTTON = By.xpath(
			"//*[@data-testid='container_modal_content']//*[self::button or @role='button' or @tabindex='0']"
					+ "[contains(translate(@aria-label,'CLOSE','close'),'close')"
					+ " or contains(translate(@data-testid,'CLOSE','close'),'close')"
					+ " or normalize-space()='×' or normalize-space()='✕' or normalize-space()='✖' or normalize-space()='X']");
	private static final By AUDIO_CANCEL_BUTTON = By.xpath(
			"//button[normalize-space()='Cancel'] | //div[@tabindex='0'][.//div[normalize-space()='Cancel']] | //*[normalize-space()='Cancel']/ancestor::*[@tabindex='0'][1]");

	// ================= NAVIGATION =================

	public void clickHamburgerMenu() {
		wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
		WebElement menu = wait.until(ExpectedConditions.presenceOfElementLocated(HAMBURGER_MENU));
		js.executeScript("arguments[0].click();", menu);
		LOGGER.log(Level.INFO, "Hamburger menu clicked successfully");
	}

	public void clickForCreators() {
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
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(UPLOAD_CONTENT_MENU));
		js.executeScript("arguments[0].click();", element);
		LOGGER.log(Level.INFO, "Upload Content menu clicked");
	}

	public void clickTransactionHistory() {
		WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(TRANSACTION_HISTORY));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
		js.executeScript("arguments[0].click();", el);
		LOGGER.log(Level.INFO, "Transaction History clicked");
	}

	// ================= UPLOAD FORM =================

	public void waitForUploadForm() {
		waitForTitleInput();
		LOGGER.log(Level.INFO, "Upload Content form loaded successfully");
	}

	public String getCurrentTitle() {
		WebElement titleInput = waitForTitleInput();
		String value = titleInput.getAttribute("value");
		return value == null ? "" : value.trim();
	}

	public String getCurrentSummary() {
		try {
			WebElement summaryInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SUMMARY_FIELD_UPLOAD));
			String value = summaryInput.getAttribute("value");
			return value == null ? "" : value.trim();
		} catch (Exception e) {
			return "";
		}
	}

	public void enterTitle(String title) {
		WebElement titleInput = waitForTitleInput();
		String safeTitle = title == null ? "" : title;

		// Scroll element into view
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", titleInput);

		// Wait a moment for scroll to complete
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// Click to focus with retry
		boolean clicked = false;
		for (int i = 0; i < 3; i++) {
			try {
				wait.until(ExpectedConditions.elementToBeClickable(titleInput)).click();
				clicked = true;
				break;
			} catch (Exception e) {
				try {
					js.executeScript("arguments[0].click();", titleInput);
					clicked = true;
					break;
				} catch (Exception ex) {
					// Retry after refreshing element
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
					titleInput = waitForTitleInput();
				}
			}
		}

		if (!clicked) {
			LOGGER.log(Level.WARNING, "Failed to click title input after 3 attempts, trying JavaScript fallback");
		}

		// Clear existing text with multiple approaches
		try {
			// Approach 1: Select all and delete
			titleInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
			// Approach 2: Clear method
			titleInput.clear();
			// Approach 3: Send backspaces to be thorough
			titleInput.sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Standard clear failed: " + e.getMessage() + ", using JavaScript");
		}

		// Enter new text with retry
		boolean textEntered = false;
		for (int attempt = 0; attempt < 3; attempt++) {
			try {
				titleInput.sendKeys(safeTitle);

				// Verify text was entered
				String enteredValue = titleInput.getAttribute("value");
				if (enteredValue != null && enteredValue.equals(safeTitle)) {
					textEntered = true;
					LOGGER.log(Level.INFO, "Title entered successfully: " + safeTitle);
					break;
				} else {
					LOGGER.log(Level.WARNING, "Title entry verification failed on attempt " + (attempt + 1) +
							". Expected: '" + safeTitle + "', Got: '" + enteredValue + "'");
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "sendKeys attempt " + (attempt + 1) + " failed: " + e.getMessage());
			}

			// Retry after refreshing element
			if (attempt < 2) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
				titleInput = waitForTitleInput();
			}
		}

		// JavaScript fallback if standard methods failed
		if (!textEntered) {
			LOGGER.log(Level.WARNING, "Standard sendKeys failed, using JavaScript fallback");
			js.executeScript("const el = arguments[0];"
					+ "const value = arguments[1] == null ? '' : arguments[1];"
					+ "el.focus();"
					+ "el.value = value;"
					+ "el.dispatchEvent(new Event('input', { bubbles: true }));"
					+ "el.dispatchEvent(new Event('change', { bubbles: true }));"
					+ "el.dispatchEvent(new Event('blur', { bubbles: true }));", titleInput, safeTitle);

			// Verify JavaScript fallback worked
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			String finalValue = titleInput.getAttribute("value");
			if (finalValue != null && finalValue.equals(safeTitle)) {
				LOGGER.log(Level.INFO, "Title entered via JavaScript: " + safeTitle);
			} else {
				LOGGER.log(Level.SEVERE, "Failed to enter title. Expected: '" + safeTitle + "', Got: '" + finalValue + "'");
			}
		}
	}

	public void enterAuthor(String author) {
		WebElement authorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AUTHOR_FIELD));
		authorInput.clear();
		authorInput.sendKeys(author == null ? "" : author);
		LOGGER.log(Level.INFO, "Entered book author");
	}

	public void enterSummary(String summary) {
		WebElement summaryInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SUMMARY_FIELD_UPLOAD));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", summaryInput);
		String safeSummary = summary == null ? "" : summary;

		try {
			wait.until(ExpectedConditions.elementToBeClickable(summaryInput)).click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", summaryInput);
		}

		try {
			summaryInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
			summaryInput.clear();
			if (safeSummary.length() > 4000) {
				js.executeScript(
						"const el = arguments[0]; const value = arguments[1];"
								+ "if ('value' in el) { el.value = value; }"
								+ "else { el.textContent = value; }"
								+ "el.dispatchEvent(new Event('input', { bubbles: true }));"
								+ "el.dispatchEvent(new Event('change', { bubbles: true }));",
						summaryInput, safeSummary);
			} else {
				summaryInput.sendKeys(safeSummary);
			}
		} catch (Exception e) {
			js.executeScript(
					"const el = arguments[0]; const value = arguments[1];"
							+ "if ('value' in el) { el.value = value; }"
							+ "else { el.textContent = value; }"
							+ "el.dispatchEvent(new Event('input', { bubbles: true }));"
							+ "el.dispatchEvent(new Event('change', { bubbles: true }));",
					summaryInput, safeSummary);
		}

		LOGGER.log(Level.INFO, "Entered book summary");
	}

	public void selectLanguage(String language) {
		Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(LANGUAGE_DROPDOWN)));
		try {
			select.selectByVisibleText(language);
			LOGGER.log(Level.INFO, "Language selected: {0}", language);
		} catch (NoSuchElementException e) {
			String fallback = selectFirstEnabledOption(select);
			LOGGER.log(Level.INFO, "Language option {0} not found. Selected fallback option: {1}",
					new Object[] { language, fallback });
		}
	}

	public void selectCountryCategory(String countryCategory) {
		Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(COUNTRYCATEGORY_DROPDOWN)));
		try {
			select.selectByVisibleText(countryCategory);
			LOGGER.log(Level.INFO, "Country Category selected: {0}", countryCategory);
		} catch (NoSuchElementException e) {
			String fallback = selectFirstEnabledOption(select);
			LOGGER.log(Level.INFO, "Country Category option {0} not found. Selected fallback option: {1}",
					new Object[] { countryCategory, fallback });
		}
	}

	public void selectCategory(String category) {
		String selectedCategory = selectCustomDropdownOption(CATEGORY, category, "Select Category", CATEGORY_SEARCH_INPUT);
		LOGGER.log(Level.INFO, "Category selected: {0}", selectedCategory);
	}

	public void selectCountry(String country) {
		String selectedCountry = selectCustomDropdownOption(COUNTRY_DROPDOWN, country, "Select Country",
				COUNTRY_SEARCH_INPUT);
		LOGGER.log(Level.INFO, "Country selected: {0}", selectedCountry);
	}

	public void selectGenre(String genre) {
		Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(SELECT_GENRE)));
		try {
			select.selectByVisibleText(genre);
			LOGGER.log(Level.INFO, "Genre selected: {0}", genre);
		} catch (NoSuchElementException e) {
			String fallback = selectFirstEnabledOption(select);
			LOGGER.log(Level.INFO, "Genre option {0} not found. Selected fallback option: {1}",
					new Object[] { genre, fallback });
		}
	}

	public void uploadBookImages(String portraitImagePath, String landscapeImagePath) {
		if (uploadBookImagesThroughInputs(portraitImagePath, landscapeImagePath)) {
			LOGGER.log(Level.INFO, "Images uploaded successfully through file inputs");
			return;
		}

		uploadImageThroughButton(PORTRAIT_UPLOAD_BUTTON, portraitImagePath, "portrait");
		uploadImageThroughButton(LANDSCAPE_UPLOAD_BUTTON, landscapeImagePath, "landscape");
		LOGGER.log(Level.INFO, "Images uploaded successfully");
	}


	public void uploadBookFile(String filePath) {
		try {
			// Look for file input that accepts PDF or audio files
			List<WebElement> fileInputs = driver.findElements(By.xpath(
					"//input[@type='file' and (contains(@accept,'pdf') or contains(@accept,'application/pdf') or contains(@accept,'audio') or contains(@accept,'mp3'))]"));

			if (!fileInputs.isEmpty() && fileInputs.get(0).isDisplayed()) {
				fileInputs.get(0).sendKeys(filePath);
				LOGGER.log(Level.INFO, "Book file uploaded via direct file input: " + filePath);
				return;
			}

			// Fallback to generic file input
			List<WebElement> genericInputs = driver.findElements(By.cssSelector("input[type='file']"));
			for (WebElement input : genericInputs) {
				try {
					if (input.isDisplayed()) {
						input.sendKeys(filePath);
						LOGGER.log(Level.INFO, "Book file uploaded via generic file input: " + filePath);
						return;
					}
				} catch (Exception e) {
					// Try next input
				}
			}

			LOGGER.warning("No suitable file input found for book file upload");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to upload book file: " + e.getMessage());
			throw new RuntimeException("Unable to upload book file: " + filePath, e);
		}
	}


	public boolean hasFileUploadInput() {
		try {
			// Look for file input that accepts PDF or audio files
			List<WebElement> fileInputs = driver.findElements(By.xpath(
					"//input[@type='file' and (contains(@accept,'pdf') or contains(@accept,'application/pdf') or contains(@accept,'audio') or contains(@accept,'mp3'))]"));

			for (WebElement input : fileInputs) {
				if (input.isDisplayed()) {
					LOGGER.log(Level.INFO, "File upload input found on edit page");
					return true;
				}
			}

			// Check for any visible file input
			List<WebElement> allFileInputs = driver.findElements(By.cssSelector("input[type='file']"));
			for (WebElement input : allFileInputs) {
				try {
					if (input.isDisplayed()) {
						LOGGER.log(Level.INFO, "Generic file upload input found on edit page");
						return true;
					}
				} catch (Exception e) {
					// Element not accessible
				}
			}

			LOGGER.log(Level.INFO, "No file upload input found on edit page (metadata editing only)");
			return false;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error checking for file upload input: " + e.getMessage());
			return false;
		}
	}


	public void clickSave() {
		for (int i = 0; i < 3; i++) {
			try {
				WebElement saveBtn = wait.until(ExpectedConditions.presenceOfElementLocated(SAVE_BUTTON));
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", saveBtn);

				try {
					wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
				} catch (Exception e) {
					js.executeScript("arguments[0].click();", saveBtn);
				}

				LOGGER.info("Save button clicked");
				return;
			} catch (Exception e) {
				LOGGER.warning("Retry Save click: " + (i + 1));
				try {
					js.executeScript("document.body.click();");
				} catch (Exception ignore) {
					// ignore
				}
			}
		}

		throw new RuntimeException("Unable to click Save button");
	}

	public void clickNext() {
		try {
			WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", nextBtn);

			try {
				nextBtn.click();
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", nextBtn);
			}

			LOGGER.log(Level.INFO, "Next button clicked successfully");
		} catch (TimeoutException e) {
			LOGGER.log(Level.WARNING, "Next button not found or not clickable - may not be applicable in current state");
			throw new RuntimeException("Next button not found or not clickable", e);
		}
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
		wait.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(TITLE_FIELD),
				ExpectedConditions.presenceOfElementLocated(AUTHOR_FIELD),
				ExpectedConditions.presenceOfElementLocated(SUMMARY_FIELD)));
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
		WebElement addBookButton = findAddBookButton();
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", addBookButton);
		js.executeScript("arguments[0].click();", addBookButton);
		LOGGER.log(Level.INFO, "Add Book button clicked");
	}

	private WebElement findAddBookButton() {
		List<WebElement> testIdMatches = driver.findElements(ADD_BOOK_BUTTON_BY_TESTID);
		for (WebElement candidate : testIdMatches) {
			try {
				if (candidate.isDisplayed() && candidate.isEnabled()) {
					return wait.until(ExpectedConditions.elementToBeClickable(candidate));
				}
			} catch (StaleElementReferenceException e) {
				// Retry with the next candidate.
			}
		}

		return wait.until(ExpectedConditions.elementToBeClickable(ADD_BOOK_BUTTON_BY_TEXT));
	}

	public void logVisibleWarnings() {
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

	public List<String> getValidationMessages() {
		List<String> messages = new ArrayList<>();
		wait.until(driver -> !driver.findElements(VALIDATION_MESSAGES).isEmpty());

		for (int attempt = 0; attempt < 3; attempt++) {
			messages.clear();
			try {
				List<WebElement> elements = driver.findElements(VALIDATION_MESSAGES);
				for (WebElement element : elements) {
					if (!element.isDisplayed()) {
						continue;
					}
					String text = element.getText().trim();
					if (!text.isEmpty()) {
						messages.add(text);
					}
				}
				if (!messages.isEmpty()) {
					return messages;
				}
			} catch (StaleElementReferenceException e) {
				// Re-read the validation nodes after the form rerenders.
			}
		}

		return messages;
	}

	public List<String> getValidationMessagesIfPresent() {
		List<String> messages = new ArrayList<>();
		for (int attempt = 0; attempt < 3; attempt++) {
			messages.clear();
			try {
				List<WebElement> elements = driver.findElements(VALIDATION_MESSAGES);
				if (elements.isEmpty()) {
					return messages;
				}
				for (WebElement element : elements) {
					if (!element.isDisplayed()) {
						continue;
					}
					String text = element.getText().trim();
					if (!text.isEmpty()) {
						messages.add(text);
					}
				}
				if (!messages.isEmpty()) {
					return messages;
				}
			} catch (StaleElementReferenceException e) {
				// Re-read the validation nodes after the form rerenders.
			}
		}

		return messages;
	}

	public List<String> getImageUploadErrors() {
		List<String> messages = new ArrayList<>();
		String portraitError = getVisibleErrorText(PORTRAIT_COVER_ERROR);
		if (!portraitError.isBlank()) {
			messages.add(portraitError);
		}

		String landscapeError = getVisibleErrorText(LANDSCAPE_COVER_ERROR);
		if (!landscapeError.isBlank() && !messages.contains(landscapeError)) {
			messages.add(landscapeError);
		}

		return messages;
	}

	public String getPortraitCoverError() {
		return getVisibleErrorText(PORTRAIT_COVER_ERROR);
	}

	public String getLandscapeCoverError() {
		return getVisibleErrorText(LANDSCAPE_COVER_ERROR);
	}

	public String waitForPortraitCoverError() {
		try {
			return new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver -> {
				String error = getVisibleErrorText(PORTRAIT_COVER_ERROR);
				return error == null || error.isBlank() ? null : error;
			});
		} catch (TimeoutException e) {
			return getPortraitCoverError();
		}
	}

	public void prepareForAudioChapterCreation() {
		waitForPostBookSaveState();
		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(ADD_AUDIO_BUTTON),
				ExpectedConditions.visibilityOfElementLocated(AUDIO_UPLOAD_SCREEN),
				ExpectedConditions.visibilityOfElementLocated(AUDIO_UPLOAD_TITLE)));
		LOGGER.log(Level.INFO, "Audio section prepared without reopening book details");
	}

	public void clickAddAudio() {
		WebElement addAudio = wait.until(ExpectedConditions.elementToBeClickable(ADD_AUDIO_BUTTON));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", addAudio);
		js.executeScript("arguments[0].click();", addAudio);
		LOGGER.log(Level.INFO, "Add Audio button clicked");
	}

	public boolean isChapterFormVisible() {
		return isDisplayed(CHAPTER_NAME_INPUT) || isDisplayed(CHAPTER_SUMMARY_INPUT)
				|| isDisplayed(AUDIO_CHAPTER_SAVE_BUTTON);
	}

	public boolean isBookDetailsFormVisible() {
		return isDisplayed(TITLE_FIELD) || isDisplayed(TITLE_FIELD_FALLBACK) || isDisplayed(AUTHOR_FIELD)
				|| isDisplayed(SUMMARY_FIELD_UPLOAD);
	}

	public void clickBackToSummaryFromAudio() {
		WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(BACK_TO_SUMMARY_BUTTON));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", backButton);
		js.executeScript("arguments[0].click();", backButton);
		LOGGER.log(Level.INFO, "Returned from Add Audio to summary");
	}

	public void cancelAddAudioPopup() {
		try {
			List<WebElement> closeButtons = driver.findElements(AUDIO_CLOSE_BUTTON);
			for (WebElement closeButton : closeButtons) {
				try {
					if (!closeButton.isDisplayed()) {
						continue;
					}
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", closeButton);
					js.executeScript("arguments[0].click();", closeButton);
					wait.until(driver -> !isChapterFormVisible());
					LOGGER.log(Level.INFO, "Cancelled Add Audio popup using close X button");
					return;
				} catch (StaleElementReferenceException e) {
					// Retry with the next candidate.
				}
			}

			List<WebElement> cancelButtons = driver.findElements(AUDIO_CANCEL_BUTTON);
			for (WebElement cancelButton : cancelButtons) {
				try {
					if (!cancelButton.isDisplayed()) {
						continue;
					}
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", cancelButton);
					js.executeScript("arguments[0].click();", cancelButton);
					wait.until(driver -> !isChapterFormVisible());
					LOGGER.log(Level.INFO, "Cancelled Add Audio popup using Cancel button");
					return;
				} catch (StaleElementReferenceException e) {
					// Retry with the next candidate.
				}
			}

			driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
			wait.until(driver -> !isChapterFormVisible() || driver.findElements(AUDIO_MODAL_CONTENT).isEmpty());
			LOGGER.log(Level.INFO, "Cancelled Add Audio popup using Escape fallback");
		} catch (Exception e) {
			throw new RuntimeException("Unable to cancel Add Audio popup", e);
		}
	}

	public boolean isAddAudioButtonVisible() {
		return isDisplayed(ADD_AUDIO_BUTTON);
	}

	public void waitForAudioUploadScreen() {
		wait.until(driver -> {
			boolean addAudioVisible = isDisplayed(ADD_AUDIO_BUTTON);
			boolean screenVisible = isDisplayed(AUDIO_UPLOAD_SCREEN);
			boolean titleVisible = isDisplayed(AUDIO_UPLOAD_TITLE);
			return addAudioVisible || screenVisible || titleVisible;
		});
		LOGGER.log(Level.INFO, "Audio upload screen is visible");
	}

	public void prepareChapterSectionFromListedBook() {
		if (isBookDetailsFormVisible() && !isAddAudioButtonVisible() && !isChapterFormVisible()) {
			clickSave();
		}
		prepareForAudioChapterCreation();
	}

	public void enterChapterName(String chapterName) {
		WebElement chapterNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CHAPTER_NAME_INPUT));
		chapterNameInput.clear();
		chapterNameInput.sendKeys(chapterName);
		LOGGER.log(Level.INFO, "Chapter name entered: {0}", chapterName);
	}

	public String getCurrentChapterName() {
		try {
			WebElement chapterNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CHAPTER_NAME_INPUT));
			String value = chapterNameInput.getAttribute("value");
			return value == null ? "" : value.trim();
		} catch (Exception e) {
			return "";
		}
	}

	public void enterChapterSummary(String summary) {
		WebElement chapterSummaryInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CHAPTER_SUMMARY_INPUT));
		chapterSummaryInput.clear();
		chapterSummaryInput.sendKeys(summary);
		LOGGER.log(Level.INFO, "Chapter summary entered");
	}

	public String getCurrentChapterSummary() {
		try {
			WebElement chapterSummaryInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CHAPTER_SUMMARY_INPUT));
			String value = chapterSummaryInput.getAttribute("value");
			return value == null ? "" : value.trim();
		} catch (Exception e) {
			return "";
		}
	}

	public void uploadAudioFile(String filePath) {
		uploadGenericFile(AUDIO_FILE_BUTTON, AUDIO_FILE_INPUT, filePath, "audio");
	}

	public void saveAudioChapter() {
		WebElement saveAudio = wait.until(ExpectedConditions.elementToBeClickable(AUDIO_CHAPTER_SAVE_BUTTON));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", saveAudio);
		js.executeScript("arguments[0].click();", saveAudio);
		LOGGER.log(Level.INFO, "Audio chapter save clicked");
	}

	private WebElement waitForTitleInput() {
		return wait.until(driver -> {
			WebElement titleInput = firstDisplayedElement(TITLE_FIELD, TITLE_FIELD_FALLBACK);
			return titleInput != null ? titleInput : null;
		});
	}

	private WebElement firstDisplayedElement(By... locators) {
		for (By locator : locators) {
			List<WebElement> elements = driver.findElements(locator);
			for (WebElement element : elements) {
				try {
					if (element.isDisplayed()) {
						return element;
					}
				} catch (StaleElementReferenceException e) {
					// Retry with the next candidate.
				}
			}
		}
		return null;
	}

	private String selectCustomDropdownOption(By dropdownLocator, String optionText, String placeholderText,
			By searchInputLocator) {
		WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownLocator));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", dropdown);
		js.executeScript("arguments[0].click();", dropdown);
		typeIntoCustomDropdownSearchIfPresent(searchInputLocator, optionText);

		WebElement option = optionText == null ? null : findVisibleCustomOption(optionText, dropdown);
		if (option != null) {
			String selectedText = option.getText().trim();
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", option);
			js.executeScript("arguments[0].click();", option);
			waitForDropdownToClose(dropdownLocator);
			return selectedText;
		}

		WebElement fallbackOption = findFirstVisibleCustomOption(dropdown, placeholderText);
		if (fallbackOption != null) {
			String selectedText = fallbackOption.getText().trim();
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", fallbackOption);
			js.executeScript("arguments[0].click();", fallbackOption);
			waitForDropdownToClose(dropdownLocator);
			return selectedText;
		}

		dropdown.sendKeys(Keys.ARROW_DOWN);
		dropdown.sendKeys(Keys.ENTER);
		waitForDropdownToClose(dropdownLocator);
		return optionText != null ? optionText : "First available option";
	}

	private String selectFirstEnabledOption(Select select) {
		for (WebElement option : select.getOptions()) {
			String text = option.getText() == null ? "" : option.getText().trim();
			if (!text.isEmpty() && option.isEnabled()) {
				select.selectByVisibleText(text);
				return text;
			}
		}
		throw new NoSuchElementException("No enabled option was available in select element");
	}

	private void waitForDropdownToClose(By dropdownLocator) {
		wait.until(driver -> {
			try {
				String expanded = driver.findElement(dropdownLocator).getAttribute("aria-expanded");
				if (expanded != null) {
					return !"true".equalsIgnoreCase(expanded);
				}
			} catch (Exception e) {
				return true;
			}
			return true;
		});
	}

	private WebElement findVisibleCustomOption(String optionText, WebElement dropdown) {
		String escapedText = escapeXpath(optionText);
		List<WebElement> candidates = driver.findElements(By.xpath("//div[@tabindex='0' and .//*[normalize-space()="
				+ escapedText + "]] | //div[@tabindex='0' and normalize-space()=" + escapedText + "]"));

		for (int index = candidates.size() - 1; index >= 0; index--) {
			WebElement candidate = candidates.get(index);
			if (isUsableDropdownOption(candidate, dropdown)) {
				return candidate;
			}
		}
		return null;
	}

	private WebElement findFirstVisibleCustomOption(WebElement dropdown, String placeholderText) {
		List<WebElement> candidates = driver.findElements(By.xpath("//div[@tabindex='0']"));
		for (int index = candidates.size() - 1; index >= 0; index--) {
			WebElement candidate = candidates.get(index);
			if (!isUsableDropdownOption(candidate, dropdown)) {
				continue;
			}

			String text = candidate.getText();
			if (text == null) {
				continue;
			}

			String normalized = text.trim();
			if (!normalized.isEmpty() && !normalized.contains(placeholderText) && !normalized.startsWith("Select ")) {
				return candidate;
			}
		}
		return null;
	}

	private boolean isUsableDropdownOption(WebElement candidate, WebElement dropdown) {
		try {
			if (!candidate.isDisplayed()) {
				return false;
			}

			if (candidate.equals(dropdown)) {
				return false;
			}

			String candidateText = candidate.getText();
			return candidateText != null && !candidateText.trim().isEmpty();
		} catch (StaleElementReferenceException e) {
			return false;
		}
	}

	private void typeIntoCustomDropdownSearchIfPresent(By searchInputLocator, String optionText) {
		if (optionText == null || optionText.trim().isEmpty()) {
			return;
		}

		try {
			List<WebElement> searchInputs = driver.findElements(searchInputLocator);
			for (WebElement input : searchInputs) {
				if (!input.isDisplayed()) {
					continue;
				}

				input.click();
				input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
				input.sendKeys(Keys.DELETE);
				input.sendKeys(optionText);
				return;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Custom dropdown search input was not usable: {0}", e.getMessage());
		}
	}

	private String escapeXpath(String value) {
		if (!value.contains("'")) {
			return "'" + value + "'";
		}

		String[] parts = value.split("'");
		StringBuilder builder = new StringBuilder("concat(");
		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				builder.append(", \"'\", ");
			}
			builder.append("'").append(parts[i]).append("'");
		}
		builder.append(")");
		return builder.toString();
	}

	private boolean uploadBookImagesThroughInputs(String portrait, String landscape) {
		try {
			List<WebElement> inputs = wait.until(driver -> {
				List<WebElement> matches = driver.findElements(IMAGE_FILE_UPLOAD);
				return matches.size() >= 2 ? matches : null;
			});

			if (portrait != null && !portrait.isBlank()) {
				inputs.get(0).sendKeys(portrait);
			}
			if (landscape != null && !landscape.isBlank()) {
				inputs.get(1).sendKeys(landscape);
			}
			return waitForRequiredImageErrorsToClear();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Direct image input upload failed: {0}", e.getMessage());
			return false;
		}
	}

	private void uploadImageThroughButton(By buttonLocator, String filePath, String imageType) {
		if (filePath == null || filePath.isBlank()) {
			return;
		}

		By errorLocator = resolveImageErrorLocator(buttonLocator);
		String previousErrorText = getVisibleErrorText(errorLocator);
		List<WebElement> knownInputs = driver.findElements(IMAGE_FILE_UPLOAD);

		WebElement uploadButton = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", uploadButton);

		for (int attempt = 0; attempt < 3; attempt++) {
			js.executeScript("arguments[0].click();", uploadButton);

			List<WebElement> candidateInputs = wait.until(driver -> {
				List<WebElement> inputs = driver.findElements(IMAGE_FILE_UPLOAD);
				return inputs.isEmpty() ? null : inputs;
			});

			for (WebElement candidateInput : candidateInputs) {
				try {
					candidateInput.sendKeys(filePath);
					if (waitForImageUploadAcceptance(errorLocator, previousErrorText)) {
						LOGGER.log(Level.INFO, "{0} image uploaded: {1}", new Object[] { imageType, filePath });
						return;
					}
				} catch (Exception e) {
					LOGGER.log(Level.FINE, "Image upload attempt failed for {0}: {1}",
							new Object[] { imageType, e.getMessage() });
				}
			}

			List<WebElement> refreshedInputs = driver.findElements(IMAGE_FILE_UPLOAD);
			refreshedInputs.removeAll(knownInputs);
			knownInputs.addAll(refreshedInputs);
		}

		clickAndUploadWithNativeDialog(uploadButton, filePath);
		if (waitForImageUploadAcceptance(errorLocator, previousErrorText)) {
			LOGGER.log(Level.INFO, "{0} image uploaded through native dialog: {1}",
					new Object[] { imageType, filePath });
			return;
		}

		throw new RuntimeException("Unable to upload " + imageType + " image");
	}

	private By resolveImageErrorLocator(By buttonLocator) {
		if (PORTRAIT_UPLOAD_BUTTON.equals(buttonLocator)) {
			return PORTRAIT_COVER_ERROR;
		}
		if (LANDSCAPE_UPLOAD_BUTTON.equals(buttonLocator)) {
			return LANDSCAPE_COVER_ERROR;
		}
		return WARNING_ELEMENTS;
	}

	private String getVisibleErrorText(By errorLocator) {
		List<WebElement> elements = driver.findElements(errorLocator);
		for (WebElement element : elements) {
			try {
				if (element.isDisplayed()) {
					String text = element.getText();
					if (text != null) {
						return text.trim();
					}
				}
			} catch (StaleElementReferenceException e) {
				// Retry on next lookup.
			}
		}
		return "";
	}

	private boolean waitForImageUploadAcceptance(By errorLocator, String previousErrorText) {
		try {
			return new WebDriverWait(driver, Duration.ofSeconds(8)).until(driver -> {
				String currentError = getVisibleErrorText(errorLocator);
				if (currentError.toLowerCase().contains("required")) {
					return false;
				}
				return !currentError.equalsIgnoreCase(previousErrorText) || currentError.isEmpty();
			});
		} catch (TimeoutException e) {
			return false;
		}
	}

	private boolean waitForRequiredImageErrorsToClear() {
		try {
			return new WebDriverWait(driver, Duration.ofSeconds(8)).until(driver -> {
				String portraitError = getVisibleErrorText(PORTRAIT_COVER_ERROR).toLowerCase();
				String landscapeError = getVisibleErrorText(LANDSCAPE_COVER_ERROR).toLowerCase();
				return !portraitError.contains("required") && !landscapeError.contains("required");
			});
		} catch (TimeoutException e) {
			return false;
		}
	}

	private void clickAndUploadWithNativeDialog(WebElement uploadButton, String filePath) {
		try {
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", uploadButton);
			js.executeScript("arguments[0].click();", uploadButton);
			Thread.sleep(1000);

			StringSelection selection = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

			Robot robot = new Robot();
			robot.delay(500);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.delay(500);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.delay(1200);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Native dialog upload fallback failed: {0}", e.getMessage());
		}
	}

	private void uploadGenericFile(By buttonLocator, By inputLocator, String filePath, String fileType) {
		List<WebElement> existingInputs = driver.findElements(inputLocator);
		int previousCount = existingInputs.size();

		WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", trigger);
		js.executeScript("arguments[0].click();", trigger);

		WebElement targetInput = wait.until(driver -> {
			List<WebElement> currentInputs = driver.findElements(inputLocator);
			if (currentInputs.size() > previousCount) {
				return currentInputs.get(currentInputs.size() - 1);
			}

			if (!currentInputs.isEmpty()) {
				return currentInputs.get(Math.min(previousCount, currentInputs.size() - 1));
			}

			return null;
		});

		targetInput.sendKeys(filePath);
		LOGGER.log(Level.INFO, "{0} file uploaded: {1}", new Object[] { fileType, filePath });
	}

	private void waitForPostBookSaveState() {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(30))
					.until(ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(AUDIO_UPLOAD_SCREEN),
							ExpectedConditions.visibilityOfElementLocated(AUDIO_UPLOAD_TITLE),
							ExpectedConditions.visibilityOfElementLocated(BACK_TO_SUMMARY_BUTTON),
							ExpectedConditions.visibilityOfElementLocated(ADD_AUDIO_BUTTON)));
		} catch (TimeoutException e) {
			LOGGER.log(Level.INFO, "Post-save transition did not expose audio controls yet");
		}
	}

	private boolean isDisplayed(By locator) {
		try {
			List<WebElement> elements = driver.findElements(locator);
			return !elements.isEmpty() && elements.get(0).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// ================= BOOK SAVE/CANCEL METHODS =================

	/**
	 * Save book (clicks Save button and waits for processing)
	 */
	public void saveBook() {
		try {
			clickSave();
			LOGGER.info("Book saved successfully");
		} catch (Exception e) {
			LOGGER.warning("Failed to save book: " + e.getMessage());
			throw new RuntimeException("Failed to save book", e);
		}
	}

	/**
	 * Cancel book editing (clicks Cancel button)
	 */
	public void cancelEdit() {
		try {
			WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//button[contains(text(),'Cancel')] | " +
							"//div[@tabindex='0'][.//div[contains(text(),'Cancel')]]")));
			js.executeScript("arguments[0].click();", cancelButton);
			LOGGER.info("Canceled book edit");
		} catch (Exception e) {
			LOGGER.warning("Could not find cancel button: " + e.getMessage());
		}
	}

	// ================= CHAPTER EDIT/DELETE METHODS =================

	private static final By CHAPTER_EDIT_BUTTON = By.xpath(
			"//*[@data-testid and contains(@data-testid,'chapter') and contains(@data-testid,'edit')]"
					+ " | //*[@data-testid and contains(@data-testid,'button_edit_uploaded_file_')]"
					+ " | //*[@data-testid='screen_upload_audio_file']//*[self::button or @role='button' or @tabindex='0']"
					+ "[normalize-space()='Edit' or .//*[normalize-space()='Edit']]");
	private static final By CHAPTER_DELETE_BUTTON = By.xpath(
			"//*[@data-testid and contains(@data-testid,'chapter') and contains(@data-testid,'delete')]"
					+ " | //*[@data-testid and contains(@data-testid,'button_delete_uploaded_file_')]"
					+ " | //*[@data-testid='screen_upload_audio_file']//*[self::button or @role='button' or @tabindex='0']"
					+ "[normalize-space()='Delete' or .//*[normalize-space()='Delete']]");
	private static final By CHAPTER_LIST = By.xpath(
			"//*[@data-testid and (contains(@data-testid,'chapter_row')"
					+ " or contains(@data-testid,'chapter-row')"
					+ " or contains(@data-testid,'chapter_item')"
					+ " or contains(@data-testid,'chapter-item')"
					+ " or contains(@data-testid,'chapter_card')"
					+ " or contains(@data-testid,'chapter-card')"
					+ " or contains(@data-testid,'container_chapter')"
					+ " or contains(@data-testid,'uploaded_file'))]");

	/**
	 * Edit first chapter in the list
	 */
	public void editFirstChapter() {
		try {
			List<WebElement> editButtons = getVisibleElements(CHAPTER_EDIT_BUTTON);
			if (!editButtons.isEmpty()) {
				WebElement firstEditButton = editButtons.get(0);
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", firstEditButton);
				try {
					wait.until(ExpectedConditions.elementToBeClickable(firstEditButton)).click();
				} catch (Exception e) {
					js.executeScript("arguments[0].click();", firstEditButton);
				}
				wait.until(driver -> isChapterFormVisible());
				LOGGER.info("Clicked edit button on first chapter");
			} else {
				throw new IllegalStateException("No edit button found on any chapter");
			}
		} catch (Exception e) {
			LOGGER.warning("Failed to edit first chapter: " + e.getMessage());
			throw new RuntimeException("Failed to edit first chapter", e);
		}
	}

	/**
	 * Delete first chapter in the list
	 */
	public void deleteFirstChapter() {
		try {
			List<WebElement> deleteButtons = getVisibleElements(CHAPTER_DELETE_BUTTON);
			if (!deleteButtons.isEmpty()) {
				WebElement firstDeleteButton = deleteButtons.get(0);
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", firstDeleteButton);
				js.executeScript("arguments[0].click();", firstDeleteButton);
				LOGGER.info("Clicked delete button on first chapter");
			} else {
				throw new IllegalStateException("No delete button found on any chapter");
			}
		} catch (Exception e) {
			LOGGER.warning("Failed to delete first chapter: " + e.getMessage());
			throw new RuntimeException("Failed to delete first chapter", e);
		}
	}

	/**
	 * Confirm chapter deletion in dialog
	 */
	public void confirmChapterDelete() {
		try {
			try {
				Alert alert = wait.until(ExpectedConditions.alertIsPresent());
				alert.accept();
				LOGGER.info("Confirmed chapter deletion via browser alert");
				return;
			} catch (TimeoutException e) {
				// Fall back to in-page confirmation when no browser alert is present.
			}

			WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//button[contains(text(),'Delete') or contains(text(),'Confirm')] | " +
							"//div[@tabindex='0'][.//div[contains(text(),'Delete') or contains(text(),'Confirm')]]")));
			js.executeScript("arguments[0].click();", confirmButton);
			LOGGER.info("Confirmed chapter deletion");
		} catch (Exception e) {
			LOGGER.warning("Could not find confirm chapter delete button: " + e.getMessage());
		}
	}

	/**
	 * Cancel chapter deletion in dialog
	 */
	public void cancelChapterDelete() {
		try {
			try {
				Alert alert = wait.until(ExpectedConditions.alertIsPresent());
				alert.dismiss();
				LOGGER.info("Canceled chapter deletion via browser alert");
				return;
			} catch (TimeoutException e) {
				// Fall back to in-page cancel when no browser alert is present.
			}

			WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//button[contains(text(),'Cancel')] | " +
							"//div[@tabindex='0'][.//div[contains(text(),'Cancel')]]")));
			js.executeScript("arguments[0].click();", cancelButton);
			LOGGER.info("Canceled chapter deletion");
		} catch (Exception e) {
			LOGGER.warning("Could not find cancel chapter delete button: " + e.getMessage());
		}
	}

	/**
	 * Cancel chapter editing
	 */
	public void cancelChapterEdit() {
		try {
			WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//button[contains(text(),'Cancel')] | " +
							"//div[@tabindex='0'][.//div[contains(text(),'Cancel')]]")));
			js.executeScript("arguments[0].click();", cancelButton);
			LOGGER.info("Canceled chapter edit");
		} catch (Exception e) {
			LOGGER.warning("Could not find cancel chapter edit button: " + e.getMessage());
		}
	}

	/**
	 * Get count of chapters
	 */
	public int getChapterCount() {
		try {
			List<WebElement> chapters = getVisibleElements(CHAPTER_LIST);
			if (!chapters.isEmpty()) {
				return chapters.size();
			}

			List<WebElement> editButtons = getVisibleElements(CHAPTER_EDIT_BUTTON);
			if (!editButtons.isEmpty()) {
				return editButtons.size();
			}

			List<WebElement> deleteButtons = getVisibleElements(CHAPTER_DELETE_BUTTON);
			return deleteButtons.size();
		} catch (Exception e) {
			LOGGER.warning("Could not get chapter count: " + e.getMessage());
			return 0;
		}
	}

	private List<WebElement> getVisibleElements(By locator) {
		List<WebElement> visibleElements = new ArrayList<>();
		for (WebElement element : driver.findElements(locator)) {
			try {
				if (element.isDisplayed()) {
					visibleElements.add(element);
				}
			} catch (StaleElementReferenceException e) {
				// Ignore transient rerenders and keep scanning remaining candidates.
			}
		}
		return visibleElements;
	}

	/**
	 * Check if any chapters exist
	 */
	public boolean hasChapters() {
		return getChapterCount() > 0;
	}
}
