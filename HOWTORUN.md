# How to run and deploy to AWS

## Requirement

- Python
- Terraform CLI >= 1.13
- SSH

### Terraform

Terraform cloud is configured for remote state management. In `infrastructure/terraform.tf` adjust the workspace name to match
your Terraform Cloud workspace. Or run it locally. 

The `infrastructure/local` terraform project is local automation for provisioning the application.

### SSH

Generate an SSH key pair for provisioning (adjust email/comment as desired):

Put the keys in `infrastructure/local/ansible`

```
ssh-keygen -t ed25519 -f ansible-ssh -C "developer@example.com" -N ""
```

**Make sure the new public key is referenced by Terraform/Ansible when creating or connecting to instances.**

See `infrastructure/ec2.tf` for details.

### Python and Ansible

Install Ansible into your Python environment:

```
pip install ansible
```

**Run all Ansible commands from the `infrastructure/local/ansible` folder for isolated and local execution.**

Install required collections/roles:

```
ansible-galaxy collection install -r requirements.yml
```

### Deploy the application to AWS:

Run `terraform apply` in the `infrastructure` folder to provision the infrastructure.

Run `terraform apply` in the `infrastructure/local` folder to provision for the Ansible automation.

Run `ansible-playbook playbooks/playbook.yaml` to provision the application to the EC2 instance.

The EC2 instance will be accessible via SSH on port 22 and on port 80 via its public IP and DNS name.

### Build and push the Docker images:

`ansible-playbook playbooks/local-build-images.yaml`

The playbook will build and push the Docker images to Docker Hub. Running on the local machine.
