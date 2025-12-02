package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.EmployeeTestFactory;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.ReusablePostgresTestContainerConfiguration;
import com.github.mangila.api.model.employee.domain.Employee;
import com.github.mangila.api.model.employee.domain.EmployeeId;
import com.github.mangila.api.model.employee.domain.EmployeeName;
import com.github.mangila.api.model.employee.domain.EmployeeSalary;
import com.github.mangila.api.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.api.model.employee.entity.EmployeeEntity;
import com.github.mangila.api.model.employee.type.EmploymentActivity;
import com.github.mangila.api.model.employee.type.EmploymentStatus;
import com.github.mangila.api.model.outbox.OutboxEntity;
import com.github.mangila.api.repository.EmployeeJpaRepository;
import com.github.mangila.api.shared.exception.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.inOrder;

/**
 * Full-scale service test with integration towards a database.
 * <br>
 * Some might use a Mock here towards the database.
 * <br>
 * If this might be developed in an Enterprise setting when behind a corporate firewall,
 * a CI/CD server might not be able to spin up a Testcontainers (might not even have Docker installed),
 * so it won't be able to fetch from Docker public repositories. Some Enterprise settings may have this already set up with a private repository for testing.
 * In that case you can use a Mock here and have a separate local disabled test to run against a real database with testcontainers.
 * Or just do prod-testing (recommended).
 * <br>
 * But here we use a real database from Docker public repositories.
 * <br>
 * Repository, Mapper and Event publisher is wired as SpyBeans just to make sure they invoke its expected method.
 */
@Import(ReusablePostgresTestContainerConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false",
                "application.notification.enabled=false"
        }
)
class EmployeeServiceTest {

    @Autowired
    private EmployeeService service;

    @Autowired
    private EmployeeFactory factory;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private EmployeeEventService eventService;

    @MockitoSpyBean
    private EmployeeJpaRepository repository;

    @MockitoSpyBean
    private EmployeeDomainMapper domainMapper;

    @MockitoSpyBean
    private EmployeeEntityMapper entityMapper;

    @AfterEach
    void cleanup() {
        var example = Example.of(new EmployeeEntity(), ExampleMatcher.matchingAny());
        repository.findAll(example)
                .forEach(entity -> repository.delete(entity));
    }

    @Test
    @DisplayName("Find Employee by ID not exists should throw")
    void findEmployeeByIdNotFound() {
        EmployeeId id = EmployeeTestFactory.createEmployeeId();
        assertThatThrownBy(() -> service.findEmployeeById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Entity with id: EMP-JODO-00000000-0000-0000-0000-000000000000");
    }

    @Test
    @DisplayName("Soft delete Employee ID not found should throw")
    void softDeleteEmployeeByIdNotFound() {
        EmployeeId id = EmployeeTestFactory.createEmployeeId();
        assertThatThrownBy(() -> service.softDeleteEmployeeById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Entity with id: EMP-JODO-00000000-0000-0000-0000-000000000000");
    }

    @Test
    @DisplayName("Update Employee ID not found should throw")
    void updateEmployeeByIdNotFound() throws IOException {
        Employee employee = EmployeeTestFactory.createEmployee(objectMapper);
        assertThatThrownBy(() -> service.updateEmployee(employee))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Entity with id: EMP-JODO-00000000-0000-0000-0000-000000000000");
    }

    @Test
    @DisplayName("Should C.R.U.D Employee")
    void crudEmployee() throws IOException {
        EmployeeId employeeId = create();
        Employee employee = read(employeeId);
        assertNewEmployee(employee);
        update(employee);
        employee = read(employeeId);
        assertUpdatedEmployee(employee);
        delete(employeeId);
        employee = read(employeeId);
        assertThat(employee.audit().deleted())
                .isTrue();
    }

    private EmployeeId create() throws IOException {
        CreateNewEmployeeRequest request = EmployeeTestFactory.createNewEmployeeRequest(objectMapper);
        Employee employee = factory.from(request);
        service.createNewEmployee(employee);
        var inOrder = inOrder(entityMapper, repository, eventService);
        inOrder.verify(entityMapper).map(any(Employee.class));
        inOrder.verify(repository).persist(any(EmployeeEntity.class));
        inOrder.verify(eventService).publishCreateNewEvent(any(Employee.class));
        clearInvocations(entityMapper, repository, eventService);
        return employee.id();
    }

    private Employee read(EmployeeId employeeId) {
        Employee employee = service.findEmployeeById(employeeId);
        var inOrder = inOrder(repository, domainMapper);
        inOrder.verify(repository).findById(any(String.class));
        inOrder.verify(domainMapper).map(any(EmployeeEntity.class));
        clearInvocations(repository, domainMapper);
        return employee;
    }

    private void assertNewEmployee(Employee employee) {
        assertThat(employee)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasOnlyFields(
                        "id",
                        "firstName",
                        "lastName",
                        "salary",
                        "employmentActivity",
                        "employmentStatus",
                        "attributes",
                        "audit"
                )
                .hasFieldOrPropertyWithValue("id", employee.id())
                .hasFieldOrPropertyWithValue("firstName", new EmployeeName("John"))
                .hasFieldOrPropertyWithValue("lastName", new EmployeeName("Doe"))
                .hasFieldOrPropertyWithValue("salary", new EmployeeSalary(new BigDecimal("20000.12")))
                .hasFieldOrPropertyWithValue("employmentActivity", EmploymentActivity.FULL_TIME)
                .hasFieldOrPropertyWithValue("employmentStatus", EmploymentStatus.ACTIVE)
                .hasFieldOrProperty("attributes")
                .hasFieldOrProperty("audit");
        // Assert JSON attributes
        var jsonAttributes = employee.attributes().value();
        var jsonAttibutesString = jsonAttributes.toString();
        assertThatJson(jsonAttibutesString)
                .isObject()
                .containsOnlyKeys(
                        "vegan",
                        "pronouns",
                        "lizard_people",
                        "licenses",
                        "evaluation"
                )
                .hasSize(5)
                .containsEntry("vegan", true)
                .containsEntry("pronouns", "he/him")
                .containsEntry("lizard_people", null)
                .hasEntrySatisfying("licenses", json -> {
                    assertThatJson(json)
                            .isArray()
                            .hasSize(2)
                            .containsExactly(
                                    "PP7",
                                    "Klobb"
                            );
                })
                .hasEntrySatisfying("evaluation", json -> {
                    assertThatJson(json)
                            .isObject()
                            .hasSize(2)
                            .containsExactly(
                                    Map.entry("medical", "FAIL"),
                                    Map.entry("physical", "FAIL")
                            );
                });
        // Assert Employee Audit
        assertThat(employee.audit())
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasOnlyFields(
                        "created",
                        "modified",
                        "deleted"
                );
        // Verify created and modified dates are somewhere in the last 5 seconds
        assertThat(employee.audit().created())
                .isNotNull()
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        assertThat(employee.audit().modified())
                .isNotNull()
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        assertThat(employee.audit().deleted())
                .isFalse();
    }

    private void update(Employee employee) {
        var request = EmployeeTestFactory.createUpdateEmployeeRequestBuilder(objectMapper)
                .employeeId(employee.id().value())
                .salary(employee.salary().value().add(new BigDecimal("20.00")))
                .employmentActivity(EmploymentActivity.PART_TIME)
                .attributes(employee.attributes().value().put("vegan", false))
                .build();
        employee = domainMapper.map(request);
        clearInvocations(domainMapper); // clear here because it is a preparation for the test
        service.updateEmployee(employee);
        var inOrder = inOrder(repository, entityMapper, domainMapper, eventService);
        inOrder.verify(repository).findById(any(String.class));
        inOrder.verify(entityMapper).map(any(Employee.class));
        inOrder.verify(repository).merge(any(EmployeeEntity.class));
        inOrder.verify(domainMapper).map(any(EmployeeEntity.class));
        inOrder.verify(eventService).publishUpdateEvent(any(Employee.class));
        clearInvocations(repository, entityMapper, domainMapper, eventService);
    }

    private void assertUpdatedEmployee(Employee employee) {
        assertThat(employee)
                .hasFieldOrPropertyWithValue("salary", new EmployeeSalary(new BigDecimal("20020.12")))
                .hasFieldOrPropertyWithValue("employmentActivity", EmploymentActivity.PART_TIME);
        // Assert JSON attributes
        var jsonAttributes = employee.attributes().value();
        var jsonAttibutesString = jsonAttributes.toString();
        assertThatJson(jsonAttibutesString)
                .isObject()
                .containsOnlyKeys(
                        "vegan",
                        "pronouns",
                        "lizard_people",
                        "licenses",
                        "evaluation"
                )
                .hasSize(5)
                .containsEntry("vegan", false);
        // Verify created and modified dates are somewhere in the last 5 seconds
        assertThat(employee.audit().created())
                .isNotNull()
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        assertThat(employee.audit().modified())
                .isNotNull()
                .isCloseTo(Instant.now(), within(Duration.ofSeconds(5)));
        assertThat(employee.audit().deleted())
                .isFalse();
    }

    private void delete(EmployeeId employeeId) {
        service.softDeleteEmployeeById(employeeId);
        var inOrder = inOrder(repository, domainMapper, eventService);
        inOrder.verify(repository).findById(any(String.class));
        inOrder.verify(repository).merge(any(EmployeeEntity.class));
        inOrder.verify(domainMapper).map(any(EmployeeEntity.class));
        inOrder.verify(eventService).publishSoftDeleteEvent(any(Employee.class));
        clearInvocations(repository, domainMapper, eventService);
    }

    @Test
    void findAllEmployeesByPage() throws IOException {
        create();
        var page = service.findAllEmployeesByPage(Pageable.unpaged());
        assertThat(page.getContent())
                .hasSize(1);
        create();
        page = service.findAllEmployeesByPage(Pageable.unpaged());
        assertThat(page.getContent())
                .hasSize(2);
    }

    @Test
    void replay() throws IOException {
        EmployeeId employeeId = create();
        Page<OutboxEntity> page = service.replayEmployee(employeeId, Pageable.unpaged());
        assertThat(page.getContent())
                .hasSize(1);
        update(read(employeeId));
        page = service.replayEmployee(employeeId, Pageable.unpaged());
        assertThat(page.getContent())
                .hasSize(2);
    }
}