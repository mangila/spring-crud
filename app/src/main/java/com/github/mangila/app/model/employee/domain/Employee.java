package com.github.mangila.app.model.employee.domain;

import com.github.mangila.app.model.employee.type.EmploymentActivity;
import com.github.mangila.app.model.employee.type.EmploymentStatus;
import com.github.mangila.app.shared.Ensure;

/**
 * Domain object, the business entity.
 * This is the data object that will execute logic.
 * <br>
 * Why wrap all the fields in a single object? Just alot of work?
 * Yes, but when isolating objects like this, it's easier to validate, test, extend and maintain the domain.
 * Instead of cluttering the whole Employee domain object with specific field methods,
 * it can be more isolated for the actual value.
 * We are able to run more specific validations on the fields and methods to ensure we are not in a bad state.
 * <br>
 * Drawbacks are that we create a lot of Objects all the time and can cause unnecessary GC pressure.
 * BUT that's not a problem in this scope mostly a problem for low-latency systems.
 */
public record Employee(
        EmployeeId id,
        EmployeeName firstName,
        EmployeeName lastName,
        EmployeeSalary salary,
        EmploymentActivity employmentActivity,
        EmploymentStatus employmentStatus,
        EmployeeAttributes attributes,
        EmployeeAudit audit
) {
    public Employee {
        Ensure.notNull(id, "Employee id must not be null");
        Ensure.notNull(firstName, "Employee first name must not be null");
        Ensure.notNull(lastName, "Employee last name must not be null");
        Ensure.notNull(salary, "Employee salary must not be null");
        Ensure.notNull(employmentActivity, "Employee employment activity must not be null");
        Ensure.notNull(employmentStatus, "Employee employment status must not be null");
        Ensure.notNull(attributes, "Employee attributes must not be null");
        Ensure.notNull(audit, "Employee audit must not be null");
    }
}