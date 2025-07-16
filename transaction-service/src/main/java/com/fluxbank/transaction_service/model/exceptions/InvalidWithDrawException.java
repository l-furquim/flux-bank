package com.fluxbank.transaction_service.model.exceptions;

public class InvalidWithDrawException extends RuntimeException {
    public InvalidWithDrawException(String message) {
        super(message);
    }
}
