package com.fluxbank.wallet_service.domain.models;

import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID userId;

    @NotNull
    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 3)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @NotNull
    private WalletStatus walletStatus;

    @Column(precision = 19, scale = 2)
    private BigDecimal blockedAmount;

    @CreationTimestamp
    @NotNull
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @NotNull
    private LocalDateTime updatedAt;

    public BigDecimal getAvailableBalance() {
        return balance.subtract(blockedAmount != null ? blockedAmount : BigDecimal.ZERO);
    }

    public boolean hasAvailableBalance(BigDecimal amount) {
        return getAvailableBalance().compareTo(amount) >= 0;
    }

}
