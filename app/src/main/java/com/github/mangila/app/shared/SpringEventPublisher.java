package com.github.mangila.app.shared;

import com.github.mangila.app.shared.event.NewEmployeeCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Wrapper for Spring's ApplicationEventPublisher.
 * <br>
 * Spring's ApplicationEventPublisher is used to publish events, very convenient when want to run a side effect.
 * This is just an example of how to use Spring's ApplicationEventPublisher.
 * <br>
 * Simple use case when using event stuffs in the same JVM
 */
@Service
public class SpringEventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(NewEmployeeCreatedEvent event) {
        publisher.publishEvent(event);
    }
}
