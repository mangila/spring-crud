locals {
  project_name = "spring-crud"
  aws_region   = "eu-north-1"
  repository   = "https://github.com/mangila/spring-crud"
}

provider "aws" {
  region = local.aws_region
  default_tags {
    tags = {
      repository = local.repository
    }
  }
}

resource "aws_servicecatalogappregistry_application" "spring_application" {
  name        = local.project_name
  description = "Spring Boot is amazing for CRUD apps!"
}
