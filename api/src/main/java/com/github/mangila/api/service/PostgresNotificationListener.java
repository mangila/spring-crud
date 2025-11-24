package com.github.mangila.api.service;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

/**
 * Postgres LISTEN/NOTIFY listener.
 */
@Service
public class PostgresNotificationListener {

    SingleConnectionDataSource dataSource;

}
