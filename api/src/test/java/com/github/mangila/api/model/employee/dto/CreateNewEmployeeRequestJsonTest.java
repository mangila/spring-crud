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
class CreateNewEmployeeRequestJsonTest {

    @Autowired
    private JacksonTester<CreateNewEmployeeRequest> jsonTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize() throws IOException {
        // Setup
        String jsonString = FilePathUtil.readJsonFileToString("json/create-new-employee-request.json");
        // Act
        ObjectContent<CreateNewEmployeeRequest> objectContent = jsonTester.parse(jsonString);
        // Assert
        objectContent.assertThat()
                .hasOnlyFields(
                        "firstName",
                        "lastName",
                        "salary",
                        "employmentActivity",
                        "employmentStatus",
                        "attributes"
                )
                .hasFieldOrPropertyWithValue("firstName", "John")
                .hasFieldOrPropertyWithValue("lastName", "Doe")
                .hasFieldOrPropertyWithValue("salary", new BigDecimal("20000.12"))
                .hasFieldOrPropertyWithValue("employmentActivity", EmploymentActivity.FULL_TIME)
                .hasFieldOrPropertyWithValue("employmentStatus", EmploymentStatus.ACTIVE)
                .hasFieldOrProperty("attributes");
        ObjectNode attributes = objectContent.getObject().attributes();
        String jsonAttributesString = attributes.toString();
        assertThatJson(jsonAttributesString)
                .and(CreateNewEmployeeRequestJsonTest::assertAttributesRoot)
                .and(root -> root.node("evaluation").and(CreateNewEmployeeRequestJsonTest::assertEvaluation))
                .and(root -> root.node("licenses").and(CreateNewEmployeeRequestJsonTest::assertLicenses));
    }

    @Test
    void deserialize() throws IOException {
        // Setup
        CreateNewEmployeeRequest request = EmployeeTestFactory.createNewEmployeeRequest(objectMapper);
        // Act
        JsonContent<CreateNewEmployeeRequest> jsonContent = jsonTester.write(request);
        // Assert
        assertThatJson(jsonContent.getJson())
                .isObject()
                .containsOnlyKeys(
                        "firstName",
                        "lastName",
                        "salary",
                        "employmentActivity",
                        "employmentStatus",
                        "attributes"
                )
                .hasSize(6)
                .containsEntry("firstName", "John")
                .containsEntry("lastName", "Doe")
                .containsEntry("salary", "20000.12")
                .containsEntry("employmentActivity", "FULL_TIME")
                .containsEntry("employmentStatus", "ACTIVE")
                .node("attributes")
                .and(CreateNewEmployeeRequestJsonTest::assertAttributesRoot)
                .and(root -> root.node("evaluation").and(CreateNewEmployeeRequestJsonTest::assertEvaluation))
                .and(root -> root.node("licenses").and(CreateNewEmployeeRequestJsonTest::assertLicenses));
    }

    private static void assertAttributesRoot(JsonAssert jsonAssert) {
        jsonAssert.isObject()
                .hasSize(4)
                .containsOnlyKeys(
                        "vegan",
                        "pronouns",
                        "evaluation",
                        "licenses"
                )
                .containsEntry("vegan", true)
                .containsEntry("pronouns", "he/him");
    }

    private static void assertLicenses(JsonAssert jsonAssert) {
        jsonAssert.isArray()
                .hasSize(3)
                .containsExactly(
                        "PP7",
                        "Klobb",
                        "DD44 Dostovei"
                );
    }

    private static void assertEvaluation(JsonAssert jsonAssert) {
        jsonAssert
                .isObject()
                .hasSize(3)
                .containsExactly(
                        Map.entry("medical", "FAIL"),
                        Map.entry("physical", "FAIL"),
                        Map.entry("psychological", "FAIL")
                );
    }
}