package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.repository.EmployeeJpaRepository;
import com.github.mangila.app.shared.Ensure;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeJpaRepository repository;
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeJpaRepository repository,
                           EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Employee findEmployeeById(EmployeeId id) {
        Ensure.notNull(id);
        return repository.findByEmployeeId(id.value())
                .map(mapper::toDomain)
                .orElseThrow();
    }

    public void createNewEmployee(Employee employee) {
        Ensure.notNull(employee);
        EmployeeEntity entity = mapper.toEntity(employee);
        repository.save(entity);
    }

    public void updateEmployee(Employee employee) {
        Ensure.notNull(employee);
        EmployeeEntity entity = mapper.toEntity(employee);
        repository.save(entity);
    }

    public void softDeleteEmployeeById(EmployeeId id) {
        Ensure.notNull(id);
        repository.softDeleteByEmployeeId(id.value());
    }
}
