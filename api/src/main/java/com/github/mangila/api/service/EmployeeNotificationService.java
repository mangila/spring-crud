package com.github.mangila.api.service;

import org.springframework.stereotype.Service;

/**
 * Notification service, mainly for fire and forget flows.
 * Well suited for Server Sent Events (SSE).
 * If some kind of acknowledgment (ACK) is needed, a Websocket (WS) can be used
 * or an acknowledgment endpoint from REST can be used.
 * <br>
 */
@Service
public class EmployeeNotificationService {
    private final PostgresNotificationListener pgListener;

    public EmployeeNotificationService() {
        this.pgListener = new PostgresNotificationListener();
    }
}
