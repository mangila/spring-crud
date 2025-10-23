package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.shared.Ensure;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class EmployeeRestFacade {

    private final EmployeeService employeeService;
    private final EmployeeMapper mapper;
    private final EmployeeFactory factory;

    public EmployeeRestFacade(EmployeeService employeeService,
                              EmployeeMapper mapper,
                              EmployeeFactory factory) {
        this.employeeService = employeeService;
        this.mapper = mapper;
        this.factory = factory;
    }

    public EmployeeDto findEmployeeById(String employeeId) {
        Ensure.notNull(employeeId);
        EmployeeId id = new EmployeeId(employeeId);
        Employee employee = employeeService.findEmployeeById(id);
        return mapper.toDto(employee);
    }

    public void createNewEmployee(@Valid CreateNewEmployeeRequest request) {
        Ensure.notNull(request);
        Employee employee = factory.from(request);
        employeeService.createNewEmployee(employee);
    }

    public void updateEmployee(@Valid UpdateEmployeeRequest request) {
        Ensure.notNull(request);
        Employee employee = mapper.toDomain(request);
        employeeService.updateEmployee(employee);
    }

    public void softDeleteEmployeeById(String employeeId) {
        Ensure.notNull(employeeId);
        EmployeeId id = new EmployeeId(employeeId);
        employeeService.softDeleteEmployeeById(id);
    }
}
