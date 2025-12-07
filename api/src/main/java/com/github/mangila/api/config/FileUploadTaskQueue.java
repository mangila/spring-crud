package com.github.mangila.api.config;

import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public record FileUploadTaskQueue(ArrayBlockingQueue<MultipartFile> queue) {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    public void put(MultipartFile file) {
        try {
            boolean ok = queue.offer(file, TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (!ok) {
                throw new RuntimeException("queue is full");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
