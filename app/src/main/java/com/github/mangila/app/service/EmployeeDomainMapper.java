package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.*;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
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
                new EmployeeSalary(dto.salary()),
                dto.employmentActivity(),
                dto.employmentStatus(),
                new EmployeeAttributes(dto.attributes()),
                EmployeeAudit.EMPTY
        );
    }

    public Employee map(EmployeeDto dto) {
        return new Employee(
                new EmployeeId(dto.employeeId()),
                new EmployeeName(dto.firstName()),
                new EmployeeName(dto.lastName()),
                new EmployeeSalary(dto.salary()),
                dto.employmentActivity(),
                dto.employmentStatus(),
                new EmployeeAttributes(dto.attributes()),
                new EmployeeAudit(dto.created(), dto.modified(), dto.deleted())
        );
    }

    public Employee map(EmployeeEntity entity) {
        return new Employee(
                new EmployeeId(entity.getId()),
                new EmployeeName(entity.getFirstName()),
                new EmployeeName(entity.getLastName()),
                new EmployeeSalary(entity.getSalary()),
                entity.getEmploymentActivity(),
                entity.getEmploymentStatus(),
                new EmployeeAttributes(entity.getAttributes()),
                new EmployeeAudit(
                        entity.getAuditMetadata().getCreated(),
                        entity.getAuditMetadata().getModified(),
                        entity.getAuditMetadata().isDeleted()
                )
        );
    }
}
