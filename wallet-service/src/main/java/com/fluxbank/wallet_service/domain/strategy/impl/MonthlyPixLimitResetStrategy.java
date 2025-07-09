package com.fluxbank.wallet_service.domain.strategy.impl;

import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;
import com.fluxbank.wallet_service.domain.strategy.WalletLimitResetStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class MonthlyPixLimitResetStrategy implements WalletLimitResetStrategy {

    @Override
    public LimitType getSupportedType() {
        return LimitType.MONTHLY_PIX;
    }

    @Override
    public BigDecimal calculateNewLimit(WalletLimit limit, List<WalletTransaction> transactions) {
        BigDecimal lastLimit = limit.getLimitAmount();

        BigDecimal total = transactions.stream()
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = total.divide(new BigDecimal(getUsageWindowDays()), RoundingMode.HALF_UP);

        if (average.compareTo(lastLimit.multiply(new BigDecimal("0.9"))) > 0) {
            return lastLimit.multiply(new BigDecimal("1.1")).min(new BigDecimal("10000"));
        }
        return lastLimit;
    }


    @Override
    public int getUsageWindowDays() {
        return 30;
    }
}
