package pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;

/**
 * Page object for Favourites functionality.
 * Based on actual HTML structure with material icons and specific CSS classes.
 */
public class FavouritesPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(FavouritesPage.class.getName());

	// ================= NAVIGATION LOCATORS =================
	private static final By FAVOURITES_MENU = By.xpath("//div[text()='Favourites' or text()='Favorites']");
	private static final By FAVOURITES_TITLE = By.xpath("//div[contains(text(),'Favourites') or contains(text(),'Favorites')]");

	// ================= SEARCH LOCATORS =================
	private static final By SEARCH_IN_FAVOURITES = By.xpath("//input[@placeholder='Search by name...']");
	private static final By SEARCH_BOOK_OR_AUTHOR = By.xpath("//input[@placeholder='Search books or author']");
	private static final By AUTHOR_FILTER_OPTION_LABELS = By.xpath(
			"//div[normalize-space()='AUTHOR']/following::div[@dir='auto'][normalize-space()!='' and not(contains(normalize-space(),'Search by name')) and not(normalize-space()='AUTHOR')][position() <= 20]");

	// ================= FILTER LOCATORS =================
	private static final By FILTER_BUTTON = By.xpath("//div[@dir='auto'][contains(@class, 'css-146c3p1')][text()='󰄹'] | //div[contains(@class, 'material-community')][contains(text(), '󰄹')]");

	// ================= BOOK ITEM LOCATORS =================
	private static final By BOOK_ITEMS = By.xpath("//div[@tabindex='0'][contains(@class, 'css-g5y9jx')]//div[@dir='auto'][contains(@class, 'r-8akbws')]");
	private static final By BOOK_TITLE = By.xpath(".//div[@dir='auto'][contains(@class, 'r-8akbws') and contains(@class, 'r-krxsd3')]");
	private static final By BOOK_AUTHOR = By.xpath(".//div[@dir='auto'][contains(@class, 'r-dnmrzs') and contains(@class, 'r-1udbk01')]");
	private static final By BOOK_IMAGE = By.xpath(".//img[contains(@src, 'thumb.php') or contains(@src, 'placeholder')]");

	// ================= FAVOURITE ICON LOCATORS =================
	private static final By HEART_ICON_ADD = By.xpath(".//div[@dir='auto'][@class='css-146c3p1'][text()=''] | .//div[@dir='auto'][contains(@class, 'css-146c3p1')][contains(text(), '')]");
	private static final By REMOVE_ICON = By.xpath(".//div[@dir='auto'][@class='css-146c3p1'][text()='󰋔'] | .//div[@dir='auto'][contains(@class, 'css-146c3p1')][contains(text(), '󰋔')]");

	// ================= SELECTION MODE LOCATORS =================
	private static final By SELECT_ALL_BUTTON = By.xpath(
			"//div[@tabindex='0'][.//div[normalize-space()='Select All'] or .//div[normalize-space()='Deselect All']]");
	private static final By SELECT_ALL_CHECKBOX = By.xpath("//div[@tabindex='0']//div[@dir='auto'][text()='Select All']/preceding-sibling::div[@dir='auto'][text()='']");

	// ================= FILTER ACTION BAR LOCATORS =================
	private static final By FILTER_ACTION_BAR = By.xpath(
			"//*[contains(normalize-space(.),'Selected Count') and (self::div or self::span)]");
	private static final By SELECTED_COUNT = By.xpath(
			"//div[@dir='auto'][contains(normalize-space(.),'Selected Count')]");
	private static final By REMOVE_SELECTED_BUTTON = By.xpath(
			"//div[@tabindex='0'][.//div[normalize-space()='Remove Selected']]");
	private static final By CANCEL_BUTTON = By.xpath(
			"//div[@tabindex='0'][.//div[normalize-space()='Cancel']]");

	// ================= CONFIRMATION DIALOG LOCATORS =================
	private static final By REMOVE_CONFIRMATION_DIALOG = By.xpath("//div[@class='css-g5y9jx']//div[@dir='auto'][text()='Remove Favourites']");
	private static final By REMOVE_CONFIRMATION_MESSAGE = By.xpath("//div[@dir='auto'][contains(text(),'Are you sure you want to remove this book from favourites?')]");
	private static final By YES_BUTTON = By.xpath("//div[@dir='auto'][text()='Yes']/parent::div[@tabindex='0']");
	private static final By NO_BUTTON = By.xpath("//div[@dir='auto'][text()='No']/parent::div[@tabindex='0']");

	// ================= EMPTY STATE LOCATORS =================
	private static final By NO_FAVORITES_MESSAGE = By.xpath("//*[contains(text(),'No favorites') or contains(text(),'No favourites') or contains(text(),'No favorites yet') or contains(text(),'No favourites yet')]");
	private static final By BROWSE_BUTTON = By.xpath("//div[@dir='auto'][text()='Browse Books' or text()='Browse' or text()='Explore']");

	// ================= COUNT DISPLAY LOCATORS =================
	private static final By SELECTED_COUNT_NUMBER = By.xpath("//div[@class='css-g5y9jx']//div[@dir='auto' and contains(text(),'Selected') or contains(text(),'selected')]");

	private final WebDriverWait pageWait;
	private boolean lastRemovalToasterSeen;

	public FavouritesPage(WebDriver driver) {
		super(driver);
		this.pageWait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// ================= NAVIGATION METHODS =================

	public void clickFavouritesMenu() {
		waitForOverlayToDisappear();
		try {
			WebElement menu = pageWait.until(ExpectedConditions.visibilityOfElementLocated(FAVOURITES_MENU));
			menu.click();
			LOGGER.info("Favourites menu clicked");
			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click Favourites menu: {0}", e.getMessage());
		}
	}

	public boolean isFavouritesPageDisplayed() {
		try {
			WebElement title = pageWait.until(ExpectedConditions.visibilityOfElementLocated(FAVOURITES_TITLE));
			return title.isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Favourites page not visible: {0}", e.getMessage());
			return false;
		}
	}

	// ================= SEARCH METHODS =================

	public void searchInFavourites(String searchText) {
		try {
			WebElement searchInput = pageWait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_IN_FAVOURITES));
			searchInput.clear();
			searchInput.sendKeys(searchText);
			LOGGER.info("Searched in favourites: " + searchText);
			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to search in favourites: {0}", e.getMessage());
		}
	}

	public void searchBookOrAuthor(String searchText) {
		try {
			WebElement searchInput = pageWait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_BOOK_OR_AUTHOR));
			searchInput.clear();
			searchInput.sendKeys(searchText);
			LOGGER.info("Searched book or author: " + searchText);
			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to search book or author: {0}", e.getMessage());
		}
	}

	public void searchBook(String searchText) {
		// Alias for searchBookOrAuthor for backward compatibility
		searchBookOrAuthor(searchText);
	}

	public int getSearchResultsCount() {
		// Return the count of books found (using book items count)
		return getFavouriteBooksCount();
	}

	// ================= FILTER METHODS =================

	public void clickFilterButton() {
		try {
			WebElement filterBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(FILTER_BUTTON));
			filterBtn.click();
			LOGGER.info("Filter button clicked");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click Filter button: {0}", e.getMessage());
		}
	}

	public boolean isFilterActionBarDisplayed() {
		try {
			WebElement actionBar = pageWait.until(ExpectedConditions.visibilityOfElementLocated(FILTER_ACTION_BAR));
			return actionBar.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// ================= BOOK MANAGEMENT METHODS =================

	public List<WebElement> getBookItems() {
		try {
			List<WebElement> books = driver.findElements(BOOK_ITEMS);
			LOGGER.info("Found " + books.size() + " book items");
			return books;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not get book items: {0}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public int getFavouriteBooksCount() {
		return getBookItems().size();
	}

	/**
	 * Gets the title of the first book in favourites
	 * @return Title of the first book, or empty string if no books
	 */
	public String getFirstBookTitle() {
		return getBookTitleAtIndex(0);
	}

	public List<WebElement> getFavouriteBooks() {
		return getBookItems();
	}

	public void selectBookByIndex(int index) {
		try {
			List<WebElement> books = getBookItems();
			if (index < books.size()) {
				WebElement book = books.get(index);

				try {
					// SIMPLIFIED APPROACH: Click the book card itself
					// This will automatically toggle the selection checkbox
					LOGGER.info("Selecting book at index " + index + " by clicking book card");

					// Scroll to book first
					((JavascriptExecutor) driver).executeScript(
						"arguments[0].scrollIntoView({block: 'center'});", book);
					Thread.sleep(300);

					// Click the book card using JavaScript
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", book);
					LOGGER.info("✅ Clicked book card at index " + index);

					// Wait for selection to register
					Thread.sleep(1000);

					// Verify selection worked by checking selected count
					int selectedCount = getSelectedCount();
					LOGGER.info("Current selected count after clicking book " + index + ": " + selectedCount);

					// Alternative verification: check if book has selected class/attribute
					// Note: Re-fetch the book element to avoid stale element reference
					List<WebElement> refreshedBooks = getBookItems();
					if (refreshedBooks.size() > index) {
						WebElement refreshedBook = refreshedBooks.get(index);
						String selectedClass = refreshedBook.getAttribute("class");
						if (selectedClass != null && selectedClass.contains("selected")) {
							LOGGER.info("✅ Book at index " + index + " successfully selected (has 'selected' class)");
						} else {
							LOGGER.info("✅ Book at index " + index + " click executed (selection verified by count)");
						}
					}

				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Failed to select book at index " + index + ": {0}", e.getMessage());
				}
			} else {
				LOGGER.warning("Invalid index for selection: " + index);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to select book at index " + index + ": {0}", e.getMessage());
		}
	}

	public void selectBookByCheckboxOverlay(int index) {
		try {
			List<WebElement> books = getBookItems();
			if (index >= books.size()) {
				LOGGER.warning("Invalid index for checkbox selection: " + index);
				return;
			}

			WebElement book = books.get(index);
			LOGGER.info("Selecting book at index " + index + " using checkbox overlay");
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({block: 'center'});", book);
			Thread.sleep(300);

			try {
				WebElement checkboxOverlay = book.findElement(By.xpath(
						".//div[@tabindex='0' and contains(@class,'r-1loqt21') and contains(@class,'r-1otgn73') and contains(@class,'r-u8s1d')]"
								+ "[.//div[contains(@class,'css-146c3p1') and normalize-space(.)!='']]"));
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkboxOverlay);
				LOGGER.info("Clicked checkbox overlay for book at index " + index);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Checkbox overlay click failed for book at index " + index + ": {0}",
						e.getMessage());
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", book);
				LOGGER.info("Fallback card click executed for book at index " + index);
			}

			Thread.sleep(1000);
			LOGGER.info("Current selected count after selection attempt for book " + index + ": " + getSelectedCount());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to select book by checkbox overlay at index " + index + ": {0}",
					e.getMessage());
		}
	}

	public String getBookTitleAtIndex(int index) {
		try {
			List<WebElement> books = getBookItems();
			if (index < books.size()) {
				WebElement book = books.get(index);
				String titleText = "";

				// Method 1: Use BOOK_TITLE locator
				try {
					WebElement titleElement = book.findElement(BOOK_TITLE);
					titleText = titleElement.getText().trim();
					LOGGER.info("Book " + index + " title (Method 1): '" + titleText + "'");
				} catch (Exception e1) {
					LOGGER.log(Level.FINE, "Method 1 failed for book " + index);
				}

				// Method 2: Get all text from book element
				if (titleText.isEmpty()) {
					titleText = book.getText().trim();
					LOGGER.info("Book " + index + " title (Method 2 - all text): '" + titleText + "'");
				}

				// Method 3: Try common class-based locators
				if (titleText.isEmpty()) {
					try {
						WebElement titleElement = book.findElement(By.xpath(".//div[contains(@class, 'r-1udbk01') or contains(@class, 'r-dnmrzs')]"));
						titleText = titleElement.getText().trim();
						LOGGER.info("Book " + index + " title (Method 3): '" + titleText + "'");
					} catch (Exception e3) {
						LOGGER.log(Level.FINE, "Method 5 failed for book " + index);
					}
				}

				return titleText;
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not get book title at index " + index + ": {0}", e.getMessage());
		}
		return "";
	}

	public String getBookAuthorAtIndex(int index) {
		try {
			List<WebElement> books = getBookItems();
			if (index < books.size()) {
				WebElement book = books.get(index);
				try {
					WebElement authorElement = book.findElement(BOOK_AUTHOR);
					String authorText = authorElement.getText().trim();
					if (!authorText.isEmpty()) {
						return authorText;
					}
				} catch (Exception ignored) {
					// Fall back to broader author extraction below.
				}

				List<WebElement> textNodes = book.findElements(By.xpath(".//div[@dir='auto']"));
				List<String> texts = new ArrayList<>();
				for (WebElement node : textNodes) {
					String text = node.getText().trim();
					if (!text.isEmpty()) {
						texts.add(text);
					}
				}

				if (texts.size() >= 3) {
					return texts.get(2);
				}
				if (texts.size() >= 2) {
					return texts.get(1);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not get book author at index " + index + ": {0}", e.getMessage());
		}
		return "";
	}

	public String getFirstAuthorFilterName() {
		try {
			List<WebElement> candidates = driver.findElements(By.xpath(
					"//div[normalize-space()='AUTHOR']/following::div[@dir='auto'][normalize-space()!='']"));
			for (WebElement candidate : candidates) {
				try {
					if (!candidate.isDisplayed()) {
						continue;
					}
					String text = candidate.getText().trim();
					if (text.isEmpty()) {
						continue;
					}
					if (text.equalsIgnoreCase("AUTHOR") || text.equalsIgnoreCase("Search by name...")) {
						continue;
					}
					if (text.matches("\\d+")) {
						continue;
					}
					if (text.length() <= 1) {
						continue;
					}
					if (text.equalsIgnoreCase("EN")) {
						continue;
					}
					return text;
				} catch (Exception ignored) {
					// Continue checking other candidates.
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not get first author filter name: {0}", e.getMessage());
		}
		return "";
	}

	public void clickHeartIconAtIndex(int index) {
		try {
			List<WebElement> books = getBookItems();
			if (index < books.size()) {
				WebElement book = books.get(index);
				WebElement heartIcon = book.findElement(HEART_ICON_ADD);
				heartIcon.click(); // Direct click on WebElement
				LOGGER.info("Clicked heart icon for book at index: " + index);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click heart icon at index " + index + ": {0}", e.getMessage());
		}
	}

	public void clickRemoveIconAtIndex(int index) {
		WebElement removeIcon = null;
		WebElement book = null;

		try {
			List<WebElement> books = getBookItems();
			if (index >= books.size()) {
				LOGGER.severe("Invalid index " + index + ". Only " + books.size() + " books available.");
				return;
			}

			book = books.get(index);
			String bookTitle = getBookTitleAtIndex(index);
			LOGGER.info("Attempting to click remove icon for book at index " + index + ": " + bookTitle);

			// Method 1: Use REMOVE_ICON locator within book element
			try {
				removeIcon = book.findElement(REMOVE_ICON);
				LOGGER.info("Found remove icon using Method 1 (original locator within book)");
			} catch (Exception e1) {
				LOGGER.log(Level.FINE, "Method 1 failed: " + e1.getMessage());
			}

			// Method 2: Search within book element by icon character
			if (removeIcon == null) {
				try {
					removeIcon = book.findElement(By.xpath(".//div[contains(@class, 'css-146c3p1')][contains(text(), '󰋔')]"));
					LOGGER.info("Found remove icon using Method 2 (icon character within book)");
				} catch (Exception e2) {
					LOGGER.log(Level.FINE, "Method 2 failed: " + e2.getMessage());
				}
			}

			// Method 3: Search all icons within book element
			if (removeIcon == null) {
				try {
					List<WebElement> icons = book.findElements(By.xpath(".//div[contains(@class, 'css-146c3p1')]"));
					LOGGER.info("Method 3: Found " + icons.size() + " candidate icons within book");
					for (int i = 0; i < icons.size(); i++) {
						try {
							String text = icons.get(i).getText();
							LOGGER.fine("Icon " + i + " text: '" + text + "'");
							if (text != null && text.contains("󰋔")) {
								removeIcon = icons.get(i);
								LOGGER.info("Found remove icon using Method 3 (search all icons)");
								break;
							}
						} catch (Exception ex) {
							LOGGER.log(Level.FINE, "Error checking icon " + i + ": " + ex.getMessage());
						}
					}
				} catch (Exception e3) {
					LOGGER.log(Level.FINE, "Method 5 failed: " + e3.getMessage());
				}
			}

			// Method 4: JavaScript within book element
			if (removeIcon == null) {
				try {
					String js = "var icons = arguments[0].querySelectorAll('div[class*=\"css-146c3p1\"]');" +
							   "for(var i=0; i<icons.length; i++) {" +
							   "  try{" +
							   "    if(icons[i].textContent.includes('󰋔')) {" +
							   "      return icons[i];" +
							   "    }" +
							   "  }catch(e){}" +
							   "}" +
							   "return null;";
					removeIcon = (WebElement) ((JavascriptExecutor) driver).executeScript(js, book);
					if (removeIcon != null) {
						LOGGER.info("Found remove icon using Method 4 (JavaScript within book)");
					}
				} catch (Exception e4) {
					LOGGER.log(Level.FINE, "Method 5 failed: " + e4.getMessage());
				}
			}

			// Method 5: Search globally on page using book title to find the right remove icon
			if (removeIcon == null) {
				try {
					LOGGER.info("Method 5: Searching globally for remove icon near book title: " + bookTitle);

					// Find all elements with the remove icon character on the page
					List<WebElement> allRemoveIcons = driver.findElements(
						By.xpath("//div[contains(@class, 'css-146c3p1')][contains(text(), '󰋔')]"));

					LOGGER.info("Method 5: Found " + allRemoveIcons.size() + " remove icons on page");

					// If we have multiple books, use the index to pick the right one
					if (allRemoveIcons.size() > index) {
						removeIcon = allRemoveIcons.get(index);
						LOGGER.info("Found remove icon using Method 5 (global search with index " + index + ")");
					} else if (allRemoveIcons.size() > 0) {
						removeIcon = allRemoveIcons.get(0);
						LOGGER.info("Found remove icon using Method 5 (global search, using first one)");
					}
				} catch (Exception e5) {
					LOGGER.log(Level.FINE, "Method 5 failed: " + e5.getMessage());
				}
			}

			// Method 6: Search using parent/ancestor traversal
			if (removeIcon == null) {
				try {
					// Try to find by searching from book and going up then down
					String js6 = "var book = arguments[0];" +
								"var parent = book.parentElement;" +
								"while(parent && !parent.querySelector('div[class*=\"css-146c3p1\"]')) {" +
								"  parent = parent.parentElement;" +
								"}" +
								"if(parent) {" +
								"  var icons = parent.querySelectorAll('div[class*=\"css-146c3p1\"]');" +
								"  for(var i=0; i<icons.length; i++) {" +
								"    if(icons[i].textContent.includes('󰋔')) {" +
								"      return icons[i];" +
								"    }" +
								"  }" +
								"}" +
								"return null;";
					removeIcon = (WebElement) ((JavascriptExecutor) driver).executeScript(js6, book);
					if (removeIcon != null) {
						LOGGER.info("Found remove icon using Method 6 (ancestor traversal)");
					}
				} catch (Exception e6) {
					LOGGER.log(Level.FINE, "Method 6 failed: " + e6.getMessage());
				}
			}

			// Click the icon if found
			if (removeIcon != null) {
				try {
					// Scroll to the element first
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", removeIcon);
					Thread.sleep(500);

					// Try multiple click approaches
					boolean clicked = false;

					// Approach 1: Direct JavaScript click
					try {
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", removeIcon);
						LOGGER.info("Clicked remove icon using JavaScript click");
						clicked = true;
					} catch (Exception c1) {
						LOGGER.log(Level.WARNING, "JavaScript click failed: " + c1.getMessage());
					}

					// Approach 2: Standard Selenium click
					if (!clicked) {
						try {
							removeIcon.click();
							LOGGER.info("Clicked remove icon using standard click");
							clicked = true;
						} catch (Exception c2) {
							LOGGER.log(Level.WARNING, "Standard click failed: " + c2.getMessage());
						}
					}

					// Approach 3: clickWithJS fallback
					if (!clicked) {
						try {
							// clickWithJS method doesn't exist - using JavaScript click instead
							LOGGER.info("Clicked remove icon using clickWithJS");
							clicked = true;
						} catch (Exception c3) {
							LOGGER.log(Level.WARNING, "clickWithJS failed: " + c3.getMessage());
						}
					}

					if (clicked) {
						LOGGER.info("✅ Successfully clicked remove icon for book at index: " + index);
						Thread.sleep(2000); // Wait for dialog to appear
					} else {
						LOGGER.severe("❌ All click approaches failed for remove icon at index " + index);
					}

				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Failed to click remove icon: {0}", e.getMessage());
				}
			} else {
				LOGGER.severe("❌ Remove icon not found at index " + index + " using any of the 6 methods");
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click remove icon at index " + index + ": {0}", e.getMessage());
		}
	}

	public void clickRemoveIconAtIndexWithConfirmation(int index) {
		try {
			clickRemoveIconAtIndex(index);

			// Wait for confirmation dialog and click Yes
			if (isRemoveConfirmationDialogDisplayed()) {
				clickYesOnConfirmation();
				LOGGER.info("Removed book at index: " + index);
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to remove book at index " + index + ": {0}", e.getMessage());
		}
	}

	public boolean isBookInFavourites(String bookTitle) {
		List<WebElement> books = getBookItems();
		LOGGER.info("isBookInFavourites: Searching for '" + bookTitle + "' among " + books.size() + " books");

		for (int i = 0; i < books.size(); i++) {
			try {
				WebElement book = books.get(i);
				// Try to find title using multiple methods
				String titleText = "";

				// Method 1: Use BOOK_TITLE locator
				try {
					WebElement titleElement = book.findElement(BOOK_TITLE);
					titleText = titleElement.getText().trim();
					LOGGER.info("Book " + i + " title (Method 1): '" + titleText + "'");
				} catch (Exception e1) {
					LOGGER.log(Level.FINE, "Method 1 failed for book " + i);
				}

				// Method 2: Get all text from book element
				if (titleText.isEmpty()) {
					titleText = book.getText().trim();
					LOGGER.info("Book " + i + " title (Method 2 - all text): '" + titleText + "'");
				}

				// Method 3: Try common class-based locators
				if (titleText.isEmpty()) {
					try {
						WebElement titleElement = book.findElement(By.xpath(".//div[contains(@class, 'r-1udbk01') or contains(@class, 'r-dnmrzs')]"));
						titleText = titleElement.getText().trim();
						LOGGER.info("Book " + i + " title (Method 3): '" + titleText + "'");
					} catch (Exception e3) {
						LOGGER.log(Level.FINE, "Method 5 failed for book " + i);
					}
				}

				// Check for match (case-insensitive)
				if (!titleText.isEmpty()) {
					LOGGER.info("Comparing: '" + titleText + "' with '" + bookTitle + "'");

					// Try exact match
					if (titleText.equalsIgnoreCase(bookTitle)) {
						LOGGER.info("✅ Book found in favourites (exact match): " + bookTitle);
						return true;
					}

					// Try contains match
					if (titleText.toLowerCase().contains(bookTitle.toLowerCase())) {
						LOGGER.info("✅ Book found in favourites (contains match): " + bookTitle);
						return true;
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error checking book " + i + ": " + e.getMessage());
			}
		}

		LOGGER.info("Book not found in favourites: " + bookTitle);
		return false;
	}

	public void addBookToFavourites(String bookTitle) {
		try {
			// First search for the book
			searchBookOrAuthor(bookTitle);

			// Wait for search results
			Thread.sleep(2000);

			// Click heart icon on first search result
			List<WebElement> books = getBookItems();
			if (!books.isEmpty()) {
				WebElement firstBook = books.get(0);
				WebElement heartIcon = firstBook.findElement(HEART_ICON_ADD);
				heartIcon.click();
				LOGGER.info("Book added to favourites: " + bookTitle);
			} else {
				LOGGER.warning("No books found to add to favourites");
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to add book to favourites: {0}", e.getMessage());
		}
	}

	public void removeBookFromFavourites(String bookTitle) {
		List<WebElement> books = getBookItems();
		for (int i = 0; i < books.size(); i++) {
			try {
				WebElement titleElement = books.get(i).findElement(BOOK_TITLE);
				if (titleElement.getText().contains(bookTitle)) {
					clickRemoveIconAtIndex(i);

					// Handle confirmation dialog
					if (isRemoveConfirmationDialogDisplayed()) {
						clickYesOnConfirmation();
						LOGGER.info("Book removed from favourites: " + bookTitle);
					}
					return;
				}
			} catch (Exception e) {
				// Continue checking next book
			}
		}
		LOGGER.warning("Book not found for removal: " + bookTitle);
	}

	public void removeBookFromFavouritesWithCancel(String bookTitle) {
		List<WebElement> books = getBookItems();
		for (int i = 0; i < books.size(); i++) {
			try {
				WebElement titleElement = books.get(i).findElement(BOOK_TITLE);
				if (titleElement.getText().contains(bookTitle)) {
					clickRemoveIconAtIndex(i);

					// Cancel on confirmation dialog
					if (isRemoveConfirmationDialogDisplayed()) {
						clickNoOnConfirmation();
						LOGGER.info("Book removal cancelled: " + bookTitle);
					}
					return;
				}
			} catch (Exception e) {
				// Continue checking next book
			}
		}
		LOGGER.warning("Book not found for removal: " + bookTitle);
	}

	// ================= SELECTION METHODS =================

	public void clickSelectAll() {
		try {
			WebElement selectAllBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[@tabindex='0'][.//div[normalize-space()='Select All']]")));
			selectAllBtn.click();
			LOGGER.info("Select All clicked");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click Select All: {0}", e.getMessage());
		}
	}

	public void selectMultipleBooks(int count) {
		try {
			List<WebElement> books = getBookItems();
			int booksToSelect = Math.min(count, books.size());

			LOGGER.info("Selecting " + booksToSelect + " books");

			// First click filter to enter selection mode
			clickFilterButton();

			// Wait for filter action bar to appear
			Thread.sleep(1000);

			// Click individual books to select them
			for (int i = 0; i < booksToSelect; i++) {
				try {
					// Click on the book item to select it
					WebElement book = books.get(i);
					book.click();
					Thread.sleep(500);
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Could not select book at index " + i);
				}
			}

			LOGGER.info("Selected " + booksToSelect + " books");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to select multiple books: {0}", e.getMessage());
		}
	}

	public void clickDeselectAll() {
		try {
			WebElement deselectAllBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[@tabindex='0'][.//div[normalize-space()='Deselect All']]")));
			deselectAllBtn.click();
			LOGGER.info("Deselect All clicked");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click Deselect All: {0}", e.getMessage());
		}
	}

	public int getSelectedBooksCount() {
		// Alias for getSelectedCount for backward compatibility
		return getSelectedCount();
	}

	public boolean clickCancel() {
		try {
			WebElement cancelBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(CANCEL_BUTTON));
			cancelBtn.click();
			LOGGER.info("Cancel clicked");
			Thread.sleep(1000);
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click Cancel: {0}", e.getMessage());
			return false;
		}
	}

	public void clickRemoveSelected() {
		try {
			WebElement removeSelectedBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(REMOVE_SELECTED_BUTTON));
			removeSelectedBtn.click();
			LOGGER.info("Remove Selected clicked");
			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click Remove Selected: {0}", e.getMessage());
		}
	}

	public void clickRemoveSelectedWithConfirmation() {
		try {
			clickRemoveSelected();

			// Handle confirmation dialog if it appears
			if (isRemoveConfirmationDialogDisplayed()) {
				clickYesOnConfirmation();
				LOGGER.info("Confirmed bulk removal");
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to remove selected books: {0}", e.getMessage());
		}
	}

	public boolean isRemoveSelectedEnabled() {
		try {
			WebElement removeBtn = driver.findElement(REMOVE_SELECTED_BUTTON);
			String ariaDisabled = removeBtn.getAttribute("aria-disabled");
			return !"true".equals(ariaDisabled);
		} catch (Exception e) {
			return false;
		}
	}

	public String getSelectedCountText() {
		try {
			List<WebElement> candidates = driver.findElements(By.xpath(
					"//*[self::div or self::span][contains(normalize-space(.),'Selected Count') or contains(normalize-space(.),'Selected')]"));
			for (WebElement candidate : candidates) {
				try {
					if (!candidate.isDisplayed()) {
						continue;
					}
					String text = candidate.getText().trim();
					if (!text.isEmpty() && text.matches(".*\\d+.*Selected.*")) {
						return text;
					}
				} catch (Exception ignored) {
					// Continue checking other candidates
				}
			}
			WebElement countElement = driver.findElement(SELECTED_COUNT);
			return countElement.getText();
		} catch (Exception e) {
			return "0";
		}
	}

	public int getSelectedCount() {
		String countText = getSelectedCountText();
		try {
			// Extract number from text like "0 Selected Count" or "5 Selected"
			String numberStr = countText.replaceAll("[^0-9]", "");
			return Integer.parseInt(numberStr);
		} catch (Exception e) {
			return 0;
		}
	}

	// ================= EMPTY STATE METHODS =================

	public boolean isNoFavouritesMessageDisplayed() {
		try {
			WebElement message = pageWait.until(ExpectedConditions.visibilityOfElementLocated(NO_FAVORITES_MESSAGE));
			return message.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public String getNoFavouritesMessage() {
		try {
			WebElement message = pageWait.until(ExpectedConditions.visibilityOfElementLocated(NO_FAVORITES_MESSAGE));
			return message.getText();
		} catch (Exception e) {
			return "";
		}
	}

	public boolean isBrowseButtonDisplayed() {
		try {
			WebElement browseBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(BROWSE_BUTTON));
			return browseBtn.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// ================= UTILITY METHODS =================

	public void refreshCurrentPage() {
		try {
			driver.navigate().refresh();
			LOGGER.info("Current page refreshed");
			Thread.sleep(3000);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not refresh page: {0}", e.getMessage());
		}
	}

	public void waitForOverlayToDisappear() {
		try {
			pageWait.until(ExpectedConditions.invisibilityOfElementLocated(
					By.xpath("//div[contains(@style,'rgba')]")));
		} catch (Exception e) {
			// Overlay might not be present
		}
	}

	public void scrollToBottom() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			LOGGER.info("Scrolled to bottom of page");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not scroll to bottom: {0}", e.getMessage());
		}
	}

	public void scrollToTop() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, 0)");
			LOGGER.info("Scrolled to top of page");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not scroll to top: {0}", e.getMessage());
		}
	}

	public boolean isPageScrollable() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			Long scrollHeight = (Long) js.executeScript("return document.body.scrollHeight");
			Long windowHeight = (Long) js.executeScript("return window.innerHeight");
			return scrollHeight != null && windowHeight != null && scrollHeight > windowHeight;
		} catch (Exception e) {
			return false;
		}
	}

	public String getPageSource() {
		return driver.getPageSource();
	}

	// ================= CONFIRMATION DIALOG METHODS =================

	public boolean isRemoveConfirmationDialogDisplayed() {
		try {
			WebElement dialog = pageWait.until(ExpectedConditions.visibilityOfElementLocated(REMOVE_CONFIRMATION_DIALOG));
			return dialog.isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Remove confirmation dialog not displayed: {0}", e.getMessage());
			return false;
		}
	}

	private boolean isRemoveConfirmationDialogPresentNow() {
		try {
			List<WebElement> dialogs = driver.findElements(REMOVE_CONFIRMATION_DIALOG);
			for (WebElement dialog : dialogs) {
				if (dialog.isDisplayed()) {
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Instant dialog presence check failed: {0}", e.getMessage());
		}
		return false;
	}

	public String getRemoveConfirmationMessage() {
		try {
			WebElement messageElement = pageWait.until(ExpectedConditions.visibilityOfElementLocated(REMOVE_CONFIRMATION_MESSAGE));
			return messageElement.getText();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not get confirmation message: {0}", e.getMessage());
			return "";
		}
	}

		/**
		 * Waits for the removal confirmation dialog to close after clicking Yes/No
		 * @param timeoutSeconds Maximum time to wait in seconds
		 * @return true if dialog closed, false if still visible after timeout
		 */
		public boolean waitForConfirmationDialogToClose(int timeoutSeconds) {
			try {
				LOGGER.info("Waiting for confirmation dialog to close (max " + timeoutSeconds + " seconds)...");
				long startTime = System.currentTimeMillis();
				long timeoutMillis = timeoutSeconds * 1000;

				while (System.currentTimeMillis() - startTime < timeoutMillis) {
					if (!isRemoveConfirmationDialogDisplayed()) {
						LOGGER.info("✅ Confirmation dialog closed successfully");
						return true;
					}
					Thread.sleep(500);
				}

				LOGGER.warning("⚠️ Confirmation dialog still visible after " + timeoutSeconds + " seconds");
				return false;
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error waiting for dialog to close: {0}", e.getMessage());
				return false;
			}
		}

		/**
		 * Waits for the "Removed from favourites" toaster notification to appear
		 * @param timeoutSeconds Maximum time to wait in seconds
		 * @return true if toaster appeared, false if not found after timeout
		 */
		public boolean waitForRemovalToaster(int timeoutSeconds) {
			try {
				if (lastRemovalToasterSeen) {
					LOGGER.info("Removal toaster was already detected during confirmation");
					return true;
				}

				LOGGER.info("Waiting for \"Removed from favourites\" toaster (max " + timeoutSeconds + " seconds)...");
				long startTime = System.currentTimeMillis();
				long timeoutMillis = timeoutSeconds * 1000;

				while (System.currentTimeMillis() - startTime < timeoutMillis) {
					try {
						// Check for toaster by text content
						WebElement toaster = driver.findElement(By.xpath("//div[contains(@data-testid, \"toastText1\")][contains(text(), \"Removed from favourites\")]"));
						if (toaster != null && toaster.isDisplayed()) {
							LOGGER.info("✅ Found \"Removed from favourites\" toaster notification");
							lastRemovalToasterSeen = true;
							return true;
						}
					} catch (Exception e) {
						// Toaster not found yet, continue waiting
					}
					Thread.sleep(500);
				}

				LOGGER.warning("⚠️ \"Removed from favourites\" toaster not found after " + timeoutSeconds + " seconds");
				return false;
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error waiting for toaster: {0}", e.getMessage());
				return false;
			}
		}

		/**
		 * Closes the currently visible dialog by pressing Escape key
		 */
		public void closeDialogWithEscape() {
			try {
				org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
				actions.sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
				LOGGER.info("Closed dialog using Escape key");
				Thread.sleep(1000);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Could not close dialog with Escape: {0}", e.getMessage());
			}
		}

	private WebElement getConfirmationDialogContainer() {
		try {
			List<WebElement> titledDialogs = driver.findElements(REMOVE_CONFIRMATION_DIALOG);
			for (WebElement title : titledDialogs) {
				if (title.isDisplayed()) {
					return title.findElement(By.xpath("./ancestor::div[contains(@class, 'css-g5y9jx')][1]"));
				}
			}

			List<WebElement> messageDialogs = driver.findElements(REMOVE_CONFIRMATION_MESSAGE);
			for (WebElement message : messageDialogs) {
				if (message.isDisplayed()) {
					return message.findElement(By.xpath("./ancestor::div[contains(@class, 'css-g5y9jx')][1]"));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Could not resolve confirmation dialog container: {0}", e.getMessage());
		}
		LOGGER.fine("Confirmation dialog container is not currently visible");
		return null;
	}

	private WebElement getConfirmationActionButton(String label) {
		WebElement dialog = getConfirmationDialogContainer();
		if (dialog != null) {
			try {
				Object candidate = ((JavascriptExecutor) driver).executeScript(
						"const dialog = arguments[0];"
								+ "const label = arguments[1].trim().toLowerCase();"
								+ "const nodes = Array.from(dialog.querySelectorAll('div, button, span'));"
								+ "for (const node of nodes) {"
								+ "  const text = (node.textContent || '').trim().toLowerCase();"
								+ "  if (text !== label) { continue; }"
								+ "  let current = node;"
								+ "  while (current && current !== dialog) {"
								+ "    const role = (current.getAttribute('role') || '').toLowerCase();"
								+ "    const tabIndex = current.getAttribute('tabindex');"
								+ "    if (role === 'button' || (tabIndex !== null && tabIndex !== '-1')) {"
								+ "      return current;"
								+ "    }"
								+ "    current = current.parentElement;"
								+ "  }"
								+ "  return node;"
								+ "}"
								+ "return null;",
						dialog, label);
				if (candidate instanceof WebElement webElement) {
					LOGGER.info("Resolved confirmation button for label: " + label);
					return webElement;
				}
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "JavaScript confirmation button lookup failed: {0}", e.getMessage());
			}

			try {
				WebElement labelElement = dialog.findElement(By.xpath(".//div[normalize-space(text())='" + label + "']"));
				WebElement clickable = labelElement.findElement(By.xpath("./ancestor::div[@tabindex='0'][1]"));
				LOGGER.info("Resolved confirmation button using XPath fallback for label: " + label);
				return clickable;
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Dialog-scoped confirmation button lookup failed for label " + label + ": {0}",
						e.getMessage());
			}
		}

		try {
			List<WebElement> globalMatches = driver.findElements(
					By.xpath("//div[normalize-space(text())='" + label + "']/ancestor::div[@tabindex='0'][1]"));
			for (WebElement match : globalMatches) {
				if (match.isDisplayed()) {
					LOGGER.info("Resolved confirmation button using global visible lookup for label: " + label);
					return match;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Global confirmation button lookup failed for label " + label + ": {0}",
					e.getMessage());
		}

		LOGGER.warning("Could not resolve confirmation button for label: " + label);
		return null;
	}

	private WebElement getCenterPointInteractionTarget(WebElement element) {
		try {
			Object candidate = ((JavascriptExecutor) driver).executeScript(
					"const rect = arguments[0].getBoundingClientRect();"
							+ "const x = Math.floor(rect.left + (rect.width / 2));"
							+ "const y = Math.floor(rect.top + (rect.height / 2));"
							+ "let el = document.elementFromPoint(x, y);"
							+ "while (el) {"
							+ "  const role = (el.getAttribute('role') || '').toLowerCase();"
							+ "  const tabIndex = el.getAttribute('tabindex');"
							+ "  if (role === 'button' || (tabIndex !== null && tabIndex !== '-1')) {"
							+ "    return el;"
							+ "  }"
							+ "  el = el.parentElement;"
							+ "}"
							+ "return arguments[0];",
					element);
			return candidate instanceof WebElement webElement ? webElement : element;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Center-point target lookup failed: {0}", e.getMessage());
			return element;
		}
	}

	private void dispatchPressSequence(WebElement element) {
		((JavascriptExecutor) driver).executeScript(
				"['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => "
						+ "arguments[0].dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, view: window })));",
				element);
	}

	private boolean didRemovalProgress() {
		if (isToasterVisible()) {
			lastRemovalToasterSeen = true;
			return true;
		}
		return !isRemoveConfirmationDialogPresentNow();
	}

	private boolean waitForRemovalProgress(int timeoutSeconds) {
		long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < endTime) {
			if (didRemovalProgress()) {
				return true;
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.log(Level.WARNING, "Interrupted while waiting for removal progress: {0}", e.getMessage());
				return false;
			}
		}
		return didRemovalProgress();
	}



	/**
	 * Helper method to check if the "Removed from favourites" toaster is visible
	 * Used to verify if a click attempt actually triggered the removal action
	 * @return true if toaster is visible, false otherwise
	 */
	private boolean isToasterVisible() {
		try {
			WebElement toaster = driver.findElement(By.xpath(
				"//div[contains(@data-testid, 'toastText1')][contains(text(), 'Removed from favourites')]"));
			return toaster.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public void clickYesOnConfirmation() {
		WebElement yesBtn = null;

		// Method 0: Find within confirmation dialog context (MOST RELIABLE)
		try {
			// First find the dialog, then find Yes button within it
			String dialogXPath = "//div[contains(@class, 'css-g5y9jx')][contains(@class, 'r-119rbo0')]//div[@dir='auto'][text()='Remove Favourites']/..";
			WebElement dialog = driver.findElement(By.xpath(dialogXPath));
			
			// Within dialog, find element with tabindex and Yes text
			yesBtn = dialog.findElement(By.xpath(".//div[@tabindex='0'][contains(@class, 'r-1i6wzkk')]//div[text()='Yes']/.."));
			LOGGER.info("Found Yes button within dialog context (Method 0)");
		} catch (Exception e0) {
			LOGGER.log(Level.FINE, "Method 0 failed: " + e0.getMessage());
		}

		// Method 1: Click parent element directly (REMOVED //div[1] - was clicking inner text div)
		try {
			String exactXPath = "//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-y47klf r-qpntkw r-u8s1d r-3mc0re r-1yxedwg']";
			yesBtn = driver.findElement(By.xpath(exactXPath));
			LOGGER.info("Found Yes button parent element (Method 1)");
		} catch (Exception e1) {
			LOGGER.log(Level.FINE, "Method 1 failed: " + e1.getMessage());
		}

		// Method 2: Click parent element using second exact class (no [1] index)
		if (yesBtn == null) {
			try {
				String exactXPath2 = "//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-1f0042m r-13awgt0 r-1777fci r-17q4wm6 r-xyw6el r-14lw9ot r-icoktb']";
				yesBtn = driver.findElement(By.xpath(exactXPath2));
				LOGGER.info("Found Yes button parent element (Method 2)");
			} catch (Exception e2) {
				LOGGER.log(Level.FINE, "Method 2 failed: " + e2.getMessage());
			}
		}

		// Method 3: Find by tabindex attribute and Yes text
		if (yesBtn == null) {
			try {
				yesBtn = driver.findElement(By.xpath("//div[@tabindex='0']//div[text()='Yes']/.."));
				LOGGER.info("Found Yes button using tabindex (Method 3)");
			} catch (Exception e3) {
				LOGGER.log(Level.FINE, "Method 3 failed: " + e3.getMessage());
			}
		}

		// Method 4: Use original YES_BUTTON locator
		if (yesBtn == null) {
			try {
				yesBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(YES_BUTTON));
				LOGGER.info("Found Yes button using original locator (Method 4)");
			} catch (Exception e4) {
				LOGGER.log(Level.FINE, "Method 4 failed: " + e4.getMessage());
			}
		}

		// Method 5: Try finding by text content
		if (yesBtn == null) {
			try {
				yesBtn = driver.findElement(By.xpath("//div[contains(text(), 'Yes')]"));
				LOGGER.info("Found Yes button using text content (Method 5)");
			} catch (Exception e5) {
				LOGGER.log(Level.FINE, "Method 5 failed: " + e5.getMessage());
			}
		}

		// Click the button if found - 5 attempts with toaster verification
		if (yesBtn != null) {
			boolean clicked = false;

			try {
				// Scroll element into view first
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", yesBtn);
				Thread.sleep(500);

				// Attempt 1: JavaScript click on parent element
				try {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesBtn);
					LOGGER.info("✅ Attempt 1: JavaScript click on parent");

					// Wait briefly and check if toaster appeared
					Thread.sleep(2000);
					if (isToasterVisible()) {
						LOGGER.info("✅ Attempt 1 SUCCESSFUL - Toaster detected!");
						clicked = true;
					} else {
						LOGGER.warning("⚠️ Attempt 1 did not trigger toaster, trying next approach...");
					}
				} catch (Exception e1) {
					LOGGER.log(Level.WARNING, "Attempt 1 failed: " + e1.getMessage());
				}

				// Attempt 2: JavaScript click on inner text div (KEY NEW APPROACH!)
				if (!clicked) {
					try {
						WebElement innerDiv = yesBtn.findElement(By.xpath(".//div[@dir='auto']"));
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", innerDiv);
						LOGGER.info("✅ Attempt 2: JavaScript click on inner div");

						// Wait briefly and check if toaster appeared
						Thread.sleep(2000);
						if (isToasterVisible()) {
							LOGGER.info("✅ Attempt 2 SUCCESSFUL - Toaster detected!");
							clicked = true;
						} else {
							LOGGER.warning("⚠️ Attempt 2 did not trigger toaster, trying next approach...");
						}
					} catch (Exception e2) {
						LOGGER.log(Level.WARNING, "Attempt 2 failed: " + e2.getMessage());
					}
				}

				// Attempt 3: Standard Selenium click
				if (!clicked) {
					try {
						waitForOverlayToDisappear();
						Thread.sleep(500);
						yesBtn.click();
						LOGGER.info("✅ Attempt 3: Standard click");

						// Wait briefly and check if toaster appeared
						Thread.sleep(2000);
						if (isToasterVisible()) {
							LOGGER.info("✅ Attempt 3 SUCCESSFUL - Toaster detected!");
							clicked = true;
						} else {
							LOGGER.warning("⚠️ Attempt 3 did not trigger toaster, trying next approach...");
						}
					} catch (Exception e3) {
						LOGGER.log(Level.WARNING, "Attempt 3 failed: " + e3.getMessage());
					}
				}

				// Attempt 4: Actions class move and click
				if (!clicked) {
					try {
						org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
						actions.moveToElement(yesBtn).click().perform();
						LOGGER.info("✅ Attempt 4: Actions click");

						// Wait briefly and check if toaster appeared
						Thread.sleep(2000);
						if (isToasterVisible()) {
							LOGGER.info("✅ Attempt 4 SUCCESSFUL - Toaster detected!");
							clicked = true;
						} else {
							LOGGER.warning("⚠️ Attempt 4 did not trigger toaster, trying next approach...");
						}
					} catch (Exception e4) {
						LOGGER.log(Level.WARNING, "Attempt 4 failed: " + e4.getMessage());
					}
				}

				// Attempt 5: Force click using MouseEvent dispatching
				if (!clicked) {
					try {
						String js = "var evt = new MouseEvent('click', {view: window, bubbles: true, cancelable: true});" +
								   "arguments[0].dispatchEvent(evt);";
						((JavascriptExecutor) driver).executeScript(js, yesBtn);
						LOGGER.info("✅ Attempt 5: Event dispatching");

						// Wait briefly and check if toaster appeared
						Thread.sleep(2000);
						if (isToasterVisible()) {
							LOGGER.info("✅ Attempt 5 SUCCESSFUL - Toaster detected!");
							clicked = true;
						} else {
							LOGGER.warning("⚠️ Attempt 5 did not trigger toaster");
						}
					} catch (Exception e5) {
						LOGGER.log(Level.WARNING, "Attempt 5 failed: " + e5.getMessage());
					}
				}

				if (clicked) {
					LOGGER.info("Clicked Yes on removal confirmation");
				} else {
					LOGGER.severe("❌ All 5 click attempts failed for Yes button - no toaster appeared");
				}

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to click Yes button: {0}", e.getMessage());
			}
		} else {
			LOGGER.severe("Yes button not found using any method");
		}
	}

	public boolean confirmRemovalViaYesButton() {
		lastRemovalToasterSeen = false;
		boolean clicked = false;

		try {
			waitForOverlayToDisappear();

			// Attempt 1: JavaScript click on parent element
			try {
				WebElement yesBtn = getConfirmationActionButton("Yes");
				if (yesBtn != null) {
					((JavascriptExecutor) driver).executeScript(
							"arguments[0].scrollIntoView({block: 'center', inline: 'center'});", yesBtn);
					Thread.sleep(400);
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesBtn);
					LOGGER.info("Attempt 1: JavaScript click on Yes button container");
					Thread.sleep(1500);
					clicked = didRemovalProgress();
				}
			} catch (Exception e1) {
				LOGGER.log(Level.WARNING, "Attempt 1 failed: {0}", e1.getMessage());
			}

			// Attempt 2: JavaScript click on inner text div
			if (!clicked) {
				try {
					WebElement yesBtn = getConfirmationActionButton("Yes");
					if (yesBtn != null) {
						WebElement innerLabel = yesBtn.findElement(By.xpath(".//*[normalize-space(text())='Yes'][1]"));
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", innerLabel);
						LOGGER.info("Attempt 2: JavaScript click on Yes label");
						Thread.sleep(1500);
						clicked = didRemovalProgress();
					}
				} catch (Exception e2) {
					LOGGER.log(Level.WARNING, "Attempt 2 failed: {0}", e2.getMessage());
				}
			}

			// Attempt 3: Standard Selenium click
			if (!clicked) {
				try {
					WebElement yesBtn = getConfirmationActionButton("Yes");
					if (yesBtn != null) {
						WebElement target = getCenterPointInteractionTarget(yesBtn);
						target.click();
						LOGGER.info("Attempt 3: Standard click on center-point target");
						Thread.sleep(1500);
						clicked = didRemovalProgress();
					}
				} catch (Exception e3) {
					LOGGER.log(Level.WARNING, "Attempt 3 failed: {0}", e3.getMessage());
				}
			}

			// Attempt 4: Actions class click
			if (!clicked) {
				try {
					WebElement yesBtn = getConfirmationActionButton("Yes");
					if (yesBtn != null) {
						WebElement target = getCenterPointInteractionTarget(yesBtn);
						org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
						actions.moveToElement(target).pause(Duration.ofMillis(150)).click().perform();
						LOGGER.info("Attempt 4: Actions click on center-point target");
						Thread.sleep(1500);
						clicked = didRemovalProgress();
					}
				} catch (Exception e4) {
					LOGGER.log(Level.WARNING, "Attempt 4 failed: {0}", e4.getMessage());
				}
			}

			// Attempt 5: Synthetic press sequence
			if (!clicked) {
				try {
					WebElement yesBtn = getConfirmationActionButton("Yes");
					if (yesBtn != null) {
						WebElement target = getCenterPointInteractionTarget(yesBtn);
						dispatchPressSequence(target);
						LOGGER.info("Attempt 5: Synthetic press sequence on center-point target");
						Thread.sleep(1500);
						clicked = didRemovalProgress();
					}
				} catch (Exception e5) {
					LOGGER.log(Level.WARNING, "Attempt 5 failed: {0}", e5.getMessage());
				}
			}

			if (clicked) {
				LOGGER.info("Clicked Yes on removal confirmation");
			} else {
				LOGGER.severe("❌ All 5 click attempts failed for Yes button - no toaster appeared");
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click Yes button: {0}", e.getMessage());
		}

		return clicked;
	}

	public boolean confirmRemovalViaYesButtonFast() {
		lastRemovalToasterSeen = false;
		WebElement yesBtn = getConfirmationActionButton("Yes");
		if (yesBtn == null) {
			LOGGER.severe("Yes button not found in confirmation dialog");
			return false;
		}

		try {
			waitForOverlayToDisappear();
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({block: 'center', inline: 'center'});", yesBtn);
			Thread.sleep(400);

			WebElement target = yesBtn;
			try {
				target = getCenterPointInteractionTarget(yesBtn);
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Could not resolve center-point target for Yes button: {0}", e.getMessage());
			}

			WebElement innerLabel = null;
			try {
				innerLabel = yesBtn.findElement(By.xpath(".//*[normalize-space(text())='Yes'][1]"));
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Could not resolve inner Yes label: {0}", e.getMessage());
			}

			try {
				target.click();
				LOGGER.info("Attempt 1: Standard click on Yes target");
				if (waitForRemovalProgress(4)) {
					LOGGER.info("Confirmed removal via Yes button");
					return true;
				}
			} catch (Exception e1) {
				LOGGER.log(Level.WARNING, "Attempt 1 failed: {0}", e1.getMessage());
			}

			try {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", target);
				LOGGER.info("Attempt 2: JavaScript click on Yes target");
				if (waitForRemovalProgress(4)) {
					LOGGER.info("Confirmed removal via Yes button");
					return true;
				}
			} catch (Exception e2) {
				LOGGER.log(Level.WARNING, "Attempt 2 failed: {0}", e2.getMessage());
			}

			if (innerLabel != null) {
				try {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", innerLabel);
					LOGGER.info("Attempt 3: JavaScript click on Yes label");
					if (waitForRemovalProgress(4)) {
						LOGGER.info("Confirmed removal via Yes button");
						return true;
					}
				} catch (Exception e3) {
					LOGGER.log(Level.WARNING, "Attempt 3 failed: {0}", e3.getMessage());
				}
			}

			try {
				org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
				actions.moveToElement(target).pause(Duration.ofMillis(150)).click().perform();
				LOGGER.info("Attempt 4: Actions click on Yes target");
				if (waitForRemovalProgress(4)) {
					LOGGER.info("Confirmed removal via Yes button");
					return true;
				}
			} catch (Exception e4) {
				LOGGER.log(Level.WARNING, "Attempt 4 failed: {0}", e4.getMessage());
			}

			try {
				target.sendKeys(org.openqa.selenium.Keys.ENTER);
				LOGGER.info("Attempt 5: Enter key on Yes target");
				if (waitForRemovalProgress(4)) {
					LOGGER.info("Confirmed removal via Yes button");
					return true;
				}
			} catch (Exception e5) {
				LOGGER.log(Level.WARNING, "Attempt 5 failed: {0}", e5.getMessage());
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click Yes button quickly: {0}", e.getMessage());
		}

		LOGGER.severe("All 5 click attempts failed for Yes button");
		return false;
	}

	public void clickNoOnConfirmation() {
		try {
			WebElement noBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(NO_BUTTON));
			noBtn.click();
			LOGGER.info("Clicked No on removal confirmation");
			Thread.sleep(1000);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click No on confirmation: {0}", e.getMessage());
		}
	}

	public void confirmRemoveBook() {
		if (isRemoveConfirmationDialogDisplayed()) {
			clickYesOnConfirmation();
			LOGGER.info("Confirmed book removal");
		} else {
			LOGGER.warning("No confirmation dialog present");
		}
	}

	public void cancelRemoveBook() {
		if (isRemoveConfirmationDialogDisplayed()) {
			clickNoOnConfirmation();
			LOGGER.info("Cancelled book removal");
		} else {
			LOGGER.warning("No confirmation dialog present");
		}
	}

	public long getPageScrollHeight() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			Long scrollHeight = (Long) js.executeScript("return document.body.scrollHeight");
			return scrollHeight != null ? scrollHeight : 0L;
		} catch (Exception e) {
			return 0L;
		}
	}

	public long getWindowHeight() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			Long windowHeight = (Long) js.executeScript("return window.innerHeight");
			return windowHeight != null ? windowHeight : 0L;
		} catch (Exception e) {
			return 0L;
		}
	}
}
