package com.fluxbank.transaction_service.controller.dto;

import com.fluxbank.transaction_service.model.enums.Currency;
import com.fluxbank.transaction_service.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record WithDrawRequest(
        String userId,
        BigDecimal amount,
        UUID transactionId,
        TransactionType type,
        String metadata,
        Currency currency

) {
}
