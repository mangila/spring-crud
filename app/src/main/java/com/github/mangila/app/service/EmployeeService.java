package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.repository.EmployeeJpaRepository;
import com.github.mangila.app.shared.Ensure;
import com.github.mangila.app.shared.SpringEventPublisher;
import com.github.mangila.app.shared.event.CreateNewEmployeeEvent;
import com.github.mangila.app.shared.event.UpdateEmployeeEvent;
import com.github.mangila.app.shared.exception.EntityNotFoundException;
import org.jspecify.annotations.NullMarked;
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

    private final EmployeeJpaRepository repository;
    private final EmployeeMapper mapper;
    private final SpringEventPublisher publisher;

    public EmployeeService(EmployeeJpaRepository repository,
                           EmployeeMapper mapper,
                           SpringEventPublisher publisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.publisher = publisher;
    }

    public boolean existsById(EmployeeId id) {
        return repository.existsById(id.value());
    }

    public Employee findEmployeeById(EmployeeId id) {
        return repository.findById(id.value())
                .map(mapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Employee with id: (%s) not found", id.value())));
    }

    public Page<Employee> findAllEmployeesByPage(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Transactional
    public void createNewEmployee(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        repository.save(entity);
        publisher.publish(new CreateNewEmployeeEvent(employee.id()));
    }

    /**
     * Update an already existing employee.
     * <br>
     * Here we don't want an Upsert-Insert to happen since we use repository.save(),
     * so we first check for existence.
     * <br>
     * Some APIs accept this, not this one! :P
     * a good reason why not - is to ensure/force employee ID to be created by the application and not by the client
     */
    @Transactional
    public void updateEmployee(Employee employee) {
        Ensure.isTrue(existsById(employee.id()), () -> new EntityNotFoundException(String.format("Employee with id: (%s) not found", employee.id().value())));
        EmployeeEntity entity = mapper.toEntity(employee);
        repository.save(entity);
        publisher.publish(new UpdateEmployeeEvent(employee.id()));
    }

    @Transactional
    public void softDeleteEmployeeById(EmployeeId id) {
        Ensure.isTrue(existsById(id), () -> new EntityNotFoundException(String.format("Employee with id: (%s) not found", id.value())));
        repository.softDeleteByEmployeeId(id);
    }
}
