package com.amalw.pages;

import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import com.amalw.config.ConfigManager;
import com.amalw.enums.Gender;
import com.amalw.logging.FrameworkLogger;
import com.amalw.model.User;

import io.qameta.allure.Step;

/** Page object for the registration page. Each action returns the page so calls can be chained. */
public class RegisterPage extends BasePage {

	private static final Logger LOGGER = FrameworkLogger.getLogger(RegisterPage.class);

	private static final String REGISTER_PATH = "/register";

	private static final int PAGE_LOAD_TIMEOUT_SECONDS = 10;
	private static final int RESULT_TIMEOUT_SECONDS = 5;
	private static final int VALIDATION_TIMEOUT_SECONDS = 2;

	// Locators
	private final By genderMaleRadio = By.id("gender-male");
	private final By genderFemaleRadio = By.id("gender-female");
	private final By firstNameInput = By.id("FirstName");
	private final By lastNameInput = By.id("LastName");
	private final By emailInput = By.id("Email");
	private final By companyInput = By.id("Company");
	private final By passwordInput = By.id("Password");
	private final By confirmPasswordInput = By.id("ConfirmPassword");
	private final By registerButton = By.id("register-button");

	private final By successMessage = By.cssSelector("div.result");
	private final By emailError = By.id("Email-error");
	private final By validationSummary = By.cssSelector(".validation-summary-errors");
	private final By fieldValidationErrors = By.cssSelector(".field-validation-error");
	private final By allErrors = By
			.cssSelector(".validation-summary-errors, .field-validation-error, .message-error");

	// ---------- Page actions ----------

	@Step("Open registration page")
	public RegisterPage open() {

		String url = ConfigManager.get("base.url") + REGISTER_PATH;
		LOGGER.info("Opening registration page: {}", url);

		navigateTo(url);
		waitForVisibility(registerButton, PAGE_LOAD_TIMEOUT_SECONDS);
		validatePageLoaded();

		return this;
	}

	@Step("Select gender: {gender}")
	public RegisterPage selectGender(Gender gender) {

		LOGGER.debug("Selecting gender: {}", gender);

		By genderRadio = switch (gender) {
		case MALE -> genderMaleRadio;
		case FEMALE -> genderFemaleRadio;
		};
		click(genderRadio);

		return this;
	}

	@Step("Fill registration form for user: {user.firstName} {user.lastName}")
	public RegisterPage fillForm(User user) {

		LOGGER.debug("Filling registration form for user: {} {}", user.getFirstName(), user.getLastName());

		type(firstNameInput, user.getFirstName());
		type(lastNameInput, user.getLastName());
		type(emailInput, user.getEmail());
		type(companyInput, user.getCompany());
		type(passwordInput, user.getPassword());
		type(confirmPasswordInput, user.getConfirmPassword());

		return this;
	}

	@Step("Submit registration form")
	public RegisterPage submit() {

		LOGGER.info("Submitting registration form");
		click(registerButton);
		LOGGER.info("Current URL after registration: {}", getDriver().getCurrentUrl());

		return this;
	}

	// ---------- Success checks ----------

	@Step("Validate registration success message visibility")
	public boolean isRegistrationSuccessful() {

		LOGGER.debug("Checking registration success message");

		boolean isSuccessful = isElementDisplayed(successMessage, RESULT_TIMEOUT_SECONDS);

		if (isSuccessful) {
			LOGGER.info("Registration successful");
		} else {
			LOGGER.warn("Registration success message was not displayed");
		}
		return isSuccessful;
	}

	@Step("Get registration confirmation message")
	public String getConfirmationMessage() {

		LOGGER.debug("Retrieving registration confirmation message");
		return getText(successMessage);
	}

	// ---------- Error checks ----------

	@Step("Check registration validation errors")
	public boolean isValidationErrorDisplayed() {
		return isElementDisplayed(validationSummary, VALIDATION_TIMEOUT_SECONDS)
				|| isElementDisplayed(fieldValidationErrors, VALIDATION_TIMEOUT_SECONDS);
	}

	@Step("Get registration validation error")
	public String getValidationErrorMessage() {

		if (isElementDisplayed(validationSummary, VALIDATION_TIMEOUT_SECONDS)) {
			return getText(validationSummary);
		}

		if (isElementDisplayed(fieldValidationErrors, VALIDATION_TIMEOUT_SECONDS)) {
			return getText(fieldValidationErrors);
		}

		return "No validation error message found.";
	}

	/** Collects every visible error on the page into one text, separated by " | ". */
	@Step("Get registration validation errors")
	public String getRegistrationErrors() {

		return getDriver().findElements(allErrors).stream()
				.filter(WebElement::isDisplayed)
				.map(element -> element.getText().trim())
				.filter(message -> !message.isEmpty())
				.distinct()
				.collect(Collectors.joining(" | "));
	}

	public boolean isEmailErrorDisplayed() {

		LOGGER.debug("Checking email validation error");
		return isElementDisplayed(emailError, RESULT_TIMEOUT_SECONDS);
	}

	// ---------- Page validation ----------

	@Override
	protected void validatePageLoaded() {

		String currentUrl = getDriver().getCurrentUrl();

		if (!currentUrl.contains(REGISTER_PATH)) {
			throw new IllegalStateException("Registration page failed to load. Current URL: " + currentUrl);
		}

		if (!isElementDisplayed(registerButton, PAGE_LOAD_TIMEOUT_SECONDS)) {
			throw new IllegalStateException("Register button is not visible on registration page.");
		}
	}
}
