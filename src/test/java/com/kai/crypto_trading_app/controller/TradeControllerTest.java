package com.kai.crypto_trading_app.controller;

import com.kai.crypto_trading_app.dto.TradeRequestDTO;
import com.kai.crypto_trading_app.dto.TradeResponseDTO;
import com.kai.crypto_trading_app.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TradeControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TradeController tradeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteTrade() {
        // Arrange
        TradeRequestDTO tradeRequest = TradeRequestDTO.builder()
                .userId(1L)
                .pairName("BTC/USD")
                .transactionType("BUY")
                .amount(BigDecimal.valueOf(100.0))
                .build();

        TradeResponseDTO expectedResponse = TradeResponseDTO.builder()
                .transactionId(12345L)
                .status("SUCCESS")
                .message("Trade executed successfully")
                .build();

        when(transactionService.executeTrade(tradeRequest)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<TradeResponseDTO> response = tradeController.executeTrade(tradeRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
    }
}