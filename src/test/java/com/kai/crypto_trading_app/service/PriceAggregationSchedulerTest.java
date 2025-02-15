package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.model.CryptoPair;
import com.kai.crypto_trading_app.repository.CryptoPairRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PriceAggregationSchedulerTest {

    @Mock
    private CryptoPairRepository cryptoPairRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PriceAggregationScheduler priceAggregationScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessAndStorePrices() {
        // Mock Binance API response
        List<Map<String, Object>> binancePrices = new ArrayList<>(List.of(
            new HashMap<>(Map.of("symbol", "ETHUSDT", "bidPrice", "2000.00", "askPrice", "2100.00")),
            new HashMap<>(Map.of("symbol", "BTCUSDT", "bidPrice", "30000.00", "askPrice", "31000.00"))
        ));

        // Mock Huobi API response
        List<Map<String, Object>> huobiPrices = new ArrayList<>(List.of(
            new HashMap<>(Map.of("symbol", "ethusdt", "bid", "1990.00", "ask", "2090.00")),
            new HashMap<>(Map.of("symbol", "btcusdt", "bid", "29900.00", "ask", "30900.00"))
        ));

        // Mock repository behavior
        CryptoPair ethPair = new CryptoPair(1L, "ETHUSDT", BigDecimal.ZERO, BigDecimal.ZERO);
        CryptoPair btcPair = new CryptoPair(2L, "BTCUSDT", BigDecimal.ZERO, BigDecimal.ZERO);

        when(cryptoPairRepository.findByPairName("ETHUSDT")).thenReturn(Optional.of(ethPair));
        when(cryptoPairRepository.findByPairName("BTCUSDT")).thenReturn(Optional.of(btcPair));

        // Directly call the processAndStorePrices method
        priceAggregationScheduler.processAndStorePrices(binancePrices, huobiPrices);

        // Capture and verify the updates to the repository
        ArgumentCaptor<CryptoPair> captor = ArgumentCaptor.forClass(CryptoPair.class);
        verify(cryptoPairRepository, times(2)).save(captor.capture());

        List<CryptoPair> savedPairs = captor.getAllValues();
        assertEquals(new BigDecimal("2000.00"), savedPairs.get(0).getBidPrice());
        assertEquals(new BigDecimal("2090.00"), savedPairs.get(0).getAskPrice());
        assertEquals(new BigDecimal("30000.00"), savedPairs.get(1).getBidPrice());
        assertEquals(new BigDecimal("30900.00"), savedPairs.get(1).getAskPrice());
    }
}