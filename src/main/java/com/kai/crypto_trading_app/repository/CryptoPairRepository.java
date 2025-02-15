package com.kai.crypto_trading_app.repository;

import com.kai.crypto_trading_app.model.CryptoPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoPairRepository extends JpaRepository<CryptoPair, Integer> {
}
