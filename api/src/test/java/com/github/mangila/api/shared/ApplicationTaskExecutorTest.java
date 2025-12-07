package com.github.mangila.api.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.TestTaskConfig;
import com.github.mangila.api.repository.TaskExecutionJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({PostgresTestContainerConfiguration.class, TestTaskConfig.class})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "application.scheduler.enabled=false",
                "application.notification.enabled=false"
        }
)
class ApplicationTaskExecutorTest {

    @MockitoSpyBean
    private ApplicationTaskExecutor taskExecutor;

    @MockitoSpyBean
    private TaskExecutionJpaRepository taskExecutionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTaskConfig.TestTask testTask;

    @Test
    void submitCompletable() {
        Mockito.clearInvocations(taskExecutor, taskExecutionRepository);
        var unused = taskExecutor.submitCompletable(testTask, objectMapper.createObjectNode())
                .join();
        verify(taskExecutor, times(1)).submitCompletable(any(), any());
        verify(taskExecutionRepository, times(1)).persist(any());
        verify(taskExecutionRepository, times(1)).merge(any());
    }
}