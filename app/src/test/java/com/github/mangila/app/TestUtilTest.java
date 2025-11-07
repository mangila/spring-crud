package com.github.mangila.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

/**
 * Responsible for testing the {@link FilePathUtil} {@link ObjectFactoryUtil} classes.
 * <br>
 * Test the Testutil :P...
 */
class TestUtilTest {

    @Test
    @DisplayName("(In)sanity check for exact matching json resources/json/create-new-update-employee-request.json")
    void shouldMatch_createNewEmployeeJson() throws IOException {
        // language=JSON
        final String jsonString = """
                  {
                  "firstName": "John",
                  "lastName": "Doe",
                  "salary": "20000.12",
                  "attributes": {
                    "vegan": true,
                    "pronouns": "he/him",
                    "licenses": [
                      "PP7",
                      "Klobb",
                      "DD44 Dostovei"
                    ],
                    "evaluation": {
                      "medical": "FAIL",
                      "physical": "FAIL",
                      "psychological": "FAIL"
                    },
                    "substance_addiction": true,
                    "secret_number": "123",
                    "notes": "subject is not approved for field duty, immediate suspension advised"
                  }
                }
                """;
        assertThatJson(jsonString)
                .isEqualTo(FilePathUtil.readJsonFileToString("json/create-new-employee-request.json"));
    }

    @Test
    @DisplayName("(In)sanity check for exact matching json resources/json/update-employee-request.json")
    void shouldMatch_employeeJson() throws IOException {
        // language=JSON
        final String jsonString = """
                 {
                  "employeeId": "EMP-JODO-00000000-0000-0000-0000-000000000000",
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "salary": "20000.12",
                  "attributes": {
                    "vegan": true,
                    "pronouns": "she/her",
                    "licenses": [
                      "PP7",
                      "Widow Maker",
                      "R.Y.N.O"
                    ],
                    "chamber_of_secrets": true,
                    "favorite_book": "No country, no home",
                    "lizard_people": null
                  },
                  "deleted": false
                }
                """;
        assertThatJson(jsonString)
                .isEqualTo(FilePathUtil.readJsonFileToString("json/update-employee-request.json"));
    }

}
