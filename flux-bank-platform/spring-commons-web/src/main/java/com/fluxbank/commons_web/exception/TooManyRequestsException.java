package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends DomainException {

    public TooManyRequestsException() {
        this("too-many-requests", "Muitas requisições");
    }

    public TooManyRequestsException(String message) {
        this("too-many-requests", message);
    }

    public TooManyRequestsException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
