package com.fluxbank.transaction_service.handler;

import com.fluxbank.transaction_service.handler.generic.ExceptionHandlerResponse;
import com.fluxbank.transaction_service.handler.generic.GenericExceptionHandler;
import com.fluxbank.transaction_service.model.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransactionExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(PixKeyNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handlePixKeyNotFoundException(
            PixKeyNotFoundException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResolvePixKeyException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleResolvePixKeyException(
            ResolvePixKeyException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidTransaction(
            InvalidTransactionException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(TransactionEventSerializerException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleSerializer(
            TransactionEventSerializerException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserClientUnavailableException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleSerializer(
            UserClientUnavailableException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request.getRequestURI());
    }


}
