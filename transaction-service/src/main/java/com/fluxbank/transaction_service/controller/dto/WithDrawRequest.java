package com.fluxbank.transaction_service.controller.dto;

import com.fluxbank.transaction_service.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record WithDrawRequest(
        String walletId,
        BigDecimal amount,
        UUID transactionId,
        TransactionType type,
        String metadata

) {
}
