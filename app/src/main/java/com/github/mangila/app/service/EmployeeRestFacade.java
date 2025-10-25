package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EmployeeRestFacade {

    private final EmployeeService service;
    private final EmployeeMapper mapper;
    private final EmployeeFactory factory;

    public EmployeeRestFacade(EmployeeService service,
                              EmployeeMapper mapper,
                              EmployeeFactory factory) {
        this.service = service;
        this.mapper = mapper;
        this.factory = factory;
    }

    public EmployeeDto findEmployeeById(String employeeId) {
        EmployeeId id = new EmployeeId(employeeId);
        Employee employee = service.findEmployeeById(id);
        return mapper.toDto(employee);
    }

    public String createNewEmployee(CreateNewEmployeeRequest request) {
        Employee employee = factory.from(request);
        service.createNewEmployee(employee);
        return employee.getId().value();
    }

    public void updateEmployee(UpdateEmployeeRequest request) {
        Employee employee = mapper.toDomain(request);
        service.updateEmployee(employee);
    }

    public void softDeleteEmployeeById(String employeeId) {
        EmployeeId id = new EmployeeId(employeeId);
        service.softDeleteEmployeeById(id);
    }

    public Page<EmployeeDto> findAllEmployeesByPage(Pageable pageable) {
        return service.findAllEmployeesByPage(pageable)
                .map(mapper::toDto);
    }
}
