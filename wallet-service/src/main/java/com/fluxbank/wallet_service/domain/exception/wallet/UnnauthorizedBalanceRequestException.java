package com.fluxbank.wallet_service.domain.exception.wallet;

public class UnnauthorizedBalanceRequestException extends RuntimeException {
    public UnnauthorizedBalanceRequestException() {
        super("This wallet does not belongs to you.");
    }
}
