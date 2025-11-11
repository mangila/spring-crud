package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.repository.OutboxJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
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
    void run(CapturedOutput output) {
        for (int i = 0; i < 55; i++) {
            var entity = ObjectFactoryUtil.createOutboxEntity(OutboxEventStatus.PUBLISHED, objectMapper);
            outboxRepository.persist(entity);
        }
        outboxRepository.flush();
        task.run();
        assertThat(output)
                .contains("Soft deleting 50 published outbox events");
        task.run();
        assertThat(output)
                .contains("Soft deleting 5 published outbox events");
        task.run();
        assertThat(output)
                .contains("No published outbox events to soft delete");
        var entities = outboxRepository.findAll(Example.of(new OutboxEntity()));
        assertThat(entities)
                .hasSize(55);
        entities.forEach(entity -> {
            assertThat(entity.getAuditMetadata().isDeleted())
                    .isTrue();
        });
    }
}