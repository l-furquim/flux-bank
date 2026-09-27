package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends DomainException {

    public ServiceUnavailableException() {
        this("service-unavailable", "Serviço temporariamente indisponível");
    }

    public ServiceUnavailableException(String message) {
        this("service-unavailable", message);
    }

    public ServiceUnavailableException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceUnavailableException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.SERVICE_UNAVAILABLE, cause);
    }
}
