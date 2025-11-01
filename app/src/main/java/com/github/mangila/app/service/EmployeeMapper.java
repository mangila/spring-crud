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
 * <br>
 * The mapper is responsible for converting domain objects to DTOs and vice versa.
 * <br>
 * In some cases a separate mapper class can be implemented for each "layer" of the application.
 * Or just use a "fat" mapper class for everything. This construct would be a "fat" mapper.
 * <br>
 * e.g., EmployeeDtoMapper.toDomain(), EmployeeEntityMapper.toDomain(), EmployeeDomainMapper.toDto() and EmployeeDomainMapper.toEntity()
 */
@Component
public class EmployeeMapper {

    public Employee toDomain(EmployeeEntity entity) {
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

    public EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(
                employee.id().value(),
                employee.firstName().value(),
                employee.lastName().value(),
                employee.salary(),
                employee.attributes(),
                employee.created(),
                employee.modified(),
                employee.deleted()
        );
    }

    public EmployeeEntity toEntity(Employee employee) {
        var entity = new EmployeeEntity();
        entity.setId(employee.id().value());
        entity.setFirstName(employee.firstName().value());
        entity.setLastName(employee.lastName().value());
        entity.setSalary(employee.salary());
        entity.setAttributes(employee.attributes());
        var auditMetadata = new AuditMetadata(
                employee.created(),
                employee.modified(),
                employee.deleted()
        );
        entity.setAuditMetadata(auditMetadata);
        return entity;
    }

    public Employee toDomain(UpdateEmployeeRequest request) {
        return new Employee(
                new EmployeeId(request.employeeId()),
                new EmployeeName(request.firstName()),
                new EmployeeName(request.lastName()),
                request.salary(),
                request.attributes(),
                null,
                null,
                false
        );
    }
}
