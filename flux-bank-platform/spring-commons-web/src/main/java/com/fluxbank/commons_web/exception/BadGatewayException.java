package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class BadGatewayException extends DomainException {

    public BadGatewayException() {
        this("bad-gateway", "Falha ao comunicar com um serviço upstream");
    }

    public BadGatewayException(String message) {
        this("bad-gateway", message);
    }

    public BadGatewayException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_GATEWAY);
    }

    public BadGatewayException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.BAD_GATEWAY, cause);
    }
}
