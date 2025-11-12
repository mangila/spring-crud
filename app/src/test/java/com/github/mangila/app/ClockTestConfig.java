package com.github.mangila.app;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Test Configuration for setting a spring bean clock for testing time operations.
 */
@TestConfiguration
public class ClockTestConfig {

    @Primary
    @Bean("clockForTesting")
    public Clock clock(TestClock testClock) {
        return testClock;
    }

    @Bean
    public TestClock testClock() {
        // just set the clock to the current time, we can use fixed here.
        return new TestClock(Clock.systemDefaultZone());
    }

    public static class TestClock extends Clock {
        private Clock clock;

        public TestClock(Clock clock) {
            this.clock = clock;
        }

        /**
         * Timemachine YEEEY!
         */
        public void advanceTime(Duration duration) {
            long millis = duration.toMillis();
            Instant newTime = this.clock.instant().plusMillis(millis);
            this.clock = Clock.fixed(newTime, ZoneId.systemDefault());
        }

        /**
         * I'm from the Future...
         */
        public void goBackTime(Duration duration) {
            long millis = duration.toMillis();
            Instant newTime = this.clock.instant().minusMillis(millis);
            this.clock = Clock.fixed(newTime, ZoneId.systemDefault());
        }

        @Override
        public ZoneId getZone() {
            return clock.getZone();
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return clock.withZone(zone);
        }

        @Override
        public Instant instant() {
            return clock.instant();
        }
    }
}
