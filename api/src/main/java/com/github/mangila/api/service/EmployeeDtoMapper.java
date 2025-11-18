package com.github.mangila.api.service;

import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.dto.EmployeeDto;
import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;

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

    public EmployeeDto map(EmployeeEventDto eventDto) {
        return new EmployeeDto(
                eventDto.employeeId(),
                eventDto.firstName(),
                eventDto.lastName(),
                eventDto.salary(),
                eventDto.employmentActivity(),
                eventDto.employmentStatus(),
                eventDto.attributes(),
                toZonedDateTime(eventDto.created(), clock),
                toZonedDateTime(eventDto.modified(), clock),
                eventDto.deleted()
        );
    }

    /**
     * Create a ZonedDateTime from an Instant and a Clock.
     * <p>
     * Client requesting from the right Timezone but a Loadbalancer or any other infra delegates to another timezoned server.
     * We would send the wrong local date time to the client
     * Since we use a hardcoded Clock, we can set a Clock in a ThreadLocal Context,
     * but then we need data from the client
     * </p>
     * <p>
     * Set the Clock Timezone according to the client request is another way.
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Preference-Applied
     * </p>
     * <code>
     * clock.withZone(ZoneId.of(request.getHeader("Preference-Applied")))
     * </code>
     */
    private static ZonedDateTime toZonedDateTime(Instant instant, Clock clock) {
        return ZonedDateTime.ofInstant(instant, clock.getZone());
    }
}
