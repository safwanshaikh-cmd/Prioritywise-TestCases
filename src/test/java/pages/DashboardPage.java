package pages;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;

/**
 * Page object representing the dashboard.
 */
public class DashboardPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(DashboardPage.class.getName());
	private static final By BOOK_IMAGES = By.xpath("//img[contains(@src,'sonarplay')]");
	private static final By DASHBOARD_SHELL = By.xpath(
			"//button[@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu']"
					+ " | //*[@role='button' and (@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu')]"
					+ " | //div[contains(.,'Trending')]"
					+ " | //div[contains(.,'Categories')]"
					+ " | //div[contains(.,'New Release')]"
					+ " | //div[contains(.,'Upcoming Releases')]"
					+ " | //img[contains(@src,'covers')]"
					+ " | //img[contains(@src,'sonarplay')]");
	private static final By RECENTLY_PLAYED_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'RECENTLY PLAYED','recently played'),'recently played')"
					+ " or contains(translate(normalize-space(.),'RECENTLY LISTENED','recently listened'),'recently listened')]");
	private static final By COOKIE_ACCEPT_BUTTON = By.xpath("//button[contains(normalize-space(.),'Accept Cookies')]"
			+ " | //div[@tabindex='0' and contains(normalize-space(.),'Accept Cookies')]"
			+ " | //*[@role='button' and contains(normalize-space(.),'Accept Cookies')]");
	private static final By RECOMMENDED_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'RECOMMENDED','recommended'),'recommended')"
						+ " or contains(translate(normalize-space(.),'FOR YOU','for you'),'for you')"
						+ " or contains(translate(normalize-space(.),'SUGGESTED','suggested'),'suggested')]");
	private static final By CONTINUE_LISTENING_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'CONTINUE LISTENING','continue listening'),'continue listening')"
						+ " or contains(translate(normalize-space(.),'RESUME','resume'),'resume')]");
	private static final By UPGRADE_BANNER = By.xpath(
			"//*[contains(translate(normalize-space(.),'UPGRADE','upgrade'),'upgrade')"
						+ " or contains(translate(normalize-space(.),'PREMIUM','premium'),'premium')"
						+ " or contains(translate(normalize-space(.),'SUBSCRIBE','subscribe'),'subscribe')"
						+ " or contains(translate(normalize-space(.),'GO PREMIUM','go premium'),'go premium')]");
	private static final By SEARCH_INPUT = By.xpath(
			"//input[contains(@placeholder, 'Search') or contains(@placeholder, 'search') or @type='search' or @enterkeyhint='search']");

	private static final By SEARCH_BUTTON = By.xpath(
			"//button[contains(@aria-label,'search') or @type='submit' or contains(.,'Search')]"
				+ " | //*[@role='button' and contains(.,'Search')]");
	private static final By PLAYLIST_WIDGET = By.xpath(
			"//*[contains(translate(normalize-space(.),'PLAYLIST','playlist'),'playlist')"
						+ " or contains(@href,'playlist')]");
	private static final By FAVORITE_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'FAVORITE','favorite'),'favorite')"
						+ " or contains(translate(normalize-space(.),'FAVOURITE','favourite'),'favourite')"
						+ " or contains(translate(normalize-space(.),'LIKED','liked'),'liked')"
						+ " or contains(translate(normalize-space(.),'SAVED','saved'),'saved')]");

	// ================= UPLOADER SPECIFIC LOCATORS =================
	private static final By UPLOAD_BUTTON = By.xpath(
			"//*[contains(translate(normalize-space(.),'UPLOAD','upload'),'upload')"
						+ " or contains(@aria-label,'upload')"
						+ " or contains(@class,'upload')]");
	private static final By UPLOAD_PAGE = By.xpath(
			"//*[contains(translate(normalize-space(.),'UPLOAD CONTENT','upload content'),'upload content')"
						+ " or contains(translate(normalize-space(.),'NEW UPLOAD','new upload'),'new upload')"
						+ " or contains(translate(normalize-space(.),'ADD CONTENT','add content'),'add content')]");
	private static final By CONTENT_STATS = By.xpath(
			"//*[contains(translate(normalize-space(.),'STATS','stats'),'stats')"
						+ " or contains(translate(normalize-space(.),'ANALYTICS','analytics'),'analytics')"
						+ " or contains(translate(normalize-space(.),'VIEWS','views'),'views')]");
	private static final By ANALYTICS_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'ANALYTICS','analytics'),'analytics')"
						+ " or contains(translate(normalize-space(.),'INSIGHTS','insights'),'insights')"
						+ " or contains(translate(normalize-space(.),'METRICS','metrics'),'metrics')]");
	private static final By DRAFTS_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'DRAFT','draft'),'draft')"
						+ " or contains(translate(normalize-space(.),'PENDING','pending'),'pending')]");
	private static final By CONTENT_LIST = By.xpath(
			"//*[contains(translate(normalize-space(.),'MY CONTENT','my content'),'my content')"
						+ " or contains(translate(normalize-space(.),'UPLOADED','uploaded'),'uploaded')"
						+ " or contains(translate(normalize-space(.),'CONTENT','content'),'content')]");
	private static final By EDIT_OPTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'EDIT','edit'),'edit')"
						+ " or contains(@aria-label,'edit')]");
	private static final By DELETE_OPTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'DELETE','delete'),'delete')"
						+ " or contains(translate(normalize-space(.),'REMOVE','remove'),'remove')]"
						+ " | //button[contains(@aria-label,'delete')]");


	private static final By LOGOUT_BUTTON = By.cssSelector("[data-testid='button_logout']");
	private static final By ADMIN_DASHBOARD = By.xpath(
				"//*[contains(translate(normalize-space(.),'ADMIN DASHBOARD','admin dashboard'),'admin dashboard')"
							+ " or contains(translate(normalize-space(.),'ADMIN PANEL','admin panel'),'admin panel')]");
	private static final By USER_MANAGEMENT = By.xpath(
			"//*[contains(translate(normalize-space(.),'USER MANAGEMENT','user management'),'user management')"
						+ " or contains(translate(normalize-space(.),'USERS','users'),'users')"
						+ " or contains(translate(normalize-space(.),'MANAGE USERS','manage users'),'manage users')]");
	private static final By ROLE_MANAGEMENT = By.xpath(
			"//*[contains(translate(normalize-space(.),'ROLE','role'),'role')"
						+ " or contains(translate(normalize-space(.),'PERMISSION','permission'),'permission')]");
	private static final By MODERATION_PANEL = By.xpath(
			"//*[contains(translate(normalize-space(.),'MODERATION','moderation'),'moderation')"
						+ " or contains(translate(normalize-space(.),'APPROVE','approve'),'approve')"
						+ " or contains(translate(normalize-space(.),'REVIEW','review'),'review')]");
	private static final By PLATFORM_ANALYTICS = By.xpath(
			"//*[contains(translate(normalize-space(.),'PLATFORM','platform'),'platform')"
						+ " or contains(translate(normalize-space(.),'SYSTEM ANALYTICS','system analytics'),'system analytics')]");
	private static final By NOTIFICATION_PANEL = By.xpath(
			"//*[contains(translate(normalize-space(.),'NOTIFICATION','notification'),'notification')"
						+ " or contains(translate(normalize-space(.),'ALERT','alert'),'alert')"
						+ " or contains(translate(normalize-space(.),'BELL','bell'),'bell')]");

	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	private static final Duration SHORT_TIMEOUT = Duration.ofSeconds(3);
	private final WebDriverWait pageWait;

	public DashboardPage(WebDriver driver) {
		super(Objects.requireNonNull(driver, "driver must not be null"));
		this.pageWait = new WebDriverWait(Objects.requireNonNull(driver, "driver must not be null"),
				Objects.requireNonNull(DEFAULT_TIMEOUT, "timeout must not be null"));
	}

	public void openAnyBook() {
		try {
			waitForBooksToLoad();
			List<WebElement> books = driver.findElements(BOOK_IMAGES);

			for (WebElement book : books) {
				if (isElementInViewport(book)) {
					clickUsingJavaScript(book);
					LOGGER.info("Successfully clicked a visible book from the carousel.");
					return;
				}
			}

			throw new IllegalStateException("No visible book found in viewport.");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to open a book: {0}", e.getMessage());
			throw e;
		}
	}

	public void acceptCookiesIfPresent() {
		try {
			WebDriverWait shortWait = new WebDriverWait(driver, SHORT_TIMEOUT);
			WebElement acceptButton = shortWait
					.until(ExpectedConditions.visibilityOfElementLocated(COOKIE_ACCEPT_BUTTON));

			if (acceptButton.isDisplayed()) {
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].scrollIntoView({block:'center', inline:'nearest'});", acceptButton);
				try {
					acceptButton.click();
				} catch (Exception e) {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptButton);
				}
				LOGGER.info("Cookie consent accepted.");
			}
		} catch (Exception e) {
			LOGGER.fine("Cookie popup not present or already handled.");
		}
	}

	public boolean waitForRecentlyPlayedSection() {
		try {
			pageWait.until(ExpectedConditions.visibilityOfElementLocated(RECENTLY_PLAYED_SECTION));
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Recently Played section not visible: {0}", e.getMessage());
			return false;
		}
	}

	public void waitForPageReady() {
		pageWait.until(driver -> "complete"
				.equals(((JavascriptExecutor) driver).executeScript("return document.readyState")));
	}

	public boolean waitForDashboardShell() {
		return waitForDashboardShell(DEFAULT_TIMEOUT);
	}

	private boolean waitForDashboardShell(Duration timeout) {
		try {
			new WebDriverWait(Objects.requireNonNull(driver, "driver must not be null"),
					Objects.requireNonNull(timeout, "timeout must not be null"))
					.until(driver -> driver.findElements(DASHBOARD_SHELL).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			}));
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Dashboard shell not ready: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isRecentlyPlayedItemVisible(String title) {
		if (title == null || title.isBlank()) {
			return false;
		}

		try {
			return pageWait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//*[contains(normalize-space(.),\"" + title.replace("\"", "\\\"") + "\")]"))).isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Recently played item {0} not visible: {1}",
					new Object[] { title, e.getMessage() });
			return false;
		}
	}

	public boolean isMenuButtonPresent() {
		try {
			return driver.findElements(DASHBOARD_SHELL).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Menu button not present: {0}", e.getMessage());
			return false;
		}
	}

	// ================= RECOMMENDED CONTENT =================

	public boolean isRecommendedSectionVisible() {
		try {
			// Wait a bit for content to load
			Thread.sleep(2000);

			boolean found = driver.findElements(RECOMMENDED_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});

			LOGGER.info("Recommended section visible: " + found);
			return found;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Recommended section check failed: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasRecommendedContent() {
		try {
			// Wait for page to stabilize
			Thread.sleep(2000);

			// Check for recommended section using multiple possible locators
			String[] recommendedKeywords = {"recommend", "for you", "suggested", "trending", "popular"};

			for (String keyword : recommendedKeywords) {
				try {
					By locator = By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + keyword + "')]");
					WebElement element = driver.findElement(locator);
					if (element.isDisplayed()) {
						LOGGER.info("Found recommended content with keyword: " + keyword);
						return true;
					}
				} catch (Exception e) {
					// Continue to next keyword
				}
			}

			LOGGER.warning("No recommended content found with any keyword");
			return false;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Recommended content check failed: {0}", e.getMessage());
			return false;
		}
	}

	// ================= CONTINUE LISTENING =================

	public boolean isContinueListeningSectionVisible() {
		try {
			return driver.findElements(CONTINUE_LISTENING_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Continue listening section not visible: {0}", e.getMessage());
			return false;
		}
	}

	// ================= SUBSCRIPTION BANNER =================

	public boolean isUpgradeBannerVisible() {
		try {
			return driver.findElements(UPGRADE_BANNER).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Upgrade banner not visible: {0}", e.getMessage());
			return false;
		}
	}

	// ================= SEARCH =================

	public void enterSearchKeyword(String keyword) {
		try {
			LOGGER.info("Looking for search input...");
			WebElement searchInput = pageWait.until(ExpectedConditions.presenceOfElementLocated(SEARCH_INPUT));
			LOGGER.info("Search input found: " + searchInput.getAttribute("placeholder"));

			// Wait for input to be visible and clickable
			pageWait.until(ExpectedConditions.visibilityOf(searchInput));

			searchInput.clear();
			searchInput.sendKeys(keyword);
			LOGGER.info("Entered search keyword: " + keyword);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to enter search keyword: {0}", e.getMessage());
			throw e;
		}
	}

	public void clickSearchButton() {
		try {
			// Try pressing Enter first (most common for search fields)
			try {
				WebElement searchInput = driver.findElement(SEARCH_INPUT);
				searchInput.sendKeys(Keys.ENTER);
				LOGGER.info("Pressed Enter in search field");
				return;
			} catch (Exception e) {
				LOGGER.fine("Could not press Enter: " + e.getMessage());
			}

			// Fallback: Look for search button
			List<WebElement> searchButtons = driver.findElements(SEARCH_BUTTON);
			for (WebElement button : searchButtons) {
				if (button.isDisplayed()) {
					clickWithJS(button);
					LOGGER.info("Clicked search button");
					return;
				}
			}

			LOGGER.warning("No search button found, search may have auto-triggered");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click search button: {0}", e.getMessage());
			throw e;
		}
	}

	public boolean areSearchResultsDisplayed() {
		try {
			// Wait for results to load
			Thread.sleep(2000);

			// Check if any content/book images are visible after search
			int bookCount = driver.findElements(BOOK_IMAGES).size();
			LOGGER.info("Found " + bookCount + " books after search");

			return bookCount > 0;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "No search results found: {0}", e.getMessage());
			return false;
		}
	}

	// ================= PLAYLIST =================

	public boolean isPlaylistWidgetVisible() {
		try {
			return driver.findElements(PLAYLIST_WIDGET).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Playlist widget not visible: {0}", e.getMessage());
			return false;
		}
	}

	public void clickFirstPlaylist() {
		try {
			WebElement playlist = pageWait.until(ExpectedConditions.elementToBeClickable(PLAYLIST_WIDGET));
			scrollIntoView(playlist);
			clickWithJS(playlist);
			LOGGER.info("Clicked on playlist");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click playlist: {0}", e.getMessage());
			throw e;
		}
	}

	public boolean isPlaylistPageOpened() {
		try {
			Thread.sleep(1000);
			String currentUrl = driver.getCurrentUrl().toLowerCase();
			return currentUrl.contains("playlist");
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Playlist page not opened: {0}", e.getMessage());
			return false;
		}
	}

	// ================= FAVORITES =================

	public boolean isFavoriteSectionVisible() {
		try {
			return driver.findElements(FAVORITE_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Favorite section not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasFavoriteContent() {
		try {
			WebElement section = pageWait.until(ExpectedConditions.presenceOfElementLocated(FAVORITE_SECTION));
			return section != null && section.isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "No favorite content found: {0}", e.getMessage());
			return false;
		}
	}

	// ================= UPLOADER FUNCTIONALITY =================

	public boolean isUploadButtonVisible() {
		try {
			return driver.findElements(UPLOAD_BUTTON).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Upload button not visible: {0}", e.getMessage());
			return false;
		}
	}

	public void clickUploadButton() {
		try {
			WebElement uploadBtn = pageWait.until(ExpectedConditions.elementToBeClickable(UPLOAD_BUTTON));
			clickWithJS(uploadBtn);
			LOGGER.info("Clicked upload button");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click upload button: {0}", e.getMessage());
			throw e;
		}
	}


	// Hamburger menu locators and methods
	private static final By HAMBURGER_MENU = By.xpath("//img[contains(@src,'ic_menu') and @draggable='false']");

	public void clickHamburgerMenu() {
		try {
			WebElement menu = pageWait.until(ExpectedConditions.presenceOfElementLocated(HAMBURGER_MENU));
			clickWithJS(menu);
			LOGGER.info("Hamburger menu clicked");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click hamburger menu: {0}", e.getMessage());
			throw e;
		}
	}

	public void clickLogout() {
		try {
			// First, click the hamburger menu to open it
			clickHamburgerMenu();

			// Wait for menu animation
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				LOGGER.log(Level.WARNING, "Sleep interrupted: {0}", ie.getMessage());
			}

			// Then click the logout button
			WebElement logoutBtn = pageWait.until(ExpectedConditions.elementToBeClickable(LOGOUT_BUTTON));
			clickWithJS(logoutBtn);
			LOGGER.info("Clicked logout button");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click logout button: {0}", e.getMessage());
			throw e;
		}
	}
	public boolean isUploadPageOpened() {
		try {
			Thread.sleep(1000);
			String currentUrl = driver.getCurrentUrl().toLowerCase();
			return currentUrl.contains("upload") || driver.findElements(UPLOAD_PAGE).size() > 0;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Upload page not opened: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasContentStats() {
		try {
			return driver.findElements(CONTENT_STATS).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Content stats not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isAnalyticsSectionVisible() {
		try {
			return driver.findElements(ANALYTICS_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Analytics section not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isDraftsSectionVisible() {
		try {
			return driver.findElements(DRAFTS_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Drafts section not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasUploadedContentList() {
		try {
			return driver.findElements(CONTENT_LIST).size() > 0;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Content list not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasEditOption() {
		try {
			return driver.findElements(EDIT_OPTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Edit option not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasDeleteOption() {
		try {
			return driver.findElements(DELETE_OPTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Delete option not visible: {0}", e.getMessage());
			return false;
		}
	}

	// ================= ADMIN FUNCTIONALITY =================

	public boolean isAdminDashboardLoaded() {
		try {
			return waitForDashboardShell() &&
				   (driver.findElements(ADMIN_DASHBOARD).size() > 0
					|| driver.getCurrentUrl().toLowerCase().contains("admin"));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Admin dashboard not loaded: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isUserManagementSectionVisible() {
		try {
			return driver.findElements(USER_MANAGEMENT).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "User management section not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasRoleManagementOption() {
		try {
			return driver.findElements(ROLE_MANAGEMENT).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Role management option not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isContentModerationPanelVisible() {
		try {
			return driver.findElements(MODERATION_PANEL).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Content moderation panel not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasPlatformAnalytics() {
		try {
			return driver.findElements(PLATFORM_ANALYTICS).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Platform analytics not visible: {0}", e.getMessage());
			return false;
		}
	}

	public boolean isNotificationPanelVisible() {
		try {
			return driver.findElements(NOTIFICATION_PANEL).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Notification panel not visible: {0}", e.getMessage());
			return false;
		}
	}

	// ================= HELPER METHODS =================

	private void waitForMilliseconds(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			LOGGER.log(Level.WARNING, "Sleep interrupted: {0}", ie.getMessage());
		}
	}

	private void clickWithJS(WebElement element) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to click with JS: {0}", e.getMessage());
		}
	}

	private void scrollIntoView(WebElement element) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to scroll into view: {0}", e.getMessage());
		}
	}

	private void waitForBooksToLoad() {
		pageWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(BOOK_IMAGES, 0));
	}

	private boolean isElementInViewport(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (Boolean) js.executeScript("var rect = arguments[0].getBoundingClientRect();"
				+ "return (rect.top >= 0 && rect.left >= 0 && rect.bottom <= window.innerHeight && rect.right <= window.innerWidth);",
				element);
	}

	private void clickUsingJavaScript(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", element);
	}

	// ================= CONSUMER CATEGORIES SECTION =================

	private static final By CATEGORIES_SECTION = By.xpath(
			"//div[contains(@class,'css-146c3p1')][normalize-space()='Categories']"
				+ " | //div[contains(@class,'css-146c3p1')][normalize-space()='Categories']"
				+ " | //*[contains(translate(normalize-space(.),'CATEGORIES','categories'),'categories')]"
				+ " | //*[@data-testid='section_categories']");

	// Use specific category names (matches HomePage.java approach)
	private static final By CATEGORY_ITEMS = By.xpath(
			"//div[@class='css-146c3p1' and (text()='Art' or text()='Business' or text()='Biography' or text()='Comedy' or text()='Culture' or text='Education' or text='Fiction' or text='History' or text='Music' or text='News' or text='Science' or text='Sports' or text='Technology' or text='True Crime')]");

	// Dynamic category items on Categories page (after clicking View All)
	// Matches: <div dir="auto" class="css-146c3p1">Category Name</div>
	// inside flex container with style="flex-flow: wrap; display: flex; gap: 8px;"
	private static final By DYNAMIC_CATEGORY_ITEMS = By.xpath(
			"//div[@class='css-g5y9jx' and contains(@style, 'flex-flow: wrap')]"
				+ "//div[@class='css-g5y9jx']"
				+ "//div[@tabindex='0']"
				+ "//div[@dir='auto' and @class='css-146c3p1']");

	private static final By CATEGORY_NAMES = By.xpath(
			"//div[@class='css-146c3p1' and (text()='Art' or text()='Business' or text()='Biography' or text()='Comedy' or text()='Culture' or text='Education' or text='Fiction' or text='History' or text='Music' or text='News' or text='Science' or text='Sports' or text='Technology' or text='True Crime')]");

	private static final By NO_DATA_MESSAGE = By.xpath(
			"//*[contains(normalize-space(.),'no data') or contains(normalize-space(.),'no results') or contains(normalize-space(.),'not found')]"
				+ " | //*[@data-testid='text_no_data']"
				+ " | //*[@data-testid='container_no_data']"
				+ " | //*[contains(@class,'no_data')]");

	private static final By VIEW_ALL_CATEGORIES = By.xpath(
			"//div[@class='css-146c3p1' and text()='View All']"
				+ " | //*[contains(normalize-space(.),'view all') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'category')]"
				+ " | //*[@data-testid='button_view_all_categories']");

	private static final By BOOK_TITLES_IN_CATEGORY = By.xpath(
			"//*[@data-testid='text_book_title']"
				+ " | //*[contains(@class,'book')]//*[contains(@class,'title')]");

	public boolean isCategoriesSectionVisible() {
		try {
			return driver.findElements(CATEGORIES_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Categories section not visible: {0}", e.getMessage());
			return false;
		}
	}

	public void scrollToCategoriesSection() {
		try {
			WebElement section = pageWait.until(ExpectedConditions.presenceOfElementLocated(CATEGORIES_SECTION));
			scrollIntoView(section);
			LOGGER.info("Scrolled to Categories section");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to scroll to Categories section: {0}", e.getMessage());
		}
	}

	public int getCategoryCount() {
		try {
			// Wait for categories to be present
			waitForMilliseconds(500);

			List<WebElement> categories = driver.findElements(CATEGORY_ITEMS);

			// Count only visible categories
			int count = (int) categories.stream().filter(el -> {
				try {
					return el.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			}).count();

			LOGGER.info("Category count: " + count);
			return count;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get category count: {0}", e.getMessage());
			return 0;
		}
	}

	/**
	 * Count all categories on the Categories page (after clicking View All)
	 * Uses precise locator based on actual HTML structure
	 */
	public int getAllCategoriesCount() {
		try {
			waitForMilliseconds(1000);

			// Use the precise locator matching the HTML structure
			List<WebElement> categoryElements = driver.findElements(DYNAMIC_CATEGORY_ITEMS);

			// Filter and count visible categories
			int count = (int) categoryElements.stream()
					.filter(el -> {
						try {
							if (!el.isDisplayed()) {
								return false;
							}

							String text = el.getText().trim();

							// Filter out "View All" button and empty elements
							if (text.isEmpty()
									|| text.equalsIgnoreCase("View All")
									|| text.length() < 2) {
								return false;
							}

							// Valid category name check
							return text.length() > 2 && text.length() < 50;

						} catch (Exception e) {
							return false;
						}
					})
					.count();

			LOGGER.info("Total categories count on page: " + count);
			return count;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to get all categories count: {0}", e.getMessage());
			return 0;
		}
	}

	public String getFirstVisibleCategoryName() {
		try {
			List<WebElement> names = driver.findElements(CATEGORY_NAMES);
			for (WebElement nameEl : names) {
				try {
					if (nameEl.isDisplayed()) {
						String name = nameEl.getText().trim();
						// Validate it's a real category name
						if (!name.isEmpty()
								&& !name.equalsIgnoreCase("categories")
								&& !name.equalsIgnoreCase("view all")
								&& !name.equalsIgnoreCase("trending")
								&& !name.equalsIgnoreCase("new release")
								&& !name.equalsIgnoreCase("upcoming")
								&& name.length() > 2) {
							LOGGER.info("First visible category: " + name);
							return name;
						}
					}
				} catch (Exception e) {
					// Continue to next
				}
			}
			return "";
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get first category name: {0}", e.getMessage());
			return "";
		}
	}

	public void clickCategory(String categoryName) {
		try {
			// Use the precise HTML structure for categories
			// Matches: <div tabindex="0"><div class="css-146c3p1">Category Name</div></div>
			By categoryLocator = By.xpath(
					"//div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73']"
							+ "[.//div[@class='css-146c3p1' and text()='" + categoryName + "']]");

			WebElement category = pageWait.until(ExpectedConditions.elementToBeClickable(categoryLocator));
			scrollIntoView(category);
			clickWithJS(category);
			LOGGER.info("Clicked category: " + categoryName);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click category {0}: {1}",
					new Object[] { categoryName, e.getMessage() });
			throw e;
		}
	}

	public boolean hasCategoryContent() {
		try {
			waitForMilliseconds(1000);
			List<WebElement> books = driver.findElements(BOOK_TITLES_IN_CATEGORY);
			return books.stream().anyMatch(el -> {
				try {
					return el.isDisplayed() && !el.getText().trim().isEmpty();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check category content: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Count the number of content items (books/shows) in the current category page
	 */
	public int getCategoryContentCount() {
		try {
			waitForMilliseconds(500);
			List<WebElement> books = driver.findElements(BOOK_TITLES_IN_CATEGORY);
			int count = (int) books.stream().filter(el -> {
				try {
					return el.isDisplayed() && !el.getText().trim().isEmpty();
				} catch (Exception e) {
					return false;
				}
			}).count();

			LOGGER.info("Category content count: " + count);
			return count;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get category content count: {0}", e.getMessage());
			return 0;
		}
	}

	public String findEmptyCategory() {
		try {
			// Store current URL
			String currentUrl = driver.getCurrentUrl();

			List<WebElement> categories = driver.findElements(CATEGORY_ITEMS);
			for (WebElement category : categories) {
				try {
					if (category.isDisplayed()) {
						String name = category.getText().trim();

						// Click category to check
						clickWithJS(category);
						waitForMilliseconds(1500);

						if (!hasCategoryContent()) {
							LOGGER.info("Found empty category: " + name);
							return name;
						}

						// Navigate back to categories list
						driver.navigate().back();
						waitForMilliseconds(1000);

						// Refresh the category list since we navigated back
						categories = driver.findElements(CATEGORY_ITEMS);
					}
				} catch (Exception e) {
					// Try to navigate back if something went wrong
					try {
						driver.navigate().back();
						waitForMilliseconds(1000);
					} catch (Exception ex) {
						// Ignore
					}
				}
			}

			// If no empty category found, navigate back to original page
			if (!driver.getCurrentUrl().equals(currentUrl)) {
				driver.navigate().back();
			}

			return "";
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to find empty category: {0}", e.getMessage());
			return "";
		}
	}

	public boolean hasNoContentMessage() {
		try {
			return driver.findElements(NO_DATA_MESSAGE).stream().anyMatch(el -> {
				try {
					return el.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check no content message: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasCategoryCards() {
		try {
			List<WebElement> cards = driver.findElements(CATEGORY_ITEMS);
			return cards.stream().anyMatch(el -> {
				try {
					return el.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check category cards: {0}", e.getMessage());
			return false;
		}
	}

	public String getFirstCategoryCardName() {
		try {
			List<WebElement> cards = driver.findElements(CATEGORY_ITEMS);
			for (WebElement card : cards) {
				try {
					if (card.isDisplayed()) {
						String text = card.getText().trim();
						if (!text.isEmpty() && !text.equalsIgnoreCase("categories")) {
							LOGGER.info("First category card: " + text);
							return text;
						}
					}
				} catch (Exception e) {
					// Continue
				}
			}
			return "";
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get first category card: {0}", e.getMessage());
			return "";
		}
	}

	public void clickCategoryCard(String cardName) {
		try {
			// Use the same precise HTML structure
			By cardLocator = By.xpath(
					"//div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73']"
							+ "[.//div[@class='css-146c3p1' and text()='" + cardName + "']]");

			WebElement card = pageWait.until(ExpectedConditions.elementToBeClickable(cardLocator));
			scrollIntoView(card);
			clickWithJS(card);
			LOGGER.info("Clicked category card: " + cardName);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click category card {0}: {1}",
					new Object[] { cardName, e.getMessage() });
			throw e;
		}
	}

	public boolean hasNoBooksMessage() {
		try {
			return driver.findElements(NO_DATA_MESSAGE).stream().anyMatch(el -> {
				try {
					String text = el.getText().toLowerCase();
					return el.isDisplayed() && (text.contains("no book") || text.contains("no content") || text.contains("not found"));
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check no books message: {0}", e.getMessage());
			return false;
		}
	}

	public void clickViewAllCategories() {
		try {
			// First try: Find the outer div (tabindex="0") that contains "View All" text
			By viewAllButton = By.xpath(
					"//div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73']"
						+ "[.//div[@class='css-146c3p1' and text()='View All']]");

			WebElement viewAll = null;
			try {
				viewAll = pageWait.until(ExpectedConditions.elementToBeClickable(viewAllButton));
			} catch (Exception e) {
				// Second try: Use the original locator
				viewAll = pageWait.until(ExpectedConditions.elementToBeClickable(VIEW_ALL_CATEGORIES));
			}

			scrollIntoView(viewAll);
			clickWithJS(viewAll);
			LOGGER.info("Clicked View All Categories");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click View All Categories: {0}", e.getMessage());
			throw e;
		}
	}

	public boolean scrollCategoriesHorizontal() {
		try {
			WebElement section = driver.findElement(CATEGORIES_SECTION);
			JavascriptExecutor js = (JavascriptExecutor) driver;

			Long initialScroll = (Long) js.executeScript(
					"return arguments[0].scrollLeft;",
					section);

			js.executeScript(
					"arguments[0].scrollLeft += 300;",
					section);

			Thread.sleep(500);

			Long afterScroll = (Long) js.executeScript(
					"return arguments[0].scrollLeft;",
					section);

			LOGGER.info("Categories horizontal scroll: " + initialScroll + " -> " + afterScroll);
			return !afterScroll.equals(initialScroll);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to scroll categories: {0}", e.getMessage());
			return false;
		}
	}

	public long getCurrentScrollPosition() {
		try {
			WebElement section = driver.findElement(CATEGORIES_SECTION);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			Long scrollPos = (Long) js.executeScript("return arguments[0].scrollLeft;", section);
			return scrollPos != null ? scrollPos : 0;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get scroll position: {0}", e.getMessage());
			return 0;
		}
	}

	// ================= CONSUMER TRENDING SHOWS SECTION =================

	private static final By TRENDING_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'TRENDING','trending'),'trending')]"
				+ " | //*[@data-testid='section_trending']"
				+ " | //*[@data-testid='container_trending']"
				+ " | //*[contains(@class,'trending')]");

	private static final By TRENDING_SHOW_ITEMS = By.xpath(
			"//*[@data-testid='container_trending_show']"
				+ " | //*[@data-testid='card_trending']"
				+ " | //*[contains(@class,'trending')]//*[contains(@class,'show') or contains(@class,'item')]");

	private static final By TRENDING_SHOW_NAMES = By.xpath(
			"//*[@data-testid='text_show_title']"
				+ " | //*[@data-testid='text_trending_title']"
				+ " | //*[contains(@class,'trending')]//*[contains(@class,'title')]");

	private static final By VIEW_ALL_TRENDING = By.xpath(
			"//*[contains(normalize-space(.),'view all') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'trending')]"
				+ " | //*[@data-testid='button_view_all_trending']"
				+ " | //*[contains(@class,'view_all') and contains(@class,'trending')]");

	private static final By RELATED_SHOWS_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'RELATED SHOWS','related shows'),'related shows')]"
				+ " | //*[@data-testid='section_related_shows']"
				+ " | //*[contains(@class,'related')]");

	private static final By SHOW_DETAILS = By.xpath(
			"//*[@data-testid='container_show_details']"
				+ " | //*[@data-testid='page_show_details']"
				+ " | //*[contains(@class,'show_details')]");

	public void scrollToTrendingSection() {
		try {
			WebElement section = pageWait.until(ExpectedConditions.presenceOfElementLocated(TRENDING_SECTION));
			scrollIntoView(section);
			LOGGER.info("Scrolled to Trending section");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to scroll to Trending section: {0}", e.getMessage());
		}
	}

	public boolean isTrendingSectionVisible() {
		try {
			return driver.findElements(TRENDING_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Trending section not visible: {0}", e.getMessage());
			return false;
		}
	}

	public String getFirstTrendingShowName() {
		try {
			List<WebElement> shows = driver.findElements(TRENDING_SHOW_NAMES);
			for (WebElement show : shows) {
				try {
					if (show.isDisplayed()) {
						String name = show.getText().trim();
						if (!name.isEmpty()) {
							LOGGER.info("First trending show: " + name);
							return name;
						}
					}
				} catch (Exception e) {
					// Continue
				}
			}
			return "";
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get first trending show: {0}", e.getMessage());
			return "";
		}
	}

	public void clickTrendingShow(String showName) {
		try {
			// Try multiple locators for trending shows
			By showLocator = By.xpath(
					"//*[@data-testid='container_trending_show' or @data-testid='card_trending']"
							+ "[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'"
							+ showName.toLowerCase() + "')]");
			WebElement show = pageWait.until(ExpectedConditions.elementToBeClickable(showLocator));
			scrollIntoView(show);
			clickWithJS(show);
			LOGGER.info("Clicked trending show: " + showName);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click trending show {0}: {1}",
					new Object[] { showName, e.getMessage() });
			throw e;
		}
	}

	public boolean isShowDetailsVisible() {
		try {
			Thread.sleep(1000);
			String currentUrl = driver.getCurrentUrl().toLowerCase();
			return currentUrl.contains("show") || currentUrl.contains("details")
					|| driver.findElements(SHOW_DETAILS).stream().anyMatch(el -> {
						try {
							return el.isDisplayed();
						} catch (Exception e) {
							return false;
						}
					});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Show details not visible: {0}", e.getMessage());
			return false;
		}
	}

	public List<String> getTrendingShowNames() {
		try {
			List<String> names = new java.util.ArrayList<>();
			List<WebElement> shows = driver.findElements(TRENDING_SHOW_NAMES);
			for (WebElement show : shows) {
				try {
					if (show.isDisplayed()) {
						String name = show.getText().trim();
						if (!name.isEmpty()) {
							names.add(name);
						}
					}
				} catch (Exception e) {
					// Continue
				}
			}
			LOGGER.info("Trending shows count: " + names.size());
			return names;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get trending show names: {0}", e.getMessage());
			return new java.util.ArrayList<>();
		}
	}

	public boolean hasTrendingShows() {
		try {
			List<WebElement> shows = driver.findElements(TRENDING_SHOW_ITEMS);
			return shows.stream().anyMatch(el -> {
				try {
					return el.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check trending shows: {0}", e.getMessage());
			return false;
		}
	}

	public boolean hasNoTrendingShowsMessage() {
		try {
			return driver.findElements(NO_DATA_MESSAGE).stream().anyMatch(el -> {
				try {
					String text = el.getText().toLowerCase();
					return el.isDisplayed() && (text.contains("no trending") || text.contains("no show") || text.contains("not found"));
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check no trending shows message: {0}", e.getMessage());
			return false;
		}
	}

	public void clickViewAllTrendingShows() {
		try {
			WebElement viewAll = pageWait.until(ExpectedConditions.elementToBeClickable(VIEW_ALL_TRENDING));
			scrollIntoView(viewAll);
			clickWithJS(viewAll);
			LOGGER.info("Clicked View All Trending Shows");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click View All Trending Shows: {0}", e.getMessage());
			throw e;
		}
	}

	public void scrollToRelatedShowsSection() {
		try {
			WebElement section = pageWait.until(ExpectedConditions.presenceOfElementLocated(RELATED_SHOWS_SECTION));
			scrollIntoView(section);
			LOGGER.info("Scrolled to Related Shows section");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to scroll to Related Shows section: {0}", e.getMessage());
		}
	}

	public boolean isRelatedShowsSectionVisible() {
		try {
			return driver.findElements(RELATED_SHOWS_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Related shows section not visible: {0}", e.getMessage());
			return false;
		}
	}
}
