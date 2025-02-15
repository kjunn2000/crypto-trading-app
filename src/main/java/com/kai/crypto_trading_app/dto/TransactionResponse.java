package com.kai.crypto_trading_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private String pairName;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private LocalDateTime timestamp;
} 