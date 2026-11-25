package com.amalw.listeners;

import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.amalw.driver.DriverFactory;
import com.amalw.logging.FrameworkLogger;
import com.amalw.utils.ScreenshotManager;

import io.qameta.allure.Allure;

/** Writes test progress to the log and the Allure report, and attaches a screenshot on failure. */
public class AllureListener implements ITestListener {

	private static final Logger LOGGER = FrameworkLogger.getLogger(AllureListener.class);

	@Override
	public void onTestStart(ITestResult result) {

		String methodName = result.getMethod().getMethodName();

		LOGGER.info("========== TEST STARTED: {} ==========", methodName);
		Allure.step("Test started: " + methodName);
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		LOGGER.info("========== TEST PASSED: {} ==========", result.getMethod().getMethodName());
		Allure.step("Test passed successfully");
	}

	@Override
	public void onTestFailure(ITestResult result) {

		String methodName = result.getMethod().getMethodName();

		LOGGER.error("========== TEST FAILED: {} ==========", methodName);
		LOGGER.error("Failure reason:", result.getThrowable());
		Allure.step("Test failed: " + result.getThrowable().getMessage());

		attachScreenshot(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		LOGGER.warn("TEST SKIPPED: {}", buildTestName(result));
		Allure.step("Test skipped");
	}

	// Takes a screenshot of the failed test and attaches it to the Allure report
	private void attachScreenshot(ITestResult result) {

		String className = result.getTestClass().getRealClass().getSimpleName();
		String methodName = result.getMethod().getMethodName();

		try {
			// getDriver() throws an exception when there is no browser for this thread
			String screenshotPath = ScreenshotManager.screenCapture(DriverFactory.getDriver(), className, methodName);

			try (InputStream screenshot = new FileInputStream(screenshotPath)) {
				Allure.addAttachment("Failure Screenshot - " + methodName, screenshot);
			}

			LOGGER.info("Failure screenshot attached to Allure: {}", screenshotPath);
			Allure.step("Screenshot captured on failure");

		} catch (Exception e) {
			LOGGER.error("Failed to capture screenshot for test: {}", methodName, e);
			Allure.step("Failed to capture screenshot: " + e.getMessage());
		}
	}

	// Test name with its first parameter, so data-driven runs can be told apart
	private String buildTestName(ITestResult result) {

		String methodName = result.getMethod().getMethodName();
		Object[] parameters = result.getParameters();

		if (parameters.length > 0) {
			return methodName + " - " + parameters[0];
		}
		return methodName;
	}
}
