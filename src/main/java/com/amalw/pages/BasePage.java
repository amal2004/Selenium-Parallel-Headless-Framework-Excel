package com.amalw.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import com.amalw.driver.DriverFactory;
import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

import io.qameta.allure.Step;

import java.time.Duration;

/* provides common WebDriver operations */

public abstract class BasePage {

	//private WebDriverWait wait;
	private static final Logger LOGGER = FrameworkLogger.getLogger(BasePage.class);
	private static final int DEFAULT_TIMEOUT = 15;

	protected WebDriver getDriver() {
		return DriverFactory.getDriver();
	}

	protected abstract void validatePageLoaded();
	
	// Create WebDriverWait using default timeout
	protected WebDriverWait getWait() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
	}


	protected void click(By locator) {
		try {
			LOGGER.debug("Clicking element: {}", locator);
			waitForClickable(locator).click();
			
		} catch (Exception e) {
			LOGGER.error("Click failed for locator: {}", locator, e);
			throw new FrameworkException("Unable to click element: " + locator, e);
		}
	}

	protected void type(By locator, String text) {

		try {

			LOGGER.debug("Typing into element: {}", locator);
			WebElement element = waitForVisible(locator);

			element.clear();
			element.sendKeys(text);

		} catch (Exception e) {
			LOGGER.error("Failed to type into element: {}", locator, e);
			throw new FrameworkException("Unable to type into element: " + locator, e);
		}
	}
	

	protected String getText(By locator) {
		try {

			LOGGER.debug("Retrieving text from element: {}", locator);
			return waitForVisible(locator).getText();

		} catch (Exception e) {
			LOGGER.error("Failed to retrieve text from element: {}", locator, e);
			throw new FrameworkException("Unable to retrieve text from element: " + locator, e);
		}
	}
	

	protected void navigateTo(String url) {

		try {

			LOGGER.info("Navigating to URL: {}", url);
			getDriver().get(url);

		} catch (Exception e) {
			LOGGER.error("Navigation failed", e);
			throw new FrameworkException("Failed to navigate to URL", e);
		}
	}

	
	// Check if element is displayed within given timeout
	protected boolean isElementDisplayed(By locator, int timeoutSeconds) {
		
		LOGGER.debug("Checking visibility of element: {} | timeout={}s",locator,timeoutSeconds);       
		try {
			new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds))
					.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return true;
			
		} catch (TimeoutException e) {
			
			throw new FrameworkException(
					String.format("Element not visible after %s seconds: %s", timeoutSeconds, locator), e);
		}
	}


	private WebElement waitForVisible(By locator) {
		try {
			LOGGER.debug("Waiting for element visibility: {}", locator);

			return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
			
		} catch (TimeoutException e) {
			throw new FrameworkException("Element is not visible after: " + DEFAULT_TIMEOUT + " seconds: " + locator, e);
		}
	}
	
	private WebElement waitForClickable(By locator) {

		try {
			LOGGER.debug("Waiting for element to become clickable: {}", locator);
			return getWait().until(ExpectedConditions.elementToBeClickable(locator));

		} catch (TimeoutException e) {
			throw new FrameworkException("Element not clickable after " + DEFAULT_TIMEOUT + " seconds: " + locator, e);
		}

	}
	
	
	protected WebElement waitForVisibility(
            By locator,
            int timeoutInSeconds) {

		LOGGER.debug(
                "Waiting for visibility of element: {}",
                locator);

        try {

            WebDriverWait wait =
                    new WebDriverWait(
                            getDriver(),
                            Duration.ofSeconds(timeoutInSeconds));

            wait.pollingEvery(Duration.ofMillis(500));

            wait.ignoring(StaleElementReferenceException.class);
            wait.ignoring(NoSuchElementException.class);

            return wait.until(
                    ExpectedConditions
                            .visibilityOfElementLocated(locator));

        } catch (TimeoutException exception) {

            String message = String.format(
                    "Element was not visible after %d seconds. Locator: %s",
                    timeoutInSeconds,
                    locator);

            LOGGER.error(message);

            throw new TimeoutException(message, exception);
        }
    }
	
}