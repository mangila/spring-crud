package com.github.mangila.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.ObjectFactoryUtil;
import com.github.mangila.app.TestcontainersConfiguration;
import com.github.mangila.app.model.employee.domain.Employee;
import com.github.mangila.app.model.employee.dto.CreateNewEmployeeRequest;
import com.github.mangila.app.model.employee.entity.EmployeeEntity;
import com.github.mangila.app.repository.EmployeeJpaRepository;
import com.github.mangila.app.shared.SpringEventPublisher;
import com.github.mangila.app.shared.event.NewEmployeeCreatedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
@ExtendWith(OutputCaptureExtension.class)
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
    private EmployeeMapper mapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findEmployeeById() {
        //  service.findEmployeeById(null);
    }

    @Test
    void findAllEmployeesByPage() {
    }

    @Test
    void createNewEmployee(CapturedOutput output) throws IOException {
        CreateNewEmployeeRequest request = ObjectFactoryUtil.createNewEmployeeRequest(objectMapper);
        Employee employee = factory.from(request);
        service.createNewEmployee(employee);
        verify(repository, times(1)).save(any(EmployeeEntity.class));
        verify(mapper, times(1)).toDomain(any(EmployeeEntity.class));
        // This is a Fire And Forget mechanism, so let's just verify the invocation from the service POV
        verify(publisher, times(1)).publish(any(NewEmployeeCreatedEvent.class));
        // With the expected log output from the received event
        assertThat(output).contains("New employee was created:");
        // Verify service returns the correct employee
        employee = service.findEmployeeById(employee.id());
        assertThat(employee.firstName().value())
                .isEqualTo("John");
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void softDeleteEmployeeById() {
    }
}