# Infrastructure Module

This module provisions cloud infrastructure and offers a local developer setup.

- Cloud: AWS resources with Terraform (remote state via Terraform Cloud in this example)
- Local: Docker Compose for PostgreSQL, plus Ansible snippets for provisioning/automation

## Contents

- Terraform (root of this folder)
  - `main.tf`: provider and AppRegistry setup
  - `vpc.tf`: VPC (via terraform-aws-modules/vpc)
  - `ec2.tf`: Security Group, SSH key, and EC2 instance (via terraform-aws-modules/ec2-instance)
  - `terraform.tf`: required versions/providers and Terraform Cloud workspace
  - `outbound.tf`: outputs (e.g., EC2 public IP)
- Local
  - `local/compose.yaml`: Docker Compose with a PostgreSQL service on `5432`
  - `local/template/`: examples such as `nginx.conf`
  - See `local/README.md` for details

## Prerequisites

- Terraform CLI >= 1.13
- Terraform Cloud account and a workspace named `spring-crud` under org `mangila` (or adjust `terraform.tf`)
- AWS credentials configured for the target account/region (`eu-north-1`)

## Terraform usage (AWS)

1. Initialize: `terraform init`
2. Review plan: `terraform plan`
3. Apply: `terraform apply`
4. Outputs: `terraform output` (e.g., `ec2_instance_public_ip`)

Notes:
- Resources are intended to align with AWS Free Tier when possible (e.g., `t3.micro` in `eu-north-1`).
- Security groups open SSH (22) and HTTP (80) as defined—review before applying to shared accounts.
- EC2 key pair reads a public key from `local/ansible.pub` by default; ensure the file exists or adjust the path.

## Local development (Docker Compose)

Start a local PostgreSQL (if Spring Boot doesn’t auto-start it via Compose integration):

```
docker compose -f infrastructure/local/compose.yaml up -d
```

Connection defaults used by app configs:
- host: `localhost`, port: `5432`
- database: `mydatabase`
- username: `myuser`, password: `secret`

## Region and tagging

- Default region: `eu-north-1` (configured in `main.tf`).
- Default tags include repository metadata and an AWS AppRegistry application reference.