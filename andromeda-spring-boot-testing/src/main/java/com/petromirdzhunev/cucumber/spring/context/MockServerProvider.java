package com.petromirdzhunev.cucumber.spring.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.petromirdzhunev.cucumber.spring.beans.MockHttpServerWrapper;

import io.cucumber.spring.ScenarioScope;
import lombok.RequiredArgsConstructor;

/**
 * Context used to store HTTP server-related data within the scenario scope.
 * Implementation of HttpServerContext for mocking HTTP services.
 */
@Component
@ScenarioScope
@RequiredArgsConstructor
public class MockServerProvider implements MockHttpServerProvider {
	private final Map<String, String> urlEncodedParameters = new HashMap<>();
	private final MockHttpServerWrapper httpServer;
	private HttpRequest httpRequest;

	@Override
	public void createRequestWithUrlPath(final String httpMethod, final String urlPath) {
		createRequestWithUrlPathAndContentType(httpMethod, urlPath, MediaType.APPLICATION_JSON_UTF_8.toString());
	}

	@Override
	public void createRequestWithUrlPathAndContentType(final String httpMethod, final String urlPath,
			final String contentType) {
		httpRequest = HttpRequest.request()
		                         .withMethod(httpMethod)
		                         .withPath(urlPath)
		                         .withContentType(MediaType.parse(contentType));
	}


	@Override
	public void setHeaderPair(final String key, final String value) {
		httpRequest.withHeader(key, value);
	}

	@Override
	public void setQueryParameter(final String key, final String value) {
		httpRequest.withQueryStringParameter(key, value);
	}

	@Override
	public void setUrlEncodedParameter(final String key, final String value) {
		urlEncodedParameters.put(key, value);
	}

	@Override
	public void setBody(final String body) {
		httpRequest.withBody(body);
	}

	@Override
	public void setNonStrictBody(final String body) {
		httpRequest.withBody(new JsonBody(body));
	}

	@Override
	public void setUrlEncodedBody() {
		httpRequest.withContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpRequest.withBody(urlEncodedBody());
	}

	@Override
	public void setResponse(final JsonNode body) {
		httpServer.delegate()
		          .when(httpRequest)
		          .respond(HttpResponse.response(body.toPrettyString())
		                               .withContentType(MediaType.APPLICATION_JSON_UTF_8));
	}

	@Override
	public void setResponse(final Integer statusCode, final JsonNode body) {
		httpServer.delegate()
		          .when(httpRequest)
		          .respond(HttpResponse.response(body.toPrettyString())
		                               .withStatusCode(statusCode)
		                               .withContentType(MediaType.APPLICATION_JSON_UTF_8));
	}

	public String urlEncodedBody() {
		final List<String> keyValuePairs = urlEncodedParameters
				.entrySet()
				.stream()
		        .map(entry -> "%s=%s".formatted(entry.getKey(), entry.getValue()))
		        .toList();

		return String.join("&", keyValuePairs);
	}
}
