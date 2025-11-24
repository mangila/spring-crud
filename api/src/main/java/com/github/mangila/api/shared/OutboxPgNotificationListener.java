package com.github.mangila.api.shared;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.postgresql.PGNotification;
import org.postgresql.jdbc.PgConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.time.Duration;

/**
 * Postgres LISTEN/NOTIFY listener.
 * <br>
 * We need to create a long-lived single connection that does not take a connection from the Hikari pool.
 */
@Slf4j
public class OutboxPgNotificationListener implements PgNotificationListener {

    private final SingleConnectionDataSource dataSource;
    private final JdbcTemplate jdbc;
    private final SpringEventPublisher publisher;

    public OutboxPgNotificationListener(SingleConnectionDataSource dataSource,
                                        SpringEventPublisher publisher) {
        this.dataSource = dataSource;
        this.jdbc = new JdbcTemplate(dataSource, true);
        this.publisher = publisher;
    }

    @Override
    public String channel() {
        return "outbox_event_channel";
    }

    /**
     * Consumes pg notifications while blocking for 5 seconds and then publish them via Spring Event Bus.
     * No exception handling here, just to rely on the executors CompletableFuture.whenComplete() mechanism.
     */
    @Override
    public void run() {
        int timeoutMillis = (int) Duration.ofSeconds(5).toMillis();
        //language=PostgreSQL
        final String sql = """
                LISTEN '%s'
                """.formatted(channel());
        // jdbc.execute(sql);
        jdbc.execute((Connection c) -> {
            PgConnection pgConnection = c.unwrap(PgConnection.class);
            while (true) {
                PGNotification[] pgNotifications = pgConnection.getNotifications(timeoutMillis);
                if (pgNotifications == null || ArrayUtils.isEmpty(pgNotifications)) {
                    continue;
                }
                publisher.publish(pgNotifications);
            }
        });
    }
}
