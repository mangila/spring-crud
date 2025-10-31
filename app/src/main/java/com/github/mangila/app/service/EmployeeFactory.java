package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.domain.EmployeeName;
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
        var employee = new Employee();
        employee.setId(id);
        employee.setFirstName(new EmployeeName(request.firstName()));
        employee.setLastName(new EmployeeName(request.lastName()));
        employee.setSalary(request.salary());
        employee.setAttributes(request.attributes());
        return employee;
    }
}
