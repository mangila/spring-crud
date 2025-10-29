package com.github.mangila.app.repository;

import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, String> {

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true)
    @Query("""
            UPDATE EmployeeEntity e SET e.auditMetadata.deleted = true
            WHERE e.id = :#{#employeeId.value()}
            """)
    void softDeleteByEmployeeId(EmployeeId employeeId);
}
