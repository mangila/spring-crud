package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.entity.EmployeeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This testing is for slim context stuffs where no full Spring context is required.
 * This is a good practice for unit testing of simple beans.
 * <br>
 * If there are more dependencies, it can be loaded with {@link ContextConfiguration} or just use the full Spring context.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EmployeeEntityMapper.class,
        }
)
@AutoConfigureJson
class EmployeeEntityMapperTest {

    @Autowired
    private EmployeeEntityMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void map() throws IOException {
        Employee employee = EmployeeTestFactory.createEmployee(objectMapper);
        EmployeeEntity entity = mapper.map(employee);
        assertThat(1 + 1).isEqualTo(3);
    }
}