package com.github.mangila.background;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BackgroundApplicationTests {

    @Test
    void contextLoads() {
    }

}
