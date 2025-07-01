package com.fluxbank.wallet_service.domain.exception;

public class UnsupportedCurrencyException extends RuntimeException {
    public UnsupportedCurrencyException() {
        super("Unsupported currency.");
    }
}
