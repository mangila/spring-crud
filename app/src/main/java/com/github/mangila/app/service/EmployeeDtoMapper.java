package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class EmployeeDtoMapper {

    private final Clock clock;

    public EmployeeDtoMapper(Clock clock) {
        this.clock = clock;
    }

    public EmployeeDto map(Employee employee) {
        return new EmployeeDto(
                employee.id().value(),
                employee.firstName().value(),
                employee.lastName().value(),
                employee.salary().value(),
                employee.employmentActivity(),
                employee.employmentStatus(),
                employee.attributes().value(),
                toLocalDateTime(employee.audit().created(), clock),
                toLocalDateTime(employee.audit().modified(), clock),
                employee.audit().deleted()
        );
    }

    /**
     * This depends on where the clock is coming from.
     * Client requesting from the right Timezone but a Loadbalancer or any other infra delegates to another timezoned server.
     * We would send the wrong local date time to the client.
     * <br>
     * Set Clock Timezone according to the client request is another way.
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Preference-Applied
     * <br>
     * <br>
     * <code>
     *     clock.withZone(ZoneId.of(request.getHeader("Preference-Applied")))
     *     <br>
     *     <br>
     *     clock.withZone("Middle-Earth/Mordor")
     * </code>
     */
    private static LocalDateTime toLocalDateTime(Instant instant, Clock clock) {
        return LocalDateTime.ofInstant(instant, clock.getZone());
    }
}
