package com.github.mangila.api.scheduler;

import com.github.mangila.api.shared.exception.TaskNotFoundException;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;

public record TaskMap(Map<String, Task> map) {

    @NonNull
    public Task getTaskOrThrow(String name) {
        Task task = map.get(name);
        if (task == null) {
            throw new TaskNotFoundException(name);
        }
        return task;
    }

    public Set<String> keySet() {
        return map.keySet();
    }
}
