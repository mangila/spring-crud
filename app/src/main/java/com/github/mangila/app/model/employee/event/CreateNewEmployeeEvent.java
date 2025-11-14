package com.github.mangila.app.model.employee.event;

import com.github.mangila.app.model.employee.dto.EmployeeEventDto;
import jakarta.validation.constraints.NotNull;

/**
 * Events often published it's a good practice to keep events as small as possible.
 * With not too much data. Buffers in event-based infra can have a limit.
 * <br>
 * Naming events can sometimes be a bit confusing.
 * Here we name it "<ACTION><DOMAIN>Event"
 * <br>
 * All the events have the same object in param but just different actions.
 * <br>
 * Send and save the full payload in the event will make us able to replay the full state from any point in time.
 * Can be good for disaster recovery, auditing, debugging, etc.
 */
public record CreateNewEmployeeEvent(@NotNull EmployeeEventDto dto) {
}
