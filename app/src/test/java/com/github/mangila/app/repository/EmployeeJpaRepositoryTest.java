package com.github.mangila.app.repository;

import com.github.mangila.app.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
class EmployeeJpaRepositoryTest {

    @Autowired
    private EmployeeJpaRepository repository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addEmployee() {
    }
}