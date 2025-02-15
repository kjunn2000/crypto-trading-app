package com.kai.crypto_trading_app.repository;

import com.kai.crypto_trading_app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Method to find transactions by user ID
    List<Transaction> findByUserId(Long userId);
}
