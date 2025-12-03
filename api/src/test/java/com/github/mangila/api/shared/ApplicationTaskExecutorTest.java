package com.github.mangila.api.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.PostgresTestContainerConfiguration;
import com.github.mangila.api.TestTaskConfig;
import com.github.mangila.api.repository.TaskExecutionJpaRepository;
import com.github.mangila.api.scheduler.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
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

    @Autowired
    private ApplicationTaskExecutor taskExecutor;

    @MockitoSpyBean
    private SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    @MockitoSpyBean
    private TaskExecutionJpaRepository taskExecutionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTaskConfig.TestTask testTask;

    @Test
    void submitCompletable() {
        var future = taskExecutor.submitCompletable(testTask, objectMapper.createObjectNode());
        verify(simpleAsyncTaskExecutor, times(1)).submitCompletable(any(Task.class));
        verify(taskExecutionRepository, times(1)).persist(any());
        await()
                .timeout(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(taskExecutionRepository, times(1)).merge(any());
                });
        var unused = future.thenAccept(objectNode -> assertThat(objectNode.has("test")).isTrue());
    }
}