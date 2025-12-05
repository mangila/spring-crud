terraform {
  required_version = ">= 1.13.0"
  cloud {
    organization = "mangila"
    workspaces {
      name = "spring-crud"
    }
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}
