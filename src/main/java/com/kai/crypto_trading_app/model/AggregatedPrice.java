package com.kai.crypto_trading_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aggregated_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pair_id", nullable = false)
    private CryptoPair pair;

    @Column(name = "bid_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal askPrice;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
