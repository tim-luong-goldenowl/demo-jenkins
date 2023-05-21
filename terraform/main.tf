resource "aws_instance" "web" {
  ami           = "ami-0406b8387ac37a82a"
  instance_type = "t2.micro"

  tags = {
    Name = "tim-first-instance"
  }
}