package com.amalw.exceptions;

/** Runtime exception used for all errors raised by the framework. */
public class FrameworkException extends RuntimeException {

	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}
}
