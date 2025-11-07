package com.github.mangila.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;

import java.io.IOException;

public class ObjectFactoryUtil {
    public static CreateNewEmployeeRequest createNewEmployeeRequest(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/create-new-employee-request.json");
        return objectMapper.readValue(jsonString, CreateNewEmployeeRequest.class);
    }

    public static UpdateEmployeeRequest createUpdateEmployeeRequest(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/update-employee-request.json");
        return objectMapper.readValue(jsonString, UpdateEmployeeRequest.class);
    }

    public static EmployeeId createFakeEmployeeId() {
        return new EmployeeId("EMP-JODO-00000000-0000-0000-0000-000000000000");
    }
}
