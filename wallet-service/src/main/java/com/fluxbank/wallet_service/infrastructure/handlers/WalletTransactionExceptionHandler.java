package com.fluxbank.wallet_service.infrastructure.handlers;

import com.fluxbank.wallet_service.domain.exception.wallettransaction.InvalidWalletRefundException;
import com.fluxbank.wallet_service.domain.exception.wallettransaction.WalletTransactionNotFoundException;
import com.fluxbank.wallet_service.infrastructure.handlers.dto.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WalletTransactionExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(InvalidWalletRefundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidRefund(
            InvalidWalletRefundException ex, HttpServletRequest request
    ) {
        return super.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(WalletTransactionNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleWalletNotFound(
            WalletTransactionNotFoundException ex, HttpServletRequest request
    ) {
        return super.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

}
