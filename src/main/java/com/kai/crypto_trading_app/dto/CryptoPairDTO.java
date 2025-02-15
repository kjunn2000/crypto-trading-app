package com.kai.crypto_trading_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoPairDTO {
    private String pairName;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
} 