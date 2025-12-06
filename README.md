# spring-crud

Spring Boot demo showcasing classic CRUD (Create, Read, Update, Delete) on an Employee domain with a clean, layered architecture. The project is split into modules for API, background processing, and infrastructure, plus a test area for live/E2E suites.

**Note: This repository is for learning/demonstration. It contains opinionated choices and serves as a personal reference.**

See the [HOWTORUN.md](HOWTORUN.md) for more details about how to start and deploy this application.

## Modules

- api — Spring Boot REST API exposing CRUD operations and related patterns. See `api/README.md`.
- background — Headless Spring Boot app for background jobs and notifications. See `background/README.md`.
- infrastructure — Terraform + Ansible for cloud and local developer setup. See `infrastructure/README.md` and `infrastructure/local/README.md`.
- test — Guidance and structure for E2E/performance/security testing. See `test/README.md`.

## Key architecture and patterns

- PostgreSQL as the persistence layer (local DB via Docker Compose).
- Transactional Outbox pattern for event publishing; outbox table can support replays.
- PostgreSQL LISTEN/NOTIFY for fire-and-forget notifications.
- Optional background scheduling with JobRunr in the background module.
- OpenAPI/Swagger for API exploration.
- OWASP Secure Headers reference integration.

## Prerequisites

- Java 21
- Maven 3.9+ (wrapper provided: `./mvnw`)
- Docker (for local PostgreSQL via Docker Compose)
- Terraform CLI if provisioning cloud infra (see infra README)
- Python 3 + pip if using local Ansible automation

## Quickstart (local)

1) Start local PostgreSQL (if not auto-started by Spring Boot):
- `docker compose -f infrastructure/local/compose.yaml up -d`

2) Run the API:
- `./mvnw -pl api -am spring-boot:run`
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

3) Run the Background module (optional):
- `./mvnw -pl background -am spring-boot:run`

## Build and test

- Build all: `./mvnw clean package`
- Run all tests: `./mvnw test`
- Module-specific builds/tests (examples):
  - API: `./mvnw -pl api -am clean package`
  - Background: `./mvnw -pl background -am clean package`

## Infrastructure

Infrastructure as Code is under `infrastructure/` using Terraform (AWS, remote state via Terraform Cloud in the example) and Ansible. A `local/` subfolder contains developer conveniences such as a Docker Compose file for PostgreSQL. See detailed docs in:

- `infrastructure/README.md` — Cloud and local overview, Terraform usage
- `infrastructure/local/README.md` — Local Compose, Ansible snippets, SSH key setup

## Testing beyond the codebase

Live/E2E, load, and security test conventions are described in `test/README.md`. The project also uses pre-commit hooks to automate checks before commits. See `.pre-commit-config.yaml`.

## Security headers

The API fetches OWASP Secure Headers reference collections:
- https://owasp.org/www-project-secure-headers/ci/headers_add.json
- https://owasp.org/www-project-secure-headers/ci/headers_remove.json

## License

This project is licensed under the terms of the LICENSE file in the repository root.




