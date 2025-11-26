package com.github.mangila.api.shared;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.concurrent.Callable;

/**
 * Prefer Interfaces, but when state is involved like field members,
 * an abstract class is needed
 */
@Slf4j
public abstract class PgNotificationListener implements Callable<ObjectNode> {

    private final SingleConnectionDataSource dataSource;
    @Getter
    private final JdbcTemplate jdbc;
    @Getter
    private final SpringEventPublisher publisher;
    @Getter
    private volatile boolean running = false;
    @Getter
    private volatile boolean shutdown = false;

    public PgNotificationListener(SingleConnectionDataSource dataSource,
                                  SpringEventPublisher publisher) {
        this.dataSource = dataSource;
        this.jdbc = new JdbcTemplate(dataSource, true);
        this.publisher = publisher;
    }

    public abstract String channel();

    public abstract void setUpPgNotify();

    public void resetConnection() {
        dataSource.resetConnection();
    }

    public void listen() {
        final String sql = "LISTEN %s".formatted(channel());
        jdbc.execute(sql);
        this.running = true;
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public void destroy() {
        dataSource.destroy();
    }

    public void unlisten() {
        final String sql = "UNLISTEN %s".formatted(channel());
        jdbc.execute(sql);
        this.running = false;
    }
}
