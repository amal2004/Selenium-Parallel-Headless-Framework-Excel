package com.amalw.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

/* ConfigManager is responsible for loading and managing configuration values
from the config.properties file. */

public class ConfigManager {

	private static final Properties PROPS = new Properties();
	private static final Logger LOGGER = FrameworkLogger.getLogger(ConfigManager.class);
	private static final String CONFIG_FILE = "/config.properties";

	static {

		try (InputStream is = ConfigManager.class.getResourceAsStream(CONFIG_FILE)) {

			if (is == null) {
				throw new FrameworkException("Config file not found: " + CONFIG_FILE);
			}
			PROPS.load(is);
			LOGGER.info("Configuration loaded successfully | file={}", CONFIG_FILE);

		} catch (Exception e) {
			LOGGER.error("Failed to load config file: {}", CONFIG_FILE, e);

			throw new FrameworkException("Failed to load config file" + CONFIG_FILE, e);
		}
	}

	private ConfigManager() {
	}

	public static String getLogLevel() {
		return get("logLevel", "INFO");
	}

	// Get required property by key
	public static String get(String key) {

		String value = System.getProperty(key);

		if (value != null && !value.isBlank()) {
			LOGGER.debug("System property override used for key: {}", key);

		} else {
			value = PROPS.getProperty(key);
			LOGGER.debug("Reading property: {}", key);
		}

		if (value == null || value.trim().isEmpty()) {

			LOGGER.error("Missing configuration value for key: {}", key);
			throw new FrameworkException("Missing configuration value for key: " + key);
		}
		return value.trim();
	}

	// Get property with default value
	public static String get(String key, String defaultValue) {

		String systemValue = System.getProperty(key);

		// Validate System Property
		if (systemValue != null && !systemValue.isBlank()) {

			LOGGER.info("Resolved config | key={} | value={} | source=SYSTEM_PROPERTY", key, systemValue);
			return systemValue.trim();
		}

		// Validate Property File Value
		String propertyValue = PROPS.getProperty(key);

		if (propertyValue != null && !propertyValue.isBlank()) {
			LOGGER.info("Resolved config | key={} | value={} | source=CONFIG_FILE", key, propertyValue);
			return propertyValue.trim();
		}

		LOGGER.info("Resolved config | key={} | value={} | source=DEFAULT_VALUE", key, defaultValue);
		return defaultValue.trim();
	}

	// Get boolean property with default value (ex: headless=true)
	public static boolean getBoolean(String key, boolean defaultValue) {
		return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
	}

	// Get integer property with default value (ex: timeout=30)
	public static int getInt(String key, int defaultValue) {

		try {
			return Integer.parseInt(get(key, String.valueOf(defaultValue)));

		} catch (NumberFormatException exception) {
			LOGGER.error("Invalid integer config value for key: {}", key);
			throw new FrameworkException("Invalid integer value for key: " + key);
		}
	}

}