variable "IMAGE_NAME" {
  type = string
  default = "asdad"
}

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
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]
  user_data = <<EOF
    #!/bin/bash

    sudo yum update -y
    sudo yum install docker -y
    sudo usermod -a -G docker ec2-user
    newgrp docker
    sudo systemctl enable docker.service
    sudo systemctl start docker.service
    sudo docker rm -f $(docker ps -a -q)
    docker rmi $(docker images | grep 'thailuong/sample-node')
    sudo docker run -p 80:3000 -d ${var.IMAGE_NAME}
  EOF

  tags = {
    Name = "tim-first-instance"
  }
}

# data "aws_instance" "web" {
#     filter {
#         name = "tag:Name"
#         values = ["tim-first-instance"]
#     }

#     depends_on = [
#       "aws_instance.web"
#     ]
# }

# # the following is_ec2_instance_exist local should return value 1 if resource exists
# locals {
#   is_ec2_instance_exist = "${data.aws_instance.web.public_ip}"
# }

# # Here is the output block printing the existence of the resource onto the console
# output "fetched_info_from_aws" {
#   value = data.aws_instance.web.public_ip
# }
