package com.github.mangila.api;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Bootstrap class for development profile.
 * </p>
 * <p>
 * Adds some data to the database.
 * </p>
 */
@Component
@Profile("dev")
public class DevelopmentBootstrap {
}
