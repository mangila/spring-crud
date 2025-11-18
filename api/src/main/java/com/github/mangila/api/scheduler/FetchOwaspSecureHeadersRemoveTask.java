package com.github.mangila.api.scheduler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.api.config.OwaspHeaders;
import com.github.mangila.api.config.WebConfig;
import com.github.mangila.api.model.owasp.OwaspRemoveResponse;
import com.github.mangila.api.shared.OwaspRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FetchOwaspSecureHeadersRemoveTask implements Task {

    private final ObjectMapper objectMapper;
    private final OwaspRestClient owaspRestClient;
    private final OwaspHeaders headers;

    public FetchOwaspSecureHeadersRemoveTask(ObjectMapper objectMapper,
                                             OwaspRestClient owaspRestClient,
                                             @Qualifier("headersToRemove") OwaspHeaders headers) {
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
        log.info("Fetching OWASP secure headers to remove");
        var node = objectMapper.createObjectNode();
        try {
            OwaspRemoveResponse owaspRemoveResponse = owaspRestClient.fetchOwaspSecureHeadersToRemove();
            node.set("response", objectMapper.valueToTree(owaspRemoveResponse));
            log.info("OWASP secure headers fetched successfully");
            String lastUpdate = headers.getHeader(WebConfig.OWASP_LAST_UPDATE_UTC_HTTP_HEADER);
            if (shouldUpdate(lastUpdate, owaspRemoveResponse)) {
                HttpHeaders httpHeaders = owaspRemoveResponse.extractHeaders();
                headers.clear();
                headers.putAll(httpHeaders);
                log.info("OWASP secure headers to remove applied successfully - {}", headers.getHeaders());
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
    private static boolean shouldUpdate(String lastUpdate, OwaspRemoveResponse response) {
        if (lastUpdate == null) {
            return true;
        }
        return response.isUpdated(lastUpdate);
    }
}
