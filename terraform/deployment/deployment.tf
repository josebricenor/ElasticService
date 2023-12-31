provider "aws" {
  region     = "us-west-2"
  access_key = "AKIASDVWZ24TK6KALRWS"
  secret_key = "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"
}

terraform {
  backend "s3" {
  }
}

data "aws_vpc" "default" {
  default = true
}

resource "aws_elastic_beanstalk_application" "elastic_service_app" {
  name        = "ElasticServiceApplication"
  description = "ElasticService Application"
}

resource "aws_elastic_beanstalk_application_version" "elastic_service_version" {
  name        = "ElasticService-0.0.1-SNAPSHOT"
  application = aws_elastic_beanstalk_application.elastic_service_app.name
  description = "Version 0.0.1-SNAPSHOT of ElasticService"
  bucket      = "elastic-service-app"
  key         = "ElasticService-0.0.1-SNAPSHOT.jar"
}

resource "aws_elastic_beanstalk_environment" "elastic_service_env" {
  name                = "ElasticServiceEnvironment"
  application         = aws_elastic_beanstalk_application.elastic_service_app.name
  solution_stack_name = "64bit Amazon Linux 2023 v4.1.1 running Corretto 17"
  version_label       = aws_elastic_beanstalk_application_version.elastic_service_version.name

  setting {
    namespace = "aws:autoscaling:asg"
    name      = "MaxSize"
    value     = 1
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "LoadBalancerType"
    value     = "application"
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "ELBScheme"
    value     = "internet facing"
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "MatcherHTTPCode"
    value     = 200
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "HealthCheckPath"
    value     = "/docs"
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "S3_BUCKET"
    value     = "elastic-service-app"
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = "t2.micro"
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "IamInstanceProfile"
    value     = "aws-elasticbeanstalk-ec2-role"
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "SecurityGroups"
    value     = "elastic-beanstalk-sg"
  }
}

