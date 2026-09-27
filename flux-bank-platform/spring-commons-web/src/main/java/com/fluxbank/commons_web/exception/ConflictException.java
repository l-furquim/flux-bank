package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends DomainException {

    public ConflictException() {
        this("conflict", "Conflito com o estado atual do recurso");
    }

    public ConflictException(String message) {
        this("conflict", message);
    }

    public ConflictException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.CONFLICT);
    }
}
