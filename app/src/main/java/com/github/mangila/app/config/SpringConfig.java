package com.github.mangila.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class SpringConfig {

    /**
     * If playing around with Time in Spring Boot, you can have a Clock bean to manipulate time with.
     * Sometimes deployment servers can be misconfigured (when relying on System Zone) and create the wrong time timezone and such.
     * Here it can be manged through application code.
     * <br>
     * Nice with Testing to be able to manipulate time instead of do Thread.sleep and other creative stuffs.
     * <br>
     * All-time creation and manipulation should use this bean.
     */
    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of("Europe/Stockholm"));
    }
}
