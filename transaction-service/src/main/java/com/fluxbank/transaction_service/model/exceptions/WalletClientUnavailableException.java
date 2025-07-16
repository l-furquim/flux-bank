package com.fluxbank.transaction_service.model.exceptions;

public class WalletClientUnavailableException extends RuntimeException {
    public WalletClientUnavailableException(String message) {
        super(message);
    }
}
