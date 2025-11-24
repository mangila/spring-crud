package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import com.github.mangila.api.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.api.model.outbox.OutboxEvent;
import com.github.mangila.api.repository.OutboxJpaRepository;
import com.github.mangila.api.shared.SpringEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

/**
 * Load the whole context and publish events
 * Run in transactional mode to test the event listeners.
 * <br>
 * "@ExtendWith(OutputCaptureExtension.class)" is for capturing the output of the test method.
 */
@Import(PostgresTestContainerConfiguration.class)
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
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockitoSpyBean
    private EmployeeEventListener listener;

    @MockitoSpyBean
    private OutboxEventHandler eventHandler;

    @MockitoSpyBean
    private OutboxFactory outboxFactory;

    @MockitoSpyBean
    private OutboxJpaRepository outboxJpaRepository;

    @MockitoSpyBean
    private OutboxEventMapper outboxEventMapper;

    @Test
    void listen() throws IOException {
        EmployeeEventDto dto = EmployeeTestFactory.createEmployeeEventDto(objectMapper);
        transactionTemplate.executeWithoutResult(txStatus -> {
            publisher.publish(
                    dto.employeeId(),
                    new CreateNewEmployeeEvent(dto)
            );
        });
        var inOrder = inOrder(
                listener,
                eventHandler,
                outboxFactory,
                outboxJpaRepository,
                outboxEventMapper);
        inOrder.verify(listener, times(1)).listen(any(OutboxEvent.class));
        assertThat(1 + 1).isEqualTo(3);
    }

}