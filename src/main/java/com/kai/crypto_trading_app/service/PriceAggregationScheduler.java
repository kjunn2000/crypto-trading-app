package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.model.CryptoPair;
import com.kai.crypto_trading_app.repository.CryptoPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriceAggregationScheduler {

    private final CryptoPairRepository cryptoPairRepository;
    private final RestTemplate restTemplate;

    @Value("${binance.api.url}")
    private String binanceApiUrl;

    @Value("${huobi.api.url}")
    private String huobiApiUrl;

    private static final List<String> SUPPORTED_PAIRS = Arrays.asList("ETHUSDT", "BTCUSDT");

    private static final Logger logger = LoggerFactory.getLogger(PriceAggregationScheduler.class);

    @Autowired
    public PriceAggregationScheduler(CryptoPairRepository cryptoPairRepository) {
        this.cryptoPairRepository = cryptoPairRepository;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 10000)
    public void aggregatePrices() {
        List<Map<String, Object>> binancePrices = fetchBinancePrices();
        List<Map<String, Object>> huobiPrices = fetchHuobiPrices();

        // Process and store the best prices
        processAndStorePrices(binancePrices, huobiPrices);
    }

    private List<Map<String, Object>> fetchBinancePrices() {
        try {
            String urlWithParam = binanceApiUrl + "?symbols=[\"ETHUSDT\",\"BTCUSDT\"]";

            return restTemplate.getForObject(urlWithParam, List.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Error fetching Binance prices: HTTP Status {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            logger.error("Error fetching Binance prices: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error fetching Binance prices: {}", e.getMessage());
        }
        return List.of(); // Return an empty list in case of error
    }

    private List<Map<String, Object>> fetchHuobiPrices() {
        try {
            Map<String, Object> response = restTemplate.getForObject(huobiApiUrl, Map.class);
            return (List<Map<String, Object>>) response.get("data");
        } catch (HttpStatusCodeException e) {
            logger.error("Error fetching Huobi prices: HTTP Status {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            logger.error("Error fetching Huobi prices: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error fetching Huobi prices: {}", e.getMessage());
        }
        return List.of(); // Return an empty list in case of error
    }

    private void processAndStorePrices(List<Map<String, Object>> binancePrices, List<Map<String, Object>> huobiPrices) {
        Map<String, BigDecimal[]> bestPrices = new HashMap<>(); // Use an array to store bid and ask prices

        if (binancePrices != null && !binancePrices.isEmpty()) {
            extractBestPrices(binancePrices, bestPrices);
        } else {
            logger.warn("No Binance prices to process.");
        }

        if (huobiPrices != null && !huobiPrices.isEmpty()) {
            // Filter out records that don't have supported crypto pairs
            huobiPrices = huobiPrices.stream()
                    .filter(price -> SUPPORTED_PAIRS.contains(((String) price.get("symbol")).toUpperCase()))
                    .peek(price -> {
                        price.put("symbol", ((String) price.get("symbol")).toUpperCase());
                        price.put("bidPrice", price.get("bid"));
                        price.put("askPrice", price.get("ask"));
                    }).collect(Collectors.toList());
            extractBestPrices(huobiPrices, bestPrices);
        } else {
            logger.warn("No Huobi prices to process.");
        }

        // Store the best prices
        for (Map.Entry<String, BigDecimal[]> entry : bestPrices.entrySet()) {
            String symbol = entry.getKey();
            BigDecimal[] prices = entry.getValue();
            updateCryptoPairPrice(symbol, prices[0], prices[1]);
        }
    }

    private void extractBestPrices(List<Map<String, Object>> prices, Map<String, BigDecimal[]> bestPrices) {
        for (Map<String, Object> priceData : prices) {
            String symbol = (String) priceData.get("symbol");

            BigDecimal bidPrice = new BigDecimal(priceData.get("bidPrice").toString());
            BigDecimal askPrice = new BigDecimal(priceData.get("askPrice").toString());

            BigDecimal[] currentPrices = bestPrices.get(symbol);
            if (currentPrices == null) {
                bestPrices.put(symbol, new BigDecimal[]{bidPrice, askPrice});
            } else {
                BigDecimal bestBid = currentPrices[0].max(bidPrice);
                BigDecimal bestAsk = currentPrices[1].min(askPrice);
                bestPrices.put(symbol, new BigDecimal[]{bestBid, bestAsk});
            }
        }
    }

    private void updateCryptoPairPrice(String symbol, BigDecimal bidPrice, BigDecimal askPrice) {
        Optional<CryptoPair> cryptoPair = cryptoPairRepository.findByPairName(symbol);
        
        if (cryptoPair.isPresent()) {
            cryptoPair.get().setBidPrice(bidPrice);
            cryptoPair.get().setAskPrice(askPrice);
            cryptoPairRepository.save(cryptoPair.get());
            logger.info("Successfully updated crypto pair price for: {} - {} {}", symbol, bidPrice, askPrice);
        } else {
            logger.warn("Crypto pair not found: {}", symbol);
        }
    }
}