package com.github.mangila.app.scheduler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.owasp.OwaspResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * <a href="https://owasp.org/www-project-secure-headers/">OWASP Secure Headers Project</a>
 * <p>
 * Fetch OWASP secure headers from the OWASP HTTP secure headers project.
 * <a href="https://owasp.org/www-project-secure-headers/ci/headers_add.json">OWASP Secure HTTP Headers</a>
 * The currently recommended http security headers are fetched from this endpoint.
 * </p>
 * <p>
 * Fetch it via a Cron job to stay up to date.
 * A simpler approach would be to use a static JSON file.
 * Buuuut I wanted to demonstrate how to fetch data from an external source and have a cron job in this application.
 * Plus, we can use the latest data. Not like the static JSON file. But yeah...
 * We are using the standard lib Java HTTP Client API for this.
 * In Spring, we have {@link RestClient} in favor for {@link org.springframework.web.client.RestTemplate}
 * Also Webclient from Webflux to name a few.
 * </p>
 * <p>
 * Java standard lib HTTP Client is the default underlying client in Spring RestClient.
 * Depends on the Java version, I guess...
 * Set it programmatically if you want to use a different client.
 * </p>
 * <pre>
 *     {@code
 *      var http = HttpClient.newHttpClient();
 *      var factory = new JdkClientHttpRequestFactory();
 *      RestClient.builder()
 *                  .requestFactory(factory)
 *                  .baseUrl("https://api.example.com")
 *                  .build();
 *
 *     }
 * </pre>
 */
@Component
@Slf4j
public class OwaspFetchSecureHeadersTask implements Task {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final HttpHeaders oWaspSecureHeaders;

    public OwaspFetchSecureHeadersTask(ObjectMapper objectMapper,
                                       HttpHeaders oWaspSecureHeaders) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
        this.oWaspSecureHeaders = oWaspSecureHeaders;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ObjectNode call() {
        log.info("Fetching OWASP secure headers");
        var node = objectMapper.createObjectNode();
        try {
            OwaspResponse response = getOwaspSecureHeaders();
            node.set("response", objectMapper.valueToTree(response));
            response.headers().forEach(header -> {
                oWaspSecureHeaders.add(header.name(), header.value());
            });
            log.info("OWASP secure headers fetched successfully");
            return node;
        } catch (Exception e) {
            log.error("Failed to fetch OWASP secure headers", e);
            node.put("error", e.getMessage());
            return node;
        }
    }

    /**
     * Get Latest OWASP secure headers, from the OWASP HTTP secure headers project.
     */
    public OwaspResponse getOwaspSecureHeaders() {
        try {
            var response = httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://owasp.org/www-project-secure-headers/ci/headers_add.json"))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), OwaspResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
