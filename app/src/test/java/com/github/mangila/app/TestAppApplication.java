package com.github.mangila.app;

import org.springframework.boot.SpringApplication;

public class TestAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
