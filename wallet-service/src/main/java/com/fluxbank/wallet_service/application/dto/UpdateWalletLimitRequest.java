package com.fluxbank.wallet_service.application.dto;

import com.fluxbank.wallet_service.domain.enums.TransactionType;
import com.fluxbank.wallet_service.domain.models.Wallet;

import java.math.BigDecimal;

public record UpdateWalletLimitRequest(
        Wallet wallet,
        BigDecimal amount,
        TransactionType type
) {
}
