# spring-crud

Spring Web app demonstrating C.R.U.D (Create, Read, Update, Delete) operations in a Layered Architecture

**NOTE: This project is just for demonstration purposes. Just me setting up a project, nothing fancy.
I mostly use this kind of repos as my flashcards.**

## Architecture

Postgres database is used as the persistence layer.

Transactional Outbox pattern is used to send events, the outbox table can be used for event sourcing replays.

Postgres LISTEN/NOTIFY is used for Fire and Forget (FaF) Notifications.

[JobRunr](https://www.jobrunr.io) is being used for background tasks scheduling in the background module.

Deployed to AWS and automated with IaC (Infrastructure as Code) and CM (Configuration Management) using Terraform and
Ansible.

Live Integration testing is done with [Venom](https://github.com/ovh/venom).

[Pre-commit](https://pre-commit.com/) hooks are used to run tests before a git commit.

### Api

Spring RESTful API with CRUD operations for the Employee domain. (The famous Employee CRUD, great for demonstration
purposes)

- Spring Web
- Spring Actuator
- Spring Scheduler
- Spring ApplicationPublisher
- Spring Validation
- Spring JPA (with hypersistence-utils)
- Spring AOP
- Postgres w Flyway
- Postgres LISTEN/NOTIFY
- Spring Server Sent Events
- Resilience4j
- JSpecify
- Ensure4j
- Spring Test with Testcontainers and JsonUnit

##### Swagger

- Swagger UI is available at http://localhost:8080/swagger-ui.html
- Swagger JSON is available at http://localhost:8080/v3/api-docs

##### Owasp Secure Headers

Project is configured to fetch the latest [OWASP secure headers project](https://owasp.org/www-project-secure-headers)
reference collections

The latest headers are fetched from the following URL:

- https://owasp.org/www-project-secure-headers/ci/headers_add.json
- https://owasp.org/www-project-secure-headers/ci/headers_remove.json

### Background

Spring Boot with JobRunr background tasks scheduler.

### Infrastructure

Terraform and Ansible are used for provisioning the infrastructure.

#### Terraform

In this example we use the Terraform Cloud remote state backend.

##### AWS

Resource created by Terraform is in the scope of the AWS free tier.

- Create a small VPC with Internet Gateway (Terraform AWS VPC module does the magic)
- Create a public EC2 instance (Terraform AWS EC2 module does the magic)
    - sg for HTTP access and SSH access
    - EC2 is a t3.micro instance (Free tier eligible in eu-north-1)

In the local folder is a Terraform local state project that runs some python and .tftpl(Terraform Template File) to
manage the Ansible Control Node and provision the EC2 instance.

#### Ansible

The Ansible Control Node is managed from our local machine.

### Test

Live testing suites

### Project Automation with Python

Python virtual environment is created in the root of the project, so it can run python automation scripts.

The virtual env is being .gitignore, so it is not pushed to the repository.

Pre-commit hooks are used to run tests before a git commit. [.pre-commit-config.yaml](.pre-commit-config.yaml)




