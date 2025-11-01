package com.github.mangila.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crudEmployee() throws IOException {
        URI location = create();
        EmployeeDto dto = read(location.toString());
        assertEmployeeDto(dto);
        dto = update(dto);
        assertUpdatedEmployeeDto(dto);
        delete(dto.employeeId());
        dto = read(location.toString());
        assertThat(dto.deleted()).isTrue();
    }

    private URI create() throws IOException {
        var request = ObjectFactoryUtil.createNewEmployeeRequest(objectMapper);
        return webTestClient.post()
                .uri("/api/v1/employee")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .exists("location")
                .expectBody()
                .isEmpty()
                .getResponseHeaders()
                .getLocation();
    }

    private EmployeeDto read(final String location) {
        return webTestClient.get()
                .uri(location)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();
    }

    private void assertEmployeeDto(EmployeeDto dto) {
        assertThat(dto)
                .isNotNull()
                .extracting(
                        EmployeeDto::firstName,
                        EmployeeDto::lastName,
                        EmployeeDto::salary,
                        EmployeeDto::deleted
                )
                .doesNotContainNull()
                .contains("John", "Doe", new BigDecimal("20000.12"), false);
        assertThat(dto.created())
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        assertThat(dto.modified())
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        // Assert the serialized JSON attributes
        // Assert JSON root key values
        assertThatJson(dto.attributes().toString())
                .isObject()
                .containsEntry("vegan", true)
                .containsEntry("pronouns", "he/him")
                // sssssssssssssnake case
                .containsEntry("substance_addiction", true)
                .containsEntry("notes", "subject is not approved for field duty, immediate suspension advised");
        // Assert nested object
        assertThatJson(dto.attributes().toString())
                .node("evaluation")
                .isObject()
                .containsEntry("medical", "FAIL")
                .containsEntry("physical", "FAIL")
                .containsEntry("psychological", "FAIL");
        // Assert array
        assertThatJson(dto.attributes().toString())
                .node("licenses")
                .isArray()
                .hasSize(3)
                .contains("PP7", "Klobb", "DD44 Dostovei");
    }

    private EmployeeDto update(EmployeeDto dto) {
        // someone got a raise ;)
        BigDecimal raise = dto.salary().add(new BigDecimal("20.00"));
        // and became a meat eater again :O
        ObjectNode updatedAttributes = dto.attributes()
                .put("vegan", false);
        var updateRequest = new UpdateEmployeeRequest(
                dto.employeeId(),
                dto.firstName(),
                dto.lastName(),
                raise,
                updatedAttributes
        );
        return webTestClient.put()
                .uri("/api/v1/employee")
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();
    }

    private void assertUpdatedEmployeeDto(EmployeeDto dto) {
        assertThat(dto)
                .isNotNull()
                .extracting(EmployeeDto::salary)
                .isEqualTo(new BigDecimal("20020.12"));
        assertThat(dto.created())
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        assertThat(dto.modified())
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        assertThatJson(dto.attributes().toString())
                .isObject()
                .containsEntry("vegan", false);
    }

    private void delete(String employeeId) {
        webTestClient.delete()
                .uri("/api/v1/employee/{employeeId}", employeeId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void findAllEmployeesByPage() {
    }
}