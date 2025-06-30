package com.fluxbank.wallet_service.domain.exception;

public class DuplicatedWalletCurrencyException extends RuntimeException {
    public DuplicatedWalletCurrencyException(String message) {
        super(message);
    }
}
