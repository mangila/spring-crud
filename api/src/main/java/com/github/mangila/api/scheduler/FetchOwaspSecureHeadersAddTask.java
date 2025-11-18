package com.github.mangila.api.scheduler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.config.OwaspHeaders;
import com.github.mangila.api.config.WebConfig;
import com.github.mangila.api.model.owasp.OwaspAddResponse;
import com.github.mangila.api.shared.OwaspRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

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
 * Keep it simple; the programmer is actually forced to read the OWASP documentation. :O
 * </p>
 * <p>
 * We are using the Spring Boot RestClient to fetch the JSON.
 * One of many HTTP clients out there. To name a few
 *      <ul>
 *          <li>{@link org.springframework.web.client.RestClient}</li>
 *          <li>{@link org.springframework.web.client.RestTemplate}</li>
 *          <li>{@link java.net.http.HttpClient}
 *          RestClient use this one under the hood by default
 *          </li>
 *          <li>WebFlux - The reactive one</li>
 *      </ul>
 * </p>
 */
@Component
@Slf4j
public class FetchOwaspSecureHeadersAddTask implements Task {

    private final ObjectMapper objectMapper;
    private final OwaspRestClient owaspRestClient;
    private final OwaspHeaders headers;

    public FetchOwaspSecureHeadersAddTask(ObjectMapper objectMapper,
                                          OwaspRestClient owaspRestClient,
                                          @Qualifier("headersToAdd") OwaspHeaders headers) {
        this.objectMapper = objectMapper;
        this.owaspRestClient = owaspRestClient;
        this.headers = headers;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ObjectNode call() {
        log.info("Fetching OWASP secure headers to add");
        var node = objectMapper.createObjectNode();
        try {
            OwaspAddResponse owaspAddResponse = owaspRestClient.fetchOwaspSecureHeadersToAdd();
            node.set("response", objectMapper.valueToTree(owaspAddResponse));
            log.info("OWASP secure headers fetched successfully");
            String lastUpdate = headers.getHeader(WebConfig.OWASP_LAST_UPDATE_UTC_HTTP_HEADER);
            if (shouldUpdate(lastUpdate, owaspAddResponse)) {
                HttpHeaders httpHeaders = owaspAddResponse.extractHeaders();
                headers.clear();
                headers.putAll(httpHeaders);
                log.info("OWASP secure headers to add applied successfully - {}", headers.getHeaders());
            } else {
                String msg = "Last update timestamp matches, wont update";
                log.info(msg);
                node.put("error", msg);
            }
            return node;
        } catch (Exception e) {
            log.error("Failed to fetch OWASP secure headers", e);
            node.put("error", e.getMessage());
            return node;
        }
    }

    /**
     * <p>
     * No value found in the header or timestamp mismatch, time for an update!
     * </p>
     */
    private static boolean shouldUpdate(String lastUpdate, OwaspAddResponse response) {
        if (lastUpdate == null) {
            return true;
        }
        return response.isUpdated(lastUpdate);
    }
}
