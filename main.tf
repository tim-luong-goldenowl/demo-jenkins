terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region = "ap-southeast-1"
}

resource "aws_instance" "web" {
  ami           = "ami-054c486632a4875d3"
  instance_type = "t2.micro"
  user_data = <<EOF
    #!/bin/bash

    sudo yum update
    sudo yum install docker
    sudo usermod -a -G docker ec2-user
    newgrp docker
    sudo systemctl enable docker.service
    sudo systemctl start docker.service
  EOF

  tags = {
    Name = "tim-first-instance"
  }
}
