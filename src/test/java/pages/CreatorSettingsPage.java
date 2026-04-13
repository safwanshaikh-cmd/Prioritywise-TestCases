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
	private static final By BACK_TO_SUMMARY_BUTTON = By.cssSelector("[data-testid='button_back_to_summary']");
	private static final By ADD_AUDIO_BUTTON = By.cssSelector("[data-testid='button_add_audio']");
	private static final By CHAPTER_NAME_INPUT = By.cssSelector("[data-testid='input_chapter_name']");
	private static final By CHAPTER_SUMMARY_INPUT = By.cssSelector("[data-testid='input_chapter_summary']");
	private static final By AUDIO_FILE_BUTTON = By.cssSelector("[data-testid='button_select_audio_file']");
	private static final By AUDIO_CHAPTER_SAVE_BUTTON = By.cssSelector("[data-testid='button_save_audio_chapter']");
	private static final By AUDIO_FILE_INPUT = By.cssSelector("input[type='file'][accept*='audio'], input[type='file']");

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

	public void enterTitle(String title) {
		WebElement titleInput = waitForTitleInput();
		titleInput.clear();
		titleInput.sendKeys(title == null ? "" : title);
		LOGGER.log(Level.INFO, "Entered book title");
	}

	public void enterAuthor(String author) {
		WebElement authorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AUTHOR_FIELD));
		authorInput.clear();
		authorInput.sendKeys(author == null ? "" : author);
		LOGGER.log(Level.INFO, "Entered book author");
	}

	public void enterSummary(String summary) {
		WebElement summaryInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SUMMARY_FIELD_UPLOAD));
		summaryInput.clear();
		summaryInput.sendKeys(summary == null ? "" : summary);
		LOGGER.log(Level.INFO, "Entered book summary");
	}

	public void selectLanguage(String language) {
		new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(LANGUAGE_DROPDOWN)))
				.selectByVisibleText(language);
		LOGGER.log(Level.INFO, "Language selected: {0}", language);
	}

	public void selectCountryCategory(String countryCategory) {
		new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(COUNTRYCATEGORY_DROPDOWN)))
				.selectByVisibleText(countryCategory);
		LOGGER.log(Level.INFO, "Country Category selected: {0}", countryCategory);
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
		new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(SELECT_GENRE)))
				.selectByVisibleText(genre);
		LOGGER.log(Level.INFO, "Genre selected: {0}", genre);
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

	public void enterChapterName(String chapterName) {
		WebElement chapterNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CHAPTER_NAME_INPUT));
		chapterNameInput.clear();
		chapterNameInput.sendKeys(chapterName);
		LOGGER.log(Level.INFO, "Chapter name entered: {0}", chapterName);
	}

	public void enterChapterSummary(String summary) {
		WebElement chapterSummaryInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CHAPTER_SUMMARY_INPUT));
		chapterSummaryInput.clear();
		chapterSummaryInput.sendKeys(summary);
		LOGGER.log(Level.INFO, "Chapter summary entered");
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
}
