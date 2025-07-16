package com.fluxbank.transaction_service.handler.generic;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public abstract class GenericExceptionHandler {

    protected ResponseEntity<ExceptionHandlerResponse> buildErrorResponse(HttpStatus status, String message, String path) {
        ExceptionHandlerResponse error = ExceptionHandlerResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }

}
