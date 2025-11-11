package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.app.model.employee.event.SoftDeleteEmployeeEvent;
import com.github.mangila.app.model.employee.event.UpdateEmployeeEvent;
import com.github.mangila.app.repository.EmployeeJpaRepository;
import com.github.mangila.app.shared.Ensure;
import com.github.mangila.app.shared.SpringEventPublisher;
import com.github.mangila.app.shared.exception.EntityNotFoundException;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Employee CRUD operations.
 * <br>
 * Service is responsible for orchestrating the business logic.
 */
@Service
@NullMarked
public class EmployeeService {
    private final EmployeeJpaRepository employeeRepository;
    private final EmployeeDomainMapper domainMapper;
    private final EmployeeEntityMapper entityMapper;
    private final SpringEventPublisher publisher;

    public EmployeeService(EmployeeJpaRepository employeeRepository,
                           EmployeeDomainMapper domainMapper,
                           EmployeeEntityMapper entityMapper,
                           SpringEventPublisher publisher) {
        this.employeeRepository = employeeRepository;
        this.domainMapper = domainMapper;
        this.entityMapper = entityMapper;
        this.publisher = publisher;
    }

    public boolean existsById(EmployeeId id) {
        return employeeRepository.existsById(id.value());
    }

    public Employee findEmployeeById(EmployeeId id) {
        return employeeRepository.findById(id.value())
                .map(domainMapper::map)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Employee with aggregateId: (%s) not found", id.value())));
    }

    public Page<Employee> findAllEmployeesByPage(Pageable pageable) {
        // TODO: create a Probe for querying
        var probe = Example.of(new EmployeeEntity());
        return employeeRepository.findAll(probe, pageable)
                .map(domainMapper::map);
    }

    @Transactional
    public void createNewEmployee(Employee employee) {
        EmployeeEntity entity = entityMapper.map(employee);
        employeeRepository.persist(entity);
        publisher.publish(employee.id().value(), new CreateNewEmployeeEvent(employee.id()));
    }

    /**
     * Update an already existing employee.
     * <br>
     * Some APIs accept Upsert-Insert to be done here, but not this one. :)
     * a good reason why not - is to ensure/force employee ID to be created by the application and not by the client
     */
    @Transactional
    public void updateEmployee(Employee employee) {
        Ensure.isTrue(existsById(employee.id()), () -> new EntityNotFoundException(String.format("Employee with aggregateId: (%s) not found", employee.id().value())));
        EmployeeEntity entity = entityMapper.map(employee);
        employeeRepository.merge(entity);
        publisher.publish(employee.id().value(), new UpdateEmployeeEvent(employee.id()));
    }

    @Transactional
    public void softDeleteEmployeeById(EmployeeId id) {
        Ensure.isTrue(existsById(id), () -> new EntityNotFoundException(String.format("Employee with aggregateId: (%s) not found", id.value())));
        employeeRepository.softDeleteByEmployeeId(id);
        publisher.publish(id.value(), new SoftDeleteEmployeeEvent(id));
    }
}
