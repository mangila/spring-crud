terraform {
  required_version = ">= 1.11.0"
  # if want to run locally remove the cloud block
  # in this example we inject the EC2 ip address to the local project via tfe_outputs
  # if running both projects locally you can use data source "terraform_remote_state" and point it to the local tf files
  cloud {
    organization = "<YOUR ORG NAME>"
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