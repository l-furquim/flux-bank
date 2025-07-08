package com.fluxbank.wallet_service.domain.exception.walletlimit;

public class LimitBlockedException extends RuntimeException {
    public LimitBlockedException() {
        super("Your limit is blocked for now. Please check your wallet and see what you can do");
    }
}
