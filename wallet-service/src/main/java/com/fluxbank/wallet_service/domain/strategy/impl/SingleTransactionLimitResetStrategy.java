package com.fluxbank.wallet_service.domain.strategy.impl;

import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.domain.strategy.WalletLimitResetStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SingleTransactionLimitResetStrategy implements WalletLimitResetStrategy {

    @Override
    public LimitType getSupportedType() {
        return LimitType.SINGLE_TRANSACTION;
    }

    @Override
    public BigDecimal calculateNewLimit(WalletLimit limit, List<WalletTransaction> transactions) {
        return limit.getLimitAmount(); // Não recalcula
    }

    @Override
    public int getUsageWindowDays() {
        return 0;
    }
}
