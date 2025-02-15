# Crypto Trading System

## Assumptions

1. Users are already authenticated and authorized to access the APIs.
2. Users have an initial wallet balance of 50,000 USDT in the database.
3. Only Ethereum (ETHUSDT) and Bitcoin (BTCUSDT) trading pairs are supported.
4. The system stores the currency values in 2 decimal places.

## Database Table Structure

### Users
- `id` (Primary Key, BigInt, Auto-generated)
- `username` (String, Unique, Not Null)
- `wallet_balance` (Decimal, default: 50000.00, Not Null)

### CryptoPairs
- `id` (Primary Key, BigInt, Auto-generated)
- `pair_name` (String, Unique, Not Null)
- `bid_price` (Decimal, Not Null)
- `ask_price` (Decimal, Not Null)

### UserCryptoBalance
- `id` (Primary Key, BigInt, Auto-generated)
- `user_id` (Foreign Key, references Users, Not Null)
- `currency` (String, Not Null)
- `amount` (Decimal, Not Null)

### Transactions
- `id` (Primary Key, BigInt, Auto-generated)
- `user_id` (Foreign Key, references Users, Not Null)
- `pair_id` (Foreign Key, references CryptoPairs, Not Null)
- `transaction_type` (String, Not Null)
- `amount` (Decimal, Not Null)
- `unit_price` (Decimal, Not Null)
- `total_price` (Decimal, Not Null)
- `timestamp` (Timestamp, Not Null)

## API Design

### Endpoints

1. **GET /api/prices**
   - Retrieve the latest best aggregated price for each trading pair.
   - **Response:**
     ```json
      [
          {
              "pairName": "ETHUSDT",
              "bidPrice": 2690.97,
              "askPrice": 2690.56
          },
          {
              "pairName": "BTCUSDT",
              "bidPrice": 97563.18,
              "askPrice": 97560.79
          }
      ]
     ```
   - **Demo :**
     <img width="1081" alt="image" src="https://github.com/user-attachments/assets/a95a0c4f-76b8-46f7-9e6f-d541c7a6e8a2" />


2. **POST /api/trade**
   - Execute a trade based on the latest best aggregated price.
   - Cater for edge cases (User not found, pair name not found, balance not enough)
   - **Request:**
     ```json
      {
          "userId": 1,
          "pairName": "ETHUSDT",
          "transactionType": "BUY",
          "amount": 3
      }
     ```
   - **Response:**
     ```json
      {
          "transactionId": 1,
          "status": "success",
          "message": "Trade executed successfully."
      }
      {
          "status": "failure",
          "message": "Crypto pair not found."
      }
      {
          "status": "failure",
          "message": "User not found."
      }
      {
          "status": "failure",
          "message": "Invalid transaction type or insufficient balance."
      }
     ```
   - **Demo:**
     <img width="1095" alt="image" src="https://github.com/user-attachments/assets/d98ab0bc-2257-486d-92c7-f48486e1fdbd" />

3. **GET /api/wallet/{userId}**
   - Retrieve the user's crypto currencies wallet balance.
   - Return 404 if user id not found.
   - **Response:**
     ```json
      {
          "userId": 1,
          "walletBalance": 44363.51,
          "currencies": [
              {
                  "currency": "ETHUSDT",
                  "amount": 3.0
              },
              {
                  "currency": "BTCUSDT",
                  "amount": 1.0
              }
          ]
      }
     ```
   - **Demo :**
     <img width="1081" alt="image" src="https://github.com/user-attachments/assets/2a1a0aad-6b9c-46ca-bb1a-4f44bf92270e" />


4. **GET /api/transactions/{userId}**
   - Retrieve the user's trading history and sorted the transaction by timestamp.
   - Return 404 if user id not found.
   - **Response:**
     ```json
      {
          "userId": 1,
          "transactions": [
              {
                  "transactionId": 2,
                  "pairName": "BTCUSDT",
                  "transactionType": "BUY",
                  "amount": 1.00000000,
                  "unitPrice": 97560.79,
                  "totalPrice": 97560.79,
                  "timestamp": "2025-02-16T01:14:03.838069"
              },
              {
                  "transactionId": 1,
                  "pairName": "ETHUSDT",
                  "transactionType": "BUY",
                  "amount": 3.00000000,
                  "unitPrice": 2691.90,
                  "totalPrice": 8075.70,
                  "timestamp": "2025-02-16T01:07:29.256994"
              }
          ]
      }
     ```
   - **Demo: **
      <img width="1081" alt="image" src="https://github.com/user-attachments/assets/47bd6976-dfbd-49ba-8db2-48924ab360ac" />


     

## Scheduler Logic

1. **Price Aggregation Scheduler**
   - Runs every 10 seconds using Spring's `@Scheduled` annotation
   - Fetches latest prices from Binance and Huobi APIs for configured trading pairs
   (https://huobiapi.github.io/docs/spot/v1/en/#get-latest-tickers-for-all-pairs, https://developers.binance.com/docs/binance-spot-api-docs/testnet/rest-api/market-data-endpoints#symbol-order-book-ticker)
   - Stores the best prices in `aggregated_prices` table
   - Best ask price is used for SELL orders (lower price)
   - Best bid price is used for BUY orders (higher price)
   - **Demo: **
     <img width="1066" alt="image" src="https://github.com/user-attachments/assets/953020b6-a82f-4696-b16e-b0192b707386" />



## Running Unit Tests

1. **Execute Tests**
   - Run all unit tests and generate coverage report:
     ```bash
     ./gradlew test jacocoTestReport
     ```

2. **View Test Coverage Report**
   - After running tests, coverage report will be generated at:
     ```
     build/reports/jacoco/test/html/index.html
     ```
   - Open in browser to view detailed coverage metrics （84% for service classes and 100% for controller classes)
      <img width="1178" alt="image" src="https://github.com/user-attachments/assets/31253f31-78e4-4b10-b46c-568890f24fb7" />


