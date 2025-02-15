package com.kai.crypto_trading_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Entity
@Table(name = "crypto_pair")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CryptoPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pair_name", unique = true, nullable = false)
    private String pairName;

    @Column(name = "bid_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal askPrice;
}
