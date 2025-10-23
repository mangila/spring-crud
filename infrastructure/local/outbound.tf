output "ansible_inventory_ini_file" {
  value = local_file.ansible_inventory_ini_file
}

output "generate_ssh_key_pair" {
  value = terraform_data.generate_ssh_key_pair
}

output "generate_ssh_key_pair_command" {
  value = terraform_data.generate_ssh_key_pair.input
}