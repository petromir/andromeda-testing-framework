package com.petromirdzhunev.cucumber.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.petromirdzhunev.cucumber.spring.context.MockHttpServerProvider;

import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;

/**
 * These steps provide the ability to mock invocations to other HTTP services.
 */
@RequiredArgsConstructor
public class MockHttpServerSteps {

	private final MockHttpServerProvider mockHttpServerProvider;

	@Given("[HTTP] mock server receives {httpMethod} request to {urlPath}")
	public void createRequestWithUrlPath(final String httpMethod, final String urlPath) {
		mockHttpServerProvider.createRequestWithUrlPath(httpMethod, urlPath);
	}

	@Given("[HTTP] mock server receives header {key} = {value}")
	public void setHeaderPair(final String key, final String value) {
		mockHttpServerProvider.setHeaderPair(key, value);
	}

	@Given("[HTTP] mock server receives query parameter {key} = {value}")
	public void setQueryParameter(final String key, final String value) {
		mockHttpServerProvider.setQueryParameter(key, value);
	}

	@Given("[HTTP] mock server receives form url-encoded parameter {key} = {value}")
	public void setUrlEncodedParameter(final String key, final String value) {
		mockHttpServerProvider.setUrlEncodedParameter(key, value);
	}

	@Given("[HTTP] mock server receives body")
	public void setBody(final String body) {
		mockHttpServerProvider.setBody(body);
	}

	@Given("[HTTP] mock server receives body containing")
	public void setNonStrictBody(final String body) {
		mockHttpServerProvider.setNonStrictBody(body);
	}

	@Given("[HTTP] mock server receives form url-encoded request body")
	public void setUrlEncodedBody() {
		mockHttpServerProvider.setUrlEncodedBody();
	}

	@Given("[HTTP] mock server responds with")
	public void setResponse(final JsonNode body) {
		mockHttpServerProvider.setResponse(body);
	}

	@Given("[HTTP] mock server responds with {statusCode} status and body")
	public void setResponse(final Integer statusCode, final JsonNode body) {
		mockHttpServerProvider.setResponse(statusCode, body);
	}
}