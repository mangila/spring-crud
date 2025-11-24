package com.github.mangila.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.api.config.OwaspHeaders;
import com.github.mangila.api.model.owasp.OwaspAddResponse;
import com.github.mangila.api.model.owasp.OwaspRemoveResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.*;

/**
 * <p>
 * Bootstrap class to run some tasks on application startup.
 * </p>
 * <p>
 * Load the static files that contain the Owasp secure headers from the OWASP HTTP secure headers project.
 * So the Controller can return the latest secure headers.
 * </p>
 */
@Component
@Slf4j
public class Bootstrap {

    private final ObjectMapper objectMapper;
    private final OwaspHeaders headersToAdd;
    private final OwaspHeaders headersToRemove;

    public Bootstrap(ObjectMapper objectMapper,
                     @Qualifier("headersToAdd") OwaspHeaders headersToAdd,
                     @Qualifier("headersToRemove") OwaspHeaders headersToRemove) {
        this.objectMapper = objectMapper;
        this.headersToAdd = headersToAdd;
        this.headersToRemove = headersToRemove;
    }

    /**
     * <p>
     * Load the OWASP secure headers from a static resource
     * </p>
     */
    @PostConstruct
    public void loadStaticResources() throws IOException {
        loadStaticOwaspSecureHeadersToAdd();
        loadStaticOwaspSecureHeadersToRemove();
    }

    private void loadStaticOwaspSecureHeadersToAdd() throws IOException {
        String fileName = "owasp-secure-headers-add.json";
        log.info("Loading static OWASP secure headers to add - {}", fileName);
        ClassPathResource resource = new ClassPathResource(fileName);
        String json = resource.getContentAsString(UTF_8);
        OwaspAddResponse owaspAddResponse = objectMapper.readValue(json, OwaspAddResponse.class);
        HttpHeaders httpHeaders = owaspAddResponse.extractHeaders();
        headersToAdd.putAll(httpHeaders);
    }

    private void loadStaticOwaspSecureHeadersToRemove() throws IOException {
        String fileName = "owasp-secure-headers-remove.json";
        log.info("Loading static OWASP secure headers to remove - {}", fileName);
        ClassPathResource resource = new ClassPathResource(fileName);
        String json = resource.getContentAsString(UTF_8);
        OwaspRemoveResponse owaspRemoveResponse = objectMapper.readValue(json, OwaspRemoveResponse.class);
        HttpHeaders httpHeaders = owaspRemoveResponse.extractHeaders();
        headersToRemove.putAll(httpHeaders);
    }
}
