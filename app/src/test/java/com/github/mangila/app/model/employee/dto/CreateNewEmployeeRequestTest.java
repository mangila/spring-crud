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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
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
        // Assert the field names
        objectContent.assertThat()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrProperty("firstName")
                .hasFieldOrProperty("lastName")
                .hasFieldOrProperty("salary")
                .hasFieldOrProperty("attributes");
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
                .hasJsonPathValue("@.attributes");
    }
}