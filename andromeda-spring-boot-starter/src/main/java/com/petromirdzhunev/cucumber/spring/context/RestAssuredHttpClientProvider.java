package com.petromirdzhunev.cucumber.spring.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.spring.ScenarioScope;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * Rest Assured implementation of the HttpClientContext interface.
 */
@Component
@ScenarioScope
@RequiredArgsConstructor
public class RestAssuredHttpClientProvider implements HttpClientProvider {

	private static final String LOCALHOST = "http://localhost";

	@Value("${server.port}")
	private Integer serverPort;
	private RestAssuredConfig restAssuredConfig;
	private Response lastHttpResponse;
	private String method;
	private RequestSpecification requestSpecification;
	// FIXME: Replace with JsonConverter once the library is released.
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	@PostConstruct
	public void init() {
		restAssuredConfig = RestAssured.config()
				.redirect(RedirectConfig.redirectConfig().followRedirects(true))
				.objectMapperConfig(
						ObjectMapperConfig.objectMapperConfig()
						                  .jackson2ObjectMapperFactory((type, s) ->
								                  objectMapper.findAndRegisterModules()))
				.logConfig(LogConfig.logConfig()
				                    .enablePrettyPrinting(true)
				                    .urlEncodeRequestUri(true))
				.httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance());
	}

	@Override
	public void createRequestWithUrlPath(final String httpMethod, final String urlPath) {
		requestSpecification = RestAssured.given()
		                                  .baseUri(LOCALHOST)
		                                  .basePath(urlPath)
		                                  .port(serverPort)
		                                  .urlEncodingEnabled(true)
		                                  .config(restAssuredConfig)
		                                  .redirects()
		                                  .follow(true)
		                                  .contentType(ContentType.JSON)
		                                  .log()
		                                  .all();

		this.method = httpMethod;
	}

	@Override
	public void createRequestWithUrlPathAndContentType(final String httpMethod, final String urlPath,
			final String contentType) {
		requestSpecification = RestAssured.given()
		                                  .baseUri(LOCALHOST)
		                                  .basePath(urlPath)
		                                  .port(serverPort)
		                                  .urlEncodingEnabled(true)
		                                  .config(restAssuredConfig)
		                                  .redirects()
		                                  .follow(true)
		                                  .contentType(ContentType.fromContentType(contentType))
		                                  .log()
		                                  .all();

		this.method = httpMethod;
	}

	@Override
	public void addRequestQueryParameter(final String key, final String value) {
		requestSpecification.queryParam(key, value);
	}

	@Override
	public void addRequestEncodedParameter(final String key, final String value) {
		requestSpecification.contentType(ContentType.URLENC);
		requestSpecification.formParam(key, value);
	}

	@Override
	public void addRequestHeaderPair(final String key, final String value) {
		requestSpecification.header(key, value);
	}

	@Override
	public void addRequestBody(final String body) {
		requestSpecification.body(body);
	}

	@Override
	public void addRequestBody(final JsonNode body) {
		requestSpecification.body(body.toPrettyString());
	}

	@Override
	public void sendRequest() {
		lastHttpResponse = switch (method) {
			case "GET" -> requestSpecification.get().then().log().all().extract().response();
			case "POST" -> requestSpecification.post().then().log().all().extract().response();
			case "PUT" -> requestSpecification.put().then().log().all().extract().response();
			case "DELETE" -> requestSpecification.delete().then().log().all().extract().response();
			default -> throw new IllegalArgumentException("Unsupported HTTP method [name=%s]".formatted(method));
		};
	}

	@Override
	public Integer responseStatusCode() {
		return lastHttpResponse.getStatusCode();
	}

	@Override
	public String responseHeader(final String key) {
		return lastHttpResponse.getHeader(key);
	}

	@Override
	public String responseBodyAsString() {
		return lastHttpResponse.getBody().asString();
	}
}
