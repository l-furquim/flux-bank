package com.fluxbank.wallet_service.domain.exception.walletlimit;

public class WalletLimitNotFoundException extends RuntimeException {
    public WalletLimitNotFoundException() {
        super("Could not found the limit for the operation.");
    }
}
