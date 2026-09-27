package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class PayloadTooLargeException extends DomainException {

    public PayloadTooLargeException() {
        this("payload-too-large", "Payload excede o tamanho permitido");
    }

    public PayloadTooLargeException(String message) {
        this("payload-too-large", message);
    }

    public PayloadTooLargeException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
