package com.github.mangila.api.service;

import com.github.mangila.api.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
class OutboxMessageRelayTest {

    @Test
    void relay() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void cleanup() {
        assertThat(1 + 1).isEqualTo(3);
    }
}