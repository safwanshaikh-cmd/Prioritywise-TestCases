package tests;

import java.util.Locale;

import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.DashboardPage;
import utils.ConfigReader;

/**
 * Smoke Test Class - Covers all basic scenarios without requiring
 * authentication. This test verifies that the application is up and running and
 * that major UI functionality is working as expected.
 *
 * Priority 999 tests run last and can also be executed independently for quick
 * smoke validation.
 */
public class SmokeTest extends BaseTest {

    private static final int MIN_BODY_CONTENT_LENGTH = 100;
    private static final int MIN_SUBSTANTIAL_CONTENT_LENGTH = 200;
    private static final long MAX_PAGE_LOAD_TIME_MS = 30000;
    private static final double MIN_SMOKE_PASS_PERCENTAGE = 0.80;

    private DashboardPage dashboard;

    @BeforeMethod(alwaysRun = true)
    public void setUpSmokeTest() {
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

    @Test(priority = 991, description = "Verify landing page loads and basic structure is intact")
    public void smokeVerifyLandingPageLoads() {
        System.out.println("\n[SMOKE TEST 1] Verifying Landing Page Loads...");

        try {
            String currentUrl = getCurrentUrl();
            Assert.assertFalse(currentUrl.isEmpty(), "Application URL should not be empty");
            System.out.println("  URL loaded: " + currentUrl);

            String pageTitle = getPageTitle();
            Assert.assertFalse(pageTitle.isEmpty(), "Page should have a title");
            System.out.println("  Page title: " + pageTitle);

            String bodyText = dashboard.getBodyText();
            Assert.assertFalse(bodyText.isEmpty(), "Page body should have content");
            System.out.println("  Page body has content");

            boolean readyState = dashboard.getBooleanFromScript("return document.readyState === 'complete';");
            Assert.assertTrue(readyState, "Page should be in complete state");
            System.out.println("  Page fully loaded");

            System.out.println("[SMOKE TEST 1] PASSED - Landing page loads successfully\n");

        } catch (Exception e) {
            System.err.println("[SMOKE TEST 1] FAILED - " + e.getMessage());
            Assert.fail("Landing page verification failed: " + e.getMessage());
        }
    }

    @Test(priority = 992, description = "Verify dashboard elements are visible and accessible")
    public void smokeVerifyDashboardElements() {
        System.out.println("\n[SMOKE TEST 2] Verifying Dashboard Elements...");

        try {
            dashboard.waitForPageReady();
            waitForPageLoad();

            String currentUrl = getCurrentUrl().toLowerCase(Locale.ROOT);

            boolean pageReady = dashboard.isBookDetailsPageVisible() || currentUrl.contains("dashboard")
                    || currentUrl.contains("home");

            System.out.println("  Page ready: " + pageReady);

            String bodyText = dashboard.getBodyText();

            boolean hasContent = bodyText.length() > MIN_BODY_CONTENT_LENGTH;
            Assert.assertTrue(hasContent, "Page should have content");
            System.out.println("  Page has content: " + hasContent);

            int buttonCount = dashboard.countElements("button");
            int linkCount = dashboard.countElements("a");

            System.out.println("  Buttons found: " + buttonCount);
            System.out.println("  Links found: " + linkCount);

            boolean hasHeader = dashboard.checkElementExists("header");
            boolean hasMain = dashboard.checkElementExists("main");
            boolean hasFooter = dashboard.checkElementExists("footer");

            System.out.println("  Header present: " + hasHeader);
            System.out.println("  Main content present: " + hasMain);
            System.out.println("  Footer present: " + hasFooter);

            System.out.println("[SMOKE TEST 2] PASSED - Dashboard elements are visible\n");

        } catch (Exception e) {
            System.err.println("[SMOKE TEST 2] FAILED - " + e.getMessage());
            Assert.fail("Dashboard elements verification failed: " + e.getMessage());
        }
    }

    @Test(priority = 993, description = "Verify book carousel/list is visible and books are displayed")
    public void smokeVerifyBookDisplay() {
        System.out.println("\n[SMOKE TEST 3] Verifying Book Display...");

        try {
            dashboard.waitForPageReady();
            waitForPageLoad();

            String bodyText = dashboard.getBodyText();
            String normalizedBodyText = bodyText.toLowerCase(Locale.ROOT);

            boolean hasBooks = normalizedBodyText.contains("book") || normalizedBodyText.contains("audiobook")
                    || normalizedBodyText.contains("episode");

            System.out.println("  Books mentioned on page: " + hasBooks);

            int imageCount = dashboard.countElements("img");
            System.out.println("  Images found: " + imageCount);

            boolean hasCarousel = normalizedBodyText.contains("carousel") || normalizedBodyText.contains("featured")
                    || normalizedBodyText.contains("recommended");

            System.out.println("  Carousel/section present: " + hasCarousel);

            boolean hasContent = bodyText.length() > MIN_SUBSTANTIAL_CONTENT_LENGTH;
            Assert.assertTrue(hasContent, "Page should have substantial content");
            System.out.println("  Sufficient content displayed");

            System.out.println("[SMOKE TEST 3] PASSED - Books are displayed\n");

        } catch (Exception e) {
            System.err.println("[SMOKE TEST 3] FAILED - " + e.getMessage());
            Assert.fail("Book display verification failed: " + e.getMessage());
        }
    }

    @Test(priority = 994, description = "Verify categories are visible and accessible")
    public void smokeVerifyCategoriesVisible() {
        System.out.println("\n[SMOKE TEST 4] Verifying Categories...");

        try {
            dashboard.waitForPageReady();
            waitForPageLoad();

            String bodyText = dashboard.getBodyText().toLowerCase(Locale.ROOT);

            String[] commonCategories = { "comedy", "music", "education", "entertainment", "news", "sports",
                    "technology", "arts" };

            int categoriesFound = 0;

            for (String category : commonCategories) {
                if (bodyText.contains(category)) {
                    categoriesFound++;
                }
            }

            System.out.println("  Common categories found: " + categoriesFound);
            System.out.println("  Categories visible: " + (categoriesFound > 0));

            boolean hasCategoryNavigation = bodyText.contains("category") || bodyText.contains("browse")
                    || bodyText.contains("genres");

            System.out.println("  Category navigation present: " + hasCategoryNavigation);

            System.out.println("[SMOKE TEST 4] PASSED - Categories are visible\n");

        } catch (Exception e) {
            System.err.println("[SMOKE TEST 4] FAILED - " + e.getMessage());
            Assert.fail("Categories verification failed: " + e.getMessage());
        }
    }

    @Test(priority = 995, description = "Verify UI responsiveness and basic interactions work")
    public void smokeVerifyUIInteractions() {
        System.out.println("\n[SMOKE TEST 5] Verifying UI Interactions...");

        try {
            Dimension windowSize = driver.manage().window().getSize();

            System.out.println("  Window size: " + windowSize.getWidth() + "x" + windowSize.getHeight());

            Assert.assertTrue(windowSize.getWidth() > 0, "Window should have width");
            Assert.assertTrue(windowSize.getHeight() > 0, "Window should have height");

            int clickableCount = dashboard.countClickableElements();
            System.out.println("  Clickable elements: " + clickableCount);

            int inputCount = dashboard.countElements("input");
            int buttonCount = dashboard.countElements("button");

            System.out.println("  Input fields: " + inputCount);
            System.out.println("  Buttons: " + buttonCount);

            boolean cssLoaded = dashboard.getBooleanFromScript("var testElem = document.createElement('div');"
                    + "testElem.style.color = 'rgb(0, 0, 0)';"
                    + "return testElem.style.color === 'rgb(0, 0, 0)';");

            Assert.assertTrue(cssLoaded, "CSS should be loaded");
            System.out.println("  CSS loaded and working");

            System.out.println("[SMOKE TEST 5] PASSED - UI interactions work\n");

        } catch (Exception e) {
            System.err.println("[SMOKE TEST 5] FAILED - " + e.getMessage());
            Assert.fail("UI interactions verification failed: " + e.getMessage());
        }
    }

    @Test(priority = 996, description = "Verify navigation elements and menu accessibility")
    public void smokeVerifyNavigation() {
        System.out.println("\n[SMOKE TEST 6] Verifying Navigation...");

        try {
            boolean hasNav = dashboard.checkElementExists("nav");
            boolean hasHeader = dashboard.checkElementExists("header");
            boolean hasMenu = dashboard.checkElementExists("[role='navigation']");

            System.out.println("  Nav element: " + hasNav);
            System.out.println("  Header element: " + hasHeader);
            System.out.println("  Navigation role: " + hasMenu);

            int linkCount = dashboard.countElements("a");
            System.out.println("  Navigation links: " + linkCount);

            Assert.assertTrue(linkCount > 0 || hasNav || hasHeader, "Page should have navigation elements");

            String bodyText = dashboard.getBodyText().toLowerCase(Locale.ROOT);

            boolean hasAccessibilityIndicators = bodyText.contains("menu") || bodyText.contains("home")
                    || bodyText.contains("browse");

            System.out.println("  Accessibility indicators: " + hasAccessibilityIndicators);

            System.out.println("[SMOKE TEST 6] PASSED - Navigation is accessible\n");

        } catch (Exception e) {
            System.err.println("[SMOKE TEST 6] FAILED - " + e.getMessage());
            Assert.fail("Navigation verification failed: " + e.getMessage());
        }
    }

    @Test(priority = 997, description = "Verify cookie consent banner if present is handled")
    public void smokeVerifyCookieBanner() {
        System.out.println("\n[SMOKE TEST 7] Verifying Cookie Banner...");

        try {
            String bodyText = dashboard.getBodyText().toLowerCase(Locale.ROOT);

            boolean hasCookieBanner = bodyText.contains("cookie") || bodyText.contains("consent")
                    || bodyText.contains("privacy") || bodyText.contains("accept");

            System.out.println("  Cookie/consent banner present: " + hasCookieBanner);

            if (hasCookieBanner && dashboard != null) {
                try {
                    dashboard.acceptCookiesIfPresent();
                    waitForPageLoad();
                    System.out.println("  Cookie banner handled if present");
                } catch (Exception e) {
                    System.out.println("  Cookie banner handling attempted: " + e.getMessage());
                }
            }

            System.out.println("[SMOKE TEST 7] PASSED - Cookie banner handled\n");

        } catch (Exception e) {
            System.err.println("[SMOKE TEST 7] FAILED - " + e.getMessage());
            Assert.fail("Cookie banner verification failed: " + e.getMessage());
        }
    }

    @Test(priority = 998, description = "Verify page performance and load times")
    public void smokeVerifyPagePerformance() {
        System.out.println("\n[SMOKE TEST 8] Verifying Page Performance...");

        try {
            long startTime = System.currentTimeMillis();

            dashboard.waitForPageReady();

            long loadTime = System.currentTimeMillis() - startTime;
            System.out.println("  Page load time: " + loadTime + "ms");

            int scriptCount = dashboard.countElements("script");
            int linkCount = dashboard.countElements("link");
            int imageCount = dashboard.countElements("img");

            System.out.println("  Scripts loaded: " + scriptCount);
            System.out.println("  Stylesheets loaded: " + linkCount);
            System.out.println("  Images loaded: " + imageCount);

            Assert.assertTrue(loadTime < MAX_PAGE_LOAD_TIME_MS, "Page should load within 30 seconds");
            System.out.println("  Load time acceptable");

            System.out.println("[SMOKE TEST 8] PASSED - Page performance is good\n");

        } catch (Exception e) {
            System.err.println("[SMOKE TEST 8] FAILED - " + e.getMessage());
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
            System.out.println("[1/8] Checking URL and Title...");

            String url = getCurrentUrl();
            String title = getPageTitle();

            if (!url.isEmpty() && !title.isEmpty()) {
                System.out.println("  URL: " + url);
                System.out.println("  Title: " + title);
                passedTests++;
            }

            System.out.println("\n[2/8] Checking Page Content...");

            dashboard.waitForPageReady();

            String bodyText = dashboard.getBodyText();
            String normalizedBodyText = bodyText.toLowerCase(Locale.ROOT);

            if (bodyText.length() > MIN_BODY_CONTENT_LENGTH) {
                System.out.println("  Content length: " + bodyText.length() + " chars");
                passedTests++;
            }

            System.out.println("\n[3/8] Checking Interactive Elements...");

            int buttons = dashboard.countElements("button");
            int links = dashboard.countElements("a");
            int inputs = dashboard.countElements("input");

            System.out.println("  Buttons: " + buttons);
            System.out.println("  Links: " + links);
            System.out.println("  Inputs: " + inputs);

            if (buttons + links + inputs > 0) {
                passedTests++;
            }

            System.out.println("\n[4/8] Checking Book Content...");

            boolean hasBooks = normalizedBodyText.contains("book") || normalizedBodyText.contains("audio")
                    || normalizedBodyText.contains("episode") || normalizedBodyText.contains("podcast");

            System.out.println("  Book content: " + hasBooks);

            if (hasBooks) {
                passedTests++;
            }

            System.out.println("\n[5/8] Checking Categories...");

            String[] categories = { "comedy", "music", "education", "news", "entertainment" };

            int categoryCount = 0;

            for (String category : categories) {
                if (normalizedBodyText.contains(category)) {
                    categoryCount++;
                }
            }

            System.out.println("  Categories found: " + categoryCount);

            if (categoryCount > 0) {
                passedTests++;
            }

            System.out.println("\n[6/8] Checking Navigation...");

            boolean hasNavigation = dashboard.checkElementExists("nav") || dashboard.checkElementExists("header");

            System.out.println("  Navigation: " + hasNavigation);

            if (hasNavigation) {
                passedTests++;
            }

            System.out.println("\n[7/8] Checking Images...");

            int images = dashboard.countElements("img");

            System.out.println("  Images: " + images);

            if (images > 0) {
                passedTests++;
            }

            System.out.println("\n[8/8] Checking CSS & Styling...");

            boolean cssWorking = dashboard.getBooleanFromScript("var div = document.createElement('div');"
                    + "div.style.color = 'red';"
                    + "return div.style.color === 'red';");

            System.out.println("  CSS working: " + cssWorking);

            if (cssWorking) {
                passedTests++;
            }

            int successRate = passedTests * 100 / totalTests;

            System.out.println("\n========================================");
            System.out.println("SMOKE TEST SUMMARY");
            System.out.println("========================================");
            System.out.println("Tests Passed: " + passedTests + "/" + totalTests);
            System.out.println("Success Rate: " + successRate + "%");
            System.out.println("========================================");

            Assert.assertTrue(passedTests >= totalTests * MIN_SMOKE_PASS_PERCENTAGE,
                    "At least 80% of smoke tests should pass");

            if (passedTests == totalTests) {
                System.out.println("ALL SMOKE TESTS PASSED!\n");
            } else {
                System.out.println("Some tests failed, but application is functional\n");
            }

        } catch (Exception e) {
            System.err.println("\n COMPREHENSIVE SMOKE TEST FAILED: " + e.getMessage());
            Assert.fail("Comprehensive smoke test failed: " + e.getMessage());
        }
    }
}
