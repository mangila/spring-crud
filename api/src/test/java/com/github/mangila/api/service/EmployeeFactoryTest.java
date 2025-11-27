package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.model.employee.domain.*;
import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
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
                EmployeeIdGenerator.class,
                EmployeeFactory.class,
        }
)
@AutoConfigureJson
class EmployeeFactoryTest {

    @Autowired
    private EmployeeFactory factory;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void from() throws IOException {
        CreateNewEmployeeRequest request = EmployeeTestFactory.createNewEmployeeRequest(objectMapper);
        Employee employee = factory.from(request);
        assertThat(employee).isNotNull();
        assertThat(employee)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("firstName", new EmployeeName(request.firstName()))
                .hasFieldOrPropertyWithValue("lastName", new EmployeeName(request.lastName()))
                .hasFieldOrPropertyWithValue("salary", new EmployeeSalary(request.salary()))
                .hasFieldOrPropertyWithValue("employmentActivity", request.employmentActivity())
                .hasFieldOrPropertyWithValue("employmentStatus", request.employmentStatus())
                .hasFieldOrPropertyWithValue("attributes", new EmployeeAttributes(request.attributes()))
                .hasFieldOrPropertyWithValue("audit", EmployeeAudit.EMPTY);
    }
}