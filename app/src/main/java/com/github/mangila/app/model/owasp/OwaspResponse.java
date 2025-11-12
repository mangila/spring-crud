package com.github.mangila.app.model.owasp;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OwaspResponse(
        @JsonProperty("last_update_utc")
        String timestamp,
        @JsonProperty("headers")
        List<Header> headers
) {
    public record Header(String name, String value) {
    }
}
