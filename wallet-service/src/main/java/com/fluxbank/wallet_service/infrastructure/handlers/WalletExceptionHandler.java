package com.fluxbank.wallet_service.infrastructure.handlers;

import com.fluxbank.wallet_service.domain.exception.DuplicatedWalletCurrencyException;
import com.fluxbank.wallet_service.infrastructure.handlers.dto.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WalletExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(DuplicatedWalletCurrencyException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleDuplicatedWallet(
            DuplicatedWalletCurrencyException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUnsupportedCurrency(
            UnsupportedOperationException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }
}
