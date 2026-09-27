package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends DomainException {

    public InternalServerException() {
        this("internal-error", "Erro interno inesperado");
    }

    public InternalServerException(String message) {
        this("internal-error", message);
    }

    public InternalServerException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
