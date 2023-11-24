# ElasticService

ElasticService is a Spring Boot application that provides REST API endpoints for interacting with an AWS Elasticsearch cluster. It enables creating Elasticsearch indices, adding documents, and retrieving documents by ID.

## Setup and Deployment

### Prerequisites

- AWS Account
- Terraform installed
- Java JDK 17
- Maven
- Git

### Initial AWS Resource Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/josebricenor/ElasticService.git
   cd ElasticService

## Create AWS Elasticsearch and S3 Bucket
Navigate to the Terraform setup directory and run Terraform commands.

    ```bash 
    cd setup
    terraform init
    terraform plan
    terraform apply

## Building and Deploying the Application

    ```bash
    cd ..
    mvn clean package

## Deploy to AWS Elastic Beanstalk
Push your changes to GitHub. GitHub Actions will automatically build, upload the JAR to S3, and deploy to AWS Elastic Beanstalk.

    ```bash
    git add .
    git commit -m "Deploying application"
    git push origin main

## API Endpoints

    POST /api/createIndex: Create an Elasticsearch index.
    POST /api/createDocument: Add a document to an index.
    GET /api/getDocument/{index}/{id}: Retrieve a document by ID.
    GET /docs: ElasticBeanstack health.

## Continuous Integration & Deployment

CI/CD is handled by GitHub Actions.
Pushes to the main branch trigger the build and deployment pipeline.

## Notes

Ensure AWS credentials are securely managed and not exposed.
Modify the Terraform scripts and application properties as per your AWS configuration.