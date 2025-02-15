CREATE TABLE "USER" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    wallet_balance DECIMAL(19, 2) NOT NULL DEFAULT 50000.00
);

CREATE TABLE "CRYPTO_PAIR" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pair_name VARCHAR(255) UNIQUE NOT NULL,
    bid_price DECIMAL(19, 2) NOT NULL,
    ask_price DECIMAL(19, 2) NOT NULL
);

CREATE TABLE "USER_CRYPTO_BALANCE" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 8) NOT NULL
);

CREATE TABLE "TRANSACTION" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pair_id BIGINT NOT NULL,
    transaction_type VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 8) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

ALTER TABLE "USER_CRYPTO_BALANCE"
ADD CONSTRAINT "FK_user_crypto_balance_user" FOREIGN KEY (user_id) REFERENCES "USER"(id) ON DELETE CASCADE;

ALTER TABLE "TRANSACTION"
ADD CONSTRAINT "FK_transaction_user" FOREIGN KEY (user_id) REFERENCES "USER"(id);

ALTER TABLE "TRANSACTION"
ADD CONSTRAINT "FK_transaction_crypto_pair" FOREIGN KEY (pair_id) REFERENCES "CRYPTO_PAIR"(id);
