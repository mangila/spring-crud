package com.github.mangila.api.scheduler;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class FileUploadWatcher implements SmartLifecycle {

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
