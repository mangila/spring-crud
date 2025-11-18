package com.github.mangila.api.config;

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Clock;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableJpaRepositories(
        value = "com.github.mangila.api.repository",
        repositoryBaseClass = BaseJpaRepositoryImpl.class
)
public class JpaConfig {

    /**
     * The JPA AuditingHandler uses the Clock bean provisioned
     * <br>
     * Audit in UTC, the default dateTimeProvider provided by Spring is in the local date time zone.
     */
    @Bean
    DateTimeProvider dateTimeProvider(Clock clock) {
        return () -> Optional.of(clock.instant());
    }
}
