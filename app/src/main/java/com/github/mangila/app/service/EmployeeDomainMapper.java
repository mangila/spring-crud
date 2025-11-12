package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.*;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class EmployeeDomainMapper {

    private final Clock clock;

    public EmployeeDomainMapper(Clock clock) {
        this.clock = clock;
    }

    public Employee map(UpdateEmployeeRequest dto) {
        return new Employee(
                new EmployeeId(dto.employeeId()),
                new EmployeeName(dto.firstName()),
                new EmployeeName(dto.lastName()),
                new EmployeeSalary(dto.salary()),
                dto.employmentActivity(),
                dto.employmentStatus(),
                new EmployeeAttributes(dto.attributes()),
                EmployeeAudit.EMPTY
        );
    }

    public Employee map(EmployeeDto dto) {
        return new Employee(
                new EmployeeId(dto.employeeId()),
                new EmployeeName(dto.firstName()),
                new EmployeeName(dto.lastName()),
                new EmployeeSalary(dto.salary()),
                dto.employmentActivity(),
                dto.employmentStatus(),
                new EmployeeAttributes(dto.attributes()),
                new EmployeeAudit(
                        toZonedTimeInstant(dto.created(), clock),
                        toZonedTimeInstant(dto.modified(), clock),
                        dto.deleted()
                )
        );
    }

    public Employee map(EmployeeEntity entity) {
        return new Employee(
                new EmployeeId(entity.getId()),
                new EmployeeName(entity.getFirstName()),
                new EmployeeName(entity.getLastName()),
                new EmployeeSalary(entity.getSalary()),
                entity.getEmploymentActivity(),
                entity.getEmploymentStatus(),
                new EmployeeAttributes(entity.getAttributes()),
                new EmployeeAudit(
                        entity.getAuditMetadata().getCreated(),
                        entity.getAuditMetadata().getModified(),
                        entity.getAuditMetadata().isDeleted()
                )
        );
    }

    /**
     * Convert a LocalDateTime to an Instant.
     * Get the zone from the clock.
     * To get the precise DST (Daylight saving time) to UTC.
     * <br>
     * But here it can get issues and confusion.
     * Since LocalDateTime is basically just a timestamp with no timezone information.
     * So here we do an Optimistic conversion, with no actual information about the origin of the LocalDateTime.
     * <br>
     * What Zone does this LocalDateTime belong to?
     */
    private static Instant toZonedTimeInstant(LocalDateTime localDateTime, Clock clock) {
        return localDateTime.atZone(clock.getZone()).toInstant();
    }
}
