package com.github.mangila.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.service.EmployeeRestFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeRestFacade restFacade;

    @MockitoSpyBean
    private RestErrorHandler errorHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void beforeEach() {
        this.webTestClient = MockMvcWebTestClient.bindTo(mockMvc)
                .build();
    }

    @DisplayName("Should validate EmployeeId")
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
                .uri(builder -> builder
                        .path("/api/v1/employees/{employeeId}")
                        .build(employeeId))
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(errorHandler, times(1))
                .handleConstraintViolationException(any());
    }

    @DisplayName("Should validate CreateNewEmployeeRequest")
    @ParameterizedTest(name = "{0}")
    @MethodSource("notValidCreateNewEmployeeRequests")
    void shouldValidateCreateNewEmployeeRequest(CreateNewEmployeeRequest request) {
        webTestClient.post()
                .uri("/api/v1/employees")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(errorHandler, times(1))
                .handleMethodArgumentNotValidException(any());
    }

    private static Stream<CreateNewEmployeeRequest> notValidCreateNewEmployeeRequests() {
        var mapper = new ObjectMapper();
        var firstNameValidation = validateFirstName(mapper);
        var lastNameValidation = validateLastName(mapper);
        var salaryValidation = validateSalary(mapper);
        var attributesValidation = validateAttributes(mapper);
        return Stream.of(
                firstNameValidation,
                lastNameValidation,
                salaryValidation,
                attributesValidation
        ).flatMap(Function.identity());
    }

    private static Stream<CreateNewEmployeeRequest> validateAttributes(ObjectMapper mapper) {
        return Stream.of(
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .attributes(null)
                        .build()
        );
    }

    private static Stream<CreateNewEmployeeRequest> validateFirstName(ObjectMapper mapper) {
        return Stream.of(
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .firstName(null)
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .firstName("")
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .firstName("J")
                        .build()
        );
    }

    private static Stream<CreateNewEmployeeRequest> validateLastName(ObjectMapper mapper) {
        return Stream.of(
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .lastName(null)
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .lastName("")
                        .build(),
                ObjectFactoryUtil.createNewEmployeeRequestBuilder(mapper)
                        .lastName("D")
                        .build()
        );
    }

    private static Stream<CreateNewEmployeeRequest> validateSalary(ObjectMapper mapper) {
        return Stream.of(
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
                        .build()
        );
    }
}
