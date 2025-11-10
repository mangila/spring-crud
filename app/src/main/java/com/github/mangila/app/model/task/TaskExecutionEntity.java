package com.github.mangila.app.model.task;

import com.github.mangila.app.model.AuditMetadata;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entity is a POJO representation of a database table.
 * Save our task executions.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "task_execution")
@lombok.NoArgsConstructor
@lombok.Data
public class TaskExecutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskName;

    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;

    @Embedded
    private AuditMetadata auditMetadata;

    public TaskExecutionEntity(String taskName, ExecutionStatus status) {
        this.taskName = taskName;
        this.status = status;
        this.auditMetadata = AuditMetadata.EMPTY;
    }
}
