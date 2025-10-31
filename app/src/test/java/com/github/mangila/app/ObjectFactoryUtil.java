package com.github.mangila.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;

import java.io.IOException;

public final class ObjectFactoryUtil {
    public static CreateNewEmployeeRequest createNewEmployeeRequest(ObjectMapper objectMapper) throws IOException {
        var jsonString = FilePathUtil.readJsonFileToString("/json/create-new-employee.json");
        return objectMapper.readValue(jsonString, CreateNewEmployeeRequest.class);
    }
}
