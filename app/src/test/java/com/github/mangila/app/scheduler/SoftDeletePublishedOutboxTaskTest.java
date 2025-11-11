package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.repository.OutboxJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
class SoftDeletePublishedOutboxTaskTest {

    @Autowired
    private SoftDeletePublishedOutboxTask task;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OutboxJpaRepository outboxRepository;

    @Test
    void run() {
        for (int i = 0; i < 55; i++) {
            var entity = ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PUBLISHED, objectMapper);
            outboxRepository.persist(entity);
        }
        outboxRepository.flush();
        var node = task.call();
        assertThatJson(node.toString())
                .isObject()
                .containsOnlyKeys(
                        "message",
                        "ids"
                )
                .containsEntry("message", "Soft deleting 50 published outbox events")
                .hasSize(2)
                .node("ids")
                .isArray()
                .hasSize(50);
        node = task.call();
        assertThatJson(node.toString())
                .isObject()
                .containsEntry("message", "Soft deleting 5 published outbox events")
                .hasSize(2)
                .node("ids")
                .isArray()
                .hasSize(5);
        node = task.call();
        assertThatJson(node.toString())
                .isObject()
                .containsEntry("message", "No published outbox events to soft delete")
                .hasSize(1);
        var entities = outboxRepository.findAll(Example.of(new OutboxEntity()));
        assertThat(entities)
                .hasSize(55);
        entities.forEach(entity -> {
            assertThat(entity.getAuditMetadata().isDeleted())
                    .isTrue();
        });
    }
}