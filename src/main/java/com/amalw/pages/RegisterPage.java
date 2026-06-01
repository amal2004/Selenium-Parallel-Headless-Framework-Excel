package com.amalw.pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import com.amalw.config.ConfigManager;
import com.amalw.logging.FrameworkLogger;
import com.amalw.model.User;

import io.qameta.allure.Step;
import com.amalw.enums.Gender;

/* Provides actions to interact with registration form elements */
public class RegisterPage extends BasePage {
	
	private static final Logger logger = FrameworkLogger.getLogger(RegisterPage.class);
	private static final String REGISTER_PATH = "/register";
	private static final int DEFAULT_TIMEOUT = 10;
	private static final int SHORT_TIMEOUT = 5;

	private final  By genderMale = By.id("gender-male");
	private final  By genderFemale = By.id("gender-female");
	private final  By firstName = By.id("FirstName");
	private final  By lastName = By.id("LastName");
	private final  By email = By.id("Email");
	private final  By company = By.id("Company");
	private final  By password = By.id("Password");
	private final  By confirmPassword = By.id("ConfirmPassword");
	private final  By registerButton = By.id("register-button");
	private final  By successMsg = By.cssSelector("div.result");
	private final  By emailError = By.id("Email-error");

	@Step("Open registration page")
	public RegisterPage open() {

		String url = ConfigManager.get("base.url") + REGISTER_PATH;

		logger.info("Opening registration page: {}", url);

		navigateTo(url);

		waitForPageToLoad();

		validatePageLoaded();

//		if (!isElementDisplayed(registerButton, 10)) {
//			throw new IllegalStateException("Registration page failed to load");
//		}
		
		return this;
	}
	
	private void waitForPageToLoad() {
		waitForVisibility(registerButton, DEFAULT_TIMEOUT);
	}


	@Step("Select gender: {gender}")
	public RegisterPage  selectGender(Gender gender) {
		
		logger.debug("Selecting gender: {}", gender);

		switch (gender) {

        case MALE:
            click(genderMale);
            break;

        case FEMALE:
            click(genderFemale);
            break;

		default:
			throw new IllegalArgumentException("Unsupported gender: " + gender);
    }

		return this;
	}

	@Step("Fill registration form for user: {user.firstName} {user.lastName}")
	public RegisterPage fillForm(User user) {
		
		logger.debug("Filling registration form for user: {} {}", user.getFirstName(), user.getLastName());
		
		 waitForVisibility(firstName, DEFAULT_TIMEOUT);
		
		type(firstName, user.getFirstName());
		type(lastName, user.getLastName());
		type(email, user.getEmail());
		type(company, user.getCompany());
		type(password, user.getPassword());
		type(confirmPassword, user.getConfirmPassword());
		
		return this;
	}

	@Step("Submit registration form")
	public RegisterPage submit() {
		
		logger.debug("Submitting registration form");
		
        click(registerButton);
        
        return this;
	}

	@Step("Validate registration success message visibility")
	public boolean isRegistrationSuccessful() {
		
		logger.debug("Validating registration success");
		
		boolean status = isElementDisplayed(successMsg, SHORT_TIMEOUT);

		if (status) {
			logger.info("Registration successful");
			
		} else {
			logger.error("Registration success message not displayed");
		}

		return status;
	}
	
	// Get success confirmation message text
	@Step("Get registration confirmation message.")
	public String getConfirmationMessage() {
		
		logger.debug("Retrieving registration confirmation message");
		
		waitForVisibility(successMsg, SHORT_TIMEOUT);
		
		return getText(successMsg);
	}

	// Check if email validation error is displayed
	public boolean isEmailErrorDisplayed() {
		
		logger.debug("Checking email validation error");
		
		return isElementDisplayed(emailError, SHORT_TIMEOUT);
	}

	@Override
	protected void validatePageLoaded() {
	
	      if (!getDriver().getCurrentUrl().contains(REGISTER_PATH)) {

	            throw new IllegalStateException(
	                    "Registration page failed to load. Current URL: "
	                            + getDriver().getCurrentUrl());
	        }

	        if (!isElementDisplayed(registerButton, DEFAULT_TIMEOUT)) {

	            throw new IllegalStateException(
	                    "Register button is not visible on registration page.");
	        }
	}


}