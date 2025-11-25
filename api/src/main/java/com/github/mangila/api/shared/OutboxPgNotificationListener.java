package com.github.mangila.api.shared;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.postgresql.PGNotification;
import org.postgresql.jdbc.PgConnection;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.time.Duration;

/**
 * Postgres LISTEN/NOTIFY listener.
 * <br>
 * We need to create a long-lived single connection that does not take a connection from the Hikari pool.
 */
@Slf4j
public class OutboxPgNotificationListener extends PgNotificationListener {

    @Getter
    @Setter
    private volatile boolean running = false;

    public OutboxPgNotificationListener(SingleConnectionDataSource dataSource,
                                        SpringEventPublisher publisher) {
        super(dataSource, publisher);
        setUpTrigger();
    }

    @Override
    public String channel() {
        return "outbox_event_channel";
    }

    @Override
    public void run() {
        int timeout = (int) Duration.ofSeconds(1).toMillis();
        getJdbc().execute((Connection c) -> {
            PgConnection pgConnection = c.unwrap(PgConnection.class);
            while (running) {
                PGNotification[] pgNotifications = pgConnection.getNotifications(timeout);
                if (pgNotifications == null || ArrayUtils.isEmpty(pgNotifications)) {
                    log.debug("No notifications received");
                    continue;
                }
                log.debug("Received {} notifications", pgNotifications.length);
                getPublisher().publish(pgNotifications);
            }
            return 0;
        });
    }

    /**
     * This is the database server internal stuffs that need to be set up for the LISTEN/NOTIFY
     * <br>
     * 1. Create a Function that uses pg_notify(channel, payload)
     * 2. Create a Trigger for the Function to be called after each INSERT on the table
     */
    private void setUpTrigger() {
        createFunction();
        createTrigger();
        listen();
    }

    private void listen() {
        getJdbc().execute((Connection c) -> {
            c.setAutoCommit(true);
            //language=PostgreSQL
            final String sql = "LISTEN %s".formatted(channel());
            c.createStatement().execute(sql);
            return 0;
        });
    }

    private void createTrigger() {
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
