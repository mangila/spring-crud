package com.github.mangila.app.service;

import com.github.mangila.app.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EmployeeServiceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

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