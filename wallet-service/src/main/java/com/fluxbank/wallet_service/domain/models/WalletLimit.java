package com.fluxbank.wallet_service.domain.models;

import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;
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
@Table(name = "wallet_limits")
public class WalletLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LimitType limitType;

    @NotNull
    @Column(precision = 19, scale = 2)
    private BigDecimal limitAmount;

    @NotNull
    @Column(precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal usedAmount = BigDecimal.ZERO;

    private LocalDateTime resetDate;

    private LimitStatus status;

    @CreationTimestamp
    @NotNull
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @NotNull
    private LocalDateTime updatedAt;

    public BigDecimal getAvailableLimit() {
        return limitAmount.subtract(usedAmount);
    }

    public boolean hasAvailableLimit(BigDecimal amount) {
        return getAvailableLimit().compareTo(amount) >= 0;
    }

    public boolean isLimitExceeded() {
        return usedAmount.compareTo(limitAmount) >= 0;
    }

}
