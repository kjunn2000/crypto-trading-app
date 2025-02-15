package com.kai.crypto_trading_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCryptoBalanceDTO {
    private String currency;
    private double amount;
} 