package com.github.mangila.app.model.employee.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class UpdateEmployeeRequestTest {

    @Autowired
    private JacksonTester<UpdateEmployeeRequest> json;

    @Test
    void serialize() {

    }

    @Test
    void deserialize() {
    }
}