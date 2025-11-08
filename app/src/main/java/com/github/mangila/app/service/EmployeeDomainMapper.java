package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.*;
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
                new EmployeeAttributes(dto.attributes()),
                dto.employmentActivity(),
                dto.employmentStatus(),
                EmployeeAudit.EMPTY
        );
    }

    public Employee map(EmployeeEntity entity) {
        return new Employee(
                new EmployeeId(entity.getId()),
                new EmployeeName(entity.getFirstName()),
                new EmployeeName(entity.getLastName()),
                new EmployeeSalary(entity.getSalary()),
                new EmployeeAttributes(entity.getAttributes()),
                entity.getEmploymentActivity(),
                entity.getEmploymentStatus(),
                new EmployeeAudit(
                        entity.getAuditMetadata().created(),
                        entity.getAuditMetadata().modified(),
                        entity.getAuditMetadata().deleted()
                )
        );
    }
}
