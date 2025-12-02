package com.github.mangila.api.service;

import com.github.mangila.api.PostgresTestContainerConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(PostgresTestContainerConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false",
                "application.notification.enabled=false"
        }
)
class EmployeeRestFacadeTest {

    @Autowired
    private EmployeeRestFacade facade;

    @Test
    @DisplayName("Should C.R.U.D Employee")
    void crudEmployee() {
        assertThat(1 + 1).isEqualTo(3);
    }
}