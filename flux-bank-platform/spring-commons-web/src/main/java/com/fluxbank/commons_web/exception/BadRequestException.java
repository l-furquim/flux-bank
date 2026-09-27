package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends DomainException {

    public BadRequestException() {
        this("bad-request", "Requisição inválida");
    }

    public BadRequestException(String message) {
        this("bad-request", message);
    }

    public BadRequestException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }
}
