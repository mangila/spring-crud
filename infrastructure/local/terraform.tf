terraform {
  required_version = ">= 1.11.0"

  required_providers {
    tfe = {
      source  = "hashicorp/tfe"
      version = "~> 0"
    }
  }
}