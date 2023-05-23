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
  ami           = "ami-0406b8387ac37a82a"
  instance_type = "t2.micro"
  user_data = "${file("./scripts/install_docker.sh")}"

  tags = {
    Name = "tim-first-instance"
  }
}
