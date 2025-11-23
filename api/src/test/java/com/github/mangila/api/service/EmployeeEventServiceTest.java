package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.repository.OutboxNextSequenceJpaRepository;
import com.github.mangila.api.repository.OutboxProcessedSequenceJpaRepository;
import com.github.mangila.api.shared.SpringEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@Import(PostgresTestContainerConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
class EmployeeEventServiceTest {

    @MockitoSpyBean
    private EmployeeEventService service;
    @MockitoSpyBean
    private EmployeeEventListener listener;
    @MockitoSpyBean
    private OutboxNextSequenceJpaRepository nextSequenceRepository;
    @MockitoSpyBean
    private OutboxProcessedSequenceJpaRepository processedSequenceRepository;
    @MockitoSpyBean
    private EmployeeEventMapper eventMapper;
    @MockitoSpyBean
    private SpringEventPublisher publisher;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publishCreateNewEvent() throws IOException {
        Employee employee = EmployeeTestFactory.createEmployee(objectMapper);
        assertThatCode(() -> service.publishCreateNewEvent(employee))
                .doesNotThrowAnyException();
        var inOrder = Mockito.inOrder(eventMapper,
                processedSequenceRepository,
                nextSequenceRepository,
                publisher,
                listener);
        inOrder.verify(eventMapper).map(any(Employee.class));
        inOrder.verify(processedSequenceRepository).persist(any());
        inOrder.verify(nextSequenceRepository).persist(any());
        inOrder.verify(publisher).publish(any(), any());
        inOrder.verify(listener, times(1)).listen(any());
    }

    @Test
    void publishUpdateEvent() throws IOException {
        Employee employee = EmployeeTestFactory.createEmployee(objectMapper);
        assertThatCode(() -> service.publishUpdateEvent(employee))
                .doesNotThrowAnyException();
        var inOrder = Mockito.inOrder(eventMapper,
                processedSequenceRepository,
                nextSequenceRepository,
                publisher,
                listener);
        inOrder.verify(eventMapper).map(any(Employee.class));
        inOrder.verify(publisher).publish(any(), any());
        inOrder.verify(listener, times(1)).listen(any());
    }

    @Test
    void publishSoftDeleteEvent() throws IOException {
        Employee employee = EmployeeTestFactory.createEmployee(objectMapper);
        assertThatCode(() -> service.publishSoftDeleteEvent(employee))
                .doesNotThrowAnyException();
        var inOrder = Mockito.inOrder(eventMapper,
                processedSequenceRepository,
                nextSequenceRepository,
                publisher,
                listener);
        inOrder.verify(eventMapper).map(any(Employee.class));
        inOrder.verify(publisher).publish(any(), any());
        inOrder.verify(listener, times(1)).listen(any());
    }
}