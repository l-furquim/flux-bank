package com.fluxbank.transaction_service.handler;

import com.fluxbank.transaction_service.handler.generic.ExceptionHandlerResponse;
import com.fluxbank.transaction_service.handler.generic.GenericExceptionHandler;
import com.fluxbank.transaction_service.model.exceptions.InvalidDepositException;
import com.fluxbank.transaction_service.model.exceptions.InvalidWithDrawException;
import com.fluxbank.transaction_service.model.exceptions.WalletClientUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WalletClientExceptionHandler extends GenericExceptionHandler {


    @ExceptionHandler(WalletClientUnavailableException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUnavailable(
            WalletClientUnavailableException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidWithDrawException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidWithDraw(
            InvalidWithDrawException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidDepositException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidDeposit(
            InvalidDepositException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

}
