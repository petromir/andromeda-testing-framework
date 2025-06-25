package com.petromirdzhunev.cucumber.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertValuesStepN;
import org.jooq.Query;
import org.jooq.Results;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;

/**
 * Definition of all Database steps.
 */
@RequiredArgsConstructor
public class DatabaseSteps {

	private static final String DATABASE_TABLE_DATA_SEPARATOR = "|";

	private final DSLContext db;
	// FIXME: Replace with JsonConverter once the library is released.
	private final ObjectMapper objectMapper;

	@Given("[DB] table {tableName} has rows")
	public void populateTableWithRecords(final String tableName, final DataTable dataTable) {
		List<List<String>> rows = new ArrayList<>(dataTable.asLists());
		List<String> columnNames = rows.getFirst();

		Table<?> table = DSL.table(DSL.name(tableName));
		List<Query> insertQueries = new ArrayList<>(rows.size() - 1);
		// Start from the second element as the first one contains the column name
		ListIterator<List<String>> rowsIterator = rows.listIterator(1);
		while (rowsIterator.hasNext()) {
			InsertValuesStepN<?> insertQuery = db.insertInto(table,
					columnNames.stream().map(columnName -> DSL.field(DSL.name(columnName)))
							.toArray(Field[]::new))
					.values(rowsIterator.next());
			insertQueries.add(insertQuery);
		}

		db.batch(insertQueries).execute();
	}

	@Then("[DB] table {tableName} must have rows")
	public void checkTableContent(final String tableName, final DataTable expected) {
		List<List<String>> rows = new ArrayList<>(expected.asLists());
		List<String> columns = rows.getFirst();

		// Extract the expected rows data
		final List<Map<String, String>> expectedRowsData = expectedRowData(rows, columns);

		// Extract the actual rows data
		String requiredDataSql = "SELECT %s FROM %s".formatted(String.join(",", columns.toArray(new String[0])),
				tableName);
		final List<Map<String, String>> actualRowsData = actualRowsData(db.fetchMany(requiredDataSql));

		Assertions.assertThat(actualRowsData)
		          .withFailMessage(() -> """
				          Db rows number differs [actualRows=%d, expectedRows=%d]%s%s
				          """.formatted(actualRowsData.size(), expectedRowsData.size(),
				          formattedRorws(columns, actualRowsData, "Actual rows"),
				          formattedRorws(columns, expectedRowsData, "Expected rows")))
		          .hasSameSizeAs(expectedRowsData);

		JsonAssertions.assertThatJson(mapToJson(actualRowsData))
		              .when(Option.IGNORING_ARRAY_ORDER)
		              .withFailMessage(() -> "Db actual and expected [DB] data differs %s%s"
						              .formatted(formattedRorws(columns, actualRowsData, "Actual rows"),
								              formattedRorws(columns, expectedRowsData, "Expected rows")))
		              .isEqualTo(mapToJson(expectedRowsData));
	}

	@Then("[DB] table {tableName} must be empty")
	public void checkTableIsEmpty(final String tableName) {
		final boolean tableHasRecords = db.fetchExists(DSL.selectFrom(tableName));
		Assertions.assertThat(tableHasRecords)
		          .as("Db table is not empty [tableName=%s]", tableName)
		          .isFalse();
	}

	private List<Map<String, String>> actualRowsData(final Results actualDataResults) {
		return actualDataResults
				.stream()
				.flatMap(Collection::stream)
				.map(row -> {
					// Using LinkedHashMap ensures that order is kept
					Map<String, String> dbRecords =
							row.intoMap()
							   .entrySet()
					           .stream()
					           .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(),
									           stringValue(entry.getValue())), LinkedHashMap::putAll);
					return dbRecords;
				})
				.toList();
	}

	private List<Map<String, String>> expectedRowData(final List<List<String>> rows,
			final List<String> columns) {
		ListIterator<List<String>> rowsIterator = rows.listIterator(1);
		// The first element contains the column metadata, so we need to start from the second one.
		List<Map<String, String>> expectedRowsData = new ArrayList<>(rows.size() - 1);

		while (rowsIterator.hasNext()) {
			final List<String> nextRow = rowsIterator.next();
			// LinkedHashMap guarantees the order is preserved.
			Map<String, String> expectedRowData = new LinkedHashMap<>(nextRow.size());
			for (int i = 0; i < nextRow.size(); i++) {
				final String columnName = columns.get(i);
				String expectedValue = nextRow.get(i);
				if ("null".equals(expectedValue)) {
					expectedValue = null;
				}
				expectedRowData.put(columnName, expectedValue);
			}
			expectedRowsData.add(expectedRowData);
		}
		return expectedRowsData;
	}

	private String mapToJson(final List<Map<String, String>> rowsData) {
		return rowsData.stream()
		               .map(Map::entrySet)
		               .map(entrySet -> {
						   final String entries = entrySet
								   .stream()
								   .map(entry -> {
									   final String prettifiedValue = prettifiedValue(entry.getValue());
									   if (entry.getValue() == null || prettifiedValue.equals(entry.getValue())) {
										   return "\"%s\" : \"%s\"".formatted(entry.getKey(), prettifiedValue);
									   } else {
										   return "\"%s\" : %s".formatted(entry.getKey(), prettifiedValue);
									   }
								   })
								   .collect(Collectors.joining("," + System.lineSeparator()));
			               return "{\n" + entries + "\n}";

		               })
		          .collect(Collectors.joining("," + System.lineSeparator(), "[", "]"));
	}

	private String prettifiedValue(final String value) {
		if (value != null) {
			try {
				JsonNode jsonNode = objectMapper.readTree(value);
				if (jsonNode instanceof ObjectNode || jsonNode instanceof ArrayNode) {
					return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

				}
			} catch (Exception exception) {
				// Do nothing
			}
		}
		return value;
	}

	private String stringValue(final Object value) {
		String stringValue = null;
		if (value != null) {
			// Check if the class is a byte array
			if (value.getClass().isArray()) {
				stringValue = new String((byte[]) value);
			} else if (value.getClass().getName().equals("[B")) {
				stringValue = Arrays.toString((Object[]) value);
			} else {
				stringValue = String.valueOf(value);
			}
		}
		return stringValue;
	}

	private String formattedRorws(final List<String> columns, final List<Map<String, String>> rowsData,
			final String rowType) {
		final StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append(System.lineSeparator());
		messageBuilder.append(rowType);
		messageBuilder.append(System.lineSeparator());
		messageBuilder.append(columns.stream()
		                             .collect(Collectors.joining(DATABASE_TABLE_DATA_SEPARATOR,
				                             DATABASE_TABLE_DATA_SEPARATOR, DATABASE_TABLE_DATA_SEPARATOR)));
		messageBuilder.append(System.lineSeparator());
		rowsData.forEach(databaseRow -> {
			messageBuilder.append(databaseRow.values()
			                                 .stream()
			                                 .collect(Collectors.joining(DATABASE_TABLE_DATA_SEPARATOR,
					                                 DATABASE_TABLE_DATA_SEPARATOR, DATABASE_TABLE_DATA_SEPARATOR)));
			messageBuilder.append(System.lineSeparator());
		});
		return messageBuilder.toString();
	}
}
