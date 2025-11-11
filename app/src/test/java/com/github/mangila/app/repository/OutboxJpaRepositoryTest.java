package com.github.mangila.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.service.OutboxFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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

    @BeforeEach
    void beforeEach() {
        repository.persistAll(
                List.of(ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PENDING, objectMapper),
                        ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.FAILURE, objectMapper),
                        ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PUBLISHED, objectMapper),
                        ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PUBLISHED, objectMapper),
                        ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PUBLISHED, objectMapper),
                        ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PUBLISHED, objectMapper),
                        ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PUBLISHED, objectMapper)
                )
        );
    }

    @Test
    @DisplayName("Should find by Status and AuditMedata deleted")
    void findIdsByStatus() {
        List<OutboxEntity> entities = repository.findAllByStatusAndAuditMetadataDeleted(
                OutboxEventStatus.PUBLISHED,
                false,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(3)
        );
        assertThat(entities)
                .hasSize(3);
        assertThat(entities.getFirst())
                .isNotNull();
        entities = repository.findAllByStatusAndAuditMetadataDeleted(
                OutboxEventStatus.PUBLISHED,
                true,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(3)
        );
        assertThat(entities)
                .isEmpty();
    }

    @Test
    void shouldAudit() {
        OutboxEntity entity = repository.persist(ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PENDING, objectMapper));
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