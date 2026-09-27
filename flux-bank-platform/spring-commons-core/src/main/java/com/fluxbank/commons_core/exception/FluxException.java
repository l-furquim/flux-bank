package com.fluxbank.commons_core.exception;

import java.util.Objects;

public abstract class FluxException extends RuntimeException {

    private final String code;

    protected FluxException(String code, String message) {
        super(message);
        this.code = Objects.requireNonNull(code, "code");
    }

    protected FluxException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = Objects.requireNonNull(code, "code");
    }

    public String code() {
        return code;
    }
}
