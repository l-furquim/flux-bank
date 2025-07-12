package com.fluxbank.wallet_service.domain.exception.wallet;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
