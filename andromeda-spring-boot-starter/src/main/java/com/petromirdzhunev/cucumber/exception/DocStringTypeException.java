package com.petromirdzhunev.cucumber.exception;

import com.petromirdzhunev.cucumber.types.DocStringTypes;

import io.cucumber.java.DocStringType;

/**
 * Thrown when there is an error during construction of our custom {@link DocStringType}s defined in {@link DocStringTypes}
 */
public class DocStringTypeException extends RuntimeException {

	public DocStringTypeException(final String message) {
		super(message);
	}
}
