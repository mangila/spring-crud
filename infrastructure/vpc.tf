data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  first_azs = data.aws_availability_zones.available.names[0]
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 6"

  name = "the-vpc"

  # 4,096 IPs total
  cidr = "10.0.0.0/20"
  # (256 IPs) Should be enough :P
  public_subnets      = ["10.0.0.0/24"]
  public_subnet_names = [local.first_azs]
  azs                 = [local.first_azs]

  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = aws_servicecatalogappregistry_application.spring_application.application_tag
}