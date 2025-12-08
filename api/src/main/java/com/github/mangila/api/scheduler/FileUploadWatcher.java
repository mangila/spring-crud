package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class FileUploadWatcher implements SmartLifecycle {

    private final FileUploadListener listener;
    private final ApplicationTaskExecutor applicationTaskExecutor;
    private final ObjectMapper objectMapper;
    private CompletableFuture<ObjectNode> listenEventLoop;

    public FileUploadWatcher(FileUploadListener listener,
                             ApplicationTaskExecutor applicationTaskExecutor,
                             ObjectMapper objectMapper) {
        this.listener = listener;
        this.applicationTaskExecutor = applicationTaskExecutor;
        this.objectMapper = objectMapper;
    }

    @Override
    public void start() {
        log.info("Starting FileUploadListener");
        listener.start();
        var attributes = objectMapper.createObjectNode();
        attributes.put("executedBy", FileUploadWatcher.class.getSimpleName());
        this.listenEventLoop = applicationTaskExecutor.submitCompletable(
                listener,
                "file-upload-listener",
                attributes
        );
    }

    @Override
    public void stop() {
        log.info("Stopping FileUploadListener");
        listener.stop();
        listenEventLoop.cancel(true);
    }

    @Override
    public boolean isRunning() {
        return listener.isRunning();
    }
}
