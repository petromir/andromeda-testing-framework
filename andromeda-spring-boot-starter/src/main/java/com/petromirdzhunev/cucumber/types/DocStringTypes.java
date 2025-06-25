package com.petromirdzhunev.cucumber.types;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petromirdzhunev.cucumber.exception.DocStringTypeException;

import io.cucumber.java.DocStringType;
import lombok.RequiredArgsConstructor;

/**
 * Defines custom {@link DocStringType}s for Cucumber steps
 */
@RequiredArgsConstructor
public class DocStringTypes {
	private final ObjectMapper objectMapper;

	@DocStringType(contentType = "json")
	public JsonNode json(final String jsonContent) {
		try {
			return objectMapper.readTree(jsonContent);
		} catch (JsonProcessingException e) {
			throw new DocStringTypeException("Failed to parse JSON [content=%s]".formatted(jsonContent));
		}
	}
}
