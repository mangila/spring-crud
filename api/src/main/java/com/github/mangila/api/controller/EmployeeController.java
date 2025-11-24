package com.github.mangila.api.controller;

import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.api.model.employee.dto.EmployeeDto;
import com.github.mangila.api.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.api.service.EmployeeRestFacade;
import com.github.mangila.api.shared.annotation.ValidEmployeeId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * <p>
 * Use a noun or no noun for the resource is the question.
 * The noun fit well in this domain, if you think about it, we are basically querying a collection of employees as a REST endpoint.
 * The endpoints are created how we want to use the employee collection.
 * </p>
 * <p>
 * The api versioning is very convenient, if huge breaking changes occur, a new controller v2 can be created.
 * <br>
 * e.g., /api/v1/employees, /api/v2/employees
 * <br>
 * e.g., A brand new DTO is introduced, and we still have consumers of the old DTO.
 * <br>
 * Can be anything really that breaks the current REST contract
 * </p>
 */
@RestController
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

    @GetMapping(value = "{employeeId}/events",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public SseEmitter subscribeToEmployeeEvents(
            @PathVariable
            @NotBlank
            @ValidEmployeeId
            String employeeId
    ) {
        SseEmitter.event().build();
        return null;
    }

    @GetMapping(value = "replay/{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PagedModel<EmployeeDto> replayEmployee(
            @PathVariable
            @NotBlank
            @ValidEmployeeId
            String employeeId,
            @NotNull Pageable pageable) {
        return new PagedModel<>(restFacade.replayEmployee(employeeId, pageable));
    }

    @GetMapping
    public PagedModel<EmployeeDto> findAllEmployeesByPage(Pageable pageable) {
        return new PagedModel<>(restFacade.findAllEmployeesByPage(pageable));
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
