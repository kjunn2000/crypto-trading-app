package com.kai.crypto_trading_app.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeResponseDTO {
    
    private Long transactionId;
    private String status;
    private String message;
} 