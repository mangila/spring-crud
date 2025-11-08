package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDtoMapper {

    public EmployeeDto map(Employee employee) {
        return new EmployeeDto(
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
}
