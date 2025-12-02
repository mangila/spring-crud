package com.github.mangila.api;

import org.springframework.boot.SpringApplication;

/**
 * Testing is good for feedback loops, and when a change is commited it's good to have a well-defined test suite.
 * Have a test for every component, and when a change is coming, the component test will give fast feedback.
 * <br>
 * The more complexity, the bigger the project, the more you have to rely on the test suite
 * <br>
 * If all expected behavior can be translated into test source code, the better.
 * The test code suite can also tell a story like the source code.
 * But of course the thin line is it application code? Library code?
 * Since this is a REST API, wouldn't just a Controller Integration Test be enough?
 * And the system can change how much it wants?
 * And its only job is to not break the REST contract?
 * <br>
 * Testing is "the project within the project"
 */
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(PostgresTestContainerConfiguration.class).run(args);
    }

}
