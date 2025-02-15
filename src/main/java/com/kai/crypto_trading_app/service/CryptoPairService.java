package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.model.CryptoPair;
import com.kai.crypto_trading_app.repository.CryptoPairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoPairService {

    private final CryptoPairRepository cryptoPairRepository;

    @Autowired
    public CryptoPairService(CryptoPairRepository cryptoPairRepository) {
        this.cryptoPairRepository = cryptoPairRepository;
    }

    public List<CryptoPair> getAllCryptoPairs() {
        return cryptoPairRepository.findAll();
    }
}