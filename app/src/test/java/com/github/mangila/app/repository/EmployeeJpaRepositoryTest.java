package com.github.mangila.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JacksonConfig;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.service.EmployeeDomainMapper;
import com.github.mangila.app.service.EmployeeEntityMapper;
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
        EmployeeEntityMapper.class})
@DataJpaTest
class EmployeeJpaRepositoryTest {

    @Autowired
    private EmployeeJpaRepository repository;

    @Autowired
    private EmployeeDomainMapper domainMapper;
    @Autowired
    private EmployeeEntityMapper entityMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldAudit() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void softDeleteEmployee() throws IOException {
        var dto = ObjectFactoryUtil.createUpdateEmployeeRequest(objectMapper);
        var domain = domainMapper.map(dto);
        var entity = entityMapper.map(domain);
        repository.save(entity);
        repository.softDeleteByEmployeeId(domain.id());
        entity = repository.findById(domain.id().value())
                .orElseThrow();
        assertThat(entity.getAuditMetadata().deleted())
                .isTrue();
    }
}