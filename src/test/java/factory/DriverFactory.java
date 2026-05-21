package factory;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Dimension;
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

/**
 * DriverFactory handles WebDriver initialization and management.
 * Thread-safe for parallel execution with ThreadLocal driver management.
 */
public class DriverFactory {

    private static final Logger LOGGER = Logger.getLogger(DriverFactory.class.getName());
    private static final int DEFAULT_WINDOW_WIDTH = 1920;
    private static final int DEFAULT_WINDOW_HEIGHT = 1080;
    private static final String DEFAULT_WINDOW_SIZE = DEFAULT_WINDOW_WIDTH + "," + DEFAULT_WINDOW_HEIGHT;

    private static final ThreadLocal<WebDriver> driver = ThreadLocal.withInitial(() -> null);

    /**
     * Initialize WebDriver based on config properties.
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
            LOGGER.fine("Returning existing WebDriver instance");
            return driver.get();
        }

        String browser = (browserOverride == null || browserOverride.isBlank())
                ? ConfigReader.getProperty("browser")
                : browserOverride;
        boolean headless = ConfigReader.getBoolean("headless", false);

        LOGGER.info(() -> String.format("Initializing WebDriver: browser=%s, headless=%s, incognito=%s",
                browser, headless, incognito));

        try {
            switch (browser.toLowerCase()) {
                case "chrome" -> {
                    WebDriverManager.chromedriver().setup();
                    driver.set(new ChromeDriver(buildChromeOptions(headless, incognito)));
                    logBrowserLaunch("Chrome", headless, incognito ? "incognito" : "standard");
                }
                case "edge" -> {
                    WebDriverManager.edgedriver().setup();
                    driver.set(new EdgeDriver(buildEdgeOptions(headless, incognito)));
                    logBrowserLaunch("Edge", headless, incognito ? "InPrivate" : "standard");
                }
                case "firefox" -> {
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = buildFirefoxOptions(headless, incognito);

                    String firefoxBinary = ConfigReader.getProperty("firefox.binary", "");
                    if (firefoxBinary != null && !firefoxBinary.isBlank()) {
                        File binaryFile = new File(firefoxBinary);
                        if (binaryFile.exists()) {
                            firefoxOptions.setBinary(binaryFile.getAbsolutePath());
                        } else {
                            throw new SkipException("Firefox binary not found: " + firefoxBinary);
                        }
                    }

                    driver.set(new FirefoxDriver(firefoxOptions));
                    logBrowserLaunch("Firefox", headless, incognito ? "private" : "standard");
                }
                default -> {
                    LOGGER.warning(() -> "Invalid browser '" + browser + "', defaulting to Chrome");
                    WebDriverManager.chromedriver().setup();
                    driver.set(new ChromeDriver(buildChromeOptions(headless, incognito)));
                    logBrowserLaunch("Chrome", headless, incognito ? "incognito" : "standard");
                }
            }

            configureBrowserSession(driver.get(), headless);
            return driver.get();

        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Driver initialization failed: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    private static ChromeOptions buildChromeOptions(boolean headless, boolean incognito) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(getChromiumArguments(headless));
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");

        if (incognito) {
            options.addArguments("--incognito");
        }

        return options;
    }

    private static EdgeOptions buildEdgeOptions(boolean headless, boolean incognito) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments(getChromiumArguments(headless));
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");

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

        options.addArguments("--width=" + getWindowWidth());
        options.addArguments("--height=" + getWindowHeight());

        if (incognito) {
            options.addArguments("-private");
        }

        return options;
    }

    private static List<String> getChromiumArguments(boolean headless) {
        List<String> arguments = new ArrayList<>();
        arguments.add("--disable-gpu");
        arguments.add("--no-sandbox");
        arguments.add("--disable-dev-shm-usage");
        arguments.add("--disable-blink-features=AutomationControlled");
        arguments.add("--window-size=" + getWindowSizeArgument());
        arguments.add("--remote-allow-origins=*");

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
            int pageLoadTimeout = ConfigReader.getInt("pageLoadTimeout", 30);
            webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Unable to set page load timeout: " + e.getMessage());
        }

        try {
            if (headless) {
                webDriver.manage().window().setSize(new Dimension(getWindowWidth(), getWindowHeight()));
            } else {
                webDriver.manage().window().maximize();
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Unable to apply window state: " + e.getMessage());
            try {
                webDriver.manage().window().setSize(new Dimension(getWindowWidth(), getWindowHeight()));
            } catch (Exception resizeException) {
                LOGGER.log(Level.FINE, "Unable to resize window: " + resizeException.getMessage());
            }
        }
    }

    private static void logBrowserLaunch(String browserName, boolean headless, String mode) {
        LOGGER.info(() -> String.format("%s launched in %s mode (%s). Window: %dx%d",
                browserName, headless ? "headless" : "UI", mode, getWindowWidth(), getWindowHeight()));
    }

    private static int getWindowWidth() {
        return ConfigReader.getInt("window.width", DEFAULT_WINDOW_WIDTH);
    }

    private static int getWindowHeight() {
        return ConfigReader.getInt("window.height", DEFAULT_WINDOW_HEIGHT);
    }

    private static String getWindowSizeArgument() {
        String configuredSize = ConfigReader.getProperty("window.size", DEFAULT_WINDOW_SIZE);
        return (configuredSize == null || configuredSize.isBlank()) ? DEFAULT_WINDOW_SIZE : configuredSize;
    }

    /**
     * Get current thread's WebDriver instance.
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Check if driver is initialized for current thread.
     */
    public static boolean isDriverInitialized() {
        return driver.get() != null;
    }

    /**
     * Quit driver and clean up for current thread.
     */
    public static void quitDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            try {
                currentDriver.quit();
                LOGGER.info("WebDriver quit successfully for thread: " + Thread.currentThread().threadId());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error quitting driver: " + e.getMessage(), e);
            } finally {
                driver.remove();
            }
        }
    }

    /**
     * Get the number of active driver instances (for debugging).
     */
    public static int getActiveDriverCount() {
        // ThreadLocal doesn't expose this directly, but we track via logging
        return 1; // Placeholder - actual tracking would require custom implementation
    }
}
