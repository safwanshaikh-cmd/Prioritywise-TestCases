package factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ConfigReader;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DriverFactory handles WebDriver initialization and management. - Supports
 * multiple browsers - Thread-safe for parallel execution - Config-driven
 * (browser, headless)
 */
public class DriverFactory {

	private static final Logger LOGGER = Logger.getLogger(DriverFactory.class.getName());

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	/**
	 * Initialize WebDriver based on config
	 */
	public static WebDriver initDriver() {

		if (driver.get() != null) {
			return driver.get();
		}

		String browser = ConfigReader.getProperty("browser");
		String headless = ConfigReader.getProperty("headless");

		try {
			switch (browser.toLowerCase()) {

			case "chrome":
				WebDriverManager.chromedriver().setup();

				ChromeOptions chromeOptions = new ChromeOptions();

				if ("true".equalsIgnoreCase(headless)) {
					chromeOptions.addArguments("--headless=new");
				}

				chromeOptions.addArguments("--start-maximized");
				chromeOptions.addArguments("--disable-notifications");

				driver.set(new ChromeDriver(chromeOptions));
				LOGGER.info("Chrome browser launched");
				break;

			case "edge":
				WebDriverManager.edgedriver().setup();

				EdgeOptions edgeOptions = new EdgeOptions();

				if ("true".equalsIgnoreCase(headless)) {
					edgeOptions.addArguments("--headless=new");
				}

				driver.set(new EdgeDriver(edgeOptions));
				LOGGER.info("Edge browser launched");
				break;

			case "firefox":
				WebDriverManager.firefoxdriver().setup();

				FirefoxOptions firefoxOptions = new FirefoxOptions();

				if ("true".equalsIgnoreCase(headless)) {
					firefoxOptions.addArguments("--headless");
				}

				driver.set(new FirefoxDriver(firefoxOptions));
				LOGGER.info("Firefox browser launched");
				break;

			default:
				LOGGER.warning("Invalid browser specified. Defaulting to Chrome.");
				WebDriverManager.chromedriver().setup();
				driver.set(new ChromeDriver());
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Driver initialization failed: {0}", e.getMessage());
			throw new RuntimeException("Failed to initialize WebDriver", e);
		}

		return driver.get();
	}

	/**
	 * Get current thread driver
	 */
	public static WebDriver getDriver() {
		return driver.get();
	}

	/**
	 * Quit driver safely
	 */
	public static void quitDriver() {

		if (driver.get() != null) {
			try {
				driver.get().quit();
				LOGGER.info("Driver closed successfully");
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error while quitting driver: {0}", e.getMessage());
			} finally {
				driver.remove();
			}
		}
	}
}