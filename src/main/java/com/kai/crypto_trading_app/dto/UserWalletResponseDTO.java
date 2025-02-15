package com.kai.crypto_trading_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class UserWalletResponseDTO {
    private Long userId;
    private BigDecimal walletBalance;
    private List<UserCryptoBalanceDTO> currencies;
} 