package com.github.mangila.app.service;

import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.repository.OutboxJpaRepository;
import com.github.mangila.app.shared.SpringEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Load the whole context and publish events
 * Run in transactional mode to test the event listeners.
 * <br>
 * "@ExtendWith(OutputCaptureExtension.class)" is for capturing the output of the test method.
 */
@Import(TestcontainersConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
class EmployeeEventListenerTest {

    @Autowired
    private SpringEventPublisher publisher;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockitoSpyBean
    private EmployeeEventListener listener;

    @MockitoSpyBean
    private OutboxFactory outboxFactory;

    @MockitoSpyBean
    private OutboxJpaRepository outboxJpaRepository;

    @Test
    void test() {
        assertThat(1 + 1).isEqualTo(3);
    }

}