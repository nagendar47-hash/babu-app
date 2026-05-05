# Spring Boot Product API

## Features
- CRUD REST API (/api/products)
- H2 (dev) and PostgreSQL (prod)
- Global Exception Handling
- Health endpoints (/ , /ready)

## Run locally
mvn spring-boot:run

## Build
mvn clean package

## Docker
docker build -t my-app .
docker run -p 8080:8080 my-app
