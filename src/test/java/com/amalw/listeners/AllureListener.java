package com.amalw.listeners;

import java.io.FileInputStream;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.amalw.driver.DriverFactory;
import com.amalw.logging.FrameworkLogger;
import com.amalw.utils.ScreenshotManager;

import io.qameta.allure.Allure;

public class AllureListener implements ITestListener {

	private static final Logger LOGGER = FrameworkLogger.getLogger(AllureListener.class);

	@Override
	public void onStart(ITestContext context) {
	}

	@Override
	public void onTestStart(ITestResult result) {

		LOGGER.info("========== TEST STARTED: {} ==========", result.getMethod().getMethodName());
		Allure.step("Test started: " + result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		LOGGER.info("========== TEST PASSED: {} ==========", result.getMethod().getMethodName());
		Allure.step("Test passed successfully");
	}

	@Override
	public void onTestFailure(ITestResult result) {

		WebDriver driver = DriverFactory.getDriver();
		String className = result.getTestClass().getRealClass().getSimpleName();
		String methodName = result.getMethod().getMethodName();

		LOGGER.error("========== TEST FAILED: {} ==========", result.getMethod().getMethodName());
		LOGGER.error("Failure reason:", result.getThrowable());

		Allure.step("Test failed: " + result.getThrowable().getMessage());

		if (driver != null) {
			
			try {
				String path = ScreenshotManager.screenCapture(driver, className, methodName);
				// Attach screenshot to Allure report
				Allure.addAttachment("Failure Screenshot - " + methodName, new FileInputStream(path));
				LOGGER.info("Failure screenshot attached to Allure: {}", path);
				Allure.step("Screenshot captured on failure");

			} catch (Exception e) {
				LOGGER.error("Failed to capture screenshot for test: {}", methodName, e);
				Allure.step("Failed to capture screenshot: " + e.getMessage());
			}
		} else {
			LOGGER.warn("Screenshot skipped because WebDriver is null");
			Allure.step("Screenshot skipped because WebDriver is null");
		}

		Allure.step("Test Failed");

	}

	@Override
	public void onTestSkipped(ITestResult result) {

		String testName = buildTestName(result);
		LOGGER.warn("TEST SKIPPED: {}", testName);
		Allure.step("Test skipped");
	}
	

	@Override
	public void onFinish(ITestContext context) {
	}

	private String buildTestName(ITestResult result) {
		
		String methodName = result.getMethod().getMethodName();
		Object[] parameters = result.getParameters();
		
		if (parameters.length > 0) {
			return methodName + " - " + parameters[0];
		}
		return methodName;
	}

}
