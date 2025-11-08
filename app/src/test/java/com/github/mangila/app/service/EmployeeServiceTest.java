package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.domain.EmployeeId;
import com.github.mangila.app.model.employee.domain.EmployeeName;
import com.github.mangila.app.model.employee.domain.EmployeeSalary;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.dto.UpdateEmployeeRequest;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.model.employee.event.CreateNewEmployeeEvent;
import com.github.mangila.app.model.employee.event.SoftDeleteEmployeeEvent;
import com.github.mangila.app.model.employee.event.UpdateEmployeeEvent;
import com.github.mangila.app.model.employee.type.EmploymentActivity;
import com.github.mangila.app.model.employee.type.EmploymentStatus;
import com.github.mangila.app.repository.EmployeeJpaRepository;
import com.github.mangila.app.shared.SpringEventPublisher;
import com.github.mangila.app.shared.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EmployeeServiceTest {

    @Autowired
    private EmployeeService service;

    @Autowired
    private EmployeeFactory factory;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private SpringEventPublisher publisher;

    @MockitoSpyBean
    private EmployeeJpaRepository repository;

    @MockitoSpyBean
    private EmployeeDomainMapper domainMapper;

    @MockitoSpyBean
    private EmployeeEntityMapper entityMapper;

    @Test
    @DisplayName("Should not find Employee by Id and throw")
    void shouldNotFindEmployeeByIdAndThrow() {
        EmployeeId id = ObjectFactoryUtil.createFakeEmployeeId();
        assertThatThrownBy(() -> service.findEmployeeById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Employee with id: (EMP-JODO-00000000-0000-0000-0000-000000000000) not found");
        verify(repository, times(1)).findById(any());
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
        CreateNewEmployeeRequest request = ObjectFactoryUtil.createNewEmployeeRequest(objectMapper);
        Employee employee = factory.from(request);
        service.createNewEmployee(employee);
        verify(entityMapper, times(1)).map(any(Employee.class));
        verify(repository, times(1)).save(any(EmployeeEntity.class));
        verify(publisher, times(1)).publish(any(CreateNewEmployeeEvent.class));
        clearInvocations(entityMapper, repository, publisher);
        return employee.id();
    }

    private Employee read(EmployeeId employeeId) {
        Employee employee = service.findEmployeeById(employeeId);
        verify(repository, times(1)).findById(any(String.class));
        verify(domainMapper, times(1)).map(any(EmployeeEntity.class));
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
                .hasFieldOrPropertyWithValue("employmentStatus", EmploymentStatus.ACTIVE);
        // Assert JSON attributes
        var jsonAttributes = employee.attributes().value();
        var jsonAttibutesString = jsonAttributes.toString();
        assertThatJson(jsonAttibutesString)
                .isObject()
                .containsOnlyKeys(
                        "vegan",
                        "pronouns",
                        "licenses",
                        "evaluation",
                        "substance_addiction",
                        "secret_number",
                        "notes"
                )
                .hasSize(7)
                .containsEntry("vegan", true)
                .containsEntry("pronouns", "he/him")
                .containsEntry("substance_addiction", true)
                .containsEntry("secret_number", "123")
                .containsEntry("notes", "subject is not approved for field duty, immediate suspension advised")
                .node("") // just to be able to traverse the whole JSON tree, maybe there is a better way to do this
                .and(jsonAssert -> jsonAssert.node("licenses")
                        .isArray()
                        .hasSize(3)
                        .containsExactly(
                                "PP7",
                                "Klobb",
                                "DD44 Dostovei"
                        ))
                .and(jsonAssert -> jsonAssert.node("evaluation")
                        .isObject()
                        .hasSize(3)
                        .containsExactly(
                                Map.entry("medical", "FAIL"),
                                Map.entry("physical", "FAIL"),
                                Map.entry("psychological", "FAIL")
                        ));
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
        var request = new UpdateEmployeeRequest(
                employee.id().value(),
                employee.firstName().value(),
                employee.lastName().value(),
                employee.salary().value().add(new BigDecimal("20.00")),
                EmploymentActivity.PART_TIME,
                EmploymentStatus.ACTIVE,
                employee.attributes().value().put("vegan", false)
        );
        employee = domainMapper.map(request);
        clearInvocations(domainMapper); // clear here since we just map to use UpdateEmployeeRequest
        service.updateEmployee(employee);
        verify(repository, times(1)).existsById(any(String.class));
        verify(entityMapper, times(1)).map(any(Employee.class));
        verify(repository, times(1)).save(any(EmployeeEntity.class));
        verify(publisher, times(1)).publish(any(UpdateEmployeeEvent.class));
        clearInvocations(repository, entityMapper, publisher);
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
                        "licenses",
                        "evaluation",
                        "substance_addiction",
                        "secret_number",
                        "notes"
                )
                .hasSize(7)
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
        verify(repository, times(1)).existsById(any(String.class));
        verify(repository, times(1)).softDeleteByEmployeeId(any(EmployeeId.class));
        verify(publisher, times(1)).publish(any(SoftDeleteEmployeeEvent.class));
        clearInvocations(repository, publisher);
    }

    @Test
    void updateEmployee() throws IOException {
        // Set up a new Employee in the database
        CreateNewEmployeeRequest request = ObjectFactoryUtil.createNewEmployeeRequest(objectMapper);
        Employee employee = factory.from(request);
        service.createNewEmployee(employee);
        // Create a update employee request
        var updateRequest = new UpdateEmployeeRequest(
                employee.id().value(),
                employee.firstName().value(),
                employee.lastName().value(),
                employee.salary().value().add(new BigDecimal("10.00")),
                employee.employmentActivity(),
                employee.employmentStatus(),
                employee.attributes().value().put("vegan", false)
        );
        employee = domainMapper.map(updateRequest);
        // Invoke the service
        service.updateEmployee(employee);
        verify(repository, times(1)).existsById(any());
        verify(repository, times(1)).save(any(EmployeeEntity.class));
        // This is a Fire And Forget mechanism that runs Async,
        // so let's just verify the invocation from the service Point of Execution
        verify(publisher, times(1)).publish(any(CreateNewEmployeeEvent.class));
        // Verify updated Employee
        employee = service.findEmployeeById(employee.id());
        System.out.println(employee);
    }

    @Test
    void softDeleteEmployeeById() {
        assertThat(1 + 1).isEqualTo(3);
    }

    @Test
    void findAllEmployeesByPage() {
        assertThat(1 + 1).isEqualTo(3);
    }
}