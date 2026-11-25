package com.amalw.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Single place to create SLF4J loggers, so every class gets its logger the same way. */
public final class FrameworkLogger {

	private FrameworkLogger() {
	}

	public static Logger getLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}
}
