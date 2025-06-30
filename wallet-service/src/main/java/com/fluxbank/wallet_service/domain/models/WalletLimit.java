package com.fluxbank.wallet_service.domain.models;


import com.fluxbank.wallet_service.domain.enums.LimitStatus;
import com.fluxbank.wallet_service.domain.enums.LimitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class WalletLimit {

    private Wallet wallet;

    private LimitType limitType;

    private BigDecimal limitAmount;

    private BigDecimal usedAmount = BigDecimal.ZERO;

    private LocalDateTime resetDate;

    private LimitStatus status;

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
