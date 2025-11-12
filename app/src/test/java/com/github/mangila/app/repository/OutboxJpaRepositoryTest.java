package com.github.mangila.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ClockTestConfig;
import com.github.mangila.app.OutboxTestFactory;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JacksonConfig;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Import(
        {
                TestcontainersConfiguration.class,
                JacksonConfig.class,
                JpaConfig.class
        }
)
@AutoConfigureJson
@DataJpaTest
class OutboxJpaRepositoryTest {

    @Autowired
    private OutboxJpaRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClockTestConfig.TestClock clock;

    @Test
    @DisplayName("Should change status")
    void changeStatus() {
        OutboxEntity entity = repository.persist(OutboxTestFactory.createOutboxEntity(OutboxEventStatus.PENDING, objectMapper));
        repository.changeStatus(OutboxEventStatus.FAILURE, entity.getAggregateId());
        entity = repository.findById(entity.getId()).orElseThrow();
        assertThat(entity.getStatus())
                .isEqualTo(OutboxEventStatus.FAILURE);
    }

    @Test
    @DisplayName("Should audit")
    void shouldAudit() {
        OutboxEntity entity = repository.persist(OutboxTestFactory.createOutboxEntity(OutboxEventStatus.PENDING, objectMapper));
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