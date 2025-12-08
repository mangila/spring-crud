package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.model.EmployeeSseEmitters;
import com.github.mangila.api.model.FileUploadRequest;
import com.github.mangila.api.model.FileUploadTaskQueue;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.domain.EmployeeId;
import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.api.model.employee.dto.EmployeeDto;
import com.github.mangila.api.model.employee.dto.EmployeeEventDto;
import com.github.mangila.api.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.api.model.task.TaskExecutionEntity;
import com.github.mangila.api.repository.TaskExecutionJpaRepository;
import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.ensure4j.EnsureException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Facade for REST endpoints.
 * <p>
 * Facade is responsible for orchestrating REST API calls.
 * <br>
 * When introducing a new protocol or input to the system, this will make it easier to adapt for the service layer.
 * E.g., EmployeeGrpcFacade, EmployeeKafkaFacade, EmployeeQuantumFacade :)
 * <br>
 * Bridge, Facade, Adapter... you name it.
 *
 */
@Service
@Slf4j
public class EmployeeRestFacade {

    private final EmployeeService service;
    private final EmployeeSseEmitters sseEmitters;
    private final EmployeeDtoMapper dtoMapper;
    private final EmployeeEventMapper eventMapper;
    private final EmployeeDomainMapper domainMapper;
    private final EmployeeFactory factory;
    private final FileUploadTaskQueue fileUploadTaskQueue;
    private final TaskExecutionJpaRepository taskExecutionRepository;

    public EmployeeRestFacade(EmployeeService service,
                              EmployeeSseEmitters sseEmitters,
                              EmployeeDtoMapper dtoMapper,
                              EmployeeEventMapper eventMapper,
                              EmployeeDomainMapper domainMapper,
                              EmployeeFactory factory,
                              FileUploadTaskQueue fileUploadTaskQueue,
                              TaskExecutionJpaRepository taskExecutionRepository) {
        this.service = service;
        this.sseEmitters = sseEmitters;
        this.dtoMapper = dtoMapper;
        this.eventMapper = eventMapper;
        this.domainMapper = domainMapper;
        this.factory = factory;
        this.fileUploadTaskQueue = fileUploadTaskQueue;
        this.taskExecutionRepository = taskExecutionRepository;
    }

    public EmployeeDto findEmployeeById(String employeeId) {
        EmployeeId id = new EmployeeId(employeeId);
        Employee employee = service.findEmployeeById(id);
        return dtoMapper.map(employee);
    }

    public Page<EmployeeDto> findAllEmployeesByPage(Pageable pageable) {
        return service.findAllEmployeesByPage(pageable)
                .map(dtoMapper::map);
    }

    public String createNewEmployee(CreateNewEmployeeRequest request) {
        Employee employee = factory.from(request);
        service.createNewEmployee(employee);
        return employee.id().value();
    }

    public EmployeeDto updateEmployee(UpdateEmployeeRequest request) {
        Employee employee = domainMapper.map(request);
        // Start a transaction and update the employee in the db
        service.updateEmployee(employee);
        // Fetch the updated employee after the transaction commit
        employee = service.findEmployeeById(employee.id());
        return dtoMapper.map(employee);
    }

    public void softDeleteEmployeeById(String employeeId) {
        EmployeeId id = new EmployeeId(employeeId);
        service.softDeleteEmployeeById(id);
    }

    public Page<@Nullable EmployeeDto> replayEmployee(String employeeId, Pageable pageable) {
        EmployeeId id = new EmployeeId(employeeId);
        return service.replayEmployee(id, pageable)
                .map(entity -> {
                    log.info("Outbox event: {}", entity);
                    // the event payload JSON must have a "dto" key
                    ObjectNode dto = (ObjectNode) entity
                            .getPayload()
                            .get("dto");
                    if (dto != null && dto.isObject()) {
                        EmployeeEventDto eventDto = eventMapper.map(dto);
                        return dtoMapper.map(eventDto);
                    }
                    log.warn("Event missing 'dto' key: {} - {}", entity.getEventName(), entity.getId());
                    return null;
                });
    }

    public SseEmitter createNewSseEmitter(String employeeId) {
        EmployeeId id = new EmployeeId(employeeId);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.put(id, emitter);
        emitter.onCompletion(() -> sseEmitters.remove(id, emitter));
        emitter.onTimeout(() -> sseEmitters.remove(id, emitter));
        emitter.onError(e -> {
            log.error("SseEmitter error: {}", e.getMessage());
            sseEmitters.remove(id, emitter);
        });
        return emitter;
    }

    public String fileUpload(MultipartFile file) {
        log.info("File upload: {}", file.getOriginalFilename());
        String contentType = file.getContentType();
        switch (contentType) {
            case "text/csv",
                 MediaType.APPLICATION_XML_VALUE,
                 MediaType.APPLICATION_JSON_VALUE -> {
                try {
                    String fileId = UUID.randomUUID().toString();
                    Path out = Paths.get(fileId);
                    Ensure.isTrue(out.toFile().createNewFile(), "File already exists: %s".formatted(out.toString()));
                    file.transferTo(out);
                    fileUploadTaskQueue.put(new FileUploadRequest(out, fileId, file.getOriginalFilename(), contentType));
                    return fileId;
                } catch (EnsureException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case null -> throw new RuntimeException("null file content type");
            default -> throw new IllegalStateException("Unexpected value: %s".formatted(contentType));
        }
    }

    /**
     * Make sure it's a valid UUID, since that is being used as fileId as taskName
     */
    public List<TaskExecutionEntity> fileStatus(String fileId) {
        try {
            UUID.fromString(fileId);
            return taskExecutionRepository.findAllByTaskName(fileId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
