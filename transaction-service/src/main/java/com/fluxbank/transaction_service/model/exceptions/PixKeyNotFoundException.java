package com.fluxbank.transaction_service.model.exceptions;

public class PixKeyNotFoundException extends RuntimeException {
    public PixKeyNotFoundException(String message) {
        super(message);
    }
}
