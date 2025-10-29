package com.github.mangila.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.service.EmployeeRestFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.math.BigDecimal;
import java.util.stream.Stream;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeRestFacade restFacade;

    private WebTestClient webTestClient;

    @BeforeEach
    void beforeEach() {
        this.webTestClient = MockMvcWebTestClient.bindTo(mockMvc)
                .build();
    }

    @ParameterizedTest(name = "Employee id {0} should be invalid")
    @ValueSource(strings = {
            "invalid",                              // Completely invalid format
            "EMP-ABC1234",                         // Wrong format (not 4 uppercase letters)
            "EMP-ABCD1234",                        // Missing UUID
            "EMP-123412345",                       // Numbers instead of letters
            "EMP-ABCD-invalid-uuid",               // Invalid UUID
            "XXX-ABCD-550e8400-e29b-41d4-a716",   // Wrong prefix
            "EMP-abcd-550e8400-e29b-41d4-a716"    // Lowercase letters
    })
    void shouldValidateEmployeeId(String employeeId) {
        webTestClient.get()
                .uri("/api/v1/employee/" + employeeId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    static Stream<CreateNewEmployeeRequest> notValidCreateNewEmployeeRequests() {
        var mapper = new ObjectMapper();
        return Stream.of(
                // validate firstname
                new CreateNewEmployeeRequest(null, "Doe", BigDecimal.valueOf(50000), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("", "Doe", BigDecimal.valueOf(50000), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("J", "Doe", BigDecimal.valueOf(50000), mapper.createObjectNode()),
                // validate lastname
                new CreateNewEmployeeRequest("John", "", BigDecimal.valueOf(50000), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("John", "D", BigDecimal.valueOf(50000), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("John", null, BigDecimal.valueOf(50000), mapper.createObjectNode()),
                // validate salary
                new CreateNewEmployeeRequest("John", "Doe", BigDecimal.valueOf(1000000), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("John", "Doe", BigDecimal.valueOf(0), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("John", "Doe", BigDecimal.valueOf(-1), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("John", "Doe", BigDecimal.valueOf(-1.00), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("John", "Doe", BigDecimal.valueOf(30.123456), mapper.createObjectNode()),
                new CreateNewEmployeeRequest("John", "Doe", null, mapper.createObjectNode()),
                // validate attributes
                new CreateNewEmployeeRequest("John", "Doe", BigDecimal.valueOf(200), null)
        );
    }

    @ParameterizedTest(name = "Employee request {0} should be invalid")
    @MethodSource("notValidCreateNewEmployeeRequests")
    void shouldValidateCreateNewEmployeeRequest(CreateNewEmployeeRequest request) {
        webTestClient.post()
                .uri("/api/v1/employee")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
