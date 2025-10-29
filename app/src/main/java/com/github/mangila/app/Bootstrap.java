package com.github.mangila.app;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Simple bootstrap class to load some fake data on start-up.
 * <br>
 * This is how to load some fake data, since our application is responsible for the business logic.
 */
@Component
@Profile("dev")
public class Bootstrap {
}
