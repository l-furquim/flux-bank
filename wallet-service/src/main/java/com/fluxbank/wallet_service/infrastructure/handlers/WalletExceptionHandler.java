package com.fluxbank.wallet_service.infrastructure.handlers;

import com.fluxbank.wallet_service.domain.enums.TransactionStatus;
import com.fluxbank.wallet_service.domain.exception.wallet.*;
import com.fluxbank.wallet_service.domain.exception.walletlimit.LimitBlockedException;
import com.fluxbank.wallet_service.infrastructure.handlers.dto.ExceptionHandlerResponse;
import com.fluxbank.wallet_service.infrastructure.persistence.adapter.WalletTransactionPersistenceAdapter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
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

        log.error("Transação invalida {}", ex.getTransactionId());

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleWalletNotFound(
            WalletNotFoundException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedWithDrawRequest.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUnauthorizedWithdraw(
            UnauthorizedWithDrawRequest ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInsufficientBalance(
            InsufficientBalanceException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUnauthorizedOperation(
            UnauthorizedOperationException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }



}
