package com.github.mangila.app.model.employee.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

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