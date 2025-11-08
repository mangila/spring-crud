package com.github.mangila.app.shared;

import com.github.mangila.app.shared.event.CreateNewEmployeeEvent;
import com.github.mangila.app.shared.event.UpdateEmployeeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wrapper for Spring's ApplicationEventPublisher.
 * <br>
 * Spring's ApplicationEventPublisher is used to publish events, very convenient when want to run a side effect.
 * This is just an example of how to use Spring's ApplicationEventPublisher.
 * <br>
 * Simple use case when using event stuffs in the same Spring Boot application.
 * <br>
 * Since we use Postgres as our database, we can also use LISTEN/NOTIFY from Postgres.
 * But this is not the focus of this example.
 */
@Service
public class SpringEventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Force the publisher to be inside a tx since our listener is a @TransactionalEventListener.
     * A fail-fast version of this, since if you forget to wrap inside a transaction, the event will not be published.
     * So loudly remind the programmer to wrap inside a transaction.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(CreateNewEmployeeEvent event) {
        publisher.publishEvent(event);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(UpdateEmployeeEvent event) {
        publisher.publishEvent(event);
    }
}
