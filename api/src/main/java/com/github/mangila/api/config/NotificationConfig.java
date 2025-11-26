package com.github.mangila.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.shared.OutboxPgNotificationListener;
import com.github.mangila.api.shared.SpringEventPublisher;
import com.zaxxer.hikari.HikariConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class NotificationConfig {

    @Bean
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
}
