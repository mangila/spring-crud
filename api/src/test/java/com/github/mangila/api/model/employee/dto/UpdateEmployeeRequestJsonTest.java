package com.github.mangila.api.model.employee.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.FilePathUtil;
import com.github.mangila.api.config.JacksonConfig;
import com.github.mangila.api.model.employee.type.EmploymentActivity;
import com.github.mangila.api.model.employee.type.EmploymentStatus;
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
    private JacksonTester<UpdateEmployeeRequest> jsonTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize() throws IOException {
        // Setup
        String jsonString = FilePathUtil.readJsonFileToString("json/update-employee-request.json");
        // Act
        ObjectContent<UpdateEmployeeRequest> objectContent = jsonTester.parse(jsonString);
        // Assert
        objectContent.assertThat()
                .hasOnlyFields(
                        "employeeId",
                        "firstName",
                        "lastName",
                        "salary",
                        "employmentActivity",
                        "employmentStatus",
                        "attributes"
                )
                .hasFieldOrPropertyWithValue("employeeId", "EMP-JODO-00000000-0000-0000-0000-000000000000")
                .hasFieldOrPropertyWithValue("firstName", "Jane")
                .hasFieldOrPropertyWithValue("lastName", "Doe")
                .hasFieldOrPropertyWithValue("salary", new BigDecimal("20000.12"))
                .hasFieldOrPropertyWithValue("employmentActivity", EmploymentActivity.FULL_TIME)
                .hasFieldOrPropertyWithValue("employmentStatus", EmploymentStatus.ACTIVE)
                .hasFieldOrProperty("attributes");
        // Assert JSON attributes
        ObjectNode attributes = objectContent.getObject().attributes();
        String jsonAttributesString = attributes.toString();
        assertThatJson(jsonAttributesString)
                .and(UpdateEmployeeRequestJsonTest::assertAttributesRoot)
                .and(root -> root.node("evaluation").and(UpdateEmployeeRequestJsonTest::assertEvaluation))
                .and(root -> root.node("licenses").and(UpdateEmployeeRequestJsonTest::assertLicenses));
    }

    @Test
    void deserialize() throws IOException {
        // Setup
        UpdateEmployeeRequest request = EmployeeTestFactory.createUpdateEmployeeRequest(objectMapper);
        // Act
        JsonContent<UpdateEmployeeRequest> jsonContent = jsonTester.write(request);
        // Assert
        assertThatJson(jsonContent.getJson())
                .isObject()
                .containsOnlyKeys(
                        "employeeId",
                        "firstName",
                        "lastName",
                        "salary",
                        "employmentActivity",
                        "employmentStatus",
                        "attributes"
                )
                .hasSize(7)
                .containsEntry("employeeId", "EMP-JODO-00000000-0000-0000-0000-000000000000")
                .containsEntry("firstName", "Jane")
                .containsEntry("lastName", "Doe")
                .containsEntry("salary", "20000.12")
                .containsEntry("employmentActivity", "FULL_TIME")
                .containsEntry("employmentStatus", "ACTIVE")
                // Assert JSON attributes
                .node("attributes")
                .and(UpdateEmployeeRequestJsonTest::assertAttributesRoot)
                .and(root -> root.node("evaluation").and(UpdateEmployeeRequestJsonTest::assertEvaluation))
                .and(root -> root.node("licenses").and(UpdateEmployeeRequestJsonTest::assertLicenses));
    }

    private static void assertAttributesRoot(JsonAssert jsonAssert) {
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
        jsonAssert
                .isArray()
                .hasSize(3)
                .containsExactly(
                        "PP7",
                        "Widow Maker",
                        "R.Y.N.O"
                );
    }

    private static void assertEvaluation(JsonAssert jsonAssert) {
        jsonAssert
                .isObject()
                .hasSize(3)
                .containsExactly(
                        Map.entry("medical", "PASS"),
                        Map.entry("physical", "PASS"),
                        Map.entry("psychological", "PASS")
                );
    }
}