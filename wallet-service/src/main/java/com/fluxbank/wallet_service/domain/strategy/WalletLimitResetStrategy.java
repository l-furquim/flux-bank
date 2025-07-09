package com.fluxbank.wallet_service.domain.strategy;

import com.fluxbank.wallet_service.domain.enums.LimitType;
import com.fluxbank.wallet_service.domain.models.WalletLimit;
import com.fluxbank.wallet_service.domain.models.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface WalletLimitResetStrategy {

    LimitType getSupportedType();
    BigDecimal calculateNewLimit(WalletLimit limit, List<WalletTransaction> transactions);
    int getUsageWindowDays();


}
