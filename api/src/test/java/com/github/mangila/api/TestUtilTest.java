package com.github.mangila.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.config.JacksonConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Test the test utility :)
 */
@Import(JacksonConfig.class)
@JsonTest
public class TestUtilTest {

    @Autowired
    private ObjectMapper mapper;

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
        assertThatCode(() -> {
            EmployeeTestFactory.createNewEmployeeRequest(mapper);
            EmployeeTestFactory.createEmployeeDto(mapper);
            EmployeeTestFactory.createUpdateEmployeeRequest(mapper);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should create Objects")
    void createObjects() {
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
