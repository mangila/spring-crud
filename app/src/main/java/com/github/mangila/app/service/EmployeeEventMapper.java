package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.dto.EmployeeEventDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventMapper {

    private final ObjectMapper objectMapper;

    public EmployeeEventMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EmployeeEventDto map(Employee employee) {
        return new EmployeeEventDto(
                employee.id().value(),
                employee.firstName().value(),
                employee.lastName().value(),
                employee.salary().value(),
                employee.employmentActivity(),
                employee.employmentStatus(),
                employee.attributes().value(),
                employee.audit().created(),
                employee.audit().modified(),
                employee.audit().deleted()
        );
    }

    public EmployeeEventDto map(ObjectNode objectNode) {
        return objectMapper.convertValue(objectNode, EmployeeEventDto.class);
    }
}
