package com.amalw.utils;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/** Changes the log level of the framework at runtime (for example from config.properties). */
public final class LogManagerUtil {

	private static final String FRAMEWORK_PACKAGE = "com.amalw";

	private LogManagerUtil() {
	}

	/** Sets the level for every logger under com.amalw. Example values: DEBUG, INFO, WARN. */
	public static void configureFrameworkLogLevel(String level) {

		Logger frameworkLogger = (Logger) LoggerFactory.getLogger(FRAMEWORK_PACKAGE);
		frameworkLogger.setLevel(Level.toLevel(level));
	}
}
