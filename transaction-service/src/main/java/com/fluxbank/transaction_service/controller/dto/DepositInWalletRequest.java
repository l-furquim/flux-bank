package com.fluxbank.transaction_service.controller.dto;

import java.math.BigDecimal;

public record DepositInWalletRequest(
        String transactionId,
        BigDecimal amount,
        String walletId,
        String type,
        String metadata,
        String description
) {
}
