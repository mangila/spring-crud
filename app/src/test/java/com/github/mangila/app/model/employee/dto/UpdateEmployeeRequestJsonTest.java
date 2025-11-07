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
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JacksonConfig.class)
@JsonTest
class UpdateEmployeeRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateEmployeeRequest> json;

    @Test
    void serialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/update-employee-request.json");
        ObjectContent<UpdateEmployeeRequest> objectContent = json.parse(jsonString);
        // Assert the field names
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
    }

    @Test
    void deserialize() throws IOException {
        String jsonString = FilePathUtil.readJsonFileToString("json/update-employee-request.json");
        UpdateEmployeeRequest request = json.parse(jsonString)
                .getObject();
        JsonContent<UpdateEmployeeRequest> jsonContent = json.write(request);
        // Assert the fields and make sure its right datatype
        assertThat(jsonContent)
                .hasJsonPathStringValue("@.employeeId")
                .hasJsonPathStringValue("@.firstName")
                .hasJsonPathStringValue("@.lastName")
                .hasJsonPathStringValue("@.salary")
                .hasJsonPathValue("@.attributes")
                .hasJsonPathBooleanValue("@.deleted");
    }
}