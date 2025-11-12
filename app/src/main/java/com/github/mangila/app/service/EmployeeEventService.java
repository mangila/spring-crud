package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.app.model.employee.event.SoftDeleteEmployeeEvent;
import com.github.mangila.app.model.employee.event.UpdateEmployeeEvent;
import com.github.mangila.app.shared.SpringEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The event service is responsible for publishing the events.
 */
@Service
public class EmployeeEventService {

    private final EmployeeDtoMapper dtoMapper;
    private final SpringEventPublisher publisher;

    public EmployeeEventService(EmployeeDtoMapper dtoMapper,
                                SpringEventPublisher publisher) {
        this.dtoMapper = dtoMapper;
        this.publisher = publisher;
    }

    @Transactional
    public void publishCreateNewEvent(Employee employee) {
        EmployeeDto dto = dtoMapper.map(employee);
        var event = new CreateNewEmployeeEvent(dto);
        publisher.publish(employee.id().value(), event);
    }

    @Transactional
    public void publishUpdateEvent(Employee employee) {
        EmployeeDto dto = dtoMapper.map(employee);
        var event = new UpdateEmployeeEvent(dto);
        publisher.publish(employee.id().value(), event);
    }

    @Transactional
    public void publishSoftDeleteEvent(Employee employee) {
        EmployeeDto dto = dtoMapper.map(employee);
        var event = new SoftDeleteEmployeeEvent(dto);
        publisher.publish(dto.employeeId(), event);
    }
}
