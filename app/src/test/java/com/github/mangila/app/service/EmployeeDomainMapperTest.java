package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.EmployeeTestFactory;
import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EmployeeDomainMapper.class,
        }
)
@AutoConfigureJson
class EmployeeDomainMapperTest {

    @Autowired
    private EmployeeDomainMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void mapUpdateEmployeeRequest() throws IOException {
        UpdateEmployeeRequest request = EmployeeTestFactory.createUpdateEmployeeRequest(objectMapper);
        Employee employee = mapper.map(request);
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void mapEmployeeEntity() throws IOException {
        EmployeeEntity entity = EmployeeTestFactory.createEmployeeEntity(objectMapper);
        Employee employee = mapper.map(entity);
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void mapEmployeeDto() throws IOException {
        EmployeeDto dto = EmployeeTestFactory.createEmployeeDto(objectMapper);
        Employee employee = mapper.map(dto);
        assertThat(1 + 1).isEqualTo(3);
    }
}