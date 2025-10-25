package com.github.mangila.app.controller;

import com.github.mangila.app.service.EmployeeRestFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

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

    @Test
    void abc() {
        webTestClient.get().uri("/api/v1/employee/1234")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

}
