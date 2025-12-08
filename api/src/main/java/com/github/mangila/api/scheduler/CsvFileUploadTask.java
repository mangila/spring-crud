package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.FileUploadRequest;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.api.model.employee.type.EmploymentActivity;
import com.github.mangila.api.model.employee.type.EmploymentStatus;
import com.github.mangila.api.service.EmployeeFactory;
import com.github.mangila.api.service.EmployeeService;
import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.EnsureException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;

@Slf4j
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

    /**
     * CSV processing with the commons csv library.
     * <br>
     * Ensures all CSV records have the required headers and not have corrupted data or else it won't insert new employees
     */
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
            int errorCount = 0;
            Validator validator = factory.getValidator();
            var requests = new ArrayList<CreateNewEmployeeRequest>();
            try {
                for (CSVRecord record : parser) {
                    CreateNewEmployeeRequest request = parse(record);
                    var violations = validator.validate(request);
                    if (!violations.isEmpty()) {
                        for (var violation : violations) {
                            var nodeName = violation.getPropertyPath()
                                    .toString()
                                    .concat("-")
                                    .concat(String.valueOf(record.getRecordNumber()));
                            node.put(nodeName, violation.getMessage());
                        }
                        errorCount = errorCount + 1;
                    } else {
                        requests.add(request);
                    }
                }
                node.put("errorCount", errorCount);
                node.put("size", requests.size());
                log.warn("Error count: {} - Won't process CSV file - {}", errorCount, fileUploadRequest.path());
                if (errorCount == 0) {
                    for (CreateNewEmployeeRequest request : requests) {
                        Employee employee = employeeFactory.from(request);
                        employeeService.createNewEmployee(employee);
                    }
                    Files.deleteIfExists(fileUploadRequest.path());
                }
            } catch (Exception e) {
                node.put("error", e.getMessage());
            }
        }
        return node;
    }

    private CreateNewEmployeeRequest parse(CSVRecord record) throws JsonProcessingException, EnsureException {
        for (String header : CreateNewEmployeeRequest.CSV_HEADERS) {
            Ensure.isTrue(record.isMapped(header), "CSV record (%d) does not contain '%s' column".formatted(record.getRecordNumber(), header));
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
