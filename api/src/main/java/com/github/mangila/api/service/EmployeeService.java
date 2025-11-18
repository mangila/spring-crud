package com.github.mangila.api.service;

import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.domain.EmployeeId;
import com.github.mangila.api.model.employee.entity.EmployeeEntity;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.repository.EmployeeJpaRepository;
import com.github.mangila.api.repository.OutboxJpaRepository;
import com.github.mangila.api.shared.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Employee CRUD operations.
 * <br>
 * Service is responsible for orchestrating the business logic.
 */
@Service
@NullMarked
@Slf4j
public class EmployeeService {
    private final EmployeeJpaRepository employeeRepository;
    private final OutboxJpaRepository outboxJpaRepository;
    private final EmployeeEventService eventService;
    private final EmployeeDomainMapper domainMapper;
    private final EmployeeEntityMapper entityMapper;

    public EmployeeService(EmployeeJpaRepository employeeRepository,
                           OutboxJpaRepository outboxJpaRepository,
                           EmployeeEventService eventService,
                           EmployeeDomainMapper domainMapper,
                           EmployeeEntityMapper entityMapper) {
        this.employeeRepository = employeeRepository;
        this.outboxJpaRepository = outboxJpaRepository;
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
        // Map back to domain to prepare for event publishing
        final Employee newEmployee = domainMapper.map(persistedEntity);
        eventService.publishCreateNewEvent(newEmployee);
    }

    /**
     * <p>
     * Update an already existing employee.
     * <br>
     * Some APIs accept Upsert-Insert to be done here, but not this one. :)
     * a good reason why not - is to ensure/force employee ID to be created by the application and not by the client
     * </p>
     * <p>
     * We are creating the UpdateRequest with empty audit fields, and we want to use them in the same transaction.
     * Then we have to face a complex JPA corner case because of the managed/detached state of the entity.
     * <br>
     * First we fetch the "old" entity, and then we map the new entity and set the "old" audit fields.
     * To make sure the entity is managing those fields when we merge()
     * All because we want the audit fields in the event in the same transaction.
     * </p>
     */
    @Transactional
    public void updateEmployee(final Employee employee) {
        final EmployeeEntity oldEntity = employeeRepository.findById(employee.id().value())
                .orElseThrow(() -> new EntityNotFoundException(employee.id().value()));
        final EmployeeEntity mappedEntity = entityMapper.map(employee);
        mappedEntity.setAuditMetadata(oldEntity.getAuditMetadata());
        final EmployeeEntity updatedEntity = employeeRepository.merge(mappedEntity);
        final Employee updatedEmployee = domainMapper.map(updatedEntity);
        eventService.publishUpdateEvent(updatedEmployee);
    }

    @Transactional
    public void softDeleteEmployeeById(final EmployeeId id) {
        final EmployeeEntity foundEntity = employeeRepository.findById(id.value())
                .orElseThrow(() -> new EntityNotFoundException(id.value()));
        foundEntity.getAuditMetadata().setDeleted(true);
        final EmployeeEntity softDeletedEntity = employeeRepository.merge(foundEntity);
        // Map back to domain to prepare for event publishing
        final Employee employee = domainMapper.map(softDeletedEntity);
        eventService.publishSoftDeleteEvent(employee);
    }

    public List<OutboxEntity> replayEmployee(EmployeeId id) {
        return outboxJpaRepository.findAllByAggregateId(
                id.value(),
                Sort.by("sequence").descending(),
                Limit.unlimited()
        );
    }
}
