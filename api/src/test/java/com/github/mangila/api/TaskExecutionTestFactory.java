package com.github.mangila.api;

import com.github.mangila.api.model.task.TaskExecutionEntity;
import com.github.mangila.api.model.task.TaskExecutionStatus;

public class TaskExecutionTestFactory {

    public static TaskExecutionEntity createTaskExecutionEntity(String taskName, TaskExecutionStatus status) {
        return new TaskExecutionEntity(taskName, status, null);
    }

}
