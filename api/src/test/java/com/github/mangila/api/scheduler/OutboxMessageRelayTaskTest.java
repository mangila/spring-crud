package com.github.mangila.api.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.OutboxTestFactory;
import com.github.mangila.api.ReusablePostgresTestContainerConfiguration;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEvent;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import com.github.mangila.api.repository.OutboxJpaRepository;
import com.github.mangila.api.service.EmployeeEventListener;
import com.github.mangila.api.service.OutboxEventMapper;
import com.github.mangila.api.shared.SpringEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import(ReusablePostgresTestContainerConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false",
                "application.notification.enabled=false"
        }
)
class OutboxMessageRelayTaskTest {

    @Autowired
    private OutboxMessageRelayTask task;
    @MockitoSpyBean
    private ObjectMapper objectMapper;
    @MockitoSpyBean
    private OutboxJpaRepository repository;
    @MockitoSpyBean
    private OutboxEventMapper eventMapper;
    @MockitoSpyBean
    private SpringEventPublisher publisher;
    @MockitoBean
    private EmployeeEventListener employeeEventListener;

    @BeforeEach
    void setup() {
        repository.persistAllAndFlush(List.of(
                OutboxTestFactory.createOutboxEntity(
                        "test",
                        "test-event",
                        OutboxEventStatus.PENDING,
                        objectMapper.createObjectNode()
                ),
                OutboxTestFactory.createOutboxEntity(
                        "test1",
                        "test-event1",
                        OutboxEventStatus.FAILURE,
                        objectMapper.createObjectNode()
                ),
                OutboxTestFactory.createOutboxEntity(
                        "test2",
                        "test-event2",
                        OutboxEventStatus.UNPROCESSABLE_EVENT,
                        objectMapper.createObjectNode()
                )
        ));
    }

    @AfterEach
    void cleanup() {
        var example = Example.of(new OutboxEntity(), ExampleMatcher.matchingAny());
        repository.findAll(example)
                .forEach(repository::delete);
    }

    @Test
    void relay() {
        assertThatCode(() -> {
            ObjectNode node = task.call();
            assertThatJson(node.toString())
                    .isObject()
                    .containsOnlyKeys(
                            "pending-size",
                            "failure-size",
                            "unprocessable-size",
                            "pending",
                            "failure",
                            "unprocessable"
                    )
                    .hasSize(6);
        }).doesNotThrowAnyException();
        var inOrder = Mockito.inOrder(repository,
                eventMapper,
                publisher,
                employeeEventListener);
        inOrder.verify(repository, times(3)).findAllByStatus(any(), any(), any());
        inOrder.verify(eventMapper).map(any());
        inOrder.verify(publisher).publish(any(OutboxEvent.class));
        inOrder.verify(employeeEventListener, times(3)).listen(any());
    }
}