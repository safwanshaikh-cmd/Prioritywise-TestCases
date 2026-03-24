package factory;

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.SkipException;
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
		return initDriver(ConfigReader.getProperty("browser"), false);
	}

	public static WebDriver initDriver(boolean incognito) {
		return initDriver(ConfigReader.getProperty("browser"), incognito);
	}

	public static WebDriver initDriver(String browserOverride) {
		return initDriver(browserOverride, false);
	}

	public static WebDriver initDriver(String browserOverride, boolean incognito) {

		if (driver.get() != null) {
			return driver.get();
		}

		String browser = browserOverride == null || browserOverride.isBlank() ? ConfigReader.getProperty("browser")
				: browserOverride;
		String headless = ConfigReader.getProperty("headless");

		try {
			switch (browser.toLowerCase()) {

			case "chrome":
				WebDriverManager.chromedriver().setup();

				ChromeOptions chromeOptions = new ChromeOptions();

				if ("true".equalsIgnoreCase(headless)) {
					chromeOptions.addArguments("--headless=new");
				}

				if (incognito) {
					chromeOptions.addArguments("--incognito");
				}

				chromeOptions.addArguments("--start-maximized");
				chromeOptions.addArguments("--disable-notifications");

				driver.set(new ChromeDriver(chromeOptions));
				LOGGER.info(incognito ? "Chrome browser launched in incognito mode" : "Chrome browser launched");
				break;

			case "edge":
				WebDriverManager.edgedriver().setup();

				EdgeOptions edgeOptions = new EdgeOptions();

				if ("true".equalsIgnoreCase(headless)) {
					edgeOptions.addArguments("--headless=new");
				}

				if (incognito) {
					edgeOptions.addArguments("--inprivate");
				}

				driver.set(new EdgeDriver(edgeOptions));
				LOGGER.info(incognito ? "Edge browser launched in InPrivate mode" : "Edge browser launched");
				break;

			case "firefox":
				WebDriverManager.firefoxdriver().setup();

				FirefoxOptions firefoxOptions = new FirefoxOptions();

				if ("true".equalsIgnoreCase(headless)) {
					firefoxOptions.addArguments("--headless");
				}

				String firefoxBinary = ConfigReader.getProperty("firefox.binary", "");
				if (firefoxBinary != null && !firefoxBinary.isBlank()) {
					File binaryFile = new File(firefoxBinary);
					if (binaryFile.exists()) {
						firefoxOptions.setBinary(binaryFile.getAbsolutePath());
					} else {
						throw new SkipException("Configured Firefox binary was not found: " + firefoxBinary);
					}
				}

				if (incognito) {
					firefoxOptions.addArguments("-private");
				}

				try {
					driver.set(new FirefoxDriver(firefoxOptions));
				} catch (RuntimeException e) {
					String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
					if (message.contains("unable to find binary") || message.contains("expected browser binary location")) {
						throw new SkipException(
								"Firefox browser is not installed or its binary path is not configured. Set firefox.binary in config.properties.");
					}
					throw e;
				}
				LOGGER.info(incognito ? "Firefox browser launched in private mode" : "Firefox browser launched");
				break;

			default:
				LOGGER.warning("Invalid browser specified. Defaulting to Chrome.");
				WebDriverManager.chromedriver().setup();
				driver.set(new ChromeDriver());
			}

		} catch (Exception e) {
			if (e instanceof SkipException skipException) {
				throw skipException;
			}
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
