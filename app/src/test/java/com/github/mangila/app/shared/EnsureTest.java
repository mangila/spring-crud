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

    @Test
    void notBlank() {
        assertThatThrownBy(() -> Ensure.notBlank("   "))
                .isInstanceOf(EnsureException.class)
                .hasMessage("Expression must be true");
        assertThatCode(() -> Ensure.notBlank("not blank"))
                .doesNotThrowAnyException();
    }

    @Test
    void isTrue() {
        assertThatThrownBy(() -> Ensure.isTrue(false))
                .isInstanceOf(EnsureException.class)
                .hasMessage("Expression must be true");
        assertThatCode(() -> Ensure.isTrue(true))
                .doesNotThrowAnyException();
    }

    @Test
    void isFalse() {
        assertThatThrownBy(() -> Ensure.isFalse(false))
                .isInstanceOf(EnsureException.class)
                .hasMessage("Expression must be false");
        assertThatCode(() -> Ensure.isFalse(false))
                .doesNotThrowAnyException();
    }
}