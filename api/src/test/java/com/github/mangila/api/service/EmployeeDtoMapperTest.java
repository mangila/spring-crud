package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.ClockTestConfig;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.config.WebConfig;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.dto.EmployeeDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EmployeeDtoMapper.class,
                WebConfig.class,
                ClockTestConfig.TestClock.class,
        }
)
@AutoConfigureJson
class EmployeeDtoMapperTest {

    @Autowired
    private EmployeeDtoMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Disabled
    void map() throws IOException {
        Employee employee = EmployeeTestFactory.createEmployee(objectMapper);
        EmployeeDto dto = mapper.map(employee);
        assertThat(1 + 1).isEqualTo(3);
    }
}