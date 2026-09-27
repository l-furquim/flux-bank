package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class RequestTimeoutException extends DomainException {

    public RequestTimeoutException() {
        this("request-timeout", "Tempo da requisição esgotado");
    }

    public RequestTimeoutException(String message) {
        this("request-timeout", message);
    }

    public RequestTimeoutException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.REQUEST_TIMEOUT);
    }
}
