package com.github.mangila.app.model.employee.dto;

import com.github.mangila.app.FilePathUtil;
import com.github.mangila.app.config.JacksonConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JacksonConfig.class)
@JsonTest
class UpdateEmployeeRequestTest {

    @Autowired
    private JacksonTester<UpdateEmployeeRequest> json;

    @Test
    void serialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/employee.json");
        ObjectContent<UpdateEmployeeRequest> objectContent = json.parse(jsonString);
        // Assert the field names
        objectContent.assertThat()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrProperty("employeeId")
                .hasFieldOrProperty("firstName")
                .hasFieldOrProperty("lastName")
                .hasFieldOrProperty("salary")
                .hasFieldOrProperty("attributes");
    }

    @Test
    void deserialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/employee.json");
        UpdateEmployeeRequest request = json.parse(jsonString)
                .getObject();
        JsonContent<UpdateEmployeeRequest> jsonContent = json.write(request);
        // Assert the fields and make sure its right datatype
        assertThat(jsonContent)
                .hasJsonPathStringValue("@.employeeId")
                .hasJsonPathStringValue("@.firstName")
                .hasJsonPathStringValue("@.lastName")
                .hasJsonPathStringValue("@.salary")
                .hasJsonPathValue("@.attributes");
    }
}