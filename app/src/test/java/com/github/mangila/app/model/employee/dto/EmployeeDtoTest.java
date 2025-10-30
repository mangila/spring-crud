package com.github.mangila.app.model.employee.dto;

import com.github.mangila.app.config.JacksonConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;

@Import(JacksonConfig.class)
@JsonTest
class EmployeeDtoTest {

    @Autowired
    private JacksonTester<EmployeeDto> json;

    @Test
    void serialize() {

    }

    @Test
    void deserialize() {
    }
}