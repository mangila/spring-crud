package com.github.mangila.app.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SchedulerTest {

    @Test
    void test() {
        assertThat(1 + 1).isEqualTo(3);
    }
}