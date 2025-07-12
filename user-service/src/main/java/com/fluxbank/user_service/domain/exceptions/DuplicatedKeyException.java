package com.fluxbank.user_service.domain.exceptions;

public class DuplicatedKeyException extends RuntimeException {
    public DuplicatedKeyException(String message) {
        super(message);
    }
}
