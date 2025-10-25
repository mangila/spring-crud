package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.repository.EmployeeJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return repository.findById(id.value())
                .map(mapper::toDomain)
                .orElseThrow();
    }

    public Page<Employee> findAllEmployeesByPage(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }

    public void createNewEmployee(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        repository.save(entity);
    }

    public void updateEmployee(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        repository.save(entity);
    }

    public void softDeleteEmployeeById(EmployeeId id) {
        repository.softDeleteByEmployeeId(id);
    }
}
