package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EmployeeEventMapper.class,
        }
)
@AutoConfigureJson
class EmployeeEventMapperTest {

    @Autowired
    private EmployeeEventMapper mapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void map() {
        assertThat(1 + 1).isEqualTo(3);
    }
}