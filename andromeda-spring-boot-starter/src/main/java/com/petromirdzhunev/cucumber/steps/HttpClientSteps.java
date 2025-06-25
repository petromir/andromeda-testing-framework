package com.petromirdzhunev.cucumber.steps;

import org.assertj.core.api.Assertions;

import com.fasterxml.jackson.databind.JsonNode;
import com.petromirdzhunev.cucumber.spring.context.HttpClientProvider;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;

/**
 * Definition of all HTTP client steps.
 */
@RequiredArgsConstructor
public class HttpClientSteps {

	private final HttpClientProvider httpClientProvider;

	// Configurations
	@Given("[HTTP] client creates {httpMethod} request with URL path {urlPath}")
	public void createRequestWithUrlPath(final String httpMethod, final String urlPath) {
		httpClientProvider.createRequestWithUrlPath(httpMethod, urlPath);
	}

	@Given("[HTTP] client creates {httpMethod} request with URL path {urlPath} and content type {contentType}")
	public void createRequestWithUrlPathAndContentType(final String httpMethod, final String urlPath,
			final String contentType) {
		httpClientProvider.createRequestWithUrlPathAndContentType(httpMethod, urlPath, contentType);
	}

	@Given("[HTTP] client adds query string parameter {key} = {value}")
	public void addQueryParameter(final String key, final String value) {
		httpClientProvider.addRequestQueryParameter(key, value);
	}

	@Given("[HTTP] client adds form url-encoded parameter {key} = {value}")
	public void addUrlEncodedParameter(final String key, final String value) {
		httpClientProvider.addRequestEncodedParameter(key, value);
	}

	@Given("[HTTP] client adds header {key} = {value}")
	public void addHeaderPair(final String key, final String value) {
		httpClientProvider.addRequestHeaderPair(key, value);
	}

	@Given("[HTTP] client adds text body")
	public void addBody(final String body) {
		httpClientProvider.addRequestBody(body);
	}

	@Given("[HTTP] client adds JSON body")
	public void addBody(final JsonNode body) {
		httpClientProvider.addRequestBody(body);
	}

	@When("[HTTP] client sends the request")
	public void sendRequest() {
		httpClientProvider.sendRequest();
	}

	// Assertions
	@Then("[HTTP] client response status code must be {statusCode}")
	public void assertResponseStatusCode(final Integer expectedStatusCode) {
		Assertions.assertThat(httpClientProvider.responseStatusCode())
				.as("HTTP client response status code is different from the expected status code")
				.isEqualTo(expectedStatusCode);
	}

	@Then("[HTTP] client response header {key} must be {value}")
	public void assertResponseHeader(final String key, final String value) {
		Assertions.assertThat(httpClientProvider.responseHeader(key))
		          .as("HTTP client response header doesn't exist").isNotNull()
		          .as("HTTP client response header is different from the expected header").isEqualTo(value);
	}

	@Then("[HTTP] client response text body must be")
	public void assertResponseBody(final String expectedBody) {
		Assertions.assertThat(httpClientProvider.responseBodyAsString())
				.as("HTTP client response text body is different from the expected body")
				.isEqualTo(expectedBody);
	}

	@Then("[HTTP] client response JSON body must be")
	public void assertResponseBody(final JsonNode expectedBody) {
		JsonAssertions.assertThatJson(httpClientProvider.responseBodyAsString())
				.as("HTTP client response JSON body is different from the expected body")
				.isEqualTo(expectedBody);
	}

	@Then("[HTTP] client response JSON non-strict body must be")
	public void assertOrderedResponseBody(final JsonNode expectedBody) {
		JsonAssertions.assertThatJson(httpClientProvider.responseBodyAsString())
		              .when(Option.IGNORING_ARRAY_ORDER)
		              .as("HTTP client response JSON non-strict body is different from the expected body")
		              .isEqualTo(expectedBody);
	}

	@Then("[HTTP] client response body must contain")
	public void assertResponseBodyContains(final JsonNode expectedBody) {
		JsonAssertions.assertThatJson(httpClientProvider.responseBodyAsString())
				.when(Option.IGNORING_EXTRA_FIELDS, Option.IGNORING_ARRAY_ORDER, Option.IGNORING_EXTRA_ARRAY_ITEMS)
				.withTolerance(0)
				.as("HTTP client response body is different from the expected body")
				.isEqualTo(expectedBody.toString());
	}

	@Then("[HTTP] client response body must be empty")
	public void assertResponseBodyIsEmpty() {
		Assertions.assertThat(httpClientProvider.responseBodyAsString())
		          .as("HTTP client response body is not empty")
		          .isNullOrEmpty();
	}
}
