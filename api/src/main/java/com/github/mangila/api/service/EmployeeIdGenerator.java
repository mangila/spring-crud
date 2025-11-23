package com.github.mangila.api.service;

import com.github.mangila.api.model.employee.domain.EmployeeId;
import org.springframework.stereotype.Component;

import java.util.Locale;
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
     * There are a lot of edge cases to consider.
     * <br>
     * The final keyword for the strings is a construct for "design for immutability" or the Data Oriented Approach.
     * <br>
     * Locale.ROOT to use not the default locale it might get LOCALE string manipulation stuffs when converting to UPPERCASE
     */
    public EmployeeId generate(final String firstName, final String lastName) {
        String subFirstName = firstName.substring(0, 2).toUpperCase(Locale.ROOT);
        String subLastName = lastName.substring(0, 2).toUpperCase(Locale.ROOT);
        UUID uuid = UUID.randomUUID();
        String id = "EMP-".concat(subFirstName)
                .concat(subLastName)
                .concat("-")
                .concat(uuid.toString());
        return new EmployeeId(id);
    }
}
