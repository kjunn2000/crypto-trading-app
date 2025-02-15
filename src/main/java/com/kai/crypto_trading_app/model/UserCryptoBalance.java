package com.kai.crypto_trading_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Entity
@Table(name = "USER_CRYPTO_BALANCE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCryptoBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "amount", nullable = false, precision = 19, scale = 8)
    private BigDecimal amount;
} 