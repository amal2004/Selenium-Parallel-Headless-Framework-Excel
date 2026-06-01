package com.amalw.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

public final class ExcelUtils {

	private static final Logger LOGGER = FrameworkLogger.getLogger(ExcelUtils.class);

    private ExcelUtils(){}
    

	public static List<Object[]> getSheetData(String fileName, String sheetName) {

        List<Object[]> data = new ArrayList<>();

        String path = "testdata/" + fileName;

		try (InputStream inputStream = ExcelUtils.class.getClassLoader().getResourceAsStream(path);

			Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);

			if (sheet == null) {
				throw new FrameworkException("Sheet not found: " + sheetName);
			}

			LOGGER.info("Reading Excel sheet | file={} | sheet={}", fileName, sheetName);

			int rowCount = sheet.getPhysicalNumberOfRows();

            for (int i = 1; i < rowCount; i++) {

                Row row = sheet.getRow(i);
                
				String runMode = getCellValue(row.getCell(0));

                // Skip rows with runMode != Y
                if (!"Y".equalsIgnoreCase(runMode)) {

					LOGGER.info("Skipping dataset | row={} | runMode={}", i, runMode);
                    continue;
                }

				String firstName = getCellValue(row.getCell(1));

				String lastName = getCellValue(row.getCell(2));

				String gender = getCellValue(row.getCell(3));

				String company = getCellValue(row.getCell(4));

				String password = getCellValue(row.getCell(5));

				String confirmPassword = getCellValue(row.getCell(6));

				data.add(new Object[] { firstName, lastName, gender, company, password, confirmPassword });

				LOGGER.debug("Dataset added | row={} | user={} {}", i, firstName, lastName);
            }

			LOGGER.info("Executable datasets loaded | count={}", data.size());

            return data;

        } catch (Exception e) {

			LOGGER.error("Failed to read Excel data | file={} | sheet={}", fileName, sheetName, e);

			throw new FrameworkException("Failed to read Excel data", e);
        }
    }

    private static String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        return cell.toString().trim();
    }
}