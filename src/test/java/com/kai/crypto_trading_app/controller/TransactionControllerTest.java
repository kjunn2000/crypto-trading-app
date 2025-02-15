package com.kai.crypto_trading_app.controller;

import com.kai.crypto_trading_app.dto.TransactionHistoryResponse;
import com.kai.crypto_trading_app.dto.TransactionResponse;
import com.kai.crypto_trading_app.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionHistory_Found() {
        // Arrange
        Long userId = 1L;
        List<TransactionResponse> transactions = List.of(
                new TransactionResponse(123L, "BTC/USD", "BUY", BigDecimal.valueOf(1.0),
                        BigDecimal.valueOf(50000.0), BigDecimal.valueOf(50000.0), LocalDateTime.now())
        );
        TransactionHistoryResponse transactionHistoryResponse = new TransactionHistoryResponse(userId, transactions);

        when(transactionService.getUserTransactionHistory(userId)).thenReturn(Optional.of(transactionHistoryResponse));

        // Act
        ResponseEntity<?> response = transactionController.getTransactionHistory(userId);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(transactionHistoryResponse, response.getBody());
    }

    @Test
    void testGetTransactionHistory_NotFound() {
        // Arrange
        Long userId = 2L;

        when(transactionService.getUserTransactionHistory(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = transactionController.getTransactionHistory(userId);

        // Assert
        assertEquals(404, response.getStatusCode().value());
    }
}