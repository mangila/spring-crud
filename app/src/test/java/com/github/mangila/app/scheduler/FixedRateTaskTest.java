package com.github.mangila.app.scheduler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FixedRateTaskTest {

    @Test
    void run() {
        assertThat(1 + 1).isEqualTo(3);
    }
}