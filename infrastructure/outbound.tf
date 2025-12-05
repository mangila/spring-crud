output "ec2_instance_public_ip" {
  value = module.ec2_instance.public_ip
}

output "ec2_instance_public_dns" {
  value = module.ec2_instance.public_dns
}

output "ansible_key_name" {
  value = local.ansible_key_name
}