package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.*;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import org.springframework.stereotype.Component;

/**
 * Java without a Factory Method is not Java :D
 * <br>
 * Spring Component Factory Method pattern, this is purely optional in this case.
 * It's separate of concerns thinking since sometimes the mapper implementation becomes both the factory and the mapper.
 */
@Component
public class EmployeeFactory {
    private final EmployeeIdGenerator employeeIdGenerator;

    public EmployeeFactory(EmployeeIdGenerator employeeIdGenerator) {
        this.employeeIdGenerator = employeeIdGenerator;
    }

    public Employee from(CreateNewEmployeeRequest request) {
        EmployeeId id = employeeIdGenerator.generate(
                request.firstName(),
                request.lastName());
        return new Employee(
                id,
                new EmployeeName(request.firstName()),
                new EmployeeName(request.lastName()),
                new EmployeeSalary(request.salary()),
                request.employmentActivity(),
                request.employmentStatus(),
                new EmployeeAttributes(request.attributes()),
                EmployeeAudit.EMPTY
        );
    }
}
