package com.github.mangila.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TestClass for configured Jackson's customizations.
 */
@Import(JacksonConfig.class)
@JsonTest
class JacksonConfigTest {

    @Autowired
    private JacksonTester<Map<String, BigDecimal>> jsonBigDecimalTester;

    @Test
    @DisplayName("BigDecimal should use .toPlainString() and be serialized as a JSON String")
    void testBigDecimalSerialization() throws IOException {
        // Setup
        var map = Map.of("value", new BigDecimal("0.0000001"));
        // Act
        var jsonContent = jsonBigDecimalTester.write(map);
        // Assert
        assertThat(jsonContent)
                .hasJsonPathStringValue("@.value")
                .extractingJsonPathStringValue("@.value")
                .isEqualTo("0.0000001");
    }
}