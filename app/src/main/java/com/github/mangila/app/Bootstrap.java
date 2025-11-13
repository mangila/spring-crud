package com.github.mangila.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.config.OwaspHeaders;
import com.github.mangila.app.model.owasp.OwaspAddResponse;
import com.github.mangila.app.model.owasp.OwaspRemoveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * Bootstrap class to run some tasks on application startup.
 * </p>
 * <p>
 * Fetch the Owasp secure headers from the OWASP HTTP secure headers project.
 * So the Controller can return the latest secure headers.
 * </p>
 *
 */
@Component
@Slf4j
public class Bootstrap implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final OwaspHeaders headersToAdd;
    private final OwaspHeaders headersToRemove;

    public Bootstrap(ObjectMapper objectMapper,
                     OwaspHeaders headersToAdd,
                     OwaspHeaders headersToRemove) {
        this.objectMapper = objectMapper;
        this.headersToAdd = headersToAdd;
        this.headersToRemove = headersToRemove;
    }


    @Override
    public void run(String... args) throws Exception {
        loadStaticOwaspSecureHeadersToAdd();
        loadStaticOwaspSecureHeadersToRemove();
    }

    /**
     * <p>
     * Load the OWASP secure headers from a static resource
     * </p>
     */
    private void loadStaticOwaspSecureHeadersToAdd() throws IOException {
        ClassPathResource resource = new ClassPathResource("owasp-secure-headers-add.json");
        String json = resource.getContentAsString(StandardCharsets.UTF_8);
        OwaspAddResponse owaspAddResponse = objectMapper.readValue(json, OwaspAddResponse.class);
        HttpHeaders httpHeaders = owaspAddResponse.extractHeaders();
        headersToAdd.putAll(httpHeaders);
    }

    private void loadStaticOwaspSecureHeadersToRemove() throws IOException {
        ClassPathResource resource = new ClassPathResource("owasp-secure-headers-remove.json");
        String json = resource.getContentAsString(StandardCharsets.UTF_8);
        OwaspRemoveResponse owaspRemoveResponse = objectMapper.readValue(json, OwaspRemoveResponse.class);
        HttpHeaders httpHeaders = owaspRemoveResponse.extractHeaders();
        headersToRemove.putAll(httpHeaders);
    }
}
