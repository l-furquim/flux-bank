package com.fluxbank.wallet_service.domain.exception.wallet;

public class UnauthorizedWithDrawRequest extends RuntimeException {
    public UnauthorizedWithDrawRequest() {
        super("You dont have permissions to withdraw that wallet.");
    }
}
