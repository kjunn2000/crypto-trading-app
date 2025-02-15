package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.dto.CryptoPairDTO;
import com.kai.crypto_trading_app.model.CryptoPair;
import com.kai.crypto_trading_app.repository.CryptoPairRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CryptoPairServiceTest {

    @Mock
    private CryptoPairRepository cryptoPairRepository;

    @InjectMocks
    private CryptoPairService cryptoPairService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCryptoPairs() {
        // Arrange
        CryptoPair pair1 = new CryptoPair(1L, "BTC/USD", new BigDecimal("50000.00"), new BigDecimal("50010.00"));
        CryptoPair pair2 = new CryptoPair(2L, "ETH/USD", new BigDecimal("4000.00"), new BigDecimal("4010.00"));
        when(cryptoPairRepository.findAll()).thenReturn(Arrays.asList(pair1, pair2));

        // Act
        List<CryptoPairDTO> result = cryptoPairService.getAllCryptoPairs();

        // Assert
        assertEquals(2, result.size());
        assertEquals("BTC/USD", result.get(0).getPairName());
        assertEquals(new BigDecimal("50000.00"), result.get(0).getBidPrice());
        assertEquals(new BigDecimal("50010.00"), result.get(0).getAskPrice());
        assertEquals("ETH/USD", result.get(1).getPairName());
        assertEquals(new BigDecimal("4000.00"), result.get(1).getBidPrice());
        assertEquals(new BigDecimal("4010.00"), result.get(1).getAskPrice());
    }
}