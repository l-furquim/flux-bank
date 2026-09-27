package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class MethodNotAllowedException extends DomainException {

    public MethodNotAllowedException() {
        this("method-not-allowed", "Método HTTP não permitido");
    }

    public MethodNotAllowedException(String message) {
        this("method-not-allowed", message);
    }

    public MethodNotAllowedException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
