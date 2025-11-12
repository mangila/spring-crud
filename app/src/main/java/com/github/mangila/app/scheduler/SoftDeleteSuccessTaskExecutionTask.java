package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;
import com.github.mangila.app.repository.TaskExecutionJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class SoftDeleteSuccessTaskExecutionTask implements Task {

    private final TaskExecutionJpaRepository taskExecutionRepository;
    private final ObjectMapper objectMapper;

    public SoftDeleteSuccessTaskExecutionTask(TaskExecutionJpaRepository taskExecutionRepository,
                                              ObjectMapper objectMapper) {
        this.taskExecutionRepository = taskExecutionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    @Transactional
    public ObjectNode call() {
        List<TaskExecutionEntity> entities = taskExecutionRepository.findAllByStatusAndAuditMetadataDeleted(
                TaskExecutionStatus.SUCCESS,
                false,
                Sort.by("auditMetadata.created").descending(),
                Limit.of(50));
        var node = objectMapper.createObjectNode();
        if (entities.isEmpty()) {
            return node.put("message", "No success task executions to soft delete");
        }
        node.put("message", "Soft deleting %d success task executions".formatted(entities.size()));
        var arrayNode = node.putArray("ids");
        for (TaskExecutionEntity entity : entities) {
            var audit = entity.getAuditMetadata();
            // Could happen if change in jpql query to include NULL
            if (audit == null) {
                log.warn("Task execution {} has no audit metadata", entity.getId());
                node.put(entity.getId().toString(), "Task execution event has no audit metadata");
                continue;
            }
            arrayNode.add(entity.getId().toString());
            audit.setDeleted(true);
        }
        taskExecutionRepository.mergeAll(entities);
        taskExecutionRepository.flush();
        return node;
    }
}
