package com.kai.crypto_trading_app.controller;

import com.kai.crypto_trading_app.dto.TradeRequestDTO;
import com.kai.crypto_trading_app.dto.TradeResponseDTO;
import com.kai.crypto_trading_app.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade")
public class TradeController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TradeResponseDTO> executeTrade(@Valid @RequestBody TradeRequestDTO tradeRequest) {
        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);
        return ResponseEntity.ok(response);
    }
} 