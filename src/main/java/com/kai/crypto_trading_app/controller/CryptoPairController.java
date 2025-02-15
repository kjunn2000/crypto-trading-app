package com.kai.crypto_trading_app.controller;

import com.kai.crypto_trading_app.dto.CryptoPairDTO;
import com.kai.crypto_trading_app.service.CryptoPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/crypto-pairs")
public class CryptoPairController {

    private final CryptoPairService cryptoPairService;

    @Autowired
    public CryptoPairController(CryptoPairService cryptoPairService) {
        this.cryptoPairService = cryptoPairService;
    }

    @GetMapping
    public ResponseEntity<List<CryptoPairDTO>> getAllCryptoPairs() {
        List<CryptoPairDTO> cryptoPairs = cryptoPairService.getAllCryptoPairs();
        return ResponseEntity.ok(cryptoPairs);
    }
} 