package com.fluxbank.wallet_service.domain.models;


import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class WalletLimit {

    private Wallet wallet;

    private LimitType limitType;

    private BigDecimal limitAmount;

    @Builder.Default
    private BigDecimal usedAmount = BigDecimal.ZERO;

    private LocalDateTime resetDate;

    @Builder.Default
    private LimitStatus status = LimitStatus.ACTIVE;

    private LocalDateTime createdAt;

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
