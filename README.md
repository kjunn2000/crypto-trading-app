# Crypto Trading System

## Assumptions

1. Users are already authenticated and authorized to access the APIs.
2. Users have an initial wallet balance of 50,000 USDT in the database.
3. Only Ethereum (ETHUSDT) and Bitcoin (BTCUSDT) trading pairs are supported.
4. The system stores the best bid and ask prices in 2 decimal places.

## Database Table Structure

### Users
- `id` (Primary Key, Integer)
- `username` (String, Unique)
- `wallet_balance` (Decimal, default: 50000.00)

### CryptoPairs
- `id` (Primary Key, Integer)
- `pair_name` (String, Unique, e.g., "ETHUSDT", "BTCUSDT")

### Transactions
- `id` (Primary Key, Integer)
- `user_id` (Foreign Key, references Users)
- `pair_id` (Foreign Key, references CryptoPairs)
- `transaction_type` (String, e.g., "BUY", "SELL")
- `amount` (Decimal)
- `price` (Decimal)
- `timestamp` (Timestamp)

### AggregatedPrices
- `id` (Primary Key, Integer)
- `pair_id` (Foreign Key, references CryptoPairs)
- `bid_price` (Decimal)
- `ask_price` (Decimal)
- `timestamp` (Timestamp)

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
       "user_id": 1,
       "pair_name": "ETHUSDT",
       "transaction_type": "BUY",
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
   - Best bid price is used for SELL orders
   - Best ask price is used for BUY orders