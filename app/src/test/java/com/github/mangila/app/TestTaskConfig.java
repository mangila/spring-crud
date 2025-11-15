package com.github.mangila.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.scheduler.Task;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestTaskConfig {

    @Bean
    public TestTask testTask(ObjectMapper objectMapper) {
        return new TestTask(objectMapper);
    }


    public static class TestTask implements Task {

        private final ObjectMapper objectMapper;

        public TestTask(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public String name() {
            return this.getClass().getSimpleName();
        }

        @Override
        public ObjectNode call() {
            return objectMapper.createObjectNode()
                    .put("test", "test");
        }
    }

}
