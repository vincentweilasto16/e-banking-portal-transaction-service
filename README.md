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
  POST /api/v1/auth/generate-token?userId={userId}
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
   1. Start All Services Using Docker Compose
      - All required services (Spring Boot app, PostgreSQL, Kafka, Zookeeper) are defined in docker-compose.yml. Start them with:
        ```bash
        docker-compose up --build -d
        ```
      
        This will create and start containers for:
        - zookeeper
        - kafka
        - postgres
        - transaction-service (Spring Boot application)

      - Ensure all service running by:
        ```bash
        docker ps
        ```

  2. Create kafka topic 'transactions'
     Once Kafka is running, create the **transactions** topic:
        ```bash
        docker exec -it kafka kafka-topics --create --topic transactions --bootstrap-server kafka:9092 --partitions 1 --replication-factor 1
        ```
     **Note:** Use kafka:9092 (container name) as the bootstrap server inside Docker network.

  3. Check PostgreSQL
     PostgreSQL is running inside Docker with the database e_banking_db and credentials defined in docker-compose.yml:
     - Database: e_banking_db
     - Username: postgres
     - Password: postgres
     - Port: 5432
     
     Access the database using DBeaver or Docker CLI:
     ```bash
     docker exec -it <postgres_container_name> psql -U postgres -d e_banking_db
     ```

      Database configuration can be found in application.properties:
      ```bash
      spring.datasource.url=jdbc:postgresql://localhost:5432/e_banking_db
      spring.datasource.username=postgres
      spring.datasource.password=postgres
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      ```

 4. Produce Transaction Messages To Kafka
    Use Kafka console producer inside Docker:
    ```bash
    docker exec -it kafka kafka-console-producer --topic transactions --bootstrap-server kafka:9092
    ```

    Then input the message and press enter, example:
    ```bash
    {"id":"123e4567-e89b-12d3-a456-426614174000","amount":100.00,"currency":"GBP","iban":"GB00TEST1234567890","valueDate":"2023-08-01","description":"Salary payment","user_id":"P-0123456789"}
     ```

 5. Generate JWT Token
    Hit Swagger endpoint to generate a token:
    ```bash
    /api/v1/auth/generate-token?userId=P-0123456789
    ```

 7. Fetch Transactions
    Use Swagger /api/v1/transactions with Bearer token.
    - Set query parameters: month, year, page, size.
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

4. **Refactoring and Code Structure**
   - The code can be further refactored to improve readability, maintainability, and modularity.
   - Some parts of the code could be better structured, e.g., separating service logic, controller logic, and repository layers more clearly.
   - Applying consistent naming conventions and removing redundant code would enhance overall code quality.


---

**Note:** These limitations highlight areas that could be improved in future iterations.




