package com.github.mangila.api.config;

import com.github.mangila.api.model.employee.domain.EmployeeId;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Employee SSE emitters. Manual connection handling is required.
 * <br>
 * With a Webflux SSE emitter, the emitters are handled automatically.
 * But to stay in the MVC Servlet Environment, we need to use SseEmitter.
 */
public record EmployeeSseEmitters(ConcurrentHashMap<EmployeeId, CopyOnWriteArrayList<SseEmitter>> map) {

    public void put(EmployeeId id, SseEmitter emitter) {
        map.compute(id, (key, value) -> {
            if (value == null) {
                var l = new CopyOnWriteArrayList<SseEmitter>();
                l.add(emitter);
                return l;
            }
            value.add(emitter);
            return value;
        });
    }

    public void remove(EmployeeId id, SseEmitter emitter) {
        map.get(id).remove(emitter);
    }

    public CopyOnWriteArrayList<SseEmitter> get(EmployeeId id) {
        return map.get(id);
    }
}
