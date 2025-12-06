# API Module

Purpose
- Exposes a RESTful API for the Employee domain demonstrating classic CRUD (Create, Read, Update, Delete) operations in a layered Spring Boot architecture.
- Showcases patterns and tooling: Transactional Outbox, Postgres LISTEN/NOTIFY for fire‑and‑forget notifications, Server‑Sent Events (SSE), validation, AOP, and Resilience4j.

Tech stack
- Java 21, Spring Boot 3.5
- Spring Web, Validation, AOP
- Spring Data JPA + PostgreSQL (with Flyway and hypersistence-utils)
- Actuator
- SpringDoc OpenAPI
- Resilience4j
- Testcontainers, JSON‑Unit, ArchUnit

Prerequisites
- Java 21
- Maven wrapper `./mvnw`
- Docker (for local PostgreSQL via Docker Compose integration)

Local development
- The module is configured to use Spring Boot’s Docker Compose integration to start a local PostgreSQL automatically.
  - Compose file: `infrastructure/local/compose.yaml`
- Start the API:
  - Maven: `./mvnw -pl api -am spring-boot:run`
  - Or from IDE: run class `com.github.mangila.api.Application`.
- Default URLs:
  - Swagger UI: http://localhost:8080/swagger-ui.html
  - OpenAPI JSON: http://localhost:8080/v3/api-docs
  - Actuator (selected endpoints): http://localhost:8080/actuator

Configuration notes
- See `api/src/main/resources/application.yml` for application, JPA, and Actuator settings.
- HTTP/2 is enabled; compression is enabled for JSON.
- JPA uses `ddl-auto: create` for demo purposes; replace with migrations for persistent environments.
- Spring Boot Docker Compose integration is enabled; it will start PostgreSQL if not already running and skip starting when it’s already up.

Key features in code
- EmployeeController: REST endpoints for employees.
- Transactional Outbox tables and repositories for event publishing.
- PostgreSQL LISTEN/NOTIFY for push-style notifications.
- Optional schedulers and notification listeners guarded by configuration flags.

Build & test
- Build: `./mvnw -pl api -am clean package`
- Run tests: `./mvnw -pl api -am test`

Packaging & Docker
- Standard Spring Boot fat JAR via `spring-boot-maven-plugin`.
- A `Dockerfile` is present to containerize the API if needed.

Quick smoke test
- Create an employee (example):
```
curl -X POST http://localhost:8080/api/v1/employees \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com"}'
```
- List employees:
```
curl http://localhost:8080/api/v1/employees
```