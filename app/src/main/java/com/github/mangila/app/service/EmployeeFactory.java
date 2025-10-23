package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import org.springframework.stereotype.Component;

@Component
public class EmployeeFactory {
    private final EmployeeIdGenerator employeeIdGenerator;

    public EmployeeFactory(EmployeeIdGenerator employeeIdGenerator) {
        this.employeeIdGenerator = employeeIdGenerator;
    }

    public Employee from(CreateNewEmployeeRequest request) {
        EmployeeId id = employeeIdGenerator.generate();
        return new Employee();
    }
}
