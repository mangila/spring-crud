package com.github.mangila.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Map;

@Configuration
public class SpringConfig {

    /**
     * The Clock bean tells Spring Boot what time zone to use.
     * <br>
     * If playing around with Time in Spring Boot, you can have a Clock bean to manipulate time with.
     * Deployment server could have different time zones, and also the Database could have a different time zone.
     * Can cause issues and confusion. Use UTC everywhere.
     * Server = UTC
     * Database = UTC
     * Logs/Audit = UTC
     * Presentation = Local date time OR send UTC and let the client work their own logic
     * <br>
     * Nice with Testing to be able to manipulate time instead of do Thread.sleep and other creative stuffs.
     * <br>
     * All-time creation and manipulation should use this bean.
     */
    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of("Europe/Stockholm"));
    }

    @Bean
    HttpHeaders oWaspSecureHeaders() {
        return new HttpHeaders();
    }

}
