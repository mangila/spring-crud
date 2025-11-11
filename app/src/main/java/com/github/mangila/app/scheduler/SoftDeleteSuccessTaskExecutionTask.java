package com.github.mangila.app.scheduler;

import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SoftDeleteSuccessTaskExecutionTask implements Task {

    private final TaskExecutionJpaRepository taskExecutionRepository;

    public SoftDeleteSuccessTaskExecutionTask(TaskExecutionJpaRepository taskExecutionRepository) {
        this.taskExecutionRepository = taskExecutionRepository;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void run() {
        List<TaskExecutionEntity> entities = taskExecutionRepository.findAllByStatusAndAuditMetadataDeleted(
                TaskExecutionStatus.SUCCESS,
                false,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(50));
        if (entities.isEmpty()) {
            log.info("No success task executions to soft delete");
            return;
        }
        log.info("Soft deleting {} success task executions", entities.size());
        for (TaskExecutionEntity entity : entities) {
            var audit = entity.getAuditMetadata();
            // Could happen if change in jpql query to include NULL
            if (audit == null) {
                log.warn("TaskExecution {} has no audit metadata", entity.getId());
                continue;
            }
            audit.setDeleted(true);
        }
        taskExecutionRepository.mergeAll(entities);
        taskExecutionRepository.flush();
    }
}
