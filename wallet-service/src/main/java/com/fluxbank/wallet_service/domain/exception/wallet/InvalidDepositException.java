package com.fluxbank.wallet_service.domain.exception.wallet;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InvalidDepositException extends RuntimeException {
    private final UUID transactionId;

    public InvalidDepositException(UUID transactionId, String message) {
        super(message);
        this.transactionId = transactionId;
    }
}
