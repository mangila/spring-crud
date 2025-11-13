package com.github.mangila.app.scheduler;

import com.github.mangila.app.TestcontainersConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
@Slf4j
class FetchOwaspSecureHeadersTaskTest {

    @Autowired
    private FetchOwaspSecureHeadersTask task;

    @Test
    void call() {
    }
}