package base;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import factory.DriverFactory;
import listeners.RetryListener;
import listeners.TestListener;
import utils.ConfigReader;
import utils.WaitUtils;

/**
 * BaseTest provides common setup and teardown for UI tests.
 * Uses explicit waits instead of Thread.sleep for synchronization.
 */
@Listeners({ TestListener.class, RetryListener.class })
public class BaseTest {

    protected static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

    protected WebDriver driver;
    protected WaitUtils waitUtils;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        initializeDriverSession();
    }

    private void initializeDriverSession() {
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        System.setProperty("org.testng.verbose", "0");
        System.setProperty("webdriver.chrome.silentOutput", "true");

        driver = DriverFactory.initDriver();

        int implicitWait = ConfigReader.getInt("implicitWait", 3);
        int pageLoadTimeout = ConfigReader.getInt("pageLoadTimeout", 30);
        int scriptTimeout = ConfigReader.getInt("scriptTimeout", 30);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeout));

        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Unable to maximize window: {0}", e.getMessage());
        }

        waitUtils = new WaitUtils(driver);

        navigateToBaseUrl();
        handleCookieConsent();
    }

    private void navigateToBaseUrl() {
        String url = ConfigReader.getProperty("url");
        if (url == null || url.isBlank()) {
            LOGGER.warning("Base URL not configured in config.properties");
            return;
        }

        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                LOGGER.log(Level.INFO, "Navigating to base URL (attempt {0}): {1}", new Object[]{i + 1, url});
                driver.get(url);
                waitUtils.waitForPageLoad(Duration.ofSeconds(10));
                LOGGER.info("Successfully navigated to base URL");
                return;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Navigation attempt {0} failed: {1}", new Object[]{i + 1, e.getMessage()});
                if (i < maxRetries - 1) {
                    // Use explicit wait instead of Thread.sleep
                    waitUtils.waitForPageLoad(Duration.ofSeconds(2));
                } else {
                    throw new RuntimeException("Failed to navigate to base URL after " + maxRetries + " attempts", e);
                }
            }
        }
    }

    private void handleCookieConsent() {
        try {
            pages.DashboardPage dashboard = new pages.DashboardPage(driver);
            dashboard.acceptCookiesIfPresent();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Cookie popup handling skipped: {0}", e.getMessage());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            DriverFactory.quitDriver();
            driver = null;
        }
    }

    /**
     * Navigate to a specific URL with explicit wait for page load.
     */
    protected void navigateTo(String url) {
        driver.get(url);
        waitUtils.waitForPageLoad();
    }

    /**
     * Refresh the current page and wait for it to load.
     */
    protected void refreshPage() {
        driver.navigate().refresh();
        waitUtils.waitForPageLoad();
    }

    /**
     * Get the current page URL.
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get the current page title.
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Wait for page to load completely.
     */
    protected void waitForPageLoad() {
        waitUtils.waitForPageLoad();
    }
}