package com.github.mangila.app.scheduler;

import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
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
class SoftDeleteSuccessTaskExecutionTaskTest {

    @Autowired
    private SoftDeleteSuccessTaskExecutionTask task;

    @Autowired
    private TaskExecutionJpaRepository taskExecutionRepository;

    @Test
    void run(CapturedOutput output) {
        for (int i = 0; i < 55; i++) {
            var entity = ObjectFactoryUtil.createTaskExecutionEntity("test", TaskExecutionStatus.SUCCESS);
            taskExecutionRepository.persist(entity);
        }
        taskExecutionRepository.flush();
        task.run();
        assertThat(output)
                .contains("Soft deleting 50 success task executions");
        task.run();
        assertThat(output)
                .contains("Soft deleting 5 success task executions");
        task.run();
        assertThat(output)
                .contains("No success task executions to soft delete");
        var entities = taskExecutionRepository.findAll(Example.of(new TaskExecutionEntity()));
        assertThat(entities)
                .hasSize(55);
        entities.forEach(entity -> {
            assertThat(entity.getAuditMetadata().isDeleted())
                    .isTrue();
        });
    }
}