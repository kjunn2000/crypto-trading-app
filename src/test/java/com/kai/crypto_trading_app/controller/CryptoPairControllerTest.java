package com.kai.crypto_trading_app.controller;

import com.kai.crypto_trading_app.dto.CryptoPairDTO;
import com.kai.crypto_trading_app.service.CryptoPairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CryptoPairControllerTest {

    @Mock
    private CryptoPairService cryptoPairService;

    @InjectMocks
    private CryptoPairController cryptoPairController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCryptoPairs() {
        // Arrange
        CryptoPairDTO pair1 = new CryptoPairDTO("BTC/USD", BigDecimal.valueOf(10), BigDecimal.valueOf(10));
        CryptoPairDTO pair2 = new CryptoPairDTO("ETH/USD", BigDecimal.valueOf(10), BigDecimal.valueOf(10));
        List<CryptoPairDTO> expectedPairs = Arrays.asList(pair1, pair2);

        when(cryptoPairService.getAllCryptoPairs()).thenReturn(expectedPairs);

        // Act
        ResponseEntity<List<CryptoPairDTO>> response = cryptoPairController.getAllCryptoPairs();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedPairs, response.getBody());
    }
}