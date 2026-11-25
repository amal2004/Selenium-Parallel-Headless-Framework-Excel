package com.amalw.config;

import org.slf4j.Logger;

import com.amalw.logging.FrameworkLogger;

/** Prints the active execution settings at the start of the run, which helps when debugging. */
public final class ConfigPrinter {

	private static final Logger LOGGER = FrameworkLogger.getLogger(ConfigPrinter.class);

	private ConfigPrinter() {
	}

	public static void logExecutionConfiguration() {

		String environment = ConfigManager.get("environment", "qa");
		String browser = ConfigManager.get("browser", "chrome");
		boolean headless = ConfigManager.getBoolean("headless", false);
		boolean grid = ConfigManager.getBoolean("grid", false);
		int retries = ConfigManager.getInt("retries", 0);

		LOGGER.info("""

				================= EXECUTION CONFIGURATION =================
				environment={}
				browser={}
				headless={}
				grid={}
				retries={}
				===========================================================
				""", environment, browser, headless, grid, retries);
	}
}
