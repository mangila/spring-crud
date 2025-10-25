# spring-crud

Spring Web app demonstrating C.R.U.D (Create, Read, Update, Delete) operations in a Layered Architecture.

Deployed to AWS and automated with IaC (Infrastructure as Code) and CM (Configuration Management) using Terraform and Ansible.

### Spring Boot

Spring RESTful service with CRUD operations for the Employee domain. (The famous Employee CRUD, great for demonstration purposes)

##### Swagger
- Swagger UI is available at http://localhost:8080/swagger-ui.html
- Swagger JSON is available at http://localhost:8080/v3/api-docs

### Terraform

In this example we use the Terraform Cloud remote state backend.

#### Terraform AWS

Resource created by Terraform is in the scope of the AWS free tier.

- Create a small VPC with Internet Gateway (Terraform AWS VPC module does the magic)
- Create a public EC2 instance (Terraform AWS EC2 module does the magic)
  - sg for HTTP access and SSH access
  - EC2 is a t3.micro instance (Free tier eligible in eu-north-1)

In the local folder is a Terraform local state project that runs some python and .tftpl(Terraform Template File) to manage the Ansible Control Node and provision the EC2 instance.

### Ansible

The Ansible Control Node is managed from our local machine.




