package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends DomainException {

    public NotFoundException() {
        this("not-found", "Recurso não encontrado");
    }

    public NotFoundException(String message) {
        this("not-found", message);
    }

    public NotFoundException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.NOT_FOUND);
    }
}
