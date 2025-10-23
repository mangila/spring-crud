package com.github.mangila.app.service;

import com.github.mangila.app.model.employee.domain.EmployeeId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmployeeIdGenerator {
    public EmployeeId generate() {
        return new EmployeeId(UUID.randomUUID().toString());
    }
}
