package com.kai.crypto_trading_app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TradeRequestDTO {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Pair name cannot be blank")
    private String pairName;

    @NotBlank(message = "Transaction type cannot be blank")
    @Pattern(regexp = "BUY|SELL", message = "Transaction type must be either BUY or SELL")
    private String transactionType;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;
} 