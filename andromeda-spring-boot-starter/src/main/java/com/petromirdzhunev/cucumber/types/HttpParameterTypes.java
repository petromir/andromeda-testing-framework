package com.petromirdzhunev.cucumber.types;

import io.cucumber.java.ParameterType;

/**
 * Defines custom {@link ParameterType}s for Cucumber steps related to HTTP operations.
 */
public class HttpParameterTypes {

	@ParameterType("GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS")
	public String httpMethod(final String httpMethod) {
		return httpMethod;
	}

	@ParameterType("[a-zA-Z0-9_/-]")
	public String urlPath(final String urlPath) {
		return urlPath;
	}

	@ParameterType("[^\\s]+")
	public String key(final String key) {
		return key;
	}

	@ParameterType(".+")
	public String value(final String value) {
		return value;
	}

	@ParameterType("[1-5][0-9]{2}")
	public String statusCode(final String statusCode) {
		return statusCode;
	}

	@ParameterType("((?:[a-zA-Z0-9-]+)/[a-zA-Z0-9.-]+(?:\\+[a-zA-Z0-9.-]+)?(?:\\s*;\\s*[a-zA-Z0-9-]+=[a-zA-Z0-9\"'._-]+(?:\\s*[^;\\s]*)*)?)")
	public String contentType(final String contentType) {
		return contentType;
	}
}
