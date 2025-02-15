CREATE TABLE "app_user" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    wallet_balance DECIMAL(19, 2) NOT NULL DEFAULT 50000.00
);

CREATE TABLE "crypto_pair" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pair_name VARCHAR(255) UNIQUE NOT NULL,
    bid_price DECIMAL(19, 2) NOT NULL,
    ask_price DECIMAL(19, 2) NOT NULL
);

CREATE TABLE "user_crypto_balance" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 8) NOT NULL
);

CREATE TABLE "transaction" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pair_id BIGINT NOT NULL,
    transaction_type VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 8) NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    total_price DECIMAL(19, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

ALTER TABLE "user_crypto_balance"
ADD CONSTRAINT "FK_user_crypto_balance_user" FOREIGN KEY (user_id) REFERENCES "app_user"(id) ON DELETE CASCADE;

ALTER TABLE "transaction"
ADD CONSTRAINT "FK_transaction_user" FOREIGN KEY (user_id) REFERENCES "app_user"(id);

ALTER TABLE "transaction"
ADD CONSTRAINT "FK_transaction_crypto_pair" FOREIGN KEY (pair_id) REFERENCES "crypto_pair"(id);
