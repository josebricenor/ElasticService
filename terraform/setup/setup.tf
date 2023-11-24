provider "aws" {
  region     = "us-west-2"
  access_key = "AKIASDVWZ24TK6KALRWS"
  secret_key = "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"
}

resource "aws_elasticsearch_domain" "elastic_service" {
  domain_name           = "elastic-service-domain"
  elasticsearch_version = "7.9"

  advanced_security_options {
    enabled                        = true
    internal_user_database_enabled = true
    master_user_options {
      master_user_name     = "josebricenor"
      master_user_password = "uhT^9$%c"
    }
  }

  cluster_config {
    instance_type = "r5.large.elasticsearch"
  }

  ebs_options {
    ebs_enabled = true
    volume_size = 10
  }

  node_to_node_encryption {
    enabled = true
  }

  encrypt_at_rest {
    enabled = true
  }

  domain_endpoint_options {
    enforce_https       = true
    tls_security_policy = "Policy-Min-TLS-1-0-2019-07"
  }

  access_policies = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          AWS = "*"
        }
        Action = "es:*"
        Resource = "arn:aws:es:us-west-2:145338521382:domain/elastic-service-domain/*"
      }
    ]
  })

  snapshot_options {
    automated_snapshot_start_hour = 23
  }
}

resource "aws_s3_bucket" "elastic_service_bucket" {
  bucket = "elastic-service-app"
}
