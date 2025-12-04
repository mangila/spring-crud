data "tfe_outputs" "spring" {
  organization = "mangila"
  workspace    = "spring-crud"
}

locals {
  has_ssh_key_file        = fileexists("ansible")
  has_ansible_ini_file    = fileexists("inventory.init")
  ec2_instance_public_ip  = try(data.tfe_outputs.spring.values.ec2_instance_public_ip, null)
  ec2_instance_public_dns = try(data.tfe_outputs.spring.values.ec2_instance_public_dns)

  should_generate_ssh_key      = local.has_ssh_key_file == false
  should_generate_ini_tpl_file = local.has_ansible_ini_file == false && local.ec2_instance_public_ip != null
}

# Generate the external SSH key pair on disk, only if not exists
# For Ansible login
resource "terraform_data" "generate_ssh_key_pair" {
  count = local.should_generate_ssh_key ? 1 : 0
  provisioner "local-exec" {
    working_dir = "keys"
    command     = "ssh-keygen -t ed25519 -f ansible -N \"\""
    quiet       = false
    on_failure  = fail
  }
}

# Generate ansibles inventory.ini file from template
resource "local_file" "ansible_inventory_ini_file" {
  count    = local.should_generate_ini_tpl_file ? 1 : 0
  filename = "ansible/inventory.ini"
  content = templatefile("template/inventory.ini", {
    TPL_EC2_PUBLIC_IP = local.ec2_instance_public_ip
  })
}

# Generate nginx config file from template
resource "local_file" "nginx_conf_file" {
  count    = local.should_generate_ini_tpl_file ? 1 : 0
  filename = "ansible/nginx.conf"
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
