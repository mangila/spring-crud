package com.github.mangila.api;

import org.springframework.boot.SpringApplication;

public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(PostgresTestContainerConfiguration.class).run(args);
    }

}
