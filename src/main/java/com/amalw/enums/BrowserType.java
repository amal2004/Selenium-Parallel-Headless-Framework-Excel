package com.amalw.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.amalw.exceptions.FrameworkException;

/* BrowserType defines the supported browser options */
public enum BrowserType {

	CHROME, FIREFOX, EDGE;

	// Converts string input into corresponding BrowserType enum
	public static BrowserType from(String value) {

		if (value == null || value.trim().isEmpty()) {
			throw new FrameworkException("Browser value is missing in config");
		}

		try {
			// Convert string to uppercase and map to enum
			return BrowserType.valueOf(value.trim().toUpperCase());

		} catch (IllegalArgumentException e) {

			String supportedBrowsers = Arrays.stream(BrowserType.values()).map(Enum::name)
					.collect(Collectors.joining(", "));

			// Handle invalid browser values
			throw new FrameworkException(
					String.format("Invalid browser: '%s'. Supported browsers: [%s]", value, supportedBrowsers), e);

		}
	}
}
