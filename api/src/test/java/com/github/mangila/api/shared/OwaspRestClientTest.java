package com.github.mangila.api.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;

/**
 * Test the rest client,
 * mock can be used if the RestClient is not configured with a new request factory
 */
@RestClientTest(OwaspRestClient.class)
class OwaspRestClientTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OwaspRestClient owaspRestClient;

    @Test
    @Disabled
    void fetchOwaspSecureHeadersToAdd() {
        var l = owaspRestClient.fetchOwaspSecureHeadersToAdd();
        System.out.println(l);
    }

    @Test
    @Disabled
    void fetchOwaspSecureHeadersToRemove() {
    }
}