package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.OutboxTestFactory;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import com.github.mangila.api.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import com.github.mangila.api.model.outbox.OutboxNextSequenceEntity;
import com.github.mangila.api.model.outbox.OutboxProcessedSequenceEntity;
import com.github.mangila.api.repository.OutboxJpaRepository;
import com.github.mangila.api.repository.OutboxNextSequenceJpaRepository;
import com.github.mangila.api.repository.OutboxProcessedSequenceJpaRepository;
import com.github.mangila.api.shared.SpringEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
                "application.scheduler.enabled=false",
                "application.notification.enabled=false"
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
    private OutboxNextSequenceJpaRepository outboxNextSequenceRepository;

    @MockitoSpyBean
    private OutboxProcessedSequenceJpaRepository outboxProcessedSequenceJpaRepository;

    @MockitoSpyBean
    private OutboxEventMapper outboxEventMapper;

    @AfterEach
    void afterEach() {

    }

    @BeforeEach
    void beforeEach() {
    }

    @Test
    void listen() throws IOException {
        EmployeeEventDto dto = EmployeeTestFactory.createEmployeeEventDto(objectMapper);
        OutboxNextSequenceEntity next = OutboxTestFactory.createOutboxNextSequenceEntity(dto.employeeId());
        next.setSequence(3);
        outboxNextSequenceRepository.persist(next);
        OutboxProcessedSequenceEntity processed = OutboxTestFactory.createOutboxProcessedSequenceEntity(dto.employeeId());
        processed.setSequence(3);
        outboxProcessedSequenceJpaRepository.persist(processed);
        transactionTemplate.executeWithoutResult(txStatus -> {
            publisher.publish(
                    dto.employeeId(),
                    new CreateNewEmployeeEvent(dto)
            );
        });

        await().untilAsserted(() -> {
            verify(listener).listen(any());
            verify(eventHandler).handle(any());
            OutboxEntity entity = outboxJpaRepository.findAllByAggregateId(dto.employeeId(), Pageable.unpaged())
                    .getContent()
                    .getFirst();
            assertThat(entity.getStatus())
                    .isEqualTo(OutboxEventStatus.PUBLISHED);
        });
    }

}