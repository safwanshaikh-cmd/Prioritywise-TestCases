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

		String url = ConfigReader.getProperty("url");
		if (url != null && !url.isBlank()) {
			driver.get(url);
		}

		try {
			driver.manage().window().maximize();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to maximize window: {0}", e.getMessage());
		}

		int implicitWait = ConfigReader.getInt("implicitWait", 10);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

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
