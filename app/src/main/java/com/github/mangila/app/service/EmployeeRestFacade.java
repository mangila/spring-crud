package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import org.springframework.stereotype.Service;

@Service
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
        EmployeeId id = new EmployeeId(employeeId);
        Employee employee = employeeService.findEmployeeById(id);
        return mapper.toDto(employee);
    }

    public void createNewEmployee(CreateNewEmployeeRequest request) {
        Employee employee = factory.from(request);
        employeeService.createNewEmployee(employee);
    }

    public void updateEmployee(UpdateEmployeeRequest request) {
        Employee employee = mapper.toDomain(request);
        employeeService.updateEmployee(employee);
    }

    public void softDeleteEmployeeById(String employeeId) {
        EmployeeId id = new EmployeeId(employeeId);
        employeeService.softDeleteEmployeeById(id);
    }
}
