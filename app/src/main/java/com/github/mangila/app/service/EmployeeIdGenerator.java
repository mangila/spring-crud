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
     * <br>
     * Roll out a unique identifier like this can be good or bad depending on the firstname or lastname
     * Prince (the musician) name change would be a good example. But he is in heaven with his little red corvette. RIP
     * There are alot of edge cases to consider.
     * <br>
     * The final keyword for the strings is a construct for "design for immutability" or the Data Oriented Approach.
     */
    public EmployeeId generate(final String firstName, final String lastName) {
        String subFirstName = firstName.substring(0, 2).toUpperCase();
        String subLastName = lastName.substring(0, 2).toUpperCase();
        UUID uuid = UUID.randomUUID();
        String id = "EMP-".concat(subFirstName)
                .concat(subLastName)
                .concat("-")
                .concat(uuid.toString());
        return new EmployeeId(id);
    }
}
