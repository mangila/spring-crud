package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.app.model.employee.event.SoftDeleteEmployeeEvent;
import com.github.mangila.app.model.employee.event.UpdateEmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Synchronous is the default for Spring @EventListener. To run it asynchronously, use @Async.
 * <br>
 * Use @Async to run side effects and fire and forget stuffs.
 * If not using the @Async annotation, it will block the current thread.
 * Can be good if you want a decoupling between components and not a tight dependency.
 */
@Service
@Slf4j
public class EmployeeEventListener {

    /**
     * Listener for NewEmployeeCreatedEvent.
     * Run a side effect.
     * <br>
     * E.g., email the employee, notify a supervisor, etc.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void listen(CreateNewEmployeeEvent event) {
        EmployeeId id = event.employeeId();
        log.info("Created Employee with ID: {}", id.value());
    }

    /**
     * Listener for UpdateEmployeeEvent.
     * <br>
     * E.g., update a cache or an integration somewhere.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void listen(UpdateEmployeeEvent event) {
        EmployeeId id = event.employeeId();
        log.info("Updated Employee with ID: {}", id.value());
    }

    /**
     * Listener for SoftDeleteEmployeeEvent
     * <br>
     * E.g., Run some cleanup tasks for a soft deleted employee
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void listen(SoftDeleteEmployeeEvent event) {
        EmployeeId id = event.employeeId();
        log.info("Soft deleted Employee with ID: {}", id.value());
    }
}
