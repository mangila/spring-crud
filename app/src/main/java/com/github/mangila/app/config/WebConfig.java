package com.github.mangila.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.Clock;
import java.time.ZoneId;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    public static final String OWASP_LAST_UPDATE_UTC_HTTP_HEADER = "X-OWASP-Secure-Headers-Project-Last-Update-UTC";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var cors = new CorsConfiguration()
                .applyPermitDefaultValues();
        cors.addAllowedMethod(HttpMethod.PUT);
        cors.addAllowedMethod(HttpMethod.DELETE);
        registry.addMapping("/**")
                .combine(cors);
    }

    @Bean
    HttpHeaders oWaspSecureHeaders() {
        return new HttpHeaders();
    }

    /**
     * Add the OWASP secure http headers to every response.
     * OncePerRequestFilter, we can be sure that the headers are only added once per request.
     */
    @Bean
    OncePerRequestFilter owaspSecureHeadersFilter(HttpHeaders oWaspSecureHeaders) {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                oWaspSecureHeaders.asSingleValueMap()
                        .forEach(response::setHeader);
                filterChain.doFilter(request, response);
            }
        };
    }

    /**
     * <p>
     * The Clock bean tells Spring Boot what time zone to use.
     * </p>
     * <p>
     * If playing around with Time in Spring Boot, you can have a Clock bean to manipulate time with.
     * Deployment server could have different time zones, and also the Database could have a different time zone.
     * Can cause issues and confusion. Use UTC everywhere.
     * </p>
     * <p>
     *     <ul>
     *         <li>Server = UTC</li>
     *         <li>Database = UTC</li>
     *         <li>Logs/Audit = UTC</li>
     *         <li>Presentation = Local date time OR send UTC and let the client work their own logic</li>
     *     </ul>
     * </p>
     * <p>
     *     Nice with Testing to be able to manipulate time instead of do Thread.sleep and other creative stuffs.
     *     All-time creation and manipulation should use this bean.
     * </p>
     */
    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of("Europe/Stockholm"));
    }
}
