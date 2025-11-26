package com.github.mangila.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.service.EmployeeNotificationService;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import com.github.mangila.api.shared.OutboxPgNotificationListener;
import com.github.mangila.api.shared.OutboxPgNotificationWatcher;
import com.github.mangila.api.shared.SpringEventPublisher;
import com.zaxxer.hikari.HikariConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class NotificationConfig {

    @Bean
    @ConditionalOnProperty(
            name = "application.notification.enabled",
            havingValue = "true"
    )
    OutboxPgNotificationListener outboxPgNotificationListener(FlywayProperties flywayProperties,
                                                              HikariConfig hikariConfig,
                                                              SpringEventPublisher publisher,
                                                              ObjectMapper objectMapper
    ) {
        var dataSource = new SingleConnectionDataSource(
                hikariConfig.getJdbcUrl(),
                hikariConfig.getUsername(),
                hikariConfig.getPassword(),
                true
        );
        return new OutboxPgNotificationListener(
                dataSource,
                publisher,
                objectMapper
        );
    }

    @Bean
    @ConditionalOnProperty(
            name = "application.notification.enabled",
            havingValue = "true"
    )
    OutboxPgNotificationWatcher outboxPgNotificationWatcher(OutboxPgNotificationListener listener,
                                                            ApplicationTaskExecutor applicationTaskExecutor,
                                                            ObjectMapper objectMapper) {
        return new OutboxPgNotificationWatcher(listener, applicationTaskExecutor, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(
            name = "application.notification.enabled",
            havingValue = "true"
    )
    EmployeeNotificationService employeeNotificationService(OutboxPgNotificationWatcher watcher, EmployeeSseEmitters sseEmitters, ObjectMapper objectMapper) {
        return new EmployeeNotificationService(watcher, sseEmitters, objectMapper);
    }
}
