package com.fluxbank.wallet_service.domain.exception.wallettransaction;

public class WalletTransactionNotFoundException extends RuntimeException {
    public WalletTransactionNotFoundException(String message) {
        super(message);
    }
}
