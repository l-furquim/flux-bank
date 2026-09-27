package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableEntityException extends DomainException {

    public UnprocessableEntityException() {
        this("unprocessable-entity", "A requisição não pôde ser processada");
    }

    public UnprocessableEntityException(String message) {
        this("unprocessable-entity", message);
    }

    public UnprocessableEntityException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
