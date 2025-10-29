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

@JsonTest
class UpdateEmployeeRequestTest {

    @Autowired
    private JacksonTester<UpdateEmployeeRequest> json;

    @Test
    void serialize() throws IOException {
        // language=JSON
        String jsonString = """
                {
                "employeeId": "EMP-JADO-00000000-0000-0000-0000-000000000000",
                "firstName": "Jane",
                "lastName": "Doe",
                "salary": 53453.99,
                "attributes": {
                "management" : true
                }
                }
                """;
        json.parse(jsonString)
                .assertThat()
                .hasNoNullFieldsOrProperties()
                .extracting(
                        UpdateEmployeeRequest::employeeId,
                        UpdateEmployeeRequest::firstName,
                        UpdateEmployeeRequest::lastName,
                        UpdateEmployeeRequest::salary
                )
                .doesNotContainNull()
                .contains("EMP-JADO-00000000-0000-0000-0000-000000000000",
                        "Jane",
                        "Doe",
                        new BigDecimal("53453.99"));

        ObjectNode attr = json.parse(jsonString)
                .getObject()
                .attributes();
        assertThat(attr.get("management").asBoolean())
                .isTrue();
    }

    @Test
    void deserialize() throws IOException {
        var attr = new ObjectMapper().createObjectNode()
                .put("eligible_for_rehire", false);
        var request = new UpdateEmployeeRequest(
                "EMP-JODO-00000000-0000-0000-0000-000000000000",
                "John",
                "Doe",
                new BigDecimal("45.33"),
                attr);
        var jsonContent = json.write(request);
        assertThat(jsonContent)
                .hasJsonPathStringValue("@.firstName")
                .hasJsonPathStringValue("@.lastName")
                .hasJsonPathNumberValue("@.salary")
                .hasJsonPathBooleanValue("@.attributes.eligible_for_rehire");
    }
}