package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends DomainException {

    public UnauthorizedException() {
        this("unauthorized", "Não autenticado");
    }

    public UnauthorizedException(String message) {
        this("unauthorized", message);
    }

    public UnauthorizedException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.UNAUTHORIZED);
    }
}
