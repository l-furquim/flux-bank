package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class GatewayTimeoutException extends DomainException {

    public GatewayTimeoutException() {
        this("gateway-timeout", "Tempo esgotado ao comunicar com um serviço upstream");
    }

    public GatewayTimeoutException(String message) {
        this("gateway-timeout", message);
    }

    public GatewayTimeoutException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.GATEWAY_TIMEOUT);
    }

    public GatewayTimeoutException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.GATEWAY_TIMEOUT, cause);
    }
}
