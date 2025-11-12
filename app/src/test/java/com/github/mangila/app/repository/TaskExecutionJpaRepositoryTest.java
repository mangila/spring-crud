package com.github.mangila.app.repository;

import com.github.mangila.app.ClockTestConfig;
import com.github.mangila.app.TaskExecutionTestFactory;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JacksonConfig;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
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
                ClockTestConfig.class,
                JpaConfig.class
        }
)
@AutoConfigureJson
@DataJpaTest
class TaskExecutionJpaRepositoryTest {

    @Autowired
    private TaskExecutionJpaRepository repository;

    @Autowired
    private ClockTestConfig.TestClock clock;

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