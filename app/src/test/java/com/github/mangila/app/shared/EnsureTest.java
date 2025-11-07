package com.github.mangila.app.shared;

import com.github.mangila.app.shared.exception.EnsureException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnsureTest {

    @Test
    void notNull() {
        assertThatThrownBy(() -> Ensure.notNull(null))
                .isInstanceOf(EnsureException.class)
                .hasMessageContaining("Value must not be null");
        assertThatCode(() -> Ensure.notNull("no null pointer here!"))
                .doesNotThrowAnyException();
    }
}