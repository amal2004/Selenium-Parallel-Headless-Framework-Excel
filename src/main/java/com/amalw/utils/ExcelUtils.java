package com.amalw.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

/**
 * Reads test data from Excel files stored in src/test/resources/testdata.
 *
 * Expected sheet layout (row 1 is the header):
 * RunMode | FirstName | LastName | Gender | Company | Password | ConfirmPassword
 */
public final class ExcelUtils {

	private static final Logger LOGGER = FrameworkLogger.getLogger(ExcelUtils.class);

	private static final String TEST_DATA_FOLDER = "testdata/";
	private static final String RUN_MODE_YES = "Y";
	private static final int HEADER_ROW_INDEX = 0;

	// Column positions in the sheet
	private static final int RUN_MODE_COLUMN = 0;
	private static final int FIRST_NAME_COLUMN = 1;
	private static final int LAST_NAME_COLUMN = 2;
	private static final int GENDER_COLUMN = 3;
	private static final int COMPANY_COLUMN = 4;
	private static final int PASSWORD_COLUMN = 5;
	private static final int CONFIRM_PASSWORD_COLUMN = 6;

	// Converts any cell type (text, number, ...) to the text shown in Excel
	private static final DataFormatter DATA_FORMATTER = new DataFormatter();

	private ExcelUtils() {
	}

	/**
	 * Reads all rows with RunMode = Y.
	 *
	 * @return one array per row: [firstName, lastName, gender, company, password,
	 *         confirmPassword]
	 */
	public static List<Object[]> getSheetData(String fileName, String sheetName) {

		String filePath = TEST_DATA_FOLDER + fileName;

		try (InputStream inputStream = ExcelUtils.class.getClassLoader().getResourceAsStream(filePath)) {

			if (inputStream == null) {
				throw new FrameworkException("Excel file not found: " + filePath);
			}

			try (Workbook workbook = new XSSFWorkbook(inputStream)) {

				Sheet sheet = workbook.getSheet(sheetName);

				if (sheet == null) {
					throw new FrameworkException("Sheet not found: " + sheetName);
				}

				LOGGER.info("Reading Excel sheet | file={} | sheet={}", fileName, sheetName);

				List<Object[]> data = readRunnableRows(sheet);

				LOGGER.info("Executable datasets loaded | count={}", data.size());
				return data;
			}

		} catch (Exception e) {
			LOGGER.error("Failed to read Excel data | file={} | sheet={}", fileName, sheetName, e);
			throw new FrameworkException("Failed to read Excel data", e);
		}
	}

	private static List<Object[]> readRunnableRows(Sheet sheet) {

		List<Object[]> data = new ArrayList<>();

		// Start after the header row
		for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

			Row row = sheet.getRow(rowIndex);

			// Completely empty rows can come back as null
			if (row == null) {
				continue;
			}

			String runMode = readCell(row, RUN_MODE_COLUMN);

			if (!RUN_MODE_YES.equalsIgnoreCase(runMode)) {
				LOGGER.info("Skipping dataset | row={} | runMode={}", rowIndex, runMode);
				continue;
			}

			String firstName = readCell(row, FIRST_NAME_COLUMN);
			String lastName = readCell(row, LAST_NAME_COLUMN);
			String gender = readCell(row, GENDER_COLUMN);
			String company = readCell(row, COMPANY_COLUMN);
			String password = readCell(row, PASSWORD_COLUMN);
			String confirmPassword = readCell(row, CONFIRM_PASSWORD_COLUMN);

			data.add(new Object[] { firstName, lastName, gender, company, password, confirmPassword });

			LOGGER.debug("Dataset added | row={} | user={} {}", rowIndex, firstName, lastName);
		}

		return data;
	}

	// Returns the cell text without extra spaces. A missing cell gives an empty text.
	private static String readCell(Row row, int columnIndex) {
		return DATA_FORMATTER.formatCellValue(row.getCell(columnIndex)).trim();
	}
}
