package com.github.mangila.app.repository;

import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.config.JpaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import({TestcontainersConfiguration.class,
        JpaConfig.class})
@DataJpaTest
class TaskExecutionJpaRepositoryTest {

    @Test
    void shouldAudit() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void test() {
        assertThat(1 + 1).isEqualTo(3);
    }

}