package com.fluxbank.wallet_service.domain.exception.wallet;

public class InvalidDepositException extends RuntimeException {
    public InvalidDepositException() {
        super("Invalid amount for a deposit");
    }
}
