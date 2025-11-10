package com.github.mangila.app.controller;


import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Find outbox status and Publish outbox messages from the Actuator endpoint.
 */
@WebEndpoint(id = "outbox")
@Component
public class OutboxActuatorController {

    @ReadOperation
    public WebEndpointResponse<Map<String, String>> findOutboxStatus(@Selector String outboxId) {
        return new WebEndpointResponse<>(
                Map.of("outbox", outboxId),
                HttpStatus.OK.value()
        );
    }

    @WriteOperation
    public WebEndpointResponse<Map<String, Object>> execute(@Selector String outboxId) {
        return new WebEndpointResponse<>(
                Map.of("outbox", outboxId),
                HttpStatus.ACCEPTED.value()
        );
    }

}
