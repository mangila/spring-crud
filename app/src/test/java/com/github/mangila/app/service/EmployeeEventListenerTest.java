package com.github.mangila.app.service;

import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.app.model.employee.event.SoftDeleteEmployeeEvent;
import com.github.mangila.app.model.employee.event.UpdateEmployeeEvent;
import com.github.mangila.app.shared.SpringEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Load the whole context and publish events
 * Run in transactional mode to test the event listeners.
 */
@Import(TestcontainersConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EmployeeEventListenerTest {

    @Autowired
    private SpringEventPublisher publisher;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockitoSpyBean
    private EmployeeEventListener listener;

    @Test
    void publishCreateEmployeeEventNeedTx() {
        assertThatThrownBy(() -> {
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new CreateNewEmployeeEvent(id));
        }).isInstanceOf(IllegalTransactionStateException.class);
        verify(listener, never()).listen(any(CreateNewEmployeeEvent.class));
    }

    @Test
    void listenTransactionCommitCreateEmployee(CapturedOutput output) {
        transactionTemplate.executeWithoutResult(txStatus -> {
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new CreateNewEmployeeEvent(id));
        });
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertThat(output).contains("Created Employee with ID:");
                });
        verify(listener, times(1)).listen(any(CreateNewEmployeeEvent.class));
    }

    @Test
    void listenTransactionRollbackCreateEmployee(CapturedOutput output) {
        transactionTemplate.executeWithoutResult(txStatus -> {
            txStatus.setRollbackOnly();
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new CreateNewEmployeeEvent(id));
        });
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertThat(output).doesNotContain("Created Employee with ID:");
                });
        verify(listener, never()).listen(any(CreateNewEmployeeEvent.class));
    }

    @Test
    void publishUpdateEmployeeEventNeedTx() {
        assertThatThrownBy(() -> {
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new UpdateEmployeeEvent(id));
        }).isInstanceOf(IllegalTransactionStateException.class);
        verify(listener, never()).listen(any(UpdateEmployeeEvent.class));
    }

    @Test
    void listenTransactionCommitUpdateEmployee(CapturedOutput output) {
        transactionTemplate.executeWithoutResult(txStatus -> {
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new UpdateEmployeeEvent(id));
        });
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertThat(output).contains("Updated Employee with ID:");
                });
        verify(listener, times(1)).listen(any(UpdateEmployeeEvent.class));
    }

    @Test
    void listenTransactionRollbackUpdateEmployee(CapturedOutput output) {
        transactionTemplate.executeWithoutResult(txStatus -> {
            txStatus.setRollbackOnly();
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new UpdateEmployeeEvent(id));
        });
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertThat(output).doesNotContain("Updated Employee with ID:");
                });
        verify(listener, never()).listen(any(UpdateEmployeeEvent.class));
    }

    @Test
    void publishSoftDeleteEmployeeEventNeedTx() {
        assertThatThrownBy(() -> {
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new SoftDeleteEmployeeEvent(id));
        }).isInstanceOf(IllegalTransactionStateException.class);
        verify(listener, never()).listen(any(SoftDeleteEmployeeEvent.class));
    }

    @Test
    void listenTransactionCommitSoftDeleteEmployee(CapturedOutput output) {
        transactionTemplate.executeWithoutResult(txStatus -> {
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new SoftDeleteEmployeeEvent(id));
        });
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertThat(output).contains("Soft deleted Employee with ID:");
                });
        verify(listener, times(1)).listen(any(SoftDeleteEmployeeEvent.class));
    }

    @Test
    void listenTransactionRollbackSoftDeleteEmployee(CapturedOutput output) {
        transactionTemplate.executeWithoutResult(txStatus -> {
            txStatus.setRollbackOnly();
            var id = ObjectFactoryUtil.createFakeEmployeeId();
            publisher.publish(new SoftDeleteEmployeeEvent(id));
        });
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertThat(output).doesNotContain("Soft deleted Employee with ID:");
                });
        verify(listener, never()).listen(any(SoftDeleteEmployeeEvent.class));
    }
}