package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility to read test configuration properties with optional overrides.
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

	public static String getProperty(String key) {

		if (key == null || key.isBlank()) {
			return null;
		}

		String value = System.getProperty(key);

		if (value == null || value.isBlank()) {
			value = System.getenv(key);
		}

		if (value == null || value.isBlank()) {
			value = prop.getProperty(key);
		}

		return value;
	}

	public static String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		return (value == null || value.isBlank()) ? defaultValue : value;
	}

	public static int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Invalid int for {0}, using default {1}", new Object[] { key, defaultValue });
			return defaultValue;
		}
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		try {
			String value = getProperty(key);
			return value == null ? defaultValue : value.equalsIgnoreCase("true");
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
