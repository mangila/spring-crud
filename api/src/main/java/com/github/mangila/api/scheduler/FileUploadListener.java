package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.FileUploadRequest;
import com.github.mangila.api.model.FileUploadTaskQueue;
import com.github.mangila.api.service.EmployeeFactory;
import com.github.mangila.api.service.EmployeeService;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@Component
@Slf4j
public class FileUploadListener implements Callable<ObjectNode> {

    private final FileUploadTaskQueue queue;
    private final ObjectMapper objectMapper;
    private final EmployeeService employeeService;
    private final EmployeeFactory employeeFactory;
    private final ApplicationTaskExecutor applicationTaskExecutor;
    @Getter
    private volatile boolean running = false;

    public FileUploadListener(FileUploadTaskQueue queue,
                              ObjectMapper objectMapper,
                              EmployeeService employeeService,
                              EmployeeFactory employeeFactory,
                              ApplicationTaskExecutor applicationTaskExecutor) {
        this.queue = queue;
        this.objectMapper = objectMapper;
        this.employeeService = employeeService;
        this.employeeFactory = employeeFactory;
        this.applicationTaskExecutor = applicationTaskExecutor;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    @Override
    public ObjectNode call() {
        Instant start = Instant.now();
        var node = objectMapper.createObjectNode();
        while (running) {
            try {
                FileUploadRequest request = queue.take();
                var attributes = objectMapper.createObjectNode();
                attributes.put("fileName", request.path().getFileName().toString());
                attributes.put("fileSize", Files.size(request.path()));
                attributes.put("originalFileName", request.originalFileName());
                attributes.put("contentType", request.contentType());
                attributes.put("executedBy", FileUploadListener.class.getSimpleName());
                Task task = getTaskOrThrow(request);
                attributes = applicationTaskExecutor.submitCompletable(task, attributes)
                        .join();
                log.info("File upload task result: {}", attributes);
                Files.deleteIfExists(request.path());
            } catch (InterruptedException e) {
                log.warn("File upload listener interrupted");
                Thread.currentThread().interrupt();
                stop();
            } catch (Exception e) {
                log.error("Failed to upload file", e);
            }
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        node.put("duration-ms", duration.toMillis());
        return node;
    }

    private @NotNull Task getTaskOrThrow(FileUploadRequest request) {
        String contentType = request.contentType();
        return switch (contentType) {
            case "text/csv" -> new CsvFileUploadTask(request, employeeFactory, employeeService, objectMapper);
            case APPLICATION_XML_VALUE ->
                    new XmlFileUploadTask(request, employeeFactory, employeeService, objectMapper);
            case APPLICATION_JSON_VALUE ->
                    new JsonFileUploadTask(request, employeeFactory, employeeService, objectMapper);
            default -> throw new IllegalArgumentException("Unsupported content type: %s".formatted(contentType));
        };
    }
}
