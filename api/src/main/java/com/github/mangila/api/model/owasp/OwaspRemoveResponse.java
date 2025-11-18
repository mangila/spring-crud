package com.github.mangila.api.model.owasp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mangila.api.config.WebConfig;
import org.springframework.http.HttpHeaders;

import java.util.List;

public record OwaspRemoveResponse(
        @JsonProperty("last_update_utc")
        String timestamp,
        @JsonProperty("headers")
        List<String> headers
) {

    public HttpHeaders extractHeaders() {
        var httpHeaders = new HttpHeaders();
        headers().forEach(header -> {
            // Add a header key to be removed, with an arbitrary value
            httpHeaders.add(header, "");
        });
        // add the last update timestamp
        httpHeaders.add(WebConfig.OWASP_LAST_UPDATE_UTC_HTTP_HEADER, timestamp());
        return httpHeaders;
    }

    public boolean isUpdated(String oldTimestamp) {
        return !timestamp.equals(oldTimestamp);
    }
}
