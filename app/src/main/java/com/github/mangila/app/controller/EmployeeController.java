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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
// Use a nouns or no noun for the resource is the question.
// the noun fit well in this domain, if you think of it we are basically querying a collection of employees as a REST endpoint.
// and creating endpoints how we want to query the employee collection.
// the api versioning is very convenient, if huge breaking changes would occur a new controller v2 can be created.
@RequestMapping("api/v1/employees")
@Validated
public class EmployeeController {

    private final EmployeeRestFacade restFacade;

    public EmployeeController(EmployeeRestFacade restFacade) {
        this.restFacade = restFacade;
    }

    @GetMapping(value = "{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EmployeeDto> findEmployeeById(
            @PathVariable
            @NotBlank
            @ValidEmployeeId
            String employeeId) {
        return ResponseEntity.ok()
                .body(restFacade.findEmployeeById(employeeId));
    }

    @GetMapping(value = "replay/{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<EmployeeDto>> replayEmployee(
            @PathVariable
            @NotBlank
            @ValidEmployeeId
            String employeeId) {
        return ResponseEntity.ok().body(restFacade.replayEmployee(employeeId));
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
                .path("/api/v1/employees/{employeeId}")
                .build(id);
        return ResponseEntity.created(location)
                .build();
    }

    @PutMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EmployeeDto> updateEmployee(
            @RequestBody
            @NotNull
            @Valid UpdateEmployeeRequest request
    ) {
        EmployeeDto dto = restFacade.updateEmployee(request);
        return ResponseEntity.ok()
                .body(dto);
    }

    @DeleteMapping("{employeeId}")
    public ResponseEntity<?> softDeleteEmployeeById(
            @PathVariable
            @NotBlank
            @ValidEmployeeId String employeeId
    ) {
        restFacade.softDeleteEmployeeById(employeeId);
        return ResponseEntity.noContent()
                .build();
    }
}
