package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.dto.TradeRequestDTO;
import com.kai.crypto_trading_app.dto.TradeResponseDTO;
import com.kai.crypto_trading_app.dto.TransactionHistoryResponse;
import com.kai.crypto_trading_app.model.CryptoPair;
import com.kai.crypto_trading_app.model.Transaction;
import com.kai.crypto_trading_app.model.User;
import com.kai.crypto_trading_app.model.UserCryptoBalance;
import com.kai.crypto_trading_app.repository.CryptoPairRepository;
import com.kai.crypto_trading_app.repository.TransactionRepository;
import com.kai.crypto_trading_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CryptoPairRepository cryptoPairRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private CryptoPair cryptoPair;
    private TradeRequestDTO tradeRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setWalletBalance(BigDecimal.valueOf(1000));
        user.setCryptoBalances(new HashSet<>());

        cryptoPair = new CryptoPair();
        cryptoPair.setPairName("BTC/USD");
        cryptoPair.setAskPrice(BigDecimal.valueOf(50000));
        cryptoPair.setBidPrice(BigDecimal.valueOf(49000));

        tradeRequest = TradeRequestDTO.builder()
            .userId(1L)
            .pairName("BTC/USD")
            .transactionType("buy")
            .amount(BigDecimal.valueOf(0.01))
            .build();
    }

    @Test
    void testGetUserTransactionHistory_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TransactionHistoryResponse> response = transactionService.getUserTransactionHistory(1L);

        assertTrue(response.isEmpty());
    }

    @Test
    void testGetUserTransactionHistory_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Transaction transaction = Transaction.builder()
            .id(1L)
            .pair(cryptoPair)
            .transactionType("buy")
            .amount(BigDecimal.valueOf(0.01))
            .unitPrice(BigDecimal.valueOf(50000))
            .totalPrice(BigDecimal.valueOf(500))
            .timestamp(LocalDateTime.now())
            .build();
        when(transactionRepository.findByUserId(1L)).thenReturn(List.of(transaction));

        Optional<TransactionHistoryResponse> response = transactionService.getUserTransactionHistory(1L);

        assertTrue(response.isPresent());
        assertEquals(1, response.get().getTransactions().size());
    }

    @Test
    void testExecuteTrade_CryptoPairNotFound() {
        when(cryptoPairRepository.findByPairName("BTC/USD")).thenReturn(Optional.empty());

        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);

        assertEquals("failure", response.getStatus());
        assertEquals("Crypto pair not found.", response.getMessage());
    }

    @Test
    void testExecuteTrade_UserNotFound() {
        when(cryptoPairRepository.findByPairName("BTC/USD")).thenReturn(Optional.of(cryptoPair));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);

        assertEquals("failure", response.getStatus());
        assertEquals("User not found.", response.getMessage());
    }

    @Test
    void testExecuteTrade_InsufficientBalance() {
        when(cryptoPairRepository.findByPairName("BTC/USD")).thenReturn(Optional.of(cryptoPair));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        tradeRequest.setAmount(BigDecimal.valueOf(1)); // Set amount to a value that exceeds balance

        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);

        assertEquals("failure", response.getStatus());
        assertEquals("Invalid transaction type or insufficient balance.", response.getMessage());
    }

    @Test
    void testExecuteTrade_Success() {
        when(cryptoPairRepository.findByPairName("BTC/USD")).thenReturn(Optional.of(cryptoPair));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Transaction transaction = Transaction.builder()
            .id(1L)
            .pair(cryptoPair)
            .transactionType("buy")
            .amount(BigDecimal.valueOf(0.01))
            .unitPrice(BigDecimal.valueOf(50000))
            .totalPrice(BigDecimal.valueOf(500))
            .timestamp(LocalDateTime.now())
            .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);

        assertEquals("success", response.getStatus());
        assertEquals("Trade executed successfully.", response.getMessage());
        assertNotNull(response.getTransactionId());
    }

    @Test
    void testExecuteTrade_SellSuccess() {
        // Arrange
        when(cryptoPairRepository.findByPairName("BTC/USD")).thenReturn(Optional.of(cryptoPair));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Set up user's crypto balance to ensure they have enough to sell
        UserCryptoBalance userCryptoBalance = UserCryptoBalance.builder()
            .user(user)
            .currency("BTC/USD")
            .amount(BigDecimal.valueOf(0.01))
            .build();
        user.getCryptoBalances().add(userCryptoBalance);

        // Modify tradeRequest for a sell transaction
        tradeRequest.setTransactionType("sell");

        Transaction transaction = Transaction.builder()
            .id(2L)
            .pair(cryptoPair)
            .transactionType("sell")
            .amount(BigDecimal.valueOf(0.01))
            .unitPrice(BigDecimal.valueOf(49000)) // Assuming bid price
            .totalPrice(BigDecimal.valueOf(490))
            .timestamp(LocalDateTime.now())
            .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);

        // Assert
        assertEquals("success", response.getStatus());
        assertEquals("Trade executed successfully.", response.getMessage());
        assertNotNull(response.getTransactionId());
    }

    @Test
    void testExecuteTrade_InvalidTransactionType() {
        // Arrange
        when(cryptoPairRepository.findByPairName("BTC/USD")).thenReturn(Optional.of(cryptoPair));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Set an invalid transaction type
        tradeRequest.setTransactionType("invalid_type");

        // Act
        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);

        // Assert
        assertEquals("failure", response.getStatus());
        assertEquals("Invalid transaction type or insufficient balance.", response.getMessage());
    }

    @Test
    void testExecuteTrade_InsufficientCryptoBalance() {
        // Arrange
        when(cryptoPairRepository.findByPairName("BTC/USD")).thenReturn(Optional.of(cryptoPair));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Set up user's crypto balance to be less than the amount to sell
        UserCryptoBalance userCryptoBalance = UserCryptoBalance.builder()
            .user(user)
            .currency("BTC/USD")
            .amount(BigDecimal.valueOf(0.005)) // Less than the trade amount
            .build();
        user.getCryptoBalances().add(userCryptoBalance);

        // Modify tradeRequest for a sell transaction
        tradeRequest.setTransactionType("sell");

        // Act
        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);

        // Assert
        assertEquals("failure", response.getStatus());
        assertEquals("Invalid transaction type or insufficient balance.", response.getMessage());
    }

    @Test
    void testExecuteTrade_BuyOrderSuccess_UpdateExistingPosition() {
        // Arrange
        when(cryptoPairRepository.findByPairName("BTC/USD")).thenReturn(Optional.of(cryptoPair));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Set up user's crypto balance to ensure they have an existing position
        UserCryptoBalance userCryptoBalance = UserCryptoBalance.builder()
            .user(user)
            .currency("BTC/USD")
            .amount(BigDecimal.valueOf(0.01)) // Existing balance
            .build();
        user.getCryptoBalances().add(userCryptoBalance);

        // Mock the transactionRepository.save() to return a valid Transaction object
        Transaction transaction = Transaction.builder()
            .id(1L) // Ensure this ID is set
            .pair(cryptoPair)
            .transactionType("buy")
            .amount(BigDecimal.valueOf(0.01))
            .unitPrice(BigDecimal.valueOf(50000))
            .totalPrice(BigDecimal.valueOf(500))
            .timestamp(LocalDateTime.now())
            .build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TradeResponseDTO response = transactionService.executeTrade(tradeRequest);

        // Assert
        assertEquals("success", response.getStatus());
        assertEquals("Trade executed successfully.", response.getMessage());
        assertNotNull(response.getTransactionId());

        // Verify the balance is updated
        assertEquals(BigDecimal.valueOf(0.02), userCryptoBalance.getAmount());
    }
}