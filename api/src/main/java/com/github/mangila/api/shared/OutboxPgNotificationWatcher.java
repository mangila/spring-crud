package com.github.mangila.api.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Take cares of PgNotificationListener lifecycles methods.
 * <br>
 * This is to manage the long-running tasks
 */
@Slf4j
public class OutboxPgNotificationWatcher implements SmartLifecycle {
    private final OutboxPgNotificationListener listener;
    private final ApplicationTaskExecutor applicationTaskExecutor;
    private final ObjectMapper objectMapper;
    private CompletableFuture<ObjectNode> listenEventLoop;

    public OutboxPgNotificationWatcher(OutboxPgNotificationListener listener,
                                       ApplicationTaskExecutor applicationTaskExecutor,
                                       ObjectMapper objectMapper) {
        this.listener = listener;
        this.applicationTaskExecutor = applicationTaskExecutor;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        listener.setUpPgNotify();
    }

    @Override
    public void start() {
        listener.listen();
        ObjectNode attributes = objectMapper.createObjectNode();
        attributes.put("channel", listener.channel());
        attributes.put("executedBy", OutboxPgNotificationWatcher.class.getSimpleName());
        this.listenEventLoop = applicationTaskExecutor.submitCompletable(listener, listener.channel(), attributes);
    }

    @Override
    public void stop() {
        log.info("Shutdown OutboxPgNotificationWatcher");
        try {
            listener.shutdown();
            listenEventLoop.cancel(true);
            // Wait here for a bit to let the side effect run and insert to db
            // TODO: verify in db instead of sleep
            TimeUnit.SECONDS.sleep(5);
            listener.unlisten();
            listener.destroy();
        } catch (Exception e) {
            log.error("Failed to shutdown OutboxPgNotificationListener gracefully", e);
        }
    }

    @Override
    public boolean isRunning() {
        return listener.isRunning();
    }

    public boolean isShutdown() {
        return listener.isShutdown();
    }

    public void reset() {
        listener.resetConnection();
    }
}
