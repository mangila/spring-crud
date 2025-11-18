package com.github.mangila.api.model.owasp;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mangila.api.config.WebConfig;
import org.springframework.http.HttpHeaders;

import java.util.List;

public record OwaspAddResponse(
        @JsonProperty("last_update_utc")
        String timestamp,
        @JsonProperty("headers")
        List<Header> headers
) {
    public record Header(String name, String value) {
    }

    public HttpHeaders extractHeaders() {
        var httpHeaders = new HttpHeaders();
        headers().forEach(header -> {
            httpHeaders.add(header.name(), header.value());
        });
        // add the last update timestamp
        httpHeaders.add(WebConfig.OWASP_LAST_UPDATE_UTC_HTTP_HEADER, timestamp());
        return httpHeaders;
    }

    public boolean isUpdated(String oldTimestamp) {
        return !timestamp.equals(oldTimestamp);
    }
}
