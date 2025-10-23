data "tfe_outputs" "spring" {
  organization = "<YOUR ORG>"
  workspace    = "spring-crud"
}

locals {
  has_ssh_key_file       = fileexists("keys/key")
  has_ansible_ini_file   = fileexists("ansible/inventory.init")
  ec2_instance_public_ip = try(data.tfe_outputs.spring.values.ec2_instance_public_ip, null)

  should_generate_ssh_key     = local.has_ssh_key_file == false
  should_generate_ini_tpl_file = local.has_ansible_ini_file == false && local.ec2_instance_public_ip != null
}

# Generate the external SSH key pair on disk, only if not exists
resource "terraform_data" "generate_ssh_key_pair" {
  count = local.should_generate_ssh_key ? 1 : 0
  input = "python generate_ssh_key_pair.py"
  provisioner "local-exec" {
    working_dir = "keys"
    command     = "python generate_ssh_key_pair.py"
    quiet       = false
    on_failure  = fail
  }
}

# Generate ansibles inventory.ini file
resource "local_file" "ansible_inventory_ini_file" {
  count    = local.should_generate_ini_tpl_file ? 1 : 0
  filename = "ansible/inventory.ini"
  content = templatefile("inventory.ini.tftpl", {
    TPL_EC2_PUBLIC_IP = local.ec2_instance_public_ip
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
