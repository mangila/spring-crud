package com.github.mangila.app.repository;

import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskExecutionJpaRepository extends BaseJpaRepository<TaskExecutionEntity, UUID> {
    List<TaskExecutionEntity> findAllByStatusAndAuditMetadataDeleted(TaskExecutionStatus status, boolean deleted, Sort sort, Limit limit);
}
