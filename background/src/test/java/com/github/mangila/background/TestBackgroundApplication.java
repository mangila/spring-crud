package com.github.mangila.background;

import org.springframework.boot.SpringApplication;

public class TestBackgroundApplication {

    public static void main(String[] args) {
        SpringApplication.from(BackgroundApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
