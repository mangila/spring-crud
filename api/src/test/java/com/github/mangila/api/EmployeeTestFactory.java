package com.github.mangila.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.AuditMetadata;
import com.github.mangila.api.model.employee.domain.*;
import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.api.model.employee.dto.EmployeeDto;
import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import com.github.mangila.api.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.api.model.employee.entity.EmployeeEntity;
import com.github.mangila.api.model.employee.type.EmploymentActivity;
import com.github.mangila.api.model.employee.type.EmploymentStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

public class EmployeeTestFactory {

    public static CreateNewEmployeeRequest createNewEmployeeRequest(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/create-new-employee-request.json");
        return objectMapper.readValue(jsonString, CreateNewEmployeeRequest.class);
    }

    public static UpdateEmployeeRequest createUpdateEmployeeRequest(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/update-employee-request.json");
        return objectMapper.readValue(jsonString, UpdateEmployeeRequest.class);
    }

    public static EmployeeDto createEmployeeDto(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/employee-dto.json");
        return objectMapper.readValue(jsonString, EmployeeDto.class);
    }

    public static EmployeeEventDto createEmployeeEventDto(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/employee-event-dto.json");
        return objectMapper.readValue(jsonString, EmployeeEventDto.class);
    }

    public static EmployeeId createEmployeeId() {
        return new EmployeeId("EMP-JODO-00000000-0000-0000-0000-000000000000");
    }

    /**
     * This one derives from employee-dto.json data
     */
    public static EmployeeEntity createEmployeeEntity(ObjectMapper objectMapper) throws IOException {
        EmployeeDto dto = createEmployeeDto(objectMapper);
        var entity = new EmployeeEntity();
        entity.setId(dto.employeeId());
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setSalary(dto.salary());
        entity.setEmploymentActivity(dto.employmentActivity());
        entity.setEmploymentStatus(dto.employmentStatus());
        entity.setAttributes(dto.attributes());
        var auditMetadata = new AuditMetadata();
        auditMetadata.setCreated(Instant.from(dto.created()));
        auditMetadata.setModified(Instant.from(dto.modified()));
        auditMetadata.setDeleted(dto.deleted());
        entity.setAuditMetadata(auditMetadata);
        return entity;
    }

    public static EmployeeEntity createEmployeeEntity(ObjectMapper objectMapper, AuditMetadata metadata) throws IOException {
        EmployeeEntity entity = createEmployeeEntity(objectMapper);
        entity.setAuditMetadata(metadata);
        return entity;
    }

    /**
     * This one derives from employee-dto.json data
     */
    public static Employee createEmployee(ObjectMapper objectMapper) throws IOException {
        EmployeeDto dto = createEmployeeDto(objectMapper);
        return new Employee(
                new EmployeeId(dto.employeeId()),
                new EmployeeName(dto.firstName()),
                new EmployeeName(dto.lastName()),
                new EmployeeSalary(dto.salary()),
                dto.employmentActivity(),
                dto.employmentStatus(),
                new EmployeeAttributes(dto.attributes()),
                new EmployeeAudit(
                        dto.created().toInstant(),
                        dto.modified().toInstant(),
                        dto.deleted()
                )
        );
    }

    /**
     * This one derives from employee-dto.json data
     */
    public static Employee createEmployee(ObjectMapper objectMapper, EmployeeAudit employeeAudit) throws IOException {
        EmployeeDto dto = createEmployeeDto(objectMapper);
        return new Employee(
                new EmployeeId(dto.employeeId()),
                new EmployeeName(dto.firstName()),
                new EmployeeName(dto.lastName()),
                new EmployeeSalary(dto.salary()),
                dto.employmentActivity(),
                dto.employmentStatus(),
                new EmployeeAttributes(dto.attributes()),
                employeeAudit
        );
    }

    public static CreateNewEmployeeRequestBuilder createNewEmployeeRequestBuilder(ObjectMapper objectMapper) {
        return new CreateNewEmployeeRequestBuilder(objectMapper);
    }

    public static class CreateNewEmployeeRequestBuilder {

        private final ObjectMapper objectMapper;
        private String firstName = "John";
        private String lastName = "Doe";
        private BigDecimal salary = new BigDecimal("20000.12");
        private EmploymentActivity employmentActivity = EmploymentActivity.FULL_TIME;
        private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;
        private ObjectNode attributes;

        public CreateNewEmployeeRequestBuilder(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            this.attributes = objectMapper.createObjectNode();
        }

        public CreateNewEmployeeRequestBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public CreateNewEmployeeRequestBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public CreateNewEmployeeRequestBuilder salary(BigDecimal salary) {
            this.salary = salary;
            return this;
        }

        public CreateNewEmployeeRequestBuilder employmentActivity(EmploymentActivity employmentActivity) {
            this.employmentActivity = employmentActivity;
            return this;
        }

        public CreateNewEmployeeRequestBuilder employmentStatus(EmploymentStatus employmentStatus) {
            this.employmentStatus = employmentStatus;
            return this;
        }

        public CreateNewEmployeeRequestBuilder nullAttributes() {
            this.attributes = null;
            return this;
        }

        public CreateNewEmployeeRequestBuilder attributes(ObjectNode attributes) {
            this.attributes = attributes;
            return this;
        }

        public CreateNewEmployeeRequest build() {
            return new CreateNewEmployeeRequest(
                    firstName,
                    lastName,
                    salary,
                    employmentActivity,
                    employmentStatus,
                    attributes
            );
        }
    }

    public static UpdateEmployeeRequestBuilder createUpdateEmployeeRequestBuilder(ObjectMapper objectMapper) {
        return new UpdateEmployeeRequestBuilder(objectMapper);
    }

    public static class UpdateEmployeeRequestBuilder {

        private final ObjectMapper objectMapper;
        private String employeeId = "EMP-JODO-00000000-0000-0000-0000-000000000000";
        private String firstName = "John";
        private String lastName = "Doe";
        private BigDecimal salary = new BigDecimal("20000.12");
        private EmploymentActivity employmentActivity = EmploymentActivity.FULL_TIME;
        private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;
        private ObjectNode attributes;

        public UpdateEmployeeRequestBuilder(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            this.attributes = objectMapper.createObjectNode();
        }

        public UpdateEmployeeRequestBuilder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public UpdateEmployeeRequestBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UpdateEmployeeRequestBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UpdateEmployeeRequestBuilder salary(BigDecimal salary) {
            this.salary = salary;
            return this;
        }

        public UpdateEmployeeRequestBuilder employmentActivity(EmploymentActivity employmentActivity) {
            this.employmentActivity = employmentActivity;
            return this;
        }

        public UpdateEmployeeRequestBuilder employmentStatus(EmploymentStatus employmentStatus) {
            this.employmentStatus = employmentStatus;
            return this;
        }

        public UpdateEmployeeRequestBuilder attributes(ObjectNode attributes) {
            this.attributes = attributes;
            return this;
        }

        public UpdateEmployeeRequest build() {
            return new UpdateEmployeeRequest(
                    employeeId,
                    firstName,
                    lastName,
                    salary,
                    employmentActivity,
                    employmentStatus,
                    attributes
            );
        }
    }

}
