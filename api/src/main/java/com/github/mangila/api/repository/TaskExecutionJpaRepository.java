package com.github.mangila.api.repository;

import com.github.mangila.api.model.task.TaskExecutionEntity;
import com.github.mangila.api.model.task.TaskExecutionStatus;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskExecutionJpaRepository extends BaseJpaRepository<TaskExecutionEntity, Long> {
    List<TaskExecutionEntity> findAllByStatusAndAuditMetadataDeleted(TaskExecutionStatus status, boolean deleted, Sort sort, Limit limit);
}
