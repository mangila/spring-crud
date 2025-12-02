package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.ClockTestConfig;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.OutboxTestFactory;
import com.github.mangila.api.ReusablePostgresTestContainerConfiguration;
import com.github.mangila.api.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.api.repository.OutboxNextSequenceJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Import({ReusablePostgresTestContainerConfiguration.class,
        ClockTestConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OutboxFactoryTest {

    @Autowired
    private OutboxFactory factory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OutboxNextSequenceJpaRepository outboxNextSequenceJpaRepository;

    @Test
    void test() {
        outboxNextSequenceJpaRepository.persist(
                OutboxTestFactory.createOutboxNextSequenceEntity("test")
        );
        assertThatCode(() -> {
            factory.from("test", new CreateNewEmployeeEvent(
                    EmployeeTestFactory.createEmployeeEventDto(objectMapper)
            ));
        }).doesNotThrowAnyException();
        var sequence = outboxNextSequenceJpaRepository.findById("test")
                .orElseThrow();
       assertThat(sequence.getSequence())
               .isEqualTo(1);
    }
}