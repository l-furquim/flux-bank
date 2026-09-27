package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends DomainException {

    public ForbiddenException() {
        this("forbidden", "Acesso negado");
    }

    public ForbiddenException(String message) {
        this("forbidden", message);
    }

    public ForbiddenException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.FORBIDDEN);
    }
}
