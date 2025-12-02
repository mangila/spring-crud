package com.github.mangila.api.service;

import com.github.mangila.api.model.employee.domain.EmployeeId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EmployeeIdGenerator.class,
        }
)
@AutoConfigureJson
class EmployeeIdGeneratorTest {

    @Autowired
    private EmployeeIdGenerator generator;

    @Test
    void generate() {
        EmployeeId id = generator.generate("John", "Doe");
        assertThat(id.value())
                .isNotNull()
                .startsWith("EMP-JODO-");
    }
}