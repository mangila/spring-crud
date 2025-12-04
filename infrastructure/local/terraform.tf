terraform {
  required_version = ">= 1.13.0"

  required_providers {
    tfe = {
      source  = "hashicorp/tfe"
      version = "~> 0"
    }
  }
}