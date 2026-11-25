package com.amalw.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

import com.amalw.exceptions.FrameworkException;
import com.amalw.logging.FrameworkLogger;

/**
 * Reads configuration values.
 *
 * Lookup order: 1) JVM system property (-Dkey=value), 2) config.properties,
 * 3) default value (only for the methods that accept one).
 */
public final class ConfigManager {

	private static final Logger LOGGER = FrameworkLogger.getLogger(ConfigManager.class);
	private static final String CONFIG_FILE = "/config.properties";
	private static final Properties PROPERTIES = loadConfigFile();

	private ConfigManager() {
	}

	public static String getLogLevel() {
		return get("logLevel", "INFO");
	}

	/** Returns a required value. Throws an exception if it is not configured. */
	public static String get(String key) {

		String value = readSystemProperty(key);

		if (value == null) {
			value = readConfigFileProperty(key);
		}

		if (value == null) {
			LOGGER.error("Missing configuration value for key: {}", key);
			throw new FrameworkException("Missing configuration value for key: " + key);
		}

		LOGGER.debug("Reading property: {}", key);
		return value;
	}

	/** Returns the configured value, or the default value when nothing is configured. */
	public static String get(String key, String defaultValue) {

		String systemValue = readSystemProperty(key);
		if (systemValue != null) {
			LOGGER.info("Resolved config | key={} | value={} | source=SYSTEM_PROPERTY", key, systemValue);
			return systemValue;
		}

		String fileValue = readConfigFileProperty(key);
		if (fileValue != null) {
			LOGGER.info("Resolved config | key={} | value={} | source=CONFIG_FILE", key, fileValue);
			return fileValue;
		}

		LOGGER.info("Resolved config | key={} | value={} | source=DEFAULT_VALUE", key, defaultValue);
		return defaultValue.trim();
	}

	/** Example: headless=true */
	public static boolean getBoolean(String key, boolean defaultValue) {
		return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
	}

	/** Example: pageLoadTimeout=30 */
	public static int getInt(String key, int defaultValue) {

		try {
			return Integer.parseInt(get(key, String.valueOf(defaultValue)));

		} catch (NumberFormatException e) {
			LOGGER.error("Invalid integer config value for key: {}", key);
			throw new FrameworkException("Invalid integer value for key: " + key, e);
		}
	}

	private static Properties loadConfigFile() {

		Properties properties = new Properties();

		try (InputStream inputStream = ConfigManager.class.getResourceAsStream(CONFIG_FILE)) {

			if (inputStream == null) {
				throw new FrameworkException("Config file not found: " + CONFIG_FILE);
			}

			properties.load(inputStream);
			LOGGER.info("Configuration loaded successfully | file={}", CONFIG_FILE);

		} catch (IOException e) {
			LOGGER.error("Failed to load config file: {}", CONFIG_FILE, e);
			throw new FrameworkException("Failed to load config file: " + CONFIG_FILE, e);
		}

		return properties;
	}

	/** Returns the trimmed system property, or null if it is missing or blank. */
	private static String readSystemProperty(String key) {
		return trimToNull(System.getProperty(key));
	}

	/** Returns the trimmed value from config.properties, or null if it is missing or blank. */
	private static String readConfigFileProperty(String key) {
		return trimToNull(PROPERTIES.getProperty(key));
	}

	private static String trimToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
