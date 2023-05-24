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

resource "aws_security_group" "ec2_sg" {
  name        = "allow_http"
  description = "Allow http inbound traffic"

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    cidr_blocks     = ["0.0.0.0/0"]
  }

  tags = {
    Name = "terraform-ec2-security-group"
  }
}

resource "aws_instance" "web" {
  ami           = "ami-054c486632a4875d3"
  instance_type = "t2.micro"
  security_groups = [aws_security_group.ec2_sg.id]
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
