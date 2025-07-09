package com.fluxbank.wallet_service.domain.exception.wallet;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient balance for this operation");
    }
}
