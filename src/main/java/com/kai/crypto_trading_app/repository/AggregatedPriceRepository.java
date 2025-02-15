package com.kai.crypto_trading_app.repository;

import com.kai.crypto_trading_app.model.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Integer> {
}
