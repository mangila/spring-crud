package com.github.mangila.app.controller;

import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.service.EmployeeRestFacade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/v1/employee")
@Validated
public class EmployeeController {

    private final EmployeeRestFacade restFacade;

    public EmployeeController(EmployeeRestFacade restFacade) {
        this.restFacade = restFacade;
    }

    @GetMapping("{employeeId}")
    public ResponseEntity<EmployeeDto> findEmployeeById(
            @PathVariable
            @NotBlank
            @Size(min = 36, max = 36) String employeeId) {
        return ResponseEntity.ok(restFacade.findEmployeeById(employeeId));
    }

    @PostMapping
    public ResponseEntity<?> createNewEmployee(
            @NotNull CreateNewEmployeeRequest request
    ) {
        restFacade.createNewEmployee(request);
        URI location = UriComponentsBuilder.fromUriString("")
                .build()
                .toUri();
        return ResponseEntity.created(location)
                .build();
    }

    @PatchMapping
    public ResponseEntity<?> updateEmployee(
            @NotNull UpdateEmployeeRequest request
    ) {
        restFacade.updateEmployee(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{employeeId}")
    public ResponseEntity<?> softDeleteEmployeeById(
            @PathVariable
            @NotBlank
            @Size(min = 36, max = 36) String employeeId
    ) {
        restFacade.softDeleteEmployeeById(employeeId);
        return ResponseEntity.ok().build();
    }
}
