package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEvent;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import com.github.mangila.api.repository.OutboxJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@Import(PostgresTestContainerConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
class OutboxEventHandlerTest {

    @MockitoSpyBean
    private OutboxEventHandler handler;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private OutboxJpaRepository outboxJpaRepository;

    @Autowired
    private OutboxEventMapper eventMapper;

    @Test
    @DisplayName("Should handle event and set to PUBLISHED")
    void handle() {
        OutboxEntity entity = outboxJpaRepository.findAllByAggregateId(
                        "test",
                        Sort.unsorted(),
                        Limit.unlimited())
                .getFirst();
        OutboxEvent event = eventMapper.map(entity);
        assertThatCode(() -> handler.handle(event))
                .doesNotThrowAnyException();
        List<OutboxEntity> outboxEntities = outboxJpaRepository.findAllByAggregateId(
                entity.getAggregateId(),
                Sort.unsorted(),
                Limit.unlimited());
        assertThat(outboxEntities)
                .hasSize(1);
        assertThat(outboxEntities.getFirst())
                .hasFieldOrPropertyWithValue("status", OutboxEventStatus.PUBLISHED);
        var inOrder = inOrder(outboxJpaRepository);
        inOrder.verify(outboxJpaRepository, times(1))
                .changeStatus(any(), any());
    }

    @Test
    @DisplayName("Should handle event and set to UNPROCESSABLE_EVENT")
    void handle1() {
        OutboxEntity entity = outboxJpaRepository.findAllByAggregateId(
                        "test",
                        Sort.unsorted(),
                        Limit.unlimited())
                .getFirst();
        entity.setEventName("UnknownEvent");
        OutboxEvent event = eventMapper.map(entity);
        assertThatCode(() -> handler.handle(event))
                .doesNotThrowAnyException();
        List<OutboxEntity> outboxEntities = outboxJpaRepository.findAllByAggregateId(
                entity.getAggregateId(),
                Sort.unsorted(),
                Limit.unlimited());
        assertThat(outboxEntities)
                .hasSize(1);
        assertThat(outboxEntities.getFirst())
                .hasFieldOrPropertyWithValue("status", OutboxEventStatus.UNPROCESSABLE_EVENT);
        var inOrder = inOrder(outboxJpaRepository);
        inOrder.verify(outboxJpaRepository, times(1))
                .changeStatus(any(), any());
    }

    @Test
    @DisplayName("Should handle event and set to FAILURE")
    void handle2() {
        Mockito.doThrow(new RuntimeException("Test exception"))
                .when(handler)
                .handleCreateNewEmployeeEvent(any());
        OutboxEntity entity = outboxJpaRepository.findAllByAggregateId(
                        "test",
                        Sort.unsorted(),
                        Limit.unlimited())
                .getFirst();
        OutboxEvent event = eventMapper.map(entity);
        assertThatCode(() -> handler.handle(event))
                .doesNotThrowAnyException();
        List<OutboxEntity> outboxEntities = outboxJpaRepository.findAllByAggregateId(
                entity.getAggregateId(),
                Sort.unsorted(),
                Limit.unlimited());
        assertThat(outboxEntities)
                .hasSize(1);
        assertThat(outboxEntities.getFirst())
                .hasFieldOrPropertyWithValue("status", OutboxEventStatus.FAILURE);
        var inOrder = inOrder(outboxJpaRepository);
        inOrder.verify(outboxJpaRepository, times(1))
                .changeStatus(any(), any());
    }

}