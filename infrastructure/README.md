# Infrastructure Module

Purpose
- Provision cloud infrastructure for the demo application and provide local developer infrastructure.
- Cloud: AWS resources managed by Terraform (remote state via Terraform Cloud).
- Local: Docker Compose for a PostgreSQL instance used by the API and Background modules during development.

Contents
- Terraform (root of this folder)
  - `main.tf`: provider and app registry setup
  - `vpc.tf`: VPC (using terraform-aws-modules/vpc)
  - `ec2.tf`: Security Group, SSH key, and EC2 instance (using terraform-aws-modules/ec2-instance)
  - `terraform.tf`: Terraform required versions, providers, and Terraform Cloud workspace
  - `outbound.tf`: outputs (e.g., EC2 public IP)
- Local
  - `local/compose.yaml`: Docker Compose with a PostgreSQL service exposed on `5432`.

Prerequisites
- Terraform CLI >= 1.13
- Terraform Cloud account and a workspace named `spring-crud` under organization `mangila` (or adjust `terraform.tf`).
- AWS credentials configured in your environment for the target account/region (`eu-north-1`).

Terraform usage (AWS)
1. Initialize: `terraform init`
2. Review plan: `terraform plan`
3. Apply: `terraform apply`
4. Output: `terraform output` (e.g., `ec2_instance_public_ip`).

Notes
- Resources aim to stay within AWS free tier where possible (e.g., `t3.micro` in `eu-north-1`).
- The EC2 security group opens SSH (22) and HTTP (80) outbound/ingress as defined; review before applying in shared accounts.
- The EC2 key pair reads a public key from `local/ansible.pub`; ensure the file exists or adjust the path.

Local development (Docker Compose)
- Start PostgreSQL locally (if not started automatically by Spring Boot):
  - `docker compose -f infrastructure/local/compose.yaml up -d`
- Connection defaults in app configs align with:
  - user: `myuser`, password: `secret`, database: `mydatabase`, host: `localhost`, port: `5432`.

Region and tagging
- Default region: `eu-north-1` (set in `main.tf`).
- Default tags include a repository link via AWS AppRegistry application.