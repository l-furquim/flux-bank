package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class GoneException extends DomainException {

    public GoneException() {
        this("gone", "Recurso não está mais disponível");
    }

    public GoneException(String message) {
        this("gone", message);
    }

    public GoneException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.GONE);
    }
}
