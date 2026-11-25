package com.amalw.dataproviders;

import java.util.ArrayList;
import java.util.List;

import com.amalw.enums.Gender;
import com.amalw.model.User;
import com.amalw.model.UserFactory;
import com.amalw.utils.ExcelUtils;

/** Converts the Excel rows into the (User, Gender) pairs used by the registration test. */
public class RegistrationDataProvider {

	private static final String EXCEL_FILE = "registration-data.xlsx";
	private static final String SHEET_NAME = "Registration";

	// Positions inside each row returned by ExcelUtils.getSheetData()
	private static final int FIRST_NAME = 0;
	private static final int LAST_NAME = 1;
	private static final int GENDER = 2;
	private static final int COMPANY = 3;
	private static final int PASSWORD = 4;
	private static final int CONFIRM_PASSWORD = 5;

	public static Object[][] registrationData() {

		List<Object[]> excelRows = ExcelUtils.getSheetData(EXCEL_FILE, SHEET_NAME);
		List<Object[]> testData = new ArrayList<>();

		for (Object[] row : excelRows) {

			User user = UserFactory.buildUser(
					row[FIRST_NAME].toString(),
					row[LAST_NAME].toString(),
					row[COMPANY].toString(),
					row[PASSWORD].toString(),
					row[CONFIRM_PASSWORD].toString());

			Gender gender = Gender.valueOf(row[GENDER].toString().toUpperCase());

			testData.add(new Object[] { user, gender });
		}

		return testData.toArray(new Object[0][]);
	}
}
