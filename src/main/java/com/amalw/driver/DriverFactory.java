package com.amalw.driver;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.MDC;

import com.amalw.config.ConfigManager;
import com.amalw.enums.BrowserType;
import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

/* DriverFactory creates and manages thread-safe WebDriver instances */
public final class DriverFactory {

	private static final ThreadLocal<WebDriver> TLDRIVER = new ThreadLocal<>();
	private static final Logger LOGGER = FrameworkLogger.getLogger(DriverFactory.class);
	private static final int pageLoadTimeout = ConfigManager.getInt("pageLoadTimeout", 60);

	private DriverFactory() {
	}

	// Returns the WebDriver instance associated with the current thread
	public static WebDriver getDriver() {

		if (TLDRIVER.get() == null) {
			throw new FrameworkException("WebDriver is not initialized for current thread");
		}
		return TLDRIVER.get();
	}

	// Initializes WebDriver based on the specified browser type
	public static void initDriver(String browserName) {

		if (TLDRIVER.get() != null) {
			LOGGER.warn("WebDriver already exists for thread: {}", Thread.currentThread().getId());
			return;
		}

		long startTime = System.currentTimeMillis();

		try {

			// If no browser name is passed, get it from properties file
			String browserValue = (browserName == null || browserName.isBlank()) ? ConfigManager.get("browser")
					: browserName;

			BrowserType browser = BrowserType.from(browserValue);

			boolean headless = ConfigManager.getBoolean("headless", false);

			LOGGER.info("Execution environment | os={} | java={} | browser={} | headless={} | thread={}",
					System.getProperty("os.name"), System.getProperty("java.version"), browser, headless,
					Thread.currentThread().getId());

			MDC.put("browser", browser.name());
			MDC.put("threadId", String.valueOf(Thread.currentThread().getId()));

			LOGGER.info("Initializing WebDriver | browser={} | headless={} | thread={}", browser, headless,
					Thread.currentThread().getId());

			WebDriver driver = BrowserManager.createDriver(browser, headless);

			configureDriver(driver, headless);

			TLDRIVER.set(driver);

			long duration = System.currentTimeMillis() - startTime;

			LOGGER.info("WebDriver initialized successfully in {} ms", duration);

		} catch (Exception e) {

			LOGGER.error("Failed to initialize WebDriver", e);
			throw new FrameworkException("Driver initialization failed: " + e);
		}

	}

	private static void configureDriver(WebDriver driver, boolean headless) {

		long start = System.currentTimeMillis();

		LOGGER.debug("Applying browser configurations");

		try {
			if (!headless) {
				LOGGER.debug("Maximizing browser window");
				driver.manage().window().maximize();
			}

			// Disable implicit wait to avoid the conflicts
			driver.manage().timeouts().implicitlyWait(Duration.ZERO);
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

			LOGGER.debug("Configured driver timeouts | implicitWait=0 | pageLoadTimeout={}s", pageLoadTimeout);

			long duration = System.currentTimeMillis() - start;
			LOGGER.debug("Driver configuration completed in {} ms", duration);

		} catch (Exception e) {
			LOGGER.error("Failed to configure browser session", e);
			throw new FrameworkException("Driver configuration failed");
		}

	}

	// Quits the driver and removes it from ThreadLocal storage
	public static void quitDriver() {

		WebDriver driver = TLDRIVER.get();

		if (driver == null) {
			LOGGER.warn("Attempted to quit WebDriver but no session exists | thread={}",
					Thread.currentThread().getId());
			return;
		}

		try {
			LOGGER.info("Closing WebDriver session | thread={}", Thread.currentThread().getId());
			driver.quit();
			LOGGER.info("WebDriver session closed successfully");

		} catch (Exception e) {
			LOGGER.error("Failed to close WebDriver session", e);

		} finally {

			TLDRIVER.remove();
			MDC.clear();
			LOGGER.debug("ThreadLocal and MDC cleared");
		}
	}
}