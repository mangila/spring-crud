# Local Infrastructure

Local developer environment for the project. This folder contains:

- `compose.yaml` — Docker Compose for a local PostgreSQL instance.
- Ansible bootstrap for local automation and for provisioning when targeting remote hosts from your machine.
- Templates and examples (e.g., NGINX config) for local testing.

## Prerequisites

- Docker and Docker Compose
- Python 3 and `pip`

Optional (for Ansible automation):
- SSH key pair for connecting to remote hosts created from this folder

## Start local PostgreSQL

If Spring Boot does not auto-start the container via its Docker Compose integration, you can start it manually:

```
docker compose -f infrastructure/local/compose.yaml up -d
```

Default connection parameters used by the app configurations:
- host: `localhost`
- port: `5432`
- database: `mydatabase`
- username: `myuser`
- password: `secret`

## Ansible (optional)

Install Ansible into your Python environment:

```
pip install ansible
```

Initialize Ansible configuration (once):

```
ansible-config init --disabled > ansible.cfg
```

Install required collections/roles:

```
ansible-galaxy collection install -r requirements.yml
```

Generate an SSH key pair for provisioning (adjust email/comment as desired):

```
ssh-keygen -t ed25519 -f ansible-ssh -C "developer@example.com" -N ""
```

The public key may be referenced by Terraform/Ansible when creating or connecting to instances.

## NGINX template

Terraform template file is provided for NGINX reverse-proxy setup. See `template/nginx.conf.tpl` for details.

## Ports

- PostgreSQL: 5432 (host -> container)

## Troubleshooting

- If containers fail to start, ensure no local service is already bound to `5432`.
- If Spring Boot’s Docker Compose integration is enabled, it will skip starting PostgreSQL if it’s already running; this is expected.