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
class CreateNewEmployeeRequestTest {

    @Autowired
    private JacksonTester<CreateNewEmployeeRequest> json;

    @Test
    void serialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/create-new-employee.json");
        ObjectContent<CreateNewEmployeeRequest> objectContent = json.parse(jsonString);
        // Assert the serialized JSON content
        objectContent.assertThat()
                .hasNoNullFieldsOrProperties()
                .extracting(CreateNewEmployeeRequest::firstName,
                        CreateNewEmployeeRequest::lastName,
                        CreateNewEmployeeRequest::salary)
                .doesNotContainNull()
                .contains("John", "Doe", new BigDecimal("20000.12"));
        // Assert the serialized JSON attributes
        ObjectNode attr = objectContent.getObject()
                .attributes();
        assertThat(attr.get("vegan").asBoolean())
                .isTrue();
        assertThat(attr.get("pronouns").asText())
                .isEqualTo("he/him");
    }

    @Test
    void deserialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/create-new-employee.json");
        CreateNewEmployeeRequest request = json.parse(jsonString)
                .getObject();
        JsonContent<CreateNewEmployeeRequest> jsonContent = json.write(request);
        // Assert the fields and make sure its right datatype
        assertThat(jsonContent)
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