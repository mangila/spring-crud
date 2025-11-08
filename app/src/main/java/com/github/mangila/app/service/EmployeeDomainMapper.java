package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.domain.EmployeeName;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDomainMapper {

    public Employee map(UpdateEmployeeRequest dto) {
        return new Employee(
                new EmployeeId(dto.employeeId()),
                new EmployeeName(dto.firstName()),
                new EmployeeName(dto.lastName()),
                dto.salary(),
                dto.attributes(),
                null, // Updated by the database
                null, // Updated by the database
                dto.deleted()
        );
    }

    public Employee map(EmployeeEntity entity) {
        return new Employee(
                new EmployeeId(entity.getId()),
                new EmployeeName(entity.getFirstName()),
                new EmployeeName(entity.getLastName()),
                entity.getSalary(),
                entity.getAttributes(),
                entity.getAuditMetadata().created(),
                entity.getAuditMetadata().modified(),
                entity.getAuditMetadata().deleted()
        );
    }
}
