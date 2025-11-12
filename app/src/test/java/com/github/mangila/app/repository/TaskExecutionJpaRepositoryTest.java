package com.github.mangila.app.repository;

import com.github.mangila.app.TaskExecutionTestFactory;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Import({TestcontainersConfiguration.class,
        JpaConfig.class})
@DataJpaTest
class TaskExecutionJpaRepositoryTest {

    @Autowired
    private TaskExecutionJpaRepository repository;

    @Test
    void test() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void shouldAudit() {
        TaskExecutionEntity entity = repository.persist(TaskExecutionTestFactory.createTaskExecutionEntity("test", TaskExecutionStatus.RUNNING));
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