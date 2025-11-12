package com.github.mangila.app.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/converter/json/Jackson2ObjectMapperBuilder.html
 * <br>
 * We get some extra modules if they are on the classpath.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer writeBigDecimalAsPlain() {
        return builder -> builder.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer writeNumbersAsStrings() {
        // Spring Boot and Jackson version mismatch. NO FIX FOR NOW.
        return builder -> builder.featuresToEnable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
    }

}
