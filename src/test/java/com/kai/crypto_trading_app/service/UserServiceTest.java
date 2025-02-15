package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.dto.UserCryptoBalanceDTO;
import com.kai.crypto_trading_app.dto.UserWalletResponseDTO;
import com.kai.crypto_trading_app.model.User;
import com.kai.crypto_trading_app.model.UserCryptoBalance;
import com.kai.crypto_trading_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserCryptoBalance cryptoBalance;

    @BeforeEach
    void setUp() {
        cryptoBalance = new UserCryptoBalance();
        cryptoBalance.setCurrency("BTC");
        cryptoBalance.setAmount(BigDecimal.valueOf(0.5));

        user = new User();
        user.setId(1L);
        user.setWalletBalance(BigDecimal.valueOf(1000));
        user.setCryptoBalances(Set.of(cryptoBalance));
    }

    @Test
    void testGetUserWalletDetails() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<UserWalletResponseDTO> result = userService.getUserWalletDetails(1L);

        // Assert
        assertTrue(result.isPresent());
        UserWalletResponseDTO responseDTO = result.get();
        assertEquals(1L, responseDTO.getUserId());
        assertEquals(BigDecimal.valueOf(1000), responseDTO.getWalletBalance());
        assertEquals(1, responseDTO.getCurrencies().size());

        UserCryptoBalanceDTO balanceDTO = responseDTO.getCurrencies().get(0);
        assertEquals("BTC", balanceDTO.getCurrency());
        assertEquals(0.5, balanceDTO.getAmount());
    }
}