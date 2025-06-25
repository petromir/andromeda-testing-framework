package com.petromirdzhunev.cucumber.types;

import io.cucumber.java.ParameterType;

/**
 * Defines custom {@link ParameterType}s for Cucumber steps related to database operations
 */
public class DatabaseParameterTypes {

	@ParameterType("[a-zA-Z0-9_]+")
	public String tableName(final String tableName) {
		return tableName;
	}
}
