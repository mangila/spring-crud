package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.shared.event.NewEmployeeCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeEventListener {

    /**
     * Listener for NewEmployeeCreatedEvent.
     * Run a side effect.
     * Synchronous is the default for Spring @EventListener. To run it asynchronously, use @Async.
     * <br>
     * E.g., email the employee, notify a supervisor, etc.
     */
    @Async
    @EventListener
    public void listen(NewEmployeeCreatedEvent event) {
        EmployeeId id = event.employeeId();
        log.info("New employee created with ID: {}", id.value());
    }

}
