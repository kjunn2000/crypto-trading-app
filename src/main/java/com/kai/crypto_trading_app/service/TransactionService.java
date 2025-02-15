package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.dto.TransactionHistoryResponse;
import com.kai.crypto_trading_app.dto.TransactionResponse;
import com.kai.crypto_trading_app.dto.TradeRequestDTO;
import com.kai.crypto_trading_app.dto.TradeResponseDTO;
import com.kai.crypto_trading_app.model.Transaction;
import com.kai.crypto_trading_app.model.CryptoPair;
import com.kai.crypto_trading_app.model.User;
import com.kai.crypto_trading_app.model.UserCryptoBalance;
import com.kai.crypto_trading_app.repository.TransactionRepository;
import com.kai.crypto_trading_app.repository.CryptoPairRepository;
import com.kai.crypto_trading_app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CryptoPairRepository cryptoPairRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(PriceAggregationScheduler.class);

    @Transactional
    public Transaction executeTrade(Transaction transaction) {
        // Additional logic for executing trade can be added here
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public TransactionHistoryResponse getUserTransactionHistory(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        List<TransactionResponse> transactionResponses = transactions.stream()
            .map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getPair().getPairName(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getUnitPrice(),
                transaction.getTotalPrice(),
                transaction.getTimestamp()
            ))
            .collect(Collectors.toList());

        return new TransactionHistoryResponse(userId, transactionResponses);
    }

    @Transactional
    public TradeResponseDTO executeTrade(TradeRequestDTO tradeRequest) {
        Optional<CryptoPair> cryptoPairOpt = cryptoPairRepository.findByPairName(tradeRequest.getPairName());
        if (cryptoPairOpt.isEmpty()) {
            return buildFailureResponse("Crypto pair not found.");
        }

        Optional<User> userOpt = userRepository.findById(tradeRequest.getUserId());
        if (userOpt.isEmpty()) {
            return buildFailureResponse("User not found.");
        }

        CryptoPair cryptoPair = cryptoPairOpt.get();
        User user = userOpt.get();

        BigDecimal price = determinePriceAndUpdateBalances(tradeRequest, cryptoPair, user);
        if (price == null) {
            return buildFailureResponse("Invalid transaction type or insufficient balance.");
        }

        Transaction transaction = createTransaction(tradeRequest, cryptoPair, user, price);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        logger.info("Transaction saved: {}", savedTransaction);

        return buildSuccessResponse(savedTransaction.getId());
    }

    private TradeResponseDTO buildFailureResponse(String message) {
        return TradeResponseDTO.builder()
            .status("failure")
            .message(message)
            .build();
    }

    private TradeResponseDTO buildSuccessResponse(Long transactionId) {
        return TradeResponseDTO.builder()
            .transactionId(transactionId)
            .status("success")
            .message("Trade executed successfully.")
            .build();
    }

    private BigDecimal determinePriceAndUpdateBalances(TradeRequestDTO tradeRequest, CryptoPair cryptoPair, User user) {
        if ("buy".equalsIgnoreCase(tradeRequest.getTransactionType())) {
            return handleBuyTransaction(tradeRequest, cryptoPair, user);
        } else if ("sell".equalsIgnoreCase(tradeRequest.getTransactionType())) {
            return handleSellTransaction(tradeRequest, cryptoPair, user);
        }
        return null;
    }

    private BigDecimal handleBuyTransaction(TradeRequestDTO tradeRequest, CryptoPair cryptoPair, User user) {
        BigDecimal askPrice = cryptoPair.getAskPrice();
        BigDecimal totalCost = askPrice.multiply(tradeRequest.getAmount());

        if (user.getWalletBalance().compareTo(totalCost) < 0) {
            return null;
        }

        user.setWalletBalance(user.getWalletBalance().subtract(totalCost));
        updateUserCryptoBalance(user, tradeRequest.getPairName(), tradeRequest.getAmount());
        return askPrice;
    }

    private BigDecimal handleSellTransaction(TradeRequestDTO tradeRequest, CryptoPair cryptoPair, User user) {
        BigDecimal bidPrice = cryptoPair.getBidPrice();
        Optional<UserCryptoBalance> userCryptoBalanceOpt = findUserCryptoBalance(user, tradeRequest.getPairName());

        if (userCryptoBalanceOpt.isEmpty() || userCryptoBalanceOpt.get().getAmount().compareTo(tradeRequest.getAmount()) < 0) {
            return null;
        }

        UserCryptoBalance userCryptoBalance = userCryptoBalanceOpt.get();
        userCryptoBalance.setAmount(userCryptoBalance.getAmount().subtract(tradeRequest.getAmount()));

        BigDecimal totalRevenue = bidPrice.multiply(tradeRequest.getAmount());
        user.setWalletBalance(user.getWalletBalance().add(totalRevenue));

        return bidPrice;
    }

    private void updateUserCryptoBalance(User user, String currency, BigDecimal amount) {
        Optional<UserCryptoBalance> userCryptoBalanceOpt = findUserCryptoBalance(user, currency);

        if (userCryptoBalanceOpt.isPresent()) {
            UserCryptoBalance userCryptoBalance = userCryptoBalanceOpt.get();
            userCryptoBalance.setAmount(userCryptoBalance.getAmount().add(amount));
        } else {
            UserCryptoBalance newBalance = UserCryptoBalance.builder()
                .user(user)
                .currency(currency)
                .amount(amount)
                .build();
            user.getCryptoBalances().add(newBalance);
        }
    }

    private Optional<UserCryptoBalance> findUserCryptoBalance(User user, String currency) {
        return user.getCryptoBalances().stream()
            .filter(balance -> balance.getCurrency().equals(currency))
            .findFirst();
    }

    private Transaction createTransaction(TradeRequestDTO tradeRequest, CryptoPair cryptoPair, User user, BigDecimal unitPrice) {
        BigDecimal totalPrice = unitPrice.multiply(tradeRequest.getAmount());
        return Transaction.builder()
            .user(user)
            .pair(cryptoPair)
            .transactionType(tradeRequest.getTransactionType())
            .amount(tradeRequest.getAmount())
            .unitPrice(unitPrice)
            .totalPrice(totalPrice)
            .timestamp(LocalDateTime.now())
            .build();
    }
} 