package com.github.mangila.app;

import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;

public class TaskExecutionTestFactory {

    public static TaskExecutionEntity createTaskExecutionEntity(String taskName, TaskExecutionStatus status) {
        return new TaskExecutionEntity(taskName, status, null);
    }

}
