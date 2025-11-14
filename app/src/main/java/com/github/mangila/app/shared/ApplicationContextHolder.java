package com.github.mangila.app.shared;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * When playing around with concurrency and the thread need some context, this is a good place to put your stuff.
 * </p>
 */
public class ApplicationContextHolder {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    private ApplicationContextHolder() {
        throw new IllegalStateException("Utility class");
    }

    public static void setEntry(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public static @Nullable Object getEntry(String key) {
        return CONTEXT.get().get(key);
    }

    public static void clear() {
        CONTEXT.get().clear();
        CONTEXT.remove();
    }

}
