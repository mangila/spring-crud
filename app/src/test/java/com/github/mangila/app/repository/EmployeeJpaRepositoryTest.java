package com.github.mangila.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JacksonConfig;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.service.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sliced Spring Context for testing the EmployeeRepository.
 * <br>
 * {@link DataJpaTest} is great for testing custom queries, repositories or entity listeners,
 * JPA lifecycle stuffs or any other JPA magic dependency.
 */
@Import({TestcontainersConfiguration.class,
        JpaConfig.class,
        JacksonConfig.class,
        ObjectMapper.class,
        EmployeeMapper.class})
@DataJpaTest
class EmployeeJpaRepositoryTest {

    @Autowired
    private EmployeeJpaRepository repository;

    @Autowired
    private EmployeeMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldAudit() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void softDeleteEmployee() throws IOException {
        var updateRequest = ObjectFactoryUtil.createUpdateEmployeeRequest(objectMapper);
        Employee employee = mapper.toDomain(updateRequest);
        repository.save(mapper.toEntity(employee));
        repository.softDeleteByEmployeeId(employee.id());
        EmployeeEntity entity = repository.findById(employee.id().value())
                .orElseThrow();
        assertThat(entity.getAuditMetadata().deleted())
                .isTrue();
    }
}