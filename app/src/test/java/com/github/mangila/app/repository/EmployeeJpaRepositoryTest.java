package com.github.mangila.app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JpaConfig;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.service.EmployeeIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Import({TestcontainersConfiguration.class,
        ObjectMapper.class,
        JpaConfig.class,
        EmployeeIdGenerator.class})
@DataJpaTest
class EmployeeJpaRepositoryTest {

    @Autowired
    private EmployeeJpaRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeIdGenerator employeeIdGenerator;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void softDeleteEmployee() {
        var id = employeeIdGenerator.generate();
        var e = new EmployeeEntityBuilder()
                .employeeId(id.value())
                .name("Name")
                .salary(new BigDecimal("10000.00"))
                .attributes(objectMapper.createObjectNode())
                .build();
        repository.save(e);
        repository.softDeleteByEmployeeId(id);
        e = repository.findById(id.value()).orElseThrow();
        assertThat(e.getAuditMetadata().getDeleted())
                .isTrue();
    }

    /**
     * Convenience method to create an instance of EmployeeEntity with a Builder.
     * For testing purposes
     */
    public static class EmployeeEntityBuilder {
        private String employeeId;
        private String name;
        private BigDecimal salary;
        private ObjectNode attributes;

        public EmployeeEntityBuilder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public EmployeeEntityBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EmployeeEntityBuilder salary(BigDecimal salary) {
            this.salary = salary;
            return this;
        }

        public EmployeeEntityBuilder attributes(ObjectNode attributes) {
            this.attributes = attributes;
            return this;
        }

        public EmployeeEntity build() {
            var e = new EmployeeEntity();
            e.setId(employeeId);
            e.setName(name);
            e.setSalary(salary);
            e.setAttributes(attributes);
            return e;
        }
    }
}