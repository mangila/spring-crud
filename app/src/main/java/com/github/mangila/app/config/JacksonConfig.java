package com.github.mangila.app.config;

import com.fasterxml.jackson.core.JsonGenerator;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer writeBigDecimalAsPlain() {
        return builder -> builder.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer writeNumbersAsStrings() {
        return builder -> builder.featuresToEnable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
    }

}
