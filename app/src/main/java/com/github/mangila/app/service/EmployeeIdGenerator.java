package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.EmployeeId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmployeeIdGenerator {

    /**
     * Generates a new EmployeeId
     * <br>Format:
     * <code>
     * EMP-<2 chars of firstName><2 chars of lastname>-UUID
     * </code>
     */
    public EmployeeId generate(final String firstName, final String lastName) {
        String subFirstName = firstName.substring(0, 2).toUpperCase();
        String subLastName = lastName.substring(0, 2).toUpperCase();
        var uuid = UUID.randomUUID();
        String id = "EMP-".concat(subFirstName)
                .concat(subLastName)
                .concat("-")
                .concat(uuid.toString());
        return new EmployeeId(id);
    }
}
