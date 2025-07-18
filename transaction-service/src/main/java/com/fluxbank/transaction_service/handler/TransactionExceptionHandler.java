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

    @ExceptionHandler(InvalidTransactionHistoryPageException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidTransactionHistoryPage(
            InvalidTransactionHistoryPageException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }



}
