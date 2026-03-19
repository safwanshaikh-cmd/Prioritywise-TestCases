package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
<<<<<<< HEAD
 * Utility to read test configuration properties. Supports: - config.properties
 * file - System properties override - Environment variable override
 */
public class ConfigReader {

	private static final Logger LOGGER = Logger.getLogger(ConfigReader.class.getName());

	private static final String CONFIG_PATH = "src/test/resources/config.properties";

	private static final Properties prop = new Properties();

	static {
		try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
			prop.load(fis);
			LOGGER.log(Level.INFO, "Configuration loaded from: {0}", CONFIG_PATH);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to load configuration from {0}: {1}",
					new Object[] { CONFIG_PATH, e.getMessage() });
		}
	}

	/**
	 * Get property value (with override support)
	 */
	public static String getProperty(String key) {

		if (key == null || key.isBlank()) {
			LOGGER.warning("Requested config key is null/blank");
			return null;
		}

		// 1️⃣ System property (highest priority)
		String value = System.getProperty(key);

		// 2️⃣ Environment variable
		if (value == null || value.isBlank()) {
			value = System.getenv(key);
		}

		// 3️⃣ Config file
		if (value == null || value.isBlank()) {
			value = prop.getProperty(key);
		}

		if (value == null) {
			LOGGER.log(Level.FINE, "Property not found: {0}", key);
		}

		return value;
	}

	/**
	 * Get property with default value
	 */
	public static String getProperty(String key, String defaultValue) {

		String value = getProperty(key);

		if (value == null || value.isBlank()) {
			return defaultValue;
		}

		return value;
	}

	/**
	 * Get integer property safely
	 */
	public static int getInt(String key, int defaultValue) {

		try {
			return Integer.parseInt(getProperty(key));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Invalid int for {0}, using default {1}", new Object[] { key, defaultValue });
			return defaultValue;
		}
	}

	/**
	 * Get boolean property safely
	 */
	public static boolean getBoolean(String key, boolean defaultValue) {

		try {
			String value = getProperty(key);
			return value != null && value.equalsIgnoreCase("true");
		} catch (Exception e) {
			return defaultValue;
		}
	}
=======
 * Utility to read test configuration properties from
 * src/test/resources/config.properties.
 * <p>
 * This class is intentionally simple and exposes a single
 * accessor getProperty(String). It loads properties once
 * in a static initializer using a try-with-resources block
 * and logs any load failures.
 */
public class ConfigReader {

    private static final Logger LOGGER = Logger.getLogger(ConfigReader.class.getName());

    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    private static final Properties prop = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            prop.load(fis);
            LOGGER.log(Level.FINE, "Loaded configuration from {0}", CONFIG_PATH);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to load configuration from {0}: {1}", new Object[] { CONFIG_PATH, e.getMessage() });
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
}