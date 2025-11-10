package com.github.mangila.app.config;

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
        value = "com.github.mangila.app.repository",
        repositoryBaseClass = BaseJpaRepositoryImpl.class
)
public class JpaConfig {
}
