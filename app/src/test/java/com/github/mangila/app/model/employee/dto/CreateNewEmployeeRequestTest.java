package com.github.mangila.app.model.employee.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class CreateNewEmployeeRequestTest {

    @Autowired
    private JacksonTester<CreateNewEmployeeRequest> json;

    @Test
    void serialize() {

    }

    @Test
    void deserialize() {
    }
}