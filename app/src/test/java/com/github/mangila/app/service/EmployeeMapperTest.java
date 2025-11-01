package com.github.mangila.app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This testing is for slim context stuffs where no full Spring context is required.
 * This is a good practice for unit testing of simple beans.
 * <br>
 * If there are more dependencies, it can be loaded with {@link ContextConfiguration} or just use the full Spring context.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EmployeeMapper.class)
class EmployeeMapperTest {

    @Autowired
    private EmployeeMapper mapper;

    @Test
    void test() {
        assertThat(1 + 1).isEqualTo(2);
    }
}