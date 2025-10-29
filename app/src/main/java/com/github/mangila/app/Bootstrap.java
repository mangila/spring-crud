package com.github.mangila.app;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Simple bootstrap class to load some fake data on start-up.
 */
@Component
@Profile("dev")
public class Bootstrap {
}
