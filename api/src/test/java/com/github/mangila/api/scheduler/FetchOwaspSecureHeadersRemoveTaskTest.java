package com.github.mangila.api.scheduler;

import com.github.mangila.api.TestcontainersConfiguration;
import com.github.mangila.api.shared.OwaspRestClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
@Slf4j
class FetchOwaspSecureHeadersRemoveTaskTest {

    @Autowired
    private FetchOwaspSecureHeadersRemoveTask task;

    @MockitoBean
    private OwaspRestClient owaspRestClient;

    @Test
    void call() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    @Disabled
    void callRemote() {

    }
}