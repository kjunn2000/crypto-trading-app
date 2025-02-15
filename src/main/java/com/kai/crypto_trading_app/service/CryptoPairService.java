package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.dto.CryptoPairDTO;
import com.kai.crypto_trading_app.repository.CryptoPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CryptoPairService {

    private final CryptoPairRepository cryptoPairRepository;

    @Autowired
    public CryptoPairService(CryptoPairRepository cryptoPairRepository) {
        this.cryptoPairRepository = cryptoPairRepository;
    }

    public List<CryptoPairDTO> getAllCryptoPairs() {
        return cryptoPairRepository.findAll().stream()
                .map(pair -> new CryptoPairDTO(pair.getPairName(), pair.getBidPrice(), pair.getAskPrice()))
                .collect(Collectors.toList());
    }
}