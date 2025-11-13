package com.github.mangila.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mangila.app.config.WebConfig;
import com.github.mangila.app.model.owasp.OwaspResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    private final HttpHeaders oWaspSecureHeaders;

    public Bootstrap(ObjectMapper objectMapper,
                     HttpHeaders oWaspSecureHeaders) {
        this.objectMapper = objectMapper;
        this.oWaspSecureHeaders = oWaspSecureHeaders;
    }

    @Override
    public void run(String... args) throws Exception {
        loadStaticOwaspSecureHeader();
    }

    /**
     * <p>
     * Load the OWASP secure headers as a static resource
     * </p>
     */
    private void loadStaticOwaspSecureHeader() throws IOException {
        ClassPathResource resource = new ClassPathResource("owasp-secure-headers.json");
        String json = resource.getContentAsString(StandardCharsets.UTF_8);
        OwaspResponse owaspResponse = objectMapper.readValue(json, OwaspResponse.class);
        applyHeaders(owaspResponse, oWaspSecureHeaders);
    }

    public static void applyHeaders(OwaspResponse response, HttpHeaders oWaspSecureHeaders) {
        log.info("Apply OWASP secure headers");
        MultiValueMap<String, String> owasp = new LinkedMultiValueMap<>();
        response.headers().forEach(header -> {
            // Add all header key value pair to the MultiValueMap
            owasp.add(header.name(), header.value());
        });
        // add the last update timestamp
        owasp.add(WebConfig.OWASP_LAST_UPDATE_UTC_HTTP_HEADER, response.timestamp());
        // Clear the existing headers
        oWaspSecureHeaders.clear();
        // Add the new security headers to the HttpHeaders
        oWaspSecureHeaders.putAll(owasp);
        log.info("OWASP secure headers applied successfully - {}", oWaspSecureHeaders.toSingleValueMap());
    }
}
