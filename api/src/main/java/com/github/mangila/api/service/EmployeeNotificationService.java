package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.shared.ApplicationTaskExecutor;
import com.github.mangila.api.shared.OutboxPgNotificationListener;
import com.github.mangila.api.shared.PgNotificationListener;
import com.github.mangila.api.shared.SpringEventPublisher;
import com.zaxxer.hikari.HikariConfig;
import io.github.mangila.ensure4j.Ensure;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGNotification;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final PgNotificationListener pgListener;
    private final ApplicationTaskExecutor applicationTaskExecutor;
    private final ObjectMapper objectMapper;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public EmployeeNotificationService(HikariConfig hikariConfig,
                                       FlywayProperties flywayProperties,
                                       SpringEventPublisher publisher,
                                       ApplicationTaskExecutor applicationTaskExecutor,
                                       ObjectMapper objectMapper) {
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
    }

    @Scheduled(
            fixedDelay = 10,
            timeUnit = TimeUnit.SECONDS,
            initialDelay = 10
    )
    public void healthProbe() {
        if (!started.get()) {
            try {
                start();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * CAS swapping the started atomic boolean and starting the listener.
     * Scheduled health-probe is checking in on the variable if the connection was lost.
     * <br>
     * Eager approach in how to start the listener, check if the connection is valid if not reset the connection.
     * If the connection to the database is lost, it will continue until the application is restarted.
     */
    public void start() throws SQLException {
        log.info("Starting OutboxPgNotificationListener");
        boolean isSwapped = started.compareAndSet(false, true);
        Ensure.isTrue(isSwapped, "Listener already started");
        if (!pgListener.isValid()) {
            pgListener.resetConnection();
        }
        ObjectNode node = objectMapper.createObjectNode();
        node.put("executedBy", "EmployeeNotificationService");
        node.put("channel", pgListener.channel());
        applicationTaskExecutor.execute(pgListener, node)
                .whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        log.error("Listener failed", throwable);
                    }
                    started.set(false);
                });
    }

    @EventListener
    @Async
    public void listen(PGNotification[] notifications) {
        log.info("Received notification: {}", Arrays.toString(notifications));
    }

    @PreDestroy
    void preDestroy() {
        pgListener.destroy();
    }
}
