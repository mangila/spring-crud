package com.github.mangila.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.AuditMetadata;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.model.employee.type.EmploymentActivity;
import com.github.mangila.app.model.employee.type.EmploymentStatus;
import com.github.mangila.app.model.outbox.OutboxEntity;
import com.github.mangila.app.model.outbox.OutboxEventStatus;
import com.github.mangila.app.model.task.TaskExecutionEntity;
import com.github.mangila.app.model.task.TaskExecutionStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class ObjectFactoryUtil {
    public static CreateNewEmployeeRequest createNewEmployeeRequest(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/create-new-employee-request.json");
        return objectMapper.readValue(jsonString, CreateNewEmployeeRequest.class);
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

        public CreateNewEmployeeRequestBuilder attributes(Map<String, Object> attributes) {
            if (attributes == null) {
                this.attributes = null;
                return this;
            }
            this.attributes = objectMapper.valueToTree(attributes);
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

    public static UpdateEmployeeRequest createUpdateEmployeeRequest(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/update-employee-request.json");
        return objectMapper.readValue(jsonString, UpdateEmployeeRequest.class);
    }

    public static EmployeeId createEmployeeId() {
        return new EmployeeId("EMP-JODO-00000000-0000-0000-0000-000000000000");
    }

    public static EmployeeEntity createEmployeeEntity(ObjectMapper objectMapper) throws IOException {
        var updateRequest = createUpdateEmployeeRequest(objectMapper);
        var entity = new EmployeeEntity();
        entity.setId(updateRequest.employeeId());
        entity.setFirstName(updateRequest.firstName());
        entity.setLastName(updateRequest.lastName());
        entity.setSalary(updateRequest.salary());
        entity.setEmploymentActivity(updateRequest.employmentActivity());
        entity.setEmploymentStatus(updateRequest.employmentStatus());
        entity.setAttributes(updateRequest.attributes());
        entity.setAuditMetadata(AuditMetadata.EMPTY);
        return entity;
    }

    public static OutboxEntity createOutboxEntity(OutboxEventStatus status, ObjectMapper objectMapper) {
        var entity = new OutboxEntity();
        entity.setEventName("test");
        entity.setStatus(status);
        entity.setPayload(objectMapper.createObjectNode());
        entity.setAuditMetadata(AuditMetadata.EMPTY);
        return entity;
    }

    public static TaskExecutionEntity createTaskExecutionEntity(String taskName, TaskExecutionStatus status) {
        return new TaskExecutionEntity(taskName, status, null);
    }
}
