package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper class for Employee DTOs and Entities.
 * <p>
 * The mapper is responsible for converting between the DTO and Entity objects.
 *
 */
@Component
public class EmployeeMapper {

    public Employee toDomain(EmployeeEntity entity) {
        return new Employee();
    }

    public EmployeeDto toDto(Employee employee) {
        return new EmployeeDto();
    }

    public EmployeeEntity toEntity(Employee employee) {
        return new EmployeeEntity();
    }

    public Employee toDomain(UpdateEmployeeRequest request) {
        return new Employee();
    }
}
