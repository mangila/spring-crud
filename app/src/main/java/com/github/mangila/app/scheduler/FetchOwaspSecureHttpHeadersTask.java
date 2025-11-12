package com.github.mangila.app.scheduler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.config.WebConfig;
import com.github.mangila.app.model.owasp.OwaspResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
 * <p>
 * Fetch it via a Cron job to stay up to date.
 * </p>
 * <p>
 * In the resource folder we have a static JSON with a timestamp for the last update.
 * Here we run a Cron job to fetch the latest headers and update the HTTP headers bean.
 * So it won't be super important to re-deploy the whole app if the static file is not updated regularly.
 * The static JSON file is NOT updated! So during a deployment if there is a new version of the headers,
 * the app will not pick up the new headers. So we need to update the static JSON file manually.
 * keep it simple; the programmer is actually forced to read the OWASP documentation. :O
 * </p>
 * <p>
 * We are using the standard lib Java HTTP Client API for this.
 * In Spring, we have {@link RestClient} in favor for {@link org.springframework.web.client.RestTemplate}
 * Also Webclient from Webflux to name a few.
 * </p>
 * <p>
 * Java standard lib HTTP Client is the default underlying client in Spring RestClient.
 * Depends on the Java version (Java 11)
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
public class FetchOwaspSecureHttpHeadersTask implements Task {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final HttpHeaders oWaspSecureHeaders;

    public FetchOwaspSecureHttpHeadersTask(ObjectMapper objectMapper,
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
            log.info("OWASP secure headers fetched successfully");
            String lastUpdate = oWaspSecureHeaders.getFirst(WebConfig.OWASP_LAST_UPDATE_UTC_HTTP_HEADER);
            // last update timestamp header was null, update headers
            if (lastUpdate == null) {
                updateHeaders(response);
            }
            // there is no new version of the headers, nothing to update
            else if (lastUpdate.equals(response.timestamp())) {
                String msg = "Last update timestamp matches, wont update";
                log.info(msg);
                node.put("error", msg);
            }
            // there is a new version of the headers, time for an update!
            else {
                updateHeaders(response);
            }
            return node;
        } catch (Exception e) {
            log.error("Failed to fetch OWASP secure headers", e);
            node.put("error", e.getMessage());
            return node;
        }
    }

    private void updateHeaders(OwaspResponse response) {
        log.info("Updating OWASP secure headers");
        MultiValueMap<String, String> owasp = new LinkedMultiValueMap<>();
        response.headers().forEach(header -> {
            // Add all header key value pairs to the MultiValueMap
            owasp.add(header.name(), header.value());
        });
        // add the last update timestamp
        owasp.add(WebConfig.OWASP_LAST_UPDATE_UTC_HTTP_HEADER, response.timestamp());
        // Clear the existing headers
        oWaspSecureHeaders.clear();
        // Add the new security headers to the HttpHeaders
        oWaspSecureHeaders.putAll(owasp);
        log.info("OWASP secure headers updated successfully - {}", oWaspSecureHeaders.toSingleValueMap());
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
