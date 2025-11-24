package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.OutboxTestFactory;
import com.github.mangila.api.ReusablePostgresTestContainerConfiguration;
import com.github.mangila.api.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.model.outbox.OutboxEvent;
import com.github.mangila.api.model.outbox.OutboxEventStatus;
import com.github.mangila.api.repository.OutboxJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@Import(ReusablePostgresTestContainerConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
class OutboxEventHandlerTest {

    @MockitoSpyBean
    private OutboxEventHandler handler;

    @MockitoSpyBean
    private OutboxJpaRepository outboxJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OutboxEventMapper eventMapper;

    private OutboxEntity reusableEntity;

    @BeforeEach
    void setup() {
        OutboxEntity entity = OutboxTestFactory.createOutboxEntity(
                "test",
                CreateNewEmployeeEvent.class.getName(),
                OutboxEventStatus.PUBLISHED,
                objectMapper.createObjectNode()
        );
        this.reusableEntity = outboxJpaRepository.persist(entity);
    }

    @AfterEach
    void cleanup() {
        outboxJpaRepository.deleteById(reusableEntity.getId());
    }

    @Test
    @DisplayName("Should handle event and set to PUBLISHED")
    void handle() {
        // Setup
        OutboxEvent event = eventMapper.map(reusableEntity);
        // Act
        assertThatCode(() -> handler.handle(event))
                .doesNotThrowAnyException();
        Page<OutboxEntity> outboxEntities = outboxJpaRepository.findAllByAggregateId(
                event.aggregateId(), Pageable.unpaged());
        // Assert
        assertThat(outboxEntities.getContent())
                .hasSize(1);
        assertThat(outboxEntities.getContent().getFirst())
                .hasFieldOrPropertyWithValue("status", OutboxEventStatus.PUBLISHED);
        var inOrder = inOrder(outboxJpaRepository);
        inOrder.verify(outboxJpaRepository, times(1))
                .changeStatus(any(), any());
    }

    @Test
    @DisplayName("Should handle event and set to UNPROCESSABLE_EVENT")
    void handle1() {
        // Setup
        reusableEntity.setEventName("UNKNOWN_EVENT");
        OutboxEvent event = eventMapper.map(reusableEntity);
        // Act
        assertThatCode(() -> handler.handle(event))
                .doesNotThrowAnyException();
        Page<OutboxEntity> outboxEntities = outboxJpaRepository.findAllByAggregateId(
                event.aggregateId(), Pageable.unpaged());
        // Assert
        assertThat(outboxEntities.getContent())
                .hasSize(1);
        assertThat(outboxEntities.getContent().getFirst())
                .hasFieldOrPropertyWithValue("status", OutboxEventStatus.UNPROCESSABLE_EVENT);
        var inOrder = inOrder(outboxJpaRepository);
        inOrder.verify(outboxJpaRepository, times(1))
                .changeStatus(any(), any());
    }

    @Test
    @DisplayName("Should handle event and set to FAILURE")
    void handle2() {
        // Setup
        Mockito.doThrow(new RuntimeException("Test exception"))
                .when(handler)
                .handleCreateNewEmployeeEvent(any());
        OutboxEvent event = eventMapper.map(reusableEntity);
        // Act
        assertThatCode(() -> handler.handle(event))
                .doesNotThrowAnyException();
        Page<OutboxEntity> outboxEntities = outboxJpaRepository.findAllByAggregateId(
                reusableEntity.getAggregateId(), Pageable.unpaged());
        // Assert
        assertThat(outboxEntities)
                .hasSize(1);
        assertThat(outboxEntities.getContent().getFirst())
                .hasFieldOrPropertyWithValue("status", OutboxEventStatus.FAILURE);
        var inOrder = inOrder(outboxJpaRepository);
        inOrder.verify(outboxJpaRepository, times(1))
                .changeStatus(any(), any());
    }

}