package factory;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Dimension;
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
	private static final int DEFAULT_WINDOW_WIDTH = 1920;
	private static final int DEFAULT_WINDOW_HEIGHT = 1080;
	private static final String DEFAULT_WINDOW_SIZE = DEFAULT_WINDOW_WIDTH + "," + DEFAULT_WINDOW_HEIGHT;

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
		boolean headless = ConfigReader.getBoolean("headless", false);

		try {
			switch (browser.toLowerCase()) {

			case "chrome":
				WebDriverManager.chromedriver().setup();
				driver.set(new ChromeDriver(buildChromeOptions(headless, incognito)));
				logBrowserLaunch("Chrome", headless, incognito ? "incognito" : "standard");
				break;

			case "edge":
				WebDriverManager.edgedriver().setup();
				driver.set(new EdgeDriver(buildEdgeOptions(headless, incognito)));
				logBrowserLaunch("Edge", headless, incognito ? "InPrivate" : "standard");
				break;

			case "firefox":
				WebDriverManager.firefoxdriver().setup();
				FirefoxOptions firefoxOptions = buildFirefoxOptions(headless, incognito);
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
				logBrowserLaunch("Firefox", headless, incognito ? "private" : "standard");
				break;

			default:
				LOGGER.warning("Invalid browser specified. Defaulting to Chrome.");
				WebDriverManager.chromedriver().setup();
				driver.set(new ChromeDriver(buildChromeOptions(headless, incognito)));
				logBrowserLaunch("Chrome", headless, incognito ? "incognito" : "standard");
			}

			configureBrowserSession(driver.get(), headless);

		} catch (Exception e) {
			if (e instanceof SkipException skipException) {
				throw skipException;
			}
			LOGGER.log(Level.SEVERE, "Driver initialization failed: {0}", e.getMessage());
			throw new RuntimeException("Failed to initialize WebDriver", e);
		}

		return driver.get();
	}

	private static ChromeOptions buildChromeOptions(boolean headless, boolean incognito) {
		ChromeOptions options = new ChromeOptions();
		options.addArguments(getChromiumArguments(headless));
		options.addArguments("--disable-notifications");

		if (incognito) {
			options.addArguments("--incognito");
		}

		return options;
	}

	private static EdgeOptions buildEdgeOptions(boolean headless, boolean incognito) {
		EdgeOptions options = new EdgeOptions();
		options.addArguments(getChromiumArguments(headless));
		options.addArguments("--disable-notifications");

		if (incognito) {
			options.addArguments("--inprivate");
		}

		return options;
	}

	private static FirefoxOptions buildFirefoxOptions(boolean headless, boolean incognito) {
		FirefoxOptions options = new FirefoxOptions();

		if (headless) {
			options.addArguments("--headless");
		}

		options.addArguments("--width=" + ConfigReader.getInt("window.width", DEFAULT_WINDOW_WIDTH));
		options.addArguments("--height=" + ConfigReader.getInt("window.height", DEFAULT_WINDOW_HEIGHT));

		if (incognito) {
			options.addArguments("-private");
		}

		return options;
	}

	private static List<String> getChromiumArguments(boolean headless) {
		List<String> arguments = new ArrayList<>();
		arguments.add("--disable-gpu");
		arguments.add("--no-sandbox");
		arguments.add("--window-size=" + getWindowSizeArgument());

		if (headless) {
			arguments.add("--headless=new");
		}

		return arguments;
	}

	private static void configureBrowserSession(WebDriver webDriver, boolean headless) {
		if (webDriver == null) {
			return;
		}

		try {
			webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("pageLoadTimeout", 30)));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to set page load timeout: {0}", e.getMessage());
		}

		try {
			if (headless) {
				webDriver.manage().window().setSize(new Dimension(getWindowWidth(), getWindowHeight()));
			} else {
				webDriver.manage().window().maximize();
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Unable to apply preferred window state: {0}", e.getMessage());
			try {
				webDriver.manage().window().setSize(new Dimension(getWindowWidth(), getWindowHeight()));
			} catch (Exception resizeException) {
				LOGGER.log(Level.FINE, "Unable to resize browser window: {0}", resizeException.getMessage());
			}
		}
	}

	private static void logBrowserLaunch(String browserName, boolean headless, String mode) {
		LOGGER.info(String.format("%s browser launched in %s mode (%s). Window size: %dx%d", browserName,
				headless ? "headless" : "UI", mode, getWindowWidth(), getWindowHeight()));
	}

	private static int getWindowWidth() {
		return ConfigReader.getInt("window.width", DEFAULT_WINDOW_WIDTH);
	}

	private static int getWindowHeight() {
		return ConfigReader.getInt("window.height", DEFAULT_WINDOW_HEIGHT);
	}

	private static String getWindowSizeArgument() {
		String configuredSize = ConfigReader.getProperty("window.size", DEFAULT_WINDOW_SIZE);
		return configuredSize == null || configuredSize.isBlank() ? DEFAULT_WINDOW_SIZE : configuredSize;
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
