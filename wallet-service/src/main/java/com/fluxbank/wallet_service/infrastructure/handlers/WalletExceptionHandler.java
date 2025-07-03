package com.fluxbank.wallet_service.infrastructure.handlers;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.DuplicatedWalletCurrencyException;
import com.fluxbank.wallet_service.domain.exception.wallet.InvalidDepositException;
import com.fluxbank.wallet_service.infrastructure.handlers.dto.ExceptionHandlerResponse;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletTransactionPersistenceAdapter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WalletExceptionHandler extends GenericExceptionHandler {

    private final WalletTransactionPersistenceAdapter adapter;

    public WalletExceptionHandler(WalletTransactionPersistenceAdapter adapter) {
        this.adapter = adapter;
    }

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

    @ExceptionHandler(InvalidDepositException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidDeposit(
            InvalidDepositException ex, HttpServletRequest request
    ) {
        adapter.updateWalletTransactionStatus(ex.getTransactionId(), TransactionStatus.FAILED);

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }
}
