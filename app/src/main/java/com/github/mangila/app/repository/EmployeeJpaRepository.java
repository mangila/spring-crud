package com.github.mangila.app.repository;

import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeJpaRepository extends BaseJpaRepository<EmployeeEntity, String> {
}
