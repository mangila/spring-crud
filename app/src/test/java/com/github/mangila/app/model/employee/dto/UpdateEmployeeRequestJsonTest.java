package com.github.mangila.app.model.employee.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.FilePathUtil;
import com.github.mangila.app.config.JacksonConfig;
import net.javacrumbs.jsonunit.assertj.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@Import(JacksonConfig.class)
@JsonTest
class UpdateEmployeeRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateEmployeeRequest> json;

    @Test
    void serialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/update-employee-request.json");
        ObjectContent<UpdateEmployeeRequest> objectContent = json.parse(jsonString);
        objectContent.assertThat()
                .hasOnlyFields(
                        "employeeId",
                        "firstName",
                        "lastName",
                        "salary",
                        "attributes",
                        "deleted"
                )
                .extracting(UpdateEmployeeRequest::employeeId,
                        UpdateEmployeeRequest::firstName,
                        UpdateEmployeeRequest::lastName,
                        UpdateEmployeeRequest::salary,
                        UpdateEmployeeRequest::deleted)
                .doesNotContainNull()
                .containsExactly(
                        "EMP-JODO-00000000-0000-0000-0000-000000000000",
                        "Jane",
                        "Doe",
                        new BigDecimal("20000.12"),
                        false
                );
        // Assert JSON attributes
        ObjectNode attributes = objectContent.getObject().attributes();
        String jsonAttributesString = attributes.toString();
        assertThatJson(jsonAttributesString)
                .and(UpdateEmployeeRequestJsonTest::assertAttributes)
                .and(UpdateEmployeeRequestJsonTest::assertEvaluation)
                .and(UpdateEmployeeRequestJsonTest::assertLicenses);
    }

    @Test
    void deserialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/update-employee-request.json");
        UpdateEmployeeRequest request = json.parse(jsonString)
                .getObject();
        JsonContent<UpdateEmployeeRequest> jsonContent = json.write(request);
        // Assert JSON root keys
        assertThatJson(jsonContent.getJson())
                .isObject()
                .containsOnlyKeys(
                        "employeeId",
                        "firstName",
                        "lastName",
                        "salary",
                        "deleted",
                        "attributes"
                )
                .hasSize(6)
                .containsEntry("employeeId", "EMP-JODO-00000000-0000-0000-0000-000000000000")
                .containsEntry("firstName", "Jane")
                .containsEntry("lastName", "Doe")
                .containsEntry("salary", "20000.12")
                .containsEntry("deleted", false)
                // Assert JSON attributes
                .node("attributes")
                .and(UpdateEmployeeRequestJsonTest::assertAttributes)
                .and(UpdateEmployeeRequestJsonTest::assertEvaluation)
                .and(UpdateEmployeeRequestJsonTest::assertLicenses);
    }

    private static void assertAttributes(JsonAssert jsonAssert) {
        jsonAssert.isObject()
                .hasSize(7)
                .containsOnlyKeys("vegan",
                        "pronouns",
                        "chamber_of_secrets",
                        "favorite_book",
                        "lizard_people",
                        "evaluation",
                        "licenses")
                .containsEntry("vegan", true)
                .containsEntry("pronouns", "she/her")
                .containsEntry("chamber_of_secrets", true)
                .containsEntry("favorite_book", "No country, no home")
                .containsEntry("lizard_people", null);
    }

    private static void assertLicenses(JsonAssert jsonAssert) {
        jsonAssert.node("licenses")
                .isArray()
                .hasSize(3)
                .containsExactly("PP7", "Widow Maker", "R.Y.N.O");
    }

    private static void assertEvaluation(JsonAssert jsonAssert) {
        jsonAssert.node("evaluation")
                .isObject()
                .hasSize(3)
                .containsExactly(
                        Map.entry("medical", "PASS"),
                        Map.entry("physical", "PASS"),
                        Map.entry("psychological", "PASS")
                );
    }
}