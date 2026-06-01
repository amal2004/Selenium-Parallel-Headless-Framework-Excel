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

public final class BrowserManager {

	private static final Logger logger = FrameworkLogger.getLogger(BrowserManager.class);

	private BrowserManager() {
	}

	// This will avoid multiple WebDriverManager calls within the switch block
	static {
		logger.info("Setting up WebDriver binaries (Chrome, Firefox, Edge)");
		WebDriverManager.chromedriver().setup();
		WebDriverManager.firefoxdriver().setup();
		WebDriverManager.edgedriver().setup();
		logger.info("WebDriver binaries setup completed");
	}

	// Create driver based on browser type using values from enums
	public static WebDriver createDriver(BrowserType browserType, boolean headless) {

		long startTime = System.currentTimeMillis();

		logger.info("=== DRIVER CREATION STARTED ===");
		logger.info("Requested browser: {} | headless={}", browserType, headless);

		try {
			WebDriver driver;

			switch (browserType) {

			case CHROME:
				logger.info("Launching ChromeDriver");
				driver = new ChromeDriver(chromeOptions(headless));
				break;

			case FIREFOX:
				logger.info("Launching FirefoxDriver");
				driver = new FirefoxDriver(firefoxOptions(headless));
				break;

			case EDGE:
				logger.info("Launching EdgeDriver");
				driver = new EdgeDriver(edgeOptions(headless));
				break;

			default:
				logger.error("Unsupported browser type received: {}", browserType);
				throw new FrameworkException("Unsupported browser: " + browserType);
			}

			long duration = System.currentTimeMillis() - startTime;

			logger.info("Browser launched successfully: {} in {} ms", browserType, duration);
			logger.info("=== DRIVER CREATION COMPLETED ===");

			return driver;

		} catch (Exception e) {

			logger.error("Driver creation failed for browser: {}", browserType, e);
			throw new FrameworkException("Failed to create driver: " + browserType);
		}
	}

	// Configure Chrome options
	private static ChromeOptions chromeOptions(boolean headless) {

		logger.debug("Configuring ChromeOptions | headless={}", headless);

		ChromeOptions options = new ChromeOptions();
		if (headless) {
			options.addArguments("--headless=new", "--window-size=1920,1080", "--disable-gpu", "--no-sandbox",
					"--disable-dev-shm-usage");
		}
		options.addArguments("--disable-notifications", "--start-maximized");
		return options;
	}

	// Configure Firefox options
	private static FirefoxOptions firefoxOptions(boolean headless) {

		logger.debug("Configuring FirefoxOptions | headless={}", headless);

		FirefoxOptions options = new FirefoxOptions();

		if (headless) {
			options.addArguments("-headless", "--width=1920", "--height=1080");
		}

		return options;
	}

	// Configure Edge options
	private static EdgeOptions edgeOptions(boolean headless) {

		logger.debug("Configuring EdgeOptions | headless={}", headless);

		EdgeOptions options = new EdgeOptions();
		if (headless) {
			options.addArguments("--headless=new", "--window-size=1920,1080", "--disable-gpu", "--no-sandbox",
					"--disable-dev-shm-usage", "--disable-notifications");
		}
		return options;
	}
}
