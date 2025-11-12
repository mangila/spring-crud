package com.github.mangila.app.service;

import com.github.mangila.app.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false"
        }
)
class EmployeeEventServiceTest {

    @Autowired
    private EmployeeEventService service;

    @Test
    void publishCreateNewEvent() {
    }

    @Test
    void publishUpdateEvent() {
    }

    @Test
    void publishSoftDeleteEvent() {
    }
}