package pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Page object representing the dashboard page of the application. Contains methods to interact with various elements
 */
public class DashboardPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(DashboardPage.class.getName());
	private static final By BOOK_IMAGES = By.xpath("//img[contains(@src,'sonarplay')]");
	private static final By DASHBOARD_SHELL = By
			.xpath("//button[@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu']"
					+ " | //*[@role='button' and (@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu')]"
					+ " | //div[contains(.,'Trending')]" + " | //div[contains(.,'Categories')]"
					+ " | //div[contains(.,'New Release')]" + " | //div[contains(.,'Upcoming Releases')]"
					+ " | //img[contains(@src,'covers')]" + " | //img[contains(@src,'sonarplay')]");
	private static final By RECENTLY_PLAYED_SECTION = By
			.xpath("//*[contains(translate(normalize-space(.),'RECENTLY PLAYED','recently played'),'recently played')"
					+ " or contains(translate(normalize-space(.),'RECENTLY LISTENED','recently listened'),'recently listened')]");
	private static final By COOKIE_ACCEPT_BUTTON = By.xpath("//button[contains(normalize-space(.),'Accept Cookies')]"
			+ " | //div[@tabindex='0' and contains(normalize-space(.),'Accept Cookies')]"
			+ " | //*[@role='button' and contains(normalize-space(.),'Accept Cookies')]");
	private static final By RECOMMENDED_SECTION = By
			.xpath("//*[contains(translate(normalize-space(.),'RECOMMENDED','recommended'),'recommended')"
					+ " or contains(translate(normalize-space(.),'FOR YOU','for you'),'for you')"
					+ " or contains(translate(normalize-space(.),'SUGGESTED','suggested'),'suggested')]");
	private static final By CONTINUE_LISTENING_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'CONTINUE LISTENING','continue listening'),'continue listening')"
					+ " or contains(translate(normalize-space(.),'RESUME','resume'),'resume')]");
	private static final By UPGRADE_BANNER = By
			.xpath("//*[contains(translate(normalize-space(.),'UPGRADE','upgrade'),'upgrade')"
					+ " or contains(translate(normalize-space(.),'PREMIUM','premium'),'premium')"
					+ " or contains(translate(normalize-space(.),'SUBSCRIBE','subscribe'),'subscribe')"
					+ " or contains(translate(normalize-space(.),'GO PREMIUM','go premium'),'go premium')]");
	private static final By SEARCH_INPUT = By.xpath(
			"//input[contains(@placeholder, 'Search') or contains(@placeholder, 'search') or @type='search' or @enterkeyhint='search']");

	private static final By SEARCH_BUTTON = By
			.xpath("//button[contains(@aria-label,'search') or @type='submit' or contains(.,'Search')]"
					+ " | //*[@role='button' and contains(.,'Search')]");
	private static final By PLAYLIST_WIDGET = By
			.xpath("//*[contains(translate(normalize-space(.),'PLAYLIST','playlist'),'playlist')"
					+ " or contains(@href,'playlist')]");
	private static final By FAVORITE_SECTION = By
			.xpath("//*[contains(translate(normalize-space(.),'FAVORITE','favorite'),'favorite')"
					+ " or contains(translate(normalize-space(.),'FAVOURITE','favourite'),'favourite')"
					+ " or contains(translate(normalize-space(.),'LIKED','liked'),'liked')"
					+ " or contains(translate(normalize-space(.),'SAVED','saved'),'saved')]");

	// ================= UPLOADER SPECIFIC LOCATORS =================
	private static final By UPLOAD_BUTTON = By
			.xpath("//*[contains(translate(normalize-space(.),'UPLOAD','upload'),'upload')"
					+ " or contains(@aria-label,'upload')" + " or contains(@class,'upload')]");
	private static final By UPLOAD_PAGE = By
			.xpath("//*[contains(translate(normalize-space(.),'UPLOAD CONTENT','upload content'),'upload content')"
					+ " or contains(translate(normalize-space(.),'NEW UPLOAD','new upload'),'new upload')"
					+ " or contains(translate(normalize-space(.),'ADD CONTENT','add content'),'add content')]");
	private static final By CONTENT_STATS = By
			.xpath("//*[contains(translate(normalize-space(.),'STATS','stats'),'stats')"
					+ " or contains(translate(normalize-space(.),'ANALYTICS','analytics'),'analytics')"
					+ " or contains(translate(normalize-space(.),'VIEWS','views'),'views')]");
	private static final By ANALYTICS_SECTION = By
			.xpath("//*[contains(translate(normalize-space(.),'ANALYTICS','analytics'),'analytics')"
					+ " or contains(translate(normalize-space(.),'INSIGHTS','insights'),'insights')"
					+ " or contains(translate(normalize-space(.),'METRICS','metrics'),'metrics')]");
	private static final By DRAFTS_SECTION = By
			.xpath("//*[contains(translate(normalize-space(.),'DRAFT','draft'),'draft')"
					+ " or contains(translate(normalize-space(.),'PENDING','pending'),'pending')]");
	private static final By CONTENT_LIST = By
			.xpath("//*[contains(translate(normalize-space(.),'MY CONTENT','my content'),'my content')"
					+ " or contains(translate(normalize-space(.),'UPLOADED','uploaded'),'uploaded')"
					+ " or contains(translate(normalize-space(.),'CONTENT','content'),'content')]");
	private static final By EDIT_OPTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'EDIT','edit'),'edit')" + " or contains(@aria-label,'edit')]");
	private static final By DELETE_OPTION = By
			.xpath("//*[contains(translate(normalize-space(.),'DELETE','delete'),'delete')"
					+ " or contains(translate(normalize-space(.),'REMOVE','remove'),'remove')]"
					+ " | //button[contains(@aria-label,'delete')]");

	private static final By LOGOUT_BUTTON = By.cssSelector("[data-testid='button_logout']");
	private static final By LOGOUT_CONFIRM_YES_BUTTON = By.xpath(
			"//*[self::button or @role='button' or @tabindex='0']"
					+ "[normalize-space()='Yes' or normalize-space()='YES'"
					+ " or contains(translate(normalize-space(.),'YES','yes'),'yes')"
					+ " or contains(translate(@aria-label,'YES','yes'),'yes')]");
	private static final By ADMIN_DASHBOARD = By
			.xpath("//*[contains(translate(normalize-space(.),'ADMIN DASHBOARD','admin dashboard'),'admin dashboard')"
					+ " or contains(translate(normalize-space(.),'ADMIN PANEL','admin panel'),'admin panel')]");
	private static final By USER_MANAGEMENT = By
			.xpath("//*[contains(translate(normalize-space(.),'USER MANAGEMENT','user management'),'user management')"
					+ " or contains(translate(normalize-space(.),'USERS','users'),'users')"
					+ " or contains(translate(normalize-space(.),'MANAGE USERS','manage users'),'manage users')]");
	private static final By ROLE_MANAGEMENT = By
			.xpath("//*[contains(translate(normalize-space(.),'ROLE','role'),'role')"
					+ " or contains(translate(normalize-space(.),'PERMISSION','permission'),'permission')]");
	private static final By MODERATION_PANEL = By
			.xpath("//*[contains(translate(normalize-space(.),'MODERATION','moderation'),'moderation')"
					+ " or contains(translate(normalize-space(.),'APPROVE','approve'),'approve')"
					+ " or contains(translate(normalize-space(.),'REVIEW','review'),'review')]");
	private static final By PLATFORM_ANALYTICS = By
			.xpath("//*[contains(translate(normalize-space(.),'PLATFORM','platform'),'platform')"
					+ " or contains(translate(normalize-space(.),'SYSTEM ANALYTICS','system analytics'),'system analytics')]");
	private static final By NOTIFICATION_PANEL = By
			.xpath("//*[contains(translate(normalize-space(.),'NOTIFICATION','notification'),'notification')"
					+ " or contains(translate(normalize-space(.),'ALERT','alert'),'alert')"
					+ " or contains(translate(normalize-space(.),'BELL','bell'),'bell')]");
	private static final By HEADER_LOGO = By.xpath(
			"//img[contains(translate(@src,'LOGO','logo'),'logo') or contains(translate(@alt,'LOGO','logo'),'logo')]"
					+ " | //img[contains(@src,'/assets/assets/images/icons/dark') or contains(@src,'icons/dark')]"
					+ " | //a[(contains(@href,'dashboard') or @href='/' or contains(@href,'home')) and (.//img"
					+ " or contains(translate(normalize-space(.),'HOME','home'),'home')"
					+ " or contains(translate(normalize-space(.),'DASHBOARD','dashboard'),'dashboard'))]"
					+ " | //header//a[.//img]"
					+ " | (//*[self::a or self::div][.//img])[1]");
	private static final By SEARCH_PAGE_LOGO_BUTTON = By.xpath(
			"//div[@tabindex='0' and .//img[contains(@src,'/assets/assets/images/icons/dark') or contains(@src,'icons/dark')]]");
	private static final By NOTIFICATION_ICON = By
			.xpath("//*[contains(@data-testid,'notification') or contains(@aria-label,'Notification')"
					+ " or contains(@aria-label,'notification') or contains(@href,'notification')"
					+ " or contains(translate(normalize-space(.),'NOTIFICATION','notification'),'notification')"
					+ " or contains(translate(normalize-space(.),'BELL','bell'),'bell')]");
	private static final By NO_SEARCH_RESULTS_MESSAGE = By
			.xpath("//*[contains(translate(normalize-space(.),'NO RESULTS','no results'),'no results')"
					+ " or contains(translate(normalize-space(.),'NO DATA','no data'),'no data')"
					+ " or contains(translate(normalize-space(.),'NOT FOUND','not found'),'not found')"
					+ " or contains(translate(normalize-space(.),'NO MATCHES FOUND. TRY ADJUSTING YOUR SEARCH.','no matches found. try adjusting your search.'),'no matches found. try adjusting your search.')]");
	private static final By NO_NOTIFICATIONS_MESSAGE = By.xpath(
			"//*[contains(translate(normalize-space(.),'NO NOTIFICATIONS AVAILABLE','no notifications available'),'no notifications available')"
					+ " or contains(translate(normalize-space(.),'NO NOTIFICATIONS','no notifications'),'no notifications')]");
	private static final By THEME_TOGGLE = By.xpath(
			"//*[contains(@data-testid,'theme') or contains(@aria-label,'theme')" + " or contains(@aria-label,'Theme')"
					+ " or contains(translate(normalize-space(.),'DARK MODE','dark mode'),'dark mode')"
					+ " or contains(translate(normalize-space(.),'LIGHT MODE','light mode'),'light mode')"
					+ " or contains(translate(normalize-space(.),'THEME','theme'),'theme')]");
	private static final By PROFILE_ICON = By
			.xpath("//*[contains(@data-testid,'profile') or contains(@data-testid,'avatar')"
					+ " or contains(@aria-label,'Profile') or contains(@aria-label,'profile')"
					+ " or contains(@aria-label,'Account') or contains(@aria-label,'account')"
					+ " or contains(translate(normalize-space(.),'PROFILE','profile'),'profile')"
					+ " or contains(translate(normalize-space(.),'ACCOUNT','account'),'account')]");
	private static final By PROFILE_MENU = By
			.xpath("//*[contains(translate(normalize-space(.),'LOGOUT','logout'),'logout')"
					+ " or contains(translate(normalize-space(.),'PROFILE','profile'),'profile')"
					+ " or contains(translate(normalize-space(.),'SETTINGS','settings'),'settings')]");
	private static final By FOOTER_SECTION = By.xpath(
			"//footer"
					+ " | //*[(self::div or self::section) and (contains(@class,'footer') or contains(@id,'footer')"
					+ " or .//a[contains(translate(normalize-space(.),'PRIVACY','privacy'),'privacy')]"
					+ " or .//a[contains(translate(normalize-space(.),'TERMS','terms'),'terms')]"
					+ " or .//a[contains(translate(normalize-space(.),'CONTACT','contact'),'contact')]"
					+ " or .//*[@tabindex='0' and contains(translate(normalize-space(.),'PRIVACY','privacy'),'privacy')]"
					+ " or .//*[@tabindex='0' and contains(translate(normalize-space(.),'TERMS','terms'),'terms')]"
					+ " or .//*[@tabindex='0' and contains(translate(normalize-space(.),'CONTACT','contact'),'contact')]"
					+ " or .//a[contains(@href,'privacy')] or .//a[contains(@href,'terms')] or .//a[contains(@href,'contact')]"
					+ " or .//a[contains(@href,'facebook')] or .//a[contains(@href,'instagram')]"
					+ " or .//a[contains(@href,'twitter')] or .//a[contains(@href,'x.com')]"
					+ " or .//*[@tabindex='0' and (.//img[contains(@src,'insta')] or .//img[contains(@src,'facebook')]"
					+ " or contains(normalize-space(.),''))])]");
	private static final By FOOTER_LINKS = By.xpath("//footer//a | //footer//*[@role='link']"
			+ " | //a[contains(translate(normalize-space(.),'PRIVACY','privacy'),'privacy')]"
			+ " | //a[contains(translate(normalize-space(.),'TERMS','terms'),'terms')]"
			+ " | //a[contains(translate(normalize-space(.),'CONTACT','contact'),'contact')]"
			+ " | //a[contains(@href,'privacy')] | //a[contains(@href,'terms')] | //a[contains(@href,'contact')]"
			+ " | //a[contains(@href,'facebook')] | //a[contains(@href,'instagram')]"
			+ " | //a[contains(@href,'twitter')] | //a[contains(@href,'x.com')]"
			+ " | //*[@tabindex='0' and contains(translate(normalize-space(.),'PRIVACY','privacy'),'privacy')]"
			+ " | //*[@tabindex='0' and contains(translate(normalize-space(.),'TERMS','terms'),'terms')]"
			+ " | //*[@tabindex='0' and contains(translate(normalize-space(.),'CONTACT','contact'),'contact')]"
			+ " | //*[@tabindex='0' and contains(translate(normalize-space(.),'ABOUT US','about us'),'about us')]"
			+ " | //*[@tabindex='0' and (.//img[contains(@src,'insta')] or .//img[contains(@src,'facebook')]"
			+ " or contains(normalize-space(.),''))]");
	private static final By PRIVACY_POLICY_LINK = By
			.xpath("//a[contains(translate(normalize-space(.),'PRIVACY','privacy'),'privacy')]"
					+ " | //*[@role='link' and contains(translate(normalize-space(.),'PRIVACY','privacy'),'privacy')]"
					+ " | //*[@tabindex='0' and contains(translate(normalize-space(.),'PRIVACY POLICY','privacy policy'),'privacy policy')]");
	private static final By TERMS_AND_CONDITIONS_LINK = By
			.xpath("//a[contains(translate(normalize-space(.),'TERMS','terms'),'terms')]"
					+ " | //*[@role='link' and contains(translate(normalize-space(.),'TERMS','terms'),'terms')]"
					+ " | //*[@tabindex='0' and contains(translate(normalize-space(.),'TERMS AND CONDITIONS','terms and conditions'),'terms and conditions')]");
	private static final By CONTACT_US_LINK = By
			.xpath("//a[contains(translate(normalize-space(.),'CONTACT','contact'),'contact')]"
					+ " | //*[@role='link' and contains(translate(normalize-space(.),'CONTACT','contact'),'contact')]"
					+ " | //*[@tabindex='0' and contains(translate(normalize-space(.),'CONTACT US','contact us'),'contact us')]");
	private static final By FACEBOOK_LINK = By.xpath(
			"//a[contains(@href,'facebook') or contains(translate(normalize-space(.),'FACEBOOK','facebook'),'facebook')]"
					+ " | //*[@role='link' and contains(@href,'facebook')]"
					+ " | //*[@tabindex='0' and (contains(normalize-space(.),'') or .//img[contains(@src,'facebook')])]");
	private static final By TWITTER_LINK = By.xpath("//a[contains(@href,'twitter') or contains(@href,'x.com')"
			+ " or contains(translate(normalize-space(.),'TWITTER','twitter'),'twitter')"
			+ " or normalize-space(.)='X']"
			+ " | //*[@role='link' and (contains(@href,'twitter') or contains(@href,'x.com'))]");
	private static final By INSTAGRAM_LINK = By.xpath(
			"//a[contains(@href,'instagram') or contains(translate(normalize-space(.),'INSTAGRAM','instagram'),'instagram')]"
					+ " | //*[@role='link' and contains(@href,'instagram')]"
					+ " | //*[@tabindex='0' and .//img[contains(@src,'insta')]]");

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
		pageWait.until(
				driver -> "complete".equals(((JavascriptExecutor) driver).executeScript("return document.readyState")));
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
			return pageWait
					.until(ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//*[contains(normalize-space(.),\"" + title.replace("\"", "\\\"") + "\")]")))
					.isDisplayed();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Recently played item {0} not visible: {1}", new Object[] { title, e.getMessage() });
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
			waitForMilliseconds(2000);

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
			waitForMilliseconds(2000);

			// Check for recommended section using multiple possible locators
			String[] recommendedKeywords = { "recommend", "for you", "suggested", "trending", "popular" };

			for (String keyword : recommendedKeywords) {
				try {
					By locator = By.xpath(
							"//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
									+ keyword + "')]");
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
			waitForMilliseconds(2000);

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
			waitForMilliseconds(1000);
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
			return section.isDisplayed();
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
	private static final By HAMBURGER_MENU = By.xpath(
			"//button[@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu']"
					+ " | //*[@role='button' and (@aria-label='Menu' or @aria-label='menu' or @aria-label='Open menu')]"
					+ " | //img[contains(@src,'ic_menu') and @draggable='false']"
					+ " | //header//*[self::img or self::div or self::button][contains(@class,'menu') or contains(@src,'menu')][1]"
					+ " | (//*[self::button or self::div][@tabindex='0' and .//*[contains(@src,'menu') or contains(@class,'menu')]])[1]");

	public void clickHamburgerMenu() {
		try {
			WebElement menu = pageWait.until(ExpectedConditions.visibilityOfElementLocated(HAMBURGER_MENU));
			scrollIntoView(menu);
			clickWithJS(menu);
			LOGGER.info("Hamburger menu clicked");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click hamburger menu: {0}", e.getMessage());
			throw e;
		}
	}

	public boolean isHamburgerMenuVisible() {
		return isAnyElementVisible(HAMBURGER_MENU);
	}

	public boolean isLogoutButtonVisible() {
		return isAnyElementVisible(LOGOUT_BUTTON);
	}

	public void clickLogout() {
		try {
			// First, click the hamburger menu to open it
			clickHamburgerMenu();

			// Wait for menu animation
			waitForMilliseconds(500);

			// Then click the logout button
			WebElement logoutBtn = pageWait.until(ExpectedConditions.elementToBeClickable(LOGOUT_BUTTON));
			clickWithJS(logoutBtn);
			LOGGER.info("Clicked logout button");

			waitForMilliseconds(500);
			clickLogoutConfirmationIfPresent();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click logout button: {0}", e.getMessage());
			throw e;
		}
	}

	private void clickLogoutConfirmationIfPresent() {
		try {
			List<WebElement> yesButtons = driver.findElements(LOGOUT_CONFIRM_YES_BUTTON);
			for (WebElement yesButton : yesButtons) {
				try {
					if (!yesButton.isDisplayed()) {
						continue;
					}

					scrollIntoView(yesButton);
					try {
						yesButton.click();
					} catch (Exception e) {
						clickWithJS(yesButton);
					}
					LOGGER.info("Clicked logout confirmation Yes button");
					waitForMilliseconds(500);
					return;
				} catch (Exception e) {
					// Try next visible candidate
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Logout confirmation dialog not present: {0}", e.getMessage());
		}
	}

	public boolean isUploadPageOpened() {
		try {
			waitForMilliseconds(1000);
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
			return waitForDashboardShell() && (driver.findElements(ADMIN_DASHBOARD).size() > 0
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

	private static final By CATEGORIES_SECTION = By
			.xpath("//div[contains(@class,'css-146c3p1')][normalize-space()='Categories']"
					+ " | //div[contains(@class,'css-146c3p1')][normalize-space()='Categories']"
					+ " | //*[contains(translate(normalize-space(.),'CATEGORIES','categories'),'categories')]"
					+ " | //*[@data-testid='section_categories']");

	// Use specific category names (matches HomePage.java approach)
	private static final By CATEGORY_ITEMS = By.xpath(
			"//div[@class='css-146c3p1' and (text()='Art' or text()='Business' or text()='Biography' or text()='Comedy' or text()='Culture' or text='Education' or text='Fiction' or text()='History' or text()='Horror' or text()='Music' or text='News' or text='Science' or text='Sports' or text='Technology' or text='True Crime')]");

	// Dynamic category items on Categories page (after clicking View All)
	// Matches: <div dir="auto" class="css-146c3p1">Category Name</div>
	// inside flex container with style="flex-flow: wrap; display: flex; gap: 8px;"
	private static final By DYNAMIC_CATEGORY_ITEMS = By
			.xpath("//div[@class='css-g5y9jx' and contains(@style, 'flex-flow: wrap')]" + "//div[@class='css-g5y9jx']"
					+ "//div[@tabindex='0']" + "//div[@dir='auto' and @class='css-146c3p1']");

	private static final By CATEGORY_NAMES = By.xpath(
			"//div[@class='css-146c3p1' and (text()='Art' or text()='Business' or text()='Biography' or text()='Comedy' or text()='Culture' or text='Education' or text='Fiction' or text()='History' or text()='Horror' or text()='Music' or text='News' or text='Science' or text='Sports' or text='Technology' or text='True Crime')]");

	private static final By NO_DATA_MESSAGE = By.xpath(
			"//*[contains(normalize-space(.),'no data') or contains(normalize-space(.),'no results') or contains(normalize-space(.),'not found')]"
					+ " | //*[@data-testid='text_no_data']" + " | //*[@data-testid='container_no_data']"
					+ " | //*[contains(@class,'no_data')]");

	private static final By VIEW_ALL_CATEGORIES = By.xpath("//div[@class='css-146c3p1' and text()='View All']"
			+ " | //*[contains(normalize-space(.),'view all') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'category')]"
			+ " | //*[@data-testid='button_view_all_categories']");

	private static final By BOOK_TITLES_IN_CATEGORY = By.xpath(
			"//*[@data-testid='text_book_title']" + " | //*[contains(@class,'book')]//*[contains(@class,'title')]");

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
	 * Count all categories on the Categories page (after clicking View All) Uses
	 * precise locator based on actual HTML structure
	 */
	public int getAllCategoriesCount() {
		try {
			waitForMilliseconds(1000);

			// Use the precise locator matching the HTML structure
			List<WebElement> categoryElements = driver.findElements(DYNAMIC_CATEGORY_ITEMS);

			// Filter and count visible categories
			int count = (int) categoryElements.stream().filter(el -> {
				try {
					if (!el.isDisplayed()) {
						return false;
					}

					String text = el.getText().trim();

					// Filter out "View All" button and empty elements
					if (text.isEmpty() || text.equalsIgnoreCase("View All") || text.length() < 2) {
						return false;
					}

					// Valid category name check
					return text.length() > 2 && text.length() < 50;

				} catch (Exception e) {
					return false;
				}
			}).count();

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
						if (!name.isEmpty() && !name.equalsIgnoreCase("categories")
								&& !name.equalsIgnoreCase("view all") && !name.equalsIgnoreCase("trending")
								&& !name.equalsIgnoreCase("new release") && !name.equalsIgnoreCase("upcoming")
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
			By categoryLocator = By.xpath("//div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73']"
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

	public boolean tryClickCategory(String categoryName) {
		try {
			clickCategory(categoryName);
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Category click was not available for {0}: {1}",
					new Object[] { categoryName, e.getMessage() });
			return false;
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
			By cardLocator = By.xpath("//div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73']"
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
					return el.isDisplayed()
							&& (text.contains("no book") || text.contains("no content") || text.contains("not found"));
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
			By viewAllButton = By.xpath("//div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73']"
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

			Long initialScroll = (Long) js.executeScript("return arguments[0].scrollLeft;", section);

			js.executeScript("arguments[0].scrollLeft += 300;", section);

			waitForMilliseconds(500);

			Long afterScroll = (Long) js.executeScript("return arguments[0].scrollLeft;", section);

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

	private static final By TRENDING_SECTION = By
			.xpath("//*[contains(translate(normalize-space(.),'TRENDING','trending'),'trending')]"
					+ " | //*[@data-testid='section_trending']" + " | //*[@data-testid='container_trending']"
					+ " | //*[contains(@class,'trending')]");

	private static final By TRENDING_SHOW_ITEMS = By
			.xpath("//*[@data-testid='container_trending_show']" + " | //*[@data-testid='card_trending']"
					+ " | //*[contains(@class,'trending')]//*[contains(@class,'show') or contains(@class,'item')]");

	private static final By TRENDING_SHOW_NAMES = By
			.xpath("//*[@data-testid='text_show_title']" + " | //*[@data-testid='text_trending_title']"
					+ " | //*[contains(@class,'trending')]//*[contains(@class,'title')]");

	private static final By VIEW_ALL_TRENDING = By
			.xpath("//*[contains(text(),'Trending')]//following::div[text()='View All'][1]"
					+ " | //div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73']"
					+ "[.//div[@class='css-146c3p1' and text()='View All']]"
					+ " | //*[contains(normalize-space(.),'view all') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'trending')]"
					+ " | //*[@data-testid='button_view_all_trending']"
					+ " | //*[contains(@class,'view_all') and contains(@class,'trending')]");

	private static final By RELATED_SHOWS_SECTION = By
			.xpath("//*[contains(translate(normalize-space(.),'RELATED SHOWS','related shows'),'related shows')]"
					+ " | //*[@data-testid='section_related_shows']" + " | //*[contains(@class,'related')]");
	private static final By VIEW_ALL_RELATED = By
			.xpath("//*[contains(text(),'Related Shows')]//following::div[text()='View All'][1]"
					+ " | //div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73'][.//div[@class='css-146c3p1' and text()='View All']]"
					+ " | //*[contains(normalize-space(.),'view all') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'related')]"
					+ " | //*[@data-testid='button_view_all_related']"
					+ " | //*[contains(@class,'view_all') and contains(@class,'related')]");

	private static final By RELATED_SHOW_ITEM = By.xpath(".//*[contains(@class,'related') and contains(@class,'show')]"
			+ " | .//*[@data-testid='card_related_show']" + " | .//*[contains(@class,'show_card')]");

	private static final By NO_RELATED_SHOWS_MESSAGE = By
			.xpath("//*[contains(text(),'No related') or contains(text(),'No shows')]"
					+ " | //*[@data-testid='message_no_related_shows']"
					+ " | //*[contains(@class,'no_related') and contains(@class,'message')]");

	// ============================================================
	// UPCOMING RELEASES SECTION
	// ============================================================

	private static final By UPCOMING_RELEASES_SECTION = By.xpath(
			"//*[contains(translate(normalize-space(.),'UPCOMING RELEASES','upcoming releases'),'upcoming releases')]"
					+ " | //*[@data-testid='section_upcoming_releases']"
					+ " | //*[contains(@class,'upcoming') and contains(@class,'releases')]");

	private static final By VIEW_ALL_UPCOMING = By
			.xpath("//*[contains(text(),'Upcoming')]//following::div[text()='View All'][1]"
					+ " | //div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73'][.//div[@class='css-146c3p1' and text()='View All']]"
					+ " | //*[contains(normalize-space(.),'view all') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upcoming')]"
					+ " | //*[@data-testid='button_view_all_upcoming']"
					+ " | //*[contains(@class,'view_all') and contains(@class,'upcoming')]");

	private static final By UPCOMING_SHOW_ITEM = By
			.xpath(".//*[contains(@class,'upcoming') and contains(@class,'show')]"
					+ " | .//*[@data-testid='card_upcoming_show']" + " | .//*[contains(@class,'show_card')]");

	private static final By RELEASE_DATE = By.xpath(
			".//*[contains(@class,'release') and contains(@class,'date')]" + " | .//*[@data-testid='text_release_date']"
					+ " | .//*[contains(text(),'Release') or contains(text(),'Coming')]");

	private static final By NO_UPCOMING_RELEASES_MESSAGE = By
			.xpath("//*[contains(text(),'No upcoming') or contains(text(),'coming soon')]"
					+ " | //*[@data-testid='message_no_upcoming_releases']"
					+ " | //*[contains(@class,'no_upcoming') and contains(@class,'message')]");

	// ============================================================
	// MOST RATED SECTION
	// ============================================================

	private static final By MOST_RATED_SECTION = By
			.xpath("//*[contains(translate(normalize-space(.),'MOST RATED','most rated'),'most rated')]"
					+ " | //*[@data-testid='section_most_rated']"
					+ " | //*[contains(@class,'most') and contains(@class,'rated')]");

	private static final By VIEW_ALL_MOST_RATED = By
			.xpath("//*[contains(text(),'Most Rated')]//following::div[text()='View All'][1]"
					+ " | //div[@tabindex='0' and @class='css-g5y9jx r-1loqt21 r-1otgn73'][.//div[@class='css-146c3p1' and text()='View All']]"
					+ " | //*[contains(normalize-space(.),'view all') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'rated')]"
					+ " | //*[@data-testid='button_view_all_rated']"
					+ " | //*[contains(@class,'view_all') and contains(@class,'rated')]");

	private static final By RATED_SHOW_ITEM = By.xpath(".//*[contains(@class,'rated') and contains(@class,'show')]"
			+ " | .//*[@data-testid='card_rated_show']" + " | .//*[contains(@class,'show_card')]");

	private static final By RATING_STARS = By.xpath(".//*[contains(@class,'rating') and contains(@class,'stars')]"
			+ " | .//*[@data-testid='rating_stars']" + " | .//*[@data-testid='RNVUI__TapRating-starContainer']"
			+ " | .//*[@data-testid='RNVUI__Star']" + " | .//*[@data-testid='RNVUI__Star-image']"
			+ " | .//*[contains(@class,'star') or contains(@class,'rating')]");

	private static final By NO_RATED_SHOWS_MESSAGE = By
			.xpath("//*[contains(text(),'No rated') or contains(text(),'No ratings')]"
					+ " | //*[@data-testid='message_no_rated_shows']"
					+ " | //*[contains(@class,'no_rated') and contains(@class,'message')]");

	private static final By SHOW_DETAILS = By.xpath("//*[@data-testid='container_show_details']"
			+ " | //*[@data-testid='page_show_details']" + " | //*[contains(@class,'show_details')]");

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
			By showLocator = By.xpath("//*[@data-testid='container_trending_show' or @data-testid='card_trending']"
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

	public boolean isShowDetailsVisible1() {
		try {
			waitForMilliseconds(1000);
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
			List<String> names = new ArrayList<>();
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
			return new ArrayList<>();
		}
	}

	// ================= TRENDING SHOWS COUNTING (HomePage Pattern)
	// =================
	/**
	 * Get trending shows/books using the working HomePage pattern Uses img elements
	 * with alt attributes to count content
	 */
	public List<String> getTrendingBooksList() {
		try {
			List<String> titles = new ArrayList<>();

			// Count by img src with bookid (reliable - works with empty alt)
			List<WebElement> books = driver.findElements(By.xpath("//img[contains(@src,'thumb.php?bookid=')]"));

			LOGGER.info("Total trending books/images found: " + books.size());

			for (WebElement book : books) {
				try {
					String src = book.getAttribute("src");
					// Extract bookid from URL: thumb.php?bookid=XXX&
					if (src != null && src.contains("bookid=")) {
						String bookId = src.substring(src.indexOf("bookid=") + 7);
						bookId = bookId.contains("&") ? bookId.substring(0, bookId.indexOf("&")) : bookId;
						titles.add("Book_" + bookId);
					}
				} catch (Exception e) {
					// Skip this element
				}
			}

			LOGGER.info("Trending books counted: " + titles.size());
			return titles;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to get trending books list: {0}", e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Get count of trending shows/books using the HomePage pattern
	 */
	public int getTrendingBooksCount() {
		List<String> books = getTrendingBooksList();
		return books.size();
	}

	/**
	 * Check if trending shows are available using HomePage pattern
	 */
	public boolean hasTrendingContent() {
		try {
			int count = getTrendingBooksCount();
			return count > 0;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check trending content: {0}", e.getMessage());
			return false;
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
					return el.isDisplayed()
							&& (text.contains("no trending") || text.contains("no show") || text.contains("not found"));
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

	// ============================================================
	// URL VERIFICATION METHODS

	// ============================================================
	// RELATED SHOWS METHODS
	// ============================================================

	/**
	 * Click on a related show
	 * 
	 * @param showName Name of the related show to click
	 */
	public void clickRelatedShow(String showName) {
		try {
			WebElement show = pageWait.until(ExpectedConditions.elementToBeClickable(RELATED_SHOW_ITEM));
			scrollIntoView(show);
			clickWithJS(show);
			LOGGER.info("Clicked on related show: " + showName);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click related show: {0}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Get the first related show name
	 * 
	 * @return Name of first related show, or empty string if none found
	 */
	public String getFirstRelatedShowName() {
		try {
			WebElement show = driver.findElements(RELATED_SHOW_ITEM).stream().findFirst().orElse(null);
			if (show != null) {
				String name = show.getText();
				LOGGER.log(Level.FINE, "Found related show: {0}", name);
				return name;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get related show name: {0}", e.getMessage());
		}
		return "";
	}

	/**
	 * Check if show details are visible
	 * 
	 * @return true if show details section is visible
	 */
	public boolean isShowDetailsVisible() {
		try {
			return driver.findElements(SHOW_DETAILS).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Show details not visible: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Click View All button for Related Shows
	 */
	public void clickViewAllRelatedShows() {
		try {
			WebElement viewAll = pageWait.until(ExpectedConditions.elementToBeClickable(VIEW_ALL_RELATED));
			scrollIntoView(viewAll);
			clickWithJS(viewAll);
			LOGGER.info("Clicked View All Related Shows");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click View All Related Shows: {0}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Check if "no related shows" message is displayed
	 * 
	 * @return true if empty state message is visible
	 */
	public boolean hasNoRelatedShowsMessage() {
		try {
			return driver.findElements(NO_RELATED_SHOWS_MESSAGE).stream().anyMatch(element -> {
				try {
					String text = element.getText().toLowerCase();
					return element.isDisplayed() && (text.contains("no related") || text.contains("no show"));
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check no related shows message: {0}", e.getMessage());
			return false;
		}
	}

	// ============================================================
	// UPCOMING RELEASES METHODS
	// ============================================================

	/**
	 * Scroll to Upcoming Releases section
	 */
	public void scrollToUpcomingReleasesSection() {
		try {
			WebElement section = pageWait.until(ExpectedConditions.presenceOfElementLocated(UPCOMING_RELEASES_SECTION));
			scrollIntoView(section);
			LOGGER.info("Scrolled to Upcoming Releases section");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to scroll to Upcoming Releases section: {0}", e.getMessage());
		}
	}

	/**
	 * Check if Upcoming Releases section is visible
	 * 
	 * @return true if section is visible
	 */
	public boolean isUpcomingReleasesSectionVisible() {
		try {
			return driver.findElements(UPCOMING_RELEASES_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Upcoming Releases section not visible: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Click on an upcoming show
	 * 
	 * @param showName Name of the upcoming show to click
	 */
	public void clickUpcomingShow(String showName) {
		try {
			WebElement show = pageWait.until(ExpectedConditions.elementToBeClickable(UPCOMING_SHOW_ITEM));
			scrollIntoView(show);
			clickWithJS(show);
			LOGGER.info("Clicked on upcoming show: " + showName);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click upcoming show: {0}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Get the first upcoming show name
	 * 
	 * @return Name of first upcoming show, or empty string if none found
	 */
	public String getFirstUpcomingShowName() {
		try {
			WebElement show = driver.findElements(UPCOMING_SHOW_ITEM).stream().findFirst().orElse(null);
			if (show != null) {
				String name = show.getText();
				LOGGER.log(Level.FINE, "Found upcoming show: {0}", name);
				return name;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get upcoming show name: {0}", e.getMessage());
		}
		return "";
	}

	/**
	 * Check if release date is visible
	 * 
	 * @return true if release date is visible
	 */
	public boolean isReleaseDateVisible() {
		try {
			WebElement date = pageWait.until(ExpectedConditions.presenceOfElementLocated(RELEASE_DATE));
			boolean visible = date.isDisplayed();
			LOGGER.log(Level.FINE, "Release date visible: {0}", visible);
			return visible;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Release date not visible: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Click View All button for Upcoming Releases
	 */
	public void clickViewAllUpcoming() {
		try {
			WebElement viewAll = pageWait.until(ExpectedConditions.elementToBeClickable(VIEW_ALL_UPCOMING));
			scrollIntoView(viewAll);
			clickWithJS(viewAll);
			LOGGER.info("Clicked View All Upcoming Releases");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click View All Upcoming: {0}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Check if "no upcoming releases" message is displayed
	 * 
	 * @return true if empty state message is visible
	 */
	public boolean hasNoUpcomingReleasesMessage() {
		try {
			return driver.findElements(NO_UPCOMING_RELEASES_MESSAGE).stream().anyMatch(element -> {
				try {
					String text = element.getText().toLowerCase();
					return element.isDisplayed() && (text.contains("no upcoming") || text.contains("coming soon"));
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check no upcoming releases message: {0}", e.getMessage());
			return false;
		}
	}

	// ============================================================
	// MOST RATED METHODS
	// ============================================================

	/**
	 * Scroll to Most Rated section
	 */
	public void scrollToMostRatedSection() {
		try {
			WebElement section = pageWait.until(ExpectedConditions.presenceOfElementLocated(MOST_RATED_SECTION));
			scrollIntoView(section);
			LOGGER.info("Scrolled to Most Rated section");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to scroll to Most Rated section: {0}", e.getMessage());
		}
	}

	/**
	 * Check if Most Rated section is visible
	 * 
	 * @return true if section is visible
	 */
	public boolean isMostRatedSectionVisible() {
		try {
			return driver.findElements(MOST_RATED_SECTION).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Most Rated section not visible: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Check if ratings are displayed
	 * 
	 * @return true if rating stars are visible
	 */
	public boolean areRatingsDisplayed() {
		try {
			return getVisibleRatingStarCount() > 0;
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Ratings not displayed: {0}", e.getMessage());
			return false;
		}
	}

	public int getVisibleRatingStarCount() {
		int visibleStarCount = 0;

		try {
			List<WebElement> ratingElements = driver.findElements(RATING_STARS);
			visibleStarCount += (int) ratingElements.stream().filter(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			}).count();

			List<WebElement> starContainers = driver
					.findElements(By.xpath("//*[@data-testid='RNVUI__TapRating-starContainer']"));
			for (WebElement container : starContainers) {
				try {
					if (!container.isDisplayed()) {
						continue;
					}

					visibleStarCount += container.findElements(By.xpath(".//*[@data-testid='RNVUI__Star']")).size();
				} catch (Exception e) {
					// Continue to next rating container
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to count visible rating stars: {0}", e.getMessage());
		}

		LOGGER.log(Level.FINE, "Visible rating star count: {0}", visibleStarCount);
		return visibleStarCount;
	}

	/**
	 * Get the first rated show name
	 * 
	 * @return Name of first rated show, or empty string if none found
	 */
	public String getFirstRatedShowName() {
		try {
			WebElement show = driver.findElements(RATED_SHOW_ITEM).stream().findFirst().orElse(null);
			if (show != null) {
				String name = show.getText();
				LOGGER.log(Level.FINE, "Found rated show: {0}", name);
				return name;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to get rated show name: {0}", e.getMessage());
		}
		return "";
	}

	/**
	 * Click View All button for Most Rated
	 */
	public void clickViewAllMostRated() {
		try {
			WebElement viewAll = pageWait.until(ExpectedConditions.elementToBeClickable(VIEW_ALL_MOST_RATED));
			scrollIntoView(viewAll);
			clickWithJS(viewAll);
			LOGGER.info("Clicked View All Most Rated");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to click View All Most Rated: {0}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Check if "no rated shows" message is displayed
	 * 
	 * @return true if empty state message is visible
	 */
	public boolean hasNoRatedShowsMessage() {
		try {
			return driver.findElements(NO_RATED_SHOWS_MESSAGE).stream().anyMatch(element -> {
				try {
					String text = element.getText().toLowerCase();
					return element.isDisplayed() && (text.contains("no rated") || text.contains("no ratings"));
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check no rated shows message: {0}", e.getMessage());
			return false;
		}
	}

	// ============================================================
	// HEADER AND FOOTER METHODS
	// ============================================================

	public void openHomePage() {
		String baseUrl = utils.ConfigReader.getProperty("url", "");
		if (!baseUrl.isBlank()) {
			driver.get(baseUrl);
			waitForPageReady();
		}
	}

	public boolean isHeaderVisible() {
		return waitForDashboardShell() || isSearchBarVisible() || isHeaderLogoVisible() || isProfileIconVisible();
	}

	public boolean isHeaderLogoVisible() {
		return isAnyElementVisible(SEARCH_PAGE_LOGO_BUTTON) || isAnyElementVisible(HEADER_LOGO);
	}

	public String clickHeaderLogoAndGetCurrentUrl() {
		List<By> logoCandidates = new ArrayList<>();
		logoCandidates.add(SEARCH_PAGE_LOGO_BUTTON);
		logoCandidates.add(HEADER_LOGO);
		logoCandidates.add(By.xpath("//a[@href='/' or contains(@href,'home') or contains(@href,'dashboard')]"));
		logoCandidates.add(By.xpath("//header//img | //header//*[self::a or self::div][.//img]"));
		logoCandidates.add(By.xpath("(//img[contains(@src,'logo') or contains(@alt,'logo')])[1]"));

		String startingUrl = getCurrentUrl();
		scrollToPageTop();

		for (By candidate : logoCandidates) {
			List<WebElement> elements = findVisibleElements(candidate);
			for (WebElement element : elements) {
				try {
					List<String> windowHandlesBeforeClick = new ArrayList<>(driver.getWindowHandles());
					scrollIntoView(element);
					clickWithJS(element);
					waitForMilliseconds(2000);

					List<String> windowHandlesAfterClick = new ArrayList<>(driver.getWindowHandles());
					if (windowHandlesAfterClick.size() > windowHandlesBeforeClick.size()) {
						driver.switchTo().window(windowHandlesAfterClick.get(windowHandlesAfterClick.size() - 1));
					}

					String currentUrl = getCurrentUrl();
					if (!currentUrl.equals(startingUrl)
							&& (currentUrl.contains("dashboard") || currentUrl.contains("home") || !currentUrl.contains("web_search"))) {
						return currentUrl;
					}
				} catch (Exception e) {
					LOGGER.log(Level.FINE, "Header logo click candidate failed for {0}: {1}",
							new Object[] { candidate, e.getMessage() });
				}
			}
		}

		if (tryJavascriptLogoNavigation()) {
			return getCurrentUrl();
		}

		return getCurrentUrl();
	}

	public boolean isSearchBarVisible() {
		return isAnyElementVisible(SEARCH_INPUT);
	}

	public String getSearchInputValue() {
		try {
			WebElement searchInput = pageWait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
			return firstNonBlank(searchInput.getAttribute("value"), searchInput.getDomProperty("value"));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Search input value not available: {0}", e.getMessage());
			return "";
		}
	}

	public boolean hasNoSearchResultsMessage() {
		return isAnyElementVisible(NO_SEARCH_RESULTS_MESSAGE);
	}

	public boolean isNotificationIconVisible() {
		return isAnyElementVisible(NOTIFICATION_ICON);
	}

	public void openNotificationsPanel() {
		WebElement notification = findFirstVisibleElement(NOTIFICATION_ICON);
		if (notification == null) {
			throw new IllegalStateException("Notification icon is not visible.");
		}

		clickWithJS(notification);
		waitForMilliseconds(1500);
	}

	public boolean isNotificationPanelVisible() {
		return isAnyElementVisible(NOTIFICATION_PANEL);
	}

	public boolean hasNoNotificationsMessage() {
		return isAnyElementVisible(NO_NOTIFICATIONS_MESSAGE);
	}

	public boolean isThemeToggleVisible() {
		return isAnyElementVisible(THEME_TOGGLE);
	}

	public boolean isProfileIconVisible() {
		return isAnyElementVisible(PROFILE_ICON) || isAnyElementVisible(HAMBURGER_MENU);
	}

	public void openProfileMenu() {
		WebElement profileIcon = findFirstVisibleElement(PROFILE_ICON);
		if (profileIcon != null) {
			clickWithJS(profileIcon);
			waitForMilliseconds(1000);
			return;
		}

		clickHamburgerMenu();
		waitForMilliseconds(1000);
	}

	public boolean isProfileMenuVisible() {
		return isAnyElementVisible(PROFILE_MENU) || isAnyElementVisible(LOGOUT_BUTTON);
	}

	public void scrollToFooter() {
		try {
			long previousHeight = -1L;
			for (int attempt = 0; attempt < 6; attempt++) {
				long currentHeight = ((Number) ((JavascriptExecutor) driver)
						.executeScript("return Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);"))
						.longValue();
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, arguments[0]);", currentHeight);
				waitForMilliseconds(800);

				if (currentHeight == previousHeight) {
					break;
				}
				previousHeight = currentHeight;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to scroll to footer: {0}", e.getMessage());
		}
	}

	public boolean isFooterVisible() {
		scrollToFooter();
		waitForMilliseconds(500);
		return isAnyElementVisible(FOOTER_SECTION) || getVisibleFooterLinksCount() > 0;
	}

	public int getVisibleFooterLinksCount() {
		try {
			return (int) driver.findElements(FOOTER_LINKS).stream().filter(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			}).count();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Footer links not available: {0}", e.getMessage());
			return 0;
		}
	}

	public String openPrivacyPolicyLink() {
		return clickElementAndCaptureUrl(PRIVACY_POLICY_LINK);
	}

	public String openTermsAndConditionsLink() {
		return clickElementAndCaptureUrl(TERMS_AND_CONDITIONS_LINK);
	}

	public String openContactUsLink() {
		return clickElementAndCaptureUrl(CONTACT_US_LINK);
	}

	public String openFacebookLink() {
		return clickElementAndCaptureUrl(FACEBOOK_LINK);
	}

	public String openTwitterLink() {
		return clickElementAndCaptureUrl(TWITTER_LINK);
	}

	public String openInstagramLink() {
		return clickElementAndCaptureUrl(INSTAGRAM_LINK);
	}

	public String openBrokenFooterLink() {
		WebElement element = findFirstVisibleElement(PRIVACY_POLICY_LINK);
		if (element == null) {
			throw new IllegalStateException("A footer link is required to simulate a broken destination.");
		}

		String brokenPath = "/broken-footer-link-automation";
		List<String> windowHandlesBeforeClick = new ArrayList<>(driver.getWindowHandles());
		scrollIntoView(element);
		((JavascriptExecutor) driver).executeScript(
				"const el = arguments[0];"
						+ "const brokenUrl = window.location.origin + arguments[1];"
						+ "if (el.tagName === 'A') { el.setAttribute('href', brokenUrl); el.setAttribute('target', '_self'); }"
						+ "el.onclick = function(event) {"
						+ "  if (event) { event.preventDefault(); event.stopPropagation(); }"
						+ "  window.location.href = brokenUrl;"
						+ "  return false;"
						+ "};",
				element, brokenPath);
		clickWithJS(element);
		waitForMilliseconds(2000);

		List<String> windowHandlesAfterClick = new ArrayList<>(driver.getWindowHandles());
		if (windowHandlesAfterClick.size() > windowHandlesBeforeClick.size()) {
			driver.switchTo().window(windowHandlesAfterClick.get(windowHandlesAfterClick.size() - 1));
		}

		return getCurrentUrl();
	}

	private boolean isAnyElementVisible(By locator) {
		try {
			return driver.findElements(locator).stream().anyMatch(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Element visibility check failed for {0}: {1}",
					new Object[] { locator, e.getMessage() });
			return false;
		}
	}

	private WebElement findFirstVisibleElement(By locator) {
		try {
			return driver.findElements(locator).stream().filter(element -> {
				try {
					return element.isDisplayed();
				} catch (Exception e) {
					return false;
				}
			}).findFirst().orElse(null);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Visible element lookup failed for {0}: {1}",
					new Object[] { locator, e.getMessage() });
			return null;
		}
	}

	private List<WebElement> findVisibleElements(By locator) {
		List<WebElement> visibleElements = new ArrayList<>();

		try {
			for (WebElement element : driver.findElements(locator)) {
				try {
					if (element.isDisplayed()) {
						visibleElements.add(element);
					}
				} catch (Exception e) {
					// Ignore stale/non-visible candidate
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Visible elements lookup failed for {0}: {1}",
					new Object[] { locator, e.getMessage() });
		}

		return visibleElements;
	}

	private void scrollToPageTop() {
		try {
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
			waitForMilliseconds(500);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to scroll to page top: {0}", e.getMessage());
		}
	}

	private boolean tryJavascriptLogoNavigation() {
		try {
			scrollToPageTop();

			Object navigationTriggered = ((JavascriptExecutor) driver).executeScript(
					"const selectors = ["
							+ "'header a[href=\"/\"]',"
							+ "'header a[href*=\"home\"]',"
							+ "'header a[href*=\"dashboard\"]',"
							+ "'a[href=\"/\"]',"
							+ "'a[href*=\"home\"]',"
							+ "'a[href*=\"dashboard\"]',"
							+ "'header img',"
							+ "'img[alt*=\"logo\" i]',"
							+ "'img[src*=\"logo\" i]'"
							+ "];"
							+ "const isVisible = (element) => {"
							+ "  if (!element) return false;"
							+ "  const rect = element.getBoundingClientRect();"
							+ "  return rect.width > 0 && rect.height > 0;"
							+ "};"
							+ "const clickableAncestor = (element) => {"
							+ "  let current = element;"
							+ "  while (current) {"
							+ "    if (current.tagName === 'A' || current.tagName === 'BUTTON' || current.onclick || current.getAttribute('role') === 'button') {"
							+ "      return current;"
							+ "    }"
							+ "    current = current.parentElement;"
							+ "  }"
							+ "  return element;"
							+ "};"
							+ "for (const selector of selectors) {"
							+ "  const elements = Array.from(document.querySelectorAll(selector)).filter(isVisible);"
							+ "  elements.sort((a, b) => {"
							+ "    const rectA = a.getBoundingClientRect();"
							+ "    const rectB = b.getBoundingClientRect();"
							+ "    return rectA.top - rectB.top || rectA.left - rectB.left;"
							+ "  });"
							+ "  for (const element of elements) {"
							+ "    const target = clickableAncestor(element);"
							+ "    if (!target) continue;"
							+ "    const href = target.getAttribute('href');"
							+ "    if (href && (href === '/' || href.includes('home') || href.includes('dashboard'))) {"
							+ "      window.location.href = href;"
							+ "      return true;"
							+ "    }"
							+ "    target.click();"
							+ "    return true;"
							+ "  }"
							+ "}"
							+ "return false;");

			waitForMilliseconds(2000);
			return Boolean.TRUE.equals(navigationTriggered);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "JavaScript logo navigation failed: {0}", e.getMessage());
			return false;
		}
	}

	private String clickElementAndCaptureUrl(By locator) {
		WebElement element = findFirstVisibleElement(locator);
		if (element == null) {
			throw new IllegalStateException("Required element is not visible for locator: " + locator);
		}

		List<String> windowHandlesBeforeClick = new ArrayList<>(driver.getWindowHandles());
		scrollIntoView(element);
		clickWithJS(element);
		waitForMilliseconds(2000);

		List<String> windowHandlesAfterClick = new ArrayList<>(driver.getWindowHandles());
		if (windowHandlesAfterClick.size() > windowHandlesBeforeClick.size()) {
			driver.switchTo().window(windowHandlesAfterClick.get(windowHandlesAfterClick.size() - 1));
		}

		return getCurrentUrl();
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return "";
	}

	// ============================================================
	// PERFORMANCE & EDGE CASE METHODS
	// ============================================================

	/**
	 * Refresh the dashboard page
	 */
	public void refreshDashboard() {
		try {
			driver.navigate().refresh();
			LOGGER.info("Dashboard page refreshed");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to refresh dashboard: {0}", e.getMessage());
		}
	}

	// ============================================================

	/**
	 * Get the current page URL
	 * 
	 * @return Current URL as lowercase string
	 */
	public String getCurrentUrl() {
		try {
			return driver.getCurrentUrl().toLowerCase();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to get current URL: {0}", e.getMessage());
			return "";
		}
	}

	/**
	 * Check if current URL contains the specified search text
	 * 
	 * @param searchText Text to search for in URL
	 * @return true if URL contains search text
	 */
	public boolean isCurrentUrlContains(String searchText) {
		String currentUrl = getCurrentUrl();
		return currentUrl.contains(searchText.toLowerCase());
	}

	/**
	 * Check if current URL contains any of the specified search terms
	 * 
	 * @param searchTerms Variable arguments for search terms
	 * @return true if URL contains any of the search terms
	 */
	public boolean isCurrentUrlContainsAny(String... searchTerms) {
		String currentUrl = getCurrentUrl();
		for (String term : searchTerms) {
			if (currentUrl.contains(term.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if current page is valid (not empty and contains known page
	 * identifiers)
	 * 
	 * @return true if current page appears to be valid
	 */
	public boolean isValidPage() {
		String currentUrl = getCurrentUrl();
		return !currentUrl.isEmpty() && currentUrl.contains("http")
				&& (currentUrl.contains("dashboard") || currentUrl.contains("home") || currentUrl.contains("category")
						|| currentUrl.contains("trending") || currentUrl.contains("shows"));
	}

	// ============================================================
	// HIGH-LEVEL ACTION METHODS - CATEGORIES
	// ============================================================

	/**
	 * Navigate to a specific category and verify navigation
	 * 
	 * @param categoryName Name of the category to navigate to
	 * @return true if navigation was successful
	 */
	public boolean navigateToCategory(String categoryName) {
		try {
			clickCategory(categoryName);
			LOGGER.log(Level.INFO, "Clicked on category: {0}", categoryName);

			// Wait for navigation
			waitForMilliseconds(3000);

			// Verify navigation
			boolean navigated = isCurrentUrlContainsAny("category", categoryName.toLowerCase());
			if (navigated) {
				LOGGER.log(Level.INFO, "Successfully navigated to category: {0}", categoryName);
			}
			return navigated;

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to navigate to category {0}: {1}",
					new Object[] { categoryName, e.getMessage() });
			return false;
		}
	}

	/**
	 * Click View All Categories and verify navigation
	 * 
	 * @return true if View All was clicked and navigation occurred
	 */
	public boolean viewAllCategoriesAndVerify() {
		try {
			clickViewAllCategories();
			waitForMilliseconds(3000);

			// Verify navigation
			boolean navigated = isCurrentUrlContainsAny("categories", "view_all", "category", "genre", "all");
			if (navigated) {
				LOGGER.info("Successfully navigated to categories page via View All");
			}
			return navigated;

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to view all categories: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Click on a category and return the content count
	 * 
	 * @param categoryName Name of the category to click
	 * @return Number of content items found, or -1 if failed
	 */
	public int clickCategoryAndGetContentCount(String categoryName) {
		try {
			clickCategory(categoryName);
			waitForMilliseconds(3000);

			int count = getCategoryContentCount();
			LOGGER.info("Category '" + categoryName + "' has " + count + " content items");

			return count;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to get content count for category {0}: {1}",
					new Object[] { categoryName, e.getMessage() });
			return -1;
		}
	}

	public boolean openCategoryCardAndVerify(String cardName) {
		try {
			clickCategoryCard(cardName);
			waitForMilliseconds(3000);
			return isCurrentUrlContainsAny("category", cardName == null ? "" : cardName.toLowerCase());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to open category card {0}: {1}",
					new Object[] { cardName, e.getMessage() });
			return false;
		}
	}

	// ============================================================
	// HIGH-LEVEL ACTION METHODS - TRENDING
	// ============================================================

	/**
	 * Navigate to Trending page via View All button
	 * 
	 * @return true if navigation was successful
	 */
	public boolean navigateToTrendingPage() {
		try {
			clickViewAllTrendingShows();
			waitForMilliseconds(5000);

			// Verify navigation
			boolean navigated = isCurrentUrlContainsAny("trending", "shows", "view_all", "list");
			if (navigated) {
				LOGGER.info("Successfully navigated to trending page via View All");
			}
			return navigated;

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to navigate to trending page: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Get trending content count using View All button
	 * 
	 * @return Number of trending items found, or -1 if failed
	 */
	public int getTrendingCountViaViewAll() {
		try {
			clickViewAllTrendingShows();
			waitForMilliseconds(5000);

			List<String> trendingItems = getTrendingBooksList();
			int count = trendingItems.size();
			LOGGER.info("Found " + count + " trending items via View All");
			return count;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to get trending count via View All: {0}", e.getMessage());
			return -1;
		}
	}

	public boolean openTrendingShowAndVerify(String showName) {
		try {
			clickTrendingShow(showName);
			waitForMilliseconds(3000);
			return isShowDetailsVisible1() || isCurrentUrlContainsAny("show", "detail",
					showName == null ? "" : showName.toLowerCase(), "trending");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to open trending show {0}: {1}",
					new Object[] { showName, e.getMessage() });
			return false;
		}
	}

	public boolean hasNoBooksOrContentMessage() {
		return hasNoBooksMessage() || hasNoContentMessage();
	}

	/**
	 * Get all trending items (tries dashboard first, then View All)
	 * 
	 * @return List of trending item names
	 */
	public List<String> getAllTrendingItems() {
		// First try to get from dashboard
		List<String> items = getTrendingBooksList();

		if (items.isEmpty()) {
			LOGGER.info("No trending items on dashboard, trying View All...");
			try {
				clickViewAllTrendingShows();
				waitForMilliseconds(5000);
				items = getTrendingBooksList();
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Failed to get trending items via View All: {0}", e.getMessage());
			}
		}

		LOGGER.info("Total trending items found: " + items.size());
		return items;
	}

	// ============================================================
	// ENHANCED STATE VERIFICATION METHODS
	// ============================================================

	/**
	 * Check if categories section exists and is not empty
	 * 
	 * @return true if categories are available
	 */
	public boolean hasCategoriesAndNotEmpty() {
		try {
			boolean hasSection = isCategoriesSectionVisible();
			if (!hasSection) {
				return false;
			}

			int count = getCategoryCount();
			return count > 0;

		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check categories state: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Check if trending content is available
	 * 
	 * @return true if trending content exists
	 */
	public boolean hasTrendingContentAvailable() {
		try {
			boolean hasSection = isTrendingSectionVisible();
			if (!hasSection) {
				return false;
			}

			return hasTrendingShows();

		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Failed to check trending state: {0}", e.getMessage());
			return false;
		}
	}

	/**
	 * Get complete category state information
	 * 
	 * @return Map containing category state details
	 */
	public Map<String, Object> getCategoryState() {
		Map<String, Object> state = new HashMap<>();
		state.put("sectionVisible", isCategoriesSectionVisible());
		state.put("categoryCount", getCategoryCount());
		state.put("hasContent", hasCategoryContent());
		state.put("contentCount", getCategoryContentCount());
		state.put("hasEmptyMessage", hasNoContentMessage());
		return state;
	}

	/**
	 * Get complete trending state information
	 * 
	 * @return Map containing trending state details
	 */
	public Map<String, Object> getTrendingState() {
		Map<String, Object> state = new HashMap<>();
		state.put("sectionVisible", isTrendingSectionVisible());
		state.put("hasShows", hasTrendingShows());
		state.put("itemCount", getTrendingBooksList().size());
		state.put("hasEmptyMessage", hasNoTrendingShowsMessage());
		return state;
	}

	// ============================================================
	// END OF ENHANCED METHODS
	// ============================================================
}
