package com.fluxbank.user_service.domain.exceptions;

public class UnauthorizedAuthException extends RuntimeException {
    public UnauthorizedAuthException() {
        super("Cpf or password are wrong.");
    }
}
