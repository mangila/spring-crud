package com.github.mangila.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.OutboxTestFactory;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Import({TestcontainersConfiguration.class,
        ObjectMapper.class,
        JpaConfig.class})
@DataJpaTest
class OutboxJpaRepositoryTest {

    @Autowired
    private OutboxJpaRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

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
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        assertThat(auditMetadata.getModified())
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
    }
}