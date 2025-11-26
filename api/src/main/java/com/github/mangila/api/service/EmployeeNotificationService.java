package com.github.mangila.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.config.EmployeeSseEmitters;
import com.github.mangila.api.model.employee.domain.EmployeeId;
import com.github.mangila.api.shared.OutboxPgNotificationWatcher;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGNotification;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Notification service, mainly for fire and forget flows.
 * Well suited for Server Sent Events (SSE).
 * If some kind of acknowledgment (ACK) is needed, a Websocket (WS) can be used
 * or an acknowledgment endpoint from REST can be used.
 * <br>
 * Service listens to events published from Spring event bus, that is delegated from a Postgres LISTEN/NOTIFY Thread
 * <br>
 * Scheduled task is doing a health check on the Postgres Thread
 */
@Slf4j
public class EmployeeNotificationService {

    private final OutboxPgNotificationWatcher watcher;
    private final EmployeeSseEmitters sseEmitters;
    private final ObjectMapper objectMapper;

    public EmployeeNotificationService(OutboxPgNotificationWatcher watcher,
                                       EmployeeSseEmitters sseEmitters,
                                       ObjectMapper objectMapper) {
        this.watcher = watcher;
        this.sseEmitters = sseEmitters;
        this.objectMapper = objectMapper;
    }

    @Scheduled(
            fixedRate = 10,
            timeUnit = TimeUnit.SECONDS
    )
    public void healthProbe() {
        if (watcher.isShutdown()) {
            return;
        }
        if (!watcher.isRunning()) {
            log.info("Resetting OutboxPgNotificationListener PostgreSQL connection");
            watcher.reset();
            watcher.start();
        }
    }

    @EventListener
    @Async
    public void listen(PGNotification[] notifications) {
        for (PGNotification notification : notifications) {
            String channel = notification.getName();
            String notificationPayload = notification.getParameter();
            log.info("Received Notification Event: {} - {}", channel, notificationPayload);
            try {
                ObjectNode objectNode = objectMapper.readValue(notificationPayload, ObjectNode.class);
                String id = objectNode.get("id").asText();
                String aggregateId = objectNode.get("aggregate_id").asText();
                String eventName = objectNode.get("event_name").asText();
                String jsonPayload = objectNode.get("payload")
                        .get("dto")
                        .toString();
                EmployeeId employeeId = new EmployeeId(aggregateId);
                CopyOnWriteArrayList<SseEmitter> emitters = sseEmitters.get(employeeId);
                if (emitters == null) {
                    log.info("No emitters found for employeeId: {}", employeeId);
                    continue;
                }
                emitters.forEach(sseEmitter -> {
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
                log.error("Failed to parse Notification Event: {} - {}", channel, notificationPayload, e);
            }
        }
    }
}
