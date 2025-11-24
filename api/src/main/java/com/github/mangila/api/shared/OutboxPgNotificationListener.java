package com.github.mangila.api.shared;

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

    public OutboxPgNotificationListener(SingleConnectionDataSource dataSource,
                                        SpringEventPublisher publisher) {
        super(dataSource, publisher);
    }

    @Override
    public String channel() {
        return "outbox_event_channel";
    }

    /**
     * Consumes pg notifications while blocking for 5 seconds and then publish them via Spring Event Bus.
     */
    @Override
    public void run() {
        int timeoutMillis = (int) Duration.ofSeconds(5).toMillis();
        //language=PostgreSQL
        final String sql = """
                LISTEN '%s'
                """.formatted(channel());
        //getJdbc().execute(sql);
        getJdbc().execute((Connection c) -> {
            PgConnection pgConnection = c.unwrap(PgConnection.class);
            while (true) {
                PGNotification[] pgNotifications = pgConnection.getNotifications(timeoutMillis);
                if (pgNotifications == null || ArrayUtils.isEmpty(pgNotifications)) {
                    continue;
                }
                getPublisher().publish(pgNotifications);
            }
        });
    }

    /**
     * This is the database server internal stuffs that need to be set up for the LISTEN/NOTIFY
     * <br>
     * 1. Create a Function that uses pg_notify(channel, payload)
     * 2. Create a Trigger when the Function is called
     */
    private void setUpTrigger() {

    }
}
