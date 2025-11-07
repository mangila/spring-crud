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
class CreateNewEmployeeRequestJsonTest {

    @Autowired
    private JacksonTester<CreateNewEmployeeRequest> json;

    @Test
    void serialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/create-new-employee-request.json");
        ObjectContent<CreateNewEmployeeRequest> objectContent = json.parse(jsonString);
        objectContent.assertThat()
                .hasOnlyFields(
                        "firstName",
                        "lastName",
                        "salary",
                        "attributes"
                )
                .extracting(
                        CreateNewEmployeeRequest::firstName,
                        CreateNewEmployeeRequest::lastName,
                        CreateNewEmployeeRequest::salary
                )
                .doesNotContainNull()
                .containsExactly("John", "Doe", new BigDecimal("20000.12"));
        ObjectNode attributes = objectContent.getObject().attributes();
        String jsonAttributesString = attributes.toString();
        assertThatJson(jsonAttributesString)
                .and(CreateNewEmployeeRequestJsonTest::assertAttributes)
                .and(CreateNewEmployeeRequestJsonTest::assertEvaluation)
                .and(CreateNewEmployeeRequestJsonTest::assertLicenses);
    }

    @Test
    void deserialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/create-new-employee-request.json");
        CreateNewEmployeeRequest request = json.parse(jsonString)
                .getObject();
        JsonContent<CreateNewEmployeeRequest> jsonContent = json.write(request);
        // Assert JSON root keys
        assertThatJson(jsonContent.getJson())
                .isObject()
                .containsOnlyKeys(
                        "firstName",
                        "lastName",
                        "salary",
                        "attributes"
                )
                .hasSize(4)
                .containsEntry("firstName", "John")
                .containsEntry("lastName", "Doe")
                .containsEntry("salary", "20000.12")
                // Assert JSON attributes
                .node("attributes")
                .and(CreateNewEmployeeRequestJsonTest::assertAttributes)
                .and(CreateNewEmployeeRequestJsonTest::assertEvaluation)
                .and(CreateNewEmployeeRequestJsonTest::assertLicenses);

    }

    private static void assertAttributes(JsonAssert jsonAssert) {
        jsonAssert.isObject()
                .hasSize(7)
                .containsOnlyKeys("vegan",
                        "pronouns",
                        "substance_addiction",
                        "notes",
                        "secret_number",
                        "evaluation",
                        "licenses")
                .containsEntry("vegan", true)
                .containsEntry("pronouns", "he/him")
                .containsEntry("substance_addiction", true)
                .containsEntry("notes", "subject is not approved for field duty, immediate suspension advised")
                .containsEntry("secret_number", "123");
    }

    private static void assertLicenses(JsonAssert jsonAssert) {
        jsonAssert.node("licenses")
                .isArray()
                .hasSize(3)
                .containsExactly("PP7", "Klobb", "DD44 Dostovei");
    }

    private static void assertEvaluation(JsonAssert jsonAssert) {
        jsonAssert.node("evaluation")
                .isObject()
                .hasSize(3)
                .containsExactly(
                        Map.entry("medical", "FAIL"),
                        Map.entry("physical", "FAIL"),
                        Map.entry("psychological", "FAIL")
                );
    }
}