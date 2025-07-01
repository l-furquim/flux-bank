package com.fluxbank.wallet_service.domain.exception.wallet;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException() {
        super("Wallet not found.");
    }
}
