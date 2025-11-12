package com.github.mangila.app.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnsureAspectTest {

    @Test
    void beforeEnsureNoNullArgsEmployeeService() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void afterEnsureNoNullArgsEmployeeService() {
        assertThat(1 + 1).isEqualTo(3);
    }
}