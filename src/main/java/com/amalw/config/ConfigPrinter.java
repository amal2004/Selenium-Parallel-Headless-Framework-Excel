package com.amalw.config;

import org.slf4j.Logger;

import com.amalw.logging.FrameworkLogger;

public class ConfigPrinter {

	private static final Logger LOGGER = FrameworkLogger.getLogger(ConfigPrinter.class);

	private ConfigPrinter() {
	}

	public static void logExecutionConfiguration() {

		LOGGER.info("""

				================= EXECUTION CONFIGURATION =================
				environment={}
				browser={}
				headless={}
				grid={}
				retries={}
				===========================================================
				""",

				ConfigManager.get("environment", "qa"), ConfigManager.get("browser", "chrome"),
				ConfigManager.getBoolean("headless", false), ConfigManager.getBoolean("grid", false),
				ConfigManager.getInt("retries", 0));

	}
}
