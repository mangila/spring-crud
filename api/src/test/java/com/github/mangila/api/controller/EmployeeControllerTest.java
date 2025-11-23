package com.github.mangila.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.ClockTestConfig;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.model.employee.dto.EmployeeDto;
import com.github.mangila.api.model.employee.type.EmploymentActivity;
import com.github.mangila.api.model.employee.type.EmploymentStatus;
import org.junit.jupiter.api.DisplayName;
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
import java.time.ZonedDateTime;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Import(
        {
                PostgresTestContainerConfiguration.class,
                ClockTestConfig.class,
        }
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClockTestConfig.TestClock clock;

    @Test
    @DisplayName("Should C.R.U.D Employee")
    void crudEmployee() throws IOException {
        URI location = create();
        EmployeeDto dto = read(location.toString());
        assertNewEmployeeDto(dto);
        // Advance time by 3 hours to simulate some timelapse
        clock.advanceTime(Duration.ofHours(3));
        dto = update(dto);
        assertUpdatedEmployeeDto(dto);
        delete(dto.employeeId());
        dto = read(location.toString());
        assertThat(dto.deleted()).isTrue();
    }

    private URI create() throws IOException {
        var request = EmployeeTestFactory.createNewEmployeeRequest(objectMapper);
        return webTestClient.post()
                .uri("/api/v1/employees")
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

    private void assertNewEmployeeDto(EmployeeDto dto) {
        assertThat(dto)
                .isNotNull()
                .hasOnlyFields(
                        "employeeId",
                        "firstName",
                        "lastName",
                        "salary",
                        "employmentActivity",
                        "employmentStatus",
                        "attributes",
                        "created",
                        "modified",
                        "deleted"
                )
                .hasFieldOrPropertyWithValue("employeeId", dto.employeeId())
                .hasFieldOrPropertyWithValue("firstName", "John")
                .hasFieldOrPropertyWithValue("lastName", "Doe")
                .hasFieldOrPropertyWithValue("salary", new BigDecimal("20000.12"))
                .hasFieldOrPropertyWithValue("employmentActivity", EmploymentActivity.FULL_TIME)
                .hasFieldOrPropertyWithValue("employmentStatus", EmploymentStatus.ACTIVE)
                .hasFieldOrPropertyWithValue("deleted", false);
        // Verify created and modified dates are somewhere in the last 5 seconds
        assertThat(dto.created())
                .isCloseTo(ZonedDateTime.now(), within(Duration.ofSeconds(5)));
        assertThat(dto.modified())
                .isCloseTo(ZonedDateTime.now(), within(Duration.ofSeconds(5)));
        // Assert JSON attributes
        var jsonAttributes = dto.attributes().toString();
        assertThatJson(jsonAttributes)
                .isObject()
                .containsOnlyKeys(
                        "vegan",
                        "pronouns",
                        "lizard_people",
                        "licenses",
                        "evaluation"
                )
                .hasSize(5)
                .containsEntry("vegan", true)
                .containsEntry("pronouns", "he/him")
                .containsEntry("lizard_people", null)
                .node("") // just to be able to walk nodes on the JSON tree, maybe there is a better way to do this
                .and(jsonAssert -> jsonAssert.node("licenses")
                        .isArray()
                        .hasSize(2)
                        .containsExactly(
                                "PP7",
                                "Klobb"
                        ))
                .and(jsonAssert -> jsonAssert.node("evaluation")
                        .isObject()
                        .hasSize(2)
                        .containsExactly(
                                Map.entry("medical", "FAIL"),
                                Map.entry("physical", "FAIL")
                        ));
    }

    private EmployeeDto update(EmployeeDto dto) {
        var updateRequest = EmployeeTestFactory.createUpdateEmployeeRequestBuilder(objectMapper)
                .employeeId(dto.employeeId())
                .salary(dto.salary().add(new BigDecimal("20.00")))
                .attributes(dto.attributes().put("vegan", false))
                .build();
        return webTestClient.put()
                .uri("/api/v1/employees")
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
                .hasFieldOrPropertyWithValue("salary", new BigDecimal("20020.12"));
        assertThat(dto.created())
                .isCloseTo(ZonedDateTime.now(), within(Duration.ofSeconds(5)));
        assertThat(dto.modified())
                .isCloseTo(ZonedDateTime.now().plusHours(3), within(Duration.ofSeconds(5)));
        assertThatJson(dto.attributes().toString())
                .isObject()
                .containsEntry("vegan", false);
    }

    private void delete(String employeeId) {
        webTestClient.delete()
                .uri("/api/v1/employees/{employeeId}", employeeId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void findAllEmployeesByPage() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void replay() {
        assertThat(1 + 1).isEqualTo(3);
    }
}