package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class FileUploadListener implements Callable<ObjectNode> {

    @Getter
    private volatile boolean running = false;
    @Getter
    private volatile boolean shutdown = false;

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void shutdown() {
        shutdown = true;
    }

    @Override
    public ObjectNode call() throws Exception {
        return null;
    }
}
