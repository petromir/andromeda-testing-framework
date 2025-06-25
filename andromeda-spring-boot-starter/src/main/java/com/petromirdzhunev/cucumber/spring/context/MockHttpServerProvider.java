package com.petromirdzhunev.cucumber.spring.context;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for HTTP server mock operations.
 */
public interface MockHttpServerProvider {

	void createRequestWithUrlPath(final String httpMethod, final String urlPath);

	void createRequestWithUrlPathAndContentType(final String httpMethod, final String urlPath,
			final String contentType);

	void setHeaderPair(final String key, final String value);

	void setQueryParameter(final String key, final String value);

	void setUrlEncodedParameter(final String key, final String value);

	void setBody(final String body);

	void setNonStrictBody(final String body);

	void setUrlEncodedBody();

	void setResponse(final JsonNode body);

	void setResponse(final Integer statusCode, final JsonNode body);
}
