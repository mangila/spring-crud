package com.github.mangila.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findEmployeeById() {
    }

    @Test
    void findAllEmployeesByPage() {
    }

    @Test
    void createNewEmployee() {
        var attributes = objectMapper.createObjectNode()
                .put("vegan", false)
                .put("pronouns", "prefer to not answer");
        webTestClient.post()
                .uri("/api/v1/employee")
                .bodyValue(new CreateNewEmployeeRequest("John", "Doe", new BigDecimal(200), attributes))
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void softDeleteEmployeeById() {
    }
}