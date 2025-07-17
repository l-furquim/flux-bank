package com.fluxbank.transaction_service.model.exceptions;

public class InvalidDepositException extends RuntimeException {
    public InvalidDepositException(String message) {
        super(message);
    }
}
