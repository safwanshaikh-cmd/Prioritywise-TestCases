package base;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

<<<<<<< HEAD
import factory.DriverFactory;
=======
import pages.DashboardPage;
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
import utils.ConfigReader;
import utils.DriverFactory;

/**
 * BaseTest provides common test setup and teardown for UI tests.
 *
<<<<<<< HEAD
 * <p>
 * It centralizes WebDriver initialization and basic test housekeeping: -
 * disables noisy logging - initializes the WebDriver via {@link DriverFactory}
 * - navigates to the configured application URL - maximizes the browser window
 * and applies a global implicit wait - automatically accepts cookie popups when
 * present
 */
public class BaseTest {

	private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

	protected WebDriver driver;

	@BeforeMethod
	public void setup() {

		// Disable Selenium logs
		Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

		// Disable TestNG verbose logs
		System.setProperty("org.testng.verbose", "0");

		// Disable ChromeDriver logs
		System.setProperty("webdriver.chrome.silentOutput", "true");

		try {
			driver = DriverFactory.initDriver();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to initialize WebDriver: {0}", e.getMessage());
			throw e;
		}

		// Open application - read URL from config safely
		String url = ConfigReader.getProperty("url");
		if (url == null || url.isBlank()) {
			LOGGER.log(Level.WARNING, "Config property 'url' is missing or empty - proceeding without navigation");
		} else {
			try {
				driver.get(url);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Failed to navigate to URL {0}: {1}", new Object[] { url, e.getMessage() });
			}
		}

		// Maximize browser
		try {
			driver.manage().window().maximize();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to maximize window: {0}", e.getMessage());
		}

		// Global implicit wait - prefer config if present
		int implicit = 10;
		try {
			String cfg = ConfigReader.getProperty("implicitWait");
			if (cfg != null && !cfg.isBlank())
				implicit = Integer.parseInt(cfg);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Invalid implicitWait in config, using default 10s: {0}", e.getMessage());
		}

		try {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to set implicit wait: {0}", e.getMessage());
		}

	}

	@AfterMethod
	public void tearDown() {

		if (driver != null) {
			try {
				DriverFactory.quitDriver();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Error while quitting driver: {0}", e.getMessage());
			}
		}
	}
=======
 * <p>It centralizes WebDriver initialization and basic test housekeeping:
 * - disables noisy logging
 * - initializes the WebDriver via {@link DriverFactory}
 * - navigates to the configured application URL
 * - maximizes the browser window and applies a global implicit wait
 * - automatically accepts cookie popups when present
 */
public class BaseTest {

    private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

    protected WebDriver driver;

    @BeforeMethod
    public void setup() {

        // Disable Selenium logs
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        // Disable TestNG verbose logs
        System.setProperty("org.testng.verbose", "0");

        // Disable ChromeDriver logs
        System.setProperty("webdriver.chrome.silentOutput", "true");

        try {
            driver = DriverFactory.initDriver();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize WebDriver: {0}", e.getMessage());
            throw e;
        }

        // Open application - read URL from config safely
        String url = ConfigReader.getProperty("url");
        if (url == null || url.isBlank()) {
            LOGGER.log(Level.WARNING, "Config property 'url' is missing or empty - proceeding without navigation");
        } else {
            try {
                driver.get(url);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to navigate to URL {0}: {1}", new Object[]{url, e.getMessage()});
            }
        }

        // Maximize browser
        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Unable to maximize window: {0}", e.getMessage());
        }

        // Global implicit wait - prefer config if present
        int implicit = 10;
        try {
            String cfg = ConfigReader.getProperty("implicitWait");
            if (cfg != null && !cfg.isBlank()) implicit = Integer.parseInt(cfg);
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Invalid implicitWait in config, using default 10s: {0}", e.getMessage());
        }

        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Unable to set implicit wait: {0}", e.getMessage());
        }

        // Handle cookie popup automatically (best-effort)
        try {
            DashboardPage dashboard = new DashboardPage(driver);
            dashboard.acceptCookiesIfPresent();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Cookie acceptance step failed (continuing tests): {0}", e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {

        if (driver != null) {
            try {
                DriverFactory.quitDriver();
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Error while quitting driver: {0}", e.getMessage());
            }
        }
    }
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
}