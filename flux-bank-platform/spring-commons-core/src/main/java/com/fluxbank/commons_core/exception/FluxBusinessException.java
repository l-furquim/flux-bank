package com.fluxbank.commons_core.exception;

public class FluxBusinessException extends FluxException {

    public FluxBusinessException(String code, String message) {
        super(code, message);
    }

    public FluxBusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
