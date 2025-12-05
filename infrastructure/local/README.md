# Local Infrastructure

It is a mix of Ansible and Terraform automation

Install ansible via pip with the Python virutal env

``pip install ansible``

and also the home of the local development `compose.yaml` to spin up a Postgres db.

`ssh-keygen -t ed25519 -f ansible-ssh -C "olsson.erik1993@gmail.com" -N ""`

` ansible-config init --disabled > ansible.cfg `

` ansible-galaxy collection install -r requirements.yml`