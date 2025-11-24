package com.github.mangila.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ReusablePostgresTestContainerConfiguration {

    /**
     * Running with re-usable containers can be good but minor pain to handle the test suites
     * since we always need to remember to current state we are creating for the next test in the suite.
     * Good for local testing to get fast feedback-If the CI/CD server becomes overloaded, it might be smart to migrate for reusable containers
     * <br>
     * or have a mix with both where some tests can be executed with reusable containers and others not.
     * for a simple test setup phase
     */
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withReuse(true);
    }
}
