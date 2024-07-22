# Mpesa B2C Integration

This project is a Spring Boot application that integrates with the Mpesa B2C API. 
It includes functionalities to process B2C transactions, check payment statuses, and update records in MongoDB. It also integrates with Kafka for message handling.

## Features

- Receive B2C requests and process them.
- Fetch and update payment statuses.
- Store transaction details in MongoDB.
- Kafka producer to send B2C requests.
- Kafka consumer to process responses.
- Custom error handling.

## Technologies Used

- Java
- Spring Boot
- Kafka
- MongoDB
- H2 Database (for testing)
- OkHttp (for making HTTP requests)
- JSON (for request/response serialization)

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- MongoDB
- Kafka

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/rabiot125/mpesa-b2c-integration.git

cd mpesa-b2c-integration 

mvn clean install
mvn spring-boot:run


### Access Swagger Ui 
`http://localhost:8080/swagger-ui/.`