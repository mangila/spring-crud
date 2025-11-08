package com.github.mangila.app.service;

import com.github.mangila.app.model.AuditMetadata;
import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEntityMapper {

    public EmployeeEntity map(Employee employee) {
        var entity = new EmployeeEntity();
        entity.setId(employee.id().value());
        entity.setFirstName(employee.firstName().value());
        entity.setLastName(employee.lastName().value());
        entity.setSalary(employee.salary().value());
        entity.setEmploymentActivity(employee.employmentActivity());
        entity.setEmploymentStatus(employee.employmentStatus());
        entity.setAttributes(employee.attributes().value());
        var auditMetadata = new AuditMetadata(
                employee.audit().created(),
                employee.audit().modified(),
                employee.audit().deleted()
        );
        entity.setAuditMetadata(auditMetadata);
        return entity;
    }
}
