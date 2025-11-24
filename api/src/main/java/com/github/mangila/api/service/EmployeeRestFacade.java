package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.domain.EmployeeId;
import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.api.model.employee.dto.EmployeeDto;
import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import com.github.mangila.api.model.employee.dto.UpdateEmployeeRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
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
@Slf4j
public class EmployeeRestFacade {

    private final EmployeeService service;
    private final EmployeeDtoMapper dtoMapper;
    private final EmployeeEventMapper eventMapper;
    private final EmployeeDomainMapper domainMapper;
    private final EmployeeFactory factory;

    public EmployeeRestFacade(EmployeeService service,
                              EmployeeDtoMapper dtoMapper,
                              EmployeeEventMapper eventMapper,
                              EmployeeDomainMapper domainMapper,
                              EmployeeFactory factory) {
        this.service = service;
        this.dtoMapper = dtoMapper;
        this.eventMapper = eventMapper;
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

    public Page<@Nullable EmployeeDto> replayEmployee(String employeeId, Pageable pageable) {
        EmployeeId id = new EmployeeId(employeeId);
        return service.replayEmployee(id, pageable)
                .map(entity -> {
                    log.info("Outbox event: {}", entity);
                    // the event payload JSON must have a "dto" key
                    ObjectNode dto = (ObjectNode) entity
                            .getPayload()
                            .get("dto");
                    if (dto != null && dto.isObject()) {
                        EmployeeEventDto eventDto = eventMapper.map(dto);
                        return dtoMapper.map(eventDto);
                    }
                    log.warn("Event missing 'dto' key: {} - {}", entity.getEventName(), entity.getId());
                    return null;
                });
    }
}
