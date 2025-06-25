package com.petromirdzhunev.cucumber.spring.context;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for HTTP client operations.
 */
public interface HttpClientProvider {

	void init();

	// Configurations
	void createRequestWithUrlPath(final String httpMethod, final String urlPath);

	void createRequestWithUrlPathAndContentType(final String httpMethod, final String urlPath,
			final String contentType);

	void addRequestQueryParameter(final String key, final String value);

	void addRequestEncodedParameter(final String key, final String value);

	void addRequestHeaderPair(final String key, final String value);

	void addRequestBody(final String body);

	void addRequestBody(final JsonNode body);

	// Actions
	void sendRequest();

	// Response access for assertions
	Integer responseStatusCode();

	String responseHeader(final String key);

	String responseBodyAsString();
}