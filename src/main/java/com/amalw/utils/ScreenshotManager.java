package com.amalw.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import com.amalw.config.ConfigManager;
import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

/** Takes browser screenshots and saves them on disk. */
public final class ScreenshotManager {

	private static final Logger LOGGER = FrameworkLogger.getLogger(ScreenshotManager.class);
	private static final String SCREENSHOT_DIR = ConfigManager.get("screenshot.dir", "screenshots");
	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

	private ScreenshotManager() {
	}

	/**
	 * Saves a screenshot as {screenshot.dir}/{testClass}/{testName}_{timestamp}.png
	 *
	 * @return the full path of the saved screenshot
	 */
	public static String screenCapture(WebDriver driver, String testClass, String testName) {

		if (driver == null) {
			LOGGER.error("Screenshot capture skipped because WebDriver is null");
			throw new FrameworkException("Cannot capture screenshot because WebDriver is null");
		}

		Path folder = Paths.get(SCREENSHOT_DIR, testClass);
		Path destination = folder.resolve(buildFileName(testName));

		try {
			Files.createDirectories(folder);

			LOGGER.info("Capturing screenshot | test={} | file={}", testName, destination.getFileName());
			File tempFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			Files.copy(tempFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("Screenshot saved successfully: {}", destination.toAbsolutePath());

			return destination.toAbsolutePath().toString();

		} catch (IOException e) {
			LOGGER.error("Failed to save screenshot for test: {}", testName, e);
			throw new FrameworkException("Failed to capture screenshot: " + testName, e);
		}
	}

	// The timestamp keeps the name unique when the same test fails more than once
	private static String buildFileName(String testName) {
		String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
		return sanitizeFileName(testName) + "_" + timestamp + ".png";
	}

	// Replaces characters that are not allowed in file names
	private static String sanitizeFileName(String name) {
		return name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}
}
