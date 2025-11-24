package com.github.mangila.api.shared;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * Prefer Interfaces, but when state is involved like field members,
 * an abstract class is needed
 */
@lombok.Data
public abstract class PgNotificationListener implements Runnable {

    private final SingleConnectionDataSource dataSource;
    private final JdbcTemplate jdbc;
    private final SpringEventPublisher publisher;

    public PgNotificationListener(SingleConnectionDataSource dataSource,
                                  SpringEventPublisher publisher) {
        this.dataSource = dataSource;
        this.jdbc = new JdbcTemplate(dataSource, true);
        this.publisher = publisher;
    }

    public abstract String channel();
}
