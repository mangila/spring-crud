package com.github.mangila.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.service.EmployeeRestFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.math.BigDecimal;
import java.util.Map;
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

    @ParameterizedTest(name = "{0}")
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
                .uri("/api/v1/employees/" + employeeId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    static Stream<CreateNewEmployeeRequest> notValidCreateNewEmployeeRequests() {
        var mapper = new ObjectMapper();
        return Stream.of(
                // validate firstname
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .firstName(null)
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .firstName("")
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .firstName("J")
                        .build(),
                // validate lastname
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .lastName(null)
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .lastName("")
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .lastName("D")
                        .build(),
                // validate salary
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .salary(null)
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .salary(BigDecimal.valueOf(1000000))
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .salary(BigDecimal.valueOf(0))
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .salary(BigDecimal.valueOf(-1))
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .salary(BigDecimal.valueOf(-1.00))
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .salary(BigDecimal.valueOf(31.231323223))
                        .build(),
                // validate attributes
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .attributes(null)
                        .build()
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("notValidCreateNewEmployeeRequests")
    void shouldValidateCreateNewEmployeeRequest(CreateNewEmployeeRequest request) {
        webTestClient.post()
                .uri("/api/v1/employees")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
