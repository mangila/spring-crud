package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.FileUploadRequest;
import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.api.model.employee.type.EmploymentActivity;
import com.github.mangila.api.model.employee.type.EmploymentStatus;
import com.github.mangila.api.service.EmployeeFactory;
import com.github.mangila.api.service.EmployeeService;
import io.github.mangila.ensure4j.Ensure;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;
import java.nio.file.Files;

public class CsvFileUploadTask implements Task {

    private final FileUploadRequest fileUploadRequest;
    private final EmployeeFactory employeeFactory;
    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    public CsvFileUploadTask(FileUploadRequest fileUploadRequest, EmployeeFactory employeeFactory, EmployeeService employeeService, ObjectMapper objectMapper) {
        this.fileUploadRequest = fileUploadRequest;
        this.employeeFactory = employeeFactory;
        this.employeeService = employeeService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return fileUploadRequest.fileId();
    }

    @Override
    public ObjectNode call() throws Exception {
        var node = objectMapper.createObjectNode();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(CreateNewEmployeeRequest.CSV_HEADERS)
                .setSkipHeaderRecord(true)
                .get();
        try (CSVParser parser = csvFormat.parse(Files.newBufferedReader(fileUploadRequest.path()));
             ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        ) {
            Validator validator = factory.getValidator();
            for (CSVRecord record : parser) {
                CreateNewEmployeeRequest request = parse(record);
                var violations = validator.validate(request);
                if (!violations.isEmpty()) {
                    for (var violation : violations) {
                        node.put(violation.getPropertyPath().toString(), violation.getMessage());
                    }
                } else {
                    employeeService.createNewEmployee(employeeFactory.from(request));
                }
            }
        }
        return node;
    }

    private CreateNewEmployeeRequest parse(CSVRecord record) throws JsonProcessingException {
        for (String header : CreateNewEmployeeRequest.CSV_HEADERS) {
            Ensure.isTrue(record.isMapped(header), "CSV record does not contain '%s' column".formatted(header));
        }
        String firstName = record.get("firstName");
        String lastName = record.get("lastName");
        BigDecimal salary = new BigDecimal(record.get("salary"));
        EmploymentActivity employmentActivity = EmploymentActivity.valueOf(record.get("employmentActivity"));
        EmploymentStatus status = EmploymentStatus.valueOf(record.get("employmentStatus"));
        ObjectNode attributes = (ObjectNode) objectMapper.readTree(record.get("attributes"));
        return new CreateNewEmployeeRequest(firstName, lastName, salary, employmentActivity, status, attributes);
    }
}
