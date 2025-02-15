package com.kai.crypto_trading_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionHistoryResponse {
    private Long userId;
    private List<TransactionResponse> transactions;
} 