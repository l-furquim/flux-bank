package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class PreconditionFailedException extends DomainException {

    public PreconditionFailedException() {
        this("precondition-failed", "Pré-condição da requisição não atendida");
    }

    public PreconditionFailedException(String message) {
        this("precondition-failed", message);
    }

    public PreconditionFailedException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.PRECONDITION_FAILED);
    }
}
