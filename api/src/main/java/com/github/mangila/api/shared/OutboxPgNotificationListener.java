package com.github.mangila.api.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.postgresql.PGNotification;
import org.postgresql.jdbc.PgConnection;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;

/**
 * Postgres LISTEN/NOTIFY listener.
 * <br>
 * We need to create a long-lived single connection that does not take a connection from the Hikari pool.
 */
@Slf4j
public class OutboxPgNotificationListener extends PgNotificationListener {

    private final ObjectMapper objectMapper;

    public OutboxPgNotificationListener(SingleConnectionDataSource dataSource,
                                        SpringEventPublisher publisher,
                                        ObjectMapper objectMapper) {
        super(dataSource, publisher);
        this.objectMapper = objectMapper;
    }

    @Override
    public String channel() {
        return "outbox_event_channel";
    }

    @Override
    public ObjectNode call() throws Exception {
        log.info("Starting notification listener event loop on channel {}", channel());
        Instant startTime = Instant.now();
        getJdbc().execute((Connection c) -> {
            PgConnection pgConnection = c.unwrap(PgConnection.class);
            int timeout = (int) Duration.ofSeconds(1).toMillis();
            while (isRunning()) {
                PGNotification[] pgNotifications = pgConnection.getNotifications(timeout);
                if (pgNotifications == null || ArrayUtils.isEmpty(pgNotifications)) {
                    log.debug("No notifications received event loop on channel {}", channel());
                    continue;
                }
                log.debug("Received {} notifications event loop on channel {}", pgNotifications.length, channel());
                getPublisher().publish(pgNotifications);
            }
            log.info("Stopping notification listener event loop on channel {}", channel());
            return 0;
        });
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        return objectMapper.createObjectNode().put("duration-ms", duration.toMillis());
    }

    /**
     * This is the database server internal stuffs that need to be set up for the LISTEN/NOTIFY
     * <br>
     * 1. Create a Function that uses pg_notify(channel, payload)
     * 2. Create a Trigger for the Function to be called after each INSERT on the table
     * 3. Listen to the channel
     */
    @Override
    public void setUpPgNotify() {
        createFunction();
        createTrigger();
    }

    private void createTrigger() {
        log.info("Creating trigger for channel {}", channel());
        //language=PostgreSQL
        String sql = """
                DROP TRIGGER IF EXISTS notify_new_outbox_event_trigger ON outbox_event;
                """;
        getJdbc().execute(sql);
        //language=PostgreSQL
        sql = """
                CREATE TRIGGER notify_new_outbox_event_trigger
                AFTER INSERT ON outbox_event
                FOR EACH ROW
                EXECUTE FUNCTION notify_new_outbox_event_fn();
                """;
        getJdbc().execute(sql);
    }

    private void createFunction() {
        log.info("Creating function for channel {}", channel());
        //language=PostgreSQL
        String sql = """
                DROP FUNCTION IF EXISTS notify_new_outbox_event_fn();
                """;
        getJdbc().execute(sql);
        //language=PostgreSQL
        sql = """
                CREATE OR REPLACE FUNCTION notify_new_outbox_event_fn() RETURNS trigger AS $$
                DECLARE
                    channel TEXT := '%s';
                    notify_payload TEXT;
                BEGIN
                    notify_payload := json_build_object(
                            'id', NEW.id,
                            'aggregate_id', NEW.aggregate_id,
                            'event_name', NEW.event_name,
                            'payload', NEW.payload
                    )::text;
                    PERFORM pg_notify(channel, notify_payload);
                    RETURN NEW;
                END;
                $$ LANGUAGE plpgsql;
                """.formatted(channel());
        getJdbc().execute(sql);
    }
}
