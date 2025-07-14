package com.fluxbank.transaction_service.model.exceptions;

public class TransactionEventSerializerException extends RuntimeException {
    public TransactionEventSerializerException(String message) {
        super(message);
    }
}
