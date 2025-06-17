package com.petromirdzhunev;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:/features",
                 glue = {"com.petromirdzhunev.cucumber"},
                 plugin = {"pretty", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"})
public class CucumberRunnerTest {
}
