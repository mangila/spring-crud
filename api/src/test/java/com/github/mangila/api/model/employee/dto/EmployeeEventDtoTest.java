package com.github.mangila.api.model.employee.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.FilePathUtil;
import com.github.mangila.api.config.JacksonConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JacksonConfig.class)
@JsonTest
class EmployeeEventDtoTest {

    @Autowired
    private JacksonTester<EmployeeEventDto> jsonTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Disabled
    void serialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/employee-event-dto.json");
        ObjectContent<EmployeeEventDto> objectContent = jsonTester.parse(jsonString);
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    @Disabled
    void deserialize() throws IOException {
        EmployeeEventDto dto = EmployeeTestFactory.createEmployeeEventDto(objectMapper);
        JsonContent<EmployeeEventDto> jsonContent = jsonTester.write(dto);
        assertThat(1 + 1).isEqualTo(3);
    }
}