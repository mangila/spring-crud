package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.config.EmployeeSseEmitters;
import com.github.mangila.api.model.employee.domain.EmployeeId;
import com.github.mangila.api.shared.OutboxPgNotificationListener;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import com.github.mangila.api.shared.SpringEventPublisher;
import com.zaxxer.hikari.HikariConfig;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGNotification;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Notification service, mainly for fire and forget flows.
 * Well suited for Server Sent Events (SSE).
 * If some kind of acknowledgment (ACK) is needed, a Websocket (WS) can be used
 * or an acknowledgment endpoint from REST can be used.
 * <br>
 */
@Service
@Slf4j
public class EmployeeNotificationService {

    private final EmployeeSseEmitters sseEmitters;
    private final OutboxPgNotificationListener pgListener;
    private final ApplicationTaskExecutor applicationTaskExecutor;
    private final ObjectMapper objectMapper;

    public EmployeeNotificationService(HikariConfig hikariConfig,
                                       FlywayProperties flywayProperties,
                                       EmployeeSseEmitters sseEmitters,
                                       SpringEventPublisher publisher,
                                       ApplicationTaskExecutor applicationTaskExecutor,
                                       ObjectMapper objectMapper) {
        this.sseEmitters = sseEmitters;
        this.applicationTaskExecutor = applicationTaskExecutor;
        this.objectMapper = objectMapper;
        this.pgListener = new OutboxPgNotificationListener(
                new SingleConnectionDataSource(
                        hikariConfig.getJdbcUrl(),
                        hikariConfig.getUsername(),
                        hikariConfig.getPassword(),
                        true
                ),
                publisher
        );
        pgListener.setRunning(true);
    }

    @Scheduled(
            fixedDelay = 10,
            timeUnit = java.util.concurrent.TimeUnit.SECONDS
    )
    public void pollNotifications() throws SQLException {
        log.info("Starting OutboxPgNotificationListener");
        if (!pgListener.isRunning()) {
            log.warn("OutboxPgNotificationListener is not running");
            return;
        }
        if (!pgListener.isValid()) {
            pgListener.resetConnection();
            pgListener.getJdbc().execute((Connection c) -> {
                c.setAutoCommit(true);
                //language=PostgreSQL
                final String sql = "LISTEN %s".formatted(pgListener.channel());
                c.createStatement().execute(sql);
                return 0;
            });
        }
        CompletableFuture<Void> future = applicationTaskExecutor.submitCompletable(pgListener);
        try {
            future.join();
        } catch (Exception e) {
            log.error("Error polling notifications", e);
        }
    }

    @EventListener
    @Async
    public void listen(PGNotification[] notifications) {
        for (PGNotification notification : notifications) {
            String channel = notification.getName();
            String notificationPayload = notification.getParameter();
            try {
                ObjectNode objectNode = objectMapper.readValue(notificationPayload, ObjectNode.class);
                String id = objectNode.get("id").asText();
                String aggregateId = objectNode.get("aggregate_id").asText();
                String eventName = objectNode.get("event_name").asText();
                String jsonPayload = objectNode.get("payload")
                        .get("dto")
                        .toString();
                sseEmitters.get(new EmployeeId(aggregateId))
                        .forEach(sseEmitter -> {
                            var sseEvent = SseEmitter.event()
                                    .id(id)
                                    .name(eventName)
                                    .data(jsonPayload, MediaType.APPLICATION_JSON)
                                    .reconnectTime(Duration.ofSeconds(5).toMillis())
                                    .comment("Event sent by %s".formatted(channel))
                                    .build();
                            try {
                                sseEmitter.send(sseEvent);
                            } catch (IOException e) {
                                log.error("Error sending SSE event: {}", sseEvent, e);
                            }
                        });
            } catch (Exception e) {
                log.error("Not valid Notification JSON payload: {} - {}", channel, notificationPayload);
            }
        }
    }

    @PreDestroy
    void preDestroy() {
        log.info("Shutting down OutboxPgNotificationListener");
        pgListener.setRunning(false);
        pgListener.destroy();
    }
}
