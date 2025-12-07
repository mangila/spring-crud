package com.github.mangila.api.model;

import com.github.mangila.api.shared.exception.FileUploadQueueFullException;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public record FileUploadTaskQueue(ArrayBlockingQueue<FileUploadRequest> queue) {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    public void put(FileUploadRequest request) throws InterruptedException {
        boolean ok = queue.offer(request, TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        if (!ok) {
            throw new FileUploadQueueFullException();
        }
    }

    public FileUploadRequest take() throws InterruptedException {
        return queue.take();
    }
}
