package com.amalw.tests;

import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.amalw.base.BaseTest;
import com.amalw.dataproviders.RegistrationDataProvider;
import com.amalw.driver.DriverFactory;
import com.amalw.enums.Gender;
import com.amalw.logging.FrameworkLogger;
import com.amalw.model.User;
import com.amalw.pages.RegisterPage;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

/** Checks that a user can register, using every dataset marked "Y" in the Excel file. */
@Epic("User Management")
@Feature("Registration")
public class RegistrationTest extends BaseTest {

	private static final Logger LOGGER = FrameworkLogger.getLogger(RegistrationTest.class);
	private static final String EXPECTED_CONFIRMATION_TEXT = "registration completed";

	@DataProvider(name = "registrationData", parallel = false)
	public Object[][] getRegistrationData() {
		return RegistrationDataProvider.registrationData();
	}

	@Test(dataProvider = "registrationData")
	@Story("Successful user registration")
	@Severity(SeverityLevel.CRITICAL)
	public void testRegistration(User user, Gender gender) {

		LOGGER.info("Starting registration test for: {} {}", user.getFirstName(), user.getLastName());

		RegisterPage registerPage = new RegisterPage();

		// Arrange and act: open the page, fill the form and submit
		registerPage.open()
				.selectGender(gender)
				.fillForm(user)
				.submit();

		// Extra information that helps when the test fails
		boolean isSuccessMessageDisplayed = registerPage.isRegistrationSuccessful();
		LOGGER.info("Current URL after registration: {}", DriverFactory.getDriver().getCurrentUrl());
		LOGGER.info("Page title after registration: {}", DriverFactory.getDriver().getTitle());
		LOGGER.info("Registration success message visible: {}", isSuccessMessageDisplayed);

		// Assert
		Assert.assertTrue(isSuccessMessageDisplayed, "Registration success message was not displayed");

		String confirmationMessage = registerPage.getConfirmationMessage();

		Assert.assertTrue(confirmationMessage.toLowerCase().contains(EXPECTED_CONFIRMATION_TEXT),
				"Unexpected confirmation message: " + confirmationMessage);
	}
}
