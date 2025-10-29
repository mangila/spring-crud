package com.github.mangila.app.controller;

import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.EmployeeDto;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.service.EmployeeRestFacade;
import com.github.mangila.app.shared.annotation.ValidEmployeeId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            @ValidEmployeeId
            String employeeId) {
        return ResponseEntity.ok(restFacade.findEmployeeById(employeeId));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> findAllEmployeesByPage(Pageable pageable) {
        return ResponseEntity.ok(restFacade.findAllEmployeesByPage(pageable));
    }

    @PostMapping
    public ResponseEntity<?> createNewEmployee(
            @RequestBody
            @NotNull
            @Valid
            CreateNewEmployeeRequest request
    ) {
        String id = restFacade.createNewEmployee(request);
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Location
        URI location = UriComponentsBuilder.newInstance()
                .path("/api/v1/employee/{employeeId}")
                .build(id);
        return ResponseEntity.created(location)
                .build();
    }

    @PutMapping
    public ResponseEntity<?> updateEmployee(
            @RequestBody
            @NotNull
            @Valid UpdateEmployeeRequest request
    ) {
        restFacade.updateEmployee(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{employeeId}")
    public ResponseEntity<?> softDeleteEmployeeById(
            @PathVariable
            @NotBlank
            @ValidEmployeeId String employeeId
    ) {
        restFacade.softDeleteEmployeeById(employeeId);
        return ResponseEntity.noContent().build();
    }
}
