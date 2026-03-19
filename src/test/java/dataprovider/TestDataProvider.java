package dataprovider;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.DataProvider;

import utils.ExcelUtils;

/**
 * Centralized DataProvider for TestNG with module and priority filtering.
 */
public class TestDataProvider {

	private static final Logger LOGGER = Logger.getLogger(TestDataProvider.class.getName());
	private static final String DEFAULT_SHEET = "Sheet1";

	@DataProvider(name = "testdata")
	public static Object[][] getExcelData() {
		try {
			LOGGER.info("Loading P0 test data from Excel");
			return ExcelUtils.getFilteredData(DEFAULT_SHEET, "P0", "ALL");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load test data: {0}", e.getMessage());
			return new Object[0][0];
		}
	}

	@DataProvider(name = "loginData")
	public static Object[][] getLoginData() {
		try {
			LOGGER.info("Loading Login P0 test data");
			return ExcelUtils.getFilteredData(DEFAULT_SHEET, "P0", "Login");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load login data: {0}", e.getMessage());
			return new Object[0][0];
		}
	}

	@DataProvider(name = "playerData")
	public static Object[][] getPlayerData() {
		try {
			LOGGER.info("Loading Player P0 test data");
			return ExcelUtils.getFilteredData(DEFAULT_SHEET, "P0", "Player");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load player data: {0}", e.getMessage());
			return new Object[0][0];
		}
	}
}
