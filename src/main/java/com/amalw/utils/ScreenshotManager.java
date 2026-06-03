package com.amalw.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import com.amalw.config.ConfigManager;
import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

//ScreenshotManager is a utility class responsible for capturing and storing
//browser screenshots during test execution.

public class ScreenshotManager {

	private static final Logger LOGGER = FrameworkLogger.getLogger(ScreenshotManager.class);
	private static final String BASE_DIR = ConfigManager.get("screenshot.dir", "screenshots");
	private static final String TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss_SSS";

	private ScreenshotManager() {
	}

	// Captures screenshot, creates unique file name and stores it in the base dir
	// under test name
	public static String screenCapture(WebDriver driver, String testName, String testClass) {

		// To avoid NullPointerException at runtime
		if (driver == null) {

			LOGGER.error("Screenshot capture skipped because WebDriver is null");

			throw new FrameworkException("Cannot capture screenshot because WebDriver is null");
		}

		// Unique timestamp
		String timeStamp = new SimpleDateFormat(TIMESTAMP_PATTERN).format(new Date());

		String sanitizedTestName = sanitizeFileName(testName);

		String fileName = sanitizedTestName + "_" + timeStamp + ".png";

		Path directoryPath = Paths.get(BASE_DIR, testClass);

		Path destinationPath = directoryPath.resolve(fileName);

		try {

			LOGGER.debug("Creating screenshot directory: {}", directoryPath);
			Files.createDirectories(directoryPath);

			LOGGER.info("Capturing screenshot | test={} | file={}", testName, fileName);
			File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("Screenshot saved successfully: {}", destinationPath.toAbsolutePath());

			return destinationPath.toAbsolutePath().toString();

		} catch (IOException e) {

			LOGGER.error("Failed to save screenshot for test: {}", testName, e);
			throw new FrameworkException("Failed to capture screenshot: " + testName);
		}
	}

	// Remove invalid filename characters.
	private static String sanitizeFileName(String name) {
		return name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}
}
