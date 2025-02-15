package com.kai.crypto_trading_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "user_crypto_balance")
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
    @JsonBackReference
    private User user;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "amount", nullable = false, precision = 19, scale = 8)
    private BigDecimal amount;

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCryptoBalance that = (UserCryptoBalance) o;
        return id != null && id.equals(that.id);
    }
} 