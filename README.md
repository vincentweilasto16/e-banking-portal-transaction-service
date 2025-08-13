# e-Banking Transaction Service

This is a **Spring Boot microservice** for handling e-Banking transactions.  
It provides a REST API for returning **paginated transaction lists** for a given user, with total credit and debit values calculated per page.  
Transactions are consumed from a Kafka topic and stored in a PostgreSQL database.


## Assumptions

- Every e-banking client may have **one or more accounts** in different currencies (GBP, EUR, CHF, etc.).
- Approximately **100,000 e-banking customers**, each with thousands of transactions per month.
- Transactions cover the last **10 years** and are consumed from **Kafka**, with the key as transaction ID and the value as JSON.
- The API client must be **authenticated**; the JWT token contains the unique `userId` (e.g., `P-0123456789`).
- Exchange rates are provided by an external API.
- Transaction structure includes:

| Field       | Type    | Description                 |
|------------|---------|-----------------------------|
| id         | UUID    | Unique transaction ID       |
| amount     | numeric | Transaction amount          |
| currency   | string  | Currency (e.g., GBP, CHF)  |
| description| string  | Transaction description     |
| iban       | string  | Account IBAN                |
| user_id    | string  | Owner user ID               |
| value_date | date    | Transaction date            |


## Architecture Diagram
```
+----------------+          +------------------+          +-----------------+
|  API Client    |  JWT     |  Spring Boot     | Kafka    |  PostgreSQL     |
| (Swagger/Postman) |------>|  Transaction API |<-------->|  transactions  |
+----------------+          | - REST endpoints |          +-----------------+
                            | - Kafka Consumer|
                            | - Service Layer |
                            | - Exchange Rate |
                            +----------------+
                                    ^
                                    |
                                    | External API
                                    v
                             +----------------+
                             | Exchange Rates |
                             +----------------+
```


## Process Flow:
1. Kafka Topic Setup
   - Create a Kafka topic called transactions inside Docker.

2. Run Spring Boot Application
   - Start the Spring Boot application.
   - The service will start consuming messages from the Kafka topic.

3. Produce Transactions to Kafka
   - Use Docker container or any Kafka client to produce transaction messages to the topic transactions.
   - Messages are JSON formatted with transaction details.

4. Persist Transactions
   - Once consumed, transactions are saved in PostgreSQL in the transactions table with fields: id, amount, currency, description, iban, user_id, and value_date

5. JWT Token Generation
   - Hit the API endpoint (/api/v1/auth/generate-token) to generate JWT token by providing a userId as query param on swagger ui.
   - The response will contain a JWT token.

6. Access Transactions API
   - Use Swagger or Postman to hit the GET /transactions endpoint (/api/v1/transactions).
   - Provide Bearer token for authentication.
   - Include query params:
     - userId (optional if token contains it)
     - month, year
     - page, size for pagination.
   - Response will return paginated transactions for the logged-in user with total credits and debits.



## API Endpoints

- Generate JWT Token (this api created for easily generate the bearer token for spesific user id)

  Endpoint:
   ```bash
  POST /api/v1/auth/generate-token?userId={userId}*
   ```

  Query Parameter:
  - userId (string, required)

  Response Example:
  ```bash
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
  }
  ```


- Get Transactions By User ID

  Endpoint:
  ```bash
  POST /api/v1/transactions?userId={userId}
  ```

  Headers:
  Authorization: Bearer <token>

  Query Parameters:
  - year (integer, required)
  - month (integer, required)
  - page (integer, optional)
  - size (integer, optional)

  Response Example:
  ```bash
  {
    "data": {
      "transactions": [
        {
          "id": "d7a2e59e-cd13-4b26-b5c0-d19a6c6df303",
          "amount": 200,
          "currency": "EUR",
          "iban": "DE00TEST1234567890",
          "valueDate": [
            2023,
            9,
            10
          ],
          "description": "Freelance payment",
          "user_id": "P-9876543210"
        }
      ],
      "total_credit": 0,
      "total_debit": 0
    },
    "page_number": 0,
    "page_size": 10,
    "total_elements": 1,
    "total_pages": 0
  }
  ```



## How To Run The Application
   1. Start Docker Containers
      ```bash
      docker-compose up -d
      ```

      - Ensure Kafka and Zookeper are running
      - Create kafka topic 'transactions'
        ```bash
        docker exec -it kafka kafka-topics --create --topic transactions --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
        ```

  2. Start PostgreSQL
     Before running the Spring Boot application, make sure PostgreSQL is running. You can start it **using Docker** or run       it **locally**.

      Example using Docker:
      ```bash
      docker run --name postgres-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=e_banking_db -p 5432:5432 -d postgres:15
      ```

      Database configuration can be found in application.properties:
      ```bash
      spring.datasource.url=jdbc:postgresql://localhost:5432/e_banking_db
      spring.datasource.username=postgres
      spring.datasource.password=postgres
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      ```
      
 3. Run Spring Boot Application
    ```bash
    mvn spring-boot:run
    ```

 4. Produce Transaction Messages
    Use Kafka client or Docker container to produce JSON messages to transactions topic.

    Example on docker container:
    ```bash
    docker exec -it kafka kafka-console-producer --topic transactions --bootstrap-server localhost:9092
    ```

    Then input the message and press enter, example:
    ```bash
    {"id":"123e4567-e89b-12d3-a456-426614174000","amount":100.00,"currency":"GBP","iban":"GB00TEST1234567890","valueDate":"2023-08-01","description":"Salary payment","user_id":"P-0123456789"}
     ```

 5. Generate JWT Token
    Hit Swagger endpoint /api/v1/auth/generate-token?userId=P-0123456789 to get JWT token.

 6. Fetch Transactions
    - Use Swagger /api/v1/transactions with Bearer token.
    - Set query params for month, year, page, size.
    - Response returns paginated transactions for the logged-in user.
     


## Technology Used
- Java 17 + Spring Boot
- Spring Kafka, Spring Security
- PostgreSQL
- Docker & Docker Compose
- Swagger/OpenAPI
- JWT for authentication
- Maven for build management


## Scope / Limitations

The current implementation of the e-Banking Transaction Service has some limitations and areas for future enhancement:

1. **Logging and Monitoring**  
   - Basic logging is implemented, but monitoring can be enhanced.  
   - Integration with tools like **Prometheus** and **Grafana** is not implemented yet.

2. **Exchange Rate Service**  
   - The exchange rate service is not implemented.  
   - As a result, the **total debit and credit values** returned by the API are always `0`.

3. **Unit and Integration Tests**  
   - Basic unit tests are included.  
   - Test coverage is **not comprehensive** and may not cover all project features and edge cases.

4. **Docker & Kubernetes / OpenShift Deployment**  
   - Building a Docker image and Kubernetes/OpenShift deployment configuration is **not included** in this repository.

5. **Continuous Integration (CI)**  
   - Integration with services like **CircleCI** or GitHub Actions is **not configured**.  
   - There is no automated pipeline to run unit or integration tests on commits.

---

**Note:** These limitations highlight areas that could be improved in future iterations.




