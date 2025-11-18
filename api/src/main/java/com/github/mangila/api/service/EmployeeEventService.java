package com.github.mangila.api.service;

import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import com.github.mangila.api.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.api.model.employee.event.SoftDeleteEmployeeEvent;
import com.github.mangila.api.model.employee.event.UpdateEmployeeEvent;
import com.github.mangila.api.model.outbox.OutboxNextSequenceEntity;
import com.github.mangila.api.model.outbox.OutboxProcessedSequenceEntity;
import com.github.mangila.api.repository.OutboxNextSequenceJpaRepository;
import com.github.mangila.api.repository.OutboxProcessedSequenceJpaRepository;
import com.github.mangila.api.shared.SpringEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The event service is responsible for publishing the events.
 */
@Service
public class EmployeeEventService {

    private final OutboxNextSequenceJpaRepository nextSequenceRepository;
    private final OutboxProcessedSequenceJpaRepository processedSequenceRepository;
    private final EmployeeEventMapper eventMapper;
    private final SpringEventPublisher publisher;

    public EmployeeEventService(OutboxNextSequenceJpaRepository nextSequenceRepository,
                                OutboxProcessedSequenceJpaRepository processedSequenceRepository,
                                EmployeeEventMapper eventMapper,
                                SpringEventPublisher publisher) {
        this.nextSequenceRepository = nextSequenceRepository;
        this.processedSequenceRepository = processedSequenceRepository;
        this.eventMapper = eventMapper;
        this.publisher = publisher;
    }


    @Transactional
    public void publishCreateNewEvent(Employee employee) {
        EmployeeEventDto dto = eventMapper.map(employee);
        var event = new CreateNewEmployeeEvent(dto);
        processedSequenceRepository.persist(OutboxProcessedSequenceEntity.from(employee.id().value()));
        nextSequenceRepository.persist(OutboxNextSequenceEntity.from(employee.id().value()));
        publisher.publish(employee.id().value(), event);
    }

    @Transactional
    public void publishUpdateEvent(Employee employee) {
        EmployeeEventDto dto = eventMapper.map(employee);
        var event = new UpdateEmployeeEvent(dto);
        publisher.publish(employee.id().value(), event);
    }

    @Transactional
    public void publishSoftDeleteEvent(Employee employee) {
        EmployeeEventDto dto = eventMapper.map(employee);
        var event = new SoftDeleteEmployeeEvent(dto);
        publisher.publish(dto.employeeId(), event);
    }
}
