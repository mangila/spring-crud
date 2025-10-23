package com.github.mangila.app.repository;

import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findByEmployeeId(String employeeId);

    @Modifying
    @Query("""
            UPDATE EmployeeEntity e SET e.deleted = true
            WHERE e.employeeId = :employeeId
            """)
    void softDeleteByEmployeeId(String employeeId);
}
