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
import pages.DashboardPage;
import utils.ConfigReader;

/**
 * BaseTest provides common setup and teardown for UI tests.
 */
@Listeners({ TestListener.class, RetryListener.class })
public class BaseTest {

	private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

	protected WebDriver driver;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		initializeDriverSession();
	}

	private void initializeDriverSession() {
		Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
		System.setProperty("org.testng.verbose", "0");
		System.setProperty("webdriver.chrome.silentOutput", "true");

		driver = DriverFactory.initDriver();

		// Set timeouts BEFORE navigation to prevent renderer timeout issues
		int implicitWait = ConfigReader.getInt("implicitWait", 10);
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

		String url = ConfigReader.getProperty("url");
		if (url != null && !url.isBlank()) {
			// Retry logic for initial navigation with page load timeout
			boolean navigationSuccess = false;
			int maxRetries = 3;
			for (int i = 0; i < maxRetries && !navigationSuccess; i++) {
				try {
					LOGGER.log(Level.INFO, "Navigation attempt {0} to: {1}", new Object[]{i + 1, url});
					driver.get(url);
					navigationSuccess = true;
					LOGGER.log(Level.INFO, "Successfully navigated to base URL");
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Navigation attempt {0} failed: {1}",
						new Object[]{i + 1, e.getMessage()});
					if (i < maxRetries - 1) {
						try {
							Thread.sleep(2000); // Wait 2 seconds before retry
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						}
					} else {
						throw new RuntimeException("Failed to navigate to base URL after " + maxRetries + " attempts", e);
					}
				}
			}
		}

		try {
			new DashboardPage(driver).acceptCookiesIfPresent();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Cookie popup not handled during setup: {0}", e.getMessage());
		}
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		if (driver != null) {
			DriverFactory.quitDriver();
			driver = null;
		}
	}
}
