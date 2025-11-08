package com.github.mangila.app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EmployeeDtoMapper.class)
class EmployeeDtoMapperTest {

    @Test
    void map() {
        assertThat(1 + 1).isEqualTo(3);
    }
}