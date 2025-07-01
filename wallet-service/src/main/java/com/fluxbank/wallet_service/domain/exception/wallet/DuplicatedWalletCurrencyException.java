package com.fluxbank.wallet_service.domain.exception.wallet;

public class DuplicatedWalletCurrencyException extends RuntimeException {
    public DuplicatedWalletCurrencyException(String message) {
        super(message);
    }
}
