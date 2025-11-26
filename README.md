# order-inventory-microservices-assignment
# Microservices Demo – Inventory Service & Order Service

This repository contains two Spring Boot microservices that communicate using REST APIs. Each service runs independently with its own H2 in-memory database and its own API documentation.

## Services Overview

Inventory Service:
- Manages products and inventory batches.
- Supports multiple batches per product with expiry dates.
- Returns batches sorted by expiry date.
- Deducts inventory when an order is placed.
- Uses the Factory Pattern for extensible inventory logic.
- Uses Spring Data JPA with H2 database.
- Endpoints:
    - GET /inventory/{productId}
    - POST /inventory/update

Order Service:
- Accepts and processes orders.
- Communicates with Inventory Service to check stock.
- Deducts inventory using Inventory Service.
- Stores order data in H2 database.
- Uses RestTemplate for inter-service calls.
- Endpoint:
    - POST /order

## Project Structure

inventory-service  
order-service  
README.md

Each folder contains an independent Spring Boot project with its own pom.xml file.

## Running the Services

1. Start the Inventory Service:
   cd inventory-service  
   mvn spring-boot:run  
   Runs at: http://localhost:8081

2. Start the Order Service:
   cd order-service  
   mvn spring-boot:run  
   Runs at: http://localhost:8082

## API Documentation (Swagger)

Inventory Service:
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs
- OpenAPI YAML: http://localhost:8081/v3/api-docs.yaml

Order Service:
- Swagger UI: http://localhost:8082/swagger-ui.html
- OpenAPI JSON: http://localhost:8082/v3/api-docs
- OpenAPI YAML: http://localhost:8082/v3/api-docs.yaml

## API Usage

Inventory Service:
GET /inventory/{productId}  
POST /inventory/update  
Example payload:  
{
"productId": 1,
"quantity": 5
}

Order Service:
POST /order  
Example payload:  
{
"productId": 1,
"quantity": 10
}

## Testing

Both services include unit and integration tests.

Run tests:

Inventory Service:  
cd inventory-service  
mvn test

Order Service:  
cd order-service  
mvn test

## Tech Stack

- Java 24
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 Database
- RestTemplate
- Lombok
- Swagger/OpenAPI (Springdoc)
- JUnit 5 and Mockito

## Inter-Service Communication Flow

1. Client calls POST /order on Order Service.
2. Order Service calls GET /inventory/{productId} on Inventory Service.
3. If inventory is available:
    - Order Service calls POST /inventory/update to deduct quantity.
4. Order Service saves the order.
5. Response is returned to client.

## Extensibility Notes

The Inventory Service uses a Factory Pattern that allows adding new strategies for handling inventory allocation. New handlers can be plugged in by implementing the InventoryHandler interface and registering a Spring bean.

## Additional Notes

- Both services use in-memory H2 databases, so data resets on restart.
- Services run independently on different ports.
- Useful for learning microservice architecture, REST communication, and layered service design.
