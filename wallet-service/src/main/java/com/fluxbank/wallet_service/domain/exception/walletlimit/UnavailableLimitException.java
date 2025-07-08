package com.fluxbank.wallet_service.domain.exception.walletlimit;

public class UnavailableLimitException extends RuntimeException {
    public UnavailableLimitException(String message) {
        super(message);
    }
}
