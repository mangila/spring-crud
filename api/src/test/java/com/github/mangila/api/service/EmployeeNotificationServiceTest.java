package com.github.mangila.api.service;

import com.github.mangila.api.PostgresTestContainerConfiguration;
import org.junit.jupiter.api.Test;
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
class EmployeeNotificationServiceTest {

    @Test
    void listen() {
        assertThat(1 + 1).isEqualTo(3);
    }
}