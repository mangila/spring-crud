package com.github.mangila.api.shared;

import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;
import java.time.Duration;

/**
 * Prefer Interfaces, but when state is involved like field members,
 * an abstract class is needed
 */
public abstract class PgNotificationListener implements Runnable {

    private final SingleConnectionDataSource dataSource;
    @Getter
    private final JdbcTemplate jdbc;
    @Getter
    private final SpringEventPublisher publisher;

    public PgNotificationListener(SingleConnectionDataSource dataSource,
                                  SpringEventPublisher publisher) {
        this.dataSource = dataSource;
        this.jdbc = new JdbcTemplate(dataSource, true);
        this.publisher = publisher;
    }

    public abstract String channel();

    public boolean isValid() throws SQLException {
        return dataSource.getConnection()
                .isValid((int) Duration.ofSeconds(5).toMillis());
    }

    public void resetConnection() {
        dataSource.resetConnection();
    }

    public void destroy() {
        dataSource.destroy();
    }
}
