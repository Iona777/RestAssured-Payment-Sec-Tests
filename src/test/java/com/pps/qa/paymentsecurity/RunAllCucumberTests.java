package com.pps.qa.paymentsecurity;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.pps.qa.paymentsecurity"},
        tags = "not @IGNORED",
        plugin = {
                "pretty",
                "html:target/site/PaymentSecurityAllTests.html",
                "json:target/cucumber/PaymentSecurityAllTests.json"
        },
        monochrome = true,
        snippets = SnippetType.CAMELCASE
)
public class RunAllCucumberTests {

}
