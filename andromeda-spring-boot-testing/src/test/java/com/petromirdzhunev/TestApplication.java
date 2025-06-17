package com.petromirdzhunev;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TestApplication {

	public static void main(final String[] args) {
		new SpringApplicationBuilder(TestApplication.class).run(args);
	}
}