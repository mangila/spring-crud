package com.github.mangila.api.repository;

import com.github.mangila.api.model.employee.entity.EmployeeEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeJpaRepository extends BaseJpaRepository<EmployeeEntity, String> {
}
