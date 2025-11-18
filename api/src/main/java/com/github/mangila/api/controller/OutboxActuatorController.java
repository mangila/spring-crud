package com.github.mangila.api.controller;


import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @WriteOperation
    public WebEndpointResponse<Map<String, Object>> execute(@Selector String outboxId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
