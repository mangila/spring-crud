package com.github.mangila.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Test the test utility :)
 */
class TestUtilTest {

    @DisplayName("Should read JSON")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "/json/create-new-employee-request.json",
            "/json/employee-dto.json",
            "/json/update-employee-request.json"
    })
    void readJson(String resourcesFilePath) throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString(resourcesFilePath);
        assertThatJson(jsonString)
                .isObject()
                .hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("Should parse JSON")
    void parseJson() {
        var mapper = new ObjectMapper();
        assertThatCode(() -> {
            EmployeeTestFactory.createNewEmployeeRequest(mapper);
            EmployeeTestFactory.createEmployeeDto(mapper);
            EmployeeTestFactory.createUpdateEmployeeRequest(mapper);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should create Objects")
    void createObjects() {
        var mapper = new ObjectMapper();
        assertThatCode(() -> {
            EmployeeTestFactory.createNewEmployeeRequestBuilder(mapper)
                    .build();
            EmployeeTestFactory.createUpdateEmployeeRequestBuilder(mapper)
                    .build();
            EmployeeTestFactory.createEmployeeEntity(mapper);
            EmployeeTestFactory.createEmployeeId();
            EmployeeTestFactory.createEmployee(mapper);
        }).doesNotThrowAnyException();
    }
}
