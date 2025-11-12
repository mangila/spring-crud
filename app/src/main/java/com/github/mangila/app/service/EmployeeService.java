package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.repository.EmployeeJpaRepository;
import com.github.mangila.app.shared.Ensure;
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
    private final EmployeeEventService eventService;
    private final EmployeeDomainMapper domainMapper;
    private final EmployeeEntityMapper entityMapper;

    public EmployeeService(EmployeeJpaRepository employeeRepository,
                           EmployeeEventService eventService,
                           EmployeeDomainMapper domainMapper,
                           EmployeeEntityMapper entityMapper) {
        this.employeeRepository = employeeRepository;
        this.eventService = eventService;
        this.domainMapper = domainMapper;
        this.entityMapper = entityMapper;
    }

    public Employee findEmployeeById(final EmployeeId id) {
        return employeeRepository.findById(id.value())
                .map(domainMapper::map)
                .orElseThrow(() -> new EntityNotFoundException(id.value()));
    }

    public Page<Employee> findAllEmployeesByPage(final Pageable pageable) {
        // TODO: create a Probe for querying
        var probe = Example.of(new EmployeeEntity());
        return employeeRepository.findAll(probe, pageable)
                .map(domainMapper::map);
    }

    @Transactional
    public void createNewEmployee(final Employee employee) {
        final EmployeeEntity mappedEntity = entityMapper.map(employee);
        final EmployeeEntity persistedEntity = employeeRepository.persist(mappedEntity);
        // Map back to domain with the new audit values
        final Employee newEmployee = domainMapper.map(persistedEntity);
        eventService.publishCreateNewEvent(newEmployee);
    }

    /**
     * Update an already existing employee.
     * <br>
     * Some APIs accept Upsert-Insert to be done here, but not this one. :)
     * a good reason why not - is to ensure/force employee ID to be created by the application and not by the client
     */
    @Transactional
    public void updateEmployee(final Employee employee) {
        final boolean exists = employeeRepository.existsById(employee.id().value());
        Ensure.isTrue(exists,
                () -> new EntityNotFoundException(employee.id().value()));
        final EmployeeEntity mappedEntity = entityMapper.map(employee);
        final EmployeeEntity updatedEntity = employeeRepository.merge(mappedEntity);
        // Map back to domain with the new audit values
        final Employee updatedEmployee = domainMapper.map(updatedEntity);
        eventService.publishUpdateEvent(updatedEmployee);
    }

    @Transactional
    public void softDeleteEmployeeById(final EmployeeId id) {
        final EmployeeEntity foundEntity = employeeRepository.findById(id.value())
                .orElseThrow(() -> new EntityNotFoundException(id.value()));
        foundEntity.getAuditMetadata().setDeleted(true);
        final EmployeeEntity softDeletedEntity = employeeRepository.merge(foundEntity);
        final Employee employee = domainMapper.map(softDeletedEntity);
        eventService.publishSoftDeleteEvent(employee);
    }
}
