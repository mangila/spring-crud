package com.github.mangila.app.repository;

import com.github.mangila.app.model.task.TaskExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskExecutionJpaRepository extends JpaRepository<TaskExecutionEntity, Long> {
}
