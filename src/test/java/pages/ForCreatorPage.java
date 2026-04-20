package pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Thread;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page object for the "For Creator" listing page. Handles listing filters,
 * search, and first-row details.
 */
public class ForCreatorPage {

	private static final Logger LOGGER = Logger.getLogger(ForCreatorPage.class.getName());

	private final WebDriver driver;
	private final WebDriverWait waitLocal;
	private final JavascriptExecutor js;

	private static final By STATUS_FILTER_DROPDOWN = By.cssSelector("[data-testid='select_status_filter_desktop']");
	private static final By FOR_CREATOR_PAGE_SEARCH_INPUT = By
			.cssSelector("[data-testid='input_search_books_desktop']");
	private static final By BOOK_ROWS = By
			.cssSelector("[data-testid='scroll_books_table'] [data-testid^='container_book_row_']");
	private static final By BOOK_TITLE_ELEMENTS = By
			.cssSelector("[data-testid='scroll_books_table'] [data-testid^='text_book_title_']");

	private static final By EDIT_BUTTONS = By.cssSelector("[data-testid^='button_edit_book_']");
	private static final By DELETE_BUTTONS = By.cssSelector("[data-testid^='button_delete_book_']");
	private static final By NO_DATA = By
			.cssSelector("[data-testid='container_no_data'], [data-testid='text_no_data_found']");

	// Updated locators based on actual HTML
	private static final By DELETE_CONFIRM_BUTTON = By
			.xpath("//*[self::button or @role='button' or @tabindex='0'][contains(normalize-space(.),'OK')]"
					+ " | //*[contains(@data-testid,'toast') or contains(@data-testid,'modal') or @role='dialog']"
					+ "//*[self::button or @role='button' or @tabindex='0'][contains(normalize-space(.),'OK')]");

	private static final By DELETE_CANCEL_BUTTON = By
			.xpath("//*[self::button or @role='button' or @tabindex='0'][contains(normalize-space(.),'Cancel')]"
					+ " | //*[contains(@data-testid,'toast') or contains(@data-testid,'modal') or @role='dialog']"
					+ "//*[self::button or @role='button' or @tabindex='0'][contains(normalize-space(.),'Cancel')]");

	private static final By MODAL_DIALOG = By
			.xpath("//*[contains(.,'Remove From Library') and contains(.,'Are you sure you want to remove')]"
					+ " | //*[@data-testid='container_modal_dialog'] | //*[@role='dialog']");

	private static final By DIALOG_TITLE = By.xpath("//*[contains(normalize-space(.),'Remove From Library')]");
	private static final By DIALOG_MESSAGE = By
			.xpath("//*[contains(normalize-space(.),'Are you sure you want to remove this Book')]");

	public ForCreatorPage(WebDriver driver) {
		this.driver = driver;
		this.waitLocal = new WebDriverWait(driver, Duration.ofSeconds(15));
		this.js = (JavascriptExecutor) driver;
	}

	// ================= FILTER METHODS =================

	public void selectApprovedFilter() {
		selectStatusFilter("Approved");
	}

	public void selectPendingFilter() {
		selectStatusFilter("Pending");
	}

	public void selectRejectedFilter() {
		selectStatusFilter("Rejected", "Reject");
	}

	public void selectStatusFilter(String... visibleTexts) {
		WebElement statusFilter = waitLocal
				.until(ExpectedConditions.visibilityOfElementLocated(STATUS_FILTER_DROPDOWN));
		Select dropdown = new Select(statusFilter);

		for (String visibleText : visibleTexts) {
			try {
				dropdown.selectByVisibleText(visibleText);
				waitForListingState();
				LOGGER.log(Level.INFO, "Status filter selected: {0}", visibleText);
				return;
			} catch (RuntimeException ex) {
				LOGGER.log(Level.FINE, "Status filter option not available: {0}", visibleText);
			}
		}

		throw new IllegalStateException(
				"Unable to select any requested status filter: " + String.join(", ", visibleTexts));
	}

	// ================= BOOK LISTING METHODS =================

	public boolean verifyBookListing() {
		waitForListingState();
		return !driver.findElements(BOOK_ROWS).isEmpty();
	}

	public void waitForPageReady() {
		waitLocal.until(webDriver -> !webDriver.findElements(STATUS_FILTER_DROPDOWN).isEmpty()
				|| !webDriver.findElements(FOR_CREATOR_PAGE_SEARCH_INPUT).isEmpty()
				|| !webDriver.findElements(BOOK_ROWS).isEmpty() || !webDriver.findElements(NO_DATA).isEmpty());
	}

	public void waitForListingState() {
		waitForPageReady();
		waitLocal.until(
				webDriver -> !webDriver.findElements(BOOK_ROWS).isEmpty() || !webDriver.findElements(NO_DATA).isEmpty()
						|| !webDriver.findElements(FOR_CREATOR_PAGE_SEARCH_INPUT).isEmpty());
	}

	public boolean hasNoDataState() {
		waitForListingState();
		return !driver.findElements(NO_DATA).isEmpty();
	}

	public boolean hasBooks() {
		waitForListingState();
		return !driver.findElements(BOOK_ROWS).isEmpty();
	}

	public int getVisibleBookCount() {
		waitForListingState();
		return driver.findElements(BOOK_ROWS).size();
	}

	public List<String> getVisibleBookTitles() {
		waitForListingState();
		Set<String> titles = new LinkedHashSet<>();

		for (WebElement titleElement : driver.findElements(BOOK_TITLE_ELEMENTS)) {
			String text = titleElement.getText().trim();
			if (isRealTitle(text)) {
				titles.add(text);
			}
		}

		if (!titles.isEmpty()) {
			return new ArrayList<>(titles);
		}

		for (WebElement row : driver.findElements(BOOK_ROWS)) {
			String title = extractTitleFromRow(row);
			if (!title.isBlank()) {
				titles.add(title);
			}
		}

		return new ArrayList<>(titles);
	}

	public boolean containsVisibleBookTitle(String expectedTitle) {
		for (String title : getVisibleBookTitles()) {
			if (title.equalsIgnoreCase(expectedTitle)) {
				return true;
			}
		}
		return false;
	}

	public void printBookDetails() {
		printBookDetailsForFilter("Current", getVisibleBookTitles());
	}

	public void printBookDetailsForFilter(String filterName, List<String> titles) {
		LOGGER.log(Level.INFO, "{0} filter - Total Books Found: {1}", new Object[] { filterName, titles.size() });
		if (titles.isEmpty()) {
			LOGGER.log(Level.INFO, "{0} filter - No books found", filterName);
			return;
		}

		for (String title : titles) {
			LOGGER.log(Level.INFO, "{0} filter - Book Title: {1}", new Object[] { filterName, title });
		}
	}

	public List<String> getBookTitlesForFilter(String... visibleTexts) {
		selectStatusFilter(visibleTexts);
		return getVisibleBookTitles();
	}

	public List<String> getPendingBookTitles() {
		return getVisibleBookTitles();
	}

	public String getFirstPendingBookTitle() {
		List<String> titles = getPendingBookTitles();
		if (titles.isEmpty()) {
			throw new IllegalStateException("No books are displayed in Pending filter");
		}
		return titles.get(0);
	}

	public String getFirstVisibleBookTitle() {
		List<String> titles = getVisibleBookTitles();
		if (titles.isEmpty()) {
			throw new IllegalStateException("No books are displayed in the current filter");
		}
		return titles.get(0);
	}

	public Map<String, String> getFirstVisibleBookDetails() {
		waitForListingState();
		List<WebElement> rows = driver.findElements(BOOK_ROWS);
		if (rows.isEmpty()) {
			throw new IllegalStateException("No books are displayed in the current filter");
		}

		WebElement firstRow = rows.get(0);
		Map<String, String> details = new LinkedHashMap<>();
		details.put("Title", getRowValue(firstRow, "text_book_title_"));
		details.put("Author", getRowValue(firstRow, "text_book_author_"));
		details.put("Category", getRowValue(firstRow, "text_book_category_"));
		details.put("Language", getRowValue(firstRow, "text_book_language_"));
		details.put("Genre", getRowValue(firstRow, "text_book_genre_"));
		return details;
	}

	public void printFirstVisibleBookDetails() {
		Map<String, String> details = getFirstVisibleBookDetails();
		LOGGER.info("First visible book details:");
		for (Map.Entry<String, String> entry : details.entrySet()) {
			LOGGER.log(Level.INFO, "{0}: {1}", new Object[] { entry.getKey(), entry.getValue() });
		}
	}

	// ================= SEARCH METHODS =================

	public void searchBook(String bookName) {
		waitForPageReady();
		WebElement searchInput = waitLocal
				.until(ExpectedConditions.visibilityOfElementLocated(FOR_CREATOR_PAGE_SEARCH_INPUT));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", searchInput);
		searchInput.clear();
		searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
		if (bookName != null && !bookName.isBlank()) {
			searchInput.sendKeys(bookName);
		}
		waitForSearchResultsToSettle();
		LOGGER.log(Level.INFO, "Searched for book: {0}", bookName);
	}

	public void submitEmptySearch() {
		waitForPageReady();
		WebElement searchInput = waitLocal
				.until(ExpectedConditions.visibilityOfElementLocated(FOR_CREATOR_PAGE_SEARCH_INPUT));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", searchInput);
		searchInput.click();
		searchInput.sendKeys(Keys.ENTER);
		waitForSearchResultsToSettle();
		LOGGER.info("Focused the For Creators search box and pressed Enter");
	}

	public void clearSearch() {
		searchBook("");
		LOGGER.info("Cleared search input");
	}

	// ================= EDIT/DELETE METHODS =================

	public void editFirstBook() {
		waitForListingState();
		List<WebElement> editButtons = driver.findElements(EDIT_BUTTONS);
		if (editButtons.isEmpty()) {
			throw new IllegalStateException("No edit button found on any book");
		}

		WebElement firstEditButton = editButtons.get(0);
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", firstEditButton);
		js.executeScript("arguments[0].click();", firstEditButton);
		LOGGER.info("Clicked edit button on first book");
	}

	public void clickEditBookByIndex(int index) {
		waitForListingState();
		List<WebElement> editButtons = driver.findElements(EDIT_BUTTONS);
		if (editButtons.isEmpty()) {
			throw new IllegalStateException("No edit button found on any book");
		}
		if (index < 0 || index >= editButtons.size()) {
			throw new IllegalArgumentException(
					"Invalid edit button index: " + index + ", available: " + editButtons.size());
		}

		WebElement editButton = editButtons.get(index);
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", editButton);
		js.executeScript("arguments[0].click();", editButton);
		LOGGER.log(Level.INFO, "Clicked edit button for book index: {0}", index);
	}

	public void deleteFirstBook() {
		waitForListingState();

		LOGGER.log(Level.INFO, "Attempting to delete first book...");

		// Strategy 1: Try data-testid locator first
		List<WebElement> deleteButtons = driver.findElements(DELETE_BUTTONS);
		if (!deleteButtons.isEmpty() && deleteButtons.get(0).isDisplayed()) {
			WebElement firstDeleteButton = deleteButtons.get(0);
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", firstDeleteButton);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			js.executeScript("arguments[0].click();", firstDeleteButton);
			LOGGER.log(Level.INFO, "Clicked delete button (Strategy 1: data-testid)");
			return;
		}

		// Strategy 2: Look for delete icon/trash can
		List<WebElement> trashIcons = driver.findElements(By.xpath(
				"//div[contains(@class, 'book') or contains(@data-testid, 'book')]//img[contains(@src, 'trash') or contains(@src, 'delete')] | "
						+ "//div[contains(@class, 'book') or contains(@data-testid, 'book')]//*[contains(@class, 'icon') and (contains(@class, 'trash') or contains(@class, 'delete'))] | "
						+ "//div[contains(@class, 'book') or contains(@data-testid, 'book')]//button[contains(@class, 'delete') or contains(@class, 'trash')]"));

		if (!trashIcons.isEmpty()) {
			WebElement trashIcon = trashIcons.get(0);
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", trashIcon);
			try {
				trashIcon.click();
			} catch (Exception e) {
				js.executeScript("arguments[0].click();", trashIcon);
			}
			LOGGER.log(Level.INFO, "Clicked delete/trash icon (Strategy 2: icon search)");
			return;
		}

		// Strategy 3: Click on first book to show delete option
		try {
			List<WebElement> books = driver.findElements(BOOK_ROWS);
			if (!books.isEmpty()) {
				WebElement firstBook = books.get(0);
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", firstBook);

				// Click the book to see if delete options appear
				js.executeScript("arguments[0].click();", firstBook);
				LOGGER.log(Level.INFO, "Clicked on first book to show delete options");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

				// Now look for delete button again
				List<WebElement> contextualDeleteButtons = driver
						.findElements(By.xpath("//button[contains(text(), 'Delete') or contains(@class, 'delete')] | "
								+ "//div[contains(@class, 'delete')] | "
								+ "//*[contains(text(), 'Remove') or contains(text(), 'Delete')]"));

				if (!contextualDeleteButtons.isEmpty()) {
					WebElement deleteBtn = contextualDeleteButtons.get(0);
					js.executeScript("arguments[0].click();", deleteBtn);
					LOGGER.log(Level.INFO, "Clicked contextual delete button (Strategy 3: click book first)");
					return;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Strategy 3 failed: " + e.getMessage());
		}

		// Strategy 4: Generic approach - look for any button with delete/trash icon
		List<WebElement> allButtons = driver.findElements(By.tagName("button"));

		for (WebElement btn : allButtons) {
			try {

				String text = btn.getText();
				text = text != null ? text.toLowerCase() : "";

				String className = btn.getAttribute("class");
				className = className != null ? className.toLowerCase() : "";

				String ariaLabel = btn.getAttribute("aria-label");
				ariaLabel = ariaLabel != null ? ariaLabel.toLowerCase() : "";

				if (text.contains("delete") || text.contains("remove") || className.contains("delete")
						|| className.contains("trash") || ariaLabel.contains("delete")
						|| ariaLabel.contains("remove")) {

					if (btn.isDisplayed()) {
						js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
						js.executeScript("arguments[0].click();", btn);

						LOGGER.log(Level.INFO, "Clicked delete button using generic search (Strategy 4)");
						return;
					}
				}

			} catch (Exception e) {
				// Continue to next button
			}
		}
	}

	public void confirmDelete() {
		try {
			// Verify dialog is displayed before confirming
			boolean dialogDisplayed = isDeleteConfirmationDialogDisplayed();
			LOGGER.log(Level.INFO, "Delete confirmation dialog displayed: " + dialogDisplayed);

			WebElement confirmButton = waitLocal.until(ExpectedConditions.elementToBeClickable(DELETE_CONFIRM_BUTTON));
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", confirmButton);
			js.executeScript("arguments[0].click();", confirmButton);

			LOGGER.log(Level.INFO, "Clicked OK button to confirm book deletion");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to confirm deletion: " + e.getMessage());
			throw new RuntimeException("Unable to confirm book deletion", e);
		}
	}

	public void cancelDelete() {
		try {
			// Verify dialog is displayed before canceling
			boolean dialogDisplayed = isDeleteConfirmationDialogDisplayed();
			LOGGER.log(Level.INFO, "Delete confirmation dialog displayed: " + dialogDisplayed);

			WebElement cancelButton = waitLocal.until(ExpectedConditions.elementToBeClickable(DELETE_CANCEL_BUTTON));
			js.executeScript("arguments[0].scrollIntoView({block:'center'});", cancelButton);
			js.executeScript("arguments[0].click();", cancelButton);

			LOGGER.log(Level.INFO, "Clicked Cancel button to cancel book deletion");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to cancel deletion: " + e.getMessage());
			throw new RuntimeException("Unable to cancel book deletion", e);
		}
	}

	public boolean isDeleteConfirmationDialogDisplayed() {
		try {
			WebElement dialog = null;
			try {
				dialog = waitLocal.until(ExpectedConditions.presenceOfElementLocated(MODAL_DIALOG));
			} catch (Exception ignored) {
				// Fall back to button-based detection below.
			}
			boolean isDisplayed = dialog != null && dialog.isDisplayed();
			boolean hasConfirmButton = driver.findElements(DELETE_CONFIRM_BUTTON).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
			boolean hasCancelButton = driver.findElements(DELETE_CANCEL_BUTTON).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});

			try {
				WebElement title = driver.findElement(DIALOG_TITLE);
				WebElement message = driver.findElement(DIALOG_MESSAGE);
				String titleText = title.getText();
				String messageText = message.getText();

				LOGGER.log(Level.INFO, "Delete Dialog Title: '" + titleText + "'");
				LOGGER.log(Level.INFO, "Delete Dialog Message: '" + messageText + "'");

				return (isDisplayed || hasConfirmButton || hasCancelButton) && titleText.contains("Remove")
						&& messageText.contains("Are you sure");
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Dialog found but title/message verification failed: " + e.getMessage());
				return isDisplayed || hasConfirmButton || hasCancelButton;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Delete confirmation dialog not found: " + e.getMessage());
			return false;
		}
	}

	// ================= ALIAS METHODS =================

	public int getBookCount() {
		return getVisibleBookCount();
	}

	public String getFirstBookTitle() {
		try {
			return getFirstVisibleBookTitle();
		} catch (IllegalStateException e) {
			return "";
		}
	}

	public boolean isBookInList(String bookTitle) {
		return containsVisibleBookTitle(bookTitle);
	}

	// ================= HELPER METHODS =================

	private void waitForSearchResultsToSettle() {
		waitLocal.until(webDriver -> {
			List<WebElement> rows = webDriver.findElements(BOOK_ROWS);
			List<WebElement> noDataElements = webDriver.findElements(NO_DATA);
			return !rows.isEmpty() || !noDataElements.isEmpty();
		});
	}

	private String getRowValue(WebElement row, String dataTestIdPrefix) {
		List<WebElement> elements = row.findElements(By.cssSelector("[data-testid^='" + dataTestIdPrefix + "']"));
		if (elements.isEmpty()) {
			return "";
		}
		return elements.get(0).getText().trim();
	}

	private String extractTitleFromRow(WebElement row) {
		List<WebElement> explicitTitleElements = row.findElements(By.cssSelector("[data-testid^='text_book_title_']"));
		for (WebElement titleElement : explicitTitleElements) {
			String text = titleElement.getText().trim();
			if (isRealTitle(text)) {
				return text;
			}
		}

		String[] lines = row.getText().split("\\R");
		for (String line : lines) {
			String text = line.trim();
			if (isRealTitle(text)) {
				return text;
			}
		}

		return "";
	}

	private boolean isRealTitle(String text) {
		if (text == null || text.isBlank()) {
			return false;
		}

		String lower = text.trim().toLowerCase();
		return !lower.equals("title") && !lower.equals("cover") && !lower.equals("author") && !lower.equals("category")
				&& !lower.equals("language") && !lower.equals("genre") && !lower.equals("status")
				&& !lower.equals("action") && !lower.equals("pending") && !lower.equals("approved")
				&& !lower.equals("rejected") && !lower.equals("edit") && !lower.equals("delete")
				&& !lower.equals("view") && !lower.equals("no data found");
	}
}
