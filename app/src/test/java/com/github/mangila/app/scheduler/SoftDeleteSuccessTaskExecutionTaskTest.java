package com.github.mangila.app.scheduler;

import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
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
class SoftDeleteSuccessTaskExecutionTaskTest {

    @Autowired
    private SoftDeleteSuccessTaskExecutionTask task;

    @Autowired
    private TaskExecutionJpaRepository taskExecutionRepository;

    @Test
    void run() {
        for (int i = 0; i < 55; i++) {
            var entity = ObjectFactoryUtil.createTaskExecutionEntity("test", TaskExecutionStatus.SUCCESS);
            taskExecutionRepository.persist(entity);
        }
        taskExecutionRepository.flush();
        var node = task.call();
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
        node = task.call();
        assertThatJson(node.toString())
                .isObject()
                .containsEntry("message", "Soft deleting 5 success task executions")
                .hasSize(2)
                .node("ids")
                .isArray()
                .hasSize(5);
        node = task.call();
        assertThatJson(node.toString())
                .isObject()
                .containsEntry("message", "No success task executions to soft delete")
                .hasSize(1);
        var entities = taskExecutionRepository.findAll(Example.of(new TaskExecutionEntity()));
        assertThat(entities)
                .hasSize(55);
        entities.forEach(entity -> {
            assertThat(entity.getAuditMetadata().isDeleted())
                    .isTrue();
        });
    }
}