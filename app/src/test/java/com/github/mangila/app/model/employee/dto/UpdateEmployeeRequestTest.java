package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.math.BigDecimal;

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
        // Assert the serialized JSON content
        objectContent.assertThat()
                .hasNoNullFieldsOrProperties()
                .extracting(
                        UpdateEmployeeRequest::employeeId,
                        UpdateEmployeeRequest::firstName,
                        UpdateEmployeeRequest::lastName,
                        UpdateEmployeeRequest::salary
                )
                .doesNotContainNull()
                .contains("EMP-JODO-00000000-0000-0000-0000-000000000000",
                        "John",
                        "Doe",
                        new BigDecimal("20000.12"));
        // Assert the attributes
        ObjectNode attr = json.parse(jsonString)
                .getObject()
                .attributes();
        assertThat(attr.get("vegan").asBoolean())
                .isTrue();
        assertThat(attr.get("pronouns").asText())
                .isEqualTo("he/him");
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
                .hasJsonPathBooleanValue("@.attributes.vegan")
                .hasJsonPathStringValue("@.attributes.pronouns");
        // Assert the big decimal value
        assertThat(jsonContent)
                .extractingJsonPathStringValue("@.salary")
                .isEqualTo("20000.12");

    }
}