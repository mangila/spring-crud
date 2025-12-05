package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.api.model.employee.entity.EmployeeEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
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
    @Disabled
    void mapUpdateEmployeeRequest() throws IOException {
        UpdateEmployeeRequest request = EmployeeTestFactory.createUpdateEmployeeRequest(objectMapper);
      //  Employee employee = mapper.map(request);
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    @Disabled
    void mapEmployeeEntity() throws IOException {
        EmployeeEntity entity = EmployeeTestFactory.createEmployeeEntity(objectMapper);
       // Employee employee = mapper.map(entity);
        assertThat(1 + 1).isEqualTo(3);
    }
}