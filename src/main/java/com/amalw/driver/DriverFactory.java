package com.amalw.driver;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.MDC;

import com.amalw.config.ConfigManager;
import com.amalw.enums.BrowserType;
import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

/**
 * Creates and stores one WebDriver per thread, so tests can run in parallel
 * without sharing a browser.
 */
public final class DriverFactory {

	private static final Logger LOGGER = FrameworkLogger.getLogger(DriverFactory.class);
	private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();
	private static final int PAGE_LOAD_TIMEOUT_SECONDS = ConfigManager.getInt("pageLoadTimeout", 60);

	private DriverFactory() {
	}

	/** Returns the WebDriver that belongs to the current thread. */
	public static WebDriver getDriver() {

		WebDriver driver = DRIVER_THREAD_LOCAL.get();

		if (driver == null) {
			throw new FrameworkException("WebDriver is not initialized for current thread");
		}
		return driver;
	}

	/**
	 * Starts a browser for the current thread.
	 *
	 * @param browserName "chrome", "firefox" or "edge". If empty, the "browser"
	 *                    value from the config is used.
	 */
	public static void initDriver(String browserName) {

		if (DRIVER_THREAD_LOCAL.get() != null) {
			LOGGER.warn("WebDriver already exists for thread: {}", currentThreadId());
			return;
		}

		long startTime = System.currentTimeMillis();

		try {
			BrowserType browser = resolveBrowser(browserName);
			boolean headless = ConfigManager.getBoolean("headless", false);

			logExecutionEnvironment(browser, headless);
			addBrowserInfoToLogs(browser);

			WebDriver driver = BrowserManager.createDriver(browser, headless);

			// Store the driver before configuring it, so quitDriver() can still close the
			// browser if the configuration step fails
			DRIVER_THREAD_LOCAL.set(driver);
			configureDriver(driver, headless);

			LOGGER.info("WebDriver initialized successfully in {} ms", System.currentTimeMillis() - startTime);

		} catch (Exception e) {
			LOGGER.error("Failed to initialize WebDriver", e);
			throw new FrameworkException("Driver initialization failed: " + e.getMessage(), e);
		}
	}

	/** Closes the browser of the current thread and cleans up thread data. */
	public static void quitDriver() {

		WebDriver driver = DRIVER_THREAD_LOCAL.get();

		if (driver == null) {
			LOGGER.warn("Attempted to quit WebDriver but no session exists | thread={}", currentThreadId());
			return;
		}

		try {
			LOGGER.info("Closing WebDriver session | thread={}", currentThreadId());
			driver.quit();
			LOGGER.info("WebDriver session closed successfully");

		} catch (Exception e) {
			LOGGER.error("Failed to close WebDriver session", e);

		} finally {
			DRIVER_THREAD_LOCAL.remove();
			MDC.clear();
			LOGGER.debug("ThreadLocal and MDC cleared");
		}
	}

	// Use the browser from the argument (testng.xml), otherwise fall back to config.properties
	private static BrowserType resolveBrowser(String browserName) {

		boolean browserNotProvided = browserName == null || browserName.isBlank();
		String browserValue = browserNotProvided ? ConfigManager.get("browser") : browserName;

		return BrowserType.from(browserValue);
	}

	private static void logExecutionEnvironment(BrowserType browser, boolean headless) {

		LOGGER.info("Execution environment | os={} | java={} | browser={} | headless={} | thread={}",
				System.getProperty("os.name"), System.getProperty("java.version"), browser, headless,
				currentThreadId());
	}

	// MDC values can be used in the logback pattern to tell parallel logs apart
	private static void addBrowserInfoToLogs(BrowserType browser) {

		MDC.put("browser", browser.name());
		MDC.put("threadId", String.valueOf(currentThreadId()));
	}

	private static void configureDriver(WebDriver driver, boolean headless) {

		LOGGER.debug("Applying browser configurations");

		try {
			if (!headless) {
				driver.manage().window().maximize();
			}

			// Implicit wait is disabled because it conflicts with explicit waits
			driver.manage().timeouts().implicitlyWait(Duration.ZERO);
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT_SECONDS));

			LOGGER.debug("Configured driver timeouts | implicitWait=0 | pageLoadTimeout={}s",
					PAGE_LOAD_TIMEOUT_SECONDS);

		} catch (Exception e) {
			LOGGER.error("Failed to configure browser session", e);
			throw new FrameworkException("Driver configuration failed", e);
		}
	}

	private static long currentThreadId() {
		return Thread.currentThread().getId();
	}
}
