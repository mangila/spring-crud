package com.github.mangila.app.controller;

import com.github.mangila.app.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void findEmployeeById() {

    }

    @Test
    void findAllEmployeesByPage() {
    }

    @Test
    void createNewEmployee() {
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void softDeleteEmployeeById() {
    }
}