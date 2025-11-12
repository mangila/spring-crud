package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import org.springframework.stereotype.Component;

import java.time.*;

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
                toZonedDateTime(employee.audit().created(), clock),
                toZonedDateTime(employee.audit().modified(), clock),
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
    private static ZonedDateTime toZonedDateTime(Instant instant, Clock clock) {
        return ZonedDateTime.ofInstant(instant, clock.getZone());
    }
}
