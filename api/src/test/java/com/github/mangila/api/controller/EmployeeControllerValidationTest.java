package com.github.mangila.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.model.employee.type.EmploymentActivity;
import com.github.mangila.api.model.employee.type.EmploymentStatus;
import com.github.mangila.api.service.EmployeeRestFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

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

    /**
     * There is alot of Unicode names out there
     */
    @DisplayName("Should allow unicode names")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "Mary-Ann",
            "José-María",
            "François-Éric",
            "Søren-Åge",
            "Zoë-Chloé",
            "Björn-Þór",
            "Amélie-Renée",
            "Małgorzata-Anna",
            "Jürgen-André",
            "Stéphanie-Hélène"
    })
    void shouldAllowNames(String name) {
        var request = EmployeeTestFactory.createNewEmployeeRequestBuilder(mapper)
                .firstName(name)
                .lastName(name)
                .build();
        webTestClient.post()
                .uri("/api/v1/employees")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    /**
     * Nothing special just verifies that the errorHandler.handleNoHandlerFoundException
     * method is invoked.
     */
    @DisplayName("Should return 404")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "no resource here",
            "/api/v1/employees/",
            "/api/v1/employees/replay/",
    })
    void shouldReturnNotFound(String s) {
        webTestClient.get()
                .uri(s)
                .exchange()
                .expectStatus()
                .isNotFound();
        webTestClient.post()
                .uri(s)
                .exchange()
                .expectStatus()
                .isNotFound();
        webTestClient.put()
                .uri(s)
                .exchange()
                .expectStatus()
                .isNotFound();
        webTestClient.delete()
                .uri(s)
                .exchange()
                .expectStatus()
                .isNotFound();
        verify(errorHandler, times(4))
                .handleNoHandlerFoundException(any());
    }

    @DisplayName("Should validate EmployeeId")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "invalid", // Completely invalid format
            "EMP-ABC1234", // Wrong format (not 4 uppercase letters)
            "EMP-ABCD1234", // Missing UUID
            "EMP-123412345", // Numbers instead of letters
            "EMP-ABCD-invalid-uuid", // Invalid UUID
            "ABC-JODO-00000000-0000-0000-0000-000000000000", // Wrong prefix
            "EMP-jodo-00000000-0000-0000-0000-000000000000" // Lowercase letters
    })
    void shouldValidateEmployeeId(String s) {
        webTestClient.get()
                .uri(builder -> builder
                        .path("/api/v1/employees/{employeeId}")
                        .build(s))
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(errorHandler, times(1))
                .handleConstraintViolationException(any());
    }

    @DisplayName("Should validate Name")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "null", // re-assign to null
            "blank", // re-assign to blank
            "J",
            "-John",
            "12345",
            "John-",
            "Jo123",
    })
    void shouldValidateName(String name) {
        name = switch (name) {
            case "blank" -> "";
            case "null" -> null;
            default -> name;
        };
        var request = EmployeeTestFactory.createNewEmployeeRequestBuilder(mapper)
                .firstName(name)
                .lastName(name)
                .build();
        webTestClient.post()
                .uri("/api/v1/employees")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(errorHandler, times(1))
                .handleMethodArgumentNotValidException(any());
    }

    @DisplayName("Should validate Salary")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "null", // re-assign to null
            "0.00",
            "-1",
            "-1.00",
            "31.43534"
    })
    void shouldValidateSalary(String s) {
        BigDecimal salary = switch (s) {
            case "null" -> null;
            default -> new BigDecimal(s);
        };
        var request = EmployeeTestFactory.createNewEmployeeRequestBuilder(mapper)
                .salary(salary)
                .build();
        webTestClient.post()
                .uri("/api/v1/employees")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(errorHandler, times(1))
                .handleMethodArgumentNotValidException(any());
    }

    @DisplayName("Should validate Employee Activity")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "null", // re-assign to null
    })
    void shouldEmployeeActivity(String s) {
        EmploymentActivity activity = switch (s) {
            case "null" -> null;
            default -> EmploymentActivity.valueOf(s);
        };
        var request = EmployeeTestFactory.createNewEmployeeRequestBuilder(mapper)
                .employmentActivity(activity)
                .build();
        webTestClient.post()
                .uri("/api/v1/employees")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(errorHandler, times(1))
                .handleMethodArgumentNotValidException(any());
    }

    @DisplayName("Should validate Employee Status")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "null", // re-assign to null
    })
    void shouldEmployeeStatus(String s) {
        EmploymentStatus status = switch (s) {
            case "null" -> null;
            default -> EmploymentStatus.valueOf(s);
        };
        var request = EmployeeTestFactory.createNewEmployeeRequestBuilder(mapper)
                .employmentStatus(status)
                .build();
        webTestClient.post()
                .uri("/api/v1/employees")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(errorHandler, times(1))
                .handleMethodArgumentNotValidException(any());
    }

    @Test
    @DisplayName("Should validate Employee Attributes")
    void shouldEmployeeAttributes() {
        var request = EmployeeTestFactory.createNewEmployeeRequestBuilder(mapper)
                .nullAttributes()
                .build();
        webTestClient.post()
                .uri("/api/v1/employees")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest();
        verify(errorHandler, times(1))
                .handleMethodArgumentNotValidException(any());
    }

    //TODO: raw json input testing
}
