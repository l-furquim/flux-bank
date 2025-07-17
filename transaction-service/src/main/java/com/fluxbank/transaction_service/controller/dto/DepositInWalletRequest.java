package com.fluxbank.transaction_service.controller.dto;

import com.fluxbank.transaction_service.model.enums.Currency;

import java.math.BigDecimal;

public record DepositInWalletRequest(
        String transactionId,
        BigDecimal amount,
        String userId,
        String type,
        String metadata,
        String description,
        Currency currency
) {
}
