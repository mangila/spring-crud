package com.github.mangila.app.shared;

import com.github.mangila.app.model.owasp.OwaspAddResponse;
import com.github.mangila.app.model.owasp.OwaspRemoveResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Service
public class OwaspRestClient {

    private final RestClient restClient;

    public OwaspRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
        this.restClient = RestClient.builder()
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
