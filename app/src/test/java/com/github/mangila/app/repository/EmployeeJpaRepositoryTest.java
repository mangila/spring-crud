package com.github.mangila.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JacksonConfig;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.service.EmployeeEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Sliced Spring Context for testing the EmployeeRepository.
 * <br>
 * {@link DataJpaTest} is great for testing custom queries, repositories or entity listeners,
 * JPA lifecycle stuffs or any other JPA magic dependency.
 */
@Import({TestcontainersConfiguration.class,
        JpaConfig.class,
        JacksonConfig.class,
        ObjectMapper.class})
@DataJpaTest
class EmployeeJpaRepositoryTest {

    @Autowired
    private EmployeeJpaRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should Soft Delete an Employee")
    void softDeleteEmployee() throws IOException {
        var entity = ObjectFactoryUtil.createEmployeeEntity(objectMapper);
        repository.persist(entity);
        repository.softDeleteByEmployeeId(new EmployeeId(entity.getId()));
        entity = repository.findById(entity.getId())
                .orElseThrow();
        assertThat(entity.getAuditMetadata().isDeleted())
                .isTrue();
    }

    @Test
    void shouldAudit() throws IOException {
        var entity = repository.persist(ObjectFactoryUtil.createEmployeeEntity(objectMapper));
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