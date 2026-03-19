package utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Utility class to read and filter Excel-driven test data.
 */
public class ExcelUtils {

	private static final Logger LOGGER = Logger.getLogger(ExcelUtils.class.getName());
	private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/testdata.xlsx";

	public static Object[][] getTestData(String sheetName) {
		return getFilteredData(sheetName, "P0", "ALL");
	}

	public static Object[][] getFilteredData(String sheetName, String priorityFilter, String moduleFilter) {

		List<Object[]> testData = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook workbook = WorkbookFactory.create(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new RuntimeException("Sheet not found: " + sheetName);
			}

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				String tcId = getCell(row, 0);
				String module = getCell(row, 1);
				String scenario = getCell(row, 2);
				String email = getCell(row, 3);
				String password = getCell(row, 4);
				String card = getCell(row, 5);
				String expiry = getCell(row, 6);
				String action = getCell(row, 7);
				String expected = getCell(row, 8);
				String priority = getCell(row, 9);
				String run = getCell(row, 10);

				boolean isRunnable = run.equalsIgnoreCase("Y") || run.equalsIgnoreCase("Yes");
				boolean isPriorityMatch = priorityFilter.equalsIgnoreCase("ALL")
						|| priority.equalsIgnoreCase(priorityFilter);
				boolean isModuleMatch = moduleFilter.equalsIgnoreCase("ALL") || module.equalsIgnoreCase(moduleFilter);

				if (isRunnable && isPriorityMatch && isModuleMatch) {
					testData.add(new Object[] { tcId, module, scenario, email, password, card, expiry, action, expected,
							priority, run });
				}
			}

			LOGGER.info("Filtered test data loaded: " + testData.size());
		} catch (Exception e) {
			throw new RuntimeException("Excel read failed: " + e.getMessage(), e);
		}

		return testData.toArray(new Object[0][0]);
	}

	private static String getCell(Row row, int index) {
		Cell cell = row.getCell(index);
		if (cell == null) {
			return "";
		}

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			return String.valueOf((long) cell.getNumericCellValue());
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			return cell.toString().trim();
		}
	}
}
