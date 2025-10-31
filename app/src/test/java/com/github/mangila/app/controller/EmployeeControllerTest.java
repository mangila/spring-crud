package com.github.mangila.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.URI;

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

    @Test
    void findAllEmployeesByPage() {
    }

    @Test
    void updateEmployee() {

    }

    @Test
    void softDeleteEmployeeById() {
    }
}