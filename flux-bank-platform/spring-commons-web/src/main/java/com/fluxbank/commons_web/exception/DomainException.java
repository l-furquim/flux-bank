package com.fluxbank.commons_web.exception;

import com.fluxbank.commons_core.exception.FluxException;
import com.fluxbank.commons_web.domain.ErrorDetails;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DomainException extends FluxException {

    public static final String ERROR_BASE_URI = "https://fluxbank.com/errors/";

    private final HttpStatus status;

    protected DomainException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message);
        this.status = status;
    }

    protected DomainException(String errorCode, String message, HttpStatus status, Throwable cause) {
        super(errorCode, message, cause);
        this.status = status;
    }

    public String getErrorCode() {
        return code();
    }

    public ErrorDetails toErrorDetails(String correlationId) {
        return new ErrorDetails(
                ERROR_BASE_URI + code(),
                code(),
                getMessage(),
                status.value(),
                correlationId
        );
    }
}
