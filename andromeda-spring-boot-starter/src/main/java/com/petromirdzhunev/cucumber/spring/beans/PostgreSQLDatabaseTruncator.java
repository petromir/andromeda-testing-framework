package com.petromirdzhunev.cucumber.spring.beans;

import java.util.Collection;

import org.jooq.DSLContext;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * A Spring component responsible to truncate all the tables except the one created by the migration tools (e.g.,
 * Flyway, Liquibase, etc.)
 */
@Component
@RequiredArgsConstructor
@DependsOnDatabaseInitialization
public class PostgreSQLDatabaseTruncator {

	private final DSLContext db;

	public void truncate() {
		String[] deleteTableSqlStatements = db
				.fetchMany("""
					SELECT table_name FROM information_schema.tables WHERE 
					table_schema = 'public' AND
					table_name != 'databasechangelog' AND 
					table_name != 'databasechangeloglock'AND
					table_type = 'BASE TABLE'
					""")
				.stream()
				.flatMap(Collection::stream)
				.map(result -> "DELETE FROM %s".formatted(result.get(0)))
				.toArray(String[]::new);

		db.batch(deleteTableSqlStatements).execute();
	}
}
