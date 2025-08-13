# e-Banking Transaction Service

This is a **Spring Boot microservice** for handling e-Banking transactions.  
It provides a REST API for returning **paginated transaction lists** for a given user, with total credit and debit values calculated per page.  
Transactions are consumed from a Kafka topic and stored in a PostgreSQL database.

---

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

---

## Architecture Diagram
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


---

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


---

## API Endpoints

- Generate JWT Token (this api created for easily generate the bearer token for spesific user id)
  *POST /api/v1/auth/generate-token?userId={userId}*

  Query Parameter:
  - userId (string, required)

  Response Example:
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
  }


- Get Transactions By User ID
  *POST /api/v1/transactions?userId={userId}*

  Headers:
  Authorization: Bearer <token>

  Query Parameters:
  - year (integer, required)
  - month (integer, required)
  - page (integer, optional)
  - size (integer, optional)

  Response Example:
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


---

## How To Run The Application



---

## Technology Used
- Java 17 + Spring Boot
- Spring Kafka, Spring Security
- PostgreSQL
- Docker & Docker Compose
- Swagger/OpenAPI
- JWT for authentication
- Maven for build management



