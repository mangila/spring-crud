package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.config.SchedulerConfig;
import com.github.mangila.api.repository.OutboxJpaRepository;
import com.github.mangila.api.service.OutboxEventMapper;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import com.github.mangila.api.shared.SpringEventPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;

@Import({PostgresTestContainerConfiguration.class, SchedulerConfig.class})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=true",
                "application.notification.enabled=false"
        }
)
class SchedulerTest {

    @MockitoSpyBean
    private Scheduler scheduler;
    @MockitoSpyBean
    private ApplicationTaskExecutor taskExecutor;
    @MockitoSpyBean
    private ObjectMapper objectMapper;
    @Autowired
    private OutboxJpaRepository outboxJpaRepository;
    @Autowired
    private OutboxEventMapper eventMapper;
    @Autowired
    private SpringEventPublisher publisher;

    @Test
    void test() {
        assertThatCode(() -> {
            scheduler.outboxMessageRelayTask();
        }).doesNotThrowAnyException();
        var inOrder = Mockito.inOrder(taskExecutor);
        inOrder.verify(taskExecutor, Mockito.times(1))
                .submitCompletable(any(), any());
    }
}