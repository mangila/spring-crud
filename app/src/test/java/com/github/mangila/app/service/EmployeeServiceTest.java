package com.github.mangila.app.service;

import com.github.mangila.app.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Full-scale service test with integration towards a database.
 * <br>
 * Some might use a Mock here towards the database.
 * If this might be developed in an Enterprise setting when behind a corporate firewall,
 * a CI/CD server might not be able to spin up a testcontainer (might not even have Docker installed),
 * so it won't be able to fetch from Docker public repositories. Some Enterprise settings may have this already set up with a private repository for testing.
 * In that case you can use a Mock here and have a separate local disabled test to run against a real database with testcontainers.
 * Or just do prod-testing (recommended).
 * <br>
 * But here we use a real database from Docker public repositories.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EmployeeServiceTest {

    @Autowired
    private EmployeeService service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findEmployeeById() {
        service.findEmployeeById(null);
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