package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.repository.EmployeeJpaRepository;
import com.github.mangila.app.shared.SpringEventPublisher;
import com.github.mangila.app.shared.event.NewEmployeeCreatedEvent;
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

    public Employee findEmployeeById(EmployeeId id) {
        return repository.findById(id.value())
                .map(mapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Employee with id %s not found", id)));
    }

    public Page<Employee> findAllEmployeesByPage(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }

    public void createNewEmployee(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        entity = repository.save(entity);
        publisher.publish(new NewEmployeeCreatedEvent(entity));
    }

    public Employee updateEmployee(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        repository.save(entity);
        return findEmployeeById(employee.id());
    }

    @Transactional
    public void softDeleteEmployeeById(EmployeeId id) {
        repository.softDeleteByEmployeeId(id);
    }
}
