package com.github.mangila.app.shared;

import com.github.mangila.app.shared.exception.EnsureException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnsureTest {

    @Test
    void notNull() {
        assertThatThrownBy(() -> Ensure.notNull(null))
                .isInstanceOf(EnsureException.class)
                .hasMessageContaining("Value must not be null");
        assertThatCode(() -> Ensure.notNull("no null pointer here!"))
                .doesNotThrowAnyException();
    }

    @Test
    void notBlank() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void isTrue() {
        assertThatThrownBy(() -> Ensure.isTrue(false, () -> new IllegalArgumentException("This is a test exception")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This is a test exception");
        assertThatCode(() -> Ensure.isTrue(true, () -> new IllegalArgumentException("This is a test exception")))
                .doesNotThrowAnyException();
    }
}