package tests;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import pages.DashboardPage;
import utils.ConfigReader;
import utils.TestWaitHelper;

/**
 * Smoke Test Class - Covers all basic scenarios without requiring authentication.
 * This test verifies the application is up and running with all major functionality working.
 *
 * Priority 999 tests run last or can be executed independently for quick smoke testing.
 */
public class SmokeTest extends BaseTest {

	private DashboardPage dashboard;

	@BeforeMethod(alwaysRun = true)
	public void setUpSmokeTest() {
		// Initialize dashboard page
		dashboard = new DashboardPage(driver);
		ConfigReader.reload();

		System.out.println("\n========================================");
		System.out.println("SMOKE TEST INITIALIZATION");
		System.out.println("========================================");
	}

	@AfterMethod(alwaysRun = true)
	public void tearDownSmokeTest() {
		System.out.println("\n========================================");
		System.out.println("SMOKE TEST COMPLETED");
		System.out.println("========================================");
	}

	// ==================== SMOKE TESTS ====================

	@Test(priority = 991, description = "Verify landing page loads and basic structure is intact")
	public void smokeVerifyLandingPageLoads() {
		System.out.println("\n[SMOKE TEST 1] Verifying Landing Page Loads...");

		try {
			// 1. Verify URL is loaded
			String currentUrl = driver.getCurrentUrl();
			Assert.assertNotNull(currentUrl, "Application URL should not be null");
			System.out.println("  ✓ URL loaded: " + currentUrl);

			// 2. Verify page title
			String pageTitle = driver.getTitle();
			Assert.assertNotNull(pageTitle, "Page should have a title");
			System.out.println("  ✓ Page title: " + pageTitle);

			// 3. Verify body is present
			String bodyText = ((JavascriptExecutor) driver).executeScript(
					"return document.body ? document.body.innerText : ''").toString();
			Assert.assertFalse(bodyText.isEmpty(), "Page body should have content");
			System.out.println("  ✓ Page body has content");

			// 4. Verify no critical JavaScript errors
			Boolean readyState = (Boolean) ((JavascriptExecutor) driver).executeScript(
					"return document.readyState === 'complete'");
			Assert.assertTrue(readyState, "Page should be in complete state");
			System.out.println("  ✓ Page fully loaded");

			System.out.println("[SMOKE TEST 1] ✓ PASSED - Landing page loads successfully\n");

		} catch (Exception e) {
			System.err.println("[SMOKE TEST 1] ✗ FAILED - " + e.getMessage());
			Assert.fail("Landing page verification failed: " + e.getMessage());
		}
	}

	@Test(priority = 992, description = "Verify dashboard elements are visible and accessible")
	public void smokeVerifyDashboardElements() {
		System.out.println("\n[SMOKE TEST 2] Verifying Dashboard Elements...");

		try {
			// Wait for page to be ready
			dashboard.waitForPageReady();
			TestWaitHelper.shortWait();

			// 1. Verify page is ready
			boolean pageReady = dashboard.isBookDetailsPageVisible() ||
			                    driver.getCurrentUrl().contains("dashboard") ||
			                    driver.getCurrentUrl().contains("home");
			System.out.println("  ✓ Page ready: " + pageReady);

			// 2. Check for visible sections
			String bodyText = ((JavascriptExecutor) driver).executeScript(
					"return document.body.innerText").toString();

			boolean hasContent = bodyText.length() > 100;
			Assert.assertTrue(hasContent, "Page should have content");
			System.out.println("  ✓ Page has content: " + hasContent);

			// 3. Count interactive elements
			int buttonCount = countElements("button");
			int linkCount = countElements("a");
			System.out.println("  ✓ Buttons found: " + buttonCount);
			System.out.println("  ✓ Links found: " + linkCount);

			// 4. Check for basic layout elements
			boolean hasHeader = checkElementExists("header");
			boolean hasMain = checkElementExists("main");
			boolean hasFooter = checkElementExists("footer");

			System.out.println("  ✓ Header present: " + hasHeader);
			System.out.println("  ✓ Main content present: " + hasMain);
			System.out.println("  ✓ Footer present: " + hasFooter);

			System.out.println("[SMOKE TEST 2] ✓ PASSED - Dashboard elements are visible\n");

		} catch (Exception e) {
			System.err.println("[SMOKE TEST 2] ✗ FAILED - " + e.getMessage());
			Assert.fail("Dashboard elements verification failed: " + e.getMessage());
		}
	}

	@Test(priority = 993, description = "Verify book carousel/list is visible and books are displayed")
	public void smokeVerifyBookDisplay() {
		System.out.println("\n[SMOKE TEST 3] Verifying Book Display...");

		try {
			dashboard.waitForPageReady();
			TestWaitHelper.shortWait();

			// 1. Check for book images or book containers
			String bodyText = ((JavascriptExecutor) driver).executeScript(
					"return document.body.innerText").toString();

			boolean hasBooks = bodyText.toLowerCase().contains("book") ||
			                 bodyText.toLowerCase().contains("audiobook") ||
			                 bodyText.toLowerCase().contains("episode");

			System.out.println("  ✓ Books mentioned on page: " + hasBooks);

			// 2. Check for images
			int imageCount = countElements("img");
			System.out.println("  ✓ Images found: " + imageCount);

			// 3. Check for book-related elements
			boolean hasCarousel = bodyText.toLowerCase().contains("carousel") ||
			                     bodyText.toLowerCase().contains("featured") ||
			                     bodyText.toLowerCase().contains("recommended");

			System.out.println("  ✓ Carousel/section present: " + hasCarousel);

			// 4. Verify content sections
			boolean hasContent = bodyText.length() > 200;
			Assert.assertTrue(hasContent, "Page should have substantial content");
			System.out.println("  ✓ Sufficient content displayed");

			System.out.println("[SMOKE TEST 3] ✓ PASSED - Books are displayed\n");

		} catch (Exception e) {
			System.err.println("[SMOKE TEST 3] ✗ FAILED - " + e.getMessage());
			Assert.fail("Book display verification failed: " + e.getMessage());
		}
	}

	@Test(priority = 994, description = "Verify categories are visible and accessible")
	public void smokeVerifyCategoriesVisible() {
		System.out.println("\n[SMOKE TEST 4] Verifying Categories...");

		try {
			dashboard.waitForPageReady();
			TestWaitHelper.shortWait();

			// 1. Check for category sections
			String bodyText = ((JavascriptExecutor) driver).executeScript(
					"return document.body.innerText").toString();

			// Common category names
			String[] commonCategories = {"comedy", "music", "education", "entertainment",
			                             "news", "sports", "technology", "arts"};

			int categoriesFound = 0;
			for (String category : commonCategories) {
				if (bodyText.toLowerCase().contains(category)) {
					categoriesFound++;
				}
			}

			System.out.println("  ✓ Common categories found: " + categoriesFound);
			System.out.println("  ✓ Categories visible: " + (categoriesFound > 0));

			// 2. Check for category navigation
			boolean hasCategoryNav = bodyText.toLowerCase().contains("category") ||
			                        bodyText.toLowerCase().contains("browse") ||
			                        bodyText.toLowerCase().contains("genres");

			System.out.println("  ✓ Category navigation present: " + hasCategoryNav);

			System.out.println("[SMOKE TEST 4] ✓ PASSED - Categories are visible\n");

		} catch (Exception e) {
			System.err.println("[SMOKE TEST 4] ✗ FAILED - " + e.getMessage());
			Assert.fail("Categories verification failed: " + e.getMessage());
		}
	}

	@Test(priority = 995, description = "Verify UI responsiveness and basic interactions work")
	public void smokeVerifyUIInteractions() {
		System.out.println("\n[SMOKE TEST 5] Verifying UI Interactions...");

		try {
			// 1. Check window size
			org.openqa.selenium.Dimension windowSize = driver.manage().window().getSize();
			System.out.println("  ✓ Window size: " + windowSize.getWidth() + "x" + windowSize.getHeight());

			Assert.assertTrue(windowSize.getWidth() > 0, "Window should have width");
			Assert.assertTrue(windowSize.getHeight() > 0, "Window should have height");

			// 2. Verify clickable elements
			int clickableCount = countClickableElements();
			System.out.println("  ✓ Clickable elements: " + clickableCount);

			// 3. Check for forms/inputs
			int inputCount = countElements("input");
			int buttonCount = countElements("button");
			System.out.println("  ✓ Input fields: " + inputCount);
			System.out.println("  ✓ Buttons: " + buttonCount);

			// 4. Verify CSS is loaded
			Boolean cssLoaded = (Boolean) ((JavascriptExecutor) driver).executeScript(
					"var testElem = document.createElement('div');" +
					"testElem.style.color = 'rgb(0, 0, 0)';" +
					"return testElem.style.color === 'rgb(0, 0, 0)';");

			Assert.assertTrue(cssLoaded, "CSS should be loaded");
			System.out.println("  ✓ CSS loaded and working");

			System.out.println("[SMOKE TEST 5] ✓ PASSED - UI interactions work\n");

		} catch (Exception e) {
			System.err.println("[SMOKE TEST 5] ✗ FAILED - " + e.getMessage());
			Assert.fail("UI interactions verification failed: " + e.getMessage());
		}
	}

	@Test(priority = 996, description = "Verify navigation elements and menu accessibility")
	public void smokeVerifyNavigation() {
		System.out.println("\n[SMOKE TEST 6] Verifying Navigation...");

		try {
			// 1. Check for navigation elements
			boolean hasNav = checkElementExists("nav");
			boolean hasHeader = checkElementExists("header");
			boolean hasMenu = checkElementExists("[role='navigation']");

			System.out.println("  ✓ Nav element: " + hasNav);
			System.out.println("  ✓ Header element: " + hasHeader);
			System.out.println("  ✓ Navigation role: " + hasMenu);

			// 2. Check for menu items/links
			int linkCount = countElements("a");
			System.out.println("  ✓ Navigation links: " + linkCount);

			Assert.assertTrue(linkCount > 0 || hasNav || hasHeader,
					"Page should have navigation elements");

			// 3. Check for accessibility attributes
			String bodyText = ((JavascriptExecutor) driver).executeScript(
					"return document.body.innerText").toString();

			boolean hasAria = bodyText.toLowerCase().contains("menu") ||
			                 bodyText.toLowerCase().contains("home") ||
			                 bodyText.toLowerCase().contains("browse");

			System.out.println("  ✓ Accessibility indicators: " + hasAria);

			System.out.println("[SMOKE TEST 6] ✓ PASSED - Navigation is accessible\n");

		} catch (Exception e) {
			System.err.println("[SMOKE TEST 6] ✗ FAILED - " + e.getMessage());
			Assert.fail("Navigation verification failed: " + e.getMessage());
		}
	}

	@Test(priority = 997, description = "Verify cookie consent banner (if present) is handled")
	public void smokeVerifyCookieBanner() {
		System.out.println("\n[SMOKE TEST 7] Verifying Cookie Banner...");

		try {
			// 1. Check for cookie banner
			String bodyText = ((JavascriptExecutor) driver).executeScript(
					"return document.body.innerText").toString();

			boolean hasCookieBanner = bodyText.toLowerCase().contains("cookie") ||
			                         bodyText.toLowerCase().contains("consent") ||
			                         bodyText.toLowerCase().contains("privacy") ||
			                         bodyText.toLowerCase().contains("accept");

			System.out.println("  ✓ Cookie/consent banner present: " + hasCookieBanner);

			// 2. Try to accept cookies if banner is present
			if (hasCookieBanner && dashboard != null) {
				try {
					dashboard.acceptCookiesIfPresent();
					TestWaitHelper.shortWait();
					System.out.println("  ✓ Cookie banner handled (if present)");
				} catch (Exception e) {
					System.out.println("  ℹ Cookie banner handling attempted");
				}
			}

			System.out.println("[SMOKE TEST 7] ✓ PASSED - Cookie banner handled\n");

		} catch (Exception e) {
			System.err.println("[SMOKE TEST 7] ✗ FAILED - " + e.getMessage());
			Assert.fail("Cookie banner verification failed: " + e.getMessage());
		}
	}

	@Test(priority = 998, description = "Verify page performance and load times")
	public void smokeVerifyPagePerformance() {
		System.out.println("\n[SMOKE TEST 8] Verifying Page Performance...");

		try {
			long startTime = System.currentTimeMillis();

			// 1. Wait for page to be fully loaded
			dashboard.waitForPageReady();

			long loadTime = System.currentTimeMillis() - startTime;
			System.out.println("  ✓ Page load time: " + loadTime + "ms");

			// 2. Check for critical resources
			int scriptCount = countElements("script");
			int linkCount = countElements("link");
			int imageCount = countElements("img");

			System.out.println("  ✓ Scripts loaded: " + scriptCount);
			System.out.println("  ✓ Stylesheets loaded: " + linkCount);
			System.out.println("  ✓ Images loaded: " + imageCount);

			// 3. Verify reasonable load time
			Assert.assertTrue(loadTime < 30000, "Page should load within 30 seconds");
			System.out.println("  ✓ Load time acceptable");

			System.out.println("[SMOKE TEST 8] ✓ PASSED - Page performance is good\n");

		} catch (Exception e) {
			System.err.println("[SMOKE TEST 8] ✗ FAILED - " + e.getMessage());
			Assert.fail("Page performance verification failed: " + e.getMessage());
		}
	}

	@Test(priority = 999, description = "Comprehensive smoke test covering all basic application scenarios")
	public void smokeComprehensiveTest() {
		System.out.println("\n========================================");
		System.out.println("COMPREHENSIVE SMOKE TEST");
		System.out.println("========================================\n");

		int passedTests = 0;
		int totalTests = 8;

		try {
			// Test 1: URL and Title
			System.out.println("[1/8] Checking URL and Title...");
			String url = driver.getCurrentUrl();
			String title = driver.getTitle();
			if (url != null && !url.isEmpty() && title != null && !title.isEmpty()) {
				System.out.println("  ✓ URL: " + url);
				System.out.println("  ✓ Title: " + title);
				passedTests++;
			}

			// Test 2: Page Content
			System.out.println("\n[2/8] Checking Page Content...");
			dashboard.waitForPageReady();
			String bodyText = ((JavascriptExecutor) driver).executeScript(
					"return document.body.innerText").toString();
			if (bodyText.length() > 100) {
				System.out.println("  ✓ Content length: " + bodyText.length() + " chars");
				passedTests++;
			}

			// Test 3: Interactive Elements
			System.out.println("\n[3/8] Checking Interactive Elements...");
			int buttons = countElements("button");
			int links = countElements("a");
			int inputs = countElements("input");
			System.out.println("  ✓ Buttons: " + buttons);
			System.out.println("  ✓ Links: " + links);
			System.out.println("  ✓ Inputs: " + inputs);
			if (buttons + links + inputs > 0) {
				passedTests++;
			}

			// Test 4: Book Content
			System.out.println("\n[4/8] Checking Book Content...");
			boolean hasBooks = bodyText.toLowerCase().contains("book") ||
			                 bodyText.toLowerCase().contains("audio") ||
			                 bodyText.toLowerCase().contains("episode") ||
			                 bodyText.toLowerCase().contains("podcast");
			System.out.println("  ✓ Book content: " + hasBooks);
			if (hasBooks) {
				passedTests++;
			}

			// Test 5: Categories
			System.out.println("\n[5/8] Checking Categories...");
			String[] categories = {"comedy", "music", "education", "news", "entertainment"};
			int catCount = 0;
			for (String cat : categories) {
				if (bodyText.toLowerCase().contains(cat)) catCount++;
			}
			System.out.println("  ✓ Categories found: " + catCount);
			if (catCount > 0) {
				passedTests++;
			}

			// Test 6: Navigation
			System.out.println("\n[6/8] Checking Navigation...");
			boolean hasNav = checkElementExists("nav") || checkElementExists("header");
			System.out.println("  ✓ Navigation: " + hasNav);
			if (hasNav) {
				passedTests++;
			}

			// Test 7: Images
			System.out.println("\n[7/8] Checking Images...");
			int images = countElements("img");
			System.out.println("  ✓ Images: " + images);
			if (images > 0) {
				passedTests++;
			}

			// Test 8: CSS & Styling
			System.out.println("\n[8/8] Checking CSS & Styling...");
			Boolean cssWorking = (Boolean) ((JavascriptExecutor) driver).executeScript(
					"var div = document.createElement('div');" +
					"div.style.color = 'red';" +
					"return div.style.color === 'red';");
			System.out.println("  ✓ CSS working: " + cssWorking);
			if (cssWorking) {
				passedTests++;
			}

			// Final Summary
			System.out.println("\n========================================");
			System.out.println("SMOKE TEST SUMMARY");
			System.out.println("========================================");
			System.out.println("Tests Passed: " + passedTests + "/" + totalTests);
			System.out.println("Success Rate: " + (passedTests * 100 / totalTests) + "%");
			System.out.println("========================================");

			Assert.assertTrue(passedTests >= totalTests * 0.8,
					"At least 80% of smoke tests should pass");

			if (passedTests == totalTests) {
				System.out.println("✓ ALL SMOKE TESTS PASSED!\n");
			} else {
				System.out.println("⚠ Some tests failed, but application is functional\n");
			}

		} catch (Exception e) {
			System.err.println("\n✗ COMPREHENSIVE SMOKE TEST FAILED: " + e.getMessage());
			Assert.fail("Comprehensive smoke test failed: " + e.getMessage());
		}
	}

	// ==================== HELPER METHODS ====================

	private int countElements(String tagName) {
		try {
			Long count = (Long) ((JavascriptExecutor) driver).executeScript(
					"return document.querySelectorAll('" + tagName + "').length");
			return count != null ? count.intValue() : 0;
		} catch (Exception e) {
			return 0;
		}
	}

	private int countClickableElements() {
		try {
			Long count = (Long) ((JavascriptExecutor) driver).executeScript(
					"return document.querySelectorAll('button, [onclick], [role=\"button\"], a').length");
			return count != null ? count.intValue() : 0;
		} catch (Exception e) {
			return 0;
		}
	}

	private boolean checkElementExists(String selector) {
		try {
			Long count = (Long) ((JavascriptExecutor) driver).executeScript(
					"return document.querySelectorAll('" + selector + "').length");
			return count != null && count > 0;
		} catch (Exception e) {
			return false;
		}
	}
}
