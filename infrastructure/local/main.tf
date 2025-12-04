data "tfe_outputs" "spring" {
  organization = "mangila"
  workspace    = "spring-crud"
}

locals {
  ec2_instance_public_ip  = data.tfe_outputs.spring.values.ec2_instance_public_ip
  ec2_instance_public_dns = data.tfe_outputs.spring.values.ec2_instance_public_dns
  ssh_private_key_path    = "ansible/ansible-ssh"
  inventory_ini_path      = "ansible/inventory.ini"
  nginx_conf_path         = "ansible/nginx.conf"
}

# Generate ansibles inventory.ini file from template
resource "local_file" "ansible_inventory_ini_file" {
  lifecycle {
    precondition {
      condition     = fileexists(local.ssh_private_key_path)
      error_message = "ssh key not exists"
    }
    precondition {
      condition     = fileexists(local.inventory_ini_path) == false
      error_message = "inventory.ini already exists"
    }
    precondition {
      condition     = local.ec2_instance_public_ip != null
      error_message = "EC2 public ip is null"
    }
  }
  filename = local.inventory_ini_path
  content = templatefile("template/inventory.ini", {
    TPL_EC2_PUBLIC_IP = local.ec2_instance_public_ip
  })
}

# Generate nginx config file from template
resource "local_file" "nginx_conf_file" {
  lifecycle {
    precondition {
      condition     = fileexists(local.ssh_private_key_path)
      error_message = "ssh key not exists"
    }
    precondition {
      condition     = fileexists(local.nginx_conf_path) == false
      error_message = "nginx.conf already exists"
    }
    precondition {
      condition     = local.ec2_instance_public_dns != null
      error_message = "EC2 public dns is null"
    }
  }
  filename = local.nginx_conf_path
  content = templatefile("template/nginx.conf", {
    TPL_EC2_PUBLIC_DNS = local.ec2_instance_public_dns
  })
}

# Run playbook command
# resource "terraform_data" "generate_ssh_key_pair" {
#   count = local.should_generate_ssh_key ? 1 : 0
#   input = "python generate_ssh_key_pair.py"
#   provisioner "local-exec" {
#     working_dir = "keys"
#     command     = "python generate_ssh_key_pair.py"
#     quiet       = false
#     on_failure  = fail
#   }
# }
