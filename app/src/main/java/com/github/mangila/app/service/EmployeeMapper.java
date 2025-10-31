package com.github.mangila.app.service;

import com.github.mangila.app.model.AuditMetadata;
import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.domain.EmployeeName;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper class for Employee DTOs and Entities.
 * <p>
 * The mapper is responsible for converting domain objects to DTOs and vice versa.
 */
@Component
public class EmployeeMapper {

    public Employee toDomain(EmployeeEntity entity) {
        var domain = new Employee();
        domain.setId(new EmployeeId(entity.getId()));
        domain.setFirstName(new EmployeeName(entity.getFirstName()));
        domain.setLastName(new EmployeeName(entity.getLastName()));
        domain.setSalary(entity.getSalary());
        domain.setAttributes(entity.getAttributes());
        domain.setCreated(entity.getAuditMetadata().getCreated());
        domain.setModified(entity.getAuditMetadata().getModified());
        domain.setDeleted(entity.getAuditMetadata().getDeleted());
        return domain;
    }

    public EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(
                employee.getFirstName().value(),
                employee.getLastName().value(),
                employee.getSalary(),
                employee.getAttributes(),
                employee.getCreated(),
                employee.getModified(),
                employee.getDeleted()
        );
    }

    public EmployeeEntity toEntity(Employee employee) {
        var entity = new EmployeeEntity();
        entity.setId(employee.getId().value());
        entity.setFirstName(employee.getFirstName().value());
        entity.setLastName(employee.getLastName().value());
        entity.setSalary(employee.getSalary());
        entity.setAttributes(employee.getAttributes());
        var auditMetadata = new AuditMetadata();
        auditMetadata.setCreated(employee.getCreated());
        auditMetadata.setModified(employee.getModified());
        auditMetadata.setDeleted(employee.getDeleted());
        entity.setAuditMetadata(auditMetadata);
        return entity;
    }

    public Employee toDomain(UpdateEmployeeRequest request) {
        var domain = new Employee();
        domain.setId(new EmployeeId(request.employeeId()));
        domain.setFirstName(new EmployeeName(request.firstName()));
        domain.setLastName(new EmployeeName(request.lastName()));
        domain.setSalary(request.salary());
        domain.setAttributes(request.attributes());
        return domain;
    }
}
