package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the DTO JSON serialization and deserialization.
 * Very useful if Jackson has been configured with some extras.
 * <p>
 * e.g Timestamps, Enumeration etc
 */
@JsonTest
class CreateNewEmployeeRequestTest {

    @Autowired
    private JacksonTester<CreateNewEmployeeRequest> json;

    @Test
    void serialize() throws IOException {
        // language=JSON
        String jsonString = """
                {
                "firstName": "John",
                "lastName": "Doe",
                "salary": 20000.12,
                "attributes": {
                  "vegan": true,
                  "pronouns": "he/him"
                }
                }
                """;
        json.parse(jsonString)
                .assertThat()
                .hasNoNullFieldsOrProperties()
                .extracting(CreateNewEmployeeRequest::firstName,
                        CreateNewEmployeeRequest::lastName,
                        CreateNewEmployeeRequest::salary)
                .doesNotContainNull()
                .contains("John", "Doe", new BigDecimal("20000.12"));
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
        var attr = new ObjectMapper().createObjectNode()
                .put("vegan", true)
                .put("pronouns", "she/her");
        var request = new CreateNewEmployeeRequest("Jane", "Doe", new BigDecimal("45.33"), attr);
        var jsonContent = json.write(request);
        assertThat(jsonContent)
                .hasJsonPathStringValue("@.firstName")
                .hasJsonPathStringValue("@.lastName")
                .hasJsonPathNumberValue("@.salary")
                .hasJsonPathBooleanValue("@.attributes.vegan")
                .hasJsonPathStringValue("@.attributes.pronouns");

    }
}