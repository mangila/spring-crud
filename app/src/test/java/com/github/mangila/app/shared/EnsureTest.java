package com.github.mangila.app.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnsureTest {

    @Test
    void notNull() {
        assertThatThrownBy(() -> Ensure.notNull(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Value must not be null");
        assertThatCode(() -> Ensure.notNull("no null pointer here!"))
                .doesNotThrowAnyException();
    }
}