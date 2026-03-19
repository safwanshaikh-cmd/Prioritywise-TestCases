package dataprovider;

import org.testng.annotations.DataProvider;
import utils.ExcelUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized DataProvider for TestNG. Supports filtering by Module, Priority,
 * and Run flag.
 */
public class TestDataProvider {

	private static final Logger LOGGER = Logger.getLogger(TestDataProvider.class.getName());

	private static final String FILE_PATH = "src/test/resources/Testdata.xlsx";
	private static final String DEFAULT_SHEET = "Sheet1";

	/**
	 * 🔥 MAIN DataProvider (Recommended for MasterTest) Filters: - Run = Y -
	 * Priority = P0
	 */
	@DataProvider(name = "testdata")
	public static Object[][] getExcelData() {

		try {
			LOGGER.info("Loading P0 test data from Excel");
			return ExcelUtils.getFilteredData("Sheet1", "P0", "ALL");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load test data: {0}", e.getMessage());
			return new Object[0][0];
		}
	}

	/**
	 * 🔥 Login Only (P0)
	 */
	@DataProvider(name = "loginData")
	public static Object[][] getLoginData() {

		try {
			LOGGER.info("Loading Login P0 test data");
			return ExcelUtils.getFilteredData("Sheet1", "P0", "ALL");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load login data: {0}", e.getMessage());
			return new Object[0][0];
		}
	}

	/**
	 * 🔥 Player Only (P0)
	 */
	@DataProvider(name = "playerData")
	public static Object[][] getPlayerData() {

		try {
			LOGGER.info("Loading Player P0 test data");
			return ExcelUtils.getFilteredData("Sheet1", "P0", "ALL");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load player data: {0}", e.getMessage());
			return new Object[0][0];
		}
	}
}