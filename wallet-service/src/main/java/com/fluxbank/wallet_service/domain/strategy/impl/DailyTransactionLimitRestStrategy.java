package com.fluxbank.wallet_service.domain.strategy.impl;

import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.domain.strategy.WalletLimitResetStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DailyTransactionLimitRestStrategy implements WalletLimitResetStrategy {

    @Override
    public LimitType getSupportedType() {
        return null;
    }

    @Override
    public BigDecimal calculateNewLimit(WalletLimit limit, List<WalletTransaction> transactions) {
        return null;
    }

    @Override
    public int getUsageWindowDays() {
        return 0;
    }

}
