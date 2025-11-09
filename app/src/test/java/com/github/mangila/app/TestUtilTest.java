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
 * Responsible for testing the {@link FilePathUtil} {@link ObjectFactoryUtil} classes.
 * <br>
 * Test the Testutil :P...
 */
class TestUtilTest {

    @DisplayName("Should read JSON")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "/json/create-new-employee-request.json",
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
            ObjectFactoryUtil.createNewEmployeeRequest(mapper);
            ObjectFactoryUtil.createUpdateEmployeeRequest(mapper);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should create Objects")
    void createObjects() {
        assertThatCode(() -> {
            ObjectFactoryUtil.createNewEmployeeRequestBuilder(new ObjectMapper())
                    .build();
            ObjectFactoryUtil.createEmployeeEntity(new ObjectMapper());
            ObjectFactoryUtil.createEmployeeId();
        }).doesNotThrowAnyException();
    }
}
