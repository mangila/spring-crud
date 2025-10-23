resource "aws_security_group" "ec2_sg" {
  name        = "ec2_sg"
  description = "EC2 sg with ssh access and HTTP from anywhere"
  vpc_id      = module.vpc.vpc_id
}

resource "aws_vpc_security_group_ingress_rule" "allow_all_ssh_traffic_ipv4" {
  security_group_id = aws_security_group.ec2_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 22
  ip_protocol       = "tcp"
  to_port           = 22
}

resource "aws_vpc_security_group_egress_rule" "allow_all_HTTP_traffic_ipv4" {
  security_group_id = aws_security_group.ec2_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 80
  ip_protocol       = "tcp"
  to_port           = 80
}

resource "aws_key_pair" "local_key" {
  key_name   = "local-key"
  public_key = file("local/keys/key.pub")
}

module "ec2_instance" {
  source  = "terraform-aws-modules/ec2-instance/aws"
  version = "~> 6"

  name                        = "the-box"
  instance_type               = "t3.micro"
  ami                         = "ami-0a716d3f3b16d290c" # ubuntu machine
  key_name                    = aws_key_pair.local_key.key_name
  subnet_id                   = module.vpc.public_subnets
  vpc_security_group_ids      = [aws_security_group.ec2_sg.id]
  associate_public_ip_address = true

  tags = aws_servicecatalogappregistry_application.spring_application.application_tag
}