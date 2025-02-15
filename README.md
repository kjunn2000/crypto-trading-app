# Crypto Trading System

## Assumptions

1. Users are already authenticated and authorized to access the APIs.
2. Users have an initial wallet balance of 50,000 USDT in the database.
3. Only Ethereum (ETHUSDT) and Bitcoin (BTCUSDT) trading pairs are supported.
4. The system stores the best bid and ask prices in 2 decimal places.

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
     {
       "prices": [
         {
           "pair_name": "ETHUSDT",
           "bid_price": 1800.50,
           "ask_price": 1801.00,
           "timestamp": "2023-10-01T12:00:00Z"
         },
         {
           "pair_name": "BTCUSDT",
           "bid_price": 30000.00,
           "ask_price": 30010.00,
           "timestamp": "2023-10-01T12:00:00Z"
         }
       ]
     }
     ```

2. **POST /api/trade**
   - Execute a trade based on the latest best aggregated price.
   - **Request:**
     ```json
     {
       "userId": 1,
       "pairName": "ETHUSDT",
       "transactionType": "BUY",
       "amount": 0.5
     }
     ```
   - **Response:**
     ```json
     {
       "transaction_id": 123,
       "status": "success",
       "message": "Trade executed successfully."
     }
     ```

3. **GET /api/wallet/{userId}**
   - Retrieve the user's crypto currencies wallet balance.
   - **Response:**
     ```json
     {
       "user_id": 1,
       "wallet_balance": 49500.00,
       "currencies": [
         {
           "currency": "ETH",
           "amount": 1.5
         },
         {
           "currency": "BTC",
           "amount": 0.2
         }
       ]
     }
     ```

4. **GET /api/transactions/{userId}**
   - Retrieve the user's trading history.
   - **Response:**
     ```json
     {
       "user_id": 1,
       "transactions": [
         {
           "transaction_id": 123,
           "pair_name": "ETHUSDT",
           "transaction_type": "BUY",
           "amount": 0.5,
           "price": 1800.50,
           "timestamp": "2023-10-01T12:00:00Z"
         },
         {
           "transaction_id": 124,
           "pair_name": "BTCUSDT",
           "transaction_type": "SELL",
           "amount": 0.1,
           "price": 30000.00,
           "timestamp": "2023-10-01T12:05:00Z"
         }
       ]
     }
     ```
     

## Scheduler Logic

1. **Price Aggregation Scheduler**
   - Runs every 10 seconds using Spring's `@Scheduled` annotation
   - Fetches latest prices from Binance and Huobi APIs for configured trading pairs
   (https://huobiapi.github.io/docs/spot/v1/en/#get-latest-tickers-for-all-pairs, https://developers.binance.com/docs/binance-spot-api-docs/testnet/rest-api/market-data-endpoints#symbol-order-book-ticker)
   - Stores the best prices in `aggregated_prices` table
   - Best ask price is used for SELL orders (lower price)
   - Best bid price is used for BUY orders (higher price)



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
   - Open in browser to view detailed coverage metrics
   
