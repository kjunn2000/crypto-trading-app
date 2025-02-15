package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.dto.TransactionHistoryResponse;
import com.kai.crypto_trading_app.dto.TransactionResponse;
import com.kai.crypto_trading_app.model.Transaction;
import com.kai.crypto_trading_app.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction executeTrade(Transaction transaction) {
        // Additional logic for executing trade can be added here
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public TransactionHistoryResponse getUserTransactionHistory(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        List<TransactionResponse> transactionResponses = transactions.stream()
            .map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getPair().getPairName(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getPrice(),
                transaction.getTimestamp()
            ))
            .collect(Collectors.toList());

        return new TransactionHistoryResponse(userId, transactionResponses);
    }
} 