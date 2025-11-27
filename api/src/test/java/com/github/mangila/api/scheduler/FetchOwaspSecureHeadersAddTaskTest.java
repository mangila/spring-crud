package com.github.mangila.api.scheduler;

import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.shared.OwaspRestClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * Since we are relying on a remote endpoint, we use a Mock here to not send requests to the real endpoint from our test suite.
 * When we are running our tests, if the remote endpoint is down, the test will fail.
 * An alternative is to have a disabled test that does an actual call to the remote endpoint to verify the behavior.
 * </p>
 * In an Enterprise setting a CI/CD server might be running in its own network and Mock everywhere would be required.
 */
@Import(PostgresTestContainerConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false",
                "application.notification.enabled=false"
        }
)
@Slf4j
class FetchOwaspSecureHeadersAddTaskTest {

    @Autowired
    private FetchOwaspSecureHeadersAddTask task;

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