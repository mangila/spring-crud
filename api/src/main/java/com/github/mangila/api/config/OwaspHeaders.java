package com.github.mangila.api.config;

import org.springframework.http.HttpHeaders;

import java.util.Map;

public record OwaspHeaders(HttpHeaders httpHeaders) {

    public Map<String, String> getHeaders() {
        return httpHeaders.toSingleValueMap();
    }

    public String getHeader(String key) {
        return httpHeaders.getFirst(key);
    }

    public void putAll(HttpHeaders httpHeaders) {
        this.httpHeaders.putAll(httpHeaders);
    }

    public void clear() {
        httpHeaders.clear();
    }
}
