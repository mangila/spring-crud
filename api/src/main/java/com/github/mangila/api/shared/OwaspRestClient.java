package com.github.mangila.api.shared;

import com.github.mangila.api.model.owasp.OwaspAddResponse;
import com.github.mangila.api.model.owasp.OwaspRemoveResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

/**
 * This is an optional approach to stay updated with the OWASP Secure Headers project.
 * Static file in the resources folder should "be good enough" and of course, not all headers are required.
 * Depends on the use case.
 */
@Service
public class OwaspRestClient {

    private final RestClient restClient;

    public OwaspRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
        this.restClient = builder
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .baseUrl("https://www.owasp.org/")
                .defaultHeader("User-Agent", "spring-crud/mangila.github")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * <p>
     * Fetch OWASP secure http headers collection to add from the OWASP HTTP secure headers project.
     * </p>
     */
    public OwaspAddResponse fetchOwaspSecureHeadersToAdd() {
        return restClient.get()
                .uri("/www-project-secure-headers/ci/headers_add.json")
                .retrieve()
                .body(OwaspAddResponse.class);
    }

    /**
     * <p>
     * Fetch OWASP secure http headers collection to remove from the OWASP HTTP secure headers project.
     * </p>
     */
    public OwaspRemoveResponse fetchOwaspSecureHeadersToRemove() {
        return restClient.get()
                .uri("/www-project-secure-headers/ci/headers_remove.json")
                .retrieve()
                .body(OwaspRemoveResponse.class);
    }
}
