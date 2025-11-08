package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Facade for REST endpoints.
 * <p>
 * Facade is responsible for orchestrating REST API calls.
 * <br>
 * When introducing a new protocol or input to the system, this will make it easier to adapt for the service layer.
 * E.g., EmployeeGrpcFacade, EmployeeKafkaFacade, EmployeeQuantumFacade :)
 * <br>
 * Bridge, Facade, Adapter... you name it.
 *
 */
@Service
public class EmployeeRestFacade {

    private final EmployeeService service;
    private final EmployeeDtoMapper dtoMapper;
    private final EmployeeDomainMapper domainMapper;
    private final EmployeeFactory factory;

    public EmployeeRestFacade(EmployeeService service,
                              EmployeeDtoMapper dtoMapper,
                              EmployeeDomainMapper domainMapper,
                              EmployeeFactory factory) {
        this.service = service;
        this.dtoMapper = dtoMapper;
        this.domainMapper = domainMapper;
        this.factory = factory;
    }

    public EmployeeDto findEmployeeById(String employeeId) {
        EmployeeId id = new EmployeeId(employeeId);
        Employee employee = service.findEmployeeById(id);
        return dtoMapper.map(employee);
    }

    public Page<EmployeeDto> findAllEmployeesByPage(Pageable pageable) {
        return service.findAllEmployeesByPage(pageable)
                .map(dtoMapper::map);
    }

    public String createNewEmployee(CreateNewEmployeeRequest request) {
        Employee employee = factory.from(request);
        service.createNewEmployee(employee);
        return employee.id().value();
    }

    public EmployeeDto updateEmployee(UpdateEmployeeRequest request) {
        Employee employee = domainMapper.map(request);
        // Start a transaction and update the employee in the db
        service.updateEmployee(employee);
        // Fetch the updated employee after the transaction commit
        employee = service.findEmployeeById(employee.id());
        return dtoMapper.map(employee);
    }

    public void softDeleteEmployeeById(String employeeId) {
        EmployeeId id = new EmployeeId(employeeId);
        service.softDeleteEmployeeById(id);
    }
}
