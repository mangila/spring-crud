package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.FileUploadRequest;
import com.github.mangila.api.repository.EmployeeJpaRepository;
import com.github.mangila.api.service.EmployeeFactory;
import com.github.mangila.api.service.EmployeeService;

public class XmlFileUploadTask implements Task {

    private final FileUploadRequest fileUploadRequest;
    private final EmployeeFactory employeeFactory;
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    public XmlFileUploadTask(FileUploadRequest fileUploadRequest, EmployeeFactory employeeFactory, EmployeeService employeeService, ObjectMapper objectMapper) {
        this.fileUploadRequest = fileUploadRequest;
        this.employeeFactory = employeeFactory;
        this.employeeService = employeeService;
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
