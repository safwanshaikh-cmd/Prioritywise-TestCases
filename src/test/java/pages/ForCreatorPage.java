package pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page object for the "For Creator" listing page.
 * Handles content listing, filtering, and book details retrieval.
 */
public class ForCreatorPage {

	private static final Logger LOGGER = Logger.getLogger(ForCreatorPage.class.getName());

	private final WebDriver driver;
	private final WebDriverWait waitLocal;

	// Locators
	private static final By STATUS_FILTER_DROPDOWN = By.cssSelector("[data-testid='select_status_filter_desktop']");
	private static final By BOOK_LIST = By.cssSelector("[data-testid='scroll_books_table'] [data-testid='container_table_wrapper'] > div:not([data-testid='container_table_header']):not([data-testid='container_no_data'])");
	private static final By BOOK_TITLE_ELEMENTS = By.xpath("//*[@data-testid='scroll_books_table']//*[@data-testid='text_book_title' and not(ancestor::*[@data-testid='container_table_header'])]");
	private static final By NO_DATA = By.cssSelector("[data-testid='container_no_data'], [data-testid='text_no_data_found']");

	public ForCreatorPage(WebDriver driver) {
		this.driver = driver;
		this.waitLocal = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// ================= FILTER METHODS =================

	public void selectPendingFilter() {
		WebElement statusFilter = waitLocal.until(ExpectedConditions.visibilityOfElementLocated(STATUS_FILTER_DROPDOWN));
		new Select(statusFilter).selectByVisibleText("Pending");
		waitLocal.until(driver -> {
			boolean hasBooks = !driver.findElements(BOOK_LIST).isEmpty();
			boolean hasNoData = !driver.findElements(NO_DATA).isEmpty();
			return hasBooks || hasNoData;
		});
		LOGGER.log(Level.INFO, "Pending filter selected");
	}

	// ================= BOOK LISTING METHODS =================

	public boolean verifyBookListing() {
		waitLocal.until(driver -> {
			boolean hasBooks = !driver.findElements(BOOK_LIST).isEmpty();
			boolean hasNoData = !driver.findElements(NO_DATA).isEmpty();
			return hasBooks || hasNoData;
		});

		List<WebElement> books = driver.findElements(BOOK_LIST);
		return books != null && !books.isEmpty();
	}

	public void printBookDetails() {
		List<String> titles = getPendingBookTitles();
		LOGGER.log(Level.INFO, "Total Books Found: {0}", titles.size());
		for (String title : titles) {
			LOGGER.log(Level.INFO, "Book Title: {0}", title);
		}
	}

	public List<String> getPendingBookTitles() {
		Set<String> titles = new LinkedHashSet<>();

		List<WebElement> explicitTitleElements = driver.findElements(BOOK_TITLE_ELEMENTS);
		for (WebElement titleElement : explicitTitleElements) {
			String text = titleElement.getText().trim();
			if (isRealTitle(text)) {
				titles.add(text);
			}
		}

		if (!titles.isEmpty()) {
			return new ArrayList<>(titles);
		}

		List<WebElement> rows = driver.findElements(BOOK_LIST);

		for (WebElement row : rows) {
			String title = extractTitleFromRow(row);
			if (!title.isBlank()) {
				titles.add(title);
			}
		}

		return new ArrayList<>(titles);
	}

	public String getFirstPendingBookTitle() {
		List<String> titles = getPendingBookTitles();
		if (titles.isEmpty()) {
			throw new IllegalStateException("No books are displayed in Pending filter");
		}
		return titles.get(0);
	}

	public List<String> getVisibleBookTitles() {
		return getPendingBookTitles();
	}

	public String getFirstVisibleBookTitle() {
		List<String> titles = getVisibleBookTitles();
		if (titles.isEmpty()) {
			throw new IllegalStateException("No books are displayed in the current filter");
		}
		return titles.get(0);
	}

	// ================= HELPER METHODS =================

	private String extractTitleFromRow(WebElement row) {
		List<WebElement> explicitTitleElements = row.findElements(By.cssSelector("[data-testid='text_book_title']"));
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

		String normalized = text.trim();
		String lower = normalized.toLowerCase();

		return !lower.equals("title")
				&& !lower.equals("cover")
				&& !lower.equals("author")
				&& !lower.equals("category")
				&& !lower.equals("language")
				&& !lower.equals("genre")
				&& !lower.equals("status")
				&& !lower.equals("action")
				&& !lower.equals("pending")
				&& !lower.equals("approved")
				&& !lower.equals("rejected")
				&& !lower.equals("edit")
				&& !lower.equals("delete")
				&& !lower.equals("view")
				&& !lower.equals("no data found");
	}
}
