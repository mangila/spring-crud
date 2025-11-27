package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.TaskExecutionTestFactory;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.model.task.TaskExecutionEntity;
import com.github.mangila.api.model.task.TaskExecutionStatus;
import com.github.mangila.api.repository.TaskExecutionJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@Import(PostgresTestContainerConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false",
                "application.notification.enabled=false"
        }
)
@Slf4j
class SoftDeleteSuccessTaskExecutionTaskTest {

    @Autowired
    private SoftDeleteSuccessTaskExecutionTask task;

    @Autowired
    private TaskExecutionJpaRepository taskExecutionRepository;

    @BeforeEach
    void beforeEach() {
        for (int i = 0; i < 55; i++) {
            TaskExecutionEntity entity = TaskExecutionTestFactory.createTaskExecutionEntity("test", TaskExecutionStatus.SUCCESS);
            taskExecutionRepository.persist(entity);
        }
        for (int i = 0; i < 5; i++) {
            TaskExecutionEntity entity1 = TaskExecutionTestFactory.createTaskExecutionEntity("test", TaskExecutionStatus.RUNNING);
            TaskExecutionEntity entity2 = TaskExecutionTestFactory.createTaskExecutionEntity("test", TaskExecutionStatus.FAILURE);
            TaskExecutionEntity entity3 = TaskExecutionTestFactory.createTaskExecutionEntity("test", TaskExecutionStatus.CANCELLED);
            taskExecutionRepository.persistAll(
                    List.of(entity1, entity2, entity3)
            );
        }
        taskExecutionRepository.flush();
    }

    @Test
    void call() {
        assertFirstRun();
        assertSecondRun();
        assertEmptyRun();
        List<TaskExecutionEntity> entities = taskExecutionRepository.findAllByStatusAndAuditMetadataDeleted(
                TaskExecutionStatus.SUCCESS,
                true,
                Sort.unsorted(),
                Limit.unlimited()
        );
        assertThat(entities)
                .hasSize(55);
        entities.forEach(entity -> {
            assertThat(entity.getAuditMetadata().deleted())
                    .isTrue();
        });
    }

    private void assertFirstRun() {
        log.info("Assert first run start");
        ObjectNode node = task.call();
        assertThatJson(node.toString())
                .isObject()
                .containsOnlyKeys(
                        "message",
                        "ids"
                )
                .containsEntry("message", "Soft deleting 50 success task executions")
                .hasSize(2)
                .node("ids")
                .isArray()
                .hasSize(50);
        log.info("Assert first run end");
    }

    private void assertSecondRun() {
        log.info("Assert second run start");
        ObjectNode node = task.call();
        assertThatJson(node.toString())
                .isObject()
                .containsEntry("message", "Soft deleting 5 success task executions")
                .hasSize(2)
                .node("ids")
                .isArray()
                .hasSize(5);
        log.info("Assert second run end");
    }

    private void assertEmptyRun() {
        log.info("Assert empty run start");
        ObjectNode node = task.call();
        assertThatJson(node.toString())
                .isObject()
                .containsOnlyKeys("message")
                .containsEntry("message", "No success task executions to soft delete")
                .hasSize(1);
        log.info("Assert empty run end");
    }
}