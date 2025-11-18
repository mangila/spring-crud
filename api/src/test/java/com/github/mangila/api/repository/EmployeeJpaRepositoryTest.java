package com.github.mangila.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.ClockTestConfig;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.TestcontainersConfiguration;
import com.github.mangila.api.config.JacksonConfig;
import com.github.mangila.api.config.JpaConfig;
import com.github.mangila.api.model.AuditMetadata;
import com.github.mangila.api.model.employee.entity.EmployeeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Sliced Spring Context for testing the EmployeeRepository.
 * <br>
 * {@link DataJpaTest} is great for testing custom queries, repositories or entity listeners,
 * JPA lifecycle stuffs or any other JPA magic dependency.
 */
@Import(
        {
                TestcontainersConfiguration.class,
                JacksonConfig.class,
                ClockTestConfig.class,
                JpaConfig.class
        }
)
@AutoConfigureJson
@DataJpaTest
class EmployeeJpaRepositoryTest {

    @Autowired
    private EmployeeJpaRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    ClockTestConfig.TestClock clock;

    @Test
    @DisplayName("Should audit EmployeeEntity")
    void shouldAudit() throws IOException {
        EmployeeEntity entity = repository.persist(
                EmployeeTestFactory.createEmployeeEntity(objectMapper, AuditMetadata.EMPTY)
        );
        var auditMetadata = entity.getAuditMetadata();
        assertThat(auditMetadata)
                .isNotNull()
                .hasOnlyFields(
                        "created",
                        "modified",
                        "deleted"
                )
                .hasFieldOrPropertyWithValue("deleted", false);
        assertThat(auditMetadata.getCreated())
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(1)));
        assertThat(auditMetadata.getModified())
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(1)));
        auditMetadata.setDeleted(true);
        // Advance in time two days
        clock.advanceTime(Duration.ofDays(2));
        entity = repository.merge(entity);
        // we need to flush here to get the new Audit values (JPA lifecycle stuffs), we need to take a trip to the DB
        repository.flush();
        auditMetadata = entity.getAuditMetadata();
        // Check that created should be unchanged
        assertThat(auditMetadata.getCreated())
                .isCloseTo(
                        Instant.now(),
                        within(Duration.ofSeconds(1)));
        // Check that modified should be updated with the new Clock time
        assertThat(auditMetadata.getModified())
                .isCloseTo(Instant.now().plus(Duration.ofDays(2)),
                        within(Duration.ofSeconds(1)));
        assertThat(auditMetadata.isDeleted())
                .isTrue();
    }
}