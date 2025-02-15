package com.kai.crypto_trading_app.service;

import com.kai.crypto_trading_app.dto.UserCryptoBalanceDTO;
import com.kai.crypto_trading_app.dto.UserWalletResponseDTO;
import com.kai.crypto_trading_app.model.User;
import com.kai.crypto_trading_app.model.UserCryptoBalance;
import com.kai.crypto_trading_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserWalletResponseDTO> getUserWalletDetails(Long userId) {
        return userRepository.findById(userId).map(this::mapToUserWalletResponseDTO);
    }

    private UserWalletResponseDTO mapToUserWalletResponseDTO(User user) {
        List<UserCryptoBalanceDTO> cryptoBalances = user.getCryptoBalances().stream()
                .map(this::mapToUserCryptoBalanceDTO)
                .collect(Collectors.toList());

        return new UserWalletResponseDTO(
                user.getId(),
                user.getWalletBalance(),
                cryptoBalances
        );
    }

    private UserCryptoBalanceDTO mapToUserCryptoBalanceDTO(UserCryptoBalance balance) {
        return new UserCryptoBalanceDTO(
                balance.getCurrency(),
                balance.getAmount().doubleValue()
        );
    }
}