package com.kai.crypto_trading_app.controller;

import com.kai.crypto_trading_app.dto.UserWalletResponseDTO;
import com.kai.crypto_trading_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/wallet/{userId}")
    public ResponseEntity<?> getUserWalletBalance(@PathVariable Long userId) {
        Optional<UserWalletResponseDTO> userWalletResponse = userService.getUserWalletDetails(userId);
        return userWalletResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
} 