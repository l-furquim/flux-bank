package com.fluxbank.commons_web.exception;

import org.springframework.http.HttpStatus;

public class UnsupportedMediaTypeException extends DomainException {

    public UnsupportedMediaTypeException() {
        this("unsupported-media-type", "Tipo de mídia não suportado");
    }

    public UnsupportedMediaTypeException(String message) {
        this("unsupported-media-type", message);
    }

    public UnsupportedMediaTypeException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
