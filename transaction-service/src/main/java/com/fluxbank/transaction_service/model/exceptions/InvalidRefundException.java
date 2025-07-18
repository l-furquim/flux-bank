package com.fluxbank.transaction_service.model.exceptions;

public class InvalidRefundException extends RuntimeException {
    public InvalidRefundException(String message) {
        super(message);
    }
}
