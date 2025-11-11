package com.github.mangila.app.model.task;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.AuditMetadata;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entity is a POJO representation of a database table.
 * Save our task executions for auditing purposes.
 * Good idea then retain it for some days or something.
 * Data can be saved here for more insight about the task.
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

    @Column(name = "task_name",
            nullable = false)
    private String taskName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",
            nullable = false)
    private TaskExecutionStatus status;

    @Type(JsonBinaryType.class)
    @Column(name = "attributes",
            columnDefinition = "jsonb")
    private ObjectNode attributes;

    @Embedded
    private AuditMetadata auditMetadata;

    public static TaskExecutionEntity newExecution(String taskName, @Nullable ObjectNode attributes) {
        return new TaskExecutionEntity(taskName, TaskExecutionStatus.RUNNING, attributes);
    }

    public TaskExecutionEntity(String taskName, TaskExecutionStatus status, @Nullable ObjectNode attributes) {
        this.taskName = taskName;
        this.status = status;
        this.attributes = attributes;
        this.auditMetadata = AuditMetadata.EMPTY;
    }
}
