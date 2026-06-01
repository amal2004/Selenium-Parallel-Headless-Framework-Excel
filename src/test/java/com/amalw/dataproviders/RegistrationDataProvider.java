package com.amalw.dataproviders;

import java.util.List;

import org.testng.annotations.DataProvider;

import com.amalw.enums.Gender;
import com.amalw.model.User;
import com.amalw.model.UserFactory;
import com.amalw.utils.ExcelUtils;

public class RegistrationDataProvider {

	// @DataProvider(name = "registrationData", parallel = true)
	    public static Object[][] registrationData() {

			List<Object[]> excelData = ExcelUtils.getSheetData("registration-data.xlsx", "Registration");

	        Object[][] data =
	                new Object[excelData.size()][2];

	        for (int i = 0; i < excelData.size(); i++) {

	            Object[] row = excelData.get(i);

	            User user =
	                    UserFactory.buildUser(
	                            row[0].toString(),
	                            row[1].toString(),
	                            row[3].toString(),
	                            row[4].toString(),
	                            row[5].toString());

	            Gender gender =
	                    Gender.valueOf(
	                            row[2].toString().toUpperCase());

	            data[i] = new Object[]{
	                    user,
	                    gender
	            };
	        }

	        return data;
	    }
}
