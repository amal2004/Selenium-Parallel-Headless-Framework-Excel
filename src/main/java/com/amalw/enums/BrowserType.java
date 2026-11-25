package com.amalw.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.amalw.exceptions.FrameworkException;

/** Browsers supported by the framework. */
public enum BrowserType {

	CHROME, FIREFOX, EDGE;

	/**
	 * Converts a text value such as "chrome" or " Firefox " into a BrowserType.
	 *
	 * @throws FrameworkException if the value is empty or not a supported browser
	 */
	public static BrowserType from(String value) {

		if (value == null || value.isBlank()) {
			throw new FrameworkException("Browser value is missing in config");
		}

		try {
			return BrowserType.valueOf(value.trim().toUpperCase());

		} catch (IllegalArgumentException e) {
			String supportedBrowsers = Arrays.stream(values())
					.map(Enum::name)
					.collect(Collectors.joining(", "));

			throw new FrameworkException(
					String.format("Invalid browser: '%s'. Supported browsers: [%s]", value, supportedBrowsers), e);
		}
	}
}
