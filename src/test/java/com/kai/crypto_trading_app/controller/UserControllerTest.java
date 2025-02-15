package com.kai.crypto_trading_app.controller;

import com.kai.crypto_trading_app.dto.UserWalletResponseDTO;
import com.kai.crypto_trading_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserWalletBalance_Found() {
        // Arrange
        Long userId = 1L;
        UserWalletResponseDTO userWalletResponse = new UserWalletResponseDTO(
                userId, 
                BigDecimal.valueOf(1000.0), 
                List.of()
        );

        when(userService.getUserWalletDetails(userId)).thenReturn(Optional.of(userWalletResponse));

        // Act
        ResponseEntity<?> response = userController.getUserWalletBalance(userId);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(userWalletResponse, response.getBody());
    }

    @Test
    void testGetUserWalletBalance_NotFound() {
        // Arrange
        Long userId = 2L;

        when(userService.getUserWalletDetails(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = userController.getUserWalletBalance(userId);

        // Assert
        assertEquals(404, response.getStatusCode().value());
    }
}