package com.amalw.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;

import com.amalw.enums.BrowserType;
import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

import io.github.bonigarcia.wdm.WebDriverManager;

/** Creates the browser instances (Chrome, Firefox, Edge) with their options. */
public final class BrowserManager {

	private static final Logger LOGGER = FrameworkLogger.getLogger(BrowserManager.class);

	// Chrome and Edge are both Chromium based, so they share the headless arguments
	private static final String[] CHROMIUM_HEADLESS_ARGS = { "--headless=new", "--window-size=1920,1080",
			"--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage" };

	private static final String[] FIREFOX_HEADLESS_ARGS = { "-headless", "--width=1920", "--height=1080" };

	// Driver binaries are downloaded once here, not every time a driver is created
	static {
		LOGGER.info("Setting up WebDriver binaries (Chrome, Firefox, Edge)");
		WebDriverManager.chromedriver().setup();
		WebDriverManager.firefoxdriver().setup();
		WebDriverManager.edgedriver().setup();
		LOGGER.info("WebDriver binaries setup completed");
	}

	private BrowserManager() {
	}

	/** Launches a new browser of the requested type. */
	public static WebDriver createDriver(BrowserType browserType, boolean headless) {

		long startTime = System.currentTimeMillis();

		LOGGER.info("Requested browser: {} | headless={}", browserType, headless);

		try {
			WebDriver driver = switch (browserType) {
			case CHROME -> new ChromeDriver(chromeOptions(headless));
			case FIREFOX -> new FirefoxDriver(firefoxOptions(headless));
			case EDGE -> new EdgeDriver(edgeOptions(headless));
			};

			LOGGER.info("Browser launched successfully: {} in {} ms", browserType,
					System.currentTimeMillis() - startTime);

			return driver;

		} catch (Exception e) {
			LOGGER.error("Driver creation failed for browser: {}", browserType, e);
			throw new FrameworkException("Failed to create driver: " + browserType, e);
		}
	}

	private static ChromeOptions chromeOptions(boolean headless) {

		LOGGER.debug("Configuring ChromeOptions | headless={}", headless);

		ChromeOptions options = new ChromeOptions();

		if (headless) {
			options.addArguments(CHROMIUM_HEADLESS_ARGS);
		}
		options.addArguments("--disable-notifications", "--start-maximized");

		return options;
	}

	private static FirefoxOptions firefoxOptions(boolean headless) {

		LOGGER.debug("Configuring FirefoxOptions | headless={}", headless);

		FirefoxOptions options = new FirefoxOptions();

		if (headless) {
			options.addArguments(FIREFOX_HEADLESS_ARGS);
		}

		return options;
	}

	private static EdgeOptions edgeOptions(boolean headless) {

		LOGGER.debug("Configuring EdgeOptions | headless={}", headless);

		EdgeOptions options = new EdgeOptions();

		if (headless) {
			options.addArguments(CHROMIUM_HEADLESS_ARGS);
			options.addArguments("--disable-notifications");
		}

		return options;
	}
}
