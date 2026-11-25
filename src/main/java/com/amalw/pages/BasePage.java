package com.amalw.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import com.amalw.driver.DriverFactory;
import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

/** Common WebDriver actions (click, type, wait, ...) shared by all page classes. */
public abstract class BasePage {

	private static final Logger LOGGER = FrameworkLogger.getLogger(BasePage.class);
	private static final int DEFAULT_TIMEOUT_SECONDS = 15;
	private static final Duration POLLING_INTERVAL = Duration.ofMillis(500);

	/** Each page should check that it is really the page we expect. */
	protected abstract void validatePageLoaded();

	protected WebDriver getDriver() {
		return DriverFactory.getDriver();
	}

	protected void navigateTo(String url) {

		try {
			LOGGER.info("Navigating to URL: {}", url);
			getDriver().get(url);

		} catch (Exception e) {
			LOGGER.error("Navigation failed for URL: {}", url, e);
			throw new FrameworkException("Failed to navigate to URL: " + url, e);
		}
	}

	protected void click(By locator) {

		try {
			LOGGER.debug("Clicking element: {}", locator);
			waitUntilClickable(locator).click();

		} catch (Exception e) {
			LOGGER.error("Click failed for locator: {}", locator, e);
			throw new FrameworkException("Unable to click element: " + locator, e);
		}
	}

	protected void type(By locator, String text) {

		try {
			LOGGER.debug("Typing into element: {}", locator);
			WebElement element = waitUntilVisible(locator);

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
			return waitUntilVisible(locator).getText();

		} catch (Exception e) {
			LOGGER.error("Failed to retrieve text from element: {}", locator, e);
			throw new FrameworkException("Unable to retrieve text from element: " + locator, e);
		}
	}

	/** Returns true if the element becomes visible in time, false otherwise (never throws). */
	protected boolean isElementDisplayed(By locator, int timeoutSeconds) {

		LOGGER.debug("Checking element visibility: {} | timeout={}s", locator, timeoutSeconds);

		try {
			new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds))
					.until(ExpectedConditions.visibilityOfElementLocated(locator));

			LOGGER.debug("Element is visible: {}", locator);
			return true;

		} catch (TimeoutException e) {
			LOGGER.warn("Element was not visible within {} seconds: {}", timeoutSeconds, locator);
			return false;
		}
	}

	/**
	 * Waits for the element to be visible with a custom timeout. Ignores stale and
	 * missing element errors while waiting.
	 *
	 * @throws TimeoutException if the element is still not visible after the timeout
	 */
	protected WebElement waitForVisibility(By locator, int timeoutSeconds) {

		LOGGER.debug("Waiting for visibility of element: {}", locator);

		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
			wait.pollingEvery(POLLING_INTERVAL);
			wait.ignoring(StaleElementReferenceException.class);
			wait.ignoring(NoSuchElementException.class);

			return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		} catch (TimeoutException e) {
			String message = String.format("Element was not visible after %d seconds. Locator: %s", timeoutSeconds,
					locator);
			LOGGER.error(message);
			throw new TimeoutException(message, e);
		}
	}

	private WebElement waitUntilVisible(By locator) {
		LOGGER.debug("Waiting for element visibility: {}", locator);
		return createDefaultWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	private WebElement waitUntilClickable(By locator) {
		LOGGER.debug("Waiting for element to become clickable: {}", locator);
		return createDefaultWait().until(ExpectedConditions.elementToBeClickable(locator));
	}

	private WebDriverWait createDefaultWait() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
	}
}
