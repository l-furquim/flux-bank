package com.fluxbank.transaction_service.controller.dto;

public record RefundWalletTransactionRequest(
        String walletTransactionId,
        String payeeId
) {
}
