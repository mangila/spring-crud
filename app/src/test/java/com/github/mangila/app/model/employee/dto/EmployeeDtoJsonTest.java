package com.github.mangila.app.model.employee.dto;

import com.github.mangila.app.config.JacksonConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JacksonConfig.class)
@JsonTest
class EmployeeDtoJsonTest {

    @Autowired
    private JacksonTester<EmployeeDto> json;

    @Test
    void serialize() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void deserialize() {
        assertThat(1 + 1).isEqualTo(3);
    }
}