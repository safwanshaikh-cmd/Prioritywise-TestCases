package utils;

<<<<<<< HEAD
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {

	private static final Logger LOGGER = Logger.getLogger(ExcelUtils.class.getName());

	private static final String FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx";

	/**
	 * 🔥 MAIN METHOD (Backward compatible)
	 */
	public static Object[][] getTestData(String sheetName) {
		return getFilteredData(sheetName, "P0", "ALL");
	}

	/**
	 * 🔥 FLEXIBLE FILTER METHOD (Recommended)
	 */
	public static Object[][] getFilteredData(String sheetName, String priorityFilter, String moduleFilter) {

		List<Object[]> testData = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(FILE_PATH); Workbook workbook = WorkbookFactory.create(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);

			if (sheet == null) {
				throw new RuntimeException("Sheet not found: " + sheetName);
			}

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {

				Row row = sheet.getRow(i);
				if (row == null)
					continue;

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

				boolean isRunnable = run.equalsIgnoreCase("Y");

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

	/**
	 * 🔹 Safe cell read
	 */
	private static String getCell(Row row, int index) {
		Cell cell = row.getCell(index);

		if (cell == null)
			return "";

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
=======
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility to read Excel test data from src/test/resources/testdata.xlsx.
 * Returns a 2D Object array suitable for TestNG data providers.
 */
public class ExcelUtils {

    private static final Logger LOGGER = Logger.getLogger(ExcelUtils.class.getName());

    public static Object[][] getTestData(String sheetName) throws Exception {

        String path = System.getProperty("user.dir") + "/src/test/resources/testdata.xlsx";

        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            int rows = sheet.getPhysicalNumberOfRows();
            if (rows <= 1) {
                return new Object[0][0];
            }
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalStateException("Header row missing in sheet: " + sheetName);
            }
            int cols = headerRow.getPhysicalNumberOfCells();

            Object[][] data = new Object[rows - 1][cols];

            for (int i = 1; i < rows; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < cols; j++) {
                    Cell cell = row != null ? row.getCell(j) : null;
                    data[i - 1][j] = cell != null ? getCellValueAsString(cell) : "";
                }
            }

            return data;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read Excel file: {0}", e.getMessage());
            throw e;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }
>>>>>>> c0a5be4f0074aea2f4efcbafd6365ab3d80a23ab
}