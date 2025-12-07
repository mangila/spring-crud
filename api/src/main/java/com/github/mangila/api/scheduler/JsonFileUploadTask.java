package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.FileUploadRequest;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.api.service.EmployeeFactory;
import com.github.mangila.api.service.EmployeeService;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class JsonFileUploadTask implements Task {

    private final FileUploadRequest fileUploadRequest;
    private final EmployeeFactory employeeFactory;
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    public JsonFileUploadTask(FileUploadRequest fileUploadRequest,
                              EmployeeFactory employeeFactory,
                              EmployeeService employeeService,
                              ObjectMapper objectMapper) {
        this.fileUploadRequest = fileUploadRequest;
        this.employeeFactory = employeeFactory;
        this.employeeService = employeeService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Transactional
    @Override
    public ObjectNode call() throws Exception {
        var node = objectMapper.createObjectNode();
        try (var channel = FileChannel.open(fileUploadRequest.path(), StandardOpenOption.READ)) {
            var buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer);
            var bytes = buffer.array();
            List<CreateNewEmployeeRequest> employees = objectMapper.readValue(bytes, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, CreateNewEmployeeRequest.class));
            for (var createRequest : employees) {
                Employee employee = employeeFactory.from(createRequest);
                employeeService.createNewEmployee(employee);
            }
            node.put("size", employees.size());
        }
        return node;
    }
}
