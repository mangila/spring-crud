# Background Module

Purpose
- Headless Spring Boot application for background processing and scheduled jobs related to the API domain.
- Runs without an embedded web server and is prepared to orchestrate background tasks and notifications.

Tech stack
- Java 21, Spring Boot 3.5
- Non‑web Spring Boot app (`web-application-type: none`)
- Virtual threads enabled
- PostgreSQL via Spring Boot Docker Compose integration
- Job scheduling (prepared for JobRunr library)

Prerequisites
- Java 21
- Maven wrapper `./mvnw`
- Docker (for local PostgreSQL via Docker Compose integration)

Local development
- The module is configured to auto‑start a local PostgreSQL using Spring Boot’s Docker Compose integration.
  - Compose file: `infrastructure/local/compose.yaml`
- Start the Background app:
  - Maven: `./mvnw -pl background -am spring-boot:run`
  - Or from IDE: run class `com.github.mangila.background.BackgroundApplication`.

Configuration notes
- See `background/src/main/resources/application.yml` for settings.
- Runs as a non‑web application and enables virtual threads.
- Uses the same local PostgreSQL as the API module for development convenience.

Build & test
- Build: `./mvnw -pl background -am clean package`
- Run tests: `./mvnw -pl background -am test`

Quick check
- Verify it boots and connects to the same local PostgreSQL as the API. Logs should indicate non‑web mode and successful datasource initialization.