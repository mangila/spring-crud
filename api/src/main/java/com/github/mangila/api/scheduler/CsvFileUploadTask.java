package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.FileUploadRequest;
import com.github.mangila.api.repository.EmployeeJpaRepository;

public class CsvFileUploadTask implements Task {

    private final FileUploadRequest fileUploadRequest;
    private final EmployeeJpaRepository employeeJpaRepository;
    private final ObjectMapper objectMapper;

    public CsvFileUploadTask(FileUploadRequest fileUploadRequest,
                             EmployeeJpaRepository employeeJpaRepository,
                             ObjectMapper objectMapper) {
        this.fileUploadRequest = fileUploadRequest;
        this.employeeJpaRepository = employeeJpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ObjectNode call() throws Exception {
        var node = objectMapper.createObjectNode();
        return node;
    }
}
