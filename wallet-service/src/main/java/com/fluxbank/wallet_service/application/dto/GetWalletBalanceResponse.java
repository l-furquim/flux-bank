package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.Currency;
import com.fluxbank.wallet_service.domain.enums.WalletStatus;

import java.math.BigDecimal;

public record GetWalletBalanceResponse(
        BigDecimal balance,
        Currency currency,
        BigDecimal blockedAmount,
        WalletStatus status
) {
}
